package org.example.bookstore;

import org.example.bookstore.Controllers.UserController;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.UserService;
import org.example.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
class UserWithMockIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new UserController(userService)).build();
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword(new BCryptPasswordEncoder().encode("Password123!"));
    }

    @Test
    void testRegisterUser_Success() throws Exception {

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

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

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);

        mockMvc.perform(post("/user/register")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "Password123!")
                        .param("confirmPassword", "Password123!")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email is already in use."));
    }


    @Test
    void testLoginUser_Success() throws Exception {

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);

        mockMvc.perform(post("/user/login")
                        .param("email", "john.doe@example.com")
                        .param("password", "Password123!")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testLoginUser_EmailNotFound() throws Exception {

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(null);

        mockMvc.perform(post("/user/login")
                        .param("email", "nonexistent@example.com")
                        .param("password", "Password123!")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Email not found"));
    }

    @Test
    void testLoginUser_InvalidPassword() throws Exception {

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);


        mockMvc.perform(post("/user/login")
                        .param("email", "john.doe@example.com")
                        .param("password", "WrongPassword")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid password"));
    }

}
