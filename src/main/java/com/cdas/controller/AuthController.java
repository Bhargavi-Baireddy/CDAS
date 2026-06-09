package com.cdas.controller;

import com.cdas.dto.LoginRequest;
import com.cdas.entity.User;
import com.cdas.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController

@RequestMapping("/api/auth")

@CrossOrigin("*")

public class AuthController {

    @Autowired
    UserRepository repo;

    @PostMapping("/login")

    public ResponseEntity<?> login(
            @RequestBody LoginRequest req) {

        Optional<User> user =
                repo.findByEmpNo(req.getEmpNo());

        if(user.isPresent()
                &&
                user.get().getPassword()
                        .equals(req.getPassword())) {

            return ResponseEntity.ok(user.get());
        }

        return ResponseEntity
                .badRequest()
                .body("Invalid Credentials");
    }
}