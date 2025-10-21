package org.example.listener;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.RouteGenerationRequest;
import org.example.service.RouteOrchestratorService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RouteKafkaListener {
    private static final Logger log = LoggerFactory.getLogger(RouteKafkaListener.class);

    // испорт сервиса
    private final RouteOrchestratorService routeService;
    public RouteKafkaListener(RouteOrchestratorService routeService) {
        this.routeService = routeService;
        log.info(">>>> RouteKafkaListener создан в конструкторе <<<<");
    }

    // Метод-слушатель для получения сообщения
    @KafkaListener(topics = "route_generation_requests", groupId = "route-generator-group")
    public void listen(@Payload RouteGenerationRequest request) {
        // log.info("Получен запрос из kafka на генерацию маршрута для: {}", request.prompt());
        // передача в сервис
        try {
            routeService.generateRoutePointIds(
                    request.prompt(),
                    request.duration_hours(),
                    request.points()
            );
        } catch (Exception e) {
            log.error("Ошибка при обработке запроса из kafka: {}",e.getMessage(), e);
        }
    }
}
