package com.cannontech.web.dev;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.dr.ecobee.message.ZeusAuthenticationRequest;
import com.cannontech.dr.ecobee.message.ZeusAuthenticationResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MockZeusAuthenticationHelper {

    private Cache<String, ZeusAuthenticationResponse> mockEcobeeAuthTokenResponseCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1440, TimeUnit.MINUTES).build();
    private static final String mockResponseCacheKey = "mockResponseCacheKey";
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZoneUTC();

    public ZeusAuthenticationResponse login(ZeusAuthenticationRequest request) {
        String authToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        DateTime afterTime = DateTime.now(DateTimeZone.UTC).plusHours(1);
        String expiryTimestamp = formatter.print(afterTime);
        ZeusAuthenticationResponse response = new ZeusAuthenticationResponse(authToken, refreshToken, expiryTimestamp);
        mockEcobeeAuthTokenResponseCache.put(mockResponseCacheKey, response);
        return response;
    }

    public ZeusAuthenticationResponse refresh(String mockRefreshToken) {
        mockEcobeeAuthTokenResponseCache.invalidateAll();
        String authToken = UUID.randomUUID().toString();
        DateTime afterTime = DateTime.now(DateTimeZone.UTC).plusHours(1);
        String expiryTimestamp = formatter.print(afterTime);
        ZeusAuthenticationResponse response = new ZeusAuthenticationResponse(authToken, mockRefreshToken, expiryTimestamp);
        mockEcobeeAuthTokenResponseCache.put(mockResponseCacheKey, response);
        return response;
    }

    public boolean isInvalidRefreshToken(String mockRefreshToken) {
        return !mockRefreshToken.equals(mockEcobeeAuthTokenResponseCache.getIfPresent(mockResponseCacheKey).getRefreshToken());
    }
}
