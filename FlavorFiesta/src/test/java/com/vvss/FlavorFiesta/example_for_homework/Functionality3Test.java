package com.vvss.FlavorFiesta.example_for_homework;

import com.vvss.FlavorFiesta.models.Recipe;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.services.CommentService;
import com.vvss.FlavorFiesta.services.RecipeService;
import com.vvss.FlavorFiesta.services.UserService;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

// Posting a comment on a recipe
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Functionality3Test {

    @LocalServerPort
    private int port;

    private String baseURL;
    private String loginPageURL;
    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    private UserService userService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CommentService commentService;

    private User testUser;
    private Recipe testRecipe;
    private final String testPassword = "test123";

    @BeforeAll
    void setup() {
        baseURL = "http://localhost:" + port;
        loginPageURL = baseURL + "/login";

        testUser = userService.saveUser(
                new User("comment_user", "comment_user@test.com", testPassword)
        );

        testRecipe = recipeService.saveRecipe(
                new Recipe(testUser, "Comment Recipe", "Tomatoes, Salt", "Cut and season")
        );

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    void tearDown() {
        commentService.getAllComments().forEach(commentService::deleteComment);
        recipeService.getAllRecipes().forEach(recipeService::deleteRecipe);
        userService.getAllUsers().forEach(userService::deleteUser);

        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    void resetBrowserState() {
        driver.manage().deleteAllCookies();
    }

    @Test
    void testFunctionality3_happyPath() {
        login(testUser.getUsername(), testPassword);

        driver.get(baseURL + "/recipe/" + testRecipe.getId());

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("recipe-id")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("comment")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("add-comment-form")));

        String uniqueComment = "Amazing recipe " + System.currentTimeMillis();

        WebElement commentTextarea = driver.findElement(By.id("comment"));
        commentTextarea.clear();
        commentTextarea.sendKeys(uniqueComment);

        // submit form ul
        driver.findElement(By.cssSelector("#add-comment-form button[type='submit']")).click();

        // dupa submit, pagina face reload
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), uniqueComment));

        assertTrue(driver.getPageSource().contains(uniqueComment),
                "The new comment should appear on the recipe page after submission.");
    }

    @Test
    void testFunctionality3_negativePath() {
        login(testUser.getUsername(), testPassword);

        driver.get(baseURL + "/recipe/" + testRecipe.getId());

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("comment")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("add-comment-form")));

        long commentsBefore = commentService.getAllComments().size();

        WebElement commentTextarea = driver.findElement(By.id("comment"));
        commentTextarea.clear();

        // formularul are required pe textarea, deci comentariul gol nu ar trebui trimis
        driver.findElement(By.cssSelector("#add-comment-form button[type='submit']")).click();

        // mica pauza ca sa permitem eventualului submit / JS sa ruleze
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long commentsAfter = commentService.getAllComments().size();

        assertEquals(commentsBefore, commentsAfter,
                "Submitting an empty comment should not create a new comment.");
        assertTrue(driver.getCurrentUrl().contains("/recipe/" + testRecipe.getId()),
                "User should remain on the same recipe page when trying to submit an empty comment.");
    }

    private void login(String username, String password) {
        driver.get(loginPageURL);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).sendKeys(username);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("password")));
        driver.findElement(By.id("password")).sendKeys(password);

        driver.findElement(By.id("submit-button")).click();

        wait.until(ExpectedConditions.urlContains("/home"));
    }

    private boolean isPresent(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException ex) {
            return false;
        }
    }
}