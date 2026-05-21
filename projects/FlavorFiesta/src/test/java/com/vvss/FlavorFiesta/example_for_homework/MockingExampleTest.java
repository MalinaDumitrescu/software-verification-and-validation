package com.vvss.FlavorFiesta.example_for_homework;

import com.vvss.FlavorFiesta.models.Recipe;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.services.CommentService;
import com.vvss.FlavorFiesta.services.RecipeService;
import com.vvss.FlavorFiesta.services.UserService;
import com.vvss.FlavorFiesta.test_utils.TestControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * This example shows how to use @MockBean in a Spring Boot integration test.
 *
 * @MockBean replaces a real Spring bean with a Mockito mock for the duration of the test.
 * This is useful for Top-Down integration testing: you keep some modules real and mock the
 * ones that haven't been "integrated" yet.
 *
 * In this example:
 *   - UserService and RecipeService are REAL (autowired from the Spring context)
 *   - CommentService is MOCKED (replaced by @MockBean)
 *
 * This simulates a Top-Down scenario where the Comment module has not yet been integrated.
 */
public class MockingExampleTest extends TestControllerIntegrationTest {

    // Real services -- these are the modules we have "integrated"
    @Autowired
    private UserService userService;

    @Autowired
    private RecipeService recipeService;

    // Mocked service -- this module is NOT yet integrated, so we replace it with a mock
    @MockBean
    private CommentService commentService;

    @Test
    public void testWithMockedCommentService() {
        // The CommentService is mocked. We can define what it returns:
        when(commentService.getAllComments()).thenReturn(Collections.emptyList());
        when(commentService.saveComment(any())).thenReturn(null);

        // Now we can test UserService and RecipeService (real) while CommentService
        // always returns the stubbed values above.

        // Create a real user
        User user = new User("mockTestUser", "mocktest@example.com", "password");
        userService.saveUser(user);

        // Create a real recipe
        Recipe recipe = new Recipe(user, "Test Recipe", "ingredients", "instructions");
        recipeService.saveRecipe(recipe);

        // Verify the real modules work
        assertNotNull(recipeService.getRecipeById(recipe.getId()));
        assertEquals("Test Recipe", recipeService.getRecipeById(recipe.getId()).getTitle());

        // The mocked module returns our stubbed values
        assertEquals(0, commentService.getAllComments().size());

        // Clean up
        recipeService.deleteRecipe(recipe);
        userService.deleteUser(user);
    }
}
