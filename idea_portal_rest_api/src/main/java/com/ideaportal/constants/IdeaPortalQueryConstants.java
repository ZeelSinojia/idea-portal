package com.ideaportal.constants;

public class IdeaPortalQueryConstants {
	private IdeaPortalQueryConstants() {
		throw new IllegalStateException("Utility class");
	}

	public static final String GET_LIKE_ID_QUERY = "select like_id from likes where idea_id=? and user_id=?";

	public static final String GET_IDEAS_BY_CREATION_DATE = "select idea_id from ideas where theme_id = ? and ideas.is_deleted=0 ORDER BY creation_date DESC, idea_id ASC";

	public static final String GET_IDEAS_BY_MOST_LIKES = "select ideas.idea_id, SUM(likes.like_value) from ideas left outer join likes on likes.idea_id=ideas.idea_id where theme_id= ? and ideas.is_deleted=0 GROUP BY(idea_id) ORDER BY SUM(likes.like_value) DESC, ideas.idea_id ASC";

	public static final String GET_IDEAS_BY_MOST_COMMENTS = "select ideas.idea_id, COUNT(comments.comment_value) from ideas left outer join comments on comments.idea_id=ideas.idea_id where theme_id=? and ideas.is_deleted=0 GROUP BY (idea_id) ORDER BY COUNT(comments.comment_value) DESC, ideas.idea_id ASC";

	public static final String GET_USER_BY_USERNAME = "select user_id, user_password from user where user_name=?";

	public static final String GET_USER_BY_EMAIL = "select user_id from user where user_email=?";

	public static final String IS_IDEA_LIKED_OR_DISLIKED = "select like_value from likes where  user_id=? and  idea_id=?";

	public static final String GET_LIKES_FOR_IDEA = "select user_id from likes where idea_id=? and like_value=?";

	public static final String GET_DISLIKES_FOR_IDEA = "select user_id from likes where idea_id=? and like_value=?";

	public static final String GET_COMMENTS_FOR_IDEA = "select comment_id from comments where idea_id=?";

	public static final String GET_PARTICIPANTS_FOR_IDEA = "select user_id from participation_responses where idea_id=?";

	public static final String IS_PARTICIPATED = "select response_id from participation_responses where idea_id=? and user_id=?";

	public static final String IS_IDEA_IN_THEME = "select idea_id from ideas where idea_id=? and theme_id=? and is_deleted=0";

	public static final String GET_MY_THEMES = "select theme_id from themes where user_id=? and is_deleted=0";

	public static final String GET_MY_IDEAS = "select idea_id from ideas where user_id=? and is_deleted=0";
	
	public static final String GET_THEME_COUNT="select count(theme_id) from themes where user_id=? and is_deleted=0";
	
	public static final String IS_THEME_NAME_SAME = "select theme_name from themes where user_id=? and theme_name=? and is_deleted=0";

	public static final String IS_THEME_NAME_UPDATABLE = "select theme_name from themes where user_id=? and theme_name=? and theme_id!=?";

	public static final String IS_IDEA_NAME_SAME = "select idea_name from ideas where user_id=? and theme_id=? and idea_name=? and is_deleted=0";

	public static final String IS_IDEA_NAME_UPDATABLE = "select idea_name from ideas where user_id=? and theme_id=? and idea_name=? and idea_id!=?";

	public static final String GET_USER_FOR_ROLES = "select roles.role_name,count(user.user_id) from user,roles where user.role_id=roles.role_id group by(roles.role_name)";

	public static final String GET_IDEAS_FOR_THEMES = "select themes.theme_name,count(ideas.idea_id) as count from themes left join ideas on themes.theme_id=ideas.theme_id where themes.is_deleted=0 and ideas.is_deleted=0 group by(themes.theme_name) order by count desc limit ?";

	public static final String GET_THEMES_BY_DATE = "select substring(themes.creation_date,1,10) date,count(themes.theme_id) as count from themes where themes.is_deleted=0 group by date limit ?";

	public static final String GET_IDEAS_BY_PARTICIPANTS = "select ideas.idea_name,count(participation_responses.response_id) as count from ideas left join participation_responses on ideas.idea_id=participation_responses.idea_id where ideas.is_deleted=0 group by(ideas.idea_name) order by count desc limit ?";

	public static final String GET_COUNT_OF_LIKES_FOR_IDEA = "select ideas.idea_name,count(likes.like_value) as count from ideas left join likes on ideas.idea_id=likes.idea_id and likes.like_value=1 where ideas.is_deleted=0 group by(ideas.idea_name) order by count desc limit ?";

	public static final String GET_COUNT_OF_DISLIKES_FOR_IDEA = "select ideas.idea_name,count(likes.like_value) as count from ideas left join likes on ideas.idea_id=likes.idea_id and likes.like_value=0 where ideas.is_deleted=0 group by(ideas.idea_name) order by count desc limit ?";

	public static final String GET_ARTIFACTS_BY_THEME_ID = "select artifact_id from artifacts where theme_id = ?";

	public static final String GET_ARTIFACTS_BY_IDEA_ID = "select artifact_id from artifacts where idea_id = ?";

	public static final String GET_IDEA_BY_THEME_ID = "select idea_id from ideas where theme_id=?";

	public static final String GET_ALL_THEMES = "select theme_id from themes where is_deleted=?";

	public static final String GET_COUNT_IDEAS = "select count(*) from ideas where is_deleted=0";

	public static final String GET_COUNT_THEMES = "select count(*) from themes where is_deleted=0";

	public static final String GET_ALL_IDEAS = "select idea_id from ideas where is_deleted=0";

	public static final String GET_ALL_PARTICIPATION_RESPONSES_FOR_IDEA = "select response_id from participation_responses where idea_id=?";
}

