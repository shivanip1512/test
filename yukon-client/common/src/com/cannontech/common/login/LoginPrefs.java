/*
 * Created on Jun 24, 2003
  */
package com.cannontech.common.login;

import com.cannontech.common.util.CtiPreferences;
import com.cannontech.encryption.CtiCipher;

/**
 * LoginPref stores some stuff via the preferences api  
  * @author aaron
 */
class LoginPrefs extends CtiPreferences {
		
	public static synchronized LoginPrefs getInstance() {
		if(instance == null) {
			instance = new LoginPrefs();
		}
		return instance;
	}
	 
	public String getCurrentYukonHost() {
		return get(CURRENT_YUKON_HOST, "http://localhost:8080");
	}
	
	@Deprecated()
	public int getCurrentYukonPort() {
		return getInt(CURRENT_YUKON_PORT, 8080);
	}
	
	public String getDefaultUsername(){
		return get(DEFAULT_USERNAME, "yukon");
	}
	
	public String getDefaultPassword() 
	{
		//if we have a value, we must decrypt it
		String password = get(DEFAULT_PASSWORD, null);
		if( password != null )
		{
			password = CtiCipher.decrypt(password);
			return (password == null ? "" : password);
		}
		else
			return "";
	}
	
	/**
	 * Checks the username to see if it matches the name that
	 * was typed in last time "remember me" was checked.  If the name
	 * matches the username provided, the password is returned.
	 */
	public String getDefaultPassword(String username){
        String password = get(DEFAULT_PASSWORD, null);
        //only use the remembered password if we're logging in as the
        //user who owns it
        if( password != null && username.equals(getDefaultUsername()) )
        {
          //if we have a value, we must decrypt it
            password = CtiCipher.decrypt(password);
            return (password == null ? "" : password);
        }
        else
            return "";
	}
	
	public boolean getDefaultRememberPassword() {
		return getBoolean(DEFAULT_REMEMBER_PASSWORD, true);
	}
	
	public void setCurrentYukonHost(String host) {
		put(CURRENT_YUKON_HOST, host);
	}
	
	public void setDefaultUsername(String username) {
		put(DEFAULT_USERNAME, username);
	}
	
	public void setDefaultPassword(String password) 
	{	
		//encrypt the stored password
		put( DEFAULT_PASSWORD, CtiCipher.encrypt(password) );
	}
	
	public void setDefaultRememberPassword(boolean val) {
		put(DEFAULT_REMEMBER_PASSWORD, val);
	}

	private static String CURRENT_YUKON_HOST = "Current Yukon Host";
	private static String CURRENT_YUKON_PORT = "Current Yukon Port";
	
	private static String DEFAULT_USERNAME = "Default Username";
	private static String DEFAULT_PASSWORD = "Default Password";
	private static String DEFAULT_REMEMBER_PASSWORD = "Default Remember Password";
	
	private static LoginPrefs instance;

	private LoginPrefs(){
		super();
	}	
}
