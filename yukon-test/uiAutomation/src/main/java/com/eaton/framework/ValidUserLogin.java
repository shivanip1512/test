package com.eaton.framework;

@SuppressWarnings("unused")
public class ValidUserLogin {

    private ValidUserLogin() {
    }

    private static final String SYUKON_USER = "syukon";
    private static final String SYUKON_PASS = "syukon";
    
    private static final String YUKON_USER = "yukon";
    private static final String YUKON_PASS = "yukon";
    
    private static final String STARSOP_USER = "starsop";
    private static final String STARSOP_PASS = "starsop";
    
    private static final String AUTOMATION_USER = "automation";
    private static final String AUTOMATION_PASS = "automation";

    public static String getUserName() {

        return AUTOMATION_USER;
    }

    public static String getPassword() {

        return AUTOMATION_PASS;
    }

}
