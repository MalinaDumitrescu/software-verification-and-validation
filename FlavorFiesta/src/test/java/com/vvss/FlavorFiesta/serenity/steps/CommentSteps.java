package com.vvss.FlavorFiesta.serenity.steps;

import net.serenitybdd.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Serenity Step Library for comment-posting actions.
 */
public class CommentSteps {

    @Step("User navigates to recipe page and waits for comment form")
    public void openRecipeCommentForm(WebDriver driver, WebDriverWait wait, String baseURL, long recipeId) {
        driver.get(baseURL + "/recipe/" + recipeId);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("comment")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("add-comment-form")));
    }

    @Step("User types comment: '{0}'")
    public void typeComment(WebDriver driver, String commentText) {
        WebElement textarea = driver.findElement(By.id("comment"));
        textarea.clear();
        textarea.sendKeys(commentText);
    }

    @Step("User submits the comment form")
    public void submitCommentForm(WebDriver driver, WebDriverWait wait) {
        driver.findElement(By.cssSelector("#add-comment-form button[type='submit']")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    @Step("Comment '{0}' should appear on the page after submission")
    public void verifyCommentAppeared(WebDriver driver, WebDriverWait wait, String commentText) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), commentText));
        assertTrue(driver.getPageSource().contains(commentText),
                "The new comment should appear on the recipe page after submission.");
    }

    @Step("Submitting empty comment form")
    public void submitEmptyCommentForm(WebDriver driver) {
        WebElement textarea = driver.findElement(By.id("comment"));
        textarea.clear();
        driver.findElement(By.cssSelector("#add-comment-form button[type='submit']")).click();
        // Small pause for any JS / browser validation to run
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    @Step("Comment count should remain {0} (empty comment was rejected)")
    public void verifyCommentCountUnchanged(long before, long after) {
        assertEquals(before, after,
                "Submitting an empty comment should not increase the comment count.");
    }

    @Step("User should still be on recipe page {0}")
    public void verifyStillOnRecipePage(WebDriver driver, long recipeId) {
        assertTrue(driver.getCurrentUrl().contains("/recipe/" + recipeId),
                "User should remain on the same recipe page.");
    }
}


