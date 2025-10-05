package org.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

// Добавлена @Valid для проверки корректности промпта на null значения
public record RouteGenerationRequest(
        @NotEmpty(message = "Промпт не может быть пустым")
        String prompt,

        @Min(value = 1, message = "Минимальное время на прогулку - не менее 1 часа. " +
                "Установите поле duration_hours > 0.")
        int duration_hours,

        @NotEmpty(message = "Массив точек не может быть пустым")
        List<Point> points
) { }
