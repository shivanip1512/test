package com.cannontech.user;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public final class UserUtils 
{	
	public static final String STATUS_DISABLED = "Disabled";
	public static final String STATUS_ENABLED = "Enabled";
	public static final String STATUS_LOGGEDIN = "LoggedIn";
	public static final String STATUS_LOGGEDOUT = "LoggedOut";
	

	/* Precanned existing users in the system */
	public static final int USER_ADMIN_ID = -1;
	public static final int USER_YUKON_ID = -2;
	public static final int USER_STARS_DEFAULT_ID = -9999;
	public static final int INVALID_CURRENT_USERID = Integer.MIN_VALUE;
	
	


	/* Precanned role categories for users in the system */
	public static final String CAT_YUKON = "Yukon";
	public static final String CAT_RADIUS = "Radius Login";

	
	public static final String[] ALL_CATEGORIES =
	{
		CAT_YUKON
	};
	
	
	
	public static final boolean isReadOnlyCategory( String cat_ )
	{
		for( int i = 0; i < ALL_CATEGORIES.length; i++ )
		{
			if( ALL_CATEGORIES[i].equalsIgnoreCase(cat_) )
				return true;
		}
		
		return false;
	}

}