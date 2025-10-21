package org.example.controller;

import org.example.dto.RouteGenerationRequest;
import org.example.listener.RouteKafkaListener;
import org.example.service.RouteOrchestratorService;  // Изменение получателя запросов на генерацию
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.core.KafkaTemplate;  // Теперь на кафку
import java.util.List;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    // private final RouteOrchestratorService service;
    private static final String KAFKA_TOPIC = "route_generation_requests";
    private final KafkaTemplate<String, RouteGenerationRequest> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(RouteController.class);

    /*
    public RouteController(RouteOrchestratorService service) {
        this.service = service;
    }
    */
    public RouteController(KafkaTemplate<String, RouteGenerationRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Подтягивание трех массивов из JSON запроса и преобразование их в объекты request перечисления
    @PostMapping("/generate-ids")
    public ResponseEntity<String> generateRoute(@RequestBody RouteGenerationRequest request) {   // Убран массив строк в возвращ. знач.
        try {
            /*
            List<String> selectedIds = service.generateRoutePointIds(
                    request.prompt(),
                    request.duration_hours(),
                    request.points());
            return ResponseEntity.ok(selectedIds);
            */
            kafkaTemplate.send(KAFKA_TOPIC, request);
            log.info(">>>> Сообщение из контроллера успешно отправлено в топик '{}' <<<<", KAFKA_TOPIC);
            return ResponseEntity.accepted().body("Запрос принят и отправлен в обработку");  // Теперь возвращ. не готовый список, а строка о успешном принятии. До этого возвращаемым значением была функция generateRoutePointIds в сервисе
        } catch (Exception e) {                                                              // RuntimeException заменена на Exception
            // return ResponseEntity.status(500).body(List.of(e.getMessage()));
            return ResponseEntity.status(500).body("Не удалось отправить send запрос в очередь Kafka");
        }
    }
}