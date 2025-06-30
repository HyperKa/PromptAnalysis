package org.example.dto;

import org.springframework.context.annotation.Description;
import java.util.List;

@Description("Запрос на выбор точек для построения маршрута")
public record PointSelectionRequest(
    @Description("Массив строковых ID, выбранных из предоставленного списка точек. Например, ['p1', 'p3', 'p5']")
    List<String> selected_ids
) {}
