package com.ideaportal.services;


import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ideaportal.constants.IdeaPortalExceptionConstants;


import com.ideaportal.constants.IdeaPortalResponseConstants;

import com.ideaportal.dao.UserDAO;

import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.exceptions.*;

import com.ideaportal.models.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.MediaType;


import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;
import java.util.Map;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

//Services All requests that are common to CP, PM and Participants
@Service
public class UserService {

	@Autowired
	HttpServletRequest request;

	@Autowired
	private JavaMailSender emailSender;

    @Autowired
    UserDAO userDAO;

    @Autowired
    DAOUtils utils;

    @Value("${server.servlet.context-path}")
	public String contextPath;

	@Value("${server.port}")
	public String serverPort;

	@Value("${server.domain}")
	public String serverDomain;

	@Value("${ideaportal.jwt.secret-key}")
	public String jwtSecretKey;

	@Value("${ideaportal.jwt.expiration-time}")
	public long jwtExpirationTime;

	@Value("${ideaportal.jwt.password-reset.expiration-time}")
	public long passwordResetTokenExpirationTime;

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

	@Value("${frontend.type}")
	private String applicationType;

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
	ServletContext context;

	private static final String PASSWORD_RESET_EMAIL_BODY = "To complete the password reset process, please click here: ";

    private static final String PASSWORD_RESET_EMAIL_SUBJECT = "Complete Password Reset!";

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    //Method to create JWT when
    public String generateJWT(User user) 
    {
        long timestamp = System.currentTimeMillis(); //current time in milliseconds

        //Token is configured using this builder method
		LOGGER.info("JWT token created");
        return Jwts.builder().signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + jwtExpirationTime))
				.claim("user", user)
                .compact(); //builds the token
    }

	//Method to create JWT when
	public String generatePasswordResetToken() {
		long timestamp = System.currentTimeMillis(); //current time in milliseconds

		LOGGER.info("Password Reset Token created");
		//Token is configured using this builder method
		return Jwts.builder().signWith(SignatureAlgorithm.HS256, jwtSecretKey)
				.setIssuedAt(new Date(timestamp))
				.setExpiration(new Date(timestamp + passwordResetTokenExpirationTime))
				.compact(); //builds the token
	}

    //Service to add user Also checks if user already exists or not
    public ResponseMessage<User> addUser(User userDetails)
    {

        int emailCount = utils.getCountByEmail(userDetails.getUserEmail());	//checks whether email is already registered or not
        int userNameCount=utils.getCountByUserName(userDetails.getUserName()); //	checks whether user name is already registered or not
        
        ResponseMessage<User> responseMessage=new ResponseMessage<>();
        
        if(userNameCount>0) {
        	LOGGER.error("User with {} name is already present in the system", userDetails.getUserName());
        	throw new UserAuthException(IdeaPortalExceptionConstants.USERNAME_IN_USE);
		}

        if (emailCount > 0) {
			LOGGER.error("User with {} email is already present in the system", userDetails.getUserEmail());
        	throw new UserAuthException(IdeaPortalExceptionConstants.EMAIL_IN_USE);
		}
        
      
        User user = userDAO.saveUser(userDetails);

        responseMessage.setResult(user);			//Returns the user object that is saved in the database
        responseMessage.setStatus(HttpStatus.CREATED.value());
        responseMessage.setStatusText(IdeaPortalResponseConstants.SIGN_UP_SUCCESSFUL);
        responseMessage.setToken(generateJWT(user));			//Passes the generated JWT
        responseMessage.setTotalElements(1);
        return responseMessage;
        		
    }

    //Service to check credentials of user while login 
    public ResponseMessage<User> checkCredentials(Login userDetails) 
    {
    	User user=userDAO.isLoginCredentialsValid(userDetails);			//Checks whether valid credentials are passes or not
    	
    	if(user==null) {
    		LOGGER.error("User Credentials are Invalid");
			throw new UserAuthException(IdeaPortalExceptionConstants.INVALID_CREDENTIALS);
		}
        ResponseMessage<User> responseMessage =new ResponseMessage<>();
        
        responseMessage.setResult(user);		//Returns the object retrieved from the database
        responseMessage.setStatus(HttpStatus.OK.value());
        responseMessage.setStatusText(IdeaPortalResponseConstants.LOGIN_SUCCESSFUL);
        responseMessage.setToken(generateJWT(user));				//Generates JWT
        
        return responseMessage;
    }

    //Service to get all the themes submitted by client partners
	public ResponseMessage<List<Themes>> getAllThemesResponseMessage() 
	{
		List<Themes> list=userDAO.getAllThemesList();
		
		ResponseMessage<List<Themes>> responseMessage=new ResponseMessage<>();
		
		int size=list.size();
		
		if(size==0)
		{
			LOGGER.info("No themes preset");
			responseMessage.setResult(null);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.NO_THEMES_CURRENTLY);

		}
		else
		{
			LOGGER.info("List of themes");
			responseMessage.setResult(list);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.LIST_ALL_THEMES);
			responseMessage.setTotalElements(size);
		}
		return responseMessage;
	}
	
	//Service to get all ideas sorted by most likes
	public ResponseMessage<List<Ideas>> getIdeasByMostLikesResponseMessage(long themeID)
	{
		ResponseMessage<List<Ideas>> responseMessage=new ResponseMessage<>();
		
		List<Ideas> list=userDAO.getIdeasByMostLikesList(themeID);
		
		if(list.isEmpty())
		{
			LOGGER.info(IdeaPortalResponseConstants.NO_IDEAS_SUBMITTED);
			responseMessage.setResult(null);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.NO_IDEAS_SUBMITTED);

		}
		else
		{
			responseMessage.setResult(list);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.LIST_ALL_IDEAS);
			responseMessage.setTotalElements(list.size());
		}
		return responseMessage;
	}

	//Service to get all ideas sorted by most comments
	public ResponseMessage<List<Ideas>> getIdeasByMostCommentsResponseMessage(long themeID)
	{
		ResponseMessage<List<Ideas>> responseMessage=new ResponseMessage<>();
		
		List<Ideas> list=userDAO.getIdeasByMostCommentsList(themeID);
		

		
		if(list.isEmpty())
		{
			LOGGER.info(IdeaPortalResponseConstants.NO_IDEAS_SUBMITTED);
			responseMessage.setResult(null);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.NO_IDEAS_SUBMITTED);

		}
		else
		{
			responseMessage.setResult(list);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.LIST_ALL_IDEAS);
			responseMessage.setTotalElements(list.size());
		}
		return responseMessage;
	}

	//Service to get all ideas sorted by newest date
	public ResponseMessage<List<Ideas>> getIdeasByCreationDateResponseMessage(long themeID) 
	{
		ResponseMessage<List<Ideas>> responseMessage=new ResponseMessage<>();

		List<Ideas> list=userDAO.getIdeasByCreationDateList(themeID);

		if(list.isEmpty())
		{
			LOGGER.info(IdeaPortalResponseConstants.NO_IDEAS_SUBMITTED);
			responseMessage.setResult(null);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.NO_IDEAS_SUBMITTED);
		}
		else
		{
			responseMessage.setResult(list);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.LIST_ALL_IDEAS);
			responseMessage.setTotalElements(list.size());
		}
		return responseMessage;
	}
	
	//Service to get idea by id
	public ResponseMessage<Ideas> getIdeaByIDResponseMessage(long ideaID) 
	{
		ResponseMessage<Ideas> responseMessage=new ResponseMessage<>();
		
		Ideas idea=userDAO.getIdea(ideaID);
		if(idea==null)
		{
			responseMessage.setResult(null);
			responseMessage.setStatus(HttpStatus.NOT_FOUND.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.IDEA_BY_ID_NOT_FOUND);

		}
		else
		{
			responseMessage.setResult(idea);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.IDEA_OBJECT_MESSAGE);
			responseMessage.setTotalElements(1);

		}
		return responseMessage;

	}
	
	//Service to update User Email
	public ResponseMessage<User> updateUserProfileResponseMessage(User user)
	{
		int emailCount = utils.getCountByEmail(user.getUserEmail());	//checks whether email is already registered or not
		
		if (emailCount > 0)  {
			LOGGER.error("{} email is already in use", user.getUserEmail());
			throw new UserAuthException(IdeaPortalExceptionConstants.EMAIL_IN_USE);
		}
		
		ResponseMessage<User> responseMessage=new ResponseMessage<>();
		
		User dbUser=userDAO.updateUserProfile(user);
		
		if(dbUser==null) {
			LOGGER.error("User not present in the database");
			throw new UserNotFoundException(IdeaPortalExceptionConstants.USER_NOT_FOUND);
		}
		else
		{
			responseMessage.setResult(dbUser);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.PROFILE_UPDATE_SUCCESS);
			responseMessage.setTotalElements(1);
			return responseMessage;
		}
		
	}

	//Service updates the password of the user
	public ResponseMessage<User> saveUserPasswordResponseMessage(User userDetail) 
	{
    	if (utils.isEqualToOldPassword(userDetail)) {
    		LOGGER.error("User already had same password earlier");
			throw new UserAuthException(IdeaPortalExceptionConstants.SAME_PASSWORD);
		}
    	ResponseMessage<User> responseMessage=new ResponseMessage<>();
    	
    	User user=userDAO.updatePassword(userDetail);
    	
    	if(user==null)
    	{
			LOGGER.error("User not present in the database");
    		responseMessage.setResult(null);
    		responseMessage.setStatus(HttpStatus.NOT_FOUND.value());
    		responseMessage.setStatusText(IdeaPortalResponseConstants.NO_USER_FOUND);
		}
    	else
    	{
    		responseMessage.setResult(user);
    		responseMessage.setStatus(HttpStatus.OK.value());
    		responseMessage.setStatusText(IdeaPortalResponseConstants.PASSWORD_UPDATE_SUCCESS);
    		responseMessage.setTotalElements(1);
		}
		return responseMessage;
	}
	
	public ResponseMessage<Object> sendEmail(User user) 
	{
    	try 
    	{
			String token = this.generatePasswordResetToken();
			
//			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//			simpleMailMessage.setTo(user.getUserEmail());
//			simpleMailMessage.setSubject(PASSWORD_RESET_EMAIL_SUBJECT);
//
//			String link = PASSWORD_RESET_EMAIL_BODY + request.getScheme() + "://" + request.getServerName()
//					+ ":" + request.getServerPort() + contextPath + "/api/login/confirmPassword"
//					+ "?token="+token+"&user="+user.getUserID()+"&application="+applicationType;
//
//			String content = "<p>Hello,</p>"
//					+ "<p>You have requested to reset your password.</p>"
//					+ "<p>Click the link below to change your password:</p>"
//					+ "<p><a href=\"" + link + "\">Change my password</a></p>"
//					+ "<br>"
//					+ "<p>Ignore this email if you do remember your password, "
//					+ "or you have not made the request.</p>";
//
//			simpleMailMessage.setText(content);
//			emailSender.send(simpleMailMessage);

			MimeMessage mimeMessage = emailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

			mimeMessageHelper.setTo(user.getUserEmail());
			mimeMessageHelper.setSubject(PASSWORD_RESET_EMAIL_SUBJECT);

			String link = request.getScheme() + "://" + request.getServerName()
					+ ":" + request.getServerPort() + contextPath + "/api/login/confirmPassword"
					+ "?token="+token+"&user="+user.getUserID()+"&application="+applicationType;

			String content = "<p>Hello,</p>"
					+ "<p>You have requested to reset your password.</p>"
					+ "<p>Click the link below to change your password:</p>"
					+ "<p><a href=\"" + link + "\">Change my password</a></p>"
					+ "<br>"
					+ "<p>Ignore this email if you do remember your password, "
					+ "or you have not made the request.</p>";

			mimeMessageHelper.setText(content, true);
			emailSender.send(mimeMessage);

    	} catch (Exception e) 
    	{
    		LOGGER.error("MailServerException: Google service down or invalid mail credentials");
    		throw new MailServerException(IdeaPortalExceptionConstants.MAIL_SERVER_EXCEPTION);
		}

		ResponseMessage<Object> responseMessage=new ResponseMessage<>();
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setStatusText(IdeaPortalResponseConstants.PASSWORD_LINK_SUCCESS);
		
		return responseMessage;
	}

	//Service to like an idea
	public ResponseMessage<Likes> likeAnIdeaResponseMessage(Likes likes) throws IdeaNotLikedException
	{
		Likes like =userDAO.saveLikes(likes);
		
		ResponseMessage<Likes> responseMessage=new ResponseMessage<>();
		
		if(like==null) {
			LOGGER.error("Idea was not liked");
			throw new IdeaNotLikedException(IdeaPortalExceptionConstants.SOME_ERROR_IN_SERVER);
		}
		else
		{
			responseMessage.setResult(like);
			responseMessage.setStatus(HttpStatus.CREATED.value());
		
			if(like.getLikeValue().compareTo(LikeValue.DISLIKE)==0)
				responseMessage.setStatusText(IdeaPortalResponseConstants.DISLIKE_IDEA_SUCCESS);
			else
				responseMessage.setStatusText(IdeaPortalResponseConstants.LIKE_IDEA_SUCCESS);
			responseMessage.setTotalElements(1);
		}
		
		return responseMessage;
	}

//	Service to comment on an idea
	public ResponseMessage<Comments> commentAnIdeaResponseMessage(Comments comment) throws IdeaNotCommentedException
	{
		Comments dbComment=userDAO.saveComment(comment);
		
		if(dbComment==null) {
			LOGGER.error("Comment was not added in the database");
			throw new IdeaNotCommentedException(IdeaPortalExceptionConstants.SOME_ERROR_IN_SERVER);
		}
		ResponseMessage<Comments> responseMessage=new ResponseMessage<>();
		
		responseMessage.setResult(dbComment);
		responseMessage.setStatus(HttpStatus.CREATED.value());
		responseMessage.setStatusText(IdeaPortalResponseConstants.COMMENT_SUCCESS);
		responseMessage.setTotalElements(1);
		return responseMessage;
	}
	
//Service validates the password reset token
	public void validatePasswordResetToken(String token) 
	{
		try {
			Jwts.parser().setSigningKey(jwtSecretKey)
					.parseClaimsJws(token).getBody();
			LOGGER.info("Password Reset Token Valid");
		}catch (Exception e) {
			LOGGER.error("Invalid password reset token");
			throw new UserAuthException(IdeaPortalExceptionConstants.INVALID_TOKEN);
		}
	}
	
	public ResponseMessage<List<User>> getLikesForIdeaResponseMessage(long ideaID) 
	{
		List<User> list=userDAO.getLikesForIdeaList(ideaID);
		
		ResponseMessage<List<User>> responseMessage=new ResponseMessage<>();
		
		int size=list.size();
		
		if(size==0)
		{
			LOGGER.info("No likes found for given idea");
			responseMessage.setResult(null);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.NO_LIKES);
		}
		else
		{
			responseMessage.setResult(list);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.LIKES_LIST);
			responseMessage.setTotalElements(size);

		}
		return responseMessage;

	}

	public ResponseMessage<List<User>> getDislikesForIdeaResponseMessage(long ideaID) 
	{
		List<User> list=userDAO.getDislikesForIdeaList(ideaID);
		
		ResponseMessage<List<User>> responseMessage=new ResponseMessage<>();
		
		int size=list.size();
		
		if(size==0)
		{
			LOGGER.info("No dislikes found for given idea");
			responseMessage.setResult(null);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.NO_DISLIKES);
		}
		else
		{
			responseMessage.setResult(list);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.DISLIKES_LIST);
			responseMessage.setTotalElements(size);

		}
		return responseMessage;

	}

	public ResponseMessage<List<Comments>> getCommentForIdeaResponseMessage(long ideaID) 
	{
		List<Comments> list=userDAO.getCommentsList(ideaID);
		
		ResponseMessage<List<Comments>> responseMessage=new ResponseMessage<>();
		
		int size=list.size();
		if(size==0)
		{
			LOGGER.info("No comments found for given idea");
			responseMessage.setResult(null);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.NO_COMMENTS);

		}
		
		else
		{
			responseMessage.setResult(list);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.COMMENT_LIST);
			responseMessage.setTotalElements(size);

		}
		return responseMessage;

	}
	
	public User getUser(long id) {
			
			return userDAO.getUser(id).orElse(null);
			
		}


    public ResponseMessage<Map<String, Integer>> getUsersForRoles() {

    	ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
    	responseMessage.setResult(utils.getUsersForRoles());
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(1);
		return responseMessage;
    }

	public ResponseMessage<Long> getCountOfIdeas() {
    	ResponseMessage<Long> responseMessage = new ResponseMessage<>();
    	responseMessage.setResult(utils.getCountOfIdeas());
    	responseMessage.setStatus(HttpStatus.OK.value());
    	responseMessage.setTotalElements(1);
    	return responseMessage;
	}

	public ResponseMessage<Long> getCountOfThemes() {
		ResponseMessage<Long> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(utils.getCountOfThemes());
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<Map<String, Integer>> getIdeasForNThemes() {
		ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(utils.getIdeasForNThemes());
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<Map<String, Integer>> getIdeasForNThemes(Integer nTheme) {
		ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(utils.getIdeasForNThemes(nTheme));
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<Map<String, Integer>> getThemesByDate() {
		ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(utils.getThemesByDate());
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<Map<String, Integer>> getThemesByDate(Integer nTheme) {
		ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(utils.getThemesByDate(nTheme));
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<Map<String, Integer>> getParticipantsForIdeas() {
		ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(utils.getParticipantsForIdeas());
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<Map<String, Integer>> getParticipantsForIdeas(Integer nIdea) {
		ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(utils.getParticipantsForIdeas(nIdea));
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<Map<String, Integer>> getLikesForIdea() {
		ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(utils.getLikesForIdea());
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<Map<String, Integer>> getLikesForIdea(Integer nIdea) {
		ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(utils.getLikesForIdea(nIdea));
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<Map<String, Integer>> getDislikesForIdea() {
		ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(utils.getDislikesForIdea());
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<Map<String, Integer>> getDislikesForIdea(Integer nIdea) {
		ResponseMessage<Map<String, Integer>> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(utils.getDislikesForIdea(nIdea));
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(1);
		return responseMessage;
	}

	public ResponseMessage<List<Ideas>> getAllIdeas() {

    	List<Ideas> listOfIdeas = userDAO.getAllIdeas();

    	ResponseMessage<List<Ideas>> responseMessage = new ResponseMessage<>();
    	responseMessage.setResult(listOfIdeas);
    	responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setStatusText(IdeaPortalResponseConstants.LIST_ALL_IDEAS);
    	responseMessage.setTotalElements(listOfIdeas.size());

    	return responseMessage;
	}

	public ResponseMessage<List<ParticipantRoles>> getParticipantsRole(){
    	List<ParticipantRoles> participantRoles = userDAO.getParticipantsRole();

		ResponseMessage<List<ParticipantRoles>> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(participantRoles);
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(participantRoles.size());

		return responseMessage;
	}

	public boolean uploadFile(MultipartFile file, String uploadsConstant)  {

    	AmazonS3 amazonS3 = buildAmazonS3();

    	if(amazonS3!=null) {
			try {
				File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
				file.transferTo(convFile);

				ObjectMetadata objectMetadata = new ObjectMetadata();
				objectMetadata.setContentType(file.getContentType());
				objectMetadata.setContentLength(file.getSize());
				objectMetadata.setContentDisposition("attachment");

				String effectiveName = uploadsConstant + "/" + convFile.getName();


				amazonS3.putObject(new PutObjectRequest(bucketName, effectiveName, convFile)
						.withCannedAcl(CannedAccessControlList.PublicRead)
						.withMetadata(objectMetadata));
				LOGGER.info("The files were successfully uploaded to the AWS bucket");
				return true;

			} catch (SdkClientException e) {
				LOGGER.error("SDKClientException: Files were not uploaded to the aws bucket.");
				return false;
			} catch (IOException e) {
				LOGGER.info("IOException encountered while in saveFile");
				return false;
			}

		}
    	else {
    		LOGGER.error("AmazonS3 client was null");
    		return false;
		}
//		https://s3.ap-south-1.amazonaws.com/ideaportaltextapplications3bucket/Uploads/Themes/demo_cp/27/1624337047308/Queries.txt
//		ideaportaltextapplications3bucket.s3.ap-south-1.amazonaws.com/Uploads/Themes/demo_pm/6/1624358211189/MuFest.pdf
	}

	public boolean uploadFile(File file, String uploadsConstant)  {

		AmazonS3 amazonS3 = buildAmazonS3();

		if(amazonS3!=null) {
			try {
				ObjectMetadata objectMetadata = new ObjectMetadata();
				objectMetadata.setContentType(MediaType.APPLICATION_XML.getType());
				objectMetadata.setContentDisposition("attachment");

				String effectiveName = uploadsConstant + "/" + file.getName();


				amazonS3.putObject(new PutObjectRequest(bucketName, effectiveName, file)
						.withCannedAcl(CannedAccessControlList.PublicRead)
						.withMetadata(objectMetadata));
				LOGGER.info("The files were successfully uploaded to the AWS bucket");
				return true;

			} catch (SdkClientException e) {
				LOGGER.error("SDKClientException: Files were not uploaded to the aws bucket.");
				return false;
			}

		}
		else {
			LOGGER.error("AmazonS3 client was null");
			return false;
		}
//		https://s3.ap-south-1.amazonaws.com/ideaportaltextapplications3bucket/Uploads/Themes/demo_cp/27/1624337047308/Queries.txt
//		ideaportaltextapplications3bucket.s3.ap-south-1.amazonaws.com/Uploads/Themes/demo_pm/6/1624358211189/MuFest.pdf
	}

	private AmazonS3 buildAmazonS3() {
		AWSCredentials credentials =
				new BasicAWSCredentials(this.accessKey, this.secretKey);
		return AmazonS3ClientBuilder
				.standard()
				.withRegion(this.region)
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
	}

	public boolean saveFile(MultipartFile file, File dir) {
    	String filename = file.getOriginalFilename();
    	String path = dir + File.separator + filename;
		Path filePath = Paths.get(path);
		try {
			InputStream fileInputStream = file.getInputStream();
			Files.copy(fileInputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e){
			LOGGER.info("IOException encountered while in saveFile");
			return false;
		}
	}

	public File workbookToFile(Workbook workbook) throws IOException {
//    	File outputFile = File.createTempFile("temp", ".xlsx");
		String fileName = "listOfIdeas_" + workbook.getSheetName(0) + ".xlsx";
		LOGGER.info(fileName);
		File outputFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
    	try (FileOutputStream fos = new FileOutputStream(outputFile)){
    		workbook.write(fos);
		}
    	return outputFile;
	}

    public ResponseMessage<Object> downloadIdeasList(Themes themes) throws IOException {
		List<Ideas> ideasList = null;
		File file = null;
		Workbook workbook = new XSSFWorkbook();
    	try
		{
			Sheet sheet = workbook.createSheet(themes.getThemeName());
			sheet.setColumnWidth(0, 2000);
			sheet.setColumnWidth(1, 6000);
			sheet.setColumnWidth(2, 10000);
			sheet.setColumnWidth(3, 15000);
			sheet.setColumnWidth(4, 4000);
			sheet.setColumnWidth(5, 4000);
			sheet.setColumnWidth(6, 8000);

			Row header = sheet.createRow(0);

			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFFont font = ((XSSFWorkbook) workbook).createFont();
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 12);
			font.setBold(true);
			headerStyle.setFont(font);

			Cell headerCell = header.createCell(0);
			headerCell.setCellValue("Sr no.");
			headerCell.setCellStyle(headerStyle);

			headerCell = header.createCell(1);
			headerCell.setCellValue("Idea Name");
			headerCell.setCellStyle(headerStyle);

			headerCell = header.createCell(2);
			headerCell.setCellValue("Idea Description");
			headerCell.setCellStyle(headerStyle);

			headerCell = header.createCell(3);
			headerCell.setCellValue("Documents");
			headerCell.setCellStyle(headerStyle);

			headerCell = header.createCell(4);
			headerCell.setCellValue("Date");
			headerCell.setCellStyle(headerStyle);

			headerCell = header.createCell(5);
			headerCell.setCellValue("Submitted by");
			headerCell.setCellStyle(headerStyle);

			headerCell = header.createCell(6);
			headerCell.setCellValue("Number of participants");
			headerCell.setCellStyle(headerStyle);

			ideasList = userDAO.getIdeasByCreationDateList(themes.getThemeID());

			for (int i = 0; i < ideasList.size(); i++) {
				CellStyle style = workbook.createCellStyle();
				CellStyle linkStyle = workbook.createCellStyle();

				XSSFFont linkFont = ((XSSFWorkbook) workbook).createFont();
				linkFont.setColor(IndexedColors.BLUE.getIndex());

				linkStyle.setFont(linkFont);
				linkStyle.setWrapText(true);
				style.setWrapText(true);

				Row row = sheet.createRow(i + 2);

				Cell cell = row.createCell(0);
				cell.setCellValue(i + (double) 1);
				cell.setCellStyle(style);

				cell = row.createCell(1);
				cell.setCellValue(ideasList.get(i).getIdeaName());
				cell.setCellStyle(style);

				cell = row.createCell(2);
				cell.setCellValue(ideasList.get(i).getIdeaDescription());
				cell.setCellStyle(style);

				URI artifactsString = new URI("http://" + frontEndDomain + ":" + frontEndPort
						+ "/theme/" + Long.toString(themes.getThemeID()) + "/ideas/" + ideasList.get(i).getIdeaID()
						+ "/ideadocuments");
				CreationHelper createHelper = workbook.getCreationHelper();
				Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.URL);
				hyperlink.setAddress(artifactsString.toString());
				cell = row.createCell(3);
				cell.setCellValue("List of documents");
				cell.setHyperlink(hyperlink);
				cell.setCellStyle(linkStyle);


				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date(ideasList.get(i).getIdeaDate().getTime());
				cell = row.createCell(4);
				cell.setCellValue(df.format(date));
				cell.setCellStyle(style);

				cell = row.createCell(5);
				cell.setCellValue(ideasList.get(i).getUser().getUserName());
				cell.setCellStyle(style);

				cell = row.createCell(6);
				cell.setCellValue(this.getParticipantsForIdeaResponseMessage(ideasList.get(i).getIdeaID()).getTotalElements());
				cell.setCellStyle(style);

				file = this.workbookToFile(workbook);
			}

		}
		catch (URISyntaxException e){
			LOGGER.info("Exception occurred while creating excel file");
		} finally {
			workbook.close();

		}
		String mainURL = null;
		String UPLOADS_CONSTANT = null;

		mainURL = "https://" + bucketName + "." + endpointUrl;

		UPLOADS_CONSTANT = "Uploads" + "/" + "Themes" + "/" + themes.getUser().getUserName() + "/" +
				themes.getThemeID() + "/" + "ListOfAllIdeas";


		boolean uploadFileStatus = this.uploadFile(file, UPLOADS_CONSTANT);
		if (uploadFileStatus) {
			LOGGER.info("Files were uploaded successfully to the AWS");
		} else {
			LOGGER.info("Files were not uploaded to the AWS");
		}
		ResponseMessage<Object> responseMessage=new ResponseMessage<>();

		if(ideasList.isEmpty())
		{
			LOGGER.info(IdeaPortalResponseConstants.NO_IDEAS_SUBMITTED);
			responseMessage.setResult(null);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.NO_IDEAS_SUBMITTED);

		}
		else
		{
			String encodedString = URLEncoder.encode("listOfIdeas_" + themes.getThemeName() + ".xlsx", "UTF-8");
			responseMessage.setResult(mainURL + "/" + UPLOADS_CONSTANT + "/" + encodedString);
			responseMessage.setStatus(HttpStatus.OK.value());
			responseMessage.setStatusText(IdeaPortalResponseConstants.LIST_ALL_IDEAS);
			responseMessage.setTotalElements(ideasList.size());
		}
		return responseMessage;


    }

	public ResponseMessage<List<ThemesCategory>> getThemesCategory() {
		List<ThemesCategory> themesCategoryList = userDAO.getThemesCategory();

		ResponseMessage<List<ThemesCategory>> responseMessage = new ResponseMessage<>();
		responseMessage.setResult(themesCategoryList);
		responseMessage.setStatus(HttpStatus.OK.value());
		responseMessage.setTotalElements(themesCategoryList.size());

		return responseMessage;
	}

	public ResponseMessage<List<User>> getParticipantsForIdeaResponseMessage(long ideaID)
	{
		List<User> list=userDAO.getParticipantsListForIdea(ideaID);

		ResponseMessage<List<User>> responseMessage=new ResponseMessage<>();

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
}
