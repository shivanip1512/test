package com.eaton.rest.api.common;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.eaton.framework.ConfigFileReader;
import com.eaton.framework.ValidUserLogin;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ApiTokenAuthentication {

    private static final long MINUTES_TO_REFRESH_TOKEN = 30;
    private String authToken = null;
    private static Cache<String, String> tokenCache =
        CacheBuilder.newBuilder().expireAfterWrite(MINUTES_TO_REFRESH_TOKEN, TimeUnit.MINUTES).build();
    private static final String AUTH_TOKEN_KEY = "authTokenKey";

    public String getAuthToken() {
        if (tokenCache == null || tokenCache.getIfPresent(AUTH_TOKEN_KEY) == null) {
            synchronized (this) {
                if (tokenCache == null || tokenCache.getIfPresent(AUTH_TOKEN_KEY) == null) {
                    authToken = generateToken();
                }
            }
        }
        return authToken;
    }

    //@SuppressWarnings("unchecked")
    private String generateToken() {
        
        JSONObject loginRequest = new JSONObject();

        loginRequest.put("password", ValidUserLogin.getPassword());
        loginRequest.put("username", ValidUserLogin.getUserName());
        String authTokenValue = null;

        try {
        ConfigFileReader configFileReader = new ConfigFileReader();
        baseURI =  configFileReader.getApplicationUrl();
            authTokenValue = given().accept("application/json").contentType("application/json").body(loginRequest.toString()).when().post(
                "/api/token").then().extract().path("token").toString();
        } catch (Exception e) {
        }
        tokenCache.put(AUTH_TOKEN_KEY, authTokenValue);
        return authTokenValue;
    }
}