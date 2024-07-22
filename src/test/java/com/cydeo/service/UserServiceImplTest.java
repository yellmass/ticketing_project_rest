package com.cydeo.service;


import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private ProjectService projectService;
    @Mock
    private TaskService taskService;
    @Mock
    private KeycloakService keycloakService;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    User user;
    UserDTO userDTO;

    @BeforeEach
    public void setUp(){
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserName("user");
        user.setPassWord("Abc1");
        user.setEnabled(true);
        user.setRole(new Role("Manager"));


        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setUserName("user");
        userDTO.setPassWord("Abc1");
        userDTO.setEnabled(true);

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setDescription("Manager");

        userDTO.setRole(roleDTO);

    }

    private User getUserWithRole(String role){
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserName("user");
        user.setPassWord("encoded1");
        user.setEnabled(true);
        user.setRole(new Role(role));

        return user;
    }

    private List<User> getUsers(){
        User user = new User();
        user.setId(2L);
        user.setFirstName("Emily");
        user.setLastName("Wert");
        user.setUserName("emily");
        user.setPassWord("encoded2");
        user.setEnabled(true);
        user.setRole(new Role("Admin"));

        return List.of(getUserWithRole("Admin"),user);
    }

    private UserDTO getUserDTO(String role){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setUserName("user");
        userDTO.setPassWord("Abc1");
        userDTO.setEnabled(true);

        userDTO.setRole(new RoleDTO(null,role));

        return userDTO;
    }

    private List<UserDTO> getUserDTOs(){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(2L);
        userDTO.setFirstName("Emily");
        userDTO.setLastName("Wert");
        userDTO.setUserName("emily");
        userDTO.setPassWord("Abc2");
        userDTO.setEnabled(true);

        userDTO.setRole(new RoleDTO(null,"Admin"));

        return List.of(getUserDTO("Admin"),userDTO);
    }



    @Test
    public void listAllUsers_Test(){
        when(userRepository.findAllByIsDeletedOrderByFirstNameDesc(false)).thenReturn(getUsers());
        when(userMapper.convertToDto(getUsers().get(0))).thenReturn(getUserDTOs().get(0));
        when(userMapper.convertToDto(getUsers().get(1))).thenReturn(getUserDTOs().get(1));

        List<UserDTO> expected = getUserDTOs();

        List<UserDTO> actual = userService.listAllUsers();

        //assertEquals(expected, actual);  // we used @EqualsAndHashCode in UserDTO class

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected); // don't need to put @EqualsAndHasCode

        verify(userRepository).findAllByIsDeletedOrderByFirstNameDesc(false);
        verify(userMapper, atLeast(1)).convertToDto(any(User.class));

    }

    @Test
    public void should_throw_noSuchElementException_when_user_not_found(){

        //given
        when(userRepository.findByUserNameAndIsDeleted(anyString(),anyBoolean())).thenReturn(null);

        //when
        Throwable actualException = catchThrowable(()->userService.findByUserName("anyUserName"));

        //then - verify - assert
        assertThat(actualException).isInstanceOf(NoSuchElementException.class);
        assertThat(actualException.getMessage()).isEqualTo("No User Found");

//        assertThrows(RuntimeException.class, ()->userService.findByUserName("anyUserName"));
//
//        Throwable actual = assertThrowsExactly(NoSuchElementException.class, ()->userService.findByUserName("anyUserName"));
//        assertEquals("No User Found", actual.getMessage());
    }


    // 	User Story - 1: As a user of the application, I want my password to be encoded
    //	so that my account remains secure.
    //
    //	Acceptance Criteria:
    //	1 - When a user creates a new account, their password should be encoded using
    //	a secure algorithm such as bcrypt or PBKDF2.
    //
    //	2 - Passwords should not be stored in plain text in the database or any other storage.
    //
    //	3 - Passwords encoding should be implemented consistently throughout the application,
    //	including any password reset or change functionality.

    @Test
    void should_encode_user_password_on_SAVE_operation(){

        //given
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userMapper.convertToEntity(userDTO)).thenAnswer(invocation ->{
            UserDTO input = invocation.getArgument(0);
            user.setPassWord(input.getPassWord());
            return user;
        });
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.convertToDto(any(User.class))).thenReturn(userDTO);

        String expectedPassword = "encoded-password";

        //when
        UserDTO savedUser = userService.save(userDTO);

        //then
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        assertEquals(expectedPassword, savedUser.getPassWord());

        assertThat(user.getPassWord()).isEqualTo(expectedPassword);



    }

    @Test
    void should_encode_user_password_on_UPDATE_operation(){

        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(user);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userMapper.convertToEntity(userDTO)).thenAnswer(p->{
            UserDTO input = p.getArgument(0);
            user.setPassWord(input.getPassWord());
            user.setId(input.getId());
            return user;
        });
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.convertToDto(user)).thenReturn(userDTO);

        String expectedPassword = "encoded-password";

        UserDTO updatedUser = userService.update(userDTO);


        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        assertEquals(expectedPassword, updatedUser.getPassWord());

    }

    // 	User Story 2: As an admin, I shouldn't be able to delete a manager user,
    // 	if that manager has projects linked to them to prevent data loss.
    //
    //	Acceptance Criteria:
    //
    //	1 - The system should prevent a manager user from being deleted
    //	if they have projects linked to them.
    //	2 - An error message should be displayed to the user if they attempt
    //	to delete a manager user with linked projects.
    //


    @Test
    void should_delete_manager() throws TicketingProjectException {

        //user must be manager
        user.setRole(new Role("Manager"));

        //given
        when(userRepository.findByUserNameAndIsDeleted(anyString(),anyBoolean())).thenReturn(user);
        when(projectService.listAllNonCompletedByAssignedManager(any())).thenReturn(new ArrayList<>());
        when(userRepository.save(user)).thenReturn(user);

        //when
        userService.delete(user.getUserName());

        //then
        assertTrue(user.getIsDeleted());
        assertThat(user.getUserName()).isNotEqualTo("user");
    }

    @Test
    void should_throw_exception_when_deleting_manager_with_project(){

        //assign user as manager
        user.setRole(new Role("Manager"));

        //given
        when(userRepository.findByUserNameAndIsDeleted(anyString(),anyBoolean())).thenReturn(user);
        when(projectService.listAllNonCompletedByAssignedManager(any())).thenReturn(List.of(new ProjectDTO(),new ProjectDTO()));

        //when
        Throwable throwable = catchThrowable(()->{
            userService.delete(user.getUserName());
        });

        //then
        assertThat(throwable.getMessage()).isEqualTo("User can not be deleted");

    }

    //	User Story 3: As an admin, I shouldn't be able to delete an employee user,
    //	if that employee has tasks linked to them to prevent data loss.
    //
    //	Acceptance Criteria:
    //
    //	1 - The system should prevent an employee user from being deleted
    //	if they have tasks linked to them.
    //	2 - An error message should be displayed to the user if they attempt
    //	to delete an employee user with linked tasks.

    @Test
    void should_delete_employee() throws TicketingProjectException {

        //user must be manager
        user.setRole(new Role("Employee"));

        //given
        when(userRepository.findByUserNameAndIsDeleted(anyString(),anyBoolean())).thenReturn(user);
        when(taskService.listAllNonCompletedByAssignedEmployee(any())).thenReturn(new ArrayList<>());
        when(userRepository.save(user)).thenReturn(user);

        //when
        userService.delete(user.getUserName());

        //then
        assertTrue(user.getIsDeleted());
        assertThat(user.getUserName()).isNotEqualTo("user");
    }

    @Test
    void should_throw_exception_when_deleting_employee_with_task(){

        //assign user as manager
        user.setRole(new Role("Employee"));

        //given
        when(userRepository.findByUserNameAndIsDeleted(anyString(),anyBoolean())).thenReturn(user);
        when(taskService.listAllNonCompletedByAssignedEmployee(any())).thenReturn(List.of(new TaskDTO(),new TaskDTO()));

        //when
        Throwable throwable = catchThrowable(()->{
            userService.delete(user.getUserName());
        });

        //then
        assertThat(throwable.getMessage()).isEqualTo("User can not be deleted");

    }

    //	User Story 4: As an admin, I shouldn't be able to delete an admin user,
    //	if that admin is the last admin in the system.
    //
    //	Acceptance Criteria:
    //
    //	1 - The system should prevent an admin user from being deleted
    //	if it is the last admin.
    //	2 - An error message should be displayed to the user if there is an
    //	attempt to delete the last admin user.

    @Test
    void should_delete_admin() throws TicketingProjectException {
        //assign user as admin
        user.setRole(new Role("Admin"));

        //given
        when(userRepository.findByUserNameAndIsDeleted(anyString(),anyBoolean())).thenReturn(user);
        when(userRepository.findByRoleDescriptionIgnoreCaseAndIsDeleted(anyString(),anyBoolean())).thenReturn(getUsers());
        when(userMapper.convertToDto(getUsers().get(0))).thenReturn(getUserDTOs().get(0));
        when(userMapper.convertToDto(getUsers().get(1))).thenReturn(getUserDTOs().get(1));
        when(userRepository.save(user)).thenReturn(user);

        //when
        userService.delete(user.getUserName());

        //then
        assertTrue(user.getIsDeleted());
        assertThat(user.getUserName()).isNotEqualTo("user");

    }


    @Test
    void should_throw_exception_when_deleting_admin_if_only_one_left(){

        //assign user as manager
        user.setRole(new Role("Admin"));

        //given
        when(userRepository.findByUserNameAndIsDeleted(anyString(),anyBoolean())).thenReturn(user);
        when(userRepository.findByRoleDescriptionIgnoreCaseAndIsDeleted(anyString(),anyBoolean())).thenReturn(List.of(user)); //assume only one admin left
        when(userMapper.convertToDto(user)).thenReturn(userDTO);


        //when
        Throwable throwable = catchThrowable(()->{
            userService.delete(user.getUserName());
        });

        //then
        assertThat(throwable.getMessage()).isEqualTo("User can not be deleted");

    }

}
