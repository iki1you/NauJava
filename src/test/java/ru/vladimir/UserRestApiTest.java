package ru.vladimir;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.vladimir.model.User;
import ru.vladimir.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class UserRestApiTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void createHashesPasswordAndHidesItInResponse() throws Exception {
        String login = "test-" + UUID.randomUUID();
        MvcResult result = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content("""
                {"login":"%s","firstName":"Test","lastName":"User","password":"test-password"}
                """.formatted(login)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        Long id = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        User savedUser = userRepository.findById(id).orElseThrow();
        assertTrue(passwordEncoder.matches("test-password", savedUser.getPasswordHash()));
    }

    @Test
    void updateChangesPasswordOnlyWhenProvided() throws Exception {
        User user = new User();
        String originalHash = passwordEncoder.encode("old-password");
        user.setLogin("test-" + UUID.randomUUID());
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPasswordHash(originalHash);
        userRepository.save(user);

        mockMvc.perform(patch("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"firstName":"Updated"}
                        """))
                .andExpect(status().isOk());
        assertEquals("Updated", user.getFirstName());
        assertEquals(originalHash, user.getPasswordHash());

        mockMvc.perform(patch("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"password":"new-password"}
                        """))
                .andExpect(status().isOk());
        assertTrue(passwordEncoder.matches("new-password", user.getPasswordHash()));
    }
}
