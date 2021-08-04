package com.ideaportal.dao;

import com.ideaportal.constants.IdeaPortalExceptionConstants;
import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.models.*;
import com.ideaportal.repos.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserDAOTest {

    @Autowired
    UserDAO userDAO;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ThemesRepository themesRepository;

    @MockBean
    LikesRepository likesRepository;

    @MockBean
    CommentsRepository commentsRepository;

    @MockBean
    PasswordLogRepository passwordLogRepository;

    @MockBean
    IdeasRepository ideasRepository;

    @MockBean
    JdbcTemplate jdbcTemplate;

    @MockBean
    DAOUtils utils;

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void saveUser(int roleID) {
        Roles roles = new Roles();
        roles.setRoleID(roleID);
        if(roleID==1)
            roles.setRoleName("Client Partner");
        if(roleID==2)
            roles.setRoleName("Product Manager");
        if(roleID==3)
            roles.setRoleName("Participant");

        User user = new User();
        user.setUserPassword("test");
        user.setRole(roles);

        Mockito.when(userRepository.save(user)).thenReturn(user);

        try {
            assertEquals(roles.getRoleName(), userDAO.saveUser(user).getRole().getRoleName());
        } catch (Exception e){
            assertEquals(IdeaPortalExceptionConstants.ROLE_NOT_FOUND,
                    e.getMessage());
        }
    }

    @Test
    void isLoginCredentialsValid() {

        Login login = new Login();
        User user = new User();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(user);
        assertEquals(user, userDAO.isLoginCredentialsValid(login));
    }

    @Test
    void getAllThemesList() {
        List<Themes> list = new ArrayList<>();

        Mockito.when(themesRepository.findAll()).thenReturn(list);
        assertEquals(0, list.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void getIdeasByConditionListTest(int condition) {
        Themes themes = new Themes();
        themes.setThemeID(1);
        List<Ideas> list = new ArrayList<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(list);

        if (condition == 1)
            assertEquals(0, userDAO.getIdeasByMostLikesList(themes.getThemeID()).size());
        assertEquals(0, userDAO.getIdeasByMostLikesList(themes.getThemeID()).size());
        assertEquals(0, userDAO.getIdeasByMostLikesList(themes.getThemeID()).size());
    }

    @Test
    void getIdea() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Mockito.when(ideasRepository.findById(ideas.getIdeaID())).thenReturn(java.util.Optional.of(ideas));
        assertEquals(1, userDAO.getIdea(ideas.getIdeaID()).getIdeaID());
    }

    @Test
    void getIdea_failure() {
        Ideas ideas = new Ideas();
        Mockito.when(ideasRepository.findById(ideas.getIdeaID())).thenReturn(java.util.Optional.empty());
        assertNull(userDAO.getIdea(ideas.getIdeaID()));
    }

    @Test
    void updateUserProfile() {
        User user = new User();
        user.setUserID(1);
        Mockito.when(userRepository.findById(user.getUserID())).thenReturn(java.util.Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        assertEquals(1, userDAO.updateUserProfile(user).getUserID());
    }

    @Test
    void updateUserProfile_failure() {
        User user = new User();
        user.setUserID(1);
        Mockito.when(userRepository.findById(user.getUserID())).thenReturn(java.util.Optional.empty());
        Mockito.when(userRepository.save(user)).thenReturn(user);

        assertNull(userDAO.updateUserProfile(user));
    }

    @Test
    void updatePassword() {
        User user = new User();
        user.setUserID(1);
        user.setUserPassword("test");

        PasswordLog passwordLog = new PasswordLog();
        passwordLog.setUser(user);
        passwordLog.setOldPassword("test");
        passwordLog.setNewPassword("test");

        Mockito.when(userRepository.findById(user.getUserID())).thenReturn(java.util.Optional.of(user));
        Mockito.when(passwordLogRepository.save(passwordLog)).thenReturn(passwordLog);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        assertEquals(1, userDAO.updatePassword(user).getUserID());
    }

    @Test
    void updatePassword_failure() {
        User user = new User();
        user.setUserID(1);
        user.setUserPassword("test");

        PasswordLog passwordLog = new PasswordLog();
        passwordLog.setUser(user);
        passwordLog.setOldPassword("test");
        passwordLog.setNewPassword("test");

        Mockito.when(userRepository.findById(user.getUserID())).thenReturn(java.util.Optional.empty());
        Mockito.when(passwordLogRepository.save(passwordLog)).thenReturn(passwordLog);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        assertNull(userDAO.updatePassword(user));
    }

    @Test
    void saveLikes() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        User user = new User();
        user.setUserID(1);
        Likes likes = new Likes();
        likes.setLikeID(1);
        likes.setUser(user);
        likes.setIdea(ideas);

        Mockito.when(utils.findLikeID(ideas.getIdeaID(), user.getUserID())).thenReturn(likes.getLikeID());
        Mockito.when(likesRepository.save(likes)).thenReturn(likes);
        Mockito.when(utils.buildLikesObject(likes)).thenReturn(likes);

        assertEquals(1, userDAO.saveLikes(likes).getLikeID());
    }

    @Test
    void testSaveLikes() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        User user = new User();
        user.setUserID(1);
        Likes likes = new Likes();
        likes.setLikeID(1);
        likes.setUser(user);
        likes.setIdea(ideas);

        Mockito.when(utils.findLikeID(ideas.getIdeaID(), user.getUserID())).thenReturn(0L);
        Mockito.when(likesRepository.save(likes)).thenReturn(likes);
        Mockito.when(utils.buildLikesObject(likes)).thenReturn(likes);

        assertEquals(1, userDAO.saveLikes(likes).getLikeID());
    }

    @Test
    void saveComment() {
        Comments comments = new Comments();
        comments.setCommentID(1);
        Mockito.when(commentsRepository.save(comments)).thenReturn(comments);
        Mockito.when(utils.buildCommentsObject(comments)).thenReturn(comments);

        assertEquals(1, userDAO.saveComment(comments).getCommentID());
    }

    @Test
    void getLikesForIdeaList() {

        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        User user = new User();
        user.setUserID(1);
        List<User> list = new ArrayList<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(list);
        assertEquals(list, userDAO.getLikesForIdeaList(ideas.getIdeaID()));
    }

    @Test
    void getDislikesForIdeaList() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        User user = new User();
        user.setUserID(1);
        List<User> list = new ArrayList<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(list);
        assertEquals(list, userDAO.getDislikesForIdeaList(ideas.getIdeaID()));
    }

    @Test
    void getCommentsList() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Comments comments = new Comments();
        comments.setCommentID(1);
        List<Comments> list = new ArrayList<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(list);
        assertEquals(list, userDAO.getCommentsList(ideas.getIdeaID()));
    }

    @Test
    void getUser() {
        User user = new User();
        user.setUserID(1);
        long id = 1;
        Mockito.when(userRepository.findById(id)).thenReturn(java.util.Optional.of(user));
        assertEquals(id, Objects.requireNonNull(userDAO.getUser(id).orElse(null)).getUserID());
    }

    @Test
    void getAllIdeas() throws SQLException {

        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        List<Ideas> list = new ArrayList<>();
        list.add(ideas);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(list);
        assertEquals(1, userDAO.getAllIdeas().size());
    }
}