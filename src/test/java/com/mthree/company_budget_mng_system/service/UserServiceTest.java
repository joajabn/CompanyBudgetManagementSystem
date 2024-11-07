package com.mthree.company_budget_mng_system.service;

import com.mthree.company_budget_mng_system.dto.UserDTO;
import com.mthree.company_budget_mng_system.exception.ConflictException;
import com.mthree.company_budget_mng_system.exception.ResourceNotFoundException;
import com.mthree.company_budget_mng_system.mapper.UserMapper;
import com.mthree.company_budget_mng_system.model.User;
import com.mthree.company_budget_mng_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create mock data for User and UserDTO
        userDTO = new UserDTO();
        userDTO.setUsername("john_doe");
        userDTO.setPassword("password123");

        user = new User();
        user.setUsername("john_doe");
        user.setPassword("password123");
    }

    @Test
    void createUser_ShouldCreateUserSuccessfully_WhenUsernameDoesNotExist() {
        // Given
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);

        // When
        UserDTO createdUser = userService.createUser(userDTO);

        // Then
        assertNotNull(createdUser);
        assertEquals("john_doe", createdUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_ShouldThrowConflictException_WhenUsernameAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(true);

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, () -> userService.createUser(userDTO));
        assertEquals("User with given username already exists!", exception.getMessage());
    }

    @Test
    void findUserByUsername_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDTO);

        // When
        UserDTO foundUser = userService.findUserByUsername(userDTO.getUsername());

        // Then
        assertNotNull(foundUser);
        assertEquals("john_doe", foundUser.getUsername());
    }

    @Test
    void findUserByUsername_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        // Given
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.findUserByUsername(userDTO.getUsername()));
        assertEquals("User with given username does not exists!", exception.getMessage());
    }

    @Test
    void update_ShouldUpdateUser_WhenUsernameMatchesAndUserExists() {
        // Given
        String username = userDTO.getUsername();
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(user));
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When
        UserDTO updatedUser = userService.update(userDTO.getUsername(), userDTO);

        // Then
        assertNotNull(updatedUser);
        assertEquals("john_doe", updatedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void update_ShouldThrowConflictException_WhenUsernamesDoNotMatch() {
        // Given
        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setUsername("jane_doe");

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, () -> userService.update("john_doe", updatedUserDTO));
        assertEquals("Usernames does not match!", exception.getMessage());
    }

    @Test
    void delete_ShouldDeleteUser_WhenUserExists() {
        // Given
        String username = userDTO.getUsername();
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When
        userService.delete(userDTO.getUsername());

        // Then
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        // Given
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.delete(userDTO.getUsername()));
        assertEquals("User with given username does not exists!", exception.getMessage());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDTO);

        // When
        List<UserDTO> users = userService.getAllUsers();

        // Then
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("john_doe", users.get(0).getUsername());
    }

}