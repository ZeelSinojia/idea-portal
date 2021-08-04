package com.ideaportal.dao;

import com.ideaportal.constants.IdeaPortalExceptionConstants;


import com.ideaportal.constants.IdeaPortalQueryConstants;
import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.exceptions.InvalidRoleException;

import com.ideaportal.models.*;

import com.ideaportal.repos.*;


import com.ideaportal.services.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.util.*;

//Database access for all requests that are common to CP, PM and Participants
@Repository
public class UserDAO {

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    ThemesRepository themesRepository;
    
    @Autowired
    LikesRepository likesRepository;

    @Autowired
    CommentsRepository commentsRepository;

    @Autowired
    PasswordLogRepository passwordLogRepository;

    @Autowired
    IdeasRepository ideasRepository;

    @Autowired
	ParticipantRoleRepository participantRoleRepository;

    @Autowired
	ThemesCategoryRepository themesCategoryRepository;
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    DAOUtils utils;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDAO.class);

    //Performs insert operation into the database to create a new user
    public User saveUser(User userDetails) throws InvalidRoleException, IllegalArgumentException
    {

    	Roles role=userDetails.getRole();
    	
    	int roleID=role.getRoleID();
    	
    	if(roleID!= 1 && roleID != 2 && roleID!= 3) {
    		LOGGER.error("Role with RoleId {} not found", roleID);
			throw new InvalidRoleException(IdeaPortalExceptionConstants.ROLE_NOT_FOUND);
		}
    	
    	if(roleID==1)
    		role.setRoleName("Client Partner");
    	if(roleID==2)
    		role.setRoleName("Product Manager");
    	if(roleID==3)
    		role.setRoleName("Participant");
    	
        String hashedUserPassword = BCrypt.hashpw(userDetails.getUserPassword(), BCrypt.gensalt(10));
        userDetails.setUserPassword(hashedUserPassword);
        
        userDetails.setSignUpDate(Timestamp.valueOf(LocalDateTime.now()));
        userDetails = userRepository.save(userDetails);
        PasswordLog passwordLog = new PasswordLog();
		passwordLog.setUser(userDetails);
		passwordLog.setOldPassword(hashedUserPassword);
		passwordLog.setNewPassword(hashedUserPassword);
		passwordLogRepository.save(passwordLog);
        LOGGER.info("User with {} username saved into the database", userDetails.getUserName());
        return userDetails;
    }

    //Performs select operation for authorization and authentication
    public User isLoginCredentialsValid(Login userDetails)
    {
    	return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_USER_BY_USERNAME, (PreparedStatementCallback<User>) ps -> {
			ps.setString(1, userDetails.getUserName());

			ResultSet resultSet=ps.executeQuery();

			if(resultSet.next() && BCrypt.checkpw(userDetails.getUserPassword(), resultSet.getString(2))) {
				return userRepository.findById(resultSet.getLong(1)).orElse(null);
			}
			return null;
		});
    }
    
        
    //Executes a select * query on themes table 
	public List<Themes> getAllThemesList()
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_ALL_THEMES, (PreparedStatementCallback<List<Themes>>) ps -> {
			ps.setInt(1, 0);

			ResultSet rSet=ps.executeQuery();

			return utils.buildThemesList(rSet);
		});
	}
	
	//Executes Left Outer join on ideas and likes, sorted by most likes, filtered by theme id
	public List<Ideas> getIdeasByMostLikesList(long themeID)
	{
		
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_IDEAS_BY_MOST_LIKES, (PreparedStatementCallback<List<Ideas>>) ps -> {
			ps.setLong(1, themeID);

			ResultSet rSet=ps.executeQuery();

			return utils.buildList(rSet);
		});
	}

	//Executes Left Outer Join on ideas and comments, sorted by most comments, filtered by theme id
	public List<Ideas> getIdeasByMostCommentsList(long themeID) 
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_IDEAS_BY_MOST_COMMENTS, (PreparedStatementCallback<List<Ideas>>) ps -> {
			ps.setLong(1, themeID);

			ResultSet resultSet=ps.executeQuery();

			return utils.buildList(resultSet);

		});
	}

	//Executes select query on ideas table filtered by theme id sorted by date descending
	public List<Ideas> getIdeasByCreationDateList(long themeID) {
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_IDEAS_BY_CREATION_DATE, (PreparedStatementCallback<List<Ideas>>) ps -> {
			ps.setLong(1, themeID);

			ResultSet rSet=ps.executeQuery();

			return utils.buildList(rSet);
		});
	}

	//Executes Select * in ideas where ideaID=ideaID
	public Ideas getIdea(long ideaID) 
	{
		try
		{
			return ideasRepository.findById(ideaID).orElse(null);
		}catch(NoSuchElementException e) {return null;}
	}
	
	//Executes a update query with save(), requires userID to be pass in request body
	public User updateUserProfile(User user)
	{
		User dbUser=null;
		try
		{
			dbUser=userRepository.findById(user.getUserID()).orElse(null);

		}catch(NoSuchElementException e) {
			LOGGER.error("User with {} userID is not present in the database", user.getUserID());
			return dbUser;
		}

		if (dbUser != null) {
			dbUser.setUserEmail(user.getUserEmail());
			dbUser.setUserCompany(user.getUserCompany());
			return userRepository.save(dbUser);
		}
		else return dbUser;

	}
	
	 public User updatePassword(User userDetail) 
	    {
	        User user = null;
	        
	        try
	        {
	        	user=userRepository.findById(userDetail.getUserID()).orElse(null);
	        }catch(NoSuchElementException e) {
				LOGGER.error("User with {} userID is not present in the database", userDetail.getUserID());
	        	return user;
	        }

	        String hashedUserPassword = BCrypt.hashpw(userDetail.getUserPassword(), BCrypt.gensalt(10));

	        if (user != null) {
				PasswordLog passwordLog = new PasswordLog();
				passwordLog.setUser(user);
				passwordLog.setOldPassword(user.getUserPassword());
				passwordLog.setNewPassword(hashedUserPassword);
				passwordLogRepository.save(passwordLog);

				user.setUserPassword(hashedUserPassword);

				return userRepository.save(user);
			}
	        else return user;
	    }


	//Executes an insertQuery on the Likes table
	public Likes saveLikes(Likes likes)
	{
		long likeID=utils.findLikeID(likes.getIdea().getIdeaID(), likes.getUser().getUserID());
		
		likes.setLikeDate(Timestamp.valueOf(LocalDateTime.now()));

		if (likeID != 0) {
			likes.setLikeID(likeID);
		}
		likes=likesRepository.save(likes);

		likes=utils.buildLikesObject(likes);
		
		return likes;
	}
	
	public Comments saveComment(Comments comment) 
	{
		
		comment.setCommentDate(Timestamp.valueOf(LocalDateTime.now()));
		
		comment=commentsRepository.save(comment);
		
		comment=utils.buildCommentsObject(comment);
		
		return comment;
	}

	
//    Saves the new password of the user in db
   
	
	public List<User> getLikesForIdeaList(long ideaID)
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_LIKES_FOR_IDEA, (PreparedStatementCallback<List<User>>) ps -> {
			ps.setLong(1, ideaID);
			ps.setLong(2, 1);

			ResultSet resultSet=ps.executeQuery();

			return utils.buildUserList(resultSet);

		});
	}
	
	public List<User> getDislikesForIdeaList(long ideaID)
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_DISLIKES_FOR_IDEA, (PreparedStatementCallback<List<User>>) ps -> {
			ps.setLong(1, ideaID);
			ps.setLong(2, 0);		//Dislike Value

			ResultSet resultSet=ps.executeQuery();

			return utils.buildUserList(resultSet);

		});
	}
	
	public List<Comments> getCommentsList(long ideaID)
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_COMMENTS_FOR_IDEA, (PreparedStatementCallback<List<Comments>>) ps -> {
			ps.setLong(1, ideaID);

			ResultSet resultSet=ps.executeQuery();

			return utils.buildGetCommentsList(resultSet);
		});
	}
	
	public Optional<User> getUser(long id) {
		
		return userRepository.findById(id);
		
	}

    public List<Ideas> getAllIdeas() {

		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_ALL_IDEAS, (PreparedStatementCallback<List<Ideas>>) ps -> {
			ResultSet resultSet=ps.executeQuery();

			Map<String, Integer> resultMap = new LinkedHashMap<>();

			return utils.buildList(resultSet);
		});
    }

    public List<ParticipantRoles> getParticipantsRole() {
    	return participantRoleRepository.findAll();
	}

    public List<ThemesCategory> getThemesCategory() {
    	return themesCategoryRepository.findAll();
    }

	public List<User> getParticipantsListForIdea(long ideaID)
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_PARTICIPANTS_FOR_IDEA, (PreparedStatementCallback<List<User>>) ps -> {
			ps.setLong(1, ideaID);

			ResultSet resultSet=ps.executeQuery();

			return utils.buildUserList(resultSet);
		});
	}
}
	