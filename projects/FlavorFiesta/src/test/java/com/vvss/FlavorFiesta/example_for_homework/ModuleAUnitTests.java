package com.vvss.FlavorFiesta.example_for_homework;

import com.vvss.FlavorFiesta.controllers.RankingController;
import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.services.RankingService;
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
public class ModuleAUnitTests {

    @Mock
    private RankingService rankingService;

    @InjectMocks
    private RankingController rankingController;

    //checked if controller returns the RankedItem list from RankingService unchanged
    @Test
    void getTopReviewers_shouldDelegateToRankingService() {
        User user = new User();
        user.setId(1L);
        user.setUsername("anna");
        user.setEmail("anna@test.com");
        user.setPassword("secret");
        user.setRole(User.ERole.USER);

        List<RankedItem<User>> expected = List.of(new RankedItem<>(user, 1L));

        when(rankingService.getUserRankingWithMostReviews()).thenReturn(expected);

        List<RankedItem<User>> actual = rankingController.getTopReviewers();

        assertEquals(1, actual.size());
        assertEquals("anna", actual.get(0).getEntity().getUsername());
        assertEquals("anna@test.com", actual.get(0).getEntity().getEmail());
        assertEquals(1L, actual.get(0).getRank());
        verify(rankingService, times(1)).getUserRankingWithMostReviews();
        verifyNoMoreInteractions(rankingService);
    }

    //edge case -> controller propagates an empty list without modification
    @Test
    void getTopReviewers_shouldReturnEmptyList_whenRankingServiceReturnsNoResults() {
        when(rankingService.getUserRankingWithMostReviews()).thenReturn(Collections.emptyList());

        List<RankedItem<User>> actual = rankingController.getTopReviewers();

        assertTrue(actual.isEmpty());
        verify(rankingService, times(1)).getUserRankingWithMostReviews();
        verifyNoMoreInteractions(rankingService);
    }
}