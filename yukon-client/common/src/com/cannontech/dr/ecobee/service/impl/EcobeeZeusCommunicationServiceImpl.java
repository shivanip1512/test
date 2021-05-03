package com.cannontech.dr.ecobee.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.CriteriaSelector;
import com.cannontech.dr.ecobee.message.Selector;
import com.cannontech.dr.ecobee.message.ZeusCreatePushConfig;
import com.cannontech.dr.ecobee.message.ZeusGroup;
import com.cannontech.dr.ecobee.message.ZeusShowPushConfig;
import com.cannontech.dr.ecobee.message.ZeusThermostatGroup;
import com.cannontech.dr.ecobee.message.ZeusThermostatState;
import com.cannontech.dr.ecobee.message.ZeusThermostatsResponse;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusGroupService;
import com.cannontech.dr.ecobee.service.helper.EcobeeZeusRequestHelper;
import com.cannontech.stars.dr.enrollment.exception.EnrollmentException;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;

public class EcobeeZeusCommunicationServiceImpl implements EcobeeZeusCommunicationService {
    private static Logger log = YukonLogManager.getLogger(EcobeeZeusCommunicationServiceImpl.class);
    @Autowired private EcobeeZeusRequestHelper requestHelper;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private EcobeeZeusGroupService ecobeeZeusGroupService;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private IDatabaseCache cache;
    // TODO: Remove hard coded String once globalsettings is created for program ID.
    private static String programId = "2df7e7a53193438a8a3aa4c919475ac0";
    private static final int thresholdThermostatCount = 9900;

    @SuppressWarnings("unchecked")
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
            ZeusCreatePushConfig zeusPushConfig = new ZeusCreatePushConfig(reportingUrl, privateKey);

            requestHelper.callEcobeeAPIForObject(pushConfigURL, HttpMethod.POST, Object.class, zeusPushConfig);

        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }
    
    @SuppressWarnings("unchecked")
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

    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);
    }
}