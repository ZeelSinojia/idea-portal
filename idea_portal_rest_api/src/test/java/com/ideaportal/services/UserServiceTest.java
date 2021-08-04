package com.ideaportal.services;

import com.ideaportal.constants.IdeaPortalExceptionConstants;
import com.ideaportal.constants.IdeaPortalResponseConstants;
import com.ideaportal.dao.UserDAO;
import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.exceptions.IdeaNotCommentedException;
import com.ideaportal.exceptions.IdeaNotLikedException;
import com.ideaportal.exceptions.MailServerException;
import com.ideaportal.models.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserService.class})
class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    DAOUtils daoUtils;

    @MockBean
    UserDAO userDAO;

    @MockBean
    JavaMailSender javaMailSender;

    @Test
    void addUser_success() {

        User user = new User();
        user.setUserEmail("test@test.com");
        user.setUserName("testname");

        Mockito.when(daoUtils.getCountByEmail(user.getUserEmail())).thenReturn(0);
        Mockito.when(daoUtils.getCountByUserName(user.getUserName())).thenReturn(0);
        Mockito.when(userDAO.saveUser(user)).thenReturn(user);

        assertEquals(IdeaPortalResponseConstants.SIGN_UP_SUCCESSFUL,
                userService.addUser(user).getStatusText());
    }

    @Test
    void addUser_usernameInUse() {

        User user = new User();
        user.setUserEmail("test@test.com");
        user.setUserName("testname");

        Mockito.when(daoUtils.getCountByEmail(user.getUserEmail())).thenReturn(0);
        Mockito.when(daoUtils.getCountByUserName(user.getUserName())).thenReturn(1);
        Mockito.when(userDAO.saveUser(user)).thenReturn(user);
        try {
            assertEquals(IdeaPortalExceptionConstants.USERNAME_IN_USE,
                    userService.addUser(user).getStatusText());
        } catch (Exception e) {
            assertEquals(IdeaPortalExceptionConstants.USERNAME_IN_USE, e.getMessage());
        }
    }

    @Test
    void addUser_emailInUse() {

        User user = new User();
        user.setUserEmail("test@test.com");
        user.setUserName("testname");

        Mockito.when(daoUtils.getCountByEmail(user.getUserEmail())).thenReturn(1);
        Mockito.when(daoUtils.getCountByUserName(user.getUserName())).thenReturn(0);
        Mockito.when(userDAO.saveUser(user)).thenReturn(user);
        try {
            assertEquals(IdeaPortalExceptionConstants.EMAIL_IN_USE,
                    userService.addUser(user).getStatusText());
        } catch (Exception e) {
            assertEquals(IdeaPortalExceptionConstants.EMAIL_IN_USE, e.getMessage());
        }
    }

    @Test
    void checkCredentials_success() {
        Login userDetails = new Login();

        User user = new User();

        Mockito.when(userDAO.isLoginCredentialsValid(userDetails)).thenReturn(user);

        assertEquals(IdeaPortalResponseConstants.LOGIN_SUCCESSFUL,
                userService.checkCredentials(userDetails).getStatusText());
    }

    @Test
    void checkCredentials_invalidCredentials() {
        Login userDetails = new Login();

        Mockito.when(userDAO.isLoginCredentialsValid(userDetails)).thenReturn(null);

        try {
            assertEquals(IdeaPortalResponseConstants.LOGIN_SUCCESSFUL,
                    userService.checkCredentials(userDetails).getStatusText());
        } catch (Exception e) {
            assertEquals(IdeaPortalExceptionConstants.INVALID_CREDENTIALS,
                    e.getMessage());
        }
    }

    @Test
    void getAllThemesResponseMessage_themeFound() {

        Themes themes = new Themes();

        List<Themes> list = new ArrayList<>();
        list.add(themes);

        Mockito.when(userDAO.getAllThemesList()).thenReturn(list);
        assertEquals(IdeaPortalResponseConstants.LIST_ALL_THEMES,
                userService.getAllThemesResponseMessage().getStatusText());
    }

    @Test
    void getAllThemesResponseMessage_themeNotFound() {

        List<Themes> list = new ArrayList<>();
        Mockito.when(userDAO.getAllThemesList()).thenReturn(list);
        assertEquals(IdeaPortalResponseConstants.NO_THEMES_CURRENTLY,
                userService.getAllThemesResponseMessage().getStatusText());
    }

    @Test
    void getIdeasByMostLikesResponseMessage_ideaFound() {

        Themes themes = new Themes();
        themes.setThemeID(1);

        Ideas ideas = new Ideas();

        List<Ideas> list = new ArrayList<>();
        list.add(ideas);

        Mockito.when(userDAO.getIdeasByMostLikesList(themes.getThemeID())).thenReturn(list);
        assertEquals(IdeaPortalResponseConstants.LIST_ALL_IDEAS,
                userService.getIdeasByMostLikesResponseMessage(themes.getThemeID()).getStatusText());
    }

    @Test
    void getIdeasByMostLikesResponseMessage_ideaNotFound() {

        Themes themes = new Themes();
        themes.setThemeID(1);

        List<Ideas> list = new ArrayList<>();

        Mockito.when(userDAO.getIdeasByMostLikesList(themes.getThemeID())).thenReturn(list);
        assertEquals(IdeaPortalResponseConstants.NO_IDEAS_SUBMITTED,
                userService.getIdeasByMostLikesResponseMessage(themes.getThemeID()).getStatusText());
    }

    @Test
    void getIdeasByMostCommentsResponseMessage_ideaFound() {
        Themes themes = new Themes();
        themes.setThemeID(1);

        Ideas ideas = new Ideas();

        List<Ideas> list = new ArrayList<>();
        list.add(ideas);

        Mockito.when(userDAO.getIdeasByMostCommentsList(themes.getThemeID())).thenReturn(list);
        assertEquals(IdeaPortalResponseConstants.LIST_ALL_IDEAS,
                userService.getIdeasByMostCommentsResponseMessage(themes.getThemeID()).getStatusText());
    }

    @Test
    void getIdeasByMostCommentsResponseMessage_ideaNotFound() {
        Themes themes = new Themes();
        themes.setThemeID(1);

        List<Ideas> list = new ArrayList<>();

        Mockito.when(userDAO.getIdeasByMostCommentsList(themes.getThemeID())).thenReturn(list);
        assertEquals(IdeaPortalResponseConstants.NO_IDEAS_SUBMITTED,
                userService.getIdeasByMostCommentsResponseMessage(themes.getThemeID()).getStatusText());
    }

    @Test
    void getIdeasByCreationDateResponseMessage_ideaFound() {
        Themes themes = new Themes();
        themes.setThemeID(1);

        Ideas ideas = new Ideas();

        List<Ideas> list = new ArrayList<>();
        list.add(ideas);

        Mockito.when(userDAO.getIdeasByCreationDateList(themes.getThemeID())).thenReturn(list);
        assertEquals(IdeaPortalResponseConstants.LIST_ALL_IDEAS,
                userService.getIdeasByCreationDateResponseMessage(themes.getThemeID()).getStatusText());
    }

    @Test
    void getIdeasByCreationDateResponseMessage_ideaNotFound() {
        Themes themes = new Themes();
        themes.setThemeID(1);

        List<Ideas> list = new ArrayList<>();

        Mockito.when(userDAO.getIdeasByCreationDateList(themes.getThemeID())).thenReturn(list);
        assertEquals(IdeaPortalResponseConstants.NO_IDEAS_SUBMITTED,
                userService.getIdeasByCreationDateResponseMessage(themes.getThemeID()).getStatusText());
    }

    @Test
    void getIdeaByIDResponseMessage_ideaFound() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);

        Mockito.when(userDAO.getIdea(ideas.getIdeaID())).thenReturn(ideas);

        assertEquals(IdeaPortalResponseConstants.IDEA_OBJECT_MESSAGE,
                userService.getIdeaByIDResponseMessage(ideas.getIdeaID()).getStatusText());
    }

    @Test
    void getIdeaByIDResponseMessage_ideaNotFound() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);

        Mockito.when(userDAO.getIdea(ideas.getIdeaID())).thenReturn(null);

        assertEquals(IdeaPortalResponseConstants.IDEA_BY_ID_NOT_FOUND,
                userService.getIdeaByIDResponseMessage(ideas.getIdeaID()).getStatusText());
    }

    @Test
    void updateUserProfileResponseMessage_success() {
        User user = new User();
        user.setUserEmail("test@test.com");

        Mockito.when(daoUtils.getCountByEmail(user.getUserEmail())).thenReturn(0);
        Mockito.when(userDAO.updateUserProfile(user)).thenReturn(user);

        assertEquals(IdeaPortalResponseConstants.PROFILE_UPDATE_SUCCESS,
                userService.updateUserProfileResponseMessage(user).getStatusText());
    }

    @Test
    void updateUserProfileResponseMessage_emailInUse() {
        User user = new User();
        user.setUserEmail("test@test.com");

        Mockito.when(daoUtils.getCountByEmail(user.getUserEmail())).thenReturn(1);
        Mockito.when(userDAO.updateUserProfile(user)).thenReturn(user);

        try {
            assertEquals(IdeaPortalResponseConstants.PROFILE_UPDATE_SUCCESS,
                    userService.updateUserProfileResponseMessage(user).getStatusText());
        } catch (Exception e){
            assertEquals(IdeaPortalExceptionConstants.EMAIL_IN_USE, e.getMessage());
        }
    }

    @Test
    void updateUserProfileResponseMessage_userNotFound() {
        User user = new User();
        user.setUserEmail("test@test.com");

        Mockito.when(daoUtils.getCountByEmail(user.getUserEmail())).thenReturn(0);
        Mockito.when(userDAO.updateUserProfile(user)).thenReturn(null);

        try {
            assertEquals(IdeaPortalResponseConstants.PROFILE_UPDATE_SUCCESS,
                    userService.updateUserProfileResponseMessage(user).getStatusText());
        } catch (Exception e){
            assertEquals(IdeaPortalExceptionConstants.USER_NOT_FOUND, e.getMessage());
        }
    }

    @Test
    void saveUserPasswordResponseMessage_success() {

        User user = new User();

        Mockito.when(daoUtils.isEqualToOldPassword(user)).thenReturn(false);
        Mockito.when(userDAO.updatePassword(user)).thenReturn(user);

        assertEquals(IdeaPortalResponseConstants.PASSWORD_UPDATE_SUCCESS,
                userService.saveUserPasswordResponseMessage(user).getStatusText());
    }

    @Test
    void saveUserPasswordResponseMessage_samePassword() {

        User user = new User();

        Mockito.when(daoUtils.isEqualToOldPassword(user)).thenReturn(true);
        Mockito.when(userDAO.updatePassword(user)).thenReturn(user);
        try {
            assertEquals(IdeaPortalResponseConstants.PASSWORD_UPDATE_SUCCESS,
                    userService.saveUserPasswordResponseMessage(user).getStatusText());
        } catch (Exception e) {
            assertEquals(IdeaPortalExceptionConstants.SAME_PASSWORD,
                    e.getMessage());
        }

    }

    @Test
    void saveUserPasswordResponseMessage_userNotFound() {

        User user = new User();

        Mockito.when(daoUtils.isEqualToOldPassword(user)).thenReturn(false);
        Mockito.when(userDAO.updatePassword(user)).thenReturn(null);

        assertEquals(IdeaPortalResponseConstants.NO_USER_FOUND,
                userService.saveUserPasswordResponseMessage(user).getStatusText());
    }

    @Test
    void sendEmail_success() {
        User user = new User();
        user.setUserEmail("test@test.com");

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getUserEmail());
        Mockito.doNothing().when(javaMailSender).send(simpleMailMessage);

        assertEquals(IdeaPortalResponseConstants.PASSWORD_LINK_SUCCESS,
                userService.sendEmail(user).getStatusText());
    }

    @Test
    void sendEmail_failure() {
        User user = new User();
        user.setUserEmail("test@test.com");

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getUserEmail());
        Mockito.doThrow(new MailServerException(IdeaPortalExceptionConstants.MAIL_SERVER_EXCEPTION)).
                when(javaMailSender).send(simpleMailMessage);

        try {
            assertEquals(IdeaPortalResponseConstants.PASSWORD_LINK_SUCCESS,
                    userService.sendEmail(user).getStatusText());
        } catch (Exception e){
            assertEquals(IdeaPortalExceptionConstants.MAIL_SERVER_EXCEPTION,
                    e.getMessage());
        }
    }

    @Test
    void likeAnIdeaResponseMessage_likeSuccess() throws IdeaNotLikedException {
        Likes likes = new Likes();
        likes.setLikeValue(LikeValue.LIKE);

        Mockito.when(userDAO.saveLikes(likes)).thenReturn(likes);

        assertEquals(IdeaPortalResponseConstants.LIKE_IDEA_SUCCESS,
                userService.likeAnIdeaResponseMessage(likes).getStatusText());
    }

    @Test
    void likeAnIdeaResponseMessage_dislikeSuccess() throws IdeaNotLikedException {
        Likes likes = new Likes();
        likes.setLikeValue(LikeValue.DISLIKE);

        Mockito.when(userDAO.saveLikes(likes)).thenReturn(likes);

        assertEquals(IdeaPortalResponseConstants.DISLIKE_IDEA_SUCCESS,
                userService.likeAnIdeaResponseMessage(likes).getStatusText());
    }

    @Test
    void likeAnIdeaResponseMessage_notLiked() {
        Likes likes = new Likes();
        likes.setLikeValue(LikeValue.LIKE);

        Mockito.when(userDAO.saveLikes(likes)).thenReturn(null);

        try {
            assertEquals(IdeaPortalResponseConstants.LIKE_IDEA_SUCCESS,
                    userService.likeAnIdeaResponseMessage(likes).getStatusText());
        } catch (Exception e){
            assertEquals(IdeaPortalExceptionConstants.SOME_ERROR_IN_SERVER,
                    e.getMessage());
        }
    }

    @Test
    void commentAnIdeaResponseMessage_success() throws IdeaNotCommentedException {
        Comments comments = new Comments();
        comments.setCommentValue("test comment");

        Mockito.when(userDAO.saveComment(comments)).thenReturn(comments);

        assertEquals(IdeaPortalResponseConstants.COMMENT_SUCCESS,
                userService.commentAnIdeaResponseMessage(comments).getStatusText());
    }

    @Test
    void commentAnIdeaResponseMessage_notCommented() {
        Comments comments = new Comments();
        comments.setCommentValue("test comment");

        Mockito.when(userDAO.saveComment(comments)).thenReturn(null);

        try {
            assertEquals(IdeaPortalResponseConstants.COMMENT_SUCCESS,
                    userService.commentAnIdeaResponseMessage(comments).getStatusText());
        } catch (Exception e){
            assertEquals(IdeaPortalExceptionConstants.SOME_ERROR_IN_SERVER,
                    e.getMessage());
        }
    }

    @Test
    void getLikesForIdeaResponseMessage_success() {
        Ideas ideas = new Ideas();
        User user = new User();
        List<User> list = new ArrayList<>();
        list.add(user);

        Mockito.when(userDAO.getLikesForIdeaList(ideas.getIdeaID())).thenReturn(list);

        assertEquals(IdeaPortalResponseConstants.LIKES_LIST,
                userService.getLikesForIdeaResponseMessage(ideas.getIdeaID()).getStatusText());
    }

    @Test
    void getLikesForIdeaResponseMessage_noLikes() {
        Ideas ideas = new Ideas();
        List<User> list = new ArrayList<>();
        Mockito.when(userDAO.getLikesForIdeaList(ideas.getIdeaID())).thenReturn(list);

        assertEquals(IdeaPortalResponseConstants.NO_LIKES,
                userService.getLikesForIdeaResponseMessage(ideas.getIdeaID()).getStatusText());
    }

    @Test
    void getDislikesForIdeaResponseMessage_success() {
        Ideas ideas = new Ideas();
        User user = new User();
        List<User> list = new ArrayList<>();
        list.add(user);
        Mockito.when(userDAO.getDislikesForIdeaList(ideas.getIdeaID())).thenReturn(list);

        assertEquals(IdeaPortalResponseConstants.DISLIKES_LIST,
                userService.getDislikesForIdeaResponseMessage(ideas.getIdeaID()).getStatusText());
    }

    @Test
    void getDislikesForIdeaResponseMessage_noDislikes() {
        Ideas ideas = new Ideas();
        List<User> list = new ArrayList<>();
        Mockito.when(userDAO.getDislikesForIdeaList(ideas.getIdeaID())).thenReturn(list);

        assertEquals(IdeaPortalResponseConstants.NO_DISLIKES,
                userService.getDislikesForIdeaResponseMessage(ideas.getIdeaID()).getStatusText());
    }

    @Test
    void getCommentForIdeaResponseMessage_success() {
        Ideas ideas = new Ideas();
        Comments comments = new Comments();
        List<Comments> list = new ArrayList<>();
        list.add(comments);
        Mockito.when(userDAO.getCommentsList(ideas.getIdeaID())).thenReturn(list);

        assertEquals(IdeaPortalResponseConstants.COMMENT_LIST,
                userService.getCommentForIdeaResponseMessage(ideas.getIdeaID()).getStatusText());
    }

    @Test
    void getCommentForIdeaResponseMessage_noComments() {
        Ideas ideas = new Ideas();
        List<Comments> list = new ArrayList<>();
        Mockito.when(userDAO.getCommentsList(ideas.getIdeaID())).thenReturn(list);

        assertEquals(IdeaPortalResponseConstants.NO_COMMENTS,
                userService.getCommentForIdeaResponseMessage(ideas.getIdeaID()).getStatusText());
    }

    @Test
    void getUser() {
        User user = new User();
        user.setUserID(1);
        Mockito.when(userDAO.getUser(user.getUserID())).thenReturn(java.util.Optional.of(user));
        assertEquals(user, userService.getUser(user.getUserID()));
    }

    @Test
    void getUsersForRoles() {
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(daoUtils.getUsersForRoles()).thenReturn(map);
        assertEquals(HttpStatus.OK.value(), userService.getUsersForRoles().getStatus());
    }

    @Test
    void getCountOfIdeas() {
        Long count = 1L;
        Mockito.when(daoUtils.getCountOfIdeas()).thenReturn(count);
        assertEquals(HttpStatus.OK.value(), userService.getCountOfIdeas().getStatus());
    }

    @Test
    void getCountOfThemes() {
        Long count = 1L;
        Mockito.when(daoUtils.getCountOfThemes()).thenReturn(count);
        assertEquals(HttpStatus.OK.value(), userService.getCountOfThemes().getStatus());
    }

    @Test
    void getIdeasForNThemes() {
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(daoUtils.getIdeasForNThemes()).thenReturn(map);
        assertEquals(HttpStatus.OK.value(), userService.getIdeasForNThemes().getStatus());
    }

    @Test
    void testGetIdeasForNThemes() {
        Map<String, Integer> map = new HashMap<>();
        int n = 1;
        Mockito.when(daoUtils.getIdeasForNThemes()).thenReturn(map);
        assertEquals(HttpStatus.OK.value(), userService.getIdeasForNThemes(n).getStatus());
    }

    @Test
    void getThemesByDate() {
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(daoUtils.getThemesByDate()).thenReturn(map);
        assertEquals(HttpStatus.OK.value(), userService.getThemesByDate().getStatus());
    }

    @Test
    void testGetThemesByDate() {
        Map<String, Integer> map = new HashMap<>();
        int n = 1;
        Mockito.when(daoUtils.getThemesByDate()).thenReturn(map);
        assertEquals(HttpStatus.OK.value(), userService.getThemesByDate(n).getStatus());
    }

    @Test
    void getParticipantsForIdeas() {
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(daoUtils.getParticipantsForIdeas()).thenReturn(map);
        assertEquals(HttpStatus.OK.value(), userService.getParticipantsForIdeas().getStatus());
    }

    @Test
    void testGetParticipantsForIdeas() {
        Map<String, Integer> map = new HashMap<>();
        int n = 1;
        Mockito.when(daoUtils.getParticipantsForIdeas()).thenReturn(map);
        assertEquals(HttpStatus.OK.value(), userService.getParticipantsForIdeas(n).getStatus());
    }

    @Test
    void getLikesForIdea() {
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(daoUtils.getLikesForIdea()).thenReturn(map);
        assertEquals(HttpStatus.OK.value(), userService.getLikesForIdea().getStatus());
    }

    @Test
    void testGetLikesForIdea() {
        Map<String, Integer> map = new HashMap<>();
        int n = 1;
        Mockito.when(daoUtils.getLikesForIdea()).thenReturn(map);
        assertEquals(HttpStatus.OK.value(), userService.getLikesForIdea(n).getStatus());
    }

    @Test
    void getDislikesForIdea() {
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(daoUtils.getDislikesForIdea()).thenReturn(map);
        assertEquals(HttpStatus.OK.value(), userService.getDislikesForIdea().getStatus());
    }

    @Test
    void testGetDislikesForIdea() {
        Map<String, Integer> map = new HashMap<>();
        int n = 1;
        Mockito.when(daoUtils.getDislikesForIdea()).thenReturn(map);
        assertEquals(HttpStatus.OK.value(), userService.getDislikesForIdea(n).getStatus());
    }

    @Test
    void getAllIdeas() {
        List<Ideas> list = new ArrayList<>();
        Mockito.when(userDAO.getAllIdeas()).thenReturn(list);

        assertEquals(IdeaPortalResponseConstants.LIST_ALL_IDEAS,
                userService.getAllIdeas().getStatusText());
    }
}