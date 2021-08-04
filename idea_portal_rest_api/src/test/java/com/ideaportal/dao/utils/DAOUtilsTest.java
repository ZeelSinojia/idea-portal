package com.ideaportal.dao.utils;

import com.ideaportal.models.*;
import com.ideaportal.repos.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@SpringBootTest
class DAOUtilsTest {

    @Autowired
    DAOUtils daoUtils;

    @MockBean
    UserRepository userRepository;
    @MockBean
    LikesRepository likesRepository;
    @MockBean
    IdeasRepository ideasRepository;
    @MockBean
    ThemesRepository themesRepository;
    @MockBean
    PasswordLogRepository passwordLogRepository;
    @MockBean
    ParticipationRepository participationRepository;
    @MockBean
    CommentsRepository commentsRespository;
    @MockBean
    JdbcTemplate jdbcTemplate;

    @Value("${max-themes}")
    private String maxThemes;

    @Test
    void getCountByEmail_testOne() {
        int count = 1;
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(count);
        assertEquals(1, daoUtils.getCountByEmail("user@email.com"));
    }

    @Test
    void getCountByEmail_testTwo() {
        int count = -1;
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(count);
        assertEquals(-1, daoUtils.getCountByEmail("user@email.com"));
    }

    @Test
    void getCountByUserName_testOne() {
        int count = 1;
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(count);
        assertEquals(1, daoUtils.getCountByUserName("username"));
    }

    @Test
    void getCountByUserName_testTwo() {
        int count = -1;
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(count);
        assertEquals(-1, daoUtils.getCountByUserName("username"));
    }

    @Test
    void findByUserName_testOne() {
        User user = new User();
        user.setUserID(1);
        Mockito.when(userRepository.findById(user.getUserID())).thenReturn(java.util.Optional.of(user));
        assertEquals(1, daoUtils.findByUserName(user).getUserID());
    }

    @Test
    void findByUserName_testTwo() {
        User user = new User();
        user.setUserID(1);
        Mockito.when(userRepository.findById(user.getUserID())).thenReturn(java.util.Optional.empty());
        assertNull(daoUtils.findByUserName(user));
    }

    @Test
    void findByUserEmail() {
        User user = new User();
        user.setUserID(1);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(user);
        assertEquals(1, daoUtils.findByUserEmail(user).getUserID());
    }

    @Test
    void findByUserId_testOne() {
        User user = new User();
        user.setUserID(1);
        Mockito.when(userRepository.findById(user.getUserID())).thenReturn(java.util.Optional.of(user));
        assertEquals(1, daoUtils.findByUserId(user.getUserID()).getUserID());
    }

    @Test
    void findByUserId_testTwo() {
        User user = new User();
        user.setUserID(1);
        Mockito.when(userRepository.findById(user.getUserID())).thenReturn(java.util.Optional.empty());
        assertNull(daoUtils.findByUserId(user.getUserID()));
    }

    @Test
    void findLikeID_testOne() {
        User user = new User();
        user.setUserID(1);
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(1L);
        assertEquals(1L, daoUtils.findLikeID(ideas.getIdeaID(), user.getUserID()));
    }

    @Test
    void findLikeID_testTwo() {
        User user = new User();
        user.setUserID(1);
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(0L);
        assertEquals(0L, daoUtils.findLikeID(ideas.getIdeaID(), user.getUserID()));
    }

    @Test
    void isIdeaLiked_testOne() {
        Likes likes  = new Likes();
        likes.setLikeID(1);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(LikeValue.LIKE.toString());
        assertEquals(LikeValue.LIKE.toString(), daoUtils.isIdeaLiked(likes));
    }

    @Test
    void isIdeaLiked_testTwo() {
        Likes likes  = new Likes();
        likes.setLikeID(1);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(LikeValue.DISLIKE.toString());
        assertEquals(LikeValue.DISLIKE.toString(), daoUtils.isIdeaLiked(likes));
    }

    @Test
    void isIdeaLiked_testThree() {
        Likes likes  = new Likes();
        likes.setLikeID(1);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(null);
        assertNull(daoUtils.isIdeaLiked(likes));
    }

    @Test
    void buildLikesObject() {
        User user = new User();
        user.setUserID(1);
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Likes likes  = new Likes();
        likes.setLikeID(1);
        likes.setUser(user);
        likes.setIdea(ideas);

        Mockito.when(userRepository.findById(likes.getUser().getUserID())).thenReturn(java.util.Optional.of(user));
        Mockito.when(ideasRepository.findById(likes.getIdea().getIdeaID())).thenReturn(java.util.Optional.of(ideas));
        assertEquals(likes, daoUtils.buildLikesObject(likes));
    }

    @Test
    void buildCommentsObject() {
        User user = new User();
        user.setUserID(1);
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Comments comments  = new Comments();
        comments.setCommentID(1);
        comments.setUser(user);
        comments.setIdea(ideas);

        Mockito.when(userRepository.findById(comments.getUser().getUserID())).thenReturn(java.util.Optional.of(user));
        Mockito.when(ideasRepository.findById(comments.getIdea().getIdeaID())).thenReturn(java.util.Optional.of(ideas));
        assertEquals(comments, daoUtils.buildCommentsObject(comments));
    }

    @Test
    void buildParticipationResponseObject() {
        User user = new User();
        user.setUserID(1);
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Themes themes = new Themes();
        themes.setThemeID(1);
        ParticipationResponses participationResponses  = new ParticipationResponses();
        participationResponses.setResponseID(1);
        participationResponses.setUser(user);
        participationResponses.setIdea(ideas);
        participationResponses.setTheme(themes);

        Mockito.when(userRepository.findById(participationResponses.getUser().getUserID())).thenReturn(java.util.Optional.of(user));
        Mockito.when(ideasRepository.findById(participationResponses.getIdea().getIdeaID())).thenReturn(java.util.Optional.of(ideas));
        Mockito.when(themesRepository.findById(participationResponses.getTheme().getThemeID())).thenReturn(java.util.Optional.of(themes));
        assertEquals(participationResponses, daoUtils.buildParticipationResponseObject(participationResponses));
    }

    @Test
    void findThemeByID_testOne() {
        Themes themes = new Themes();
        themes.setThemeID(1);
        themes.setIsDeleted(IsDeleted.FALSE);
        Mockito.when(themesRepository.findById(themes.getThemeID())).thenReturn(java.util.Optional.of(themes));
        assertEquals(themes, daoUtils.findThemeByID(themes.getThemeID()));
    }

    @Test
    void findThemeByID_testTwo() {
        Themes themes = new Themes();
        themes.setThemeID(1);
        Mockito.when(themesRepository.findById(themes.getThemeID())).thenReturn(java.util.Optional.empty());
        assertNull(daoUtils.findThemeByID(themes.getThemeID()));
    }

    @Test
    void isThemeNameSame_testOne() {
        User user = new User();
        user.setUserID(1);
        Themes themes = new Themes();
        themes.setThemeName("theme");
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(true);
        assertTrue(daoUtils.isThemeNameSame(user.getUserID(), themes.getThemeName()));
    }

    @Test
    void isThemeNameSame_testTwo() {
        User user = new User();
        user.setUserID(1);
        Themes themes = new Themes();
        themes.setThemeName("theme");
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(false);
        assertFalse(daoUtils.isThemeNameSame(user.getUserID(), themes.getThemeName()));
    }

    @Test
    void isAlreadyParticipated_testOne() {
        User user = new User();
        user.setUserID(1);
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Themes themes = new Themes();
        themes.setThemeID(1);
        ParticipationResponses participationResponses  = new ParticipationResponses();
        participationResponses.setResponseID(1);
        participationResponses.setUser(user);
        participationResponses.setIdea(ideas);
        participationResponses.setTheme(themes);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(true);
        assertTrue(daoUtils.isAlreadyParticipated(participationResponses));
    }

    @Test
    void isAlreadyParticipated_testTwo() {
        User user = new User();
        user.setUserID(1);
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Themes themes = new Themes();
        themes.setThemeID(1);
        ParticipationResponses participationResponses  = new ParticipationResponses();
        participationResponses.setResponseID(1);
        participationResponses.setUser(user);
        participationResponses.setIdea(ideas);
        participationResponses.setTheme(themes);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(true);
        assertTrue(daoUtils.isAlreadyParticipated(participationResponses));
    }

    @Test
    void isIdeaInTheme_testOne() {
        User user = new User();
        user.setUserID(1);
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Themes themes = new Themes();
        themes.setThemeID(1);
        ParticipationResponses participationResponses  = new ParticipationResponses();
        participationResponses.setResponseID(1);
        participationResponses.setUser(user);
        participationResponses.setIdea(ideas);
        participationResponses.setTheme(themes);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(true);
        assertTrue(daoUtils.isIdeaInTheme(participationResponses));
    }

    @Test
    void isIdeaInTheme_testTwo() {
        User user = new User();
        user.setUserID(1);
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Themes themes = new Themes();
        themes.setThemeID(1);
        ParticipationResponses participationResponses  = new ParticipationResponses();
        participationResponses.setResponseID(1);
        participationResponses.setUser(user);
        participationResponses.setIdea(ideas);
        participationResponses.setTheme(themes);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(false);
        assertFalse(daoUtils.isIdeaInTheme(participationResponses));
    }

    @Test
    void isIdeaIDValid_testOne() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Mockito.when(ideasRepository.findById(ideas.getIdeaID())).thenReturn(java.util.Optional.of(ideas));
        assertEquals(ideas, daoUtils.isIdeaIDValid(ideas.getIdeaID()));
    }

    @Test
    void isIdeaIDValid_testTwo() {
        Ideas ideas = new Ideas();
        ideas.setIdeaID(1);
        Mockito.when(ideasRepository.findById(ideas.getIdeaID())).thenReturn(java.util.Optional.empty());
        assertNull(daoUtils.isIdeaIDValid(ideas.getIdeaID()));
    }

    @Test
    void isIdeaNameSame_testOne() {
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(true);
        assertTrue(daoUtils.isIdeaNameSame(1, "ideaName", 1));
    }

    @Test
    void isIdeaNameSame_testTwo() {
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(false);
        assertFalse(daoUtils.isIdeaNameSame(1, "ideaName", 1));
    }

    @Test
    void isCountExceeding_testOne() {
        User user = new User();
        user.setUserID(1);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(true);
        assertTrue(daoUtils.isCountExceeding(user.getUserID()));
    }

    @Test
    void isCountExceeding_testTwo() {
        User user = new User();
        user.setUserID(1);
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(false);
        assertFalse(daoUtils.isCountExceeding(user.getUserID()));
    }

    @Test
    void getUsersForRoles() {
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(map);
        assertEquals(map, daoUtils.getUsersForRoles());
    }

    @Test
    void getCountOfIdeas() {
        long count = 1;
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(count);
        assertEquals(1, daoUtils.getCountOfIdeas());
    }

    @Test
    void getCountOfThemes() {
        long count = 1;
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(count);
        assertEquals(1, daoUtils.getCountOfThemes());
    }

    @Test
    void getIdeasForNThemes() {
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(map);
        assertEquals(map, daoUtils.getIdeasForNThemes());
    }

    @Test
    void testGetIdeasForNThemes() {
        int n = 2;
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(map);
        assertEquals(map, daoUtils.getIdeasForNThemes(n));
    }

    @Test
    void getThemesByDate() {
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(map);
        assertEquals(map, daoUtils.getThemesByDate());
    }

    @Test
    void testGetThemesByDate() {
        int n = 2;
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(map);
        assertEquals(map, daoUtils.getThemesByDate(n));
    }

    @Test
    void getParticipantsForIdeas() {
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(map);
        assertEquals(map, daoUtils.getParticipantsForIdeas());
    }

    @Test
    void testGetParticipantsForIdeas() {
        int n = 2;
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(map);
        assertEquals(map, daoUtils.getParticipantsForIdeas(n));
    }

    @Test
    void getLikesForIdea() {
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(map);
        assertEquals(map, daoUtils.getLikesForIdea());
    }

    @Test
    void testGetLikesForIdea() {
        int n = 2;
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(map);
        assertEquals(map, daoUtils.getLikesForIdea(n));
    }

    @Test
    void getDislikesForIdea() {
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(map);
        assertEquals(map, daoUtils.getDislikesForIdea());
    }

    @Test
    void testGetDislikesForIdea() {
        int n = 2;
        Map<String, Integer> map = new HashMap<>();
        Mockito.when(jdbcTemplate.execute(anyString(), (PreparedStatementCallback<Object>) any())).thenReturn(map);
        assertEquals(map, daoUtils.getDislikesForIdea(n));
    }
}