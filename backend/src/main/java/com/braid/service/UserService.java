package com.braid.service;

import com.braid.dto.UserRegistrationDTO;
import com.braid.model.RoleEntity;
import com.braid.model.UserEntity;
import com.braid.repository.RoleRepository;
import com.braid.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserEntity createUser(UserRegistrationDTO userRegistrationDTO) {
        UserEntity newUser = new UserEntity();
        newUser.setUsername(userRegistrationDTO.getUsername());
        newUser.setEmail(userRegistrationDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        RoleEntity role = roleRepository.findByName("ROLE_USER").get();
        newUser.setRoles(Collections.singletonList(role));
        userRepository.save(newUser);
        return newUser;
    }

    public UserEntity findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User not found with username: %s", username)));
    }

    public UserEntity updateUser(String username, UserEntity updatedUser) {
        UserEntity existingUser = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User not found with username: %s", username)));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setEmail(updatedUser.getEmail());
        return userRepository.save(existingUser);
    }

    public void deleteUser(String username) {
        UserEntity userToDelete = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User not found with username: %s", username)));

        userRepository.deleteById(username);
    }

    public Iterable<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}
