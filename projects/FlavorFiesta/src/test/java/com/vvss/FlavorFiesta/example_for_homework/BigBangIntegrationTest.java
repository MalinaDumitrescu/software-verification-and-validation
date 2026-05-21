package com.vvss.FlavorFiesta.example_for_homework;

import com.vvss.FlavorFiesta.models.Recipe;
import com.vvss.FlavorFiesta.models.Review;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.repositories.CommentRepository;
import com.vvss.FlavorFiesta.repositories.RecipeRepository;
import com.vvss.FlavorFiesta.repositories.ReviewRepository;
import com.vvss.FlavorFiesta.repositories.UserRepository;
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
// A = RankingController
// B = RankingService
// C = ReviewService
// Flow: P → A → B → C
// Setup uses repositories directly so that UserService / RecipeService
// are not part of the tested execution path.
public class BigBangIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // repositories: used for test data setup AND teardown
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
    void topReviewers_shouldIntegrateRankingControllerRankingServiceAndReviewService_withoutMocks() throws Exception {

        // Step 1: seed prerequisite users directly via repository
        User anna = new User();
        anna.setUsername("anna");
        anna.setEmail("anna-bigbang@test.com");
        anna.setPassword("secret");
        anna.setRole(User.ERole.USER);
        anna = userRepository.save(anna);

        User bob = new User();
        bob.setUsername("bob");
        bob.setEmail("bob-bigbang@test.com");
        bob.setPassword("secret");
        bob.setRole(User.ERole.USER);
        bob = userRepository.save(bob);

        User carla = new User();
        carla.setUsername("carla");
        carla.setEmail("carla-bigbang@test.com");
        carla.setPassword("secret");
        carla.setRole(User.ERole.USER);
        carla = userRepository.save(carla);

        // spectator: exists in DB but posts no reviews - must be absent from ranking
        User spectator = new User();
        spectator.setUsername("spectator");
        spectator.setEmail("spectator-bigbang@test.com");
        spectator.setPassword("secret");
        spectator.setRole(User.ERole.USER);
        userRepository.save(spectator);

        User chef = new User();
        chef.setUsername("chef");
        chef.setEmail("chef-bigbang@test.com");
        chef.setPassword("secret");
        chef.setRole(User.ERole.USER);
        chef = userRepository.save(chef);

        //Step 2: seed prerequisite recipe directly via repository
        Recipe recipe = new Recipe();
        recipe.setOwner(chef);
        recipe.setTitle("Pasta");
        recipe.setIngredients("Flour, eggs");
        recipe.setInstructions("Cook");
        recipe = recipeRepository.save(recipe);

        // Step 3 (phase A): anna(2) + bob(2) reviews directly via repository
        Review r1 = new Review();
        r1.setOwner(anna); r1.setRecipe(recipe);
        r1.setComment("great"); r1.setRating(9); r1.setAnonymous(false);
        reviewRepository.save(r1);

        Review r2 = new Review();
        r2.setOwner(anna); r2.setRecipe(recipe);
        r2.setComment("nice"); r2.setRating(8); r2.setAnonymous(false);
        reviewRepository.save(r2);

        Review r3 = new Review();
        r3.setOwner(bob); r3.setRecipe(recipe);
        r3.setComment("ok"); r3.setRating(7); r3.setAnonymous(false);
        reviewRepository.save(r3);

        Review r4 = new Review();
        r4.setOwner(bob); r4.setRecipe(recipe);
        r4.setComment("solid"); r4.setRating(8); r4.setAnonymous(false);
        reviewRepository.save(r4);

        // Step 4: intermediate ranking call - anna and bob both at 2 reviews (tied)
        // Ordering is non-deterministic for equal counts; verify only size and rank values.
        mockMvc.perform(get("/api/rankings/top-reviewers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].rank").value(2))
                .andExpect(jsonPath("$[1].rank").value(2))
                .andExpect(jsonPath("$[0].entity.username").exists())
                .andExpect(jsonPath("$[1].entity.username").exists());

        // Step 5 (phase B): add carla(1) and anna's third review
        Review r5 = new Review();
        r5.setOwner(carla); r5.setRecipe(recipe);
        r5.setComment("good enough"); r5.setRating(6); r5.setAnonymous(false);
        reviewRepository.save(r5);

        Review r6 = new Review();
        r6.setOwner(anna); r6.setRecipe(recipe);
        r6.setComment("would cook again"); r6.setRating(10); r6.setAnonymous(false);
        reviewRepository.save(r6);

        // Step 6: final ranking — anna(3), bob(2), carla(1); spectator absent
        mockMvc.perform(get("/api/rankings/top-reviewers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].entity.username").value("anna"))
                .andExpect(jsonPath("$[0].entity.email").value("anna-bigbang@test.com"))
                .andExpect(jsonPath("$[0].rank").value(3))
                .andExpect(jsonPath("$[1].entity.username").value("bob"))
                .andExpect(jsonPath("$[1].entity.email").value("bob-bigbang@test.com"))
                .andExpect(jsonPath("$[1].rank").value(2))
                .andExpect(jsonPath("$[2].entity.username").value("carla"))
                .andExpect(jsonPath("$[2].entity.email").value("carla-bigbang@test.com"))
                .andExpect(jsonPath("$[2].rank").value(1));
    }
}