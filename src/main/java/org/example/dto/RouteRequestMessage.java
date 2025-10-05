package org.example.dto;

public record RouteRequestMessage(
    String routeId,
    String userPrompt,
    int durationHours,
    Point point
) {}
