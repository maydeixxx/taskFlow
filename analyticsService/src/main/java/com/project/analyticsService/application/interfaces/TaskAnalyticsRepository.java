package com.project.analyticsService.application.interfaces;

import com.project.analyticsService.models.TaskAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAnalyticsRepository extends MongoRepository<TaskAnalytics, String> {
    List<TaskAnalytics> findByUserId(Long userId);
    List<TaskAnalytics> findByProjectId(Long projectId);

}
