package com.ideaportal.controllers;

import com.ideaportal.dao.ProductManagerDAO;
import com.ideaportal.dao.utils.DAOUtils;

import com.ideaportal.models.*;
import com.ideaportal.services.ClientPartnerServices;
import com.ideaportal.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ClientPartnerController.class)
class ClientPartnerControllerTest {



    @MockBean
    ClientPartnerServices clientPartnerServices;

    @MockBean
    UserService userService;

    @MockBean
    DAOUtils daoUtils;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductManagerDAO productManagerDAO;

    @Value("${ideaportal.jwt.secret-key}")
    public String jwtSecretKey;

    @Value("${ideaportal.jwt.expiration-time}")
    public long jwtExpirationTime;

    //Method to create JWT when
    public String generateJWT() {
        long timestamp = System.currentTimeMillis(); //current time in milliseconds


        //Token is configured using this builder method
        return Jwts.builder().signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + jwtExpirationTime))
                .compact(); //builds the token
    }

    @Test
    void uploadReviews_success() throws Exception{


        Themes themes = new Themes();

        Roles role = new Roles();
        role.setRoleID(1);

        User user = new User();
        user.setRole(role);

        String themeName = "test name";
        String themeDesc = "test theme description";
        long themeCategory = 1;

        List<Artifacts> list=new ArrayList<>();

        list.add(new Artifacts(1, themes, null, user, "TestURL"));

        ResponseMessage<Themes> responseMessage = new ResponseMessage<>();
        responseMessage.setResult(themes);
        responseMessage.setStatus(HttpStatus.CREATED.value());

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.clientPartnerServices.saveTheme(any())).thenReturn(responseMessage);
        Mockito.when(this.daoUtils.isThemeNameSame(user.getUserID(), themeName)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create/theme")
                .param("userID",Long.toString(user.getUserID()))
                .param("themeName", themeName)
                .param("themeDescription", themeDesc)
                .param("themeCategory", Long.toString(themeCategory))
                .param("files", Arrays.toString((Object[]) null))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isCreated());
    }

    @Test
    void uploadReviews_userNotFound() throws Exception{
        ResponseMessage<Themes> responseMessage = new ResponseMessage<>();

        Themes themes = new Themes();

        Roles role = new Roles();
        role.setRoleID(1);

        User user = new User();
        user.setRole(role);

        String themeName = "test name";
        String themeDesc = "test theme description";
        long themeCategory = 1;
        List<Artifacts> list=new ArrayList<>();

        list.add(new Artifacts(1, themes, null, user, "TestURL"));

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(null);
        Mockito.when(this.clientPartnerServices.saveTheme(themes)).thenReturn(responseMessage);
        Mockito.when(this.daoUtils.isThemeNameSame(user.getUserID(), themeName)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create/theme")
                .param("userID",Long.toString(user.getUserID()))
                .param("themeName", themeName)
                .param("themeDescription", themeDesc)
                .param("themeCategory", Long.toString(themeCategory))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadReviews_invalidRole() throws Exception{
        ResponseMessage<Themes> responseMessage = new ResponseMessage<>();

        Themes themes = new Themes();

        Roles role = new Roles();
        role.setRoleID(2);

        User user = new User();
        user.setRole(role);

        String themeName = "test name";
        String themeDesc = "test theme description";
        long themeCategory = 1;
        List<Artifacts> list=new ArrayList<>();

        list.add(new Artifacts(1, themes, null, user, "TestURL"));

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.clientPartnerServices.saveTheme(themes)).thenReturn(responseMessage);
        Mockito.when(this.daoUtils.isThemeNameSame(user.getUserID(), themeName)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create/theme")
                .param("userID",Long.toString(user.getUserID()))
                .param("themeName", themeName)
                .param("themeDescription", themeDesc)
                .param("themeCategory", Long.toString(themeCategory))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadReviews_sameThemeName() throws Exception{
        ResponseMessage<Themes> responseMessage = new ResponseMessage<>();

        Themes themes = new Themes();

        Roles role = new Roles();
        role.setRoleID(1);

        User user = new User();
        user.setRole(role);

        String themeName = "test name";
        String themeDesc = "test theme description";
        long themeCategory = 1;

        List<Artifacts> list=new ArrayList<>();

        list.add(new Artifacts(1, themes, null, user, "TestURL"));


        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.clientPartnerServices.saveTheme(themes)).thenReturn(responseMessage);
        Mockito.when(this.daoUtils.isThemeNameSame(user.getUserID(), themeName)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create/theme")
                .param("userID",Long.toString(user.getUserID()))
                .param("themeName", themeName)
                .param("themeDescription", themeDesc)
                .param("themeCategory", Long.toString(themeCategory))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadReviews_jwtAuthFailure() throws Exception{
        ResponseMessage<Themes> responseMessage = new ResponseMessage<>();

        Themes themes = new Themes();

        Roles role = new Roles();
        role.setRoleID(1);

        User user = new User();
        user.setRole(role);

        String themeName = "test name";
        String themeDesc = "test theme description";

        List<Artifacts> list=new ArrayList<>();

        list.add(new Artifacts(1, themes, null, user, "TestURL"));


        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.clientPartnerServices.saveTheme(themes)).thenReturn(responseMessage);
        Mockito.when(this.daoUtils.isThemeNameSame(user.getUserID(), themeName)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create/theme")
                .param("userID",Long.toString(user.getUserID()))
                .param("themeName", themeName)
                .param("themeDescription", themeDesc)
                .accept(MediaType.APPLICATION_JSON))
//                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isForbidden());
    }

//    @Test
//    void getParticipantsForAnIdea_success() throws Exception{
//        ResponseMessage<List<User>> responseMessage = new ResponseMessage<>();
//        responseMessage.setStatus(HttpStatus.OK.value());
//        Ideas idea = new Ideas();
//
//        long ideaID = 1;
//        Mockito.when(this.daoUtils.isIdeaIDValid(ideaID)).thenReturn(idea);
//        Mockito.when(this.clientPartnerServices.getParticipantsForIdeaResponseMessage(ideaID)).thenReturn(responseMessage);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                .get("/api/user/idea/" + ideaID + "/participants")
//                .param("ideaID", String.valueOf(ideaID))
//                .accept(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + this.generateJWT()))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void getParticipantsForAnIdea_ideaNotFound() throws Exception{
//        ResponseMessage<List<User>> responseMessage = new ResponseMessage<>();
//
//        long ideaID = 1;
//        Mockito.when(this.daoUtils.isIdeaIDValid(ideaID)).thenReturn(null);
//        Mockito.when(this.clientPartnerServices.getParticipantsForIdeaResponseMessage(ideaID)).thenReturn(responseMessage);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                .get("/api/user/idea/" + ideaID + "/participants")
//                .param("ideaID", String.valueOf(ideaID))
//                .accept(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + this.generateJWT()))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void getParticipantsForAnIdea_noToken() throws Exception{
//        ResponseMessage<List<User>> responseMessage = new ResponseMessage<>();
//
//        Ideas idea = new Ideas();
//
//        long ideaID = 1;
//        Mockito.when(this.daoUtils.isIdeaIDValid(ideaID)).thenReturn(idea);
//        Mockito.when(this.clientPartnerServices.getParticipantsForIdeaResponseMessage(ideaID)).thenReturn(responseMessage);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                .get("/api/user/idea/" + ideaID + "/participants")
//                .param("ideaID", String.valueOf(ideaID))
//                .accept(MediaType.APPLICATION_JSON))
////                .header("Authorization", "Bearer " + this.generateJWT()))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void getParticipantsForAnIdea_noBearerToken() throws Exception{
//        ResponseMessage<List<User>> responseMessage = new ResponseMessage<>();
//
//        Ideas idea = new Ideas();
//
//        long ideaID = 1;
//        Mockito.when(this.daoUtils.isIdeaIDValid(ideaID)).thenReturn(idea);
//        Mockito.when(this.clientPartnerServices.getParticipantsForIdeaResponseMessage(ideaID)).thenReturn(responseMessage);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                .get("/api/user/idea/" + ideaID + "/participants")
//                .param("ideaID", String.valueOf(ideaID))
//                .accept(MediaType.APPLICATION_JSON)
//                .header("Authorization", this.generateJWT()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void getParticipantsForAnIdea_invalidToken() throws Exception{
//        ResponseMessage<List<User>> responseMessage = new ResponseMessage<>();
//
//        Ideas idea = new Ideas();
//
//        long ideaID = 1;
//        Mockito.when(this.daoUtils.isIdeaIDValid(ideaID)).thenReturn(idea);
//        Mockito.when(this.clientPartnerServices.getParticipantsForIdeaResponseMessage(ideaID)).thenReturn(responseMessage);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                .get("/api/user/idea/" + ideaID + "/participants")
//                .param("ideaID", String.valueOf(ideaID))
//                .accept(MediaType.APPLICATION_JSON))
////                .header("Authorization", "Bearer " + this.generateJWT()))
//                .andExpect(status().isForbidden());
//    }

    @Test
    void getMyCreatedThemes_success() throws Exception{
        ResponseMessage<List<Themes>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());
        Roles roles = new Roles();
        roles.setRoleID(1);

        User user = new User();
        user.setUserID(3);
        user.setRole(roles);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.clientPartnerServices.getMyCreatedThemesResponseMessage(user.getUserID())).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/mythemes/" + user.getUserID())
                .param("userID", String.valueOf(user.getUserID()))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getMyCreatedThemes_userNotFound() throws Exception{
        ResponseMessage<List<Themes>> responseMessage = new ResponseMessage<>();

        Roles roles = new Roles();
        roles.setRoleID(1);

        User user = new User();
        user.setUserID(3);
        user.setRole(roles);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(null);
        Mockito.when(this.clientPartnerServices.getMyCreatedThemesResponseMessage(user.getUserID())).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/mythemes/" + user.getUserID())
                .param("userID", String.valueOf(user.getUserID()))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMyCreatedThemes_invalidRole() throws Exception{
        ResponseMessage<List<Themes>> responseMessage = new ResponseMessage<>();

        Roles roles = new Roles();
        roles.setRoleID(2);

        User user = new User();
        user.setUserID(3);
        user.setRole(roles);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.clientPartnerServices.getMyCreatedThemesResponseMessage(user.getUserID())).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/mythemes/" + user.getUserID())
                .param("userID", String.valueOf(user.getUserID()))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMyCreatedThemes_jwtAuthFailure() throws Exception{
        ResponseMessage<List<Themes>> responseMessage = new ResponseMessage<>();

        Roles roles = new Roles();
        roles.setRoleID(1);

        User user = new User();
        user.setUserID(3);
        user.setRole(roles);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.clientPartnerServices.getMyCreatedThemesResponseMessage(user.getUserID())).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/mythemes/" + user.getUserID())
                .param("userID", String.valueOf(user.getUserID()))
                .accept(MediaType.APPLICATION_JSON))
//                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isForbidden());
    }

}