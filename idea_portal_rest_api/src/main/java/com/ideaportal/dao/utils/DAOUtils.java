package com.ideaportal.dao.utils;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.ideaportal.models.*;
import com.ideaportal.repos.*;


import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import com.ideaportal.constants.IdeaPortalExceptionConstants;
import com.ideaportal.constants.IdeaPortalQueryConstants;


//Utility functions required in the DAO layer should be written here
@Repository
public class DAOUtils 
{
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	IdeasRepository ideasRepository;
	@Autowired
	ThemesRepository themesRepository;
	@Autowired
	PasswordLogRepository passwordLogRepository;

	@Autowired
	CommentsRepository commentsRepository;

	@Autowired
	ArtifactRepository artifactRepository;

	@Autowired
	ParticipationRepository participationRepository;

	@Autowired
	ThemesCategoryRepository themesCategoryRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Value("${max-themes}")
	private String maxThemes;
	
	 
	//Performs select operation and returns number of user based on the email
    public int getCountByEmail(String userEmail) throws DataAccessException
    {

    	return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_USER_BY_EMAIL, (PreparedStatementCallback<Integer>) ps -> {
			ps.setString(1, userEmail);

			ResultSet resultSet = ps.executeQuery();

			if (resultSet.next())
				return 1;
			else
				return -1;
		});
    	
    }
    
    //Performs select operation and returns number of user based on the userName
    public int getCountByUserName(String userName)
    {
    	return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_USER_BY_USERNAME, (PreparedStatementCallback<Integer>) ps -> {
			ps.setString(1, userName);

			ResultSet resultSet=ps.executeQuery();

			if(resultSet.next())
				return 1;
			else
				return -1;
		});
    	
	
    }

    public User findByUserName(User user)
    {
    	return userRepository.findById(user.getUserID()).orElse(null);
    	
    }

	//	Executes a select query to find a particular user
	public User findByUserEmail(User userDetail)
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_USER_BY_EMAIL, (PreparedStatementCallback<User>) ps -> {
			ps.setString(1, userDetail.getUserEmail());

			ResultSet resultSet=ps.executeQuery();

			User user=null;

			if(resultSet.next())
				user=userRepository.findById(resultSet.getLong(1)).orElse(null);
			return user;
		});
	}

	//	Executes a select query to find a particular user by their id
	public User findByUserId(long userID)
	{
		
		User user=null; 
		try
		{
			user=userRepository.findById(userID).orElse(null);
			
		}catch(NoSuchElementException exception) {return user;}
		
		return user;

	}
    
    public long findLikeID(long ideaID, long userID)
    {
    	long result;
    	
    	result=jdbcTemplate.execute(IdeaPortalQueryConstants.GET_LIKE_ID_QUERY, (PreparedStatementCallback<Long>) ps -> {
			ps.setLong(1, ideaID);
			ps.setLong(2, userID);

			ResultSet resultSet=ps.executeQuery();

			if(resultSet.next())
			{
				return resultSet.getLong(1);
			}
			else {
				return 0L;
			}

		});
    	
    	if(result>0)
    		return result;
    	else
    		return 0L;
    }
    
	public String isIdeaLiked(Likes likes) 
	{
		final LikeValue likeValue=likes.getLikeValue();
		
		return jdbcTemplate.execute(IdeaPortalQueryConstants.IS_IDEA_LIKED_OR_DISLIKED, (PreparedStatementCallback<String>) ps -> {
			ps.setLong(1, likes.getUser().getUserID());
			ps.setLong(2, likes.getIdea().getIdeaID());

			ResultSet resultSet=ps.executeQuery();

			if(resultSet.next())
			{
				int userLikeValue=0;

				if(likeValue.compareTo(LikeValue.LIKE)==0)
					userLikeValue=1;

				long dbLikeValue=resultSet.getLong(1);

				if(dbLikeValue==userLikeValue && userLikeValue==1)
					return IdeaPortalExceptionConstants.IDEA_LIKED;
				if(dbLikeValue==userLikeValue && userLikeValue==0)
					return IdeaPortalExceptionConstants.IDEA_DISLIKED;
			}
			return null;
		});
	}

	public Likes buildLikesObject(Likes likes) 
	{
		Optional<User> optionalUser=userRepository.findById(likes.getUser().getUserID());
		
		User user= optionalUser.orElse(null);
	
		Optional<Ideas> optionalIdeas=ideasRepository.findById(likes.getIdea().getIdeaID());
		
		Ideas idea= optionalIdeas.orElse(null);
		
		likes.setUser(user);
		likes.setIdea(idea);
		
		return likes;
	}

	public Comments buildCommentsObject(Comments comment) 
	{
		Optional<User> optionalUser=userRepository.findById(comment.getUser().getUserID());
		
		User user= optionalUser.orElse(null);
	
		Optional<Ideas> optionalIdeas=ideasRepository.findById(comment.getIdea().getIdeaID());
		
		Ideas idea= optionalIdeas.orElse(null);
	
		comment.setUser(user);
		comment.setIdea(idea);
	
		return comment;
		
	}

	public ParticipationResponses buildParticipationResponseObject(ParticipationResponses participationResponses) 
	{

		Optional<Ideas> optionalIdeas = ideasRepository.findById(participationResponses.getIdea().getIdeaID());
		Ideas idea = optionalIdeas.orElse(null);
		
		Optional<Themes> optionalThemes = themesRepository.findById(participationResponses.getTheme().getThemeID());
		Themes theme = optionalThemes.orElse(null);
		
		Optional<User> optionalUser = userRepository.findById(participationResponses.getUser().getUserID());
		User user = optionalUser.orElse(null);
		
		
		participationResponses.setIdea(idea);
		participationResponses.setTheme(theme);
		participationResponses.setUser(user);
		
		return participationResponses;
	}

//	Checks whether the new password is same as the old one
	public boolean isEqualToOldPassword(User userDetail) 
	{
    	List<PasswordLog> logList = passwordLogRepository.findAll();
    	PasswordLog passwordLog;
		for (PasswordLog log : logList) {
			passwordLog = log;
			if (userDetail.getUserID() == passwordLog.getUser().getUserID() && BCrypt.checkpw(userDetail.getUserPassword(), passwordLog.getOldPassword()))
				return true;
		}
    	return false;
	}

	public Themes findThemeByID(long themeID)
	{
		try
		{
			Themes themes = themesRepository.findById(themeID).orElse(null);
			if (themes != null && themes.getIsDeleted().equals(IsDeleted.TRUE))
				return null;
			return themes;
		}catch(NoSuchElementException e) {return null;}
		
	}

	public Ideas findIdeaByID(long ideaID)
	{
		try
		{
			return ideasRepository.findById(ideaID).orElse(null);
		}catch(NoSuchElementException e) {return null;}

	}

	public Artifacts findArtifactsByID(long artifactID){
    	try {
			return artifactRepository.findById(artifactID).orElse(null);
		} catch (NoSuchElementException e) {
    		return null;
		}
	}

	public List<Artifacts> findArtifactByThemeID(long themeID){
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_ARTIFACTS_BY_THEME_ID, (PreparedStatementCallback<List<Artifacts>>) ps -> {
			ps.setLong(1, themeID);

			ResultSet resultSet=ps.executeQuery();

			List<Artifacts> artifactsList = new ArrayList<>();

			while (resultSet.next())
				artifactsList.add(findArtifactsByID(resultSet.getLong(1)));

			return artifactsList;

		});
	}

	public List<Artifacts> findArtifactByIdeaID(long ideaID){
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_ARTIFACTS_BY_IDEA_ID, (PreparedStatementCallback<List<Artifacts>>) ps -> {
			ps.setLong(1, ideaID);

			ResultSet resultSet=ps.executeQuery();

			List<Artifacts> artifactsList = new ArrayList<>();

			while (resultSet.next())
				artifactsList.add(findArtifactsByID(resultSet.getLong(1)));

			return artifactsList;

		});
	}

	public List<Ideas> buildList(ResultSet resultSet) throws SQLException
	{
		List<Ideas> list=new ArrayList<>();
		
		while(resultSet.next())
		{
			Optional<Ideas> optional=ideasRepository.findById(resultSet.getLong(1));
			list.add(optional.orElse(null));
		}
		return list;
	}
	
	public boolean isThemeNameSame(long userID, String themeName)
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.IS_THEME_NAME_SAME, (PreparedStatementCallback<Boolean>) ps -> {
			ps.setLong(1, userID);
			ps.setString(2, themeName);

			ResultSet resultSet=ps.executeQuery();

			return resultSet.next();
		});
	}

    public boolean isAlreadyParticipated(ParticipationResponses participationResponses) 
    {
    	return jdbcTemplate.execute(IdeaPortalQueryConstants.IS_PARTICIPATED, (PreparedStatementCallback<Boolean>) ps -> {
			ps.setLong(1, participationResponses.getIdea().getIdeaID());
			ps.setLong(2, participationResponses.getUser().getUserID());

			ResultSet resultSet=ps.executeQuery();

			return resultSet.next();
		});
    }
    
    
    public boolean isIdeaInTheme(ParticipationResponses participationResponses) 
    {
    	
    	return jdbcTemplate.execute(IdeaPortalQueryConstants.IS_IDEA_IN_THEME, (PreparedStatementCallback<Boolean>) ps -> {
			ps.setLong(1, participationResponses.getIdea().getIdeaID());
			ps.setLong(2, participationResponses.getTheme().getThemeID());

			ResultSet resultSet=ps.executeQuery();

			return resultSet.next();
		});
    	
	}
    
    public Ideas isIdeaIDValid(long ideaID)
    {
    	Ideas idea;
    	try
    	{
    		idea=ideasRepository.findById(ideaID).orElse(null);
    	}catch (NoSuchElementException e) {return null;}
    	return idea;
    	
    }

	public boolean isIdeaNameSame(long userID, String ideaName, long themeID)
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.IS_IDEA_NAME_SAME, (PreparedStatementCallback<Boolean>) ps -> {
			ps.setLong(1, userID);
			ps.setLong(2, themeID);
			ps.setString(3, ideaName);

			ResultSet resultSet=ps.executeQuery();

			return resultSet.next();
		});
	}

	public List<User> buildUserList(ResultSet resultSet) throws SQLException 
	{
		List<User> list=new ArrayList<>();
		
		while(resultSet.next())
		{
			Optional<User> optional=userRepository.findById(resultSet.getLong(1));
			list.add(optional.orElse(null));
		}
		return list;
	}

	public List<ParticipationResponses> buildParticipationResponsesList(ResultSet resultSet) throws SQLException
	{
		List<ParticipationResponses> list=new ArrayList<>();

		while(resultSet.next())
		{
			Optional<ParticipationResponses> optional=participationRepository.findById(resultSet.getLong(1));
			list.add(optional.orElse(null));
		}
		return list;
	}
	
	public List<Comments> buildGetCommentsList(ResultSet resultSet) throws SQLException 
	{
		List<Comments> list=new ArrayList<>();
		
		while(resultSet.next())
		{
			Optional<Comments> optional=commentsRepository.findById(resultSet.getLong(1));
			list.add(optional.orElse(null));
		}
		return list;
		
	}

	public List<Themes> buildThemesList(ResultSet resultSet) throws SQLException 
	{
		List<Themes> list=new ArrayList<>();
		while(resultSet.next())
		{
			Optional<Themes> optional=themesRepository.findById(resultSet.getLong(1));
			list.add(optional.orElse(null));
		}
		return list;
	}

	public boolean isCountExceeding(long userID) 
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_THEME_COUNT, (PreparedStatementCallback<Boolean>) ps -> {
			ps.setLong(1, userID);

			ResultSet resultSet=ps.executeQuery();

			if(resultSet.next())
				return resultSet.getLong(1)==Long.parseLong(maxThemes);
			else
				return false;
		});
	}
	
			
    public Map<String, Integer> getUsersForRoles() 
    {
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_USER_FOR_ROLES, (PreparedStatementCallback<Map<String, Integer>>) ps -> {
			ResultSet resultSet=ps.executeQuery();

			Map<String, Integer> resultMap = new HashMap<>();

			while (resultSet.next())
				resultMap.put(resultSet.getString(1), resultSet.getInt(2));

			return resultMap;
		});
    }

	public Long getCountOfIdeas() 
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_COUNT_IDEAS, (PreparedStatementCallback<Long>) ps -> {
			ResultSet resultSet=ps.executeQuery();

			resultSet.next();
			return resultSet.getLong(1);
		});
	}

	public Long getCountOfThemes() 
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_COUNT_THEMES, (PreparedStatementCallback<Long>) ps -> {
			ResultSet resultSet=ps.executeQuery();

			resultSet.next();
			return resultSet.getLong(1);
		});
	}

	public Map<String, Integer> getIdeasForNThemes() 
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_IDEAS_FOR_THEMES, (PreparedStatementCallback<Map<String, Integer>>) ps -> {
			ps.setLong(1, 5);
			ResultSet resultSet=ps.executeQuery();

			Map<String, Integer> resultMap = new LinkedHashMap<>();

			while (resultSet.next())
				resultMap.put(resultSet.getString(1), resultSet.getInt(2));

			return resultMap;
		});
	}

	public Map<String, Integer> getIdeasForNThemes(Integer nTheme) 
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_IDEAS_FOR_THEMES, (PreparedStatementCallback<Map<String, Integer>>) ps -> {
			ps.setLong(1, nTheme);
			ResultSet resultSet=ps.executeQuery();

			Map<String, Integer> resultMap = new LinkedHashMap<>();

			while (resultSet.next())
				resultMap.put(resultSet.getString(1), resultSet.getInt(2));

			return resultMap;
		});
	}

	public Map<String, Integer> getThemesByDate() 
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_THEMES_BY_DATE, (PreparedStatementCallback<Map<String, Integer>>) ps -> {
			ps.setLong(1, 5);
			ResultSet resultSet=ps.executeQuery();

			Map<String, Integer> resultMap = new LinkedHashMap<>();

			while (resultSet.next())
				resultMap.put(resultSet.getString(1), resultSet.getInt(2));

			return resultMap;
		});
	}

	public Map<String, Integer> getThemesByDate(Integer nTheme) {
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_THEMES_BY_DATE, (PreparedStatementCallback<Map<String, Integer>>) ps -> {
			ps.setLong(1, nTheme);
			ResultSet resultSet=ps.executeQuery();

			Map<String, Integer> resultMap = new LinkedHashMap<>();

			while (resultSet.next())
				resultMap.put(resultSet.getString(1), resultSet.getInt(2));

			return resultMap;
		});
	}

	public Map<String, Integer> getParticipantsForIdeas() {
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_IDEAS_BY_PARTICIPANTS, (PreparedStatementCallback<Map<String, Integer>>) ps -> {
			ps.setLong(1, 5);
			ResultSet resultSet=ps.executeQuery();

			Map<String, Integer> resultMap = new LinkedHashMap<>();

			while (resultSet.next())
				resultMap.put(resultSet.getString(1), resultSet.getInt(2));

			return resultMap;
		});
	}

	public Map<String, Integer> getParticipantsForIdeas(Integer nIdea) {
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_IDEAS_BY_PARTICIPANTS, (PreparedStatementCallback<Map<String, Integer>>) ps -> {
			ps.setLong(1, nIdea);
			ResultSet resultSet=ps.executeQuery();

			Map<String, Integer> resultMap = new LinkedHashMap<>();

			while (resultSet.next())
				resultMap.put(resultSet.getString(1), resultSet.getInt(2));

			return resultMap;
		});
	}

	public Map<String, Integer> getLikesForIdea() {
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_COUNT_OF_LIKES_FOR_IDEA, (PreparedStatementCallback<Map<String, Integer>>) ps -> {
			ps.setLong(1, 5);
			ResultSet resultSet=ps.executeQuery();

			Map<String, Integer> resultMap = new LinkedHashMap<>();

			while (resultSet.next())
				resultMap.put(resultSet.getString(1), resultSet.getInt(2));

			return resultMap;
		});
	}

	public Map<String, Integer> getLikesForIdea(Integer nIdea) {
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_COUNT_OF_LIKES_FOR_IDEA, (PreparedStatementCallback<Map<String, Integer>>) ps -> {
			ps.setLong(1, nIdea);
			ResultSet resultSet=ps.executeQuery();

			Map<String, Integer> resultMap = new LinkedHashMap<>();

			while (resultSet.next())
				resultMap.put(resultSet.getString(1), resultSet.getInt(2));

			return resultMap;
		});
	}

	public Map<String, Integer> getDislikesForIdea() {
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_COUNT_OF_DISLIKES_FOR_IDEA, (PreparedStatementCallback<Map<String, Integer>>) ps -> {
			ps.setLong(1, 5);
			ResultSet resultSet=ps.executeQuery();

			Map<String, Integer> resultMap = new LinkedHashMap<>();

			while (resultSet.next())
				resultMap.put(resultSet.getString(1), resultSet.getInt(2));

			return resultMap;
		});
	}

	public Map<String, Integer> getDislikesForIdea(Integer nIdea) {
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_COUNT_OF_DISLIKES_FOR_IDEA, (PreparedStatementCallback<Map<String, Integer>>) ps -> {
			ps.setLong(1, nIdea);
			ResultSet resultSet=ps.executeQuery();

			Map<String, Integer> resultMap = new LinkedHashMap<>();

			while (resultSet.next())
				resultMap.put(resultSet.getString(1), resultSet.getInt(2));

			return resultMap;
		});
	}

    public boolean isThemeNameUpdatable(long userID, String themeName, long themeID) {
		return jdbcTemplate.execute(IdeaPortalQueryConstants.IS_THEME_NAME_UPDATABLE, (PreparedStatementCallback<Boolean>) ps -> {
			ps.setLong(1, userID);
			ps.setString(2, themeName);
			ps.setLong(3, themeID);

			ResultSet resultSet=ps.executeQuery();
			return resultSet.next();
		});
    }

	public void markAsModified(List<Artifacts> artifactsList) {
    	for (Artifacts artifacts:artifactsList){
    		Artifacts dbArtifact = artifactRepository.findById(artifacts.getArtifactID()).orElse(null);

    		if (dbArtifact != null){
    			dbArtifact.setIsModified(IsModified.TRUE);
    			dbArtifact = artifactRepository.save(dbArtifact);
			}

		}
	}

	public boolean isIdeaNameUpdatable(long userID, String ideaName, long themeID, long ideaID) {
		return jdbcTemplate.execute(IdeaPortalQueryConstants.IS_IDEA_NAME_UPDATABLE, (PreparedStatementCallback<Boolean>) ps -> {
			ps.setLong(1, userID);
			ps.setLong(2, themeID);
			ps.setString(3, ideaName);
			ps.setLong(4, ideaID);

			ResultSet resultSet=ps.executeQuery();
			return resultSet.next();
		});
	}

    public List<Ideas> findIdeaByThemeID(long themeID) {
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_IDEA_BY_THEME_ID, (PreparedStatementCallback<List<Ideas>>) ps -> {
			ps.setLong(1, themeID);

			ResultSet resultSet=ps.executeQuery();

			List<Ideas> ideasList = new ArrayList<>();

			while (resultSet.next())
				ideasList.add(this.findIdeaByID(resultSet.getLong(1)));
			return ideasList;

		});
    }

    public ThemesCategory findThemeCategory(long themeCategoryID){
		Optional<ThemesCategory> optionalThemesCategory = themesCategoryRepository.findById(themeCategoryID);

		return optionalThemesCategory.orElse(null);
	}
}
