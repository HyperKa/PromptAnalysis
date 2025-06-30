package org.example.controller;

import org.example.dto.RouteGenerationRequest; // <-- ИМПОРТИРУЕМ ПРАВИЛЬНЫЙ DTO
import org.example.service.RouteOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteOrchestratorService service;

    public RouteController(RouteOrchestratorService service) {
        this.service = service;
    }

    @PostMapping("/generate-ids")
    public ResponseEntity<List<String>> generateRoute(@RequestBody RouteGenerationRequest request) { // <-- ИСПОЛЬЗУЕМ RouteGenerationRequest
        try {
            List<String> selectedIds = service.generateRoutePointIds(
                    request.prompt(),
                    request.duration_hours(),
                    request.points());
            return ResponseEntity.ok(selectedIds);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(List.of(e.getMessage())); // Возвращаем ошибку в теле ответа
        }
    }
}