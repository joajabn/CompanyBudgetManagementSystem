package com.mthree.company_budget_mng_system.service;

import com.mthree.company_budget_mng_system.dto.UserDTO;
import com.mthree.company_budget_mng_system.exception.ConflictException;
import com.mthree.company_budget_mng_system.exception.ResourceNotFoundException;
import com.mthree.company_budget_mng_system.mapper.UserMapper;
import com.mthree.company_budget_mng_system.model.User;
import com.mthree.company_budget_mng_system.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        String username = userDTO.getUsername();
        log.info("Creating user with username: '{}'.", username);
        boolean exists = userRepository.existsByUsername(username);
        if (exists) {
            String message = "User with given username already exists!";
            log.error(message);
            throw new ConflictException(message);
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        log.info("Creating user completed.");
        return userMapper.toDto(savedUser);
    }

    private void checkIfUserExists(String username) {
        boolean exists = userRepository.existsByUsername(username);
        if (!exists) {
            String message = "User with given username does not exists!";
            log.error(message);
            throw new ResourceNotFoundException(message);
        }
    }

    @Transactional
    public void delete(String username) {
        log.info("Removing user with username '{}'.", username);
        checkIfUserExists(username);
        Optional<User> user = userRepository.findByUsername(username);
        userRepository.deleteById(user.get().getId());
        log.info("Removing user completed.");
    }

    public UserDTO findUserByUsername(String username) {
        log.info("Fetching user with username '{}'.", username);
        UserDTO userDTO = userRepository.findByUsername(username)
                .map(user -> userMapper.toDto(user))
                .orElseThrow(() -> {
                    String message = "User with given username does not exists!";
                    log.error(message);
                    throw new ResourceNotFoundException(message);
                });
        log.info("Fetch completed.");
        return userDTO;
    }

    @Transactional
    public UserDTO update(String username, UserDTO userDTO) {
        log.info("Updating user with username '{}'.", username);

        if (!username.equals(userDTO.getUsername())) {
            String message = "Usernames does not match!";
            log.error(message);
            throw new ConflictException(message);
        }

        checkIfUserExists(username);
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        log.info("Updating user completed.");
        return userMapper.toDto(savedUser);
    }
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users...");
        List<User> users = userRepository.findAll();
        List<UserDTO> usersDTO = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        log.info("Fetch completed.");
        return usersDTO;

    }


}
