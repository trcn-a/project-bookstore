package org.example.bookstore;

import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.UserService;
import org.example.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
       userService = new UserService(userRepository);
    }

    @Test
    void testRegisterUserSuccess() {

        String email = "test@example.com";
        String password = "Password123!";


        when(userRepository.findByEmail(email)).thenReturn(null);

        User newUser = new User();
        newUser.setFirstName("John");
        newUser.setLastName("Doe");
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole("USER");

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User registeredUser = userService.registerUser("John", "Doe", email, password);

        assertNotNull(registeredUser);
        assertEquals(email, registeredUser.getEmail());
        assertTrue(passwordEncoder.matches(password, registeredUser.getPassword()));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserEmailAlreadyExists() {
        String email = "existing@example.com";

        when(userRepository.findByEmail(email)).thenReturn(new User());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("Jane", "Doe", email, "password123")
        );

        assertEquals("Email is already in use.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticateUserSuccess() {
        String email = "test@example.com";
        String password = "Password123!";

        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setPassword(passwordEncoder.encode(password));

        when(userRepository.findByEmail(email)).thenReturn(existingUser);

        User authenticatedUser = userService.authenticateUser(email, password);

        assertNotNull(authenticatedUser);
        assertEquals(email, authenticatedUser.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testAuthenticateUserInvalidPassword() {
        String email = "test@example.com";
        String password = "wrongPassword1!";

        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setPassword(passwordEncoder.encode("correctPassword1!"));

        when(userRepository.findByEmail(email)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.authenticateUser(email, password)
        );

        assertEquals("Invalid email or password.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testAuthenticateUserNotFound() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.authenticateUser(email, "Password123!")
        );

        assertEquals("Invalid email or password.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }
}
