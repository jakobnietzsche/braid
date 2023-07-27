package com.braid.controller;

import com.braid.model.UserEntity;
import com.braid.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add-user")
    public UserEntity addNewUser(@RequestBody UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @GetMapping("/get-all-users")
    public @ResponseBody Iterable<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}
