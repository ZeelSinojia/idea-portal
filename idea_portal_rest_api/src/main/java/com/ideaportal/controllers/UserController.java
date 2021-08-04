package com.ideaportal.controllers;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideaportal.constants.IdeaPortalExceptionConstants;
import com.ideaportal.constants.IdeaPortalResponseConstants;
import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.dto.CommentsDTO;
import com.ideaportal.dto.LikesDTO;
import com.ideaportal.dto.UserDTO;
import com.ideaportal.exceptions.*;
import com.ideaportal.models.*;
import com.ideaportal.services.UserService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/api")
//This class controls the request that are common between CP, PM and Participants
public class UserController {

    final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${frontend.domain}")
	public String frontEndDomain;

	@Value("${frontend.port}")
	public String frontEndPort;

	@Autowired
	UserService userService;

	@Autowired
	DAOUtils utils;

	@Autowired
    ModelMapper modelMapper;
	@Autowired
	ServletContext context;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	//Sign up function for the user
	@PostMapping(value = "/signup")
	public ResponseEntity<ResponseMessage<User>> createNewUser(@RequestBody UserDTO userDTO)
	{
        LOGGER.info("Request URL: POST");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        try {
            LOGGER.info("Request Body: {}", objectMapper.writeValueAsString(userDTO));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
	    User userDetails = modelMapper.map(userDTO, User.class);
        ResponseMessage<User> responseMessage = userService.addUser(userDetails);
        LOGGER.info("Response Code: {}", responseMessage.getStatus());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

	// Login function for the user
	@PostMapping(value = "/login")
	public ResponseEntity<ResponseMessage<User>> loginUser(@RequestBody Login userDetails) throws UserAuthException 
	{
	    LOGGER.info("Request URL: POST");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        try {
            LOGGER.info("Request Body: {}", objectMapper.writeValueAsString(userDetails));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        ResponseMessage<User> responseMessage = userService.checkCredentials(userDetails);
        LOGGER.info("Response Code: {}", responseMessage.getStatus());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

		return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));

    }
      
    //Function to get a list of all themes for home page
    @GetMapping(value="/themes")
    public ResponseEntity<ResponseMessage<List<Themes>>> getAllThemes()
    {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        ResponseMessage<List<Themes>> responseMessage = userService.getAllThemesResponseMessage();
        LOGGER.info("Response Code: {}", responseMessage.getStatus());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }
    
    //Function to get a theme by id
    @GetMapping(value = "/themes/{id}")
    public ResponseEntity<ResponseMessage<Themes>> getThemeByID(@PathVariable("id")String themeID) {

        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

	    Themes themes;
	    
	    themes = utils.findThemeByID(Long.parseLong(themeID));

        ResponseMessage<Themes> responseMessage = new ResponseMessage<>();

        if(themes==null) {
            LOGGER.error("Theme with {} themeID not found in the database", themeID);
            throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
        }
	    else 
	    {
            responseMessage.setResult(themes);
            responseMessage.setStatus(HttpStatus.OK.value());
            responseMessage.setStatusText(IdeaPortalResponseConstants.THEME_OBJECT_MESSAGE);
            responseMessage.setTotalElements(1);
            LOGGER.info("Response Code: {}", responseMessage.getStatus());
            try {
                LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
            }
            catch (JsonProcessingException e){
                LOGGER.error("JsonProcessingException: Can't convert User Object to String");
            }
        }

        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to support user can get all ideas submitted for a particular theme sorted in most liked manner
    @GetMapping(value="/themes/{themeID}/ideas/mostlikes")
    public ResponseEntity<ResponseMessage<List<Ideas>>> getIdeasByMostLikesForTheme(@PathVariable("themeID") long themeID) throws ThemeNotFoundException
    {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

    	Themes theme=utils.findThemeByID(themeID);
    	
    	if(theme==null) {
    	    LOGGER.error("Theme with {} themeID not found in the database", themeID);
            throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
        }

    	ResponseMessage<List<Ideas>> responseMessage = userService.getIdeasByMostLikesResponseMessage(themeID);

        LOGGER.info("Response Code: {}", responseMessage.getStatus());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

    	return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }
    
    //Function to sort ideas by most comments
    @GetMapping(value="/themes/{themeID}/ideas/mostcomments")
    public ResponseEntity<ResponseMessage<List<Ideas>>> getIdeasByMostCommentsForTheme(@PathVariable ("themeID") long themeID)
    {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        Themes theme=utils.findThemeByID(themeID);

        if(theme==null) {
            LOGGER.error("Theme with {} themeID not found in the database", themeID);
            throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
        }

        ResponseMessage<List<Ideas>> responseMessage = userService.getIdeasByMostCommentsResponseMessage(themeID);

        LOGGER.info("Response Code: {}", responseMessage.getStatus());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to sort ideas by newest date
    @GetMapping(value = "/themes/{themeID}/ideas/newest")
    public ResponseEntity<ResponseMessage<List<Ideas>>> getIdeasByCreationDateForTheme(@PathVariable("themeID") long themeID) throws ThemeNotFoundException
    {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        Themes theme=utils.findThemeByID(themeID);

        if(theme==null) {
            LOGGER.error("Theme with {} themeID not found in the database", themeID);
            throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
        }

        ResponseMessage<List<Ideas>> responseMessage = userService.getIdeasByCreationDateResponseMessage(themeID);

        LOGGER.info("Response Code: {}", responseMessage.getStatus());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to get a Idea by id
    @GetMapping(value="/idea/{ideaID}")
    public ResponseEntity<ResponseMessage<Ideas>> getIdeaByID(@PathVariable ("ideaID") long ideaID) throws IdeaNotPresentInThemeException {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

    	ResponseMessage<Ideas> responseMessage=userService.getIdeaByIDResponseMessage(ideaID);

        if(responseMessage.getResult().getIsDeleted().equals(IsDeleted.TRUE)) {
            LOGGER.error("Idea with {} ideaID is marked as deleted in the database", ideaID);
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }
        if(responseMessage.getResult()==null) {
            LOGGER.error("Idea with {} ideaID is not present in the database", ideaID);
            throw new ThemeNotFoundException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
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
        
    //Function to update user Email ID and company
    @PutMapping(value = "/user/profile/update/profile")
    public ResponseEntity<ResponseMessage<User>> updateUserProfile(@RequestBody UserDTO userDTO)
    {
        LOGGER.info("Request URL: PUT");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        try {
            LOGGER.info("Request Body: {}", objectMapper.writeValueAsString(userDTO));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

        User user = modelMapper.map(userDTO, User.class);
        ResponseMessage<User> responseMessage = userService.updateUserProfileResponseMessage(user);

        LOGGER.info("Response Code: {}", responseMessage.getStatus());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

    	return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to update user password
    @PutMapping(value = {"/user/profile/update/password", "/login/savePassword"})
    public ResponseEntity<ResponseMessage<User>> updateUserPassword(@RequestBody UserDTO userDTO)
    {
        LOGGER.info("Request URL: PUT");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        try {
            LOGGER.info("Request Body: {}", objectMapper.writeValueAsString(userDTO));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

        User userDetail = modelMapper.map(userDTO, User.class);
    	ResponseMessage<User> responseMessage=userService.saveUserPasswordResponseMessage(userDetail);

        LOGGER.info("Response Code: {}", responseMessage.getStatus());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

    	return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }
    //Function to reset password
    @PostMapping(value = "/login/resetPassword")
    public ResponseEntity<ResponseMessage<Object>> resetPassword(@RequestBody UserDTO userDTO)
    {
        LOGGER.info("Request URL: POST");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        try {
            LOGGER.info("Request Body: {}", objectMapper.writeValueAsString(userDTO));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

        User userDetail = modelMapper.map(userDTO, User.class);
	    User user = utils.findByUserEmail(userDetail);
	    if(user==null) {
	        LOGGER.error("User with {} email is not present in the database", userDetail.getUserEmail());
            throw new UserNotFoundException(IdeaPortalExceptionConstants.USER_NOT_FOUND);
        }

	    ResponseMessage<Object> responseMessage = userService.sendEmail(user);

        LOGGER.info("Response Code: {}", responseMessage.getStatus());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }
    
    //Functions to res
    @GetMapping(value = "/login/confirmPassword")
    public ResponseEntity<Object> confirmResetUserPassword(@RequestParam("token")String token,
                                                           @RequestParam("user")long user,
                                                           @RequestParam("application") String application) throws URISyntaxException
    {

        LOGGER.info("Request URL: POST");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        URI redirect = null;
        if(application.equalsIgnoreCase("next"))
             redirect = new URI("http://"+frontEndDomain + ":" + frontEndPort +"/confirmpassword/"  + Long.toString(user));
        else if(application.equalsIgnoreCase("angular"))
            redirect = new URI("http://" + frontEndDomain + ":" + frontEndPort + "/#" +"/updatePassword"+"?user="+user);


        HttpHeaders httpHeaders = new HttpHeaders();
        userService.validatePasswordResetToken(token);

        httpHeaders.setLocation(redirect);

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(httpHeaders));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }
    
  //Function to support that user can like an idea
    @PutMapping(value="/user/idea/like")
    public ResponseEntity<ResponseMessage<Likes>> likeAnIdea(@RequestBody LikesDTO likesDTO)throws UserNotFoundException,
            IdeaNotPresentInThemeException, IdeaAlreadyLikedException, IdeaNotLikedException
    {
        LOGGER.info("Request URL: PUT");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        try {
            LOGGER.info("Request Body: {}", objectMapper.writeValueAsString(likesDTO));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

        Likes likes = modelMapper.map(likesDTO, Likes.class);
    	String res=utils.isIdeaLiked(likes);
    	
    	Ideas idea=utils.isIdeaIDValid(likes.getIdea().getIdeaID());
    	
    	User user=utils.findByUserId(likes.getUser().getUserID());
    	if(user==null){
    	    LOGGER.error("User not found");
    	    throw new UserNotFoundException(IdeaPortalExceptionConstants.USER_NOT_FOUND);
        }

    	if(idea==null) {
            LOGGER.error("Idea not found");
    	    throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }
    	
    	if(res!=null) {
            LOGGER.error("Idea is already likes");
    	    throw new IdeaAlreadyLikedException(res);
        }

        if(idea.getIsDeleted().equals(IsDeleted.TRUE)) {
            LOGGER.error("Idea is marked as deleted in the database");
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }

        ResponseMessage<Likes> responseMessage = userService.likeAnIdeaResponseMessage(likes);

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

    	return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to support that user can comment an idea
    @PostMapping(value="/user/idea/comment")
    public ResponseEntity<ResponseMessage<Comments>> commentAnIdea(@RequestBody CommentsDTO commentsDTO) throws
            UserNotFoundException, IdeaNotPresentInThemeException, IdeaNotCommentedException
    {
        LOGGER.info("Request URL: POST");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        try {
            LOGGER.info("Request Body: {}", objectMapper.writeValueAsString(commentsDTO));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

        Comments comment = modelMapper.map(commentsDTO, Comments.class);
    	User user=utils.findByUserId(comment.getUser().getUserID());
		
    	Ideas idea=utils.isIdeaIDValid(comment.getIdea().getIdeaID());
    	
    	if(user==null) {
            LOGGER.error("User not found");
            throw new UserNotFoundException(IdeaPortalExceptionConstants.USER_NOT_FOUND);
        }
    	if(idea==null) {
            LOGGER.error("Idea not found");
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }
        if(idea.getIsDeleted().equals(IsDeleted.TRUE)) {
            LOGGER.error("Idea is marked as deleted in the database");
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }

        ResponseMessage<Comments> responseMessage = userService.commentAnIdeaResponseMessage(comment);

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

    	return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    	
    }
    
    //Function to get a list of likes for an idea
    @GetMapping(value = "idea/{ideaID}/likes")
    public ResponseEntity<ResponseMessage<List<User>>> getLikesForIdea(@PathVariable ("ideaID") long ideaID) throws IdeaNotPresentInThemeException
    {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
    	Ideas idea=utils.isIdeaIDValid(ideaID);
    	if(idea==null) {
    	    LOGGER.error("Idea with {} ideaID not present in the database", ideaID);
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }
        if(idea.getIsDeleted().equals(IsDeleted.TRUE)) {
            LOGGER.error("Idea with {} ideaID is marked as deleted in the database", ideaID);
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }

        ResponseMessage<List<User>> responseMessage = userService.getLikesForIdeaResponseMessage(ideaID);

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

    	return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }
    
    //Function to get a list of dislikes for an idea
    @GetMapping(value = "idea/{ideaID}/dislikes")
    public ResponseEntity<ResponseMessage<List<User>>> getDislikesForIdea(@PathVariable ("ideaID") long ideaID) throws IdeaNotPresentInThemeException
    {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
    	Ideas idea=utils.isIdeaIDValid(ideaID);
    	if(idea==null) {
            LOGGER.error("Idea with {} ideaID not present in the database", ideaID);
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }
        if(idea.getIsDeleted().equals(IsDeleted.TRUE)) {
            LOGGER.error("Idea with {} ideaID is marked as deleted in the database", ideaID);
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }

        ResponseMessage<List<User>> responseMessage = userService.getDislikesForIdeaResponseMessage(ideaID);

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

    	return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }
    //Function to get a  list of comments for an idea
    @GetMapping(value="idea/{ideaID}/comments")
    public ResponseEntity<ResponseMessage<List<Comments>>> getCommentsForIdea(@PathVariable ("ideaID") long ideaID) throws IdeaNotPresentInThemeException
    {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
    	Ideas idea=utils.isIdeaIDValid(ideaID);
    	if(idea==null) {
            LOGGER.error("Idea with {} ideaID not present in the database", ideaID);
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }
        if(idea.getIsDeleted().equals(IsDeleted.TRUE)) {
            LOGGER.error("Idea with {} ideaID is marked as deleted in the database", ideaID);
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }

        ResponseMessage<List<Comments>> responseMessage = userService.getCommentForIdeaResponseMessage(ideaID);

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

    	return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to get number of user for all roles
    @GetMapping(value = "user/userCount")
    public ResponseEntity<ResponseMessage<Map<String, Integer>>> getUsersForRoles() {

        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        ResponseMessage<Map<String, Integer>> responseMessage = userService.getUsersForRoles();

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
	    return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to get count of ideas
    @GetMapping(value = "user/ideaCount")
    public ResponseEntity<ResponseMessage<Long>> getCountOfIdeas() {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        ResponseMessage<Long> responseMessage = userService.getCountOfIdeas();

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to get count of ideas
    @GetMapping(value = "user/themeCount")
    public ResponseEntity<ResponseMessage<Long>> getCountOfThemes() {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        ResponseMessage<Long> responseMessage = userService.getCountOfThemes();

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to get number of ideas in all themes
    @GetMapping(value = {"user/ideasForThemes/{n}", "user/ideasForThemes"})
    public ResponseEntity<ResponseMessage<Map<String, Integer>>> getIdeasForThemes(@PathVariable(value = "n", required = false) Integer nTheme) {

	    if (nTheme == null) {
            LOGGER.info("Request URL: GET");
            LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

            ResponseMessage<Map<String, Integer>> responseMessage = userService.getIdeasForNThemes();

            LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
            try {
                LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
            }
            catch (JsonProcessingException e){
                LOGGER.error("JsonProcessingException: Can't convert User Object to String");
            }
            return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
        }
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        ResponseMessage<Map<String, Integer>> responseMessage = userService.getIdeasForNThemes(nTheme);

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to get count of themes by their creation date
    @GetMapping(value = {"user/themesByDate/{n}", "user/themesByDate"})
    public ResponseEntity<ResponseMessage<Map<String, Integer>>> getThemesByDate(@PathVariable(value = "n", required = false) Integer nTheme) {

        if (nTheme == null) {
            LOGGER.info("Request URL: GET");
            LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

            ResponseMessage<Map<String, Integer>> responseMessage = userService.getThemesByDate();

            LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
            try {
                LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
            }
            catch (JsonProcessingException e){
                LOGGER.error("JsonProcessingException: Can't convert User Object to String");
            }
            return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
        }
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        ResponseMessage<Map<String, Integer>> responseMessage = userService.getThemesByDate(nTheme);

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to get count of participants in ideas
    @GetMapping(value = {"user/participantsForIdea/{n}", "user/participantsForIdea"})
    public ResponseEntity<ResponseMessage<Map<String, Integer>>> getParticipantsForIdeas(@PathVariable(value = "n", required = false) Integer nIdea) {

        if (nIdea == null) {
            LOGGER.info("Request URL: GET");
            LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

            ResponseMessage<Map<String, Integer>> responseMessage = userService.getParticipantsForIdeas();

            LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
            try {
                LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
            }
            catch (JsonProcessingException e){
                LOGGER.error("JsonProcessingException: Can't convert User Object to String");
            }
            return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
        }
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        ResponseMessage<Map<String, Integer>> responseMessage = userService.getParticipantsForIdeas(nIdea);

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to get count of likes for idea
    @GetMapping(value = {"user/getLikesForIdea", "user/getLikesForIdea/{n}"})
    public ResponseEntity<ResponseMessage<Map<String, Integer>>> getLikesForIdea(@PathVariable(value = "n", required = false) Integer nIdea) {

        if (nIdea == null) {
            LOGGER.info("Request URL: GET");
            LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

            ResponseMessage<Map<String, Integer>> responseMessage = userService.getLikesForIdea();

            LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
            try {
                LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
            }
            catch (JsonProcessingException e){
                LOGGER.error("JsonProcessingException: Can't convert User Object to String");
            }
            return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
        }
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        ResponseMessage<Map<String, Integer>> responseMessage = userService.getLikesForIdea(nIdea);

        LOGGER.info("Response Code: {}", HttpStatus.SEE_OTHER.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to get count of dislikes for idea
    @GetMapping(value = {"user/getDislikesForIdea", "user/getDislikesForIdea/{n}"})
    public ResponseEntity<ResponseMessage<Map<String, Integer>>> getDislikesForIdea(@PathVariable(value = "n", required = false) Integer nIdea) {

        if (nIdea == null) {
            LOGGER.info("Request URL: GET");
            LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

            ResponseMessage<Map<String, Integer>> responseMessage = userService.getDislikesForIdea();

            LOGGER.info("Response Code: {}", HttpStatus.OK.value());
            try {
                LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
            }
            catch (JsonProcessingException e){
                LOGGER.error("JsonProcessingException: Can't convert User Object to String");
            }
            return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
        }
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        ResponseMessage<Map<String, Integer>> responseMessage = userService.getDislikesForIdea(nIdea);

        LOGGER.info("Response Code: {}", HttpStatus.OK.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to get all ideas
    @GetMapping(value = "ideas")
    public ResponseEntity<ResponseMessage<List<Ideas>>> getAllIdeas() {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        ResponseMessage<List<Ideas>> responseMessage = userService.getAllIdeas();

        LOGGER.info("Response Code: {}", HttpStatus.OK.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to download list of all ideas for a particular theme
    @GetMapping(value = "/themes/{themeID}/ideas/download")
    public ResponseEntity<ResponseMessage<Object>> downloadIdeasList(@PathVariable ("themeID") String themeID) throws IOException, URISyntaxException {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        Themes theme=utils.findThemeByID(Long.parseLong(themeID));

        if(theme==null) {
            LOGGER.error("Theme with {} themeID not found in the database", themeID);
            throw new ThemeNotFoundException(IdeaPortalExceptionConstants.THEME_NOT_FOUND);
        }

        ResponseMessage<Object> responseMessage = userService.downloadIdeasList(theme);

        LOGGER.info("Response Code: {}", HttpStatus.OK.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }

        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to get Participants Roles
    @GetMapping("participation/getParticipantsRole")
    public ResponseEntity<ResponseMessage<List<ParticipantRoles>>> getParticipantsRole() {

        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        ResponseMessage<List<ParticipantRoles>> responseMessage = userService.getParticipantsRole();

        LOGGER.info("Response Code: {}", HttpStatus.OK.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    //Function to get themes category
    @GetMapping("themes/getThemesCategory")
    public ResponseEntity<ResponseMessage<List<ThemesCategory>>> getThemesCategory() {

        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        ResponseMessage<List<ThemesCategory>> responseMessage = userService.getThemesCategory();

        LOGGER.info("Response Code: {}", HttpStatus.OK.value());
        try {
            LOGGER.info("Response Body: {}", objectMapper.writeValueAsString(responseMessage.getResult()));
        }
        catch (JsonProcessingException e){
            LOGGER.error("JsonProcessingException: Can't convert User Object to String");
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @GetMapping(value = "/idea/{ideaID}/participants")
    public ResponseEntity<ResponseMessage<List<User>>> getParticipantsForAnIdea(@PathVariable("ideaID") long ideaID)
            throws IdeaNotPresentInThemeException
    {
        LOGGER.info("Request URL: GET");
        LOGGER.info("Request URL: {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        Ideas idea = utils.isIdeaIDValid(ideaID);
        if (idea == null) {
            LOGGER.error("Idea with {} ideaID is not present in the database", ideaID);
            throw new IdeaNotPresentInThemeException(IdeaPortalExceptionConstants.IDEA_NOT_FOUND);
        }
        ResponseMessage<List<User>> responseMessage = userService.getParticipantsForIdeaResponseMessage(ideaID);
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