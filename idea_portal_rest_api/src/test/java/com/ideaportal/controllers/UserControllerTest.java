package com.ideaportal.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideaportal.constants.IdeaPortalResponseConstants;
import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.models.*;
import com.ideaportal.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    UserService userService;

    @MockBean
    DAOUtils daoUtils;

    @Autowired
    MockMvc mockMvc;

    @Value("${ideaportal.jwt.secret-key}")
    public String jwtSecretKey;

    @Value("${ideaportal.jwt.expiration-time}")
    public long jwtExpirationTime;

    //Method to create JWT
    public String generateJWT() {
        long timestamp = System.currentTimeMillis(); //current time in milliseconds


        //Token is configured using this builder method
        return Jwts.builder().signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + jwtExpirationTime))
                .compact();
    }

    @Test
    void createNewUser() throws Exception {
        ResponseMessage<User> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.CREATED.value());
        Roles role = new Roles();
        role.setRoleID(1);
        role.setRoleName("CP");

        User user = new User();
        user.setUserName("test name");
        user.setUserPassword("test password");
        user.setUserEmail("test@email.com");
        user.setUserCompany("test company");
        user.setRole(role);

        String jsonString = objectMapper.writeValueAsString(user);

        Mockito.when(this.userService.addUser(any())).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/signup")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    void loginUser() throws Exception {
        ResponseMessage<User> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Login loginUser = new Login();
        loginUser.setUserName("test name");
        loginUser.setUserPassword("test password");

        String jsonString = objectMapper.writeValueAsString(loginUser);

        Mockito.when(this.userService.checkCredentials(any())).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/login")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void getAllThemes() throws Exception {
        ResponseMessage<List<Themes>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Mockito.when(this.userService.getAllThemesResponseMessage()).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/themes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getThemeByID_themeFound() throws Exception {

        long themeID = 1;

        Themes themes = new Themes();

        Mockito.when(this.daoUtils.findThemeByID(themeID)).thenReturn(themes);

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/themes/"+ themeID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getThemeByID_themeNotFound() throws Exception {

        long themeID = 1;

        Mockito.when(this.daoUtils.findThemeByID(themeID)).thenReturn(null);

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/themes/"+ themeID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getIdeaByID_ideaFound() throws Exception {

        Ideas idea = new Ideas();
        idea.setIdeaID(1);
        idea.setIsDeleted(IsDeleted.FALSE);

        ResponseMessage<Ideas> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        responseMessage.setResult(idea);

        Mockito.when(this.userService.getIdeaByIDResponseMessage(idea.getIdeaID())).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/idea/"+ idea.getIdeaID())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getIdeaByID_ideaNotFound() throws Exception {

        Ideas idea = new Ideas();
        idea.setIdeaID(1);
        idea.setIsDeleted(IsDeleted.FALSE);

        ResponseMessage<Ideas> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.NOT_FOUND.value());
        responseMessage.setResult(idea);

        Mockito.when(this.userService.getIdeaByIDResponseMessage(idea.getIdeaID())).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/idea/"+ idea.getIdeaID())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getIdeasByMostLikesForTheme_themeFound() throws Exception {

        long themeID = 1;

        ResponseMessage<List<Ideas>> responseMessage = new ResponseMessage<>();

        responseMessage.setStatus(HttpStatus.OK.value());

        Mockito.when(this.userService.getIdeasByMostLikesResponseMessage(themeID)).thenReturn(responseMessage);
        Themes themes = new Themes();

        Mockito.when(this.daoUtils.findThemeByID(themeID)).thenReturn(themes);
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/themes/" + themeID + "/ideas/mostlikes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getIdeasByMostLikesForTheme_themeNotFound() throws Exception {

        long themeID = 1;

        ResponseMessage<List<Ideas>> responseMessage = new ResponseMessage<>();

        Mockito.when(this.userService.getIdeasByMostLikesResponseMessage(themeID)).thenReturn(responseMessage);

        Mockito.when(this.daoUtils.findThemeByID(themeID)).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/themes/" + themeID + "/ideas/mostlikes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getIdeasByMostCommentsForTheme_themeNotFound() throws Exception {
        long themeID = 1;

        ResponseMessage<List<Ideas>> responseMessage = new ResponseMessage<>();

        Mockito.when(this.daoUtils.findThemeByID(themeID)).thenReturn(null);
        Mockito.when(this.userService.getIdeasByMostCommentsResponseMessage(themeID)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/themes/" + themeID + "/ideas/mostcomments")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getIdeasByMostCommentsForTheme_themeFound() throws Exception {
        long themeID = 1;

        ResponseMessage<List<Ideas>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Themes themes = new Themes();

        Mockito.when(this.daoUtils.findThemeByID(themeID)).thenReturn(themes);
        Mockito.when(this.userService.getIdeasByMostCommentsResponseMessage(themeID)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/themes/" + themeID + "/ideas/mostcomments")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getIdeasByCreationDateForTheme_themeFound() throws Exception {

        long themeID = 1;

        ResponseMessage<List<Ideas>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Themes themes = new Themes();

        Mockito.when(this.daoUtils.findThemeByID(themeID)).thenReturn(themes);
        Mockito.when(this.userService.getIdeasByCreationDateResponseMessage(themeID)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/themes/" + themeID + "/ideas/newest")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getIdeasByCreationDateForTheme_themeNotFound() throws Exception {

        long themeID = 1;

        ResponseMessage<List<Ideas>> responseMessage = new ResponseMessage<>();

        Mockito.when(this.daoUtils.findThemeByID(themeID)).thenReturn(null);
        Mockito.when(this.userService.getIdeasByMostCommentsResponseMessage(themeID)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/themes/" + themeID + "/ideas/newest")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserProfile() throws Exception{



        User user = new User();
        user.setUserEmail("test@test.com");
        user.setUserCompany("test company");

        ResponseMessage<User> responseMessage = new ResponseMessage<>(HttpStatus.OK.value(),
                IdeaPortalResponseConstants.PROFILE_UPDATE_SUCCESS, user);

        String jsonString = objectMapper.writeValueAsString(user);
        Mockito.when(this.userService.updateUserProfileResponseMessage(any())).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/api/user/profile/update/profile")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonString)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void likeAnIdea_alreadyLiked() throws Exception {
        ResponseMessage<Likes> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        String res = "null";

        Ideas idea = new Ideas();
        idea.setIdeaID(2);
        idea.setIsDeleted(IsDeleted.FALSE);

        User user = new User();
        user.setUserID(1);

        Likes likes = new Likes();
        likes.setLikeID(1);
        likes.setLikeValue(LikeValue.LIKE);
        likes.setLikeDate(new java.sql.Date(System.currentTimeMillis()));
        likes.setUser(user);
        likes.setIdea(idea);

        String jsonString  = objectMapper.writeValueAsString(likes);

        Mockito.when(this.daoUtils.isIdeaLiked(likes)).thenReturn(res);
        Mockito.when(this.daoUtils.isIdeaIDValid(likes.getIdea().getIdeaID())).thenReturn(idea);
        Mockito.when(this.daoUtils.findByUserId(likes.getUser().getUserID())).thenReturn(user);
        Mockito.when(this.userService.likeAnIdeaResponseMessage(any())).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/api/user/idea/like")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void likeAnIdea_success() throws Exception {
        ResponseMessage<Likes> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Ideas idea = new Ideas();
        idea.setIdeaID(2);
        idea.setIsDeleted(IsDeleted.FALSE);

        User user = new User();
        user.setUserID(1);

        Likes likes = new Likes();
        likes.setLikeID(1);
        likes.setLikeValue(LikeValue.LIKE);
        likes.setLikeDate(new java.sql.Date(System.currentTimeMillis()));
        likes.setUser(user);
        likes.setIdea(idea);

        String jsonString  = objectMapper.writeValueAsString(likes);

        Mockito.when(this.daoUtils.isIdeaLiked(likes)).thenReturn(null);
        Mockito.when(this.daoUtils.isIdeaIDValid(likes.getIdea().getIdeaID())).thenReturn(idea);
        Mockito.when(this.daoUtils.findByUserId(likes.getUser().getUserID())).thenReturn(user);
        Mockito.when(this.userService.likeAnIdeaResponseMessage(any())).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/api/user/idea/like")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void likeAnIdea_ideaNotFound() throws Exception {
        ResponseMessage<Likes> responseMessage = new ResponseMessage<>();

        Ideas idea = new Ideas();
        idea.setIdeaID(2);

        User user = new User();
        user.setUserID(1);

        Likes likes = new Likes();
        likes.setLikeID(1);
        likes.setLikeValue(LikeValue.LIKE);
        likes.setLikeDate(new java.sql.Date(System.currentTimeMillis()));
        likes.setUser(user);
        likes.setIdea(idea);

        String jsonString  = objectMapper.writeValueAsString(likes);

        Mockito.when(this.daoUtils.isIdeaLiked(likes)).thenReturn(null);
        Mockito.when(this.daoUtils.isIdeaIDValid(likes.getIdea().getIdeaID())).thenReturn(null);
        Mockito.when(this.daoUtils.findByUserId(likes.getUser().getUserID())).thenReturn(user);
        Mockito.when(this.userService.likeAnIdeaResponseMessage(likes)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/api/user/idea/like")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void likeAnIdea_userNotFound() throws Exception {
        ResponseMessage<Likes> responseMessage = new ResponseMessage<>();

        Ideas idea = new Ideas();
        idea.setIdeaID(2);

        User user = new User();
        user.setUserID(1);

        Likes likes = new Likes();
        likes.setLikeID(1);
        likes.setLikeValue(LikeValue.LIKE);
        likes.setLikeDate(new java.sql.Date(System.currentTimeMillis()));
        likes.setUser(user);
        likes.setIdea(idea);

        String jsonString  = objectMapper.writeValueAsString(likes);

        Mockito.when(this.daoUtils.isIdeaLiked(likes)).thenReturn(null);
        Mockito.when(this.daoUtils.isIdeaIDValid(likes.getIdea().getIdeaID())).thenReturn(idea);
        Mockito.when(this.daoUtils.findByUserId(likes.getUser().getUserID())).thenReturn(null);
        Mockito.when(this.userService.likeAnIdeaResponseMessage(likes)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/api/user/idea/like")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void likeAnIdea_jwtAuthFailure() throws Exception {
        ResponseMessage<Likes> responseMessage = new ResponseMessage<>();

        Ideas idea = new Ideas();
        idea.setIdeaID(2);

        User user = new User();
        user.setUserID(1);

        Likes likes = new Likes();
        likes.setLikeID(1);
        likes.setLikeValue(LikeValue.LIKE);
        likes.setLikeDate(new java.sql.Date(System.currentTimeMillis()));
        likes.setUser(user);
        likes.setIdea(idea);

        String jsonString  = objectMapper.writeValueAsString(likes);

        Mockito.when(this.daoUtils.isIdeaLiked(likes)).thenReturn(null);
        Mockito.when(this.daoUtils.isIdeaIDValid(likes.getIdea().getIdeaID())).thenReturn(idea);
        Mockito.when(this.daoUtils.findByUserId(likes.getUser().getUserID())).thenReturn(user);
        Mockito.when(this.userService.likeAnIdeaResponseMessage(likes)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/api/user/idea/like")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isForbidden());
    }

    @Test
    void commentAnIdea_success() throws Exception{
        ResponseMessage<Comments> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Ideas idea = new Ideas();
        idea.setIdeaID(1);
        idea.setIsDeleted(IsDeleted.FALSE);

        User user = new User();
        user.setUserID(1);

        Comments comments = new Comments();
        comments.setCommentValue("test comment");
        comments.setCommentDate(new java.sql.Date(System.currentTimeMillis()));
        comments.setUser(user);
        comments.setIdea(idea);

        String jsonString  = objectMapper.writeValueAsString(comments);

        Mockito.when(this.daoUtils.findByUserId(comments.getUser().getUserID())).thenReturn(user);
        Mockito.when(this.daoUtils.isIdeaIDValid(comments.getIdea().getIdeaID())).thenReturn(idea);
        Mockito.when(this.userService.commentAnIdeaResponseMessage(comments)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/idea/comment")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void commentAnIdea_ideaNotFound() throws Exception{
        ResponseMessage<Comments> responseMessage = new ResponseMessage<>();

        Ideas idea = new Ideas();
        idea.setIdeaID(1);

        User user = new User();
        user.setUserID(1);

        Comments comments = new Comments();
        comments.setCommentValue("test comment");
        comments.setCommentDate(new java.sql.Date(System.currentTimeMillis()));
        comments.setUser(user);
        comments.setIdea(idea);

        String jsonString  = objectMapper.writeValueAsString(comments);

        Mockito.when(this.daoUtils.findByUserId(comments.getUser().getUserID())).thenReturn(user);
        Mockito.when(this.daoUtils.isIdeaIDValid(comments.getIdea().getIdeaID())).thenReturn(null);
        Mockito.when(this.userService.commentAnIdeaResponseMessage(comments)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/idea/comment")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void commentAnIdea_userNotFound() throws Exception{
        ResponseMessage<Comments> responseMessage = new ResponseMessage<>();

        Ideas idea = new Ideas();
        idea.setIdeaID(1);

        User user = new User();
        user.setUserID(1);

        Comments comments = new Comments();
        comments.setCommentValue("test comment");
        comments.setCommentDate(new java.sql.Date(System.currentTimeMillis()));
        comments.setUser(user);
        comments.setIdea(idea);

        String jsonString  = objectMapper.writeValueAsString(comments);

        Mockito.when(this.daoUtils.findByUserId(comments.getUser().getUserID())).thenReturn(null);
        Mockito.when(this.daoUtils.isIdeaIDValid(comments.getIdea().getIdeaID())).thenReturn(idea);
        Mockito.when(this.userService.commentAnIdeaResponseMessage(comments)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/idea/comment")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void commentAnIdea_jwtAuthFailure() throws Exception{
        ResponseMessage<Comments> responseMessage = new ResponseMessage<>();

        Ideas idea = new Ideas();
        idea.setIdeaID(1);

        User user = new User();
        user.setUserID(1);

        Comments comments = new Comments();
        comments.setCommentValue("test comment");
        comments.setCommentDate(new java.sql.Date(System.currentTimeMillis()));
        comments.setUser(user);
        comments.setIdea(idea);

        String jsonString  = objectMapper.writeValueAsString(comments);

        Mockito.when(this.daoUtils.findByUserId(comments.getUser().getUserID())).thenReturn(user);
        Mockito.when(this.daoUtils.isIdeaIDValid(comments.getIdea().getIdeaID())).thenReturn(idea);
        Mockito.when(this.userService.commentAnIdeaResponseMessage(comments)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/idea/comment")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isForbidden());
    }




    @Test
    void getLikesForIdea_ideaNotFound() throws Exception{
        ResponseMessage<List<User>> responseMessage = new ResponseMessage<>();

        long ideaID = 1;


        Mockito.when(this.daoUtils.isIdeaIDValid(ideaID)).thenReturn(null);
        Mockito.when(this.userService.getLikesForIdeaResponseMessage(ideaID)).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/idea/" + ideaID + "/likes")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLikesForIdea_success() throws Exception{
        ResponseMessage<List<User>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        long ideaID = 1;

        Ideas idea = new Ideas();
        idea.setIsDeleted(IsDeleted.FALSE);

        Mockito.when(this.daoUtils.isIdeaIDValid(ideaID)).thenReturn(idea);
        Mockito.when(this.userService.getLikesForIdeaResponseMessage(ideaID)).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/idea/" + ideaID + "/likes")
                .param("ideaID", String.valueOf(ideaID))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getDislikesForIdea_success() throws Exception{
        ResponseMessage<List<User>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        long ideaID = 1;
        Ideas idea = new Ideas();
        idea.setIsDeleted(IsDeleted.FALSE);

        Mockito.when(this.daoUtils.isIdeaIDValid(ideaID)).thenReturn(idea);
        Mockito.when(this.userService.getDislikesForIdeaResponseMessage(ideaID)).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/idea/" + ideaID + "/dislikes")
                .param("ideaID", String.valueOf(ideaID))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getDislikesForIdea_ideaNotFound() throws Exception{
        ResponseMessage<List<User>> responseMessage = new ResponseMessage<>();

        long ideaID = 1;

        Mockito.when(this.daoUtils.isIdeaIDValid(ideaID)).thenReturn(null);
        Mockito.when(this.userService.getLikesForIdeaResponseMessage(ideaID)).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/idea/" + ideaID + "/dislikes")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }


    @Test
    void getCommentsForIdea_success() throws Exception{
        ResponseMessage<List<Comments>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        long ideaID = 1;
        Ideas idea = new Ideas();
        idea.setIsDeleted(IsDeleted.FALSE);

        Mockito.when(this.daoUtils.isIdeaIDValid(ideaID)).thenReturn(idea);
        Mockito.when(this.userService.getCommentForIdeaResponseMessage(ideaID)).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/idea/" + ideaID + "/comments")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getCommentsForIdea_ideaNotFound() throws Exception{
        ResponseMessage<List<User>> responseMessage = new ResponseMessage<>();

        long ideaID = 1;

        Mockito.when(this.daoUtils.isIdeaIDValid(ideaID)).thenReturn(null);
        Mockito.when(this.userService.getLikesForIdeaResponseMessage(ideaID)).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/idea/" + ideaID + "/comments")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUsersForRoles() throws Exception{
        ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Mockito.when(userService.getUsersForRoles()).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/userCount")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getCountOfIdeas() throws Exception {
        ResponseMessage<Long> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Mockito.when(userService.getCountOfIdeas()).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/ideaCount")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getCountOfThemes() throws Exception {
        ResponseMessage<Long> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Mockito.when(userService.getCountOfThemes()).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/themeCount")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getIdeasForThemes() throws Exception {
        ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Mockito.when(userService.getIdeasForNThemes()).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/ideasForThemes")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getThemesByDate() throws Exception {
        ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Mockito.when(userService.getThemesByDate()).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/themesByDate")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getParticipantsForIdeas() throws Exception {
        ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Mockito.when(userService.getParticipantsForIdeas()).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/participantsForIdea")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getLikesForIdea() throws Exception {
        ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Mockito.when(userService.getLikesForIdea()).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/getLikesForIdea")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getDislikesForIdea() throws Exception {
        ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Mockito.when(userService.getDislikesForIdea()).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/getDislikesForIdea")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllIdeas() throws Exception {
        ResponseMessage<List<Ideas>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Mockito.when(userService.getAllIdeas()).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/ideas")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }
}