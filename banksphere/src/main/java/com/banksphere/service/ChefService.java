package com.banksphere.service;

import org.springframework.security.core.Authentication;

public interface ChefService {

    // Personal account-related questions
    String askPersonal(String message, Authentication authentication);

    // General banking questions
    String askGeneral(String message);
}