package com.ideaportal.services;

import com.ideaportal.constants.IdeaPortalResponseConstants;
import com.ideaportal.dao.ProductManagerDAO;
import com.ideaportal.dao.UserDAO;
import com.ideaportal.models.Artifacts;
import com.ideaportal.models.Ideas;
import com.ideaportal.models.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProductManagerService.class})
class ProductManagerServiceTest {

    @Autowired
    ProductManagerService productManagerService;

    @MockBean
    ProductManagerDAO productManagerDAO;

    @MockBean
    UserDAO userDAO;

    @MockBean
    UserService userService;

    @Test
    void createNewIdeaResponseMessage() throws IOException, URISyntaxException {
        Ideas ideas = new Ideas();

        List<Artifacts> list = new ArrayList<>();

        Mockito.when(productManagerDAO.createNewIdeaObject(ideas)).thenReturn(ideas);

        assertEquals(IdeaPortalResponseConstants.IDEA_CREATE_SUCCESS,
                productManagerService.createNewIdeaResponseMessage(ideas).getStatusText());
    }

    @Test
    void getMySubmittedIdeasResponseMessage_ideaFound() {

        User user = new User();
        user.setUserID(1);

        Ideas ideas = new Ideas();

        List<Ideas> list = new ArrayList<>();
        list.add(ideas);

        Mockito.when(productManagerDAO.getMyIdeasList(user.getUserID())).thenReturn(list);

        assertEquals(IdeaPortalResponseConstants.LIST_ALL_IDEAS,
                productManagerService.getMySubmittedIdeasResponseMessage(user.getUserID()).getStatusText());
    }

    @Test
    void getMySubmittedIdeasResponseMessage_ideaNotFound() {

        User user = new User();
        user.setUserID(1);

        List<Ideas> list = new ArrayList<>();

        Mockito.when(productManagerDAO.getMyIdeasList(user.getUserID())).thenReturn(list);

        assertEquals(IdeaPortalResponseConstants.YOU_POSTED_NO_IDEAS,
                productManagerService.getMySubmittedIdeasResponseMessage(user.getUserID()).getStatusText());
    }
}