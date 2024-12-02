package org.example.bookstore;

import org.example.bookstore.Entities.User;
import org.example.bookstore.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setFirstName("Alice");
        testUser.setLastName("Smith");
        testUser.setEmail("user@example.com");
        testUser.setPassword(new BCryptPasswordEncoder().encode("Password123!"));
        testUser.setRole("USER");
        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUser_Success() throws Exception {

        mockMvc.perform(post("/user/register")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "Password123!")
                        .param("confirmPassword", "Password123!")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testRegisterUser_EmailAlreadyInUse() throws Exception {

        mockMvc.perform(post("/user/register")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "user@example.com")
                        .param("password", "Password123!")
                        .param("confirmPassword", "Password123!")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email is already in use."));
    }

    @Test
    void testLoginUser_Success() throws Exception {

        mockMvc.perform(post("/user/login")
                        .param("email", "user@example.com")
                        .param("password", "Password123!")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void testLoginUser_EmailNotFound() throws Exception {

        mockMvc.perform(post("/user/login")
                        .param("email", "nonexistent@example.com")
                        .param("password", "Password123!")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Email not found"));
    }

    @Test
    void testLoginUser_InvalidPassword() throws Exception {

        mockMvc.perform(post("/user/login")
                        .param("email", "user@example.com")
                        .param("password", "WrongPassword")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid password"));
    }

}
