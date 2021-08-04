package com.ideaportal.dao;


import com.ideaportal.models.Artifacts;
import com.ideaportal.models.Ideas;
import com.ideaportal.models.Themes;
import com.ideaportal.models.User;
import com.ideaportal.repos.ThemesRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@SpringBootTest
class ClientPartnerDAOTest {

    @Autowired
    ClientPartnerDAO clientPartnerDAO;

    @MockBean
    ThemesRepository themesRepository;

    @MockBean
    JdbcTemplate jdbcTemplate;

    @Test
    void save() {
        Themes themes = new Themes();
        User user = new User();
        List<Artifacts> list = new ArrayList<>();
        Mockito.when(themesRepository.save(themes)).thenReturn(new Themes(1L, "name",
                "description", new Date(), user, list, null, null, null, null));
        assertEquals(1,clientPartnerDAO.saveTheme(themes).getThemeID());
    }

//    @Test
//    void getParticipantsListForIdea() {
//        Ideas ideas = new Ideas();
//        ideas.setIdeaID(1);
//        User user = new User();
//        user.setUserID(1);
//        List<User> list = new ArrayList<>();
//        list.add(user);
//
//        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(list);
//        assertEquals(list, clientPartnerDAO.getParticipantsListForIdea(ideas.getIdeaID()));
//    }

    @Test
    void getMyThemesList() {
        User user = new User();
        user.setUserID(1);

        Themes themes = new Themes();
        themes.setThemeName("theme");

        List<Themes> list = new ArrayList<>();
        list.add(themes);

        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(list);
        assertEquals(list, clientPartnerDAO.getMyThemesList(user.getUserID()));
    }
}