package com.cannontech.dr.ecobee.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.ZeusThermostatRequest;
import com.cannontech.dr.ecobee.message.CriteriaSelector;
import com.cannontech.dr.ecobee.message.ZeusThermostatGroup;
import com.cannontech.dr.ecobee.message.ZeusThermostatState;
import com.cannontech.dr.ecobee.message.ZeusThermostatsResponse;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusGroupService;
import com.cannontech.dr.ecobee.service.helper.EcobeeZeusRequestHelper;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class EcobeeZeusCommunicationServiceImpl implements EcobeeZeusCommunicationService {
    @Autowired private EcobeeZeusRequestHelper requestHelper;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private EcobeeZeusGroupService ecobeeZeusGroupService;
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
    public void enroll(String lmGroupId, String inventoryId) {
        String zeusGroupId = ecobeeZeusGroupService.getZeusGroupId(lmGroupId, inventoryId);
        if (zeusGroupId == null) {
            ResponseEntity<ZeusThermostatGroup> responseEntity = createEcobeeGroup(lmGroupId);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                // this id retrive from ecobee response
                zeusGroupId = responseEntity.getBody().getProgramId();
                ecobeeZeusGroupService.mapGroupIdToZeusGroupId(lmGroupId, zeusGroupId);
            }
            // Make a api call to create ecobee group in ecobee sytem.https://ecp-api.readme.io/reference#post_tstatgroups
        } else {
            // create amethod in ecobeeZeusGroupService to retrive the no of thermostat avilable in a ecobee group.
            int deviceCount = ecobeeZeusGroupService.getDeviceCount(lmGroupId);
            if (deviceCount < 9900) {
                ResponseEntity<ZeusThermostatRequest> responseEntity = addThermostatToGroup(zeusGroupId, inventoryId);
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    ecobeeZeusGroupService.mapInventoryToZeusGroupId(inventoryId, zeusGroupId);
                }
            } else {
                // Make a api call to create ecobee group in ecobee sytem.https://ecp-api.readme.io/reference#post_tstatgroups
                ResponseEntity<ZeusThermostatGroup> responseEntity = createEcobeeGroup(lmGroupId);
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    zeusGroupId = responseEntity.getBody().getProgramId();
                    // If above api call is successfull , update the mapping
                    // if successfull API call update the inventory mapping.I.e
                    ecobeeZeusGroupService.mapGroupIdToZeusGroupId(lmGroupId, zeusGroupId);
                    ecobeeZeusGroupService.mapInventoryToZeusGroupId(inventoryId, zeusGroupId);
                }
            } // Add thermostat into the required ecobee group by this API
              // https://ecp-api.readme.io/reference#put_tstatgroups-id-thermostats
        }
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<ZeusThermostatGroup> createEcobeeGroup(String lmGroupId) {
        try {
            String createThermostatGroupURL = getUrlBase() + "tstatgroups";
            List<String> list = new ArrayList<String>();
            //add incrementel old group name ecobeeZeusGroupService.getGroupName(lmGroupId)
            ZeusThermostatGroup zeusThermostatGroup = new ZeusThermostatGroup(ecobeeZeusGroupService.getGroupName(lmGroupId),
                    programId, new CriteriaSelector("identifier", list));
            return (ResponseEntity<ZeusThermostatGroup>) requestHelper
                    .callEcobeeAPIForObject(createThermostatGroupURL, HttpMethod.POST, ZeusThermostatGroup.class,
                            zeusThermostatGroup);
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<ZeusThermostatRequest> addThermostatToGroup(String zeusGroupId, String inventoryId) {
        String addThermostatURL = getUrlBase() + "tstatgroups/" + inventoryId + "thermostats";
        ZeusThermostatRequest addThermostatGroup = new ZeusThermostatRequest(zeusGroupId, "ENROLLED", inventoryId);
        try {
            return (ResponseEntity<ZeusThermostatRequest>) requestHelper
                    .callEcobeeAPIForObject(addThermostatURL, HttpMethod.PUT, Object.class, addThermostatGroup);
            // Add thermostat into the required ecobee group by this API
            // https://ecp-api.readme.io/reference#put_tstatgroups-id-thermostats
            // if successfull API call update the inventory mapping.I.e
        } catch (RestClientException | EcobeeAuthenticationException e) {
            throw new EcobeeCommunicationException("Error occurred while communicating Ecobee API.", e);
        }
    }

    @Override
    public void unEnroll(String lmGroupId, String inventoryId) {
        // TODO Auto-generated method stub

    }
}