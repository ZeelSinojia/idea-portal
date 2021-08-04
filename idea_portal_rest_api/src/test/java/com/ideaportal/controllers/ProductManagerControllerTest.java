package com.ideaportal.controllers;

import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.models.*;
import com.ideaportal.services.ParticipantService;
import com.ideaportal.services.ProductManagerService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductManagerController.class)
class ProductManagerControllerTest {



    @MockBean
    ProductManagerService productManagerService;

    @MockBean
    UserService userService;

    @MockBean
    DAOUtils daoUtils;

    @MockBean
    ParticipantService participantService;

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
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + jwtExpirationTime))
                .compact(); //builds the token
    }

    @Test
    void uploadReviews_success() throws Exception{


        Roles role = new Roles();
        role.setRoleID(2);

        User user = new User();
        user.setRole(role);

        Themes themes = new Themes();
        themes.setUser(user);
        themes.setIsDeleted(IsDeleted.FALSE);

        Ideas idea = new Ideas();
        ResponseMessage<Ideas> responseMessage = new ResponseMessage<>();
        responseMessage.setResult(idea);
        responseMessage.setStatus(HttpStatus.CREATED.value());



        String ideaName = "test name";
        String ideaDescription = "test theme description";
        
        List<Artifacts> list=new ArrayList<>();
        
        list.add(new Artifacts(1, null, idea, user, "Test URL"));
        
        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.daoUtils.findThemeByID(themes.getThemeID())).thenReturn(themes);
        Mockito.when(this.daoUtils.isIdeaNameSame(user.getUserID(), ideaName, themes.getThemeID())).thenReturn(false);
        Mockito.when(this.productManagerService.createNewIdeaResponseMessage(idea)).thenReturn(responseMessage);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create/idea")
                .param("userID",Long.toString(user.getUserID()))
                .param("themeID", Long.toString(themes.getThemeID()))
                .param("ideaName", ideaName)
                .param("ideaDescription", ideaDescription)
                .param("files", Arrays.toString((Object[]) null))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isCreated());
    }

    @Test
    void uploadReviews_userNotFound() throws Exception{
        ResponseMessage<Ideas> responseMessage = new ResponseMessage<>();

        Roles role = new Roles();
        role.setRoleID(2);

        User user = new User();
        user.setRole(role);

        Themes themes = new Themes();
        themes.setUser(user);

        Ideas idea = new Ideas();

        String ideaName = "test name";
        String ideaDescription = "test theme description";

        List<Artifacts> list=new ArrayList<>();
        
        list.add(new Artifacts(1, null, idea, user, "Test URL"));
        
        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(null);
        Mockito.when(this.daoUtils.findThemeByID(themes.getThemeID())).thenReturn(themes);
        Mockito.when(this.daoUtils.isIdeaNameSame(user.getUserID(), ideaName, themes.getThemeID())).thenReturn(false);
        Mockito.when(this.productManagerService.createNewIdeaResponseMessage(idea)).thenReturn(responseMessage);

        

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create/idea")
                .param("userID",Long.toString(user.getUserID()))
                .param("themeID", Long.toString(themes.getThemeID()))
                .param("ideaName", ideaName)
                .param("ideaDescription", ideaDescription)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadReviews_invalidRole() throws Exception{
        ResponseMessage<Ideas> responseMessage = new ResponseMessage<>();

        Roles role = new Roles();
        role.setRoleID(1);

        User user = new User();
        user.setRole(role);

        Themes themes = new Themes();
        themes.setUser(user);

        Ideas idea = new Ideas();


        String ideaName = "test name";
        String ideaDescription = "test theme description";
        
        List<Artifacts> list=new ArrayList<>();
        
        list.add(new Artifacts(1, null, idea, user, "Test URL"));

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.daoUtils.findThemeByID(themes.getThemeID())).thenReturn(themes);
        Mockito.when(this.daoUtils.isIdeaNameSame(user.getUserID(), ideaName, themes.getThemeID())).thenReturn(false);
        Mockito.when(this.productManagerService.createNewIdeaResponseMessage(idea)).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create/idea")
                .param("userID",Long.toString(user.getUserID()))
                .param("themeID", Long.toString(themes.getThemeID()))
                .param("ideaName", ideaName)
                .param("ideaDescription", ideaDescription)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadReviews_themeNotFound() throws Exception{
        ResponseMessage<Ideas> responseMessage = new ResponseMessage<>();

        Roles role = new Roles();
        role.setRoleID(2);

        User user = new User();
        user.setRole(role);

        Themes themes = new Themes();
        themes.setUser(user);

        Ideas idea = new Ideas();

        String ideaName = "test name";
        String ideaDescription = "test theme description";
        
        List<Artifacts> list=new ArrayList<>();
        
        list.add(new Artifacts(1, null, idea, user, "Test URL"));
        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.daoUtils.findThemeByID(themes.getThemeID())).thenReturn(null);
        Mockito.when(this.daoUtils.isIdeaNameSame(user.getUserID(), ideaName, themes.getThemeID())).thenReturn(false);
        Mockito.when(this.productManagerService.createNewIdeaResponseMessage(idea)).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create/idea")
                .param("userID",Long.toString(user.getUserID()))
                .param("themeID", Long.toString(themes.getThemeID()))
                .param("ideaName", ideaName)
                .param("ideaDescription", ideaDescription)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadReviews_sameIdeaName() throws Exception{
        ResponseMessage<Ideas> responseMessage = new ResponseMessage<>();

        Roles role = new Roles();
        role.setRoleID(2);

        User user = new User();
        user.setRole(role);

        Themes themes = new Themes();
        themes.setUser(user);
        themes.setIsDeleted(IsDeleted.FALSE);

        Ideas idea = new Ideas();

        String ideaName = "test name";
        String ideaDescription = "test theme description";
        
        List<Artifacts> list=new ArrayList<>();
        
        list.add(new Artifacts(1, null, idea, user, "Test URL"));

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.daoUtils.findThemeByID(themes.getThemeID())).thenReturn(themes);
        Mockito.when(this.daoUtils.isIdeaNameSame(user.getUserID(), ideaName, themes.getThemeID())).thenReturn(true);
        Mockito.when(this.productManagerService.createNewIdeaResponseMessage(idea)).thenReturn(responseMessage);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create/idea")
                .param("userID",Long.toString(user.getUserID()))
                .param("themeID", Long.toString(themes.getThemeID()))
                .param("ideaName", ideaName)
                .param("ideaDescription", ideaDescription)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadReviews_jwtAuthFailure() throws Exception{
        ResponseMessage<Ideas> responseMessage = new ResponseMessage<>();

        Roles role = new Roles();
        role.setRoleID(2);

        User user = new User();
        user.setRole(role);

        Themes themes = new Themes();
        themes.setUser(user);

        Ideas idea = new Ideas();

        String ideaName = "test name";
        String ideaDescription = "test theme description";
        
        List<Artifacts> list=new ArrayList<>();	
        
        list.add(new Artifacts(1, null, idea, user, "Test URL"));

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.daoUtils.findThemeByID(themes.getThemeID())).thenReturn(themes);
        Mockito.when(this.daoUtils.isIdeaNameSame(user.getUserID(), ideaName,themes.getThemeID())).thenReturn(false);
        Mockito.when(this.productManagerService.createNewIdeaResponseMessage(idea)).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/user/create/idea")
                .param("userID",Long.toString(user.getUserID()))
                .param("themeID", Long.toString(themes.getThemeID()))
                .param("ideaName", ideaName)
                .param("ideaDescription", ideaDescription)
                .accept(MediaType.APPLICATION_JSON))
//                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMySubmittedIdeas_success() throws Exception{
        ResponseMessage<List<Ideas>> responseMessage = new ResponseMessage<>();
        responseMessage.setStatus(HttpStatus.OK.value());

        Roles roles = new Roles();
        roles.setRoleID(2);

        User user = new User();
        user.setUserID(3);
        user.setRole(roles);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.productManagerService.getMySubmittedIdeasResponseMessage(user.getUserID())).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/myideas/" + user.getUserID())
                .param("userID", String.valueOf(user.getUserID()))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isOk());
    }

    @Test
    void getMySubmittedIdeas_userNotFound() throws Exception{
        ResponseMessage<List<Ideas>> responseMessage = new ResponseMessage<>();

        Roles roles = new Roles();
        roles.setRoleID(2);

        User user = new User();
        user.setUserID(3);
        user.setRole(roles);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(null);
        Mockito.when(this.productManagerService.getMySubmittedIdeasResponseMessage(user.getUserID())).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/myideas/" + user.getUserID())
                .param("userID", String.valueOf(user.getUserID()))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMySubmittedIdeas_invalidRole() throws Exception{
        ResponseMessage<List<Ideas>> responseMessage = new ResponseMessage<>();

        Roles roles = new Roles();
        roles.setRoleID(3);

        User user = new User();
        user.setUserID(3);
        user.setRole(roles);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.productManagerService.getMySubmittedIdeasResponseMessage(user.getUserID())).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/myideas/" + user.getUserID())
                .param("userID", String.valueOf(user.getUserID()))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMySubmittedIdeas_jwtAuthFailure() throws Exception{
        ResponseMessage<List<Ideas>> responseMessage = new ResponseMessage<>();

        Roles roles = new Roles();
        roles.setRoleID(2);

        User user = new User();
        user.setUserID(3);
        user.setRole(roles);

        Mockito.when(this.daoUtils.findByUserId(user.getUserID())).thenReturn(user);
        Mockito.when(this.productManagerService.getMySubmittedIdeasResponseMessage(user.getUserID())).thenReturn(responseMessage);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/myideas/" + user.getUserID())
                .param("userID", String.valueOf(user.getUserID()))
                .accept(MediaType.APPLICATION_JSON))
//                .header("Authorization", "Bearer " + this.generateJWT()))
                .andExpect(status().isForbidden());
    }
}
