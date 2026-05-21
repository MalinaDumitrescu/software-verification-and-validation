package com.vvss.FlavorFiesta.serenity.steps;

import net.serenitybdd.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Serenity Step Library for recipe-list browsing actions.
 */
public class RecipeBrowsingSteps {

    @Step("User navigates to the home/recipe-list page")
    public void navigateToHomePage(WebDriver driver, WebDriverWait wait, String homeURL) {
        driver.get(homeURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Recipes"));
    }

    @Step("Recipe list should contain {0} recipe cards")
    public void verifyRecipeCount(WebDriver driver, int expectedCount) {
        List<WebElement> cards = driver.findElements(By.className("card"));
        assertEquals(expectedCount, cards.size(),
                "Expected " + expectedCount + " recipe card(s) on the home page.");
    }

    @Step("Recipe titled '{0}' should be visible in the list")
    public void verifyRecipeVisible(WebDriver driver, String title) {
        assertTrue(driver.getPageSource().contains(title),
                "Recipe '" + title + "' should appear in the recipe list.");
    }

    @Step("Page should load correctly and show 'Recipes' heading even when list is empty")
    public void verifyPageLoadsWhenEmpty(WebDriver driver) {
        assertTrue(driver.getPageSource().contains("Recipes"),
                "The page should render the 'Recipes' heading even with no recipes.");
    }
}


