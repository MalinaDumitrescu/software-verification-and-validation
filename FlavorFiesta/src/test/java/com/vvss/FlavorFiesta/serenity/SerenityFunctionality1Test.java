package com.vvss.FlavorFiesta.serenity;

import com.vvss.FlavorFiesta.models.Recipe;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.serenity.steps.LoginSteps;
import com.vvss.FlavorFiesta.serenity.steps.RecipeBrowsingSteps;
import com.vvss.FlavorFiesta.services.CommentService;
import com.vvss.FlavorFiesta.services.RecipeService;
import com.vvss.FlavorFiesta.services.UserService;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

/**
 * Serenity BDD tests for Functionality 1: Browsing the Recipe List.
 *
 * Running: ./mvnw verify -Dtest=SerenityFunctionality1Test
 * Reports: target/site/serenity/index.html
 */
@ExtendWith(SerenityJUnit5Extension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SerenityFunctionality1Test {

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

    // Serenity Step Libraries — each method call is recorded as a step in the HTML report
    @Steps
    LoginSteps loginSteps;

    @Steps
    RecipeBrowsingSteps browsingSteps;

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
    @Title("Browsing recipe list — happy path: two recipes appear in the list")
    void testBrowseRecipeList_happyPath() {
        recipeService.saveRecipe(new Recipe(testUser, "Test Pasta", "Pasta, Cheese", "Boil and mix"));
        recipeService.saveRecipe(new Recipe(testUser, "Test Soup", "Water, Vegetables", "Boil slowly"));

        loginSteps.navigateToLoginPage(driver, wait, loginPageURL);
        loginSteps.enterCredentials(driver, testUser.getUsername(), testPassword);
        loginSteps.submitLoginForm(driver, wait);
        loginSteps.verifyOnHomePage(driver);

        browsingSteps.navigateToHomePage(driver, wait, homePageURL);
        browsingSteps.verifyRecipeCount(driver, 2);
        browsingSteps.verifyRecipeVisible(driver, "Test Pasta");
        browsingSteps.verifyRecipeVisible(driver, "Test Soup");
    }

    @Test
    @Title("Browsing recipe list — negative path: empty list is handled gracefully")
    void testBrowseRecipeList_negativePath() {
        loginSteps.navigateToLoginPage(driver, wait, loginPageURL);
        loginSteps.enterCredentials(driver, testUser.getUsername(), testPassword);
        loginSteps.submitLoginForm(driver, wait);
        loginSteps.verifyOnHomePage(driver);

        browsingSteps.navigateToHomePage(driver, wait, homePageURL);
        browsingSteps.verifyRecipeCount(driver, 0);
        browsingSteps.verifyPageLoadsWhenEmpty(driver);
    }
}


