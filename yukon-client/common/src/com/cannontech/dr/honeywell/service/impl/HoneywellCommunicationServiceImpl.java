package com.cannontech.dr.honeywell.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCountDao;
import com.cannontech.dr.honeywell.service.HoneywellCommunicationService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class HoneywellCommunicationServiceImpl implements HoneywellCommunicationService {

    private static final Logger log = YukonLogManager.getLogger(HoneywellCommunicationServiceImpl.class);

    @Autowired private EcobeeQueryCountDao ecobeeQueryCountDao;
    @Autowired private @Qualifier("honeywell") RestOperations restTemplate;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    private static final String createDREventGroupUrlPart = "webapi/api/drEventGroups/";
    private static final String cancelDREventForDevicesUrlPart = "api/drEvents/optout/";
    private static final String removeDeviceFromDRGroupUrlPart = "api/drEventGroups/";

    @Override
    public void addDevicesToGroup(int[] thermostatIds, int groupId) {
        log.debug("Adding honeywell device with thermostat Id " + thermostatIds);

        String url = getUrlBase() + createDREventGroupUrlPart + groupId + "/add";
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        body.add("thermostatIds", thermostatIds);

        HttpEntity<String> requestEntity = new HttpEntity<String>(new HttpHeaders());
        try {
            HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
        } catch (RestClientException ex) {
            log.debug("Add devices for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new EcobeeCommunicationException("Unable to add device. Message: \"" + ex.getMessage() + "\".");
        }

    }

    @Override
    public void cancelDREventForDevices(int[] deviceIds, int eventId, boolean immediateCancel) {
        log.debug("Cancelling DR event for devices " + deviceIds);

        String url = getUrlBase() + cancelDREventForDevicesUrlPart + eventId + "/" + immediateCancel;
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        body.add("deviceIds", deviceIds);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        HttpEntity<String> requestEntity = new HttpEntity<String>(new HttpHeaders());
        try {
            HttpEntity<String> response =
                restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.PUT, requestEntity, String.class);
        } catch (RestClientException ex) {
            log.debug("Cancel DR event for devices for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new EcobeeCommunicationException("Unable to add device. Message: \"" + ex.getMessage() + "\".");
        }
    }

    @Override
    public void removeDeviceFromDRGroup(int[] thermostatIds, int groupId) {
        log.debug("Removing specified devices from demand-response group:" + thermostatIds);

        String url = getUrlBase() + removeDeviceFromDRGroupUrlPart + removeDeviceFromDRGroupUrlPart + "/remove";
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        body.add("deviceIds", thermostatIds);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        HttpEntity<String> requestEntity = new HttpEntity<String>(new HttpHeaders());
        try {
            HttpEntity<String> response =
                restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.PUT, requestEntity, String.class);
        } catch (RestClientException ex) {
            log.debug("Removing devices from DR group for Honeywell failed with message: \"" + ex.getMessage() + "\".");
            throw new EcobeeCommunicationException("Unable to add device. Message: \"" + ex.getMessage() + "\".");
        }
    }

    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.HONEYWELL_SERVER_URL);
    }
}
