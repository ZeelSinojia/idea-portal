package com.ideaportal.controllers;

import java.io.File;


import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideaportal.constants.IdeaPortalResponseConstants;
import com.ideaportal.exceptions.*;
import com.ideaportal.models.*;
import com.ideaportal.services.ParticipantService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ideaportal.constants.IdeaPortalExceptionConstants;
import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.services.ProductManagerService;
import com.ideaportal.services.UserService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/api")
public class ProductManagerController {
	@Autowired
	UserService userService;

	@Value("${server.servlet.context-path}")
	private String contextPath;

	@Value("${server.domain}")
	private String domain;

	@Value("${server.port}")
	private String port;

	@Autowired
	ProductManagerService pmService;

	@Autowired
	ServletContext context;

	@Autowired
	DAOUtils utils;

	@Value("${aws.endpointUrl}")
	private String endpointUrl;

	@Value("${aws.bucketName}")
	private String bucketName;

	@Value("${storage}")
	private String storage;

	@Autowired
	private ParticipantService participantService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductManagerController.class);

	final ObjectMapper objectMapper = new ObjectMapper();

	@PostMapping(value = "/user/create/idea")
	public ResponseEntity<ResponseMessage<Ideas>> createNewIdea(@RequestParam ("userID") String userID,
																@RequestParam("themeID") String themeID,
																@RequestParam("ideaName") String ideaName, 
																@RequestParam("ideaDescription") String ideaDescription,
																@RequestParam(value = "files", required = false) MultipartFile [] files) throws RequestFailedException, IOException, URISyntaxException {

		LOGGER.info("Request URL: POST");
		LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

		User user = utils.findByUserId(Long.parseLong(userID));
		
		Themes themes=utils.findThemeByID(Long.parseLong(themeID));
		
		List<Artifacts> artifactList=new ArrayList<>();

		if (user == null) {
			LOGGER.error("User with {} userID not found", userID);
			throw new UserNotFoundException(IdeaPortalExceptionConstants.USER_NOT_FOUND);
		}
		if (user.getRole().getRoleID() != 2) {
			LOGGER.error("RoleID {} is not allowed to upload the files", user.getRole().getRoleID());
			throw new InvalidRoleException(IdeaPortalExceptionConstants.ROLE_NOT_FOUND);
		}

		if (themes == null) {
			LOGGER.error("Theme with {} themeID is not present in the database", themeID);
			throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
		}

		if(themes.getIsDeleted().equals(IsDeleted.TRUE)) {
			LOGGER.error("Theme with {} themeID is marked as deleted in the database", themeID);
			throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
		}

		if (utils.isIdeaNameSame(user.getUserID(), ideaName, Long.parseLong(themeID))) {
			LOGGER.error("{} is already present in the database", ideaName);
			throw new IdeaNameSameException(IdeaPortalExceptionConstants.SAME_IDEA_NAME);
		}

		String userName=user.getUserName();
		
		String cpUserName=themes.getUser().getUserName();
		
		Ideas idea =new Ideas();
		
		Timestamp timestamp=Timestamp.valueOf(LocalDateTime.now());
		
		idea.setIdeaDate(timestamp);
		idea.setIdeaModificationDate(null);
		idea.setIdeaDescription(ideaDescription);
		idea.setIdeaName(ideaName);
		idea.setTheme(themes);
		idea.setUser(user);
		idea.setModifiedBy(null);
		idea.setIsDeleted(IsDeleted.FALSE);

		ResponseMessage<Ideas> responseMessage = pmService.createNewIdeaResponseMessage(idea);

		String mainURL = null;
		String uploads_constant = null;

		if (storage.equals("local")) {
			mainURL = "http://" + domain + ":" + port + contextPath;
			uploads_constant = "Uploads" + File.separator + "Themes" + File.separator + cpUserName + File.separator +
					themeID + File.separator + "Ideas" + File.separator + userName + File.separator +
					responseMessage.getResult().getIdeaID() + File.separator + timestamp.getTime();
		}
		else if (storage.equals("aws")){
			mainURL = "https://" + bucketName + "." + endpointUrl;
			uploads_constant = "Uploads" + "/" + "Themes" + "/" + cpUserName + "/" + themeID + "/" + "Ideas" + "/" +
					userName + "/" + responseMessage.getResult().getIdeaID() + "/" + timestamp.getTime();
		}
		if(files!=null) {
			for (MultipartFile myFile : files) {
				if (!myFile.isEmpty()) {
					if (storage.equals("local")) {
						boolean dirStatus = false;
						File dir = new File(context.getRealPath(uploads_constant));
						if (!dir.exists())
							dirStatus = dir.mkdirs();
						if (dirStatus)
							LOGGER.info("Directory created successfully");
						else
							LOGGER.info("Directory was not created");
						boolean saveStatus = userService.saveFile(myFile, dir);
						if (saveStatus)
							LOGGER.info("File saved at local machine successfully");
						else {
							LOGGER.info("Unable to save files locally");
							pmService.deleteIdea(responseMessage.getResult().getIdeaID());
							throw new RequestFailedException(IdeaPortalResponseConstants.FILES_NOT_UPLOADED);
						}
					} else if (storage.equals("aws")) {
						boolean uploadFileStatus = userService.uploadFile(myFile, uploads_constant);
						if (uploadFileStatus) {
							LOGGER.info("Files were uploaded successfully to the AWS");
						} else {
							LOGGER.info("Files were not uploaded to the AWS");
							pmService.deleteIdea(responseMessage.getResult().getIdeaID());
							throw new RequestFailedException(IdeaPortalResponseConstants.FILES_NOT_UPLOADED);
						}
					}
					String fileName = myFile.getOriginalFilename();
					Artifacts artifact = new Artifacts();

					artifact.setIdea(idea);
					artifact.setUser(user);
					if (storage.equals("local"))
						artifact.setArtifactURL(mainURL + File.separator + uploads_constant + File.separator + fileName);
					else if (storage.equals("aws")) {
						String encodedString = null;
						try {
							encodedString = URLEncoder.encode(fileName, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							LOGGER.info("UnsupportedEncodingException encountered");
						}
						artifact.setArtifactURL(mainURL + "/" + uploads_constant + "/" + encodedString);
					}
					artifact.setFileType(FilenameUtils.getExtension(fileName));
					artifact.setArtifactCreationDate(timestamp);
					artifact.setOriginalFileName(fileName);
					artifact.setIsModified(IsModified.FALSE);

					artifactList.add(artifact);
				}
			}
		}
		pmService.saveArtifacts(artifactList, responseMessage.getResult().getIdeaID());

		LOGGER.info("Response Code: {}", responseMessage.getStatus());
		try {
			LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
		}
		catch (JsonProcessingException e){
			LOGGER.error("JsonProcessingException: Can't convert User Object to String");
		}

		return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
	}

	@GetMapping(value = "/user/myideas/{userID}")
	public ResponseEntity<ResponseMessage<List<Ideas>>> getMySubmittedIdeas(@PathVariable("userID") long userID) {
		LOGGER.info("Request URL: GET");
		LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
		User user = utils.findByUserId(userID);
		if (user == null) {
			LOGGER.error("User with {} userID is not present in the database", userID);
			throw new UserNotFoundException(IdeaPortalExceptionConstants.USER_NOT_FOUND);
		}
		if (user.getRole().getRoleID() != 2) {
			LOGGER.error("RoleID {} is not allowed to get the files", user.getRole().getRoleID());
			throw new InvalidRoleException(IdeaPortalExceptionConstants.ROLE_NOT_FOUND);
		}

		ResponseMessage<List<Ideas>> responseMessage = pmService.getMySubmittedIdeasResponseMessage(userID);

		return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
	}

	@PutMapping(value = "/user/update/idea/{ideaID}")
	public ResponseEntity<ResponseMessage<Ideas>> updateIdea(@RequestParam ("userID") String userID,
																@RequestParam("themeID") String themeID,
																@RequestParam("ideaName") String ideaName,
																@RequestParam("ideaDescription") String ideaDescription,
																@RequestParam(value = "files", required = false) MultipartFile [] files,
															 	@PathVariable("ideaID") long ideaID) throws RequestFailedException {

		LOGGER.info("Request URL: PUT");
		LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

		User user = utils.findByUserId(Long.parseLong(userID));

		Themes themes = utils.findThemeByID(Long.parseLong(themeID));

		List<Artifacts> artifactList = new ArrayList<>();

		if (user == null) {
			LOGGER.error("User with {} userID not found", userID);
			throw new UserNotFoundException(IdeaPortalExceptionConstants.USER_NOT_FOUND);
		}

		if (user.getRole().getRoleID() != 2) {
			LOGGER.error("RoleID {} is not allowed to upload the files", user.getRole().getRoleID());
			throw new InvalidRoleException(IdeaPortalExceptionConstants.ROLE_NOT_FOUND);
		}

		if (themes == null) {
			LOGGER.error("Theme with {} themeID is not present in the database", themeID);
			throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
		}

		if (themes.getIsDeleted().equals(IsDeleted.TRUE)) {
			LOGGER.error("Theme with {} themeID is marked as deleted in the database", themeID);
			throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
		}

		if (utils.isIdeaNameUpdatable(user.getUserID(), ideaName, Long.parseLong(themeID), ideaID)) {
			LOGGER.error("{} is already present in the database", ideaName);
			throw new IdeaNameSameException(IdeaPortalExceptionConstants.SAME_IDEA_NAME);
		}

		boolean fileSaveFail = false;

		String userName = user.getUserName();

		String cpUserName = themes.getUser().getUserName();


		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());


		Ideas idea = utils.findIdeaByID(ideaID);
		idea.setIdeaModificationDate(timestamp);
		idea.setIdeaDescription(ideaDescription);
		idea.setIdeaName(ideaName);
		idea.setModifiedBy(user);

		String mainURL = null;
		String uploads_constant = null;

		if (storage.equals("local")) {
			mainURL = "http://" + domain + ":" + port + contextPath;
			uploads_constant = "Uploads" + File.separator + "Themes" + File.separator + cpUserName + File.separator +
					themeID + File.separator + "Ideas" + File.separator + userName + File.separator +
					ideaID + File.separator + timestamp.getTime();
		} else if (storage.equals("aws")) {
			mainURL = "https://" + bucketName + "." + endpointUrl;
			uploads_constant = "Uploads" + "/" + "Themes" + "/" + cpUserName + "/" + themeID + "/" + "Ideas" + "/" +
					userName + "/" + ideaID + "/" + timestamp.getTime();
		}

		utils.markAsModified(utils.findArtifactByIdeaID(ideaID));
		if(files!=null)
		{
			for (MultipartFile myFile : files) {
				if (!myFile.isEmpty()) {
					if (storage.equals("local")) {
						boolean dirStatus = false;
						File dir = new File(context.getRealPath(uploads_constant));
						if (!dir.exists())
							dirStatus = dir.mkdirs();
						if (dirStatus)
							LOGGER.info("Directory created successfully");
						else
							LOGGER.info("Directory was not created");
						boolean saveStatus = userService.saveFile(myFile, dir);
						if (saveStatus)
							LOGGER.info("File saved at local machine successfully");
						else {
							LOGGER.info("Unable to save files locally");
							fileSaveFail = true;
						}
					} else if (storage.equals("aws")) {
						boolean uploadFileStatus = userService.uploadFile(myFile, uploads_constant);
						if (uploadFileStatus) {
							LOGGER.info("Files were uploaded successfully to the AWS");
						} else {
							LOGGER.info("Files were not uploaded to the AWS");
							fileSaveFail = true;
						}
					}
					String fileName = myFile.getOriginalFilename();
					Artifacts artifact = new Artifacts();

					artifact.setIdea(idea);
					artifact.setUser(user);
					if (storage.equals("local"))
						artifact.setArtifactURL(mainURL + File.separator + uploads_constant + File.separator + fileName);
					else if (storage.equals("aws")) {
						String encodedString = null;
						try {
							encodedString = URLEncoder.encode(fileName, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							LOGGER.info("UnsupportedEncodingException encountered");
						}
						artifact.setArtifactURL(mainURL + "/" + uploads_constant + "/" + encodedString);
					}
					artifact.setFileType(FilenameUtils.getExtension(fileName));
					artifact.setArtifactCreationDate(timestamp);
					artifact.setOriginalFileName(fileName);
					artifact.setIsModified(IsModified.FALSE);

					artifactList.add(artifact);
				}
			}
		}


		ResponseMessage<Ideas> responseMessage = pmService.updateIdeaResponseMessage(ideaID, idea, artifactList);

		if(fileSaveFail){
			throw new RequestFailedException(IdeaPortalResponseConstants.FILES_NOT_UPLOADED);
		}

		LOGGER.info("Response Code: {}", responseMessage.getStatus());
		try {
			LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
		}
		catch (JsonProcessingException e){
			LOGGER.error("JsonProcessingException: Can't convert User Object to String");
		}

		return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
	}

	@DeleteMapping(value = "/user/delete/idea/{ideaID}")
	public ResponseEntity<ResponseMessage<Ideas>> deleteReview(@PathVariable("ideaID") long ideaID) throws IdeaNotPresentInThemeException {

		LOGGER.info("Request URL: DELETE");
		LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

		if (utils.findIdeaByID(ideaID) == null) {
			LOGGER.error("Idea with {} ideaID is not present in the database", ideaID);
			throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
		}

		if(utils.findIdeaByID(ideaID).getIsDeleted().equals(IsDeleted.TRUE)) {
			LOGGER.error("Idea with {} ideaID is marked as deleted in the database", ideaID);
			throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
		}

		ResponseMessage<Ideas> responseMessage = pmService.deleteIdea(ideaID);

		LOGGER.info("Response Code: {}", responseMessage.getStatus());
		try {
			LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
		}
		catch (JsonProcessingException e){
			LOGGER.error("JsonProcessingException: Can't convert User Object to String");
		}

		return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
	}
}