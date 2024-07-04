package com.cydeo.controller;

import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.TaskDTO;
import com.cydeo.enums.Status;
import com.cydeo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper> getTasks() {
        List<TaskDTO> tasks = taskService.listAllTasks();

        return ResponseEntity.ok(new ResponseWrapper(
                "All Tasks are retrieved successfully.",
                tasks,
                HttpStatus.OK
        ));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ResponseWrapper> getTaskById(@PathVariable("taskId") Long id) {
        TaskDTO taskDTO = taskService.findById(id);

        return ResponseEntity.ok(new ResponseWrapper(
                "Task is retrieved successfully.",
                taskDTO,
                HttpStatus.OK
        ));
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> createTask(@RequestBody TaskDTO taskDTO) {
        taskService.save(taskDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseWrapper(
                                "Task is created successfully.",
                                taskDTO,
                                HttpStatus.CREATED
                        )
                );
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ResponseWrapper> deleteTask(@PathVariable("taskId") Long id) {
        taskService.delete(id);

        return ResponseEntity.ok(new ResponseWrapper(
                "Task is deleted succesfully.",
                HttpStatus.OK
        ));
    }

    @PutMapping
    public ResponseEntity<ResponseWrapper> updateTask(@RequestBody TaskDTO taskDTO) {
        taskService.update(taskDTO);

        return ResponseEntity.ok(new ResponseWrapper(
                "Task is updated succesfully.",
                HttpStatus.OK
        ));

    }

    @GetMapping("/employee/pending-tasks")
    public ResponseEntity<ResponseWrapper> employeePendingTasks() {
        List<TaskDTO> tasks = taskService.listAllTasksByStatusIsNot(Status.COMPLETE);

        return ResponseEntity.ok(new ResponseWrapper(
                "Tasks are retrieved succesfully.",
                tasks,
                HttpStatus.OK
        ));
    }


    @GetMapping("/employee/archive")
    public ResponseEntity<ResponseWrapper> employeeArchivedTasks() {

        List<TaskDTO> tasks = taskService.listAllTasksByStatus(Status.COMPLETE);

        return ResponseEntity.ok(new ResponseWrapper(
                "Tasks are retrieved succesfully.",
                tasks,
                HttpStatus.OK
        ));
    }

    @PutMapping("/employee/update")
    public ResponseEntity<ResponseWrapper> employeeUpdateTasks(@RequestBody TaskDTO taskDTO) {
        taskService.update(taskDTO);

        return ResponseEntity.ok(new ResponseWrapper(
                "Tasks is updated succesfully.",
                HttpStatus.OK
        ));
    }

}
