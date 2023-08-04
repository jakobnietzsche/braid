package com.braid.controller;

import com.braid.dto.UserRegistrationDTO;
import com.braid.model.UserEntity;
import com.braid.repository.UserRepository;
import com.braid.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            return new ResponseEntity<>("Username is already associated with an account", HttpStatus.BAD_REQUEST);
        }
        UserEntity newUser = userService.createUser(registrationDTO);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @GetMapping("/get-all-users")
    public @ResponseBody Iterable<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/delete-user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }
}
