package com.cannontech.dr.ecobee.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.CriteriaSelector;
import com.cannontech.dr.ecobee.message.DrEventState;
import com.cannontech.dr.ecobee.message.EcoplusSelector;
import com.cannontech.dr.ecobee.message.Selector;

import com.cannontech.dr.ecobee.message.ZeusCreatePushConfig;
import com.cannontech.dr.ecobee.message.ZeusDemandResponseRequest;
import com.cannontech.dr.ecobee.message.ZeusEvent;

import com.cannontech.dr.ecobee.message.ZeusGroup;
import com.cannontech.dr.ecobee.message.ZeusGroupResponse;
import com.cannontech.dr.ecobee.message.ZeusShowPushConfig;
import com.cannontech.dr.ecobee.message.ZeusThermostat;
import com.cannontech.dr.ecobee.message.ZeusThermostatGroup;
import com.cannontech.dr.ecobee.message.ZeusThermostatState;
import com.cannontech.dr.ecobee.message.ZeusThermostatsResponse;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.model.EcobeeSetpointDrParameters;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusGroupService;
import com.cannontech.dr.ecobee.service.helper.EcobeeZeusRequestHelper;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.enrollment.exception.EnrollmentException;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.core.JsonProcessingException;

public class EcobeeZeusCommunicationServiceImpl implements EcobeeZeusCommunicationService {
    private static Logger log = YukonLogManager.getLogger(EcobeeZeusCommunicationServiceImpl.class);
    @Autowired private EcobeeZeusRequestHelper requestHelper;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private EcobeeZeusGroupService ecobeeZeusGroupService;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    // TODO: Remove hard coded String once globalsettings is created for program ID.
    private static String programId = "2df7e7a53193438a8a3aa4c919475ac0";
    private static final int thresholdThermostatCount = 9900;
    private static final String YUKON_CYCLE_EVENT_NAME = "yukonCycle";

    @Override
    public boolean isDeviceRegistered(String serialNumber) {
        try {
            String thermostatGroupID = retrieveThermostatGroupID();
            String listThermostatsURL = getUrlBase() + "tstatgroups/" + thermostatGroupID + "/thermostats?enrollment_state="
                    + ZeusThermostatState.ENROLLED + "&thermostat_ids=" + serialNumber;

            ResponseEntity<ZeusThermostatsResponse> responseEntity = (ResponseEntity<ZeusThermostatsResponse>) requestHelper
                    .callEcobeeAPIForObject(listThermostatsURL, HttpMethod.GET, ZeusThermostatsResponse.class);
            return responseEntity.getStatusCode() == HttpStatus.OK
                    && CollectionUtils.isNotEmpty(responseEntity.getBody().getThermostats());
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }

    /**
     * Removes the thermostat(s) from the specified thermostat group. When a thermostat is deleted from a group, 
     * it's state changes to "REMOVED".
     */
    @Override
    public void deleteDevice(String serialNumber) {
        try {
            String thermostatGroupID = retrieveThermostatGroupID();
            String deleteThermostatsURL = getUrlBase() + "tstatgroups/" + thermostatGroupID + "/thermostats?thermostat_ids=" + serialNumber;

            requestHelper.callEcobeeAPIForObject(deleteThermostatsURL, HttpMethod.DELETE, Object.class);
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
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

    @Override
    public void enroll(int lmGroupId, String serialNumber, int inventoryId) {
        String zeusGroupId = ecobeeZeusGroupService.getZeusGroupIdForLmGroup(lmGroupId);
        // For a new customer, zeusGroupId might be empty. So create a new Ecobee group.
        if (StringUtils.isEmpty(zeusGroupId)) {
            zeusGroupId = createEcobeeGroup(lmGroupId, serialNumber);
        }
        synchronized (this) {
            int deviceCount = ecobeeZeusGroupService.getDeviceCount(zeusGroupId);
            if (deviceCount < thresholdThermostatCount) {
                addThermostatToGroup(zeusGroupId, serialNumber, inventoryId);
            } else {
                String groupName = cache.getAllPaosMap().get(lmGroupId).getPaoName();
                throw new EnrollmentException("Yukon Ecobee group " + groupName + " can not contain more than "
                        + thresholdThermostatCount
                        + " thermostats. Create new group and attach it to the program before enrolling.");
            }
        }
    }

    /**
     * Create a Ecobee Zeus group and return the group ID.
     */
    @SuppressWarnings("unchecked")
    private String createEcobeeGroup(int lmGroupId, String serialNumber) {
        String zeusGroupId = StringUtils.EMPTY;
        try {
            String createThermostatGroupURL = getUrlBase() + "tstatgroups";

            CriteriaSelector criteriaSelector = new CriteriaSelector(Selector.IDENTIFIER.getType(), Arrays.asList(serialNumber));
            String groupName = cache.getAllPaosMap().get(lmGroupId).getPaoName();
            ZeusGroup group = new ZeusGroup(groupName, programId);
            ZeusThermostatGroup zeusThermostatGroup = new ZeusThermostatGroup(group, criteriaSelector);

            ResponseEntity<Map> responseEntity = (ResponseEntity<Map>) requestHelper.callEcobeeAPIForObject(
                    createThermostatGroupURL, HttpMethod.POST, Map.class, zeusThermostatGroup);

            if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
                Map<String, String> responseFields = (Map<String, String>) responseEntity.getBody().get("group");
                zeusGroupId = responseFields.get("id");
                groupName = responseFields.get("name");
                ecobeeZeusGroupService.mapGroupIdToZeusGroup(lmGroupId, zeusGroupId, groupName);
                log.info("Zeus group with ID: {} and Name: {} created successfully on Ecobee and Mapped to Yukon LM group {}.",
                        zeusGroupId, groupName, groupName);
            } else if (responseEntity.getStatusCode() == HttpStatus.PARTIAL_CONTENT) {
                Map<String, Object> response = (Map<String, Object>) responseEntity.getBody();
                List<String> failedThermostatIds = (List<String>) response.get("failed_thermostat_ids");
                failedThermostatIds.stream()
                        .forEach(thermostatId -> log.error("Unable to create the thermostat group with thermostat id {}",
                                thermostatId));
                throw new EnrollmentException("Enrolment not completed successfully for all the thermostats.");
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
        return zeusGroupId;
    }

    @SuppressWarnings("unchecked")
    private void addThermostatToGroup(String zeusGroupId, String serialNumber, int inventoryId) {
        List<Integer> inventoryIds = ecobeeZeusGroupService.getInventoryIdsForZeusGrouID(zeusGroupId);
        List<String> thermostatIds = new ArrayList<String>();
        for (int id : inventoryIds) {
            thermostatIds.add(lmHardwareBaseDao.getSerialNumberForInventoryId(id));
        }
        // Before Update, add the thermostat which we want to enroll.
        thermostatIds.add(serialNumber);
        ResponseEntity<? extends Object> responseEntity = updateThermostatGroup(zeusGroupId, thermostatIds);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            log.info("Thermostat serial number {} mapped successfully to the zeus group ID {}", serialNumber, zeusGroupId);
            ecobeeZeusGroupService.mapInventoryToZeusGroupId(inventoryId, zeusGroupId);
        } else if (responseEntity.getStatusCode() == HttpStatus.PARTIAL_CONTENT) {
            Map<String, Object> response = (Map<String, Object>) responseEntity.getBody();
            List<String> failedThermostatIds = (List<String>) response.get("failed_thermostat_ids");
            failedThermostatIds.stream()
                    .forEach(thermostatId -> log.error("Unable to update the the thermostat: {} to the Zeus group: {}",
                            thermostatId, zeusGroupId));
            throw new EnrollmentException("Enrolment not completed successfully for all the thermostats.");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void unEnroll(int lmGroupId, String serialNumber, int inventoryId) {
        try {
            String zeusGroupId = ecobeeZeusGroupService.getZeusGroupIdForLmGroup(lmGroupId);

            String deleteThermostatsURL = getUrlBase() + "tstatgroups/" + zeusGroupId + "/thermostats?thermostat_ids="
                    + serialNumber;
            ResponseEntity<? extends Object> responseEntiry = requestHelper.callEcobeeAPIForObject(deleteThermostatsURL,
                    HttpMethod.DELETE, Object.class);
            if (responseEntiry.getStatusCode() == HttpStatus.OK) {
                Map<String, Integer> response = (Map<String, Integer>) responseEntiry.getBody();
                if (response.get("deleted") == 1) {
                    log.info("Thermostat serial number {} removed successfully from the zeus group ID {}", serialNumber,
                            zeusGroupId);
                    ecobeeZeusGroupService.deleteZeusGroupMappingForInventoryId(inventoryId);
                } else {
                    throw new EnrollmentException("Error occurred while unenrolling thermostat " + serialNumber
                            + " from the zeus group ID " + zeusGroupId);
                }
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }

    private ResponseEntity<? extends Object> updateThermostatGroup(String zeusGroupId, List<String> thermostatIds) {

        String updateThermostatURL = getUrlBase() + "tstatgroups/" + zeusGroupId;

        String groupName = ecobeeZeusGroupService.zeusGroupName(zeusGroupId);
        CriteriaSelector criteriaSelector = new CriteriaSelector(Selector.IDENTIFIER.getType() , thermostatIds);
        ZeusGroup group = new ZeusGroup(groupName, programId);
        ZeusThermostatGroup zeusThermostatGroup = new ZeusThermostatGroup(group, criteriaSelector);

        try {
            return requestHelper.callEcobeeAPIForObject(updateThermostatURL, HttpMethod.PUT, Object.class,
                    zeusThermostatGroup);

        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }

    
    @Override
    public void createPushApiConfiguration(String reportingUrl, String privateKey) {
        try {
            String utilityId = getUtilityId();
            String pushConfigURL = getUrlBase() + "utilities/" + utilityId + "/pushconfig";
            ZeusCreatePushConfig zeusPushConfig = new ZeusCreatePushConfig();
            zeusPushConfig.setPrivateKey(privateKey);
            zeusPushConfig.setReportingUrl(reportingUrl);

            requestHelper.callEcobeeAPIForObject(pushConfigURL, HttpMethod.POST, Object.class, zeusPushConfig);

        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }
    
    @Override
    public ZeusShowPushConfig showPushApiConfiguration() {
        try {
            String utilityId = getUtilityId();
            String showPushConfigURL = getUrlBase() + "utilities/" + utilityId + "/pushconfig";

            ResponseEntity<ZeusShowPushConfig> responseEntity = (ResponseEntity<ZeusShowPushConfig>) requestHelper
                    .callEcobeeAPIForObject(showPushConfigURL, HttpMethod.GET, ZeusShowPushConfig.class);
            return responseEntity.getBody();

        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
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

    @Override
    public String sendDutyCycleDR(EcobeeDutyCycleDrParameters parameters) {
        String eventId = StringUtils.EMPTY;

        String issueDemandResponseUrl = getUrlBase() + "events/dr";
        ZeusDemandResponseRequest dutyCycleDr = new ZeusDemandResponseRequest(buildZeusEvent(parameters.getGroupId(),
                parameters.getStartTime(), parameters.getEndTime(), parameters.isOptional()));
        dutyCycleDr.getEvent().setDutyCyclePercentage(parameters.getDutyCyclePercent());
        if (log.isDebugEnabled()) {
            try {
                log.debug("Sending ecobee duty cycle DR with body: {}", JsonUtils.toJson(dutyCycleDr));
            } catch (JsonProcessingException e) {
                log.warn("Error parsing json in debug.", e);
            }
        }
        try {
            ResponseEntity<ZeusDemandResponseRequest> zeusDrResponseEntity = requestHelper
                    .callEcobeeAPIForObject(issueDemandResponseUrl, HttpMethod.POST, ZeusDemandResponseRequest.class,
                            dutyCycleDr);
            if (zeusDrResponseEntity.getStatusCode() == HttpStatus.CREATED) {
                eventId = zeusDrResponseEntity.getBody().getEvent().getId();
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
        return eventId;
    }

    @Override
    public String sendSetpointDR(EcobeeSetpointDrParameters parameters) {
        String eventId = StringUtils.EMPTY;

        String issueDemandResponseUrl = getUrlBase() + "events/dr";
        ZeusDemandResponseRequest setpointDr = new ZeusDemandResponseRequest(buildZeusEvent(parameters.getGroupId(),
                parameters.getStartTime(), parameters.getStopTime(), parameters.isOptional()));
        setpointDr.getEvent().setIsHeatingEvent(parameters.istempOptionHeat());
        setpointDr.getEvent().setRelativeTemp((float) parameters.getTempOffset());
        if (log.isDebugEnabled()) {
            try {
                log.debug("Sending ecobee set point DR with body: {}", JsonUtils.toJson(setpointDr));
            } catch (JsonProcessingException e) {
                log.warn("Error parsing json in debug.", e);
            }
        }
        try {
            ResponseEntity<ZeusDemandResponseRequest> zeusDrResponseEntity = requestHelper
                    .callEcobeeAPIForObject(issueDemandResponseUrl, HttpMethod.POST, ZeusDemandResponseRequest.class, setpointDr);
            if (zeusDrResponseEntity.getStatusCode() == HttpStatus.CREATED) {
                eventId = zeusDrResponseEntity.getBody().getEvent().getId();
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
        return eventId;
    }

    /**
     * Method to build Zeus event.
     */
    private ZeusEvent buildZeusEvent(int groupId, Instant startTime, Instant stopTime, boolean optional) {
        DateTimeFormatter dateTimeFormmater = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss");
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        String eventDisplayMessage = messageSourceAccessor.getMessage("yukon.web.modules.dr.ecobee.eventDisplayMessage");
        String zeusGroupId = ecobeeZeusGroupService.getZeusGroupIdForLmGroup(groupId);

        ZeusEvent event = new ZeusEvent();
        event.setName(YUKON_CYCLE_EVENT_NAME);
        event.setTstatGroupId(zeusGroupId);

        event.setEventStartTime(startTime.toString(dateTimeFormmater));
        Duration res = new Duration(startTime, stopTime);
        event.setDurationInMinutes(res.toStandardMinutes().getMinutes());

        event.setMandatory(!optional);

        event.setMessage(eventDisplayMessage);
        event.setSendEmail(settingDao.getBoolean(GlobalSettingType.ECOBEE_SEND_NOTIFICATIONS));

        event.setEcoplusSelector(EcoplusSelector.ALL);
        event.setState(DrEventState.SUBMITTED_DIRECTLY);
        event.setShowThermostat(true);
        event.setShowWeb(true);
        return event;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ZeusGroup> getAllGroups() {
        String getAllGroups = getUrlBase() + "tstatgroups";
        List<ZeusGroup> zeusGroups = new ArrayList<>();
        try {
            ResponseEntity<ZeusGroupResponse> responseEntity = (ResponseEntity<ZeusGroupResponse>) requestHelper
                    .callEcobeeAPIForObject(getAllGroups, HttpMethod.GET, ZeusGroupResponse.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                ZeusGroupResponse zeusGroupResponse = responseEntity.getBody();
                zeusGroups = zeusGroupResponse.getGroups();
            } 
            return zeusGroups;
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }

    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ZeusThermostat> getThermostatsInGroup(String thermostatGroupID) {
        String getThermostatsURL = getUrlBase() + "tstatgroups/" + thermostatGroupID + "/thermostats";
        List<ZeusThermostat> zeusThermostats = new ArrayList<>();
        try {
            ResponseEntity<ZeusThermostatsResponse> responseEntity = (ResponseEntity<ZeusThermostatsResponse>) requestHelper
                    .callEcobeeAPIForObject(getThermostatsURL, HttpMethod.GET, ZeusThermostatsResponse.class);
            
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                ZeusThermostatsResponse zeusGroupResponse = responseEntity.getBody();
                zeusThermostats = zeusGroupResponse.getThermostats();
            } 
            return zeusThermostats;
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }
    
    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);

    }

}