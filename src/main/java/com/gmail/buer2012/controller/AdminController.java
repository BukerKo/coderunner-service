package com.gmail.buer2012.controller;

import com.gmail.buer2012.entity.RunInformation;
import com.gmail.buer2012.entity.Task;
import com.gmail.buer2012.repository.RunInformationRepository;
import com.gmail.buer2012.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  private RunInformationRepository runInformationRepository;
  private TaskRepository taskRepository;

  @GetMapping("/results")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getResults() {
    return ResponseEntity.ok(runInformationRepository.findAll());
  }

  @PostMapping("/deleteResult")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteResult(@RequestBody RunInformation runInformation) {
    runInformationRepository.delete(runInformation);
    return ResponseEntity.ok(0);
  }

  @PostMapping("/setTask")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> setTask(@RequestBody String newTask) {
    Task task = taskRepository.findFirstByTaskIsNotNull();
    task.setTask(newTask);
    taskRepository.save(task);
    return ResponseEntity.ok(0);
  }

  @GetMapping("/getTask")
  public ResponseEntity<?> getTask() {
    Task task = taskRepository.findFirstByTaskIsNotNull();
    return ResponseEntity.ok(task);
  }

}
