package org.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.PointSelectionRequest;
import org.example.kafka.KafkaRouteConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class MistralAiClient {

    private static final Logger log = LoggerFactory.getLogger(MistralAiClient.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String apiUrl;

    public MistralAiClient(RestTemplate restTemplate, ObjectMapper objectMapper, @Value("${spring.ai.mistralai.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.apiUrl = "https://api.mistral.ai/v1/chat/completions";
    }

    public PointSelectionRequest callMistralAi(String systemPrompt, String userPrompt) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(apiKey);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", "open-mistral-nemo");
    requestBody.put("temperature", 0.2);
    requestBody.put("messages", new Object[] {
        Map.of("role", "system", "content", systemPrompt),
        Map.of("role", "user", "content", userPrompt)
    });

    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

    ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Map.class);

    Map<String, Object> responseBody = responseEntity.getBody();
    if (responseBody == null || !responseBody.containsKey("choices")) {
        throw new RuntimeException("Invalid response from Mistral AI API");
    }

    Map<String, Object> firstChoice = (Map<String, Object>) ((java.util.List<Map<String, Object>>) responseBody.get("choices")).get(0);
    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
    String content = (String) message.get("content");

    log.info("Mistral AI response: {}", content);

    try {
        return objectMapper.readValue(content, PointSelectionRequest.class);
    } catch (Exception e) {
        log.error("Failed to parse Mistral AI response: {}", content, e);
        return new PointSelectionRequest(new ArrayList<>());
    }
}
}

