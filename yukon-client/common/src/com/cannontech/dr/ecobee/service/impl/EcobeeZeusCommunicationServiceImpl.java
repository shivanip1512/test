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
import com.cannontech.dr.ecobee.message.ZeusThermostatGroup;
import com.cannontech.dr.ecobee.message.ZeusThermostatState;
import com.cannontech.dr.ecobee.message.ZeusThermostatsResponse;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusGroupService;
import com.cannontech.dr.ecobee.service.helper.EcobeeZeusRequestHelper;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class EcobeeZeusCommunicationServiceImpl implements EcobeeZeusCommunicationService {
    private static Logger log = YukonLogManager.getLogger(EcobeeZeusCommunicationServiceImpl.class);
    @Autowired private EcobeeZeusRequestHelper requestHelper;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private EcobeeZeusGroupService ecobeeZeusGroupService;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    // TODO: Remove hard coded String once globalsettings is created for program ID.
    private static String programId = "2df7e7a53193438a8a3aa4c919475ac0";
    private int thresholdThermostatCount = 9900;

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

    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);
    }

    @Override
    public void enroll(int lmGroupId, String serialNumber) {
        int inventoryId = lmHardwareBaseDao.getBySerialNumber(serialNumber).getInventoryId();
        String zeusGroupId = ecobeeZeusGroupService.getZeusGroupId(lmGroupId, inventoryId);
        List<String> existingzeusGroupIds = ecobeeZeusGroupService.getZeusGroupIdsForLmGroup(lmGroupId);
        // For a new customer, zeusGroupId might be empty. So create a new Ecobee group.
        if (StringUtils.isEmpty(zeusGroupId)) {
            zeusGroupId = createEcobeeGroup(lmGroupId, serialNumber);
        }
        synchronized (this) {
            int deviceCount = ecobeeZeusGroupService.getDeviceCount(zeusGroupId);
            if (deviceCount < thresholdThermostatCount) {
                addThermostatToGroup(zeusGroupId, serialNumber, inventoryId);
            } else {
                zeusGroupId = createEcobeeGroup(lmGroupId, serialNumber);
                addThermostatToGroup(zeusGroupId, serialNumber, inventoryId);
            }
        }
    }

    /**
     * Create a Ecobee Zeus group and return the group ID.
     */
    @SuppressWarnings("unchecked")
    private String createEcobeeGroup(int lmGroupId, String inventoryId) {
        String zeusGroupId = StringUtils.EMPTY;
        try {
            String createThermostatGroupURL = getUrlBase() + "tstatgroups";
            CriteriaSelector criteriaSelector = new CriteriaSelector("identifier", Arrays.asList(inventoryId));
            String groupName = ecobeeZeusGroupService.getNextGroupName(lmGroupId);
            ZeusThermostatGroup zeusThermostatGroup = new ZeusThermostatGroup(groupName, programId,
                    criteriaSelector);
            ResponseEntity<Map> responseEntity = (ResponseEntity<Map>) requestHelper.callEcobeeAPIForObject(
                    createThermostatGroupURL, HttpMethod.POST, Map.class, zeusThermostatGroup);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Map<String, String> responseFields = (Map<String, String>) responseEntity.getBody().get("group");
                zeusGroupId = responseFields.get("id");
                groupName = responseFields.get("name");
                ecobeeZeusGroupService.mapGroupIdToZeusGroup(lmGroupId, zeusGroupId, groupName);
                log.info("Zeus group with ID: {} and Name: {} created successfully on Ecobee and Mapped to Yukon LM group.",
                        zeusGroupId, groupName);
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
        return zeusGroupId;
    }

    private void addThermostatToGroup(String zeusGroupId, String serialNumber, int inventoryId) {
        List<Integer> inventoryIds = ecobeeZeusGroupService.getInventoryIdsForZeusGrouID(zeusGroupId);
        List<String> thermostatIds = new ArrayList<String>();
        for (int id : inventoryIds) {
            thermostatIds.add(lmHardwareBaseDao.getSerialNumberForInventoryId(id));
        }
        // Before Update, add the thermostat which we want to enroll.
        thermostatIds.add(serialNumber);
        updateThermostatGroup(zeusGroupId, thermostatIds);
        ecobeeZeusGroupService.mapInventoryToZeusGroupId(inventoryId, zeusGroupId);
    }

    @Override
    public void unEnroll(int lmGroupId, String serialNumber) {
        Integer inventoryId = lmHardwareBaseDao.getBySerialNumber(serialNumber).getInventoryId();
        String zeusGroupId = ecobeeZeusGroupService.getZeusGroupId(lmGroupId, inventoryId);
        List<Integer> inventoryIds = ecobeeZeusGroupService.getInventoryIdsForZeusGrouID(zeusGroupId);
        // Before Update, remove the thermostat which we want to unenroll.
        inventoryIds.remove(inventoryId);
        List<String> thermostatIds = new ArrayList<String>();
        for (int id : inventoryIds) {
            thermostatIds.add(lmHardwareBaseDao.getSerialNumberForInventoryId(id));
        }
        ecobeeZeusGroupService.deleteInventoryToZeusGroupId(inventoryId);
    }

    private void updateThermostatGroup(String zeusGroupId, List<String> thermostatIds) {
        String updateThermostatURL = getUrlBase() + "tstatgroups/" + zeusGroupId;
        String groupName = ecobeeZeusGroupService.zeusGroupName(zeusGroupId);
        CriteriaSelector criteriaSelector = new CriteriaSelector("identifier", thermostatIds);
        ZeusThermostatGroup zeusThermostatGroupRequest = new ZeusThermostatGroup(groupName, programId, criteriaSelector);
        try {
            ResponseEntity<? extends Object> responseEntity = requestHelper.callEcobeeAPIForObject(updateThermostatURL, HttpMethod.PUT, Object.class, zeusThermostatGroupRequest);
            if(responseEntity.getStatusCode() == HttpStatus.PARTIAL_CONTENT) {
                log.warn("Add warnig for partial content");
            }
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }
}