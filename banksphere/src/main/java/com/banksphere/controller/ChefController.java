package com.banksphere.controller;

import java.util.Map;

import com.banksphere.service.ChefService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO Step 5: @RestController, @RequestMapping("/ai")
//   GET /ai/chat?message=...   answer: { "answer": "..." }
@RestController
@RequestMapping("/ai")
public class ChefController {
   @Autowired
   private ChefService chefService;

   @GetMapping("/chat")
   public Map<String, String> chat(@RequestParam String message) {
      return Map.of("answer", chefService.ask(message));
   }

}
