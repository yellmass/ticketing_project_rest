package com.cydeo.controller;

import com.cydeo.dto.ResponseWrapper;
import com.cydeo.dto.UserDTO;
import com.cydeo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseWrapper> getUsers(){
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
    public ResponseEntity<ResponseWrapper> getUserByUsername(@PathVariable("username") String username){
        UserDTO userDTO = userService.findByUserName(username);

        ResponseWrapper wrapper = new ResponseWrapper(
                "User \'"+ username  +"\' is retrieved successfully",
                userDTO,
                HttpStatus.OK
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company", "Cydeo")
                .body(wrapper);

    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> createUser(@RequestBody UserDTO userDTO){
        userService.save(userDTO);

        ResponseWrapper wrapper = new ResponseWrapper(
                "User \'"+ userDTO.getUserName()  +"\' is created successfully",
                HttpStatus.CREATED
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Company", "Cydeo")
                .body(wrapper);
    }
    @PutMapping
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO userDTO){
        UserDTO user = userService.update(userDTO);

        ResponseWrapper wrapper = new ResponseWrapper(
                "User \'"+ user.getUserName()  +"\' is updated successfully",
                HttpStatus.OK
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company", "Cydeo")
                .body(wrapper);

    }

    @DeleteMapping("/{username}")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable("username") String username){
        userService.delete(username);

        ResponseWrapper wrapper = new ResponseWrapper(
                "User \'"+ username  +"\' is deleted successfully",
                HttpStatus.OK
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Company", "Cydeo")
                .body(wrapper);
    }


}
