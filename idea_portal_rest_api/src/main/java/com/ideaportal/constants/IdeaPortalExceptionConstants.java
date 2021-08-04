package com.ideaportal.constants;


public class IdeaPortalExceptionConstants
{
	private IdeaPortalExceptionConstants() {
		throw new IllegalStateException("Utility class");
	}

	public static final String USER_NOT_FOUND ="User Not Found, Please try again!";

	public static final String ROLE_NOT_FOUND="Invalid Role id was passed, Please try again!";
	
	public static final String INVALID_CREDENTIALS="Invalid Username/Password, Please try again";
	
	public static final String EMAIL_IN_USE="This email is registered with us, Please use another Email ID!";
	
	public static final String USERNAME_IN_USE="User Name already in use, Try another User Name!";
	
	public static final String SAME_PASSWORD="Your new password can't be same as the old one";

	public static final String MAIL_SERVER_EXCEPTION="Error while resetting the password. Please try again!";
	
	public static final String INVALID_TOKEN= "Invalid token! Please try again!";
	
	public static final String IDEA_DISLIKED="You have already disliked this idea";
	
	public static final String IDEA_LIKED="You have already liked this idea";
	
	public static final String IDEA_NOT_FOUND="Invalid Idea ID";
	
	public static final String SOME_ERROR_IN_SERVER="Some error occurred, Please try again";

	public static final String SAME_THEME_NAME="You already have a theme with this name!";

	public static final String SAME_IDEA_NAME = "You have created idea with same name, Please try other name";
	
	public static final String THEME_NOT_FOUND="No theme present";
	
	public static final String IDEA_ALREADY_PARTICIPATED="You have already participated for this idea!";
	
	public static final String IDEA_NOT_IN_THEME="The idea is not present in the given theme";

	public static final String THEME_COUNT_EXCEEDS = "You have reached the maximum number of creating themes";

	public static final String REQUEST_FAILED = "The request failed. Please try again";

	public static final String PARTICIPATION_ROLE_NOT_FOUND = "Participation role must be provided while participating";
}
