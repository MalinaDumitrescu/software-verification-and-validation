package com.vvss.FlavorFiesta.example_for_homework;

import com.vvss.FlavorFiesta.models.Recipe;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.services.CommentService;
import com.vvss.FlavorFiesta.services.RecipeService;
import com.vvss.FlavorFiesta.services.UserService;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Browsing the recipe list
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Functionality1Test {

    @LocalServerPort
    private int port;

    private String baseURL;
    private String loginPageURL;
    private String homePageURL;
    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    private UserService userService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CommentService commentService;

    private User testUser;
    private final String testPassword = "test123";

    @BeforeAll
    void setup() {
        baseURL = "http://localhost:" + port;
        loginPageURL = baseURL + "/login";
        homePageURL = baseURL + "/home";

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
                new User("browse_user", "browse_user@test.com", testPassword)
        );
    }

    @Test
    void testFunctionality1_happyPath() {
        recipeService.saveRecipe(new Recipe(testUser, "Test Pasta", "Pasta, Cheese", "Boil and mix"));
        recipeService.saveRecipe(new Recipe(testUser, "Test Soup", "Water, Vegetables", "Boil slowly"));

        login(testUser.getUsername(), testPassword);

        driver.get(homePageURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Recipes"));

        List<WebElement> recipeCards = driver.findElements(By.className("card"));

        assertEquals(2, recipeCards.size(), "The recipe list should contain the 2 test recipes.");
        assertTrue(driver.getPageSource().contains("Test Pasta"),
                "The first recipe title should be visible.");
        assertTrue(driver.getPageSource().contains("Test Soup"),
                "The second recipe title should be visible.");
    }

    @Test
    void testFunctionality1_negativePath() {
        // Edge case: authenticated user opens the recipe list when there are no recipes
        login(testUser.getUsername(), testPassword);

        driver.get(homePageURL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Recipes"));

        List<WebElement> recipeCards = driver.findElements(By.className("card"));

        assertEquals(0, recipeCards.size(),
                "When there are no recipes, the recipe list should be empty.");
        assertTrue(driver.getPageSource().contains("Recipes"),
                "The page should still load correctly even when no recipes exist.");
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