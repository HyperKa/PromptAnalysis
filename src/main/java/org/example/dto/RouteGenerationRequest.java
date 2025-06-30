package org.example.dto;

import java.util.List;

public record RouteGenerationRequest(
    String prompt,
    int duration_hours,
    List<Point> points
) {}
