package com.banksphere;

import com.banksphere.entity.Role;
import com.banksphere.entity.User;
import com.banksphere.repository.UserRepository;
import com.banksphere.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:banksphere-test;DB_CLOSE_DELAY=-1",
		"spring.sql.init.mode=never"
})
@AutoConfigureMockMvc
class BankSphereApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Test
	void contextLoads() {
	}

	@Test
	void invalidBearerTokenReturnsCompleteUnauthorizedJson() throws Exception {
		mockMvc.perform(get("/api/accounts/me")
						.header("Authorization", "Bearer invalid-token"))
				.andExpect(status().isUnauthorized())
				.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
				.andExpect(jsonPath("$.timestamp").isNotEmpty())
				.andExpect(jsonPath("$.status").value(401))
				.andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
				.andExpect(jsonPath("$.message").isNotEmpty())
				.andExpect(jsonPath("$.path").value("/api/accounts/me"));
	}

	@Test
	void customerCanOpenAccountWithGeneratedAccountNumber() throws Exception {
		String username = "customer-" + java.util.UUID.randomUUID();
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode("password123"));
		user.setFullName("Test Customer");
		user.setRole(Role.CUSTOMER);
		userRepository.saveAndFlush(user);

		String token = jwtUtil.generateToken(username);

		mockMvc.perform(post("/api/accounts")
						.header("Authorization", "Bearer " + token)
						.contentType(APPLICATION_JSON)
						.content("""
								{
								  "accountType": "SAVINGS",
								  "initialDeposit": 1000.00
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.accountType").value("SAVINGS"))
				.andExpect(jsonPath("$.accountNumber").value(matchesPattern("\\d{10}")));
	}
}
