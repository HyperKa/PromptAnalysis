package org.example.service;

import org.example.dto.Point;
import org.example.kafka.KafkaRouteConsumer;
import org.example.kafka.KafkaRouteProducer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class RouteOrchestratorService {
    private final KafkaRouteProducer kafkaProducer;
    private final KafkaRouteConsumer kafkaConsumer;

    public RouteOrchestratorService(KafkaRouteProducer kafkaProducer, KafkaRouteConsumer kafkaConsumer) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
    }

    public List<String> generateRoutePointIds(String userPrompt, int durationHours, List<Point> availablePoints) {
        String routeId = UUID.randomUUID().toString();
        CountDownLatch latch = new CountDownLatch(availablePoints.size());
        kafkaConsumer.registerLatch(routeId, latch);

        kafkaProducer.sendRouteRequestToKafka(routeId, userPrompt, durationHours, availablePoints);

        try {
            boolean completed = latch.await(30, TimeUnit.SECONDS);
            if (!completed) {
                throw new RuntimeException("Таймаут ожидания обработки точек");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Ожидание обработки прервано", e);
        }

        return kafkaConsumer.getResults(routeId);
    }
}
