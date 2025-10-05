package org.example.dto;

import java.util.List;

public record RouteResponseMessage(
    String routeId,
    List<String> selectedPointIds
) {}
