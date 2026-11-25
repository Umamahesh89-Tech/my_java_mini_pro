package com.banksphere.serviceImpl;

import com.banksphere.dto.AccountResponse;
import com.banksphere.service.AccountService;
import com.banksphere.service.ChefService;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChefServiceImpl implements ChefService {

    private final ChatClient chatClient;
    private final AccountService accountService;

    public ChefServiceImpl(
            ChatClient.Builder builder,
            AccountService accountService) {

        this.chatClient = builder.build();
        this.accountService = accountService;
    }

    private static final String SYSTEM_MESSAGE = """
            You are a banking assistant for BankSphere.

            Answer questions related to banking and financial accounts.

            Provide clear, simple and accurate answers.

            Never invent banking information.

            Keep every answer under 120 words.
            """;

    // PERSONAL ACCOUNT CHATBOT
    @Override
    public String askPersonal(
            String message,
            Authentication authentication) {

        // Retrieve authenticated user's accounts from MySQL
        List<AccountResponse> accounts =
                accountService.getMyAccounts(authentication);

        if (accounts.isEmpty()) {
            return "You don't have any bank accounts registered in BankSphere.";
        }

        // Prepare account information
        StringBuilder accountData = new StringBuilder();

        for (AccountResponse account : accounts) {

            accountData.append("Account Type: ")
                    .append(account.getAccountType())
                    .append("\n");

            accountData.append("Account Number: ")
                    .append(maskAccountNumber(account.getAccountNumber()))
                    .append("\n");

            accountData.append("Balance: ₹")
                    .append(account.getBalance())
                    .append("\n");

            accountData.append("Opened On: ")
                    .append(account.getOpenedAt())
                    .append("\n\n");
        }

        // Send account information and question to Gemini
        return chatClient.prompt()
                .system("""
                        You are BankSphere's personal banking assistant.

                        Answer using only the supplied account information.

                        Never invent account details or balances.

                        Never reveal information about other customers.

                        If the requested information is unavailable,
                        clearly mention that.

                        Keep answers under 120 words.
                        """)
                .user("""
                        Customer account information:

                        %s

                        Customer question:

                        %s
                        """.formatted(accountData, message))
                .call()
                .content();
    }

    // GENERAL BANKING CHATBOT
    @Override
    public String askGeneral(String message) {

        return chatClient.prompt()
                .system(SYSTEM_MESSAGE)
                .user(message)
                .call()
                .content();
    }

    // Mask account number
    private String maskAccountNumber(String accountNumber) {

        if (accountNumber == null || accountNumber.length() <= 4) {
            return "****";
        }

        return "******" +
                accountNumber.substring(accountNumber.length() - 4);
    }
}