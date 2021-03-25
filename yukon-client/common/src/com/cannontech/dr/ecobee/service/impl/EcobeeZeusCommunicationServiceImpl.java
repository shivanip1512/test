package com.cannontech.dr.ecobee.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.ZeusThermostatsResponse;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.helper.EcobeeZeusRequestHelper;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class EcobeeZeusCommunicationServiceImpl implements EcobeeZeusCommunicationService {
    @Autowired private EcobeeZeusRequestHelper requestHelper;
    @Autowired private GlobalSettingDao settingDao;
    // TODO: Remove hard coded String once globalsettings is created for thermostat group id.
    private static String thermostatGroupID = "f9f2b0dc9d264602aa54473733be8c16";

    @SuppressWarnings("unchecked")
    @Override
    public boolean createZeusDevice(String serialNumber) {

        String listThermostatsURL = getUrlBase() + "tstatgroups/" + thermostatGroupID + "/thermostats";
        try {
            ResponseEntity<ZeusThermostatsResponse> responseEntity = (ResponseEntity<ZeusThermostatsResponse>) requestHelper
                    .callEcobeeAPIForObject(listThermostatsURL, HttpMethod.GET, ZeusThermostatsResponse.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                ZeusThermostatsResponse response = responseEntity.getBody();
                if (CollectionUtils.isNotEmpty(response.getThermostats())) {
                    return response.getThermostats().stream()
                            .filter(stats -> stats.getId() == serialNumber && stats.getState() == "ENROLLED")
                            .findAny().isPresent();
                }
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
        return false;
    }

    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);
    }
}