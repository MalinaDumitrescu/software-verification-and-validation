package com.vvss.FlavorFiesta.example_for_homework;

import com.vvss.FlavorFiesta.models.Recipe;
import com.vvss.FlavorFiesta.models.Review;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.repositories.CommentRepository;
import com.vvss.FlavorFiesta.repositories.RecipeRepository;
import com.vvss.FlavorFiesta.repositories.ReviewRepository;
import com.vvss.FlavorFiesta.repositories.UserRepository;
import com.vvss.FlavorFiesta.services.ReviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
// Module structure for this test:
// A = RankingController  (real)
// B = RankingService     (real)
// C = ReviewService      (real, integrated - highest trust level)
// Flow: P → A → B → C
public class IncrementalIntegrationModuleCTest {

    @Autowired
    private MockMvc mockMvc;

    // ReviewService is the integration focus of this test (Module C — real)
    @Autowired
    private ReviewService reviewService;

    // Repositories used for user/recipe setup and teardown only
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    void cleanUp() {
        commentRepository.deleteAll();
        reviewRepository.deleteAll();
        recipeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void rankingController_rankingService_and_reviewService_shouldWorkTogether_withoutMocks() throws Exception {
        User anna = new User();
        anna.setUsername("anna");
        anna.setEmail("anna-inc-c@test.com");
        anna.setPassword("secret");
        anna.setRole(User.ERole.USER);
        anna = userRepository.save(anna);

        User bob = new User();
        bob.setUsername("bob");
        bob.setEmail("bob-inc-c@test.com");
        bob.setPassword("secret");
        bob.setRole(User.ERole.USER);
        bob = userRepository.save(bob);

        User recipeOwner = new User();
        recipeOwner.setUsername("chef");
        recipeOwner.setEmail("chef-inc-c@test.com");
        recipeOwner.setPassword("secret");
        recipeOwner.setRole(User.ERole.USER);
        recipeOwner = userRepository.save(recipeOwner);

        Recipe recipe = new Recipe();
        recipe.setOwner(recipeOwner);
        recipe.setTitle("Pizza");
        recipe.setIngredients("Dough, cheese");
        recipe.setInstructions("Bake");
        recipe = recipeRepository.save(recipe);

        // Add reviews through ReviewService (Module C real integration)
        Review r1 = new Review();
        r1.setOwner(anna);
        r1.setRecipe(recipe);
        r1.setComment("great");
        r1.setRating(9);
        r1.setAnonymous(false);
        reviewService.addReview(r1);

        Review r2 = new Review();
        r2.setOwner(anna);
        r2.setRecipe(recipe);
        r2.setComment("nice");
        r2.setRating(8);
        r2.setAnonymous(false);
        reviewService.addReview(r2);

        Review r3 = new Review();
        r3.setOwner(bob);
        r3.setRecipe(recipe);
        r3.setComment("ok");
        r3.setRating(7);
        r3.setAnonymous(false);
        reviewService.addReview(r3);

        mockMvc.perform(get("/api/rankings/top-reviewers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].entity.username").value("anna"))
                .andExpect(jsonPath("$[0].entity.email").value("anna-inc-c@test.com"))
                .andExpect(jsonPath("$[0].rank").value(2))
                .andExpect(jsonPath("$[1].entity.username").value("bob"))
                .andExpect(jsonPath("$[1].entity.email").value("bob-inc-c@test.com"))
                .andExpect(jsonPath("$[1].rank").value(1));
    }
}