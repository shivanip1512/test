package com.cannontech.web.api;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.device.virtualDevice.VirtualDeviceBaseModel;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.web.notificationGroup.NotificationGroup;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.model.PaginatedResponse;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.dr.setup.model.ControlAreaFilteredResult;
import com.cannontech.web.api.dr.setup.model.ControlScenarioFilteredResult;
import com.cannontech.web.api.dr.setup.model.GearFilteredResult;
import com.cannontech.web.api.dr.setup.model.LoadGroupFilteredResult;
import com.cannontech.web.api.dr.setup.model.LoadProgramFilteredResult;
import com.cannontech.web.api.dr.setup.model.MacroLoadGroupFilteredResult;
import com.cannontech.web.api.route.model.RouteBaseModel;
import com.cannontech.web.api.token.TokenHelper;

public class ApiRequestHelper {

    private static final String authToken = "authToken";
    @Autowired private RestTemplate apiRestTemplate;
    @Autowired private GlobalSettingDao settingDao;
    
    public static volatile boolean isSSLConfigInitialized = false; 
    private static final Logger log = YukonLogManager.getLogger(ApiRequestHelper.class);

    public synchronized void setProxyAndSslConfig() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        YukonHttpProxy.fromGlobalSetting(settingDao).ifPresent(httpProxy -> {
            HttpHost proxyHost = new HttpHost(httpProxy.getHost(), httpProxy.getPort());
            HttpClient httpClient = HttpClientBuilder.create()
                                                     .setProxy(proxyHost)
                                                     .setSSLSocketFactory(getSSLConnectionSocketFactory())
                                                     .build();
            factory.setHttpClient(httpClient);
        });
        apiRestTemplate.setRequestFactory(factory);
    }

    public synchronized void setSslConfig() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create()
                                                 .setSSLSocketFactory(getSSLConnectionSocketFactory())
                                                 .build();
        factory.setHttpClient(httpClient);
        apiRestTemplate.setRequestFactory(factory);
        isSSLConfigInitialized = true;
    }

    private SSLConnectionSocketFactory getSSLConnectionSocketFactory() {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLConnectionSocketFactory connectionSocketFactory = null;
        try {
            connectionSocketFactory = new SSLConnectionSocketFactory(SSLContexts.custom()
                                                                                .loadTrustMaterial(acceptingTrustStrategy)
                                                                                .build(), NoopHostnameVerifier.INSTANCE);
        } catch (KeyManagementException|NoSuchAlgorithmException|KeyStoreException e) {
            log.error("Error setting up Proxy and SSL support", e);
        }
        return connectionSocketFactory;
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
        paramTypeRefMap.put(DeviceBaseModel.class, new ParameterizedTypeReference<List<DeviceBaseModel>>() {
        });
        paramTypeRefMap.put(AttributeAssignment.class, new ParameterizedTypeReference<List<AttributeAssignment>>() {
        });
        paramTypeRefMap.put(CustomAttribute.class, new ParameterizedTypeReference<List<CustomAttribute>>() {
        });
        paramTypeRefMap.put(RouteBaseModel.class, new ParameterizedTypeReference<List<RouteBaseModel<?>>>() {
        });
        paramTypeRefMap.put(YukonLogger.class, new ParameterizedTypeReference<List<YukonLogger>>() {
        });
        paramTypeRefMap.put(TerminalBase.class, new ParameterizedTypeReference<List<TerminalBase>>() {
        });
        paramTypeRefMap.put(NotificationGroup.class, new ParameterizedTypeReference<List<NotificationGroup>>() {
        });
        paramTypeRefMap.put(Object.class, new ParameterizedTypeReference<List<Object>>() {
        });
    }
    
    @SuppressWarnings("rawtypes")
    public final static HashMap<Class, ParameterizedTypeReference> paramTypeObjectRefMap = new HashMap<>();
    static {
        paramTypeObjectRefMap.put(LMPaoDto.class, new ParameterizedTypeReference<SearchResults<LMPaoDto>>() {
        });
        paramTypeObjectRefMap.put(GearFilteredResult.class, new ParameterizedTypeReference<SearchResults<GearFilteredResult>>() {
        });
        paramTypeObjectRefMap.put(ControlAreaFilteredResult.class, new ParameterizedTypeReference<SearchResults<ControlAreaFilteredResult>>() {
        });
        paramTypeObjectRefMap.put(ControlScenarioFilteredResult.class, new ParameterizedTypeReference<SearchResults<ControlScenarioFilteredResult>>() {
        });
        paramTypeObjectRefMap.put(LoadGroupFilteredResult.class, new ParameterizedTypeReference<SearchResults<LoadGroupFilteredResult>>() {
        });
        paramTypeObjectRefMap.put(LoadProgramFilteredResult.class, new ParameterizedTypeReference<SearchResults<LoadProgramFilteredResult>>() {
        });
        paramTypeObjectRefMap.put(MacroLoadGroupFilteredResult.class, new ParameterizedTypeReference<SearchResults<MacroLoadGroupFilteredResult>>() {
        });
        paramTypeObjectRefMap.put(ProgramConstraint.class, new ParameterizedTypeReference<SearchResults<ProgramConstraint>>() {
        });
        paramTypeObjectRefMap.put(ProgramGear.class, new ParameterizedTypeReference<ProgramGear>() {
        });
        paramTypeObjectRefMap.put(VirtualDeviceBaseModel.class, new ParameterizedTypeReference<PaginatedResponse<VirtualDeviceBaseModel<?>>>() {
        });
        paramTypeObjectRefMap.put(DeviceBaseModel.class, new ParameterizedTypeReference<SearchResults<DeviceBaseModel>>() {});
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
        return response;
    }

    // Generates a HttpEntity
    private HttpEntity<Object> getRequestEntity(YukonUserContext userContext, HttpServletRequest request,
            Object... requestObject) {
        HttpEntity<Object> requestEntity = null;
        if (requestObject.length == 1) {
            requestEntity = new HttpEntity<>(requestObject[0], getHttpHeaders(userContext, request));
        } else {
            requestEntity = new HttpEntity<>(getHttpHeaders(userContext, request));
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
