package org.example.bookstore;// Generated by Selenium IDE

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;

import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;

public class SeleniumTest {

    private WebDriver driver;
    private Map<String, Object> vars;
    JavascriptExecutor js;

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        js = (JavascriptExecutor) driver;
        vars = new HashMap<String, Object>();
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void loginEmailNotFound() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.linkText("Увійти")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("user2@example.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Password1!");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.getTitle(), is("Авторизація"));
        assertThat(driver.findElement(By.id("error")).getText(), is("Email not found"));
    }

    @Test
    public void loginInvalidEmail() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.linkText("Увійти")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("example.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Password1!");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.getTitle(), is("Авторизація"));
    }

    @Test
    public void loginInvalidPassword() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.linkText("Увійти")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("user@example.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Password");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.getTitle(), is("Авторизація"));
        assertThat(driver.findElement(By.id("error")).getText(), is("Invalid password"));
    }

    @Test
    public void loginSuccess() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.linkText("Увійти")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("user@example.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Password1!");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.getTitle(), is("Головна сторінка"));
        assertThat(driver.findElement(By.id("userName")).getText(), is("User"));
    }

    @Test
    public void registerWithoutFirstName() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.linkText("Зареєструватися")).click();
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("lastName")).click();
        driver.findElement(By.id("lastName")).sendKeys("Турчин");
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("turcinalina279@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Password1!");
        driver.findElement(By.id("confirmPassword")).click();
        driver.findElement(By.id("confirmPassword")).sendKeys("Password1!");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.getTitle(), is("Реєстрація"));
        assertThat(driver.findElement(By.id("error")).getText(), is("First name is required"));
    }

    @Test
    public void registerWithoutLastName() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.linkText("Зареєструватися")).click();
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("firstName")).sendKeys("Аліна");
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("turcinalina279@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Password1!");
        driver.findElement(By.id("confirmPassword")).click();
        driver.findElement(By.id("confirmPassword")).sendKeys("Password1!");
        driver.findElement(By.cssSelector("button")).click();

        assertThat(driver.getTitle(), is("Реєстрація"));
        assertThat(driver.findElement(By.id("error")).getText(), is("Last name is required"));
    }

    @Test
    public void registerInvalidEmail() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.linkText("Зареєструватися")).click();
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("firstName")).sendKeys("Аліна");
        driver.findElement(By.id("lastName")).click();
        driver.findElement(By.id("lastName")).sendKeys("Турчин");
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("examplecom");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Password1!");
        driver.findElement(By.id("confirmPassword")).click();
        driver.findElement(By.id("confirmPassword")).sendKeys("Password1!");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.getTitle(), is("Реєстрація"));
    }

    @Test
    public void registerInvalidPassword() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.linkText("Зареєструватися")).click();
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("firstName")).sendKeys("Аліна");
        driver.findElement(By.id("lastName")).click();
        driver.findElement(By.id("lastName")).sendKeys("Турчин");
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("turcinalina279@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("pass");
        driver.findElement(By.id("confirmPassword")).click();
        driver.findElement(By.id("confirmPassword")).sendKeys("pass");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.getTitle(), is("Реєстрація"));
        assertThat(driver.findElement(By.id("error")).getText(), is("Password must be at least 8 characters long, including at least one uppercase letter, one lowercase letter, one digit, and one special character"));
    }

    @Test
    public void registerConfirmPasswordDoesNotMatch() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.linkText("Зареєструватися")).click();
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("firstName")).sendKeys("Аліна");
        driver.findElement(By.id("lastName")).click();
        driver.findElement(By.id("lastName")).sendKeys("Турчин");
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("turcinalina279@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Password1!");
        driver.findElement(By.id("confirmPassword")).click();
        driver.findElement(By.id("confirmPassword")).sendKeys("Password2!");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.getTitle(), is("Реєстрація"));
        assertThat(driver.findElement(By.id("error")).getText(), is("Password and password confirmation do not match"));
    }

    @Test
    public void registerEmailAlreadyInUse() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.linkText("Зареєструватися")).click();
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("firstName")).sendKeys("Аліна");
        driver.findElement(By.id("lastName")).click();
        driver.findElement(By.id("lastName")).sendKeys("Турчин");
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("user@example.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Password1!");
        driver.findElement(By.id("confirmPassword")).click();
        driver.findElement(By.id("confirmPassword")).sendKeys("Password1!");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.getTitle(), is("Реєстрація"));
        assertThat(driver.findElement(By.id("error")).getText(), is("Email is already in use."));

    }

    @Test
    public void registerSuccess() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.linkText("Зареєструватися")).click();
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("firstName")).sendKeys("Аліна");
        driver.findElement(By.id("lastName")).click();
        driver.findElement(By.id("lastName")).sendKeys("Турчин");
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("turcinalina279@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("Password1!");
        driver.findElement(By.id("confirmPassword")).click();
        driver.findElement(By.id("confirmPassword")).sendKeys("Password1!");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.getTitle(), is("Головна сторінка"));
        assertThat(driver.findElement(By.id("userName")).getText(), is("Аліна"));
    }


}