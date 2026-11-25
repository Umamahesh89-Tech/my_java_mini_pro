package com.banksphere.controller;

import java.util.Map;

import com.banksphere.service.ChefService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class ChefController {

    private final ChefService chefService;

    public ChefController(ChefService chefService) {
        this.chefService = chefService;
    }

    // Personal account information endpoint
    @GetMapping("/personal-chat")
    public Map<String, String> personalChat(
            @RequestParam String message,
            Authentication authentication) {

        return Map.of(
                "answer",
                chefService.askPersonal(message, authentication)
        );
    }

    // General banking questions endpoint
    @GetMapping("/general-chat")
    public Map<String, String> generalChat(
            @RequestParam String message) {

        return Map.of(
                "answer",
                chefService.askGeneral(message)
        );
    }
}