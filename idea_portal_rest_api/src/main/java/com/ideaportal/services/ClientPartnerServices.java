package com.ideaportal.services;


import com.ideaportal.models.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ideaportal.constants.IdeaPortalResponseConstants;
import com.ideaportal.dao.ClientPartnerDAO;



import java.util.List;

@Service
public class ClientPartnerServices {

	@Autowired
	ClientPartnerDAO clientPartnerDAO;

	@Value("${aws.endpointUrl}")
	private String endpointUrl;

	@Value("${aws.bucketName}")
	private String bucketName;

	@Value("${aws.accessKey}")
	private String accessKey;

	@Value("${aws.secretKey}")
	private String secretKey;

	@Value("${aws.region}")
	private String region;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientPartnerServices.class);

	public ResponseMessage<Themes> saveTheme(Themes themes)
	{
		ResponseMessage<Themes> responseMessage=new ResponseMessage<>();
		responseMessage.setResult(clientPartnerDAO.saveTheme(themes));
		responseMessage.setStatus(HttpStatus.CREATED.value());
		responseMessage.setStatusText(IdeaPortalResponseConstants.THEME_CREATE_SUCCESS);
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<Themes> updateTheme(long themeID, Themes themes, List<Artifacts> list)
	{
		ResponseMessage<Themes> responseMessage=new ResponseMessage<>();
		responseMessage.setResult(clientPartnerDAO.update(themeID, themes, list));
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setStatusText(IdeaPortalResponseConstants.THEME_UPDATE_SUCCESS);
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<List<ParticipationResponses>> getParticipantResponsesForIdeaResponseMessage(long ideaID)
	{
		List<ParticipationResponses> list=clientPartnerDAO.getParticipantResponsesListForIdea(ideaID);

		ResponseMessage<List<ParticipationResponses>> responseMessage=new ResponseMessage<>();

		int size=list.size();
		if(size==0)
		{
			LOGGER.info("No participants were found for the provided idea");
			responseMessage.setResult(null);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.NO_PARTICIPANTS);

		}

		else
		{
			responseMessage.setResult(list);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.LIST_PARTICIPANTS);
			responseMessage.setTotalElements(size);

		}
		return responseMessage;
	}

	public ResponseMessage<List<Themes>> getMyCreatedThemesResponseMessage(long userID) 
	{
		ResponseMessage<List<Themes>> responseMessage=new ResponseMessage<>();
		
		List<Themes> list=clientPartnerDAO.getMyThemesList(userID);
		
		int size=list.size();
		
		if(size==0)
		{
			LOGGER.info("No themes were found for the user with {} userID", userID);
			responseMessage.setResult(null);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.YOU_POSTED_NO_THEMES);
			responseMessage.setTotalElements(0);
			
		}
		else
		{
			responseMessage.setResult(list);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.LIST_ALL_THEMES);
			responseMessage.setTotalElements(size);
			
		}
		
		return responseMessage;
	}

	public void saveArtifacts(List<Artifacts> artifactList, long themeID) {
		clientPartnerDAO.saveArtifacts(artifactList, themeID);
	}

	public ResponseMessage<Themes> deleteTheme(long themeID) {
		ResponseMessage<Themes> responseMessage=new ResponseMessage<>();
		responseMessage.setResult(clientPartnerDAO.deleteTheme(themeID));
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setStatusText(IdeaPortalResponseConstants.THEME_DELETE_SUCCESS);
		responseMessage.setTotalElements(1);
		return responseMessage;
	}


}
