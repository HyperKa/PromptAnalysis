package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.Point;
import org.example.dto.PointSelectionRequest;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

public class RouteOrchestratorServiceLegacy {
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final String systemPromptTemplate;
    private final String userPromptTemplate;

    public RouteOrchestratorServiceLegacy(
        ChatClient chatClient,
        ObjectMapper objectMapper,
        String systemPromptTemplate,
        String userPromptTemplate
    ) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
        this.systemPromptTemplate = systemPromptTemplate;
        this.userPromptTemplate = userPromptTemplate;
    }

    public List<String> generateRoutePointIds(String userPrompt, int durationHours, List<Point> availablePoints) {
        String pointsAsJson = serializePoints(availablePoints);
        String systemPromptText = systemPromptTemplate.replace("{total_minutes}", String.valueOf(durationHours * 60));
        String userPromptText = userPromptTemplate
            .replace("{user_wish}", userPrompt)
            .replace("{duration_hours}", String.valueOf(durationHours))
            .replace("{points_json}", pointsAsJson);

        String jsonResponse = chatClient.prompt()
            .system(systemPromptText)
            .user(userPromptText)
            .call()
            .content();

        try {
            PointSelectionRequest selection = objectMapper.readValue(jsonResponse, PointSelectionRequest.class);
            return selection.selected_ids();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга ответа LLM", e);
        }
    }

    private String serializePoints(List<Point> points) {
        try {
            return objectMapper.writeValueAsString(points);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации точек", e);
        }
    }
}
