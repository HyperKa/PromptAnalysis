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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.example.dto.RouteGenerationRequest;    // Для отправки запроса на LLM

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(RouteOrchestratorService.class);
    private static final int MAX_RETRIES = 2;

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    private String systemPromptTemplate;
    private String userPromptTemplate;

    // Подтягивание содержимого текстовых промптов с помощью аннотации value и классу Resource, преобразовывающему
    // байт-код из @Value в читаемый формат String
    @Autowired
    public RouteOrchestratorService(ChatClient.Builder builder, ObjectMapper objectMapper,
             @Value("classpath:system_prompt.txt") Resource systemPromptResource,
             @Value("classpath:user_prompt.txt") Resource userPromptResource) {
        this.chatClient = builder.build();
        this.objectMapper = objectMapper;
        this.systemPromptTemplate = asString(systemPromptResource);
        this.userPromptTemplate = asString(userPromptResource);
        // Закомментировал вывод ненужный (для отладки), не судить
        // System.out.println("System prompt template is:\n " + systemPromptTemplate);
    }

    // тестовый конструктор
    RouteOrchestratorService(ChatClient chatClient, ObjectMapper objectMapper, String systemTmpl, String userTmpl) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
        this.systemPromptTemplate = systemTmpl;
        this.userPromptTemplate = userTmpl;
    }

    // технические мозговые работы, не трогать (>_<)
    private List<String> quick_sorted_list(List<String> input_list) {
        Random rand = new Random();
        int pivot = Integer.parseInt(input_list.get(rand.nextInt(0, input_list.size() - 1)));
        int left = 0;
        int right = input_list.size() - 1;
        for (int i = left; i < input_list.size() - 1; i++) {
            if (Integer.parseInt(input_list.get(i)) < pivot) {
                int buffer = Integer.parseInt(input_list.get(i));
                input_list.set(i, String.valueOf(input_list.get(left)));
                input_list.set(left, String.valueOf(buffer));
            }
        }
        if (right - left == 1) {
            return input_list;
        }
        return null;
    }


    private List<String> validateAndTrimRoute(List<String> suggestedIds, List<Point> availablePoints, int limitMinutes) {
        // Словарь для быстрого доступа к точкам по их ID
        Map<String, Point> pointsMap = availablePoints.stream()
                .collect(Collectors.toMap(Point::id, point -> point));

        // Решил добавить наиболее значимые по времязатратам точки в маршрут по высшему приоритету
        List<String> sortedList = new ArrayList<>(suggestedIds);
        sortedList.sort(Comparator.comparing((String id) -> pointsMap.get(id).avg_visit_duration_min()).reversed());

        // РЕЗУЛЬТАТ РАБОТЫ ВСЕГО МИКРОСЕРВИСА для дальнейшей реализации
        List<String> validRoute = new ArrayList<>();
        int totalDuration = 0;

        log.info("--- ЗАПУСК КОРРЕКТИРОВКИ МАРШРУТА ---");
        log.info("Лимит времени: {} минут.", limitMinutes);
        log.info("Предложение от LLM: {}", suggestedIds);

        for (String id : sortedList) {
            Point point = pointsMap.get(id);
            if (point != null) {
                if (totalDuration + point.avg_visit_duration_min() <= limitMinutes) {
                    validRoute.add(id);
                    totalDuration += point.avg_visit_duration_min();
                    log.info("Добавлена точка '{}' ({} мин). Текущее время: {} мин.", point.name(), point.avg_visit_duration_min(), totalDuration);
                } else {
                    log.info("Не добавлена точка '{}' ({} мин), превышен лимит ({} + {} > {}).", point.name(), point.avg_visit_duration_min(), totalDuration, point.avg_visit_duration_min(), limitMinutes);
                    // можно раскомментить, но думаю можно и помедленнее ради точного отбора
                    // break;
                }
            }
        }

        log.info("--- ВАЛИДАЦИЯ ЗАВЕРШЕНА ---");
        log.info("Итоговый маршрут: {}. Суммарное время: {} мин.", validRoute, totalDuration);
        return validRoute;
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

        // log.info("--- ОТПРАВКА В LLM (ручной рендеринг) ---");
        // log.info("SYSTEM PROMPT: {}", systemPromptText);
        // log.info("USER PROMPT: {}", userPromptText);

        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                log.info("Попытка #{} вызова LLM...", i + 1);

                String rawResponse = chatClient.prompt() // Получаем сырой ответ, а не jsonResponse
                        .system(systemPromptText)
                        .user(userPromptText)
                        .call()
                        .content();

                if (rawResponse != null && !rawResponse.isBlank()) {

                    // --- НАЧАЛО ИЗМЕНЕНИЙ ---

                    log.info("Сырой ответ от LLM: {}", rawResponse); // Логируем, чтобы видеть, что пришло

                    // начало JSON-объекта
                    int jsonStart = rawResponse.indexOf('{');
                    // конец JSON-объекта (последнюю '}')
                    int jsonEnd = rawResponse.lastIndexOf('}');

                    if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                        // вырез текста между { и }
                        String jsonOnly = rawResponse.substring(jsonStart, jsonEnd + 1);
                        log.info("Извлеченный JSON: {}", jsonOnly);

                        // псевдо JSON парсится в класс ... для сохранения выбранных точек
                        PointSelectionRequest selection = objectMapper.readValue(jsonOnly, PointSelectionRequest.class);

                        if (selection != null && selection.selected_ids() != null && !selection.selected_ids().isEmpty()) {
                            return validateAndTrimRoute(selection.selected_ids(), availablePoints, durationHours * 60);
                        }
                    }
                    log.warn("Попытка #{} не привела к выбору точек. Ответ был пустым или некорректным.", i + 1);
                }
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