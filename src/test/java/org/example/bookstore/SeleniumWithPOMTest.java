package org.example.bookstore;

import org.example.bookstore.Page.LoginPage;
import org.example.bookstore.Page.MainPage;
import org.example.bookstore.Page.RegistrationPage;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumWithPOMTest {

    private WebDriver driver;
    private LoginPage loginPage;
    private RegistrationPage registrationPage;
    private MainPage mainPage;

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        loginPage = new LoginPage(driver);
        registrationPage = new RegistrationPage(driver);
        mainPage = new MainPage(driver);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void loginEmailNotFound() {
        driver.get("http://localhost:8080/");
        mainPage.clickLoginLink();
        loginPage.enterEmail("user2@example.com");
        loginPage.enterPassword("Password1!");
        loginPage.clickLoginButton();
        assertThat(driver.getTitle(), is("Авторизація"));
        assertThat(loginPage.getErrorMessage(), is("Email not found"));
    }

    @Test
    public void loginInvalidEmail() {
        driver.get("http://localhost:8080/");
        mainPage.clickLoginLink();
        loginPage.enterEmail("example.com");
        loginPage.enterPassword("Password1!");
        loginPage.clickLoginButton();
        assertThat(driver.getTitle(), is("Авторизація"));
    }

    @Test
    public void loginInvalidPassword() {
        driver.get("http://localhost:8080/");
        mainPage.clickLoginLink();
        loginPage.enterEmail("user@example.com");
        loginPage.enterPassword("Password");
        loginPage.clickLoginButton();
        assertThat(driver.getTitle(), is("Авторизація"));
        assertThat(loginPage.getErrorMessage(), is("Invalid password"));
    }

    @Test
    public void loginSuccess() {
        driver.get("http://localhost:8080/");
        mainPage.clickLoginLink();
        loginPage.enterEmail("user@example.com");
        loginPage.enterPassword("Password1!");
        loginPage.clickLoginButton();
        assertThat(driver.getTitle(), is("Головна сторінка"));
        assertThat(mainPage.getUserName(), is("User"));
    }

    @Test
    public void registerWithoutFirstName() {
        driver.get("http://localhost:8080/");
        mainPage.clickRegisterLink();
        registrationPage.enterLastName("Турчин");
        registrationPage.enterEmail("turcinalina279@gmail.com");
        registrationPage.enterPassword("Password!1");
        registrationPage.enterConfirmPassword("Password!1");
        registrationPage.clickRegisterButton();
        assertThat(driver.getTitle(), is("Реєстрація"));
        assertThat(registrationPage.getErrorMessage(), is("First name is required"));
    }

    @Test
    public void registerWithoutLastName() {
        driver.get("http://localhost:8080/");
        mainPage.clickRegisterLink();
        registrationPage.enterFirstName("Аліна");
        registrationPage.enterEmail("turcinalina279@gmail.com");
        registrationPage.enterPassword("Password1!");
        registrationPage.enterConfirmPassword("Password1!");
        registrationPage.clickRegisterButton();
        assertThat(driver.getTitle(), is("Реєстрація"));
        assertThat(registrationPage.getErrorMessage(), is("Last name is required"));
    }

    @Test
    public void registerInvalidEmail() {
        driver.get("http://localhost:8080/");
        mainPage.clickRegisterLink();
        registrationPage.enterFirstName("Аліна");
        registrationPage.enterLastName("Турчин");
        registrationPage.enterEmail("examplecom");
        registrationPage.enterPassword("Password1!");
        registrationPage.enterConfirmPassword("Password!1");
        registrationPage.clickRegisterButton();
        assertThat(driver.getTitle(), is("Реєстрація"));
    }

    @Test
    public void registerInvalidPassword() {
        driver.get("http://localhost:8080/");
        mainPage.clickRegisterLink();
        registrationPage.enterFirstName("Аліна");
        registrationPage.enterLastName("Турчин");
        registrationPage.enterEmail("turcinalina279@gmail.com");
        registrationPage.enterPassword("pass");
        registrationPage.enterConfirmPassword("pass");
        registrationPage.clickRegisterButton();
        assertThat(driver.getTitle(), is("Реєстрація"));
        assertThat(registrationPage.getErrorMessage(), is("Password must be at least 8 characters long, including at least one uppercase letter, one lowercase letter, one digit, and one special character"));
    }

    @Test
    public void registerConfirmPasswordDoesNotMatch() {
        driver.get("http://localhost:8080/");
        mainPage.clickRegisterLink();
        registrationPage.enterFirstName("Аліна");
        registrationPage.enterLastName("Турчин");
        registrationPage.enterEmail("turcinalina279@gmail.com");
        registrationPage.enterPassword("Password1!");
        registrationPage.enterConfirmPassword("Password2!");
        registrationPage.clickRegisterButton();
        assertThat(driver.getTitle(), is("Реєстрація"));
        assertThat(registrationPage.getErrorMessage(), is("Password and password confirmation do not match"));
    }

    @Test
    public void registerEmailAlreadyInUse() {

        driver.get("http://localhost:8080/");
        mainPage.clickRegisterLink();
        registrationPage.enterFirstName("Аліна");
        registrationPage.enterLastName("Турчин");
        registrationPage.enterEmail("user@example.com");
        registrationPage.enterPassword("Password1!");
        registrationPage.enterConfirmPassword("Password1!");
        registrationPage.clickRegisterButton();

        assertThat(driver.getTitle(), is("Реєстрація"));
        assertThat(registrationPage.getErrorMessage(), is("Email is already in use."));

    }

    @Test
    public void registerSuccess() {
        driver.get("http://localhost:8080/");
        mainPage.clickRegisterLink();
        registrationPage.enterFirstName("Аліна");
        registrationPage.enterLastName("Турчин");
        registrationPage.enterEmail("turcinalina279@gmail.com");
        registrationPage.enterPassword("Password1!");
        registrationPage.enterConfirmPassword("Password1!");
        registrationPage.clickRegisterButton();
        assertThat(driver.getTitle(), is("Головна сторінка"));
        assertThat(mainPage.getUserName(), is("Аліна"));
    }
}
