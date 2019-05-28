package com.cannontech.web.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.token.TokenHelper;

public class APIRequestHelper {

    private final static String authToken = "authToken";
    @Autowired private RestTemplate apiRestTemplate;

    /**
     * This method will send a API request to the passed url and return object.
     * 
     * @param userContext - Yukon user context
     * @param request - HttpRequest
     * @param url - URL for the request
     * @param method - HttpMethod type for the request
     * @param responseType - Type of response required
     * @param requestObject - Optional parameter for request object.
     * @return - ResponseEntity having the object send via responseType
     * @throws RestClientException - Any exception from API call have to be handled by the caller.
     */
    public ResponseEntity<? extends Object> callAPIForObject(YukonUserContext userContext, HttpServletRequest request,
            String url, HttpMethod method, Class<? extends Object> responseType, Object... requestObject)
            throws RestClientException {
        HttpEntity<Object> requestEntity = getRequestEntity(userContext, request, requestObject);
        ResponseEntity<? extends Object> response = apiRestTemplate.exchange(url, method, requestEntity, responseType);
        return response;
    }

    /**
     * This method will send a API request to the passed url and return list.
     * 
     * @param userContext - Yukon user context
     * @param request - HttpRequest
     * @param url - URL for the request
     * @param method - HttpMethod type for the request
     * @param requestObject - Optional parameter for request object.
     * @return - ResponseEntity having list of object
     * @throws RestClientException - Any exception from API call have to be handled by the caller.
     */
    public ResponseEntity<List<? extends Object>> callAPIForList(YukonUserContext userContext,
            HttpServletRequest request, String url, HttpMethod method, Object... requestObject)
            throws RestClientException {
        HttpEntity<?> requestEntity = getRequestEntity(userContext, request, requestObject);
        ResponseEntity<List<? extends Object>> response = apiRestTemplate.exchange(url, method, requestEntity,
            new ParameterizedTypeReference<List<? extends Object>>() {
            });
        return response;
    }

    // Generates a HttpEntity
    private HttpEntity<Object> getRequestEntity(YukonUserContext userContext, HttpServletRequest request,
            Object... requestObject) {
        HttpEntity<Object> requestEntity = null;
        if (requestObject.length == 1) {
            requestEntity = new HttpEntity<Object>(requestObject[0], getHttpHeaders(userContext, request));
        } else {
            requestEntity = new HttpEntity<Object>(getHttpHeaders(userContext, request));
        }
        return requestEntity;
    }

    // Generates a HttpHeaders with JWT token
    private static HttpHeaders getHttpHeaders(YukonUserContext userContext, HttpServletRequest request) {
        Integer userId = userContext.getYukonUser().getUserID();
        HttpHeaders newheaders = new HttpHeaders();
        newheaders.setContentType(MediaType.APPLICATION_JSON);

        HttpSession session = request.getSession(false);
        String token = (String) session.getAttribute(authToken);

        if (token == null || TokenHelper.checkExpiredJwt(token)) {
            token = TokenHelper.createToken(userId);
            session.setAttribute(authToken, token);
        }
        newheaders.set("Authorization", "Bearer " + token);
        return newheaders;
    }

}
