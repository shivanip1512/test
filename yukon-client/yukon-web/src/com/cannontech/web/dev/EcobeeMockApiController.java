package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.dr.ecobee.message.ZeusAuthenticationRequest;
import com.cannontech.dr.ecobee.message.ZeusCreateDevice;
import com.cannontech.dr.ecobee.message.ZeusCreatePushConfig;
import com.cannontech.dr.ecobee.message.ZeusDemandResponseRequest;
import com.cannontech.dr.ecobee.message.ZeusErrorResponse;
import com.cannontech.dr.ecobee.message.ZeusShowPushConfig;
import com.cannontech.dr.ecobee.message.ZeusThermostat;
import com.cannontech.dr.ecobee.message.ZeusThermostatGroup;
import com.cannontech.dr.ecobee.message.ZeusThermostatState;
import com.cannontech.dr.ecobee.message.ZeusThermostatsResponse;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.IgnoreCsrfCheck;

@Controller
@RequestMapping("/mockecobee/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class EcobeeMockApiController {

    @Autowired private ZeusEcobeeDataConfiguration zeusEcobeeDataConfiguration;
    @Autowired private MockZeusResponseFactory responseFactory;
    private ZeusCreatePushConfig createConfig;
    

    @IgnoreCsrfCheck
    @PostMapping("auth")
    public ResponseEntity<Object> auth(@RequestBody ZeusAuthenticationRequest request) {
        int authenticationCode = zeusEcobeeDataConfiguration.getAuthenticate();
        if (authenticationCode == 0) {
            return new ResponseEntity<>(responseFactory.login(request), HttpStatus.OK);
        } else if (authenticationCode == 1) {
            return new ResponseEntity<>(getUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
        }
    }

    @IgnoreCsrfCheck
    @GetMapping("auth/refresh")
    public ResponseEntity<Object> refresh(@RequestParam("refresh_token") String refreshToken) {
        if (responseFactory.isInvalidRefreshToken(refreshToken)) {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(responseFactory.refresh(refreshToken), HttpStatus.OK);
    }

    @IgnoreCsrfCheck
    @GetMapping("programs/{programId}")
    public ResponseEntity<Object> programs(@PathVariable String programId) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Map<String, String> thermostatIdMap = new HashMap<String, String>();
        thermostatIdMap.put("root_tstatgroup_id", "i89uUYUuioyhyu36hsidch9s8NUYGUA");
        responseMap.put("program", thermostatIdMap);
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @IgnoreCsrfCheck
    @PutMapping("tstatgroups/{thermostatGroupID}/thermostats")
    public ResponseEntity<Object> addThermostats(@PathVariable String thermostatGroupID, @RequestBody ZeusCreateDevice device) {
        int createDeviceCode = zeusEcobeeDataConfiguration.getCreateDevice();
        if (createDeviceCode == 0) {
            Map<String, Integer> responseMap = new HashMap<String, Integer>();
            responseMap.put("added", 1);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } else if (createDeviceCode == 1) {
            return new ResponseEntity<>(getUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
        } else if (createDeviceCode == 3) {
            return new ResponseEntity<>(getNotFoundResponse(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
        }
    }

    @IgnoreCsrfCheck
    @GetMapping("tstatgroups/{thermostatGroupID}/thermostats")
    public ResponseEntity<Object> retrieveThermostats(@PathVariable String thermostatGroupID,
            @RequestParam(name = "enrollment_state", required = false) ZeusThermostatState state,
            @RequestParam(name = "thermostat_ids", required = false) List<String> thermostatIds,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer pageNumber) {
        if (state != null && CollectionUtils.isNotEmpty(thermostatIds)) {
            int deviceStatusResponse = zeusEcobeeDataConfiguration.getDeviceStatus();
            if (deviceStatusResponse == 0) {
                ZeusThermostatsResponse response = new ZeusThermostatsResponse();
                List<ZeusThermostat> thermostats = new ArrayList<ZeusThermostat>();
                ZeusThermostat thermostat = new ZeusThermostat();
                thermostat.setSerialNumber(thermostatIds.get(0));
                thermostat.setState(ZeusThermostatState.ENROLLED);
                thermostats.add(thermostat);
                response.setThermostats(thermostats);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ZeusThermostatsResponse(), HttpStatus.OK);
            }
        }
        int pagenatedResponse = zeusEcobeeDataConfiguration.getPaginatedResponse();
        if (pagenatedResponse == 1) {
            return responseFactory.getPaginatedThermostatsInGroup(thermostatGroupID, pageNumber);
        } else {
            return new ResponseEntity<>(responseFactory.getThermostatsInGroup(thermostatGroupID), HttpStatus.OK);
        }
    }

    @IgnoreCsrfCheck
    @DeleteMapping("tstatgroups/{thermostatGroupID}/thermostats")
    public ResponseEntity<Object> deleteThermostats(@PathVariable String thermostatGroupID,
            @RequestParam(name = "thermostat_ids") List<String> thermostatIds) {
        int deleteDeviceCode = zeusEcobeeDataConfiguration.getDeleteDevice();
        if (deleteDeviceCode == 0) {
            return new ResponseEntity<>(responseFactory.deleteThermostats(thermostatIds), HttpStatus.OK);
        } else if (deleteDeviceCode == 1) {
            return new ResponseEntity<>(getUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
        } else if (deleteDeviceCode == 3) {
            return new ResponseEntity<>(getNotFoundResponse(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
        }
    }

    @IgnoreCsrfCheck
    @PostMapping("tstatgroups")
    public ResponseEntity<Object> createThermostatGroup(@RequestBody ZeusThermostatGroup thermostatGroup) {
        Map<String, Map<String, Object>> responseMap = new HashMap<String, Map<String, Object>>();
        Map<String, Object> groupMap = new HashMap<String, Object>();
        groupMap.put("id", StringUtils.replace(UUID.randomUUID().toString(), "-", "").substring(0, 32));
        groupMap.put("name", thermostatGroup.getGroup().getName());
        groupMap.put("utility_id", "utility-123");
        groupMap.put("thermostat_count", 100);
        responseMap.put("group", groupMap);
        return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
    }

    @IgnoreCsrfCheck
    @PutMapping("tstatgroups/{id}")
    public ResponseEntity<Object> updateThermostatGroup(@RequestBody ZeusThermostatGroup thermostatGroup,
            @PathVariable String id) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Map<String, Object> groupMap = new HashMap<String, Object>();
        groupMap.put("id", id);
        groupMap.put("name", thermostatGroup.getGroup().getName());
        groupMap.put("utility_id", "utility-123");
        groupMap.put("thermostat_count", thermostatGroup.getCriteriaSelector().getValues().size());
        responseMap.put("group", groupMap);
        int enrollment = zeusEcobeeDataConfiguration.getEnrollment();
        if (enrollment == 0) {
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } else if (enrollment == 1) {
            return new ResponseEntity<>(getUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
        } else if (enrollment == 3) {
            return new ResponseEntity<>(getNotFoundResponse(), HttpStatus.NOT_FOUND);
        } else if (enrollment == 4) {
            responseMap.put("failed_thermostat_ids", Arrays.asList("12345678"));
            return new ResponseEntity<>(responseMap, HttpStatus.PARTIAL_CONTENT);
        } else {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @IgnoreCsrfCheck
    @PostMapping("events/dr")
    public ResponseEntity<Object> issueDemandResponse(@RequestBody ZeusDemandResponseRequest zeusDutyCycleDrRequest) {
        String eventId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        zeusDutyCycleDrRequest.getEvent().setId(eventId);
        int issueDemandResponse = zeusEcobeeDataConfiguration.getIssueDemandResponse();
        if (issueDemandResponse == 0) {
            return new ResponseEntity<>(zeusDutyCycleDrRequest, HttpStatus.CREATED);
        } else if (issueDemandResponse == 1) {
            return new ResponseEntity<>(getUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
        } else if (issueDemandResponse == 3) {
            return new ResponseEntity<>(getNotFoundResponse(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("utilities/{utilityId}/pushconfig")
    public ResponseEntity<Object> showPushApiConfiguration(@PathVariable String utilityId) {
        ZeusShowPushConfig showconfig = new ZeusShowPushConfig();
        if (createConfig != null) {
            String privateKeySh1 = DigestUtils.sha1Hex(createConfig.getPrivateKey());
            showconfig.setPrivateKey(privateKeySh1);
            showconfig.setReportingUrl(createConfig.getReportingUrl());
        } else {
            showconfig.setPrivateKey("e80af43fd2f03da341c70c3f186dd4fe8521c688");
            showconfig.setReportingUrl("http://127.0.0.1:8080/ecobee/runtimeData");
        }
        int getShowPushConfigCode = zeusEcobeeDataConfiguration.getShowPushConfiguration();
        if (getShowPushConfigCode == 0) {
            return new ResponseEntity<>(showconfig, HttpStatus.OK);
        } else if (getShowPushConfigCode == 1) {
            return new ResponseEntity<>(getUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
        } else if (getShowPushConfigCode == 3) {
            return new ResponseEntity<>(getNotFoundResponse(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
        }
    }

    @IgnoreCsrfCheck
    @PostMapping("utilities/{utilityId}/pushconfig")
    public ResponseEntity<Object> createPushApiConfiguration(@RequestBody ZeusCreatePushConfig zeusPushConfig,
            @PathVariable String utilityId) {
        
        createConfig = new ZeusCreatePushConfig();
        createConfig.setPrivateKey(zeusPushConfig.getPrivateKey());
        createConfig.setReportingUrl(zeusPushConfig.getReportingUrl());
        int getPushConfigCode = zeusEcobeeDataConfiguration.getCreatePushConfiguration();
        if (getPushConfigCode == 0) {
            return new ResponseEntity<>(createConfig, HttpStatus.OK);
        } else if (getPushConfigCode == 1) {
            return new ResponseEntity<>(getUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
        } else if (getPushConfigCode == 3) {
            return new ResponseEntity<>(getNotFoundResponse(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @IgnoreCsrfCheck
    @GetMapping("auth/user")
    public ResponseEntity<Object> showUser() {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("username", "yukon@eaton.com");
        responseMap.put("utility_id", "f9c3631b800027106256");
        int showUserCode = zeusEcobeeDataConfiguration.getShowUser();
        if (showUserCode == 0) {
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } else if (showUserCode == 1) {
            return new ResponseEntity<>(getUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
        }
    }

    @IgnoreCsrfCheck
    @DeleteMapping("events/dr/{id}")
    public ResponseEntity<Object> cancelDemandResponse(@PathVariable String id,
            @RequestParam(name = "thermostat_ids", required = false) String... thermostatIds) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        int cancelDemandResponse = zeusEcobeeDataConfiguration.getCancelDemandResponse();
        if (cancelDemandResponse == 0) {
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } else if (cancelDemandResponse == 1) {
            return new ResponseEntity<>(getUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
        } else if (cancelDemandResponse == 3) {
            return new ResponseEntity<>(getNotFoundResponse(), HttpStatus.NOT_FOUND);
        } else if (cancelDemandResponse == 5) {
            return new ResponseEntity<>(getForbiddenResponse(), HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Response for UNAUTHORIZED status code
     */
    private ZeusErrorResponse getUnauthorizedResponse() {
        return new ZeusErrorResponse("access_denied", "Invalid Credentials or Supplied authorization token is invalid");
    }

    /**
     * Response for NOT_FOUND status code
     */
    private ZeusErrorResponse getNotFoundResponse() {
        return new ZeusErrorResponse("not_found", "Object not found.");
    }

    /**
     * Response for BAD_REQUEST status code
     */
    private ZeusErrorResponse getBadRequestResponse() {
        return new ZeusErrorResponse("bad_request", "Supplied request is not well formed.");
    }

    
    @IgnoreCsrfCheck
    @GetMapping("tstatgroups")
    public ResponseEntity<Object> getAllGroups(@RequestParam(name = "program_id") String programId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer pageNumber) {
        int getGroupCode = zeusEcobeeDataConfiguration.getGetGroup();
        if (getGroupCode == 0) {
            if (zeusEcobeeDataConfiguration.getPaginatedResponse() == 1) {
                return responseFactory.retrievePaginatedGroups(programId, pageNumber);
            } else {
                return new ResponseEntity<>(responseFactory.retrieveGroups(programId), HttpStatus.OK);
            }
        } else if (getGroupCode == 1) {
            return new ResponseEntity<>(getUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
        } else if (getGroupCode == 3) {
            return new ResponseEntity<>(getNotFoundResponse(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
        }
    }
    
    
    @IgnoreCsrfCheck
    @DeleteMapping("tstatgroups/{zeusGroupId}")
    public ResponseEntity<Object> deleteGroup(@PathVariable String zeusGroupId) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        int getGroupCode = zeusEcobeeDataConfiguration.getGetGroup();
        if (getGroupCode == 0) {
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } else if (getGroupCode == 1) {
            return new ResponseEntity<>(getUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
        } else if (getGroupCode == 3) {
            return new ResponseEntity<>(getNotFoundResponse(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
        }
    }
    /**
     * Response for FORBIDDEN status code
     */
    private Object getForbiddenResponse() {
        return new ZeusErrorResponse("Forbidden", "Access with provided parameters is permanently forbidden.");
    }
}