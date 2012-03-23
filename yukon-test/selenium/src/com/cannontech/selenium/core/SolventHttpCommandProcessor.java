/**
 * 
 */
package com.cannontech.selenium.core;

import com.thoughtworks.selenium.HttpCommandProcessor;

/**
 * @author anuradha.uduwage
 *
 */
public class SolventHttpCommandProcessor extends HttpCommandProcessor {
	
    private static final String FIREFOX2_LABEL = "yukon-selenium-firefox";
    private static final String FIREFOX2_EXECUTABLE = "browsers/windows/Mozilla/Firefox2/firefox.exe";
    private static final String FIREFOX2_PROFILE_DIR = "firefox2";

    private static final String FIREFOX3_LABEL = "yukon-selenium-firefox";
    private static final String FIREFOX3_EXECUTABLE = "server/windows/Mozilla/Firefox3/firefox.exe";
    private static final String FIREFOX3_PROFILE_DIR = "firefox3";

    private String browserStartCommand = null;
    private String browserURL          = null;
    private String optionsString       = null;
    private String sessionId           = null;
                                  

    public SolventHttpCommandProcessor(String serverHost, int serverPort, String browserStartCommand, String browserURL) {
        super(serverHost, serverPort, getFixedCommand(browserStartCommand), browserURL);

        this.browserURL = browserURL;
        this.browserStartCommand = browserStartCommand;

        if (this.browserStartCommand.equals(FIREFOX2_LABEL)) {
            this.optionsString = "profile:" + FIREFOX2_PROFILE_DIR;
        } else if (browserStartCommand.equals(FIREFOX3_LABEL)) {
            this.optionsString = "profile:" + FIREFOX3_PROFILE_DIR;
        } else {
            this.optionsString = "";
        }
        
    }

    public String getBaseUrl() {
        return this.browserURL;
    }

    public String getSessionId() {
        // This method was moved from HttpCommandProcessor down here
        // to reduce number of alterations to the original Selenium code
        return sessionId;
    }

    @Override
    public void start() {
        // The code of this method is functionally equivalent to
        //     HttpCommandProcessor.start(String optionsString)
        // and could be replaced with the call:
        //
        //     super.start(this.optionsString);
        //
        // The purpose of having it this way is to be able to get hold
        // of value of access "sessionId" (which is private in the parent class)
        // and make it available to clients via "getSessionId()" method.
    	//super.start();
        
        String[] args = {getFixedCommand(browserStartCommand), browserURL, "", optionsString};
        sessionId = getString("getNewBrowserSession", args);
        setSessionInProgress(sessionId);     
    }
    
    private static String getFixedCommand(String browserStartCommand) {
        String customBrowserStartCommand;
        if (browserStartCommand.equals(FIREFOX2_LABEL)) {
            customBrowserStartCommand = "*firefox2 " + FIREFOX2_EXECUTABLE;
        } else if (browserStartCommand.equals(FIREFOX3_LABEL)) {
            customBrowserStartCommand = "*firefox3 " + FIREFOX3_EXECUTABLE;
        } else {
            customBrowserStartCommand = browserStartCommand;
        }
        return customBrowserStartCommand;
    }    

}
