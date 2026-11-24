package com.banksphere.serviceImpl;


import com.banksphere.service.ChefService;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChefServiceImpl implements ChefService {
    private final ChatClient chatClient;

    public ChefServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder.defaultSystem(SYSTEM_MESSAGE).build();
    }

    private static final String SYSTEM_MESSAGE = """
            You are a banking management system.
            Answer only questions about banking, accounts and money.
            If the question is about anything else, reply exactly:
            "Sorry, I can only help with banking."
            Keep every answer under 120 words.
            """;

    @Override
    public String ask(String message) {
        return chatClient.prompt().user(message).call().content();
    }

}
