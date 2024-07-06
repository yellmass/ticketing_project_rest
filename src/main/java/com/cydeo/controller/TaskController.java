package com.cydeo.controller;

import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.TaskDTO;
import com.cydeo.enums.Status;
import com.cydeo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> getTasks() {
        List<TaskDTO> tasks = taskService.listAllTasks();

        return ResponseEntity.ok(new ResponseWrapper(
                "All Tasks are retrieved successfully.",
                tasks,
                HttpStatus.OK
        ));
    }

    @GetMapping("/{taskId}")
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> getTaskById(@PathVariable("taskId") Long id) {
        TaskDTO taskDTO = taskService.findById(id);

        return ResponseEntity.ok(new ResponseWrapper(
                "Task is retrieved successfully.",
                taskDTO,
                HttpStatus.OK
        ));
    }

    @PostMapping
    @RolesAllowed("Manager")
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
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> deleteTask(@PathVariable("taskId") Long id) {
        taskService.delete(id);

        return ResponseEntity.ok(new ResponseWrapper(
                "Task is deleted succesfully.",
                HttpStatus.OK
        ));
    }

    @PutMapping
    @RolesAllowed("Manager")
    public ResponseEntity<ResponseWrapper> updateTask(@RequestBody TaskDTO taskDTO) {
        taskService.update(taskDTO);

        return ResponseEntity.ok(new ResponseWrapper(
                "Task is updated succesfully.",
                HttpStatus.OK
        ));

    }

    @GetMapping("/employee/pending-tasks")
    @RolesAllowed("Employee")
    public ResponseEntity<ResponseWrapper> employeePendingTasks() {
        List<TaskDTO> tasks = taskService.listAllTasksByStatusIsNot(Status.COMPLETE);

        return ResponseEntity.ok(new ResponseWrapper(
                "Tasks are retrieved succesfully.",
                tasks,
                HttpStatus.OK
        ));
    }


    @GetMapping("/employee/archive")
    @RolesAllowed("Employee")
    public ResponseEntity<ResponseWrapper> employeeArchivedTasks() {

        List<TaskDTO> tasks = taskService.listAllTasksByStatus(Status.COMPLETE);

        return ResponseEntity.ok(new ResponseWrapper(
                "Tasks are retrieved succesfully.",
                tasks,
                HttpStatus.OK
        ));
    }

    @PutMapping("/employee/update")
    @RolesAllowed("Employee")
    public ResponseEntity<ResponseWrapper> employeeUpdateTasks(@RequestBody TaskDTO taskDTO) {
        taskService.update(taskDTO);

        return ResponseEntity.ok(new ResponseWrapper(
                "Tasks is updated succesfully.",
                HttpStatus.OK
        ));
    }

}
