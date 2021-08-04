package com.ideaportal.services;


import com.ideaportal.constants.IdeaPortalResponseConstants;
import com.ideaportal.dao.ClientPartnerDAO;
import com.ideaportal.models.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ClientPartnerServices.class})
class ClientPartnerServicesTest {

    @Autowired
    ClientPartnerServices clientPartnerServices;

    @MockBean
    ClientPartnerDAO clientPartnerDAO;

    @Test
    void saveTheme() {

        Themes themes = new Themes();
        List<Artifacts> list = new ArrayList<>();

        Mockito.when(clientPartnerDAO.saveTheme(themes)).thenReturn(themes);
        assertEquals(IdeaPortalResponseConstants.THEME_CREATE_SUCCESS, clientPartnerServices.saveTheme(themes).getStatusText());
    }

//    @Test
//    void getParticipantsForIdeaResponseMessage_success(){
//
//        Ideas idea = new Ideas();
//        idea.setIdeaID(1);
//
//        User user = new User();
//
//        List<User> list = new ArrayList<>();
//        list.add(user);
//
//        Mockito.when(clientPartnerDAO.getParticipantsListForIdea(idea.getIdeaID())).thenReturn(list);
//
//        assertEquals(IdeaPortalResponseConstants.LIST_PARTICIPANTS,
//                clientPartnerServices.getParticipantsForIdeaResponseMessage(idea.getIdeaID()).getStatusText());
//    }

//    @Test
//    void getParticipantsForIdeaResponseMessage_noParticipantFound(){
//
//        Ideas idea = new Ideas();
//        idea.setIdeaID(1);
//
//        List<User> list = new ArrayList<>();
//
//        Mockito.when(clientPartnerDAO.getParticipantsListForIdea(idea.getIdeaID())).thenReturn(list);
//
//        assertEquals(IdeaPortalResponseConstants.NO_PARTICIPANTS,
//                clientPartnerServices.getParticipantsForIdeaResponseMessage(idea.getIdeaID()).getStatusText());
//    }

    @Test
    void getMyCreatedThemesResponseMessage_success(){

        User  user = new User();
        user.setUserID(1);

        Themes theme = new Themes();

        List<Themes> list = new ArrayList<>();
        list.add(theme);

        Mockito.when(clientPartnerDAO.getMyThemesList(user.getUserID())).thenReturn(list);

        assertEquals(IdeaPortalResponseConstants.LIST_ALL_THEMES,
                clientPartnerServices.getMyCreatedThemesResponseMessage(user.getUserID()).getStatusText());
    }

    @Test
    void getMyCreatedThemesResponseMessage_noThemeFound(){

        User  user = new User();
        user.setUserID(1);

        List<Themes> list = new ArrayList<>();

        Mockito.when(clientPartnerDAO.getMyThemesList(user.getUserID())).thenReturn(list);

        assertEquals(IdeaPortalResponseConstants.YOU_POSTED_NO_THEMES,
                clientPartnerServices.getMyCreatedThemesResponseMessage(user.getUserID()).getStatusText());
    }

}