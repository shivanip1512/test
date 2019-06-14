package com.cannontech.rest.api.common;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

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
        baseURI = ApiRequestHelper.getProperty("baseURI");
        File file = ApiRequestHelper.getInputFile("login.json");
        return given().accept("application/json").contentType("application/json").body(file).when().post(
            "/api/token").then().extract().path("token").toString();
    }
}
