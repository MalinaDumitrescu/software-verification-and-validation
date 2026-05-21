package com.vvss.FlavorFiesta.example_for_homework;

import com.vvss.FlavorFiesta.models.Comment;
import com.vvss.FlavorFiesta.models.Recipe;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.services.CommentService;
import com.vvss.FlavorFiesta.services.RecipeService;
import com.vvss.FlavorFiesta.services.UserService;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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

// Viewing a recipe’s details
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Functionality2Test {

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
    void resetState() {
        driver.manage().deleteAllCookies();

        commentService.getAllComments().forEach(commentService::deleteComment);
        recipeService.getAllRecipes().forEach(recipeService::deleteRecipe);
        userService.getAllUsers().forEach(userService::deleteUser);

        testUser = userService.saveUser(
                new User("details_user", "details_user@test.com", testPassword)
        );

        testRecipe = recipeService.saveRecipe(
                new Recipe(testUser, "Detailed Recipe", "Eggs, Flour, Milk", "Mix ingredients and bake")
        );

        commentService.saveComment(
                new Comment(testRecipe, testUser, "Looks delicious!")
        );
    }

    @Test
    void testFunctionality2_happyPath() {
        login(testUser.getUsername(), testPassword);

        driver.get(baseURL + "/recipe/" + testRecipe.getId());

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Detailed Recipe"));

        assertTrue(driver.getPageSource().contains("Detailed Recipe"),
                "Recipe title should be visible on the details page.");
        assertTrue(driver.getPageSource().contains("Eggs, Flour, Milk"),
                "Recipe ingredients should be visible on the details page.");
        assertTrue(driver.getPageSource().contains("Mix ingredients and bake"),
                "Recipe instructions should be visible on the details page.");
        assertTrue(driver.getPageSource().contains("Looks delicious!"),
                "Existing comments should be visible on the recipe details page.");
    }

    @Test
    void testFunctionality2_negativePath() {
        login(testUser.getUsername(), testPassword);

        long invalidRecipeId = 999999L;
        driver.get(baseURL + "/recipe/" + invalidRecipeId);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        boolean isErrorPage = driver.getTitle().contains("Error")
                || driver.getPageSource().contains("Whitelabel Error Page")
                || driver.getPageSource().contains("This application has no explicit mapping")
                || driver.getPageSource().contains("Bad Request")
                || driver.getPageSource().contains("Internal Server Error")
                || driver.getPageSource().contains("Not Found");

        boolean validRecipeContentVisible =
                driver.getPageSource().contains("Detailed Recipe")
                        || driver.getPageSource().contains("Eggs, Flour, Milk")
                        || driver.getPageSource().contains("Mix ingredients and bake")
                        || driver.getPageSource().contains("Looks delicious!");

        assertTrue(isErrorPage || !validRecipeContentVisible,
                "Accessing a non-existent recipe should not display valid recipe details.");
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
}