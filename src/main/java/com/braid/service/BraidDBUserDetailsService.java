package com.braid.service;

import com.braid.model.UserEntity;
import com.braid.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class BraidDBUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public BraidDBUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Cannot find user with username: %s", username))
        );
        return new org.springframework.security.core.userdetails.User(userEntity.getUsername(), userEntity.getPassword(), new ArrayList<>());
    }
}