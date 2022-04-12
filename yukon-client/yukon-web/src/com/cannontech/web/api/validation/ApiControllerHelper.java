package com.cannontech.web.api.validation;

import static com.cannontech.web.api.ApiRequestHelper.isSSLConfigInitialized;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;

/**
 * Helper class for controller (MVC) that called Rest Api. 
 *
 */
public class ApiControllerHelper {
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    private String webServerUrl;
    private static final Logger log = YukonLogManager.getLogger(ApiControllerHelper.class);

    @PostConstruct
    public void initialize() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING, (event) -> {
            if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.YUKON_INTERNAL_URL)) {
                clearWebUrl();
            }
        });
    }

    private void clearWebUrl() {
        setWebServerUrl(StringUtils.EMPTY);
        log.info("Yukon Internal URL changed to {}, API connection URL will be reloaded.", getYukonInternalUrl());
    }

    /**
     * Populate and return binding error from the error object received from rest call.
     */
    public BindingResult populateBindingError(BindingResult result, BindException error,
            ResponseEntity<? extends Object> errorResponse) {
        LinkedHashMap<?, ?> errorObject = (LinkedHashMap<?, ?>) errorResponse.getBody();
        ArrayList<?> fieldError = (ArrayList<?>) errorObject.get("fieldErrors");
        ArrayList<?> globalError = (ArrayList<?>) errorObject.get("globalErrors");

        fieldError.stream().forEach(e -> {
            error.rejectValue(((LinkedHashMap<?, ?>) e).get("field").toString(), StringUtils.EMPTY,
                ((LinkedHashMap<?, ?>) e).get("code").toString());
        });

        globalError.stream().forEach(e -> {
            error.reject(((LinkedHashMap<?, ?>) e).get("code").toString());
        });

        if (result.hasErrors()) {
            List<ObjectError> mvcErrors = (List<ObjectError>) result.getAllErrors();
            result = new BindException(error.getTarget(), error.getObjectName());
            result.addAllErrors(error);
            for (ObjectError objectError : mvcErrors) {
                FieldError mvcError = (FieldError) objectError;
                String fieldName = mvcError.getField();
                if (result.getFieldError(fieldName) == null) {
                    result.rejectValue(fieldName, mvcError.getCode());
                } else if (!fieldName.equals(result.getFieldError(fieldName).getField())) {
                    result.rejectValue(fieldName, mvcError.getCode());
                }
            }
        } else {
            result.addAllErrors(error);
        }
        return result;
    }

    
    /**
     * Populate and return binding error from the AppErrorModel object received from rest call.
     */
    public BindingResult populateBindingErrorForApiErrorModel(BindingResult result, BindException error,
            ResponseEntity<? extends Object> errorResponse, String keyBase) {

        LinkedHashMap<?, ?> errorObject = (LinkedHashMap<?, ?>) errorResponse.getBody();
        ArrayList<?> errors = (ArrayList<?>) errorObject.get("errors");

        if (!errors.isEmpty()) {

            for (Object e : errors) {
                LinkedHashMap<?, ?> errorMap = (LinkedHashMap<?, ?>) e;
                ArrayList paramList = ((ArrayList) ((LinkedHashMap<?, ?>) e).get("parameters"));

                String field = errorMap.get("field").toString();
                String codePostfix = errorMap.get("code").toString();
                String errorCode = keyBase + codePostfix;
                if (CollectionUtils.isNotEmpty(paramList)) {
                    error.rejectValue(field, errorCode, paramList.toArray(), StringUtils.EMPTY);
                } else {
                    error.rejectValue(field, errorCode);
                }
            }

            List<ObjectError> mvcErrors = (List<ObjectError>) result.getAllErrors();
            result = new BindException(error.getTarget(), error.getObjectName());
            result.addAllErrors(error);

            for (ObjectError objectError : mvcErrors) {
                FieldError mvcError = (FieldError) objectError;
                String fieldName = mvcError.getField();
                if (result.getFieldError(fieldName) == null) {
                    result.rejectValue(fieldName, mvcError.getCode());
                } else if (!fieldName.equals(result.getFieldError(fieldName).getField())) {
                    result.rejectValue(fieldName, mvcError.getCode());
                }
            }
        } else {
            result.addAllErrors(error);
        }
        return result;
    }

    /**
     * Set the WebServer Url
     */
    private void setWebServerUrl(String webServerUrl) {
        this.webServerUrl = webServerUrl;
    }

    /**
     * Returns the Yukon Internal Url.
     */
    private String getYukonInternalUrl() {
        return globalSettingDao.getString(GlobalSettingType.YUKON_INTERNAL_URL);
    }

    /**
     * Checks for TestConnection and returns the correct hostname:port for next api Urls.
     * @throws ApiCommunicationException
     */
    private String buildWebServerUrl(HttpServletRequest request, YukonUserContext userContext) throws ApiCommunicationException {

        String webServerApiUrl = webServerUrl;
        if (StringUtils.isEmpty(webServerApiUrl)) {
            HttpStatus httpStatus = apiConnection(request, userContext);
            if (httpStatus != HttpStatus.OK) {
                apiRequestHelper.setProxyAndSslConfig();
                httpStatus = apiConnection(request, userContext);
            }
            if (httpStatus != HttpStatus.OK) {
                throw new ApiCommunicationException("Error while communicating with Api.");
            }
            log.info("Connection with Api successful with URL: " + webServerUrl);
        }
        return webServerUrl;
    }

    /**
     * Check the connection with given Url and return the status
     */
    private HttpStatus checkUrl(YukonUserContext userContext, HttpServletRequest request, String webUrl) {
        try {
            String testConnectionUrl = webUrl + "/api/yUk0n1ranD6_";
            log.info("Checking the Api communication with URL: " + testConnectionUrl);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request,
                testConnectionUrl, HttpMethod.GET, String.class);
            return response.getStatusCode();
        } catch (RestClientException ex) {
            log.error("Error communicating with Api. " + ex.getMessage());
        }
        return HttpStatus.NOT_FOUND;
    }
    
    private HttpStatus apiConnection(HttpServletRequest request, YukonUserContext userContext) {
        HttpStatus responseCode = null;
        String webUrl = null;
        try {
            if (!getYukonInternalUrl().isEmpty()) {
                webUrl = getYukonInternalUrl();
                boolean isHttps = StringUtils.startsWithIgnoreCase(webUrl, "https");
                if (isHttps && !isSSLConfigInitialized) {
                    apiRequestHelper.setSslConfig();
                }
                responseCode = checkUrl(userContext, request, webUrl);
            }

            String serverPort = Integer.toString(request.getLocalPort());
            if (responseCode != HttpStatus.OK) {
                if (request.isSecure()) {
                    webUrl = "https://127.0.0.1:" + serverPort;
                } else {
                    webUrl = "http://127.0.0.1:" + serverPort;
                }
                if (!request.getContextPath().isEmpty()) {
                    webUrl = webUrl + request.getContextPath();
                }

                if (request.isSecure() && !isSSLConfigInitialized) {
                    apiRequestHelper.setSslConfig();
                }
                responseCode = checkUrl(userContext, request, webUrl);
            }

            if (responseCode != HttpStatus.OK) {
                InetAddress[] inetAddress = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
                for (InetAddress hostName : inetAddress) {
                    if (request.isSecure()) {
                        webUrl = "https://" + hostName.getHostAddress() + ":" + serverPort;
                    } else {
                        webUrl = "http://" + hostName.getHostAddress() + ":" + serverPort;
                    }

                    if (!request.getContextPath().isEmpty()) {
                        webUrl = webUrl + request.getContextPath();
                    }

                    if (request.isSecure() && !isSSLConfigInitialized) {
                        apiRequestHelper.setSslConfig();
                    }

                    responseCode = checkUrl(userContext, request, webUrl);
                    if (responseCode != HttpStatus.OK) {
                        continue;
                    } else {
                        break;
                    }
                }
            }
        } catch (UnknownHostException | SecurityException ex) {
            log.warn("Error while finding API host " + ex.getMessage());
        }

        if (responseCode == HttpStatus.OK) {
            setWebServerUrl(webUrl);
        }
        return responseCode;
    }

    /**
     * Generate dynamic Url for API calls
     */
    public String findWebServerUrl(HttpServletRequest request, YukonUserContext userContext, String suffixUrl)
            throws ApiCommunicationException {
        return buildWebServerUrl(request, userContext) + "/api" + suffixUrl;
    }
}
