package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.Point;
import org.example.dto.PointSelectionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RouteOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(RouteOrchestratorService.class);
    private static final int MAX_RETRIES = 2;

    private final ChatClient chatClient;  // Объект класса для работы с запросами от Spring AI
    private final ObjectMapper objectMapper;  // Сериализация в JSON и десериализация из JSON

    @Autowired
    public RouteOrchestratorService(ChatClient.Builder builder, ObjectMapper objectMapper) {
        this.chatClient = builder.build(); // Настройка
        this.objectMapper = objectMapper;
    }

    RouteOrchestratorService(ChatClient chatClient, ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
    }

    public List<String> generateRoutePointIds(String userPrompt, int durationHours, List<Point> availablePoints) {
        String pointsAsJson = serializePoints(availablePoints);

        String systemPromptText = """
        Ты — API-сервис, который преобразует пользовательский запрос в JSON.
        Твоя единственная задача — проанализировать предоставленные данные и вернуть JSON-объект.
        
        ЗАПРЕЩЕНО:
        - Начинать ответ с чего-либо, кроме открывающей фигурной скобки `{`.
        - Добавлять любые слова, объяснения, комментарии или Markdown-форматирование (например, ```json) до или после JSON-объекта.
        - Извиняться или делать предположения.
        
        Твой ответ должен быть валидным JSON и ТОЛЬКО им.
        
        Формат ответа:
        { "selected_ids": ["id_точки_1", "id_2", ...] }
        
        Проанализируй пожелания пользователя и предоставленные точки, учитывая желаемую длительность.
        Суммарное время посещения (avg_visit_duration_min) должно быть близко к {total_minutes} минутам.
        """;

        // пользовательский промпт
        String userPromptTemplate = """
            Пожелания пользователя: {user_wish}
            Желаемая длительность прогулки: примерно {duration_hours} часов.
            
            Список доступных точек:
            {points_json}
        """;

        // не нужен
        PromptTemplate promptTemplate = new PromptTemplate(userPromptTemplate);

        // заполнение плейсхолдеров
        Map<String, Object> promptParameters = Map.of(
            "user_wish", userPrompt,
            "duration_hours", durationHours,
            "total_minutes", durationHours * 60,
            "points_json", pointsAsJson
        );

        log.info("--- ОТПРАВКА В LLM (PromptTemplate Mode) ---");

        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                log.info("Попытка #{} вызова LLM...", i + 1);

                String jsonResponse = chatClient.prompt()
                    .system(systemPromptText)
                    .user(p -> p.text(userPromptTemplate).params(promptParameters))
                    .call()
                    .content(); // на выходе - строка, поступающая в LLM

                if (jsonResponse != null && !jsonResponse.isBlank()) {
                    // Парсинг строки в DTO
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
            log.error("Критическая ошибка: не удалось сериализовать точки в JSON", e);
            throw new RuntimeException("Ошибка подготовки данных для LLM", e);
        }
    }
}