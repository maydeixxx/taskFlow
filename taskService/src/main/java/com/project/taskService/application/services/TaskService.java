package com.project.taskService.application.services;

import com.project.taskService.api.DTOs.TaskUpdateDTO;
import com.project.taskService.application.interfaces.TaskRepository;
import com.project.taskService.models.TaskEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskProducer producer;

    public List<TaskEntity> findAll() {
        return taskRepository.findAll();
    }

    public void saveTask(TaskEntity task) {
        taskRepository.save(task);
        Long taskId = task.getId();
        Long projectId = task.getProjectId();
        Set<Long> members = task.getMembers();
        producer.saveTaskToProject(projectId, taskId, members);
    }

    public void deleteTask(Long id) {
        TaskEntity task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("task %s not found", id)));
        taskRepository.delete(task);
    }

    public void updateTask(Long id, TaskUpdateDTO taskUpdate) {
        TaskEntity task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("task %s not found", id)));
        switch (taskUpdate.getField()) {
            case "title" -> task.setTitle(taskUpdate.getTitle());
            case "body" -> task.setBody(taskUpdate.getBody());
            case "status" -> task.setStatus(taskUpdate.getStatus());
            case "deadline" -> task.setDeadline(taskUpdate.getDeadline());
            case "members" ->  {
                task.setMembers(taskUpdate.getMembers());
            }
        }
        taskRepository.save(task);
    }

    public List<TaskEntity> findTasksByProject(Long projectId) {
        return taskRepository.findAll()
                .stream()
                .filter(task -> task.getProjectId().equals(projectId))
                .toList();
    }

    public TaskEntity findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Task %s not found", id)));
    }
}
