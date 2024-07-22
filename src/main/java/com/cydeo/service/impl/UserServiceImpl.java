package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.KeycloakService;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final KeycloakService keycloakService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, @Lazy ProjectService projectService, @Lazy TaskService taskService, KeycloakService keycloakService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.projectService = projectService;
        this.taskService = taskService;
        this.keycloakService = keycloakService;
        this.passwordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDTO findByUserName(String username) {
        User user = userRepository.findByUserNameAndIsDeleted(username, false);
        if (user == null) throw new NoSuchElementException("No User Found");
        return userMapper.convertToDto(user);
    }

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> userList = userRepository.findAllByIsDeletedOrderByFirstNameDesc(false);
        return userList.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public UserDTO save(UserDTO userDTO) {

        userDTO.setEnabled(true);
        userDTO.setPassWord(passwordEncoder.encode(userDTO.getPassWord()));

        User user = userMapper.convertToEntity(userDTO);

        userRepository.save(user);

        keycloakService.userCreate(userDTO);

        return userMapper.convertToDto(user);
    }

//    @Override
//    public void deleteByUserName(String username) {
//
//        userRepository.deleteByUserName(username);
//    }

    @Override
    public UserDTO update(UserDTO userDTO) {

        //Find user in DB
        User userInDB = userRepository.findByUserNameAndIsDeleted(userDTO.getUserName(), false);  //has id
        //set id as in db
        userDTO.setId(userInDB.getId());
        //encode the password before converting to entity
        userDTO.setPassWord(passwordEncoder.encode(userDTO.getPassWord()));
        //convert to entity
        User convertedUser = userMapper.convertToEntity(userDTO);
        //save the updated user in the db
        User updatedUser = userRepository.save(convertedUser);

        return userMapper.convertToDto(updatedUser);

    }

    @Override
    public void delete(String username) throws TicketingProjectException {

        User user = userRepository.findByUserNameAndIsDeleted(username, false);

        if (checkIfUserCanBeDeleted(user)) {
            user.setIsDeleted(true);
            user.setUserName(user.getUserName() + "-" + user.getId());  // harold@manager.com-2

            userRepository.save(user);
            keycloakService.delete(username);
        }else {
            throw new TicketingProjectException("User can not be deleted");
        }

    }

    @Override
    public List<UserDTO> listAllByRole(String role) {
        List<User> users = userRepository.findByRoleDescriptionIgnoreCaseAndIsDeleted(role, false);
        return users.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    private boolean checkIfUserCanBeDeleted(User user) {

        switch (user.getRole().getDescription()) {
            case "Manager":
                List<ProjectDTO> projectDTOList = projectService.listAllNonCompletedByAssignedManager(userMapper.convertToDto(user));
                return projectDTOList.size() == 0;
            case "Employee":
                List<TaskDTO> taskDTOList = taskService.listAllNonCompletedByAssignedEmployee(userMapper.convertToDto(user));
                return taskDTOList.size() == 0;
            case "Admin":
                List<UserDTO> adminList = listAllByRole("Admin");
                return adminList.size() > 1;
            default:
                return true;
        }

    }

}
