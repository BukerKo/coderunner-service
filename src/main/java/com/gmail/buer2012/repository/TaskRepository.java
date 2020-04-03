package com.gmail.buer2012.repository;

import com.gmail.buer2012.entity.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {
    Task findFirstByTaskIsNotNull();
}
