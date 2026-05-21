package com.vvss.FlavorFiesta.example_for_homework;

import com.vvss.FlavorFiesta.models.Recipe;
import com.vvss.FlavorFiesta.models.Review;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.services.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
// Module structure for this test (Step 2 — A + B):
// A = RankingController  (real)
// B = RankingService     (real, under test alongside A)
// C = ReviewService      (mocked — isolates A+B from C, per lab Step-2 requirement)
// Note: CommentService is a field of RankingService but is NOT called by
//       getUserRankingWithMostReviews(); the real bean is available in the
//       Spring context and is correctly left unmocked.
// Flow: P → A → B → C
public class IncrementalIntegrationModuleBTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Test
    void rankingController_and_rankingService_shouldIntegrate_usingMockedReviewService() throws Exception {
        User anna = new User();
        anna.setId(1L);
        anna.setUsername("anna");
        anna.setEmail("anna@test.com");
        anna.setPassword("secret");
        anna.setRole(User.ERole.USER);

        User bob = new User();
        bob.setId(2L);
        bob.setUsername("bob");
        bob.setEmail("bob@test.com");
        bob.setPassword("secret");
        bob.setRole(User.ERole.USER);

        User recipeOwner = new User();
        recipeOwner.setId(3L);
        recipeOwner.setUsername("chef");
        recipeOwner.setEmail("chef@test.com");
        recipeOwner.setPassword("secret");
        recipeOwner.setRole(User.ERole.USER);

        Recipe recipe = new Recipe();
        recipe.setId(10L);
        recipe.setOwner(recipeOwner);
        recipe.setTitle("Cake");
        recipe.setIngredients("Flour, sugar");
        recipe.setInstructions("Bake");

        Review r1 = new Review();
        r1.setId(100L);
        r1.setOwner(anna);
        r1.setRecipe(recipe);
        r1.setComment("great");
        r1.setRating(9);
        r1.setAnonymous(false);

        Review r2 = new Review();
        r2.setId(101L);
        r2.setOwner(anna);
        r2.setRecipe(recipe);
        r2.setComment("nice");
        r2.setRating(8);
        r2.setAnonymous(false);

        Review r3 = new Review();
        r3.setId(102L);
        r3.setOwner(bob);
        r3.setRecipe(recipe);
        r3.setComment("ok");
        r3.setRating(7);
        r3.setAnonymous(false);

        when(reviewService.getAllReviews()).thenReturn(List.of(r1, r2, r3));

        mockMvc.perform(get("/api/rankings/top-reviewers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].entity.username").value("anna"))
                .andExpect(jsonPath("$[0].entity.email").value("anna@test.com"))
                .andExpect(jsonPath("$[0].rank").value(2))
                .andExpect(jsonPath("$[1].entity.username").value("bob"))
                .andExpect(jsonPath("$[1].entity.email").value("bob@test.com"))
                .andExpect(jsonPath("$[1].rank").value(1));

        verify(reviewService, times(1)).getAllReviews();
        verifyNoMoreInteractions(reviewService);
    }
}