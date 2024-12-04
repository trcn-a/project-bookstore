package org.example.bookstore.Page;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MainPage {
    private WebDriver driver;

    private By loginLink = By.linkText("Увійти");
    private By registerLink = By.linkText("Зареєструватися");
    private By userName = By.id("userName");

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }


    public void clickLoginLink() {
        driver.findElement(loginLink).click();
    }

    public void clickRegisterLink() {
        driver.findElement(registerLink).click();
    }

    public String getUserName() {
        return driver.findElement(userName).getText();
    }

}

