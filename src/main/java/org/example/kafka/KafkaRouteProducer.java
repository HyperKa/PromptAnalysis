package org.example.kafka;

import org.example.dto.Point;
import org.example.dto.RouteRequestMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaRouteProducer {
    private final KafkaTemplate<String, RouteRequestMessage> kafkaTemplate;

    public KafkaRouteProducer(KafkaTemplate<String, RouteRequestMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRouteRequestToKafka(String routeId, String userPrompt, int durationHours, List<Point> points) {
        points.forEach(point -> {
            RouteRequestMessage message = new RouteRequestMessage(routeId, userPrompt, durationHours, point);
            kafkaTemplate.send("route-request-topic", message);
        });
    }
}
