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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteOrchestratorServiceLegacyTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClientMock;
    private RouteOrchestratorServiceLegacy routeOrchestratorService;

    @BeforeEach
    void setUp() {
        String fakeSystemTmpl = "System prompt with {total_minutes}";
        String fakeUserTmpl = "User prompt with {user_wish}, {duration_hours}, {points_json}";
        routeOrchestratorService = new RouteOrchestratorServiceLegacy(
            chatClientMock,
            new ObjectMapper(),
            fakeSystemTmpl,
            fakeUserTmpl
        );
    }

    @Test
    void whenLlmReturnsValidJson_thenServiceReturnsThem() {
        String mockJsonResponse = "{\"selected_ids\":[\"p1_test\",\"p2_test\"]}";
        when(chatClientMock.prompt()
            .system(anyString())
            .user(anyString())
            .call()
            .content())
            .thenReturn(mockJsonResponse);

        List<String> result = routeOrchestratorService.generateRoutePointIds("test", 2, List.of());
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
