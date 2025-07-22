package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.Point;
import org.example.dto.PointSelectionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class RouteOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(RouteOrchestratorService.class);
    private static final int MAX_RETRIES = 2;

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    private String systemPromptTemplate;
    private String userPromptTemplate;

    @Autowired
    public RouteOrchestratorService(ChatClient.Builder builder, ObjectMapper objectMapper,
             @Value("classpath:system_prompt.txt") Resource systemPromptResource,
             @Value("classpath:user_prompt.txt") Resource userPromptResource) {
        this.chatClient = builder.build();
        this.objectMapper = objectMapper;
        this.systemPromptTemplate = asString(systemPromptResource);
        this.userPromptTemplate = asString(userPromptResource);
    }

    // тестовый конструктор
    RouteOrchestratorService(ChatClient chatClient, ObjectMapper objectMapper, String systemTmpl, String userTmpl) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
        this.systemPromptTemplate = systemTmpl;
        this.userPromptTemplate = userTmpl;
    }


    public List<String> generateRoutePointIds(String userPrompt, int durationHours, List<Point> availablePoints) {
        String pointsAsJson = serializePoints(availablePoints);

        // замена плейсхолдеров
        String systemPromptText = systemPromptTemplate
                .replace("{total_minutes}", String.valueOf(durationHours * 60));

        String userPromptText = userPromptTemplate
                .replace("{user_wish}", userPrompt)
                .replace("{duration_hours}", String.valueOf(durationHours))
                .replace("{points_json}", pointsAsJson);

        log.info("--- ОТПРАВКА В LLM (ручной рендеринг) ---");
        log.info("SYSTEM PROMPT: {}", systemPromptText);
        log.info("USER PROMPT: {}", userPromptText);

        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                log.info("Попытка #{} вызова LLM...", i + 1);

                String jsonResponse = chatClient.prompt()
                    .system(systemPromptText)
                    .user(userPromptText)
                    .call()
                    .content();

                if (jsonResponse != null && !jsonResponse.isBlank()) {
                    PointSelectionRequest selection = objectMapper.readValue(jsonResponse, PointSelectionRequest.class);
                    if (selection != null && selection.selected_ids() != null && !selection.selected_ids().isEmpty()) {
                        log.info("УСПЕХ! LLM сгенерировала JSON с точками: {}", selection.selected_ids());
                        return selection.selected_ids();
                    }
                }
                log.warn("Попытка #{} не привела к выбору точек. Ответ был пустым или некорректным.", i + 1);

            } catch (Exception e) {
                log.error("Ошибка при вызове или парсинге ответа LLM на попытке #{}: {}", i + 1, e.getMessage(), e);
            }
        }

        log.error("Не удалось получить выбор точек после {} попыток.", MAX_RETRIES);
        throw new RuntimeException("Не удалось сгенерировать маршрут. Сервис LLM не ответил корректно.");
    }

    private String serializePoints(List<Point> points) {
        try {
            return objectMapper.writeValueAsString(points);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка подготовки данных для LLM", e);
        }
    }

    private static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}