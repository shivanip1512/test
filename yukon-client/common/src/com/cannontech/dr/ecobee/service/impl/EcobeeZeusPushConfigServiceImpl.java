package com.cannontech.dr.ecobee.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.ZeusPushConfig;
import com.cannontech.dr.ecobee.service.EcobeeZeusPushConfigService;
import com.cannontech.dr.ecobee.service.helper.EcobeeZeusRequestHelper;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class EcobeeZeusPushConfigServiceImpl implements EcobeeZeusPushConfigService {
    @Autowired private EcobeeZeusRequestHelper requestHelper;
    @Autowired private GlobalSettingDao settingDao;
    private static Logger log = YukonLogManager.getLogger(EcobeeZeusPushConfigServiceImpl.class);

    /**
     * Create Push API Configuration for utility id.
     */
    @SuppressWarnings("unchecked")
    @Override
    public String createPushApiConfig() {
        try {
            String utilityId = getUtilityId();
            String pushConfigURL = getUrlBase() + "utilities/" + utilityId + "/pushconfig";
            String url = "http://abcenergy.com/ecobee/runtimedata";
            String key = "142f8801bc58d69f5100bd2779d75c9e36011244";
            ZeusPushConfig zeusPushConfig = new ZeusPushConfig(url, key);

            ResponseEntity<?> responseEntity = requestHelper.callEcobeeAPIForObject(
                    pushConfigURL, HttpMethod.POST, Map.class, zeusPushConfig);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Map<String, String> responseFields = (Map<String, String>) responseEntity.getBody();
                log.info("Sent push API configuration " + responseFields);
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
        return null;
    }

    /**
     * Show Push API Configuration for utility id.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> showPushApiConfig() {
        try {
            String utilityId = getUtilityId();
            String showPushConfigURL = getUrlBase() + "utilities/" + utilityId + "/pushconfig";

            ResponseEntity<?> responseEntity = requestHelper.callEcobeeAPIForObject(showPushConfigURL, HttpMethod.GET,
                    Object.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Map<String, String> responseFields = (Map<String, String>) responseEntity.getBody();
                log.info("Get push API configuration " + responseFields);
                return responseFields;
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String getUtilityId() throws RestClientException, EcobeeAuthenticationException {
        String utilityId = StringUtils.EMPTY;
        String showUsersURL = getUrlBase() + "auth/user";
        try {
            ResponseEntity<?> responseEntity = requestHelper.callEcobeeAPIForObject(showUsersURL, HttpMethod.GET, Object.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseFields = (Map<String, Object>) ((Map<String, Object>) responseEntity.getBody());
                utilityId = (String) responseFields.get("utility_id");
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
        return utilityId;
    }

    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);
    }
}
