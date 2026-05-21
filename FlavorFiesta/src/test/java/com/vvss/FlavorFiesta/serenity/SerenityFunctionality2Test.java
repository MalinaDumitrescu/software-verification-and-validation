package com.vvss.FlavorFiesta.serenity;

import com.vvss.FlavorFiesta.models.Comment;
import com.vvss.FlavorFiesta.models.Recipe;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.serenity.steps.LoginSteps;
import com.vvss.FlavorFiesta.serenity.steps.RecipeDetailSteps;
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
 * Serenity BDD tests for Functionality 2: Viewing a Recipe's Details.
 *
 * Running: ./mvnw verify -Dtest=SerenityFunctionality2Test
 * Reports: target/site/serenity/index.html
 */
@ExtendWith(SerenityJUnit5Extension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SerenityFunctionality2Test {

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

    // Serenity Step Libraries — each method call is recorded as a step in the HTML report
    @Steps
    LoginSteps loginSteps;

    @Steps
    RecipeDetailSteps recipeDetailSteps;

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
    @Title("Viewing recipe details — happy path: title, ingredients, instructions and comments are shown")
    void testViewRecipeDetails_happyPath() {
        loginSteps.navigateToLoginPage(driver, wait, loginPageURL);
        loginSteps.enterCredentials(driver, testUser.getUsername(), testPassword);
        loginSteps.submitLoginForm(driver, wait);
        loginSteps.verifyOnHomePage(driver);

        recipeDetailSteps.navigateToRecipePage(driver, wait, baseURL, testRecipe.getId());
        recipeDetailSteps.verifyRecipeTitle(driver, wait, "Detailed Recipe");
        recipeDetailSteps.verifyIngredients(driver, "Eggs, Flour, Milk");
        recipeDetailSteps.verifyInstructions(driver, "Mix ingredients and bake");
        recipeDetailSteps.verifyCommentVisible(driver, "Looks delicious!");
    }

    @Test
    @Title("Viewing recipe details — negative path: non-existent recipe shows an error, not recipe data")
    void testViewRecipeDetails_negativePath() {
        loginSteps.navigateToLoginPage(driver, wait, loginPageURL);
        loginSteps.enterCredentials(driver, testUser.getUsername(), testPassword);
        loginSteps.submitLoginForm(driver, wait);
        loginSteps.verifyOnHomePage(driver);

        recipeDetailSteps.navigateToRecipePage(driver, wait, baseURL, 999999L);
        recipeDetailSteps.verifyNonExistentRecipeHandled(driver);
    }
}


