package com.project.taskService.api.controllers;

import com.project.taskService.api.DTOs.TaskDTO;
import com.project.taskService.api.DTOs.TaskUpdateDTO;
import com.project.taskService.application.interfaces.TaskMapper;
import com.project.taskService.application.services.TaskService;
import com.project.taskService.models.TaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping("/all_tasks")
    public ResponseEntity<?> findAllTasks() {
        List<TaskDTO> tasks = taskService.findAll()
                .stream()
                .map(taskMapper::entityToDto)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> findTasksByProject(@PathVariable Long projectId) {
        List<TaskEntity> tasks = taskService.findTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/save_task")
    public ResponseEntity<?> saveTask(@RequestBody TaskDTO task) {
        taskService.saveTask(taskMapper.dtoToEntity(task));
        return ResponseEntity.ok("Task saved");
    }

    @GetMapping("/task_id/{id}")
    public ResponseEntity<?> findTaskById(@PathVariable Long id) {
        TaskEntity taskById = taskService.findTaskById(id);
        return ResponseEntity.ok(taskById);
    }

    @PutMapping("/update_task/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskUpdateDTO taskUpdateDTO) {
        try {
            taskService.updateTask(id, taskUpdateDTO);
            TaskEntity updatedTask = taskService.findTaskById(id);
            return ResponseEntity.ok(String.format("Task %s updated:\n%s", id, updatedTask));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
