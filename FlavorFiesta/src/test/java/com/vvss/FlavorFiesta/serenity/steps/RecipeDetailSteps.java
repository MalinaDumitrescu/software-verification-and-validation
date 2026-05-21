package com.vvss.FlavorFiesta.serenity.steps;

import net.serenitybdd.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Serenity Step Library for viewing a single recipe's detail page.
 */
public class RecipeDetailSteps {

    @Step("User navigates to recipe detail page for recipe id {0}")
    public void navigateToRecipePage(WebDriver driver, WebDriverWait wait, String baseURL, long recipeId) {
        driver.get(baseURL + "/recipe/" + recipeId);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    @Step("Recipe title '{0}' should be displayed")
    public void verifyRecipeTitle(WebDriver driver, WebDriverWait wait, String title) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), title));
        assertTrue(driver.getPageSource().contains(title),
                "Recipe title '" + title + "' should be visible.");
    }

    @Step("Ingredients '{0}' should be displayed")
    public void verifyIngredients(WebDriver driver, String ingredients) {
        assertTrue(driver.getPageSource().contains(ingredients),
                "Ingredients '" + ingredients + "' should be visible.");
    }

    @Step("Instructions '{0}' should be displayed")
    public void verifyInstructions(WebDriver driver, String instructions) {
        assertTrue(driver.getPageSource().contains(instructions),
                "Instructions '" + instructions + "' should be visible.");
    }

    @Step("Comment '{0}' should be visible on the recipe page")
    public void verifyCommentVisible(WebDriver driver, String commentText) {
        assertTrue(driver.getPageSource().contains(commentText),
                "Comment '" + commentText + "' should be visible.");
    }

    @Step("Accessing non-existent recipe should show an error or no recipe content")
    public void verifyNonExistentRecipeHandled(WebDriver driver) {
        boolean isErrorPage = driver.getTitle().contains("Error")
                || driver.getPageSource().contains("Whitelabel Error Page")
                || driver.getPageSource().contains("Bad Request")
                || driver.getPageSource().contains("Internal Server Error")
                || driver.getPageSource().contains("Not Found");

        boolean validContentVisible =
                driver.getPageSource().contains("Detailed Recipe")
                || driver.getPageSource().contains("Eggs, Flour, Milk");

        assertTrue(isErrorPage || !validContentVisible,
                "Accessing a non-existent recipe should not display valid recipe details.");
    }
}


