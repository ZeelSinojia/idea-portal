package com.ideaportal.services;


import com.ideaportal.constants.IdeaPortalResponseConstants;
import com.ideaportal.dao.ParticipantDAO;
import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.models.ParticipationResponses;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ParticipantService.class})
class ParticipantServiceTest {

    @Autowired
    ParticipantService participantService;

    @MockBean
    ParticipantDAO participantDAO;

    @MockBean
    DAOUtils daoUtils;

    @Test
    void registerForIdea() {
        ParticipationResponses participationResponses = new ParticipationResponses();

        Mockito.when(participantDAO.saveParticipantForIdea(participationResponses)).thenReturn(participationResponses);

        assertEquals(IdeaPortalResponseConstants.PARTICIPATION_SUCCESS,
                participantService.registerForIdea(participationResponses).getStatusText());
    }

    @Test
    void alreadyParticipatedInAnIdeaResponse_trueCondition() {

        ParticipationResponses participationResponses = new ParticipationResponses();

        Mockito.when(daoUtils.isAlreadyParticipated(participationResponses)).thenReturn(true);

        assertTrue(participantService.alreadyParticipatedInAnIdeaResponse(participationResponses));
    }

    @Test
    void alreadyParticipatedInAnIdeaResponse_falseCondition() {

        ParticipationResponses participationResponses = new ParticipationResponses();

        Mockito.when(daoUtils.isAlreadyParticipated(participationResponses)).thenReturn(false);

        assertFalse(participantService.alreadyParticipatedInAnIdeaResponse(participationResponses));
    }

    @Test
    void isIdeaInTheme_trueCondition() {
        ParticipationResponses participationResponses = new ParticipationResponses();

        Mockito.when(daoUtils.isIdeaInTheme(participationResponses)).thenReturn(true);

        assertTrue(participantService.isIdeaInTheme(participationResponses));
    }

    @Test
    void isIdeaInTheme_falseCondition() {
        ParticipationResponses participationResponses = new ParticipationResponses();

        Mockito.when(daoUtils.isIdeaInTheme(participationResponses)).thenReturn(false);

        assertFalse(participantService.isIdeaInTheme(participationResponses));
    }
}