package com.cannontech.common.login;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.ClientApplicationRememberMe;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiPreferences;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;

/**
 * LoginPref stores some stuff via the preferences api  
 */
class LoginPrefs extends CtiPreferences {
    
    private static String CURRENT_YUKON_HOST = "Current Yukon Host";
    private static String CURRENT_YUKON_PORT = "Current Yukon Port";
    
    private static String DEFAULT_USERNAME = "Default Username";
    private static String DEFAULT_PASSWORD = "Default Password";
    private static String DEFAULT_REMEMBER_PASSWORD = "Default Remember Password";
    
    private static LoginPrefs instance;

    private final AESPasswordBasedCrypto aesCipher = new AESPasswordBasedCrypto("smoe_kRAZy-Key FOR_+ NOW THIS is kIND of CrZY wrighT-?".toCharArray());
    private final Logger log = YukonLogManager.getLogger(LoginPrefs.class);
    
    private LoginPrefs() {
        super();
    }   

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
	
	public String getRememberedUsername(){
		return get(DEFAULT_USERNAME, "yukon");
	}
	
	public String getRememberedPassword() {
        //if we have a value, we must decrypt it
        String password = get(DEFAULT_PASSWORD, null);
        if(StringUtils.isEmpty(password)) {
            return "";
        }

        try {
            return aesCipher.decryptHexStr(password);
        } catch (CryptoException | DecoderException e) {
            log.warn("Unable to decrypt saved password.", e);
            return "";
        } 
    }

	/**
	 * Checks the username to see if it matches the name that
	 * was typed in last time "remember me" was checked.  If the name
	 * matches the username provided, the password is returned.
	 */
	public String getRememberedPassword(String username) {

        String password = get(DEFAULT_PASSWORD, null);
        //only use the remembered password if we're logging in as the
        //user who owns it
        if(password != null && username.equals(getRememberedUsername())) {
          //if we have a value, we must decrypt it
            try {
                password = aesCipher.decryptHexStr(password);
            } catch (CryptoException | DecoderException e) {
                log.warn("Unable to decrypt saved password.", e);
                password = null;
            } 
            return (password == null ? "" : password);
        } else {
            return "";
        }
	}

	public boolean shouldRememberCredentials() {
		return getBoolean(DEFAULT_REMEMBER_PASSWORD, true);
	}

	public void setCurrentYukonHost(String host) {
		put(CURRENT_YUKON_HOST, host);
	}

    /**
     * Will remember credentials based on CLIENT_APPLICATIONS_REMEMBER_ME global setting value.
     * Wipes out any previously remembered settings.
     */
    public void rememberCredentials(String username, String password, ClientApplicationRememberMe rememberMe) {
        forgetCredentials();

        if (rememberMe == ClientApplicationRememberMe.USERNAME) {
            put(DEFAULT_USERNAME, username);
        } else if (rememberMe == ClientApplicationRememberMe.USERNAME_AND_PASSWORD) {
            put(DEFAULT_USERNAME, username);
            put(DEFAULT_PASSWORD, aesCipher.encryptToHexStr(password));
        }
    }

	public void forgetCredentials() {
	    put(DEFAULT_USERNAME, "");
        put(DEFAULT_PASSWORD, "");
	}

	public void shouldRememberCredentials(boolean val) {
		put(DEFAULT_REMEMBER_PASSWORD, val);
	}

    /**
     * Returns 'USERNAME' or 'USERNAME_AND_PASSWORD' if the jnlp.yukon.rememberMe preference
     * matches a valid setting, otherwise returns 'NONE'
     */
    public ClientApplicationRememberMe getRememberMeSetting() {
        return ClientApplicationRememberMe.valueOf(System.getProperty("jnlp.yukon.rememberMe",
                                                      ClientApplicationRememberMe.NONE.name()));
    }
}
