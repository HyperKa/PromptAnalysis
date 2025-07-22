package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.Point;
import org.example.dto.PointSelectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;

import org.springframework.ai.chat.model.ChatModel;  // Новый импорт
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteOrchestratorServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilderMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClientMock;

    private RouteOrchestratorService routeOrchestratorService;

    @BeforeEach
    void setUp() {
        String fakeSystemTmpl = "System prompt with {total_minutes}";
        String fakeUserTmpl = "User prompt with {user_wish}, {duration_hours}, {points_json}";

        // --- Вызываем новый тестовый конструктор ---
        // Передаем настоящий мок chatClientMock, который мы создали выше
        routeOrchestratorService = new RouteOrchestratorService(chatClientMock, new ObjectMapper(), fakeSystemTmpl, fakeUserTmpl);
    }

    @Test
    void whenLlmReturnsValidJson_thenServiceReturnsThem() {
        // 1. Arrange
        String cloneJsonResponse = "{\"selected_ids\":[\"p1_test\",\"p2_test\"]}";

        when(chatClientMock.prompt()
                .system(anyString())
                .user(anyString()) // Используем any(Function.class), так как в сервисе лямбда
                .call()
                .content())
                .thenReturn(cloneJsonResponse);

        // 2. Act
        List<String> result = routeOrchestratorService.generateRoutePointIds("test", 2,
                List.of());

        // 3. Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        // assertEquals("p1_test", result.get(0));
    }

    @Test
    void whenLlmApiFails_thenServiceThrowsException() {
        // 1. Arrange
        when(chatClientMock.prompt()
                .system(anyString())
                .user(anyString())
                .call()
                .content())
                .thenThrow(new RuntimeException("API is down"));

        // 2. Act & 3. Assert
        assertThrows(RuntimeException.class, () -> {
            routeOrchestratorService.generateRoutePointIds("test", 2,
                    List.of());
        });
    }
}