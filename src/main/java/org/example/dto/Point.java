package org.example.dto;

public record Point(
    String id,
    String name,
    String category,
    int avg_visit_duration_min
) {}
