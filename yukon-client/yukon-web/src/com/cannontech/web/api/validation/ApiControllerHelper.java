package com.cannontech.web.api.validation;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.dr.setup.LoadGroupSetupController;

/**
 * Helper class for controller (MVC) that called Rest Api. 
 *
 */
public class ApiControllerHelper {
    @Autowired GlobalSettingDao globalSettingDao;
    @Autowired private ApiRequestHelper apiRequestHelper;
    private static String webserverURL = StringUtils.EMPTY;
    private static final Logger log = YukonLogManager.getLogger(ApiControllerHelper.class);
    
    /**
     * Populate binding error from the error object received from rest call. 
     */
    public void populateBindingError(BindingResult result, BindException error, ResponseEntity<? extends Object> errorResponse) {
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
        result.addAllErrors(error);
    }

    /**
     * Set the webserver URL
     */
    private static void setWebserverURL(String webserverURL) {
        ApiControllerHelper.webserverURL = webserverURL;
    }

    /**
     * Returns the Yukon Internal Url.
     */
    private String getYukonInternalUrl() {
        return globalSettingDao.getString(GlobalSettingType.YUKON_INTERNAL_URL);
    }

    /**
     * Checks for TestConnection and returns the correct hostname:port for next api URLs.
     * @throws ApiCommunicationException
     */
    private String getWebServerURL(HttpServletRequest request, YukonUserContext userContext)
            throws ApiCommunicationException {
        if (webserverURL.isEmpty()) {
            String serverPort = Integer.toString(request.getLocalPort());
            String webUrl = "http://127.0.0.1:" + serverPort;
            if (!request.getContextPath().isEmpty()) {
                webUrl = webUrl + request.getContextPath();
            }
            HttpStatus responseCode = checkURL(userContext, request, webUrl);
            if (responseCode != HttpStatus.OK) {
                if (!getYukonInternalUrl().isEmpty()) {
                    webUrl = getYukonInternalUrl();
                    responseCode = checkURL(userContext, request, webUrl);
                }
            }
            if (responseCode == HttpStatus.OK) {
                setWebserverURL(webUrl);
            } else {
                throw new ApiCommunicationException(" Check Yukon Internal URL.");
            }
        }
        return webserverURL;
    }

    /* Check the connection with given URL and return the status */
    private HttpStatus checkURL(YukonUserContext userContext, HttpServletRequest request, String webUrl) {
        try {
            String testConnectionURL = webUrl + "/test";
            log.info("Checking the Api communication with URL: " + testConnectionURL);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request,
                testConnectionURL, HttpMethod.GET, String.class);
            return response.getStatusCode();
        } catch (RestClientException ex) {
            log.error("Error communicating with Api. " + ex.getMessage());
        }
        return HttpStatus.NOT_FOUND;
    }

    /**
     * Generate dynamic URL for API calls
     */
    public String findwebServerURL(HttpServletRequest request, YukonUserContext userContext, String suffixURL)
            throws ApiCommunicationException {
        return getWebServerURL(request, userContext) + "/api" + suffixURL;
    }
}
