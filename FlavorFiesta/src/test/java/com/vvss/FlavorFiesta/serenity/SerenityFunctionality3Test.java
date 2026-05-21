package com.vvss.FlavorFiesta.serenity;

import com.vvss.FlavorFiesta.models.Recipe;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.serenity.steps.CommentSteps;
import com.vvss.FlavorFiesta.serenity.steps.LoginSteps;
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
 * Serenity BDD tests for Functionality 3: Posting a Comment on a Recipe.
 *
 * Running: ./mvnw verify -Dtest=SerenityFunctionality3Test
 * Reports: target/site/serenity/index.html
 */
@ExtendWith(SerenityJUnit5Extension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SerenityFunctionality3Test {

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
    CommentSteps commentSteps;

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
    @Title("Posting a comment — happy path: comment appears on the page after submission")
    void testPostComment_happyPath() {
        String uniqueComment = "Amazing recipe! " + System.currentTimeMillis();

        loginSteps.navigateToLoginPage(driver, wait, loginPageURL);
        loginSteps.enterCredentials(driver, testUser.getUsername(), testPassword);
        loginSteps.submitLoginForm(driver, wait);
        loginSteps.verifyOnHomePage(driver);

        commentSteps.openRecipeCommentForm(driver, wait, baseURL, testRecipe.getId());
        commentSteps.typeComment(driver, uniqueComment);
        commentSteps.submitCommentForm(driver, wait);
        commentSteps.verifyCommentAppeared(driver, wait, uniqueComment);
    }

    @Test
    @Title("Posting a comment — negative path: empty comment is rejected, count stays the same")
    void testPostComment_negativePath() {
        loginSteps.navigateToLoginPage(driver, wait, loginPageURL);
        loginSteps.enterCredentials(driver, testUser.getUsername(), testPassword);
        loginSteps.submitLoginForm(driver, wait);
        loginSteps.verifyOnHomePage(driver);

        commentSteps.openRecipeCommentForm(driver, wait, baseURL, testRecipe.getId());

        long commentsBefore = commentService.getAllComments().size();

        commentSteps.submitEmptyCommentForm(driver);

        long commentsAfter = commentService.getAllComments().size();

        commentSteps.verifyCommentCountUnchanged(commentsBefore, commentsAfter);
        commentSteps.verifyStillOnRecipePage(driver, testRecipe.getId());
    }
}


