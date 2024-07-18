package com.cydeo.controller;

import com.cydeo.annotation.DefaultExceptionMessage;
import com.cydeo.annotation.ExecutionTime;
import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "UserController", description = "User API")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;


    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    @RolesAllowed({"Manager","Admin"})
    @Operation(summary = "Get users")
    @ExecutionTime
    public ResponseEntity<ResponseWrapper> getUsers() {
        List<UserDTO> userList = userService.listAllUsers();

        ResponseWrapper wrapper = new ResponseWrapper(
                "User list is retrieved successfully",
                userList,
                HttpStatus.OK
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company", "Cydeo")
                .body(wrapper);

    }

    @GetMapping("/{username}")
    @RolesAllowed({"Manager","Admin"})
    @Operation(summary = "Get user by username")
    @ExecutionTime
    public ResponseEntity<ResponseWrapper> getUserByUsername(@PathVariable("username") String username) {
        UserDTO userDTO = userService.findByUserName(username);

        ResponseWrapper wrapper = new ResponseWrapper(
                "User \'" + username + "\' is retrieved successfully",
                userDTO,
                HttpStatus.OK
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company", "Cydeo")
                .body(wrapper);

    }

    @PostMapping
    @RolesAllowed("Admin")
    @Operation(summary = "Create user")
    @ExecutionTime
    public ResponseEntity<ResponseWrapper> createUser(@RequestBody UserDTO userDTO) {
        userService.save(userDTO);

        ResponseWrapper wrapper = new ResponseWrapper(
                "User \'" + userDTO.getUserName() + "\' is created successfully",
                userDTO,
                HttpStatus.CREATED
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Company", "Cydeo")
                .body(wrapper);
    }

    @PutMapping
    @RolesAllowed("Admin")
    @Operation(summary = "Update user")
    @DefaultExceptionMessage(defaultMessage = "Bad request!")
    @ExecutionTime
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO userDTO) {
        UserDTO user = userService.update(userDTO);

        ResponseWrapper wrapper = new ResponseWrapper(
                "User \'" + user.getUserName() + "\' is updated successfully",
                HttpStatus.OK
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company", "Cydeo")
                .body(wrapper);

    }

    @DeleteMapping("/{username}")
    @RolesAllowed("Admin")
    @Operation(summary = "Delete user")
    @ExecutionTime
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable("username") String username) throws TicketingProjectException {
        userService.delete(username);

        ResponseWrapper wrapper = new ResponseWrapper(
                "User \'" + username + "\' is deleted successfully",
                HttpStatus.OK);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company", "Cydeo")
                .body(wrapper);
    }


}
