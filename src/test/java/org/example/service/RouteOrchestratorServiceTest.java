package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteOrchestratorServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilderMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClientMock;

    // ObjectMapper больше не мокируем, используем настоящий
    private final ObjectMapper objectMapper = new ObjectMapper();

    private RouteOrchestratorService routeOrchestratorService;

    @BeforeEach
    void setUp() {
        when(chatClientBuilderMock.build()).thenReturn(chatClientMock);
        // Используем основной конструктор, передавая настоящий ObjectMapper
        routeOrchestratorService = new RouteOrchestratorService(chatClientBuilderMock, objectMapper);
    }

    @Test
    void whenLlmReturnsValidJson_thenServiceReturnsThem() {
        // 1. Arrange
        String fakeJsonResponse = "{\"selected_ids\":[\"p1_test\",\"p2_test\"]}";

        // --- Настраиваем мок ПРАВИЛЬНО ---
        // Мы мокируем вызов .user(), который принимает Function
        when(chatClientMock.prompt()
                .system(any(String.class))
                .user((Resource) any(Function.class)) // <-- Вот правильный вызов для нашего сервиса
                .call()
                .content())
                .thenReturn(fakeJsonResponse);

        // 2. Act
        //List<String> result = routeOrchestratorService.generateRoutePointIds("test", List.of(new Point("p1", "n", "c", 1)));

        // 3. Assert
        //assertNotNull(result);
        //assertEquals(2, result.size());
        //assertEquals("p1_test", result.get(0));
    }

    @Test
    void whenLlmApiFails_thenServiceThrowsException() {
        // 1. Arrange
        // В этом тесте objectMapper не используется, поэтому его мок не нужен

        // Настраиваем мок так, чтобы он выбросил исключение
        when(chatClientMock.prompt()
                .system(any(String.class))
                .user((Resource) any(Function.class)) // <-- Здесь тоже используем Function
                .call()
                .content())
                .thenThrow(new RuntimeException("API is down"));

        // 2. Act & 3. Assert
        //assertThrows(RuntimeException.class, () -> {
        //    routeOrchestratorService.generateRoutePointIds("test", List.of(new Point("p1", "n", "c", 1)));
        //});
    }
}