package com.vvss.FlavorFiesta.example_for_homework;

import com.vvss.FlavorFiesta.models.Recipe;
import com.vvss.FlavorFiesta.models.Review;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.repositories.ReviewRepository;
import com.vvss.FlavorFiesta.services.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModuleCUnitTests {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    // -
    // getAllReviews() - the method called by RankingService in the ranking flow
    //

    // hmultiple reviews from different users are returned as-is
    @Test
    void getAllReviews_shouldReturnAllReviewsFromRepository() {
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
        recipe.setTitle("Pasta");
        recipe.setIngredients("Flour, eggs");
        recipe.setInstructions("Cook");

        Review r1 = new Review();
        r1.setId(1L);
        r1.setOwner(anna);
        r1.setRecipe(recipe);
        r1.setComment("great");
        r1.setRating(9);
        r1.setAnonymous(false);

        Review r2 = new Review();
        r2.setId(2L);
        r2.setOwner(anna);
        r2.setRecipe(recipe);
        r2.setComment("nice");
        r2.setRating(8);
        r2.setAnonymous(false);

        Review r3 = new Review();
        r3.setId(3L);
        r3.setOwner(bob);
        r3.setRecipe(recipe);
        r3.setComment("ok");
        r3.setRating(7);
        r3.setAnonymous(false);

        when(reviewRepository.findAll()).thenReturn(List.of(r1, r2, r3));

        List<Review> actual = reviewService.getAllReviews();

        assertEquals(3, actual.size());
        // verify content is propagated without modification
        assertEquals("anna", actual.get(0).getOwner().getUsername());
        assertEquals(9,      actual.get(0).getRating());
        assertEquals("great", actual.get(0).getComment());
        assertEquals("anna", actual.get(1).getOwner().getUsername());
        assertEquals(8,      actual.get(1).getRating());
        assertEquals("bob",  actual.get(2).getOwner().getUsername());
        assertEquals(7,      actual.get(2).getRating());

        verify(reviewRepository, times(1)).findAll();
        verifyNoMoreInteractions(reviewRepository);
    }

    // edge case: repository has no reviews -> ranking flow receives empty input
    @Test
    void getAllReviews_shouldReturnEmptyList_whenNoReviewsExist() {
        when(reviewRepository.findAll()).thenReturn(Collections.emptyList());

        List<Review> actual = reviewService.getAllReviews();

        assertTrue(actual.isEmpty());
        verify(reviewRepository, times(1)).findAll();
        verifyNoMoreInteractions(reviewRepository);
    }

    //
    // addReview() - the write path that populates the data read by the flow
    //

    // review is saved and the persisted (ID-assigned) object is returned
    @Test
    void addReview_shouldSaveReviewAndReturnPersistedResult() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("anna");
        owner.setEmail("anna@test.com");
        owner.setPassword("secret");
        owner.setRole(User.ERole.USER);

        Recipe recipe = new Recipe();
        recipe.setId(10L);
        recipe.setOwner(owner);
        recipe.setTitle("Soup");
        recipe.setIngredients("Water, vegetables");
        recipe.setInstructions("Boil");

        Review input = new Review();
        input.setOwner(owner);
        input.setRecipe(recipe);
        input.setComment("delicious");
        input.setRating(9);
        input.setAnonymous(false);

        // repository assigns the ID when persisting
        Review persisted = new Review();
        persisted.setId(100L);
        persisted.setOwner(owner);
        persisted.setRecipe(recipe);
        persisted.setComment("delicious");
        persisted.setRating(9);
        persisted.setAnonymous(false);

        when(reviewRepository.save(input)).thenReturn(persisted);

        Review actual = reviewService.addReview(input);

        assertEquals(100L,        actual.getId());
        assertEquals("anna",      actual.getOwner().getUsername());
        assertEquals(9,           actual.getRating());
        assertEquals("delicious", actual.getComment());
        assertFalse(actual.isAnonymous());

        verify(reviewRepository, times(1)).save(input);
        verifyNoMoreInteractions(reviewRepository);
    }

    // edge case: anonymous review is saved correctly and flag is preserved
    @Test
    void addReview_shouldPreserveAnonymousFlag_whenReviewIsAnonymous() {
        User owner = new User();
        owner.setId(2L);
        owner.setUsername("bob");
        owner.setEmail("bob@test.com");
        owner.setPassword("secret");
        owner.setRole(User.ERole.USER);

        Recipe recipe = new Recipe();
        recipe.setId(20L);
        recipe.setOwner(owner);
        recipe.setTitle("Cake");
        recipe.setIngredients("Flour, sugar");
        recipe.setInstructions("Bake");

        Review input = new Review();
        input.setOwner(owner);
        input.setRecipe(recipe);
        input.setComment("not bad");
        input.setRating(6);
        input.setAnonymous(true);

        Review persisted = new Review();
        persisted.setId(200L);
        persisted.setOwner(owner);
        persisted.setRecipe(recipe);
        persisted.setComment("not bad");
        persisted.setRating(6);
        persisted.setAnonymous(true);

        when(reviewRepository.save(input)).thenReturn(persisted);

        Review actual = reviewService.addReview(input);

        assertEquals(200L, actual.getId());
        assertEquals(6,    actual.getRating());
        assertTrue(actual.isAnonymous());

        verify(reviewRepository, times(1)).save(input);
        verifyNoMoreInteractions(reviewRepository);
    }
}