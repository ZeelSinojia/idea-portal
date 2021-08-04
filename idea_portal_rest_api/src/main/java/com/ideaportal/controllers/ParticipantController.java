package com.ideaportal.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideaportal.constants.IdeaPortalExceptionConstants;

import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.dto.ParticipationResponsesDTO;
import com.ideaportal.exceptions.IdeaAlreadyParticipatedException;
import com.ideaportal.exceptions.IdeaNotPresentInThemeException;
import com.ideaportal.exceptions.InvalidRoleException;
import com.ideaportal.exceptions.ThemeNotFoundException;
import com.ideaportal.exceptions.UserNotFoundException;
import com.ideaportal.models.*;
import com.ideaportal.services.ParticipantService;


import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "api/user/participant")
//Controller class for the participant
public class ParticipantController 
{        

    @Autowired
    ParticipantService participantService;

    @Autowired
    DAOUtils utils;

    @Autowired
    ModelMapper modelMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductManagerController.class);

    final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(value = "/participate", consumes={"application/json"})
    public ResponseEntity<ResponseMessage<Object>> participateForIdea(@RequestBody ParticipationResponsesDTO participationResponsesDTO) throws IdeaAlreadyParticipatedException, IdeaNotPresentInThemeException
    {

        LOGGER.info("Request URL: POST");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        try {
            LOGGER.info("Request Body: {}", objectMapper.writeValueAsString(participationResponsesDTO));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

        ParticipationResponses participationResponses = modelMapper.map(participationResponsesDTO, ParticipationResponses.class);

    	User user=utils.findByUserId(participationResponses.getUser().getUserID());
    	
    	Ideas idea=utils.isIdeaIDValid(participationResponses.getIdea().getIdeaID());
    	
    	Themes themes=utils.findThemeByID(participationResponses.getTheme().getThemeID());
    	
    	if(idea==null) {
            LOGGER.error("Idea with is not present in the database");
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }

        if (user == null) {
            LOGGER.error("User not found");
            throw new UserNotFoundException(IdeaPortalExceptionConstants.USER_NOT_FOUND);
        }
    	
    	if(themes==null || utils.findThemeByID(themes.getThemeID()).getIsDeleted().equals(IsDeleted.TRUE)) {
    	    LOGGER.error("Theme is either not present or marked as deleted in the database");
            throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
        }
 
        if(participantService.alreadyParticipatedInAnIdeaResponse(participationResponses)) {
            LOGGER.error("User with {} userID has already participated in the Idea with {} ideaID", user.getUserID(), idea.getIdeaID());
            throw new IdeaAlreadyParticipatedException(IdeaPortalExceptionConstants.IDEA_ALREADY_PARTICIPATED);
        }

        if(!participantService.isIdeaInTheme(participationResponses) ||
                utils.findIdeaByID(idea.getIdeaID()).getIsDeleted().equals(IsDeleted.TRUE)) {
            LOGGER.error("Idea with {} ideaID is either not present or marked as deleted in the database", idea.getIdeaID());
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_IN_THEME);
        }
        
        if(user.getRole().getRoleID()!=3 && user.getRole().getRoleID()!=2) {
            LOGGER.error("RoleID {} is not allowed to upload the files", user.getRole().getRoleID());
            throw new InvalidRoleException(IdeaPortalExceptionConstants.ROLE_NOT_FOUND);
        }

        if(participationResponses.getParticipantRoles() == null){
            LOGGER.error("Participation role must be present");
            throw new InvalidRoleException(IdeaPortalExceptionConstants.PARTICIPATION_ROLE_NOT_FOUND);
        }

        return new ResponseEntity<>(participantService.registerForIdea(participationResponses), HttpStatus.OK);
    }

}
