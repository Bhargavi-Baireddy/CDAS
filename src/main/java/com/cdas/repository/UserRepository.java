package com.cdas.repository;

import com.cdas.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmpNo(String empNo);
}