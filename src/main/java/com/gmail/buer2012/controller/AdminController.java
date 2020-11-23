package com.gmail.buer2012.controller;

import com.gmail.buer2012.entity.Task;
import com.gmail.buer2012.repository.RunInformationRepository;
import com.gmail.buer2012.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  private final RunInformationRepository runInformationRepository;
  private final TaskRepository taskRepository;

  @GetMapping("/results")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getResults(Pageable page, @RequestParam(required = false, defaultValue = "") String username) {
    return ResponseEntity.ok(runInformationRepository.findByUser_UsernameContainingIgnoreCase(page, username));
  }

  @DeleteMapping("/result/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteResult(@PathVariable Long id) {
    runInformationRepository.deleteById(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/setTask")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> setTask(@RequestBody String newTask) {
    Task task = taskRepository.findFirstByTaskIsNotNull();
    task.setTask(newTask);
    taskRepository.save(task);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/getTask")
  public ResponseEntity<?> getTask() {
    Task task = taskRepository.findFirstByTaskIsNotNull();
    return ResponseEntity.ok(task);
  }

}
