/*
 * Created on Jun 24, 2003
  */
package com.cannontech.common.login;

import com.cannontech.common.util.CtiPreferences;
import com.cannontech.crypto.CtiCipher;

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
	 
	public String getCurrentSessionID() {
		return get(CURRENT_SESSION_ID, "");
	}
	
	public int getCurrentUserID() {
		return getInt(CURRENT_USER_ID, -1);
	}
	
	public String getCurrentYukonHost() {
		return get(CURRENT_YUKON_HOST, "127.0.0.1");
	}
	
	public String[] getAvailableYukonHosts() {
		return getStringArr(AVAILABLE_YUKON_HOSTS, new String[] { "127.0.0.1" } );
	}
	
	public int getCurrentYukonPort() {
		return getInt(CURRENT_YUKON_PORT, getDefaultYukonPort());
	}
	
	public int getDefaultYukonPort() {
		return getInt(DEFAULT_YUKON_PORT, 8080);
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
			return (password == null ? "yukon" : password);
		}
		else
			return "yukon";
	}
	
	public boolean getDefaultRememberPassword() {
		return getBoolean(DEFAULT_REMEMBER_PASSWORD, true);
	}
	
	public void setCurrentSessionID(String sessID) {
		put(CURRENT_SESSION_ID, sessID);
	}
	
	public void setCurrentUserID(int userid) {
		put(CURRENT_USER_ID, userid);
	}
	
	public void setCurrentYukonHost(String host) {
		put(CURRENT_YUKON_HOST, host);
	}
	
	public void setAvailableYukonHosts(String[] hosts) {
		put(AVAILABLE_YUKON_HOSTS, hosts);
	}
	
	public void addAvailableYukonHost(String host){
		String[] hosts = getAvailableYukonHosts();
		for(int i = 0; i < hosts.length; i++) {
			if(hosts[i].trim().equalsIgnoreCase(host.trim())) {
				return;
			}
		}
		
		String[] newHosts = new String[hosts.length+1];
		System.arraycopy(hosts, 0, newHosts, 0, hosts.length);
		newHosts[newHosts.length-1] = host;
		setAvailableYukonHosts(newHosts);
	}
	
	public void setCurrentYukonPort(int port) {
		put(CURRENT_YUKON_PORT, port); 
	}
	
	public void setDefaultUsername(String username) {
		put(DEFAULT_USERNAME, username);
	}
	
	public void setDefaultYukonPort(int port) {
		put(DEFAULT_YUKON_PORT, port);
	}
	
	public void setDefaultPassword(String password) 
	{	
		//encrypt the stored password
		put( DEFAULT_PASSWORD, CtiCipher.encrypt(password) );
	}
	
	public void setDefaultRememberPassword(boolean val) {
		put(DEFAULT_REMEMBER_PASSWORD, val);
	}

	private static String AVAILABLE_YUKON_HOSTS = "Available Yukon Hosts";
	
	private static String CURRENT_SESSION_ID = "Current Session ID";
	private static String CURRENT_USER_ID = "Current User ID";
	
	private static String CURRENT_YUKON_HOST = "Current Yukon Host";
	private static String CURRENT_YUKON_PORT = "Current Yukon Port";
	
	private static String DEFAULT_YUKON_PORT = "Default Port";
	private static String DEFAULT_USERNAME = "Default Username";
	private static String DEFAULT_PASSWORD = "Default Password";
	private static String DEFAULT_REMEMBER_PASSWORD = "Default Remember Password";
	
	private static LoginPrefs instance;

	private LoginPrefs(){
		super();
	}	
}
