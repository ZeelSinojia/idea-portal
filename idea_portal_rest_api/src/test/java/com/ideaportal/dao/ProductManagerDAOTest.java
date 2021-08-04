package com.ideaportal.dao;

import com.ideaportal.models.Artifacts;
import com.ideaportal.models.Ideas;
import com.ideaportal.models.Themes;
import com.ideaportal.models.User;
import com.ideaportal.repos.IdeasRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@SpringBootTest
class ProductManagerDAOTest {

    @Autowired
    ProductManagerDAO productManagerDAO;

    @MockBean
    IdeasRepository ideasRepository;

    @MockBean
    JdbcTemplate jdbcTemplate;

    @Test
    void createNewIdeaObject() {
        Themes themes = new Themes();
        Ideas ideas = new Ideas();
        User user = new User();
        List<Artifacts> list = new ArrayList<>();
        Mockito.when(ideasRepository.save(ideas)).thenReturn(new Ideas(1, "name",
                "description", new Date(), themes, user, list, null, null));
        assertEquals(1,productManagerDAO.createNewIdeaObject(ideas).getIdeaID());
    }

    @Test
    void getMyIdeasList() {

        User user = new User();
        user.setUserID(1);

        List<Ideas> list = new ArrayList<>();

        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(list);
        assertEquals(list, productManagerDAO.getMyIdeasList(user.getUserID()));
    }
}