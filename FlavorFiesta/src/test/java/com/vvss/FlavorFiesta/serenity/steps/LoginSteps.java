package com.vvss.FlavorFiesta.serenity.steps;

import net.serenitybdd.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Serenity Step Library for login/logout actions.
 * Each @Step method becomes a documented step in the Serenity HTML report.
 */
public class LoginSteps {

    @Step("User navigates to the login page")
    public void navigateToLoginPage(WebDriver driver, WebDriverWait wait, String loginURL) {
        driver.get(loginURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
    }

    @Step("User enters username '{0}' and password")
    public void enterCredentials(WebDriver driver, String username, String password) {
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
    }

    @Step("User submits the login form")
    public void submitLoginForm(WebDriver driver, WebDriverWait wait) {
        driver.findElement(By.id("submit-button")).click();
        wait.until(ExpectedConditions.urlContains("/home"));
    }

    @Step("User should be on the home page")
    public void verifyOnHomePage(WebDriver driver) {
        assertTrue(driver.getCurrentUrl().contains("/home"),
                "User should be redirected to /home after login.");
    }

    @Step("User should still be on the login page (login failed)")
    public void verifyStillOnLoginPage(WebDriver driver, WebDriverWait wait, String loginURL) {
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"),
                "User should remain on /login after a failed login attempt.");
    }

    @Step("Sign-out button should be visible")
    public void verifySignOutVisible(WebDriver driver) {
        assertTrue(driver.findElement(By.id("sign-out")).isDisplayed(),
                "Sign-out button should be visible after login.");
    }

    @Step("Sign-out button should not be present")
    public void verifySignOutAbsent(WebDriver driver) {
        assertFalse(driver.findElements(By.id("sign-out")).size() > 0
                        && driver.findElement(By.id("sign-out")).isDisplayed(),
                "Sign-out button should not be present when not logged in.");
    }
}


