package com.vvss.FlavorFiesta.example_for_homework;

import com.vvss.FlavorFiesta.models.Recipe;
import com.vvss.FlavorFiesta.models.Review;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.services.CommentService;
import com.vvss.FlavorFiesta.services.RankingService;
import com.vvss.FlavorFiesta.services.ReviewService;
import com.vvss.FlavorFiesta.util.RankedItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModuleBUnitTests {

    @Mock
    private ReviewService reviewService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private RankingService rankingService;

    // if users ordered by review count descending, with correct rank values (equal counts -> same rank)
    @Test
    void getUserRankingWithMostReviews_shouldReturnUsersOrderedByReviewCount() {
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
        recipe.setTitle("Pasta");
        recipe.setIngredients("Flour, eggs");
        recipe.setInstructions("Cook");

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

        List<RankedItem<User>> result = rankingService.getUserRankingWithMostReviews();

        assertEquals(2, result.size());
        assertEquals("anna", result.get(0).getEntity().getUsername());
        assertEquals(2L, result.get(0).getRank());
        assertEquals("bob", result.get(1).getEntity().getUsername());
        assertEquals(1L, result.get(1).getRank());

        verify(reviewService, times(1)).getAllReviews();
        verifyNoInteractions(commentService);
    }

    // edge case: no reviews -> empty ranking
    @Test
    void getUserRankingWithMostReviews_shouldReturnEmptyList_whenNoReviewsExist() {
        when(reviewService.getAllReviews()).thenReturn(Collections.emptyList());

        List<RankedItem<User>> result = rankingService.getUserRankingWithMostReviews();

        assertTrue(result.isEmpty());
        verify(reviewService, times(1)).getAllReviews();
        verifyNoInteractions(commentService);
    }

    // edge case: two users with equal review counts - both present, both with same rank value ---
    @Test
    void getUserRankingWithMostReviews_shouldIncludeBothUsers_whenTied() {
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

        Recipe recipe = new Recipe();
        recipe.setId(10L);
        recipe.setOwner(anna);
        recipe.setTitle("Soup");
        recipe.setIngredients("Water");
        recipe.setInstructions("Boil");

        Review r1 = new Review(); r1.setId(1L); r1.setOwner(anna); r1.setRecipe(recipe); r1.setComment("a1"); r1.setRating(8); r1.setAnonymous(false);
        Review r2 = new Review(); r2.setId(2L); r2.setOwner(anna); r2.setRecipe(recipe); r2.setComment("a2"); r2.setRating(7); r2.setAnonymous(false);
        Review r3 = new Review(); r3.setId(3L); r3.setOwner(bob);  r3.setRecipe(recipe); r3.setComment("b1"); r3.setRating(9); r3.setAnonymous(false);
        Review r4 = new Review(); r4.setId(4L); r4.setOwner(bob);  r4.setRecipe(recipe); r4.setComment("b2"); r4.setRating(6); r4.setAnonymous(false);

        when(reviewService.getAllReviews()).thenReturn(List.of(r1, r2, r3, r4));

        List<RankedItem<User>> result = rankingService.getUserRankingWithMostReviews();

        // Both users should appear and both should carry rank = 2 (their review count)
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(item -> item.getRank() == 2L));

        List<String> usernames = result.stream()
                .map(item -> item.getEntity().getUsername())
                .toList();
        assertTrue(usernames.contains("anna"));
        assertTrue(usernames.contains("bob"));

        verify(reviewService, times(1)).getAllReviews();
        verifyNoInteractions(commentService);
    }
}