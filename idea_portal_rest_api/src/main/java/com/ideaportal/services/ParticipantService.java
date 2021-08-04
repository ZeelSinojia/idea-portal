package com.ideaportal.services;

import com.ideaportal.constants.IdeaPortalResponseConstants;
import com.ideaportal.dao.ParticipantDAO;

import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.models.ParticipationResponses;
import com.ideaportal.models.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService {

    @Autowired
    ParticipantDAO participantDAO;
    @Autowired
    DAOUtils utils;


    public ResponseMessage<Object> registerForIdea(ParticipationResponses participationResponses)
    {
        ResponseMessage<Object> responseMessage = new ResponseMessage<>();
        responseMessage.setResult(participantDAO.saveParticipantForIdea(participationResponses));
        responseMessage.setStatus(HttpStatus.OK.value());
        responseMessage.setStatusText(IdeaPortalResponseConstants.PARTICIPATION_SUCCESS);
        responseMessage.setTotalElements(1);
        return responseMessage;
    }

    public boolean alreadyParticipatedInAnIdeaResponse(ParticipationResponses participationResponses) 
    {
        return utils.isAlreadyParticipated(participationResponses);
    }

    public boolean isIdeaInTheme(ParticipationResponses participationResponses) {
        return utils.isIdeaInTheme(participationResponses);
    }
}
