package com.ideaportal.services;


import java.io.IOException;

import java.net.URISyntaxException;

import java.util.List;

import com.ideaportal.dao.UserDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ideaportal.constants.IdeaPortalResponseConstants;
import com.ideaportal.dao.ProductManagerDAO;
import com.ideaportal.models.Artifacts;
import com.ideaportal.models.Ideas;
import com.ideaportal.models.ResponseMessage;

@Service
public class ProductManagerService {

	@Autowired
	ProductManagerDAO pmDAO;

	@Value("${frontend.domain}")
	public String frontEndDomain;

	@Value("${frontend.port}")
	public String frontEndPort;

	@Value("${storage}")
	private String storage;

	@Value("${server.domain}")
	private String domain;

	@Value("${server.port}")
	private String port;

	@Autowired
	UserService userService;

	@Value("${aws.endpointUrl}")
	private String endpointUrl;

	@Value("${aws.bucketName}")
	private String bucketName;

	@Autowired
	UserDAO userDAO;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductManagerService.class);

	public ResponseMessage<Ideas> createNewIdeaResponseMessage(Ideas ideas) throws IOException, URISyntaxException {
		ResponseMessage<Ideas> responseMessage=new ResponseMessage<>();
		
		responseMessage.setResult(pmDAO.createNewIdeaObject(ideas));
		responseMessage.setStatus(HttpStatus.CREATED.value());
		responseMessage.setStatusText(IdeaPortalResponseConstants.IDEA_CREATE_SUCCESS);
		responseMessage.setTotalElements(1);

		return responseMessage;
		
	}

	public ResponseMessage<Ideas> updateIdeaResponseMessage(long ideaID, Ideas ideas, List<Artifacts> list)
	{
		ResponseMessage<Ideas> responseMessage=new ResponseMessage<>();

		responseMessage.setResult(pmDAO.updateIdeaObject(ideaID, ideas, list));
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setStatusText(IdeaPortalResponseConstants.IDEA_UPDATE_SUCCESS);
		responseMessage.setTotalElements(1);
		return responseMessage;

	}


	public ResponseMessage<List<Ideas>> getMySubmittedIdeasResponseMessage(long userID) 
	{
		ResponseMessage<List<Ideas>> responseMessage=new ResponseMessage<>();
		
		List<Ideas> list=pmDAO.getMyIdeasList(userID);
		
		int size=list.size();
		
		if(size==0)
		{
			LOGGER.info("No themes were found for the user with {} userID", userID);
			responseMessage.setResult(null);
			responseMessage.setStatusText(IdeaPortalResponseConstants.YOU_POSTED_NO_IDEAS);
		}
		else
		{
			responseMessage.setResult(list);
			responseMessage.setStatusText(IdeaPortalResponseConstants.LIST_ALL_IDEAS);
		}
		responseMessage.setTotalElements(list.size());
		responseMessage.setStatus(HttpStatus.OK.value());
		return responseMessage;
	}

	public void saveArtifacts(List<Artifacts> artifactList, long ideaID) {
		pmDAO.saveArtifacts(artifactList, ideaID);
	}

    public ResponseMessage<Ideas> deleteIdea(long ideaID) {
		ResponseMessage<Ideas> responseMessage=new ResponseMessage<>();
		responseMessage.setResult(pmDAO.deleteIdea(ideaID));
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setStatusText(IdeaPortalResponseConstants.IDEA_DELETE_SUCCESS);
		responseMessage.setTotalElements(1);
		return responseMessage;
    }

}
