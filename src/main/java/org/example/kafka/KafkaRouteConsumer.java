package org.example.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.client.MistralAiClient;
import org.example.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

@Service
public class KafkaRouteConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaRouteConsumer.class);
    private final MistralAiClient mistralAiClient;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, RouteResponseMessage> responseKafkaTemplate;
    private final ConcurrentMap<String, List<String>> routeResults = new ConcurrentHashMap<>();
    private final Map<String, CountDownLatch> routeLatches = new ConcurrentHashMap<>();


    private String systemPromptTemplate;
    private String userPromptTemplate;

    public KafkaRouteConsumer(
            MistralAiClient mistralAiClient,
            ObjectMapper objectMapper,
            KafkaTemplate<String, RouteResponseMessage> responseKafkaTemplate,
            @Value("classpath:system_prompt.txt") Resource systemPromptResource,
            @Value("classpath:user_prompt.txt") Resource userPromptResource) {
        this.mistralAiClient = mistralAiClient;
        this.objectMapper = objectMapper;
        this.responseKafkaTemplate = responseKafkaTemplate;
        this.systemPromptTemplate = asString(systemPromptResource);
        this.userPromptTemplate = asString(userPromptResource);
    }

    @KafkaListener(topics = "route-request-topic", groupId = "route-group")
    public void consume(RouteRequestMessage message) {
        log.info("Обработка точки {}", message.point().id());
        try {
            List<String> selectedIds = callMistralAiForPoint(message);
            if (selectedIds != null) {
                routeResults.computeIfAbsent(message.routeId(), k -> new ArrayList<>()).addAll(selectedIds);
                responseKafkaTemplate.send(
                    "route-response-topic",
                    new RouteResponseMessage(message.routeId(), selectedIds)
                );
            } else {
                log.warn("Mistral AI вернул null для точки {}", message.point().id());
            }
            CountDownLatch latch = routeLatches.get(message.routeId());
            if (latch != null) {
                latch.countDown();
            }
        } catch (Exception e) {
            log.error("Ошибка обработки точки {}", message.point().id(), e);
            CountDownLatch latch = routeLatches.get(message.routeId());
            if (latch != null) {
                latch.countDown();
            }
        }
    }

    private List<String> callMistralAiForPoint(RouteRequestMessage message) {
        String pointsAsJson;
        try {
            pointsAsJson = objectMapper.writeValueAsString(List.of(message.point()));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации точек", e);
        }

        String systemPromptText = systemPromptTemplate
                .replace("{total_minutes}", String.valueOf(message.durationHours() * 60));

        String userPromptText = userPromptTemplate
                .replace("{user_wish}", message.userPrompt())
                .replace("{duration_hours}", String.valueOf(message.durationHours()))
                .replace("{points_json}", pointsAsJson);

        PointSelectionRequest selection = mistralAiClient.callMistralAi(systemPromptText, userPromptText);
        return selection != null && selection.selected_ids() != null ? selection.selected_ids() : new ArrayList<>();
    }

    public List<String> getResults(String routeId) {
        return routeResults.getOrDefault(routeId, List.of());
    }

    public void registerLatch(String routeId, CountDownLatch latch) {
        routeLatches.put(routeId, latch);
    }

    private static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
