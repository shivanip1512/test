package com.cannontech.web.api;

import java.util.HashMap;
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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.common.dr.setup.ControlRawState;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.token.TokenHelper;

public class ApiRequestHelper {

    private final static String authToken = "authToken";
    @Autowired private RestTemplate apiRestTemplate;
    @Autowired private GlobalSettingDao settingDao;

    public synchronized void setProxy() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        YukonHttpProxy.fromGlobalSetting(settingDao).ifPresent(httpProxy -> {
            factory.setProxy(httpProxy.getJavaHttpProxy());
        });
        factory.setOutputStreaming(false);
        apiRestTemplate.setRequestFactory(factory);
    }

    @SuppressWarnings("rawtypes")
    public final static HashMap<Class, ParameterizedTypeReference> paramTypeRefMap = new HashMap<>();
    static {
        paramTypeRefMap.put(LiteYukonPAObject.class, new ParameterizedTypeReference<List<LiteYukonPAObject>>() {
        });
        paramTypeRefMap.put(LMDto.class, new ParameterizedTypeReference<List<LMDto>>() {
        });
        paramTypeRefMap.put(LiteGear.class, new ParameterizedTypeReference<List<LiteGear>>() {
        });
        paramTypeRefMap.put(ControlRawState.class, new ParameterizedTypeReference<List<ControlRawState>>() {
        });
    }
    
    @SuppressWarnings("rawtypes")
    public final static HashMap<Class, ParameterizedTypeReference> paramTypeObjectRefMap = new HashMap<>();
    static {
        paramTypeObjectRefMap.put(LMPaoDto.class, new ParameterizedTypeReference<SearchResults<LMPaoDto>>() {
        });
    }
    
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
     * This method will send a API request to the passed url and return the parameterized reference object.
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
    public ResponseEntity<? extends Object> callAPIForParameterizedTypeObject(YukonUserContext userContext,
            HttpServletRequest request, String url, HttpMethod method, Class<? extends Object> responseType,
            Object... requestObject) throws RestClientException {
        HttpEntity<Object> requestEntity = getRequestEntity(userContext, request, requestObject);
        ResponseEntity<? extends Object> response =
            apiRestTemplate.exchange(url, method, requestEntity, paramTypeObjectRefMap.get(responseType));
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
            HttpServletRequest request, String url, Class<? extends Object> responseType, HttpMethod method, Object... requestObject)
            throws RestClientException {
        HttpEntity<?> requestEntity = getRequestEntity(userContext, request, requestObject);
        ResponseEntity<List<? extends Object>> response = apiRestTemplate.exchange(url, method, requestEntity, paramTypeRefMap.get(responseType));
            ;
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
