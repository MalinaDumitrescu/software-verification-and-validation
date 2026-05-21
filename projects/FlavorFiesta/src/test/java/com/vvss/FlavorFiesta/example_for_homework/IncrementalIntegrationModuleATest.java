package com.vvss.FlavorFiesta.example_for_homework;

import com.vvss.FlavorFiesta.models.User;
import com.vvss.FlavorFiesta.services.RankingService;
import com.vvss.FlavorFiesta.services.ReviewService;
import com.vvss.FlavorFiesta.util.RankedItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
// Module structure for this test (Step 1 - A only):
// A = RankingController  (real, under test)
// B = RankingService     (mocked - isolates A from B)
// C = ReviewService      (mocked - isolates A from C, per lab Step-1 requirement)
// Flow: P → A → B → C
public class IncrementalIntegrationModuleATest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RankingService rankingService;

    @MockBean
    private ReviewService reviewService;

    // controller serialises and returns the service result
    @Test
    void rankingController_shouldCallRankingService_forTopReviewers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("anna");
        user.setEmail("anna@test.com");
        user.setPassword("secret");
        user.setRole(User.ERole.USER);

        when(rankingService.getUserRankingWithMostReviews())
                .thenReturn(List.of(new RankedItem<>(user, 1L)));

        mockMvc.perform(get("/api/rankings/top-reviewers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].entity.username").value("anna"))
                .andExpect(jsonPath("$[0].entity.email").value("anna@test.com"))
                .andExpect(jsonPath("$[0].rank").value(1));

        verify(rankingService, times(1)).getUserRankingWithMostReviews();
        verifyNoMoreInteractions(rankingService);
        verifyNoInteractions(reviewService);
    }

    // edge case: controller propagates empty list unchanged
    @Test
    void rankingController_shouldReturnEmptyList_whenRankingServiceReturnsNoResults() throws Exception {
        when(rankingService.getUserRankingWithMostReviews())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/rankings/top-reviewers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(rankingService, times(1)).getUserRankingWithMostReviews();
        verifyNoMoreInteractions(rankingService);
        verifyNoInteractions(reviewService);
    }
}