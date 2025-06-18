package com.project.analyticsService.api.controllers;

import com.project.analyticsService.application.services.TaskAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    private final TaskAnalyticsService service;

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserStatistic(@PathVariable Long id) {
        Map<String, Object> usersStatistic = service.calculateUserStatistic(id);
        if (usersStatistic.isEmpty()) {
            return ResponseEntity.badRequest().body(String.format("Statistic of user %s empty", id));
        }
        return ResponseEntity.ok(usersStatistic);
    }
}
