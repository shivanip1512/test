package com.eaton.framework;

public class ValidUserLogin {

    private ValidUserLogin() {
    }

    private static final String YUKON_USER = "syukon";
    private static final String YUKON_PASS = "syukon";
    
    private static final String STARSOP_USER = "starsop";
    private static final String STARSOP_PASS = "starsop";
    
    private static final String AUTOMATION_USER = "syukon";
    private static final String AUTOMATION_PASS = "syukon";

    public static String getUserName() {

        return AUTOMATION_USER;
    }

    public static String getPassword() {

        return AUTOMATION_PASS;
    }

}
