package com.cdas.controller;

import com.cdas.entity.User;
import com.cdas.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository repo;

    // GET all users
    @GetMapping
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    // POST create new user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        Optional<User> existing = repo.findByEmpNo(user.getEmpNo());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Employee number " + user.getEmpNo() + " already exists.");
        }
        if (user.getActive() == null) user.setActive(true);
        User saved = repo.save(user);
        return ResponseEntity.ok(saved);
    }
}