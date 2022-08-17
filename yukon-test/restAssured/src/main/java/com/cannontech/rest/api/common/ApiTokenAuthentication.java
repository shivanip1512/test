package com.cannontech.rest.api.common;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

import java.util.concurrent.TimeUnit;

import com.cannontech.rest.api.utilities.Log;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ApiTokenAuthentication {

    private static final long MINUTES_TO_REFRESH_TOKEN = 30;
    private String authToken = null;
    private static Cache<String, String> tokenCache =
        CacheBuilder.newBuilder().expireAfterWrite(MINUTES_TO_REFRESH_TOKEN, TimeUnit.MINUTES).build();
    private final String authTokenKey = "authTokenKey";

    public String getAuthToken() {
        if (tokenCache == null || tokenCache.getIfPresent(authTokenKey) == null) {
            synchronized (this) {
                if (tokenCache == null || tokenCache.getIfPresent(authTokenKey) == null) {
                    authToken = generateToken();
                }
            }
        }
        return authToken;
    }

    private String generateToken() {
        MockLoginRequest loginRequest = MockLoginRequest.builder()
                                                .username("yukon")
                                                .password("yukon")
                                                .build();
        baseURI = ApiCallHelper.getProperty("baseURI");
        String authTokenValue = null;
        try {
            authTokenValue = given().accept("application/json").contentType("application/json").body(loginRequest).when().post(
                "/api/token").then().extract().path("token").toString();
        } catch (Exception e) {
            Log.error("Error in token generation - " + e.toString(), e);
        }
        tokenCache.put(authTokenKey, authTokenValue);
        return authTokenValue;

    }
}
