package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Find user by username (Login)
    Optional<UserEntity> findByUsername(String username);

    // Check whether username already exists (Registration)
    boolean existsByUsername(String username);
}