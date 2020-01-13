package com.eaton.framework;

public class ValidUserLogin {

    private ValidUserLogin() {
    }

    private static final String USER_NAME = "ea";
    private static final String PASSWORD = "ea";

    public static String getUserName() {

        return USER_NAME;
    }

    public static String getPassword() {

        return PASSWORD;
    }

}
