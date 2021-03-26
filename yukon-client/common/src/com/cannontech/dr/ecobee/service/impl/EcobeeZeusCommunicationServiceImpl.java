package com.cannontech.dr.ecobee.service.impl;

import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.ZeusThermostatState;
import com.cannontech.dr.ecobee.message.ZeusThermostatsResponse;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.helper.EcobeeZeusRequestHelper;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class EcobeeZeusCommunicationServiceImpl implements EcobeeZeusCommunicationService {
    @Autowired private EcobeeZeusRequestHelper requestHelper;
    @Autowired private GlobalSettingDao settingDao;
    // TODO: Remove hard coded String once globalsettings is created for program ID.
    private static String programId = "2df7e7a53193438a8a3aa4c919475ac0";

    @SuppressWarnings("unchecked")
    @Override
    public boolean isDeviceRegistered(String serialNumber) {
        try {
            String thermostatGroupID = retrieveThermostatGroupID();
            String listThermostatsURL = getUrlBase() + "tstatgroups/" + thermostatGroupID + "/thermostats?enrollment_state="
                    + ZeusThermostatState.ENROLLED + "&thermostat_ids=" + serialNumber;

            ResponseEntity<ZeusThermostatsResponse> responseEntity = (ResponseEntity<ZeusThermostatsResponse>) requestHelper
                    .callEcobeeAPIForObject(listThermostatsURL, HttpMethod.GET, ZeusThermostatsResponse.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                ZeusThermostatsResponse response = responseEntity.getBody();
                if (CollectionUtils.isNotEmpty(response.getThermostats())) {
                    return response.getThermostats().stream()
                            .filter(stats -> stats.getSerialNumber() == serialNumber
                                    && stats.getState() == ZeusThermostatState.ENROLLED)
                            .findAny()
                            .isPresent();
                }
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
        return false;
    }

    /**
     * Retrieve root_tstatgroup_id from Ecobee by using programID.
     */
    @SuppressWarnings("unchecked")
    private String retrieveThermostatGroupID() throws RestClientException, EcobeeAuthenticationException {
        String showProgramURL = getUrlBase() + "programs/" + programId;

        ResponseEntity<?> programResponse = requestHelper.callEcobeeAPIForObject(showProgramURL, HttpMethod.GET, Object.class);
        if (programResponse.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseMap = (Map<String, Object>) ((Map<String, Object>) programResponse.getBody())
                    .get("program");
            return (String) responseMap.get("root_tstatgroup_id");
        } else {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.");
        }
    }

    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);
    }
}