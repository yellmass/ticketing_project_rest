package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.UserDTO;
import com.cydeo.service.ProjectService;
import com.cydeo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper> getProjects(){
        List<ProjectDTO> projects = projectService.listAllProjectDetails();
        List<UserDTO> managers = userService.listAllByRole("manager");

        Map<String, Object> data = new HashMap<>();
        data.put("projects", projects);
        data.put("managers", managers);

        ResponseWrapper wrapper = new ResponseWrapper(
                "All the projects retrieved.",
                data,
                HttpStatus.OK
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company","Cydeo")
                .body(wrapper);
    }
    @GetMapping("/{projectCode}")
    public ResponseEntity<ResponseWrapper> getProjectByCode(@PathVariable("projectCode") String code){
        ProjectDTO projectDTO = projectService.getByProjectCode(code);

        ResponseWrapper wrapper = new ResponseWrapper(
                "Project "+ code +" is retrieved.",
                projectDTO,
                HttpStatus.OK
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company","Cydeo")
                .body(wrapper);
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> createProject(@RequestBody ProjectDTO projectDTO){
        projectService.save(projectDTO);

        ResponseWrapper wrapper = new ResponseWrapper(
                "Project is created.",
                HttpStatus.CREATED
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Company","Cydeo")
                .body(wrapper);

    }
    @PutMapping()
    public ResponseEntity<ResponseWrapper> updateProject(@RequestBody ProjectDTO projectDTO){
        projectService.update(projectDTO);

        ResponseWrapper wrapper = new ResponseWrapper(
                "Project is updated.",
                HttpStatus.OK
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company","Cydeo")
                .body(wrapper);
    }
    @DeleteMapping("/{projectCode}")
    public ResponseEntity<ResponseWrapper> deleteProject(@PathVariable("projectCode") String code){
        projectService.delete(code);

        ResponseWrapper wrapper = new ResponseWrapper(
                "Project is deleted.",
                HttpStatus.OK
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company","Cydeo")
                .body(wrapper);
    }

    @GetMapping("/project-status")
    public ResponseEntity<ResponseWrapper> getProjectByManager(@RequestBody UserDTO assignedManager){
        List<ProjectDTO> projects = projectService.listAllProjectDetails();

        ResponseWrapper wrapper = new ResponseWrapper(
                "All the projects retrieved.",
                projects,
                HttpStatus.OK
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company","Cydeo")
                .body(wrapper);


    }

    @GetMapping("/complete/{projectCode}")
    public ResponseEntity<ResponseWrapper> managerCompleteByProject(@PathVariable("projectCode") String code){
        projectService.complete(code);

        ResponseWrapper wrapper = new ResponseWrapper(
                "Project "+ code +" has been set as completed.",
                HttpStatus.OK
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company","Cydeo")
                .body(wrapper);
    }

}
