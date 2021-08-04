package com.ideaportal.dao;

import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.models.Ideas;
import com.ideaportal.models.ParticipationResponses;
import com.ideaportal.models.Themes;
import com.ideaportal.models.User;
import com.ideaportal.repos.ParticipationRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class ParticipantDAOTest {

    @Autowired
    ParticipantDAO participantDAO;

    @MockBean
    DAOUtils daoUtils;

    @MockBean
    ParticipationRepository participationRepository;


    @Test
    void saveParticipantForIdea() {
        Ideas ideas = new Ideas();
        Themes themes = new Themes();
        User user = new User();

        ParticipationResponses participationResponses = new ParticipationResponses();
        participationResponses.setTheme(themes);
        participationResponses.setIdea(ideas);
        participationResponses.setUser(user);

        Mockito.when(participationRepository.save(participationResponses)).thenReturn(participationResponses);
        Mockito.when(daoUtils.buildParticipationResponseObject(participationResponses)).thenReturn(participationResponses);

        assertEquals(participationResponses, participantDAO.saveParticipantForIdea(participationResponses));

    }
}