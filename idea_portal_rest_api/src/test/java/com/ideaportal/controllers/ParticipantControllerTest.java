package com.ideaportal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.models.*;
import com.ideaportal.services.ParticipantService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Date;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(ParticipantController.class)
class ParticipantControllerTest {



    final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    ParticipantService participantService;

    @MockBean
    DAOUtils daoUtils;

    @Autowired
    MockMvc mockMvc;

    @Value("${ideaportal.jwt.secret-key}")
    public String jwtSecretKey;

    @Value("${ideaportal.jwt.expiration-time}")
    public long jwtExpirationTime;

    //Method to create JWT when
    public String generateJWT() {
        long timestamp = System.currentTimeMillis(); //current time in milliseconds


        //Token is configured using this builder method
        return Jwts.builder().signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .setIssuedAt(new java.util.Date(timestamp))
                .setExpiration(new java.util.Date(timestamp + jwtExpirationTime))
                .compact(); //builds the token
    }

    @Test
    void participateForIdea_success() throws Exception {

        ResponseMessage<Object> responseMessage = new ResponseMessage<>();

        Ideas idea = new Ideas();
        idea.setIdeaID(1);

        Roles role = new Roles();
        role.setRoleID(3);

        User user = new User();
        user.setUserID(1);
        user.setRole(role);

        Themes themes = new Themes();
        themes.setThemeName("random");
        themes.setThemeID(1);
        themes.setIsDeleted(IsDeleted.FALSE);

        ParticipationResponses participationResponses = new ParticipationResponses();
        participationResponses.setIdea(idea);
        participationResponses.setUser(user);
        participationResponses.setTheme(themes);
        participationResponses.setParticipationDate(new Date(1621080367));

        String jsonString = objectMapper.writeValueAsString(participationResponses);

        Mockito.when(this.daoUtils.findByUserId(anyInt())).thenReturn(user);
        Mockito.when(this.daoUtils.isIdeaIDValid(anyInt())).thenReturn(idea);
        Mockito.when(this.daoUtils.findThemeByID(anyInt())).thenReturn(themes);
        Mockito.when(this.participantService.alreadyParticipatedInAnIdeaResponse(any())).thenReturn(false);
        Mockito.when(this.participantService.isIdeaInTheme(any())).thenReturn(true);
        Mockito.when(this.participantService.registerForIdea(any())).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/participant/participate")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }


    @ParameterizedTest
    @CsvSource({
            "3, false",
            "1, false",
            "3, true",
    })
    void participateForIdea_ideaNotFound_invalidRole_alreadyParticipated(int roleIDParam, boolean alreadyParticipatedParam) throws Exception {

        ResponseMessage<Object> responseMessage = new ResponseMessage<>();

        Ideas idea = new Ideas();
        idea.setIdeaID(1);

        Roles role = new Roles();
        role.setRoleID(roleIDParam);

        User user = new User();
        user.setUserID(1);
        user.setRole(role);

        Themes themes = new Themes();
        themes.setThemeID(1);

        ParticipationResponses participationResponses = new ParticipationResponses();
        participationResponses.setIdea(idea);
        participationResponses.setUser(user);
        participationResponses.setTheme(themes);
        participationResponses.setParticipationDate(new Date(1621080367));

        String jsonString = objectMapper.writeValueAsString(participationResponses);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.daoUtils.isIdeaIDValid(idea.getIdeaID())).thenReturn(null);
        Mockito.when(this.daoUtils.findThemeByID(themes.getThemeID())).thenReturn(themes);
        Mockito.when(this.participantService.alreadyParticipatedInAnIdeaResponse(participationResponses)).thenReturn(alreadyParticipatedParam);
        Mockito.when(this.participantService.isIdeaInTheme(participationResponses)).thenReturn(true);
        Mockito.when(this.participantService.registerForIdea(participationResponses)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/participant/participate")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void participateForIdea_userNotFound() throws Exception {

        ResponseMessage<Object> responseMessage = new ResponseMessage<>();

        Ideas idea = new Ideas();
        idea.setIdeaID(1);

        Roles role = new Roles();
        role.setRoleID(3);

        User user = new User();
        user.setUserID(1);
        user.setRole(role);

        Themes themes = new Themes();
        themes.setThemeID(1);

        ParticipationResponses participationResponses = new ParticipationResponses();
        participationResponses.setIdea(idea);
        participationResponses.setUser(user);
        participationResponses.setTheme(themes);
        participationResponses.setParticipationDate(new Date(1621080367));

        String jsonString = objectMapper.writeValueAsString(participationResponses);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(null);
        Mockito.when(this.daoUtils.isIdeaIDValid(idea.getIdeaID())).thenReturn(idea);
        Mockito.when(this.daoUtils.findThemeByID(themes.getThemeID())).thenReturn(themes);
        Mockito.when(this.participantService.alreadyParticipatedInAnIdeaResponse(participationResponses)).thenReturn(false);
        Mockito.when(this.participantService.isIdeaInTheme(participationResponses)).thenReturn(true);
        Mockito.when(this.participantService.registerForIdea(participationResponses)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/participant/participate")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void participateForIdea_themeNotFound() throws Exception {

        ResponseMessage<Object> responseMessage = new ResponseMessage<>();

        Ideas idea = new Ideas();
        idea.setIdeaID(1);

        Roles role = new Roles();
        role.setRoleID(3);

        User user = new User();
        user.setUserID(1);
        user.setRole(role);

        Themes themes = new Themes();
        themes.setThemeID(1);

        ParticipationResponses participationResponses = new ParticipationResponses();
        participationResponses.setIdea(idea);
        participationResponses.setUser(user);
        participationResponses.setTheme(themes);
        participationResponses.setParticipationDate(new Date(1621080367));

        String jsonString = objectMapper.writeValueAsString(participationResponses);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.daoUtils.isIdeaIDValid(idea.getIdeaID())).thenReturn(idea);
        Mockito.when(this.daoUtils.findThemeByID(themes.getThemeID())).thenReturn(null);
        Mockito.when(this.participantService.alreadyParticipatedInAnIdeaResponse(participationResponses)).thenReturn(false);
        Mockito.when(this.participantService.isIdeaInTheme(participationResponses)).thenReturn(true);
        Mockito.when(this.participantService.registerForIdea(participationResponses)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/participant/participate")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void participateForIdea_ideaNotInTheme() throws Exception {

        ResponseMessage<Object> responseMessage = new ResponseMessage<>();

        Ideas idea = new Ideas();
        idea.setIdeaID(1);

        Roles role = new Roles();
        role.setRoleID(3);

        User user = new User();
        user.setUserID(1);
        user.setRole(role);

        Themes themes = new Themes();
        themes.setThemeID(1);
        themes.setIsDeleted(IsDeleted.FALSE);

        ParticipationResponses participationResponses = new ParticipationResponses();
        participationResponses.setIdea(idea);
        participationResponses.setUser(user);
        participationResponses.setTheme(themes);
        participationResponses.setParticipationDate(new Date(1621080367));

        String jsonString = objectMapper.writeValueAsString(participationResponses);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.daoUtils.isIdeaIDValid(idea.getIdeaID())).thenReturn(idea);
        Mockito.when(this.daoUtils.findThemeByID(themes.getThemeID())).thenReturn(themes);
        Mockito.when(this.participantService.alreadyParticipatedInAnIdeaResponse(any())).thenReturn(false);
        Mockito.when(this.participantService.isIdeaInTheme(any())).thenReturn(false);
        Mockito.when(this.participantService.registerForIdea(any())).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/participant/participate")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void participateForIdea_jwtAuthFailure() throws Exception {

        ResponseMessage<Object> responseMessage = new ResponseMessage<>();

        Ideas idea = new Ideas();
        idea.setIdeaID(1);

        Roles role = new Roles();
        role.setRoleID(3);

        User user = new User();
        user.setUserID(1);
        user.setRole(role);

        Themes themes = new Themes();
        themes.setThemeID(1);

        ParticipationResponses participationResponses = new ParticipationResponses();
        participationResponses.setIdea(idea);
        participationResponses.setUser(user);
        participationResponses.setTheme(themes);
        participationResponses.setParticipationDate(new Date(1621080367));

        String jsonString = objectMapper.writeValueAsString(participationResponses);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.daoUtils.isIdeaIDValid(idea.getIdeaID())).thenReturn(idea);
        Mockito.when(this.daoUtils.findThemeByID(themes.getThemeID())).thenReturn(themes);
        Mockito.when(this.participantService.alreadyParticipatedInAnIdeaResponse(participationResponses)).thenReturn(false);
        Mockito.when(this.participantService.isIdeaInTheme(participationResponses)).thenReturn(true);
        Mockito.when(this.participantService.registerForIdea(participationResponses)).thenReturn(responseMessage);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/participant/participate")
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isForbidden());
    }
}