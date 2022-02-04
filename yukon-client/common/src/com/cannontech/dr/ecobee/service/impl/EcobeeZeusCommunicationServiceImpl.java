package com.cannontech.dr.ecobee.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.CriteriaSelector;
import com.cannontech.dr.ecobee.message.EcoplusSelector;
import com.cannontech.dr.ecobee.message.Selector;
import com.cannontech.dr.ecobee.message.ZeusCreateDevice;
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
import com.cannontech.dr.ecobee.model.EcobeePlusDrParameters;
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
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    private ConcurrentHashMap<String, String> thermostatGroupIDCache = new ConcurrentHashMap<String, String>(1);
    private static final String thermostatGroupIDCacheKey = "thermostatGroupIDCacheKey";
    private static final int thresholdThermostatCount = 4900;
    private static final String YUKON_CYCLE_EVENT_NAME = "yukonCycle";

    @Override
    public void createDevice(String serialNumber) {
        try {
            ZeusThermostat zeusThermostat = retrieveThermostatFromRootGroup(serialNumber);
            if (zeusThermostat.getState() != ZeusThermostatState.ENROLLED) {
                String thermostatGroupID = retrieveThermostatGroupID();
                String listThermostatsURL = getUrlBase() + "tstatgroups/" + thermostatGroupID + "/thermostats";
                ZeusCreateDevice device = new ZeusCreateDevice(ZeusThermostatState.NOT_YET_CONNECTED, List.of(serialNumber));

                ResponseEntity<Map> responseEntity = requestHelper.callEcobeeAPIForObject(listThermostatsURL, HttpMethod.PUT,
                        Map.class, device);
                if (responseEntity.getStatusCode() != HttpStatus.OK || (int) responseEntity.getBody().get("added") != 1) {
                    log.error("Not creating the device as the provided thermostat serial number is invalid.");
                    throw new DeviceCreationException("Invalid thermostat serial number.");
                }
            } else {
                log.info("Not making Add Thermostat API call as thermostat {} is already in ENROLLED state in group {}.",
                        serialNumber, thermostatGroupIDCache.get(thermostatGroupIDCacheKey));
            }
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
            String deleteThermostatsURL = getUrlBase() + "tstatgroups/" + thermostatGroupID + "/thermostats?thermostat_ids="
                    + serialNumber;

            requestHelper.callEcobeeAPIForObject(deleteThermostatsURL, HttpMethod.DELETE, Object.class);
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }
    
    /**
     * Delete a thermostat group
     */
    @Override
    public void deleteGroup(String zeusGroupId) {
        try {
            String deleteGroupURL = getUrlBase() + "tstatgroups/" + zeusGroupId;
            requestHelper.callEcobeeAPIForObject(deleteGroupURL, HttpMethod.DELETE, Object.class);
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }

    /**
     * Return root thermostat group ID for the configured programID.
     */
    public String retrieveThermostatGroupID() throws RestClientException, EcobeeAuthenticationException {
        if (StringUtils.isBlank(thermostatGroupIDCache.get(thermostatGroupIDCacheKey))) {
            thermostatGroupIDCache.put(thermostatGroupIDCacheKey, retrieveRootTstatGroupID());
            asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
                if (settingDao.isDbChangeForSetting(event, GlobalSettingType.ECOBEE_PROGRAM_ID)) {
                    try {
                        thermostatGroupIDCache.put(thermostatGroupIDCacheKey, retrieveRootTstatGroupID());
                    } catch (RestClientException | EcobeeAuthenticationException e) {
                        throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
                    }
                }
            });
        }
        return thermostatGroupIDCache.get(thermostatGroupIDCacheKey);
    }

    /**
     * Retrieve root_tstatgroup_id from Ecobee by using programID.
     */
    @SuppressWarnings("unchecked")
    private String retrieveRootTstatGroupID() throws RestClientException, EcobeeAuthenticationException {
        String showProgramURL = getUrlBase() + "programs/" + getZeusProgramId();

        ResponseEntity<?> programResponse = requestHelper.callEcobeeAPIForObject(showProgramURL, HttpMethod.GET,
                Object.class);
        if (programResponse.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseMap = (Map<String, Object>) ((Map<String, Object>) programResponse.getBody())
                    .get("program");
            return (String) responseMap.get("root_tstatgroup_id");
        } else {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.");
        }
    }

    @Override
    public void enroll(int lmGroupId, String serialNumber, int inventoryId, int programId, boolean updateDeviceMapping) {
        synchronized (this) {
            ZeusThermostatState thermostatState = retrieveThermostatFromRootGroup(serialNumber).getState();
            if (thermostatState == ZeusThermostatState.ENROLLED || thermostatState == ZeusThermostatState.NOT_YET_CONNECTED) {
                String zeusGroupId = StringUtils.EMPTY;
                List<String> zeusGroupIds = ecobeeZeusGroupService.getZeusGroupIdsForLmGroup(lmGroupId, programId);
                // For new system and when there are no suitable Ecobee group available for enrollment, create a new Ecobee group.
                if (CollectionUtils.isEmpty(zeusGroupIds) || getSuitableGroupForEnrollment(zeusGroupIds).isEmpty()) {
                    zeusGroupId = createEcobeeGroup(lmGroupId, serialNumber, programId);
                } else {
                    zeusGroupId = getSuitableGroupForEnrollment(zeusGroupIds);
                }
                addThermostatToGroup(zeusGroupId, serialNumber, inventoryId, updateDeviceMapping);
                if (ecobeeZeusGroupService.shouldUpdateProgramId(zeusGroupId)) {
                    ecobeeZeusGroupService.updateProgramId(zeusGroupId, programId);
                }
            } else {
                throw new EnrollmentException("Enrollment failed as serial number " +
                        serialNumber + " is not in ENROLLED or NOT_YET_CONNECTED state.");
            }
        }
    }

    /**
     * Retrieve thermostat from the root group. 
     */
    public ZeusThermostat retrieveThermostatFromRootGroup(String serialNumber) {
        try {
            String rootThermostatGroupID = retrieveThermostatGroupID();
            List<ZeusThermostat> zeusThermostats = getThermostatsInGroup(rootThermostatGroupID, serialNumber);
            if (CollectionUtils.size(zeusThermostats) == 1) {
                return zeusThermostats.get(0);
            } else {
                throw new EcobeeCommunicationException("Number of device(s) found for the serial number in root"
                        + " thermostat group: " + CollectionUtils.size(zeusThermostats));
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }

    /**
     * Return the 1st group which can accommodate the thermostat. Return Empty String if all group have reached the threshold limit.
     */
    private String getSuitableGroupForEnrollment(List<String> zeusGroupIds) {
        String zeusGroupId = StringUtils.EMPTY;
        for (String groupId : zeusGroupIds) {
            if (ecobeeZeusGroupService.getDeviceCount(groupId) < thresholdThermostatCount) {
                zeusGroupId = groupId;
                break;
            }
        }
        return zeusGroupId;
    }

    /**
     * Create a Ecobee Zeus group and return the group ID.
     */
    @SuppressWarnings("unchecked")
    private String createEcobeeGroup(int lmGroupId, String serialNumber, int programId) {
        String zeusGroupId = StringUtils.EMPTY;
        try {
            String createThermostatGroupURL = getUrlBase() + "tstatgroups";

            CriteriaSelector criteriaSelector = new CriteriaSelector(Selector.IDENTIFIER.getType(), Arrays.asList(serialNumber));
            String newName = ecobeeZeusGroupService.getNextGroupName(lmGroupId, programId);
            ZeusGroup group = new ZeusGroup(newName, getZeusProgramId());
            ZeusThermostatGroup zeusThermostatGroup = new ZeusThermostatGroup(group, criteriaSelector);

            ResponseEntity<Map> responseEntity = (ResponseEntity<Map>) requestHelper.callEcobeeAPIForObject(
                    createThermostatGroupURL, HttpMethod.POST, Map.class, zeusThermostatGroup);

            if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
                Map<String, String> responseFields = (Map<String, String>) responseEntity.getBody().get("group");
                zeusGroupId = responseFields.get("id");
                String groupName = responseFields.get("name");
                String yukonGroupName = cache.getAllPaosMap().get(lmGroupId).getPaoName();
                ecobeeZeusGroupService.mapGroupIdToZeusGroup(lmGroupId, zeusGroupId, groupName, programId);
                log.info("Zeus group with ID: {} and Name: {} created successfully on Ecobee and Mapped to Yukon LM group {}.",
                        zeusGroupId, groupName, yukonGroupName);
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

    @Override
    @SuppressWarnings("unchecked")
    public void addThermostatToGroup(String zeusGroupId, String serialNumber, int inventoryId, boolean updateDeviceMapping) {
        List<Integer> inventoryIds = ecobeeZeusGroupService.getInventoryIdsForZeusGrouID(zeusGroupId);
        List<String> thermostatIds = new ArrayList<String>();
        for (int id : inventoryIds) {
            thermostatIds.add(lmHardwareBaseDao.getSerialNumberForInventoryId(id));
        }
        // Before Update, add the thermostat which we want to enroll.
        // In case of sync issues thermostat will be already available in zeus mapping table, So don't add it again.
        if (!thermostatIds.contains(serialNumber)) {
            thermostatIds.add(serialNumber);
        }
        ResponseEntity<? extends Object> responseEntity = updateThermostatGroup(zeusGroupId, thermostatIds);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            log.info("Thermostat serial number {} mapped successfully to the zeus group ID {}", serialNumber, zeusGroupId);
            // Insert device to Zeus mapping table only in case of enrollment. For Cancel OupOut, do not insert the mapping.
            if (updateDeviceMapping) {
                ecobeeZeusGroupService.mapInventoryToZeusGroupId(inventoryId, zeusGroupId);
            }
        } else if (responseEntity.getStatusCode() == HttpStatus.PARTIAL_CONTENT) {
            Map<String, Object> response = (Map<String, Object>) responseEntity.getBody();
            List<String> failedThermostatIds = (List<String>) response.get("failed_thermostat_ids");
            failedThermostatIds.stream()
                    .forEach(thermostatId -> log.error("Unable to update the the thermostat: {} to the Zeus group: {}",
                            thermostatId, zeusGroupId));
            throw new EnrollmentException("Enrolment not completed successfully for all the thermostats.");
        }
    }

    @Override
    public void unEnroll(Set<Integer> lmGroupIds, String serialNumber, int inventoryId, boolean updateDeviceMapping) {
        for (int lmGroupId : lmGroupIds) {
            int programId = ecobeeZeusGroupService.getProgramIdToUnenroll(inventoryId, lmGroupId);
            if (programId != EcobeeZeusGroupService.DEFAULT_PROGRAM_ID) {
                String zeusGroupId = ecobeeZeusGroupService.getZeusGroupId(lmGroupId, inventoryId, programId);
                removeThermostatFromGroup(zeusGroupId, serialNumber, inventoryId, updateDeviceMapping);
                List<Integer> inventories = ecobeeZeusGroupService.getInventoryIdsForZeusGrouID(zeusGroupId);
                if (CollectionUtils.isEmpty(inventories)) {
                    ecobeeZeusGroupService.deleteZeusGroupMapping(zeusGroupId);
                    deleteGroup(zeusGroupId);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeThermostatFromGroup(String zeusGroupId, String serialNumber, int inventoryId, boolean updateDeviceMapping) {
        try {
            String deleteThermostatsURL = getUrlBase() + "tstatgroups/" + zeusGroupId + "/thermostats?thermostat_ids="
                    + serialNumber;
            ResponseEntity<? extends Object> responseEntiry = requestHelper.callEcobeeAPIForObject(deleteThermostatsURL,
                    HttpMethod.DELETE, Object.class);
            if (responseEntiry.getStatusCode() == HttpStatus.OK) {
                Map<String, Integer> response = (Map<String, Integer>) responseEntiry.getBody();
                if (response.get("deleted") == 1) {
                    log.info("Thermostat serial number {} removed successfully from the zeus group ID {}", serialNumber,
                            zeusGroupId);
                    // Delete device to Zeus mapping only in case of Unenrollment. For OupOut, do not remove the mapping.
                    if (updateDeviceMapping) {
                        ecobeeZeusGroupService.deleteZeusGroupMappingForInventory(inventoryId, zeusGroupId);
                    }
                } else {
                    throw new EnrollmentException("Error occurred while unenrolling/opting out thermostat " + serialNumber
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
        CriteriaSelector criteriaSelector = new CriteriaSelector(Selector.IDENTIFIER.getType(), thermostatIds);
        ZeusGroup group = new ZeusGroup(groupName, getZeusProgramId());
        ZeusThermostatGroup zeusThermostatGroup = new ZeusThermostatGroup(group, criteriaSelector);

        try {
            return requestHelper.callEcobeeAPIForObject(updateThermostatURL, HttpMethod.PUT, Object.class,
                    zeusThermostatGroup);

        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }

    @Override
    public void createThermostatGroup(String oldZeusGroupId, List<Integer> inventoryIds) {

        List<String> thermostatIds = new ArrayList<String>();
        for (int id : inventoryIds) {
            thermostatIds.add(lmHardwareBaseDao.getSerialNumberForInventoryId(id));
        }
        String createThermostatURL = getUrlBase() + "tstatgroups";

        String groupName = ecobeeZeusGroupService.zeusGroupName(oldZeusGroupId);
        CriteriaSelector criteriaSelector = new CriteriaSelector(Selector.IDENTIFIER.getType(), thermostatIds);
        ZeusGroup group = new ZeusGroup(groupName, getZeusProgramId());
        ZeusThermostatGroup zeusThermostatGroup = new ZeusThermostatGroup(group, criteriaSelector);

        try {
            ResponseEntity<Map> responseEntity = requestHelper.callEcobeeAPIForObject(createThermostatURL, HttpMethod.POST,
                    Map.class, zeusThermostatGroup);
            if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
                Map<String, String> responseFields = (Map<String, String>) responseEntity.getBody().get("group");
                String newZeusGroupId = responseFields.get("id");
                ecobeeZeusGroupService.updateZeusGroupId(oldZeusGroupId, newZeusGroupId);
            }

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
    public void sendDutyCycleDR(EcobeeDutyCycleDrParameters parameters) {
        String eventId = StringUtils.EMPTY;
        String issueDemandResponseUrl = getUrlBase() + "events/dr";

        List<String> zeusGroupIds = ecobeeZeusGroupService.getZeusGroupIdsForLmGroup(parameters.getGroupId(),
                parameters.getProgramId());
        for (String zeusGroupId : zeusGroupIds) {
            ZeusDemandResponseRequest dutyCycleDr = new ZeusDemandResponseRequest(buildZeusEvent(zeusGroupId,
                    parameters.getStartTime(), parameters.getEndTime(), parameters.isMandatory()));
            dutyCycleDr.getEvent().setDutyCyclePercentage(parameters.getDutyCyclePercent());
            dutyCycleDr.getEvent().setRandomTimeSeconds(parameters.getRandomTimeSeconds());
            dutyCycleDr.getEvent().setEcoplusSelector(EcoplusSelector.ALL);
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
                    ecobeeZeusGroupService.updateEventId(eventId, zeusGroupId);
                }
            } catch (RestClientException | EcobeeAuthenticationException e) {
                throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
            }
        }
    }

    @Override
    public void sendSetpointDR(EcobeeSetpointDrParameters parameters) {
        String eventId = StringUtils.EMPTY;
        String issueDemandResponseUrl = getUrlBase() + "events/dr";

        List<String> zeusGroupIds = ecobeeZeusGroupService.getZeusGroupIdsForLmGroup(parameters.getGroupId(),
                parameters.getProgramId());
        for (String zeusGroupId : zeusGroupIds) {
            ZeusDemandResponseRequest setpointDr = new ZeusDemandResponseRequest(buildZeusEvent(zeusGroupId,
                    parameters.getStartTime(), parameters.getStopTime(), parameters.isMandatory()));
            boolean isHeatingEvent = parameters.isTempOptionHeat();
            float relativeTemp = (float) parameters.getTempOffset();
            setpointDr.getEvent().setIsHeatingEvent(isHeatingEvent);
            setpointDr.getEvent().setRelativeTemp(relativeTemp);
            if (relativeTemp < 0 && !isHeatingEvent) {
                log.info("Relative temperature is negative for the cool event so setting ecoplus selector as NON_ECOPLUS");
                setpointDr.getEvent().setEcoplusSelector(EcoplusSelector.NON_ECOPLUS);
            } else {
                setpointDr.getEvent().setEcoplusSelector(EcoplusSelector.ALL);
            }
            if (log.isDebugEnabled()) {
                try {
                    log.debug("Sending ecobee set point DR with body: {}", JsonUtils.toJson(setpointDr));
                } catch (JsonProcessingException e) {
                    log.warn("Error parsing json in debug.", e);
                }
            }
            try {
                ResponseEntity<ZeusDemandResponseRequest> zeusDrResponseEntity = requestHelper
                        .callEcobeeAPIForObject(issueDemandResponseUrl, HttpMethod.POST, ZeusDemandResponseRequest.class,
                                setpointDr);
                if (zeusDrResponseEntity.getStatusCode() == HttpStatus.CREATED) {
                    eventId = zeusDrResponseEntity.getBody().getEvent().getId();
                    ecobeeZeusGroupService.updateEventId(eventId, zeusGroupId);
                }
            } catch (RestClientException | EcobeeAuthenticationException e) {
                throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
            }
        }
    }

    @Override
    public void sendEcoPlusDR(EcobeePlusDrParameters parameters) {
        String eventId = StringUtils.EMPTY;
        String issueDemandResponseUrl = getUrlBase() + "events/dr";

        List<String> zeusGroupIds = ecobeeZeusGroupService.getZeusGroupIdsForLmGroup(parameters.getGroupId(),
                parameters.getProgramId());
        for (String zeusGroupId : zeusGroupIds) {
            ZeusDemandResponseRequest ecoPluspointDr = new ZeusDemandResponseRequest(buildZeusEcoPlusEvent(zeusGroupId,
                                                                                                           parameters.getStartTime(),
                                                                                                           parameters.getEndTime(),
                                                                                                           parameters.getRandomTimeSeconds(),
                                                                                                           parameters.isHeatingEvent()));

            if (log.isDebugEnabled()) {
                try {
                    log.debug("Sending eco+ point DR with body: {}", JsonUtils.toJson(ecoPluspointDr));
                } catch (JsonProcessingException e) {
                    log.warn("Error parsing json in debug.", e);
                }
            }
            try {
                ResponseEntity<ZeusDemandResponseRequest> zeusDrResponseEntity = requestHelper
                        .callEcobeeAPIForObject(issueDemandResponseUrl, HttpMethod.POST, ZeusDemandResponseRequest.class,
                                                ecoPluspointDr);
                if (zeusDrResponseEntity.getStatusCode() == HttpStatus.CREATED) {
                    eventId = zeusDrResponseEntity.getBody().getEvent().getId();
                    ecobeeZeusGroupService.updateEventId(eventId, zeusGroupId);
                }
            } catch (RestClientException | EcobeeAuthenticationException e) {
                throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
            }
        }
        
    }
    /**
     * Method to build Zeus event.
     */
    private ZeusEvent buildZeusEvent(String zeusGroupId, Instant startTime, Instant stopTime, boolean isMandatory) {
        DateTimeFormatter dateTimeFormmater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        String eventDisplayMessage = messageSourceAccessor.getMessage("yukon.web.modules.dr.ecobee.eventDisplayMessage");

        ZeusEvent event = new ZeusEvent();
        event.setName(YUKON_CYCLE_EVENT_NAME);
        event.setTstatGroupId(zeusGroupId);

        event.setEventStartTime(startTime.toString(dateTimeFormmater));
        Duration res = new Duration(startTime, stopTime);
        event.setDurationInMinutes(res.toStandardMinutes().getMinutes());

        event.setMandatory(isMandatory);

        event.setMessage(eventDisplayMessage);
        event.setSendEmail(settingDao.getBoolean(GlobalSettingType.ECOBEE_SEND_NOTIFICATIONS));

        event.setShowThermostat(true);
        event.setShowWeb(true);
        return event;
    }

    
    /**
     * Method to build Zeus eco+ event.
     */
    private ZeusEvent buildZeusEcoPlusEvent(String zeusGroupId, Instant startTime, Instant stopTime, Integer randomTimeSeconds, Boolean isHeatingEvent) {
        DateTimeFormatter dateTimeFormmater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        ZeusEvent event = new ZeusEvent();
        event.setName(YUKON_CYCLE_EVENT_NAME);
        event.setTstatGroupId(zeusGroupId);

        event.setRandomTimeSeconds(randomTimeSeconds);
        event.setIsHeatingEvent(isHeatingEvent);

        event.setEventStartTime(startTime.toString(dateTimeFormmater));
        Duration res = new Duration(startTime, stopTime);
        event.setDurationInMinutes(res.toStandardMinutes().getMinutes());

        event.setEcoplusSelector(EcoplusSelector.ECOPLUS);
        return event;
    }

    @Override
    public List<ZeusGroup> getAllGroups() {
        String getAllGroupsForProgram = getUrlBase() + "tstatgroups?program_id=" + getZeusProgramId();
        List<ZeusGroup> zeusGroups = new ArrayList<>();
        try {
            ResponseEntity<ZeusGroupResponse> responseEntity = (ResponseEntity<ZeusGroupResponse>) requestHelper
                    .callEcobeeAPIForObject(getAllGroupsForProgram, HttpMethod.GET, ZeusGroupResponse.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                zeusGroups = responseEntity.getBody().getGroups();
                int numberOfPages = getNumberOfPages(responseEntity, 20);
                if (numberOfPages > 0) {
                    for (int i = 1; i <= numberOfPages; i++) {
                        zeusGroups = responseEntity.getBody().getGroups();
                        getAllGroupsForProgram = getAllGroupsForProgram.concat("&page=" + i);
                        responseEntity = (ResponseEntity<ZeusGroupResponse>) requestHelper
                                .callEcobeeAPIForObject(getAllGroupsForProgram, HttpMethod.GET, ZeusGroupResponse.class);
                        if (responseEntity.getStatusCode() == HttpStatus.OK) {
                            zeusGroups.addAll(responseEntity.getBody().getGroups());
                        }
                    }
                }
            }
            return zeusGroups;
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }

    }

    @Override
    public List<ZeusThermostat> getThermostatsInGroup(String thermostatGroupID, String... serialNumbers) {
        String getThermostatsURL = getUrlBase() + "tstatgroups/" + thermostatGroupID + "/thermostats";
        if (serialNumbers != null && serialNumbers.length > 0) {
            getThermostatsURL = getThermostatsURL.concat("?thermostat_ids=").concat(String.join(",", serialNumbers));
        }
        List<ZeusThermostat> zeusThermostats = new ArrayList<>();
        try {
            ResponseEntity<ZeusThermostatsResponse> responseEntity = (ResponseEntity<ZeusThermostatsResponse>) requestHelper
                    .callEcobeeAPIForObject(getThermostatsURL, HttpMethod.GET, ZeusThermostatsResponse.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                zeusThermostats = responseEntity.getBody().getThermostats();
                int numberOfPages = getNumberOfPages(responseEntity, 100);
                if (numberOfPages > 0) {
                    for (int i = 1; i <= numberOfPages; i++) {
                        getThermostatsURL = getThermostatsURL.concat("?page=" + i);
                        responseEntity = (ResponseEntity<ZeusThermostatsResponse>) requestHelper
                                .callEcobeeAPIForObject(getThermostatsURL, HttpMethod.GET, ZeusThermostatsResponse.class);
                        if (responseEntity.getStatusCode() == HttpStatus.OK) {
                            zeusThermostats.addAll(responseEntity.getBody().getThermostats());
                        }
                    }
                }
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
        return zeusThermostats;
    }

    /**
     * Calculate and return the number of pages required from x-total-count property from response header
     */
    private int getNumberOfPages(ResponseEntity<?> responseEntity, int itemsPerPage) {
        int numberOfPages = 0;
        HttpHeaders headers = responseEntity.getHeaders();
        List<String> countList = headers.get("x-total-count");
        if (CollectionUtils.isNotEmpty(countList)) {
            int itemsCount = Integer.valueOf(countList.get(0));
            numberOfPages = (itemsCount / itemsPerPage);
        }
        return numberOfPages;
    }

    @Override
    public void cancelDemandResponse(List<Integer> groupIds, String... serialNumbers) throws CommandCompletionException {
        for (int yukonGroupId : groupIds) {
            boolean isNotEmptyThermostats = ArrayUtils.isNotEmpty(serialNumbers);

            List<String> zeusEventIds = ecobeeZeusGroupService.getEventIds(yukonGroupId);
            if (CollectionUtils.isEmpty(zeusEventIds)) {
                log.debug("There are no events for the group: {}. No DR Event cancellation request sent to Ecobee.",
                        yukonGroupId);
                return;
            }
            for (String zeusEventId : zeusEventIds) {
                String cancelDrUrl = getUrlBase() + "events/dr/" + zeusEventId;
                if (isNotEmptyThermostats) {
                    cancelDrUrl = cancelDrUrl.concat("?thermostat_ids=").concat(String.join(",", serialNumbers));
                }
                try {
                    log.debug("Sending Zeus cancel DR request for Yukon group : {} for {} thermostat(s)", yukonGroupId,
                            isNotEmptyThermostats ? serialNumbers : "all");
                    ResponseEntity<?> responseEntity = requestHelper.callEcobeeAPIForObject(cancelDrUrl, HttpMethod.DELETE,
                            Map.class);
                    if (responseEntity.getStatusCode() == HttpStatus.OK) {
                        log.debug("Canceled DR request for Yukon group : {} for {} thermostat(s)", yukonGroupId,
                                isNotEmptyThermostats ? serialNumbers : "all");
                        if (!isNotEmptyThermostats) {
                            // For DR event cancellation, update the eventId to empty String for the Yukon group.
                            // For Opt Out, just make call to Ecobee. Do not remove the mapping between inventory ID and Zeus
                            // group ID.
                            ecobeeZeusGroupService.removeEventId(zeusEventId);
                        }
                    }
                } catch (RestClientException | EcobeeAuthenticationException e) {
                    throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
                }
            }
        }
    }

    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);

    }

    private String getZeusProgramId() {
        String programId = settingDao.getString(GlobalSettingType.ECOBEE_PROGRAM_ID);
        if (StringUtils.isEmpty(programId)) {
            throw new EcobeeCommunicationException("Ecobee Zeus program id is empty.");
        }
        return programId;
    }

    @Override
    public void optOut(String serialNumber, int inventoryId) {
        List<String> zeusGroupIds = ecobeeZeusGroupService.getZeusGroupIdsForInventoryId(inventoryId);
        zeusGroupIds.stream().forEach(zeusGroupId -> {
            // Do not remove the mapping from ZeusGroupInventoryMapping table.
            removeThermostatFromGroup(zeusGroupId, serialNumber, inventoryId, false);
        });
    }

    @Override
    public void cancelOptOut(String serialNumber, int inventoryId) {
        List<String> zeusGroupIds = ecobeeZeusGroupService.getZeusGroupIdsForInventoryId(inventoryId);
        zeusGroupIds.stream().forEach(zeusGroupId -> {
            // Do not remove the mapping from ZeusGroupInventoryMapping table.
            addThermostatToGroup(zeusGroupId, serialNumber, inventoryId, false);
        });
    }

}