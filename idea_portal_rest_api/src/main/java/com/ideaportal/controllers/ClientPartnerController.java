package com.ideaportal.controllers;

import java.io.File;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideaportal.constants.IdeaPortalResponseConstants;
import com.ideaportal.dao.ProductManagerDAO;
import com.ideaportal.exceptions.*;
import com.ideaportal.models.*;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ideaportal.constants.IdeaPortalExceptionConstants;
import com.ideaportal.dao.utils.DAOUtils;

import com.ideaportal.services.ClientPartnerServices;
import com.ideaportal.services.UserService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class ClientPartnerController {

	@Autowired
	ClientPartnerServices clientPartnerServices;
	@Autowired
	UserService userService;

	@Value("${server.servlet.context-path}")
	private String contextPath;

	@Value("${server.domain}")
	private String domain;

	@Value("${server.port}")
	private String port;

	@Autowired
	ServletContext context;
	
	@Autowired
	DAOUtils utils;
	@Autowired
	ServletConfig servelet;
	@Autowired
	ProductManagerDAO productManagerDAO;

	@Value("${aws.endpointUrl}")
	private String endpointUrl;

	@Value("${aws.bucketName}")
	private String bucketName;

	@Value("${storage}")
	private String storage;

	final ObjectMapper objectMapper = new ObjectMapper();

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientPartnerController.class);

	@PostMapping(value = "/user/create/theme")
	public ResponseEntity<ResponseMessage<Themes>> uploadReviews(@RequestParam(value="files", required = false) MultipartFile [] files,
																@RequestParam ("userID") String userID, 
																@RequestParam("themeName") String themeName,
																@RequestParam("themeCategory") long themeCategory,
																@RequestParam("themeDescription") String themeDesc) throws ThemeNameSameException, InvalidRoleException, RequestFailedException {


		LOGGER.info("Request URL: POST");
		LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());


		List<Artifacts> artifactList=new ArrayList<>();
		
		User dbUser=utils.findByUserId(Long.parseLong(userID));
		
		if (dbUser == null) {
			LOGGER.error("User with {} userID not found", userID);
			throw new UserNotFoundException(IdeaPortalExceptionConstants.USER_NOT_FOUND);
		}
		if (dbUser.getRole().getRoleID() != 1) {
			LOGGER.error("RoleID {} is not allowed to upload the files", dbUser.getRole().getRoleID());
			throw new InvalidRoleException(IdeaPortalExceptionConstants.ROLE_NOT_FOUND);
		}
		if (utils.isThemeNameSame(dbUser.getUserID(), themeName)) {
			LOGGER.error("{} is already present in the database", themeName);
			throw new ThemeNameSameException(IdeaPortalExceptionConstants.SAME_THEME_NAME);
		}
		if(utils.isCountExceeding(Long.parseLong(userID))) {
			LOGGER.error("User already has created maximum number of themes allowed");
			throw new ThemeCountExceedsException(IdeaPortalExceptionConstants.THEME_COUNT_EXCEEDS);
		}
		Timestamp timestamp=Timestamp.valueOf(LocalDateTime.now());

		ThemesCategory themesCategory = utils.findThemeCategory(themeCategory);

		Themes themes = new Themes();
		themes.setThemeName(themeName);
		themes.setThemeDescription(themeDesc);
		themes.setThemeDate(timestamp);
		themes.setUser(dbUser);
		themes.setThemeModificationDate(null);
		themes.setModifiedBy(null);
		themes.setIsDeleted(IsDeleted.FALSE);
		themes.setThemesCategory(themesCategory);

		String userName=dbUser.getUserName();




		ResponseMessage<Themes> responseMessage = clientPartnerServices.saveTheme(themes);

		String mainURL = null;
		String uploads_constant = null;

		if (storage.equals("local")) {
			mainURL = "http://" + domain + ":" + port + contextPath;

			uploads_constant = "Uploads" + File.separator + "Themes" + File.separator + userName + File.separator +
					responseMessage.getResult().getThemeID() + File.separator + timestamp.getTime();
		}
		else if(storage.equals("aws")){
			mainURL = "https://" + bucketName + "." + endpointUrl;

			uploads_constant = "Uploads" + "/" + "Themes" + "/" + userName + "/" +
					responseMessage.getResult().getThemeID() + "/" + timestamp.getTime();
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
							clientPartnerServices.deleteTheme(responseMessage.getResult().getThemeID());
							throw new RequestFailedException(IdeaPortalResponseConstants.FILES_NOT_SAVED);
						}
					} else if (storage.equals("aws")) {
						boolean uploadFileStatus = userService.uploadFile(myFile, uploads_constant);
						if (uploadFileStatus) {
							LOGGER.info("Files were uploaded successfully to the AWS");
						} else {
							LOGGER.info("Files were not uploaded to the AWS");
							clientPartnerServices.deleteTheme(responseMessage.getResult().getThemeID());
							throw new RequestFailedException(IdeaPortalResponseConstants.FILES_NOT_UPLOADED);
						}
					}
					String fileName = myFile.getOriginalFilename();
					Artifacts artifact = new Artifacts();


					artifact.setTheme(themes);
					artifact.setIdea(null);
					artifact.setUser(dbUser);
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

		clientPartnerServices.saveArtifacts(artifactList, responseMessage.getResult().getThemeID());



		LOGGER.info("Response Code: {}", responseMessage.getStatus());
		try {
			LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
		}
		catch (JsonProcessingException e){
			LOGGER.error("JsonProcessingException: Can't convert User Object to String");
		}

		return new ResponseEntity<>(responseMessage, new HttpHeaders(),  HttpStatus.valueOf(responseMessage.getStatus()));
	}

	@GetMapping(value = "/user/idea/{ideaID}/participantResponses")
	public ResponseEntity<ResponseMessage<List<ParticipationResponses>>> getParticipantResponsesForAnIdea(@PathVariable("ideaID") long ideaID)
			throws IdeaNotPresentInThemeException
	{
		LOGGER.info("Request URL: GET");
		LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
		Ideas idea = utils.isIdeaIDValid(ideaID);
		if (idea == null) {
			LOGGER.error("Idea with {} ideaID is not present in the database", ideaID);
			throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
		}
		ResponseMessage<List<ParticipationResponses>> responseMessage = clientPartnerServices.getParticipantResponsesForIdeaResponseMessage(ideaID);
		LOGGER.info("Response Code: {}", responseMessage.getStatus());
		try {
			LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
		}
		catch (JsonProcessingException e){
			LOGGER.error("JsonProcessingException: Can't convert User Object to String");
		}
		return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
	}


	@GetMapping(value = "/user/mythemes/{userID}")
	public ResponseEntity<ResponseMessage<List<Themes>>> getMyCreatedThemes(@PathVariable("userID") long userID) {
		LOGGER.info("Request URL: GET");
		LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
		User user = utils.findByUserId(userID);
		if (user == null) {
			LOGGER.error("User with {} userID is not present in the database", userID);
			throw new UserNotFoundException(IdeaPortalExceptionConstants.USER_NOT_FOUND);
		}
		if (user.getRole().getRoleID() != 1) {
			LOGGER.error("RoleID {} is not allowed to get the files", user.getRole().getRoleID());
			throw new InvalidRoleException(IdeaPortalExceptionConstants.ROLE_NOT_FOUND);
		}

		ResponseMessage<List<Themes>> responseMessage = clientPartnerServices.getMyCreatedThemesResponseMessage(userID);
		LOGGER.info("Response Code: {}", responseMessage.getStatus());
		try {
			LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
		}
		catch (JsonProcessingException e){
			LOGGER.error("JsonProcessingException: Can't convert User Object to String");
		}
		return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
	}

	@PutMapping(value = "/user/update/theme/{themeID}")
	public ResponseEntity<ResponseMessage<Themes>> updateReviews(@RequestParam(value="files", required = false) MultipartFile [] files,
																 @RequestParam ("userID") String userID,
																 @RequestParam("themeName") String themeName,
																 @RequestParam("themeCategory") long themeCategory,
																 @RequestParam("themeDescription") String themeDesc,
																 @PathVariable("themeID") long themeID) throws RequestFailedException {
		LOGGER.info("Request URL: PUT");
		LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

		List<Artifacts> artifactList=new ArrayList<>();

		User dbUser=utils.findByUserId(Long.parseLong(userID));

		if (dbUser == null) {
			LOGGER.error("User with {} userID not found", userID);
			throw new UserNotFoundException(IdeaPortalExceptionConstants.USER_NOT_FOUND);
		}
		if (dbUser.getRole().getRoleID() != 1) {
			LOGGER.error("RoleID {} is not allowed to upload the files", dbUser.getRole().getRoleID());
			throw new InvalidRoleException(IdeaPortalExceptionConstants.ROLE_NOT_FOUND);
		}

		if (utils.isThemeNameUpdatable(dbUser.getUserID(), themeName, themeID)) {
			LOGGER.error("{} is already present in the database", themeName);
			throw new ThemeNameSameException(IdeaPortalExceptionConstants.SAME_THEME_NAME);
		}

		if(utils.findThemeByID(themeID).getIsDeleted().equals(IsDeleted.TRUE)) {
			LOGGER.error("Theme with {} themeID is marked as deleted in the database", themeID);
			throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
		}

		boolean fileSaveFail = false;

		Timestamp timestamp=Timestamp.valueOf(LocalDateTime.now());

		ThemesCategory themesCategory = utils.findThemeCategory(themeCategory);

		Themes themes = utils.findThemeByID(themeID);
		themes.setThemeName(themeName);
		themes.setThemeDescription(themeDesc);
		themes.setThemeModificationDate(timestamp);
		themes.setModifiedBy(dbUser);
		themes.setThemesCategory(themesCategory);

		String userName=dbUser.getUserName();


		String mainURL = null;
		String uploads_constant = null;

		if (storage.equals("local")) {
			mainURL = "http://" + domain + ":" + port + contextPath;

			uploads_constant = "Uploads" + File.separator + "Themes" + File.separator + userName + File.separator +
					themeID + File.separator + timestamp.getTime();
		}
		else if(storage.equals("aws")){
			mainURL = "https://" + bucketName + "." + endpointUrl;

			uploads_constant = "Uploads" + "/" + "Themes" + "/" + userName + "/" +
					themeID + "/" + timestamp.getTime();
		}

		utils.markAsModified(utils.findArtifactByThemeID(themeID));

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

					artifact.setTheme(themes);
					artifact.setIdea(null);
					artifact.setUser(dbUser);
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
		ResponseMessage<Themes> responseMessage = clientPartnerServices.updateTheme(themeID, themes, artifactList);

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

		return new ResponseEntity<>(responseMessage, new HttpHeaders(), HttpStatus.valueOf(responseMessage.getStatus()));
	}

	@DeleteMapping(value = "/user/delete/theme/{themeID}")
	public ResponseEntity<ResponseMessage<Themes>> deleteReview(@PathVariable("themeID") long themeID){


		LOGGER.info("Request URL: DELETE");
		LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

		if (utils.findThemeByID(themeID) == null) {
			LOGGER.error("Theme with {} themeID is not present in the database", themeID);
			throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
		}
		if(utils.findThemeByID(themeID).getIsDeleted().equals(IsDeleted.TRUE)) {
			LOGGER.error("Theme with {} themeID is marked as deleted in the database", themeID);
			throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
		}

		List<Ideas> ideasList = utils.findIdeaByThemeID(themeID);

		for(Ideas ideas:ideasList)
			productManagerDAO.deleteIdea(ideas.getIdeaID());

		ResponseMessage<Themes> responseMessage = clientPartnerServices.deleteTheme(themeID);

		LOGGER.info("Response Code: {}", responseMessage.getStatus());
		try {
			LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
		}
		catch (JsonProcessingException e){
			LOGGER.error("JsonProcessingException: Can't convert User Object to String");
		}

		return new ResponseEntity<>(responseMessage,HttpStatus.valueOf(responseMessage.getStatus()));
	}

}