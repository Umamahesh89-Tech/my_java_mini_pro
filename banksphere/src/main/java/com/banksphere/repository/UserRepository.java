package com.banksphere.repository;

import com.banksphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Used for login and finding out "who is calling?"
    Optional<User> findByUsername(String username);
    
    // Used during registration to return 409 if the username is already taken
    boolean existsByUsername(String username);
}