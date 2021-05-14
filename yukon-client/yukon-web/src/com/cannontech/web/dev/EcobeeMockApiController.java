package com.cannontech.web.dev;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.dr.ecobee.message.AuthenticationRequest;
import com.cannontech.dr.ecobee.message.AuthenticationResponse;
import com.cannontech.dr.ecobee.message.BaseResponse;
import com.cannontech.dr.ecobee.message.DrRequest;
import com.cannontech.dr.ecobee.message.DrResponse;
import com.cannontech.dr.ecobee.message.EcobeeJobStatus;
import com.cannontech.dr.ecobee.message.HierarchyResponse;
import com.cannontech.dr.ecobee.message.RegisterDeviceRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportJobRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportJobResponse;
import com.cannontech.dr.ecobee.message.RuntimeReportJobStatusRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportJobStatusResponse;
import com.cannontech.dr.ecobee.message.SetRequest;
import com.cannontech.dr.ecobee.message.StandardResponse;
import com.cannontech.dr.ecobee.message.ZeusAuthenticationRequest;
import com.cannontech.dr.ecobee.message.ZeusCreatePushConfig;
import com.cannontech.dr.ecobee.message.ZeusDemandResponseRequest;
import com.cannontech.dr.ecobee.message.ZeusErrorResponse;
import com.cannontech.dr.ecobee.message.ZeusShowPushConfig;
import com.cannontech.dr.ecobee.message.ZeusThermostatGroup;
import com.cannontech.dr.ecobee.message.ZeusThermostatState;
import com.cannontech.dr.ecobee.message.partial.Status;
import com.cannontech.dr.ecobee.service.EcobeeStatusCode;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.IgnoreCsrfCheck;

@Controller
@RequestMapping("/mockecobee/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class EcobeeMockApiController {
    @Autowired private EcobeeMockApiService ecobeeMockApiService;
    @Autowired private EcobeeDataConfiguration ecobeeDataConfiguration;
    @Autowired private ZeusEcobeeDataConfiguration zeusEcobeeDataConfiguration;
    @Autowired private MockZeusResponseFactory responseFactory;
    
    @IgnoreCsrfCheck
    @RequestMapping(value = "hierarchy/set", method = RequestMethod.POST)
    public @ResponseBody StandardResponse hierarchy(HttpEntity<SetRequest> requestEntity) {
        StandardResponse standardResponse = null;
        SetRequest request = requestEntity.getBody();
        if (request.getOperation().equalsIgnoreCase("add")) {
            standardResponse = new StandardResponse(true, new Status(ecobeeDataConfiguration.getCreateSet(), " Add Set Operation"));
        } else if (request.getOperation().equalsIgnoreCase("remove")) {
            standardResponse = new StandardResponse(true, new Status(ecobeeDataConfiguration.getRemoveSet(), " Delete Set Operation"));
        } else if (request.getOperation().equalsIgnoreCase("move")) {
            standardResponse = new StandardResponse(true, new Status(ecobeeDataConfiguration.getMoveSet(), " Move Set Operation"));
        }
        return standardResponse;
    }

    @RequestMapping(value = "hierarchy/set", method = RequestMethod.GET)
    public @ResponseBody HierarchyResponse listHierarchy(HttpEntity<byte[]> requestEntity) {
        return ecobeeMockApiService.getHierarchyList();
    }

    @IgnoreCsrfCheck
    @RequestMapping(value = "hierarchy/thermostat", method = RequestMethod.POST)
    public @ResponseBody StandardResponse thermostatHierarchy(HttpEntity<RegisterDeviceRequest> requestEntity) {
        StandardResponse standardResponse = null;
        RegisterDeviceRequest request = requestEntity.getBody();
        if (request.getOperation().equalsIgnoreCase("register")) {
            standardResponse = new StandardResponse(true, new Status(ecobeeDataConfiguration.getRegisterDevice(), " Register Thermostat"));
        } else if (request.getOperation().equalsIgnoreCase("assign")) {
            standardResponse = new StandardResponse(true, new Status(ecobeeDataConfiguration.getAssignThermostat(), " Assign Thermostat"));
        }
        return standardResponse;
    }

    @IgnoreCsrfCheck
    @RequestMapping(value = "demandResponse", method = RequestMethod.POST)
    public @ResponseBody BaseResponse demandResponse(HttpEntity<DrRequest> requestEntity) {
        BaseResponse response = null;
        DrRequest request = requestEntity.getBody();
        if (request.getOperation().equalsIgnoreCase("create")) {
            response = new DrResponse("11", new Status(ecobeeDataConfiguration.getSendDR(), "DutyCycle send Operation "));
        } else if (request.getOperation().equalsIgnoreCase("cancel")) {
            response = new BaseResponse(new Status(ecobeeDataConfiguration.getSendRestore(), "Restore Completed!"));
        }
        return response;
    }

    @IgnoreCsrfCheck
    @RequestMapping("register")
    public @ResponseBody AuthenticationResponse register(@RequestBody AuthenticationRequest request) {
        Status status = new Status(ecobeeDataConfiguration.getAuthenticate(), "Authenticated!");
        AuthenticationResponse response = new AuthenticationResponse("TK1", status);
        return response;
    }
    
    @IgnoreCsrfCheck
    @PostMapping("runtimeReportJob/create")
    public @ResponseBody RuntimeReportJobResponse createRuntimeReportJob(@RequestBody RuntimeReportJobRequest request) {
        int code = ecobeeDataConfiguration.getRuntimeReport();
        RuntimeReportJobResponse response = null;
        if (code == EcobeeStatusCode.SUCCESS.getCode()) {
            response = ecobeeMockApiService.createRuntimeReportJob(request);
        } else {
            Status status = new Status(code, "Some error has occurred");
            response = new RuntimeReportJobResponse(null, EcobeeJobStatus.ERROR, status);
        }
        return response;
    }
    
    @IgnoreCsrfCheck
    @GetMapping("runtimeReportJob/status")
    public @ResponseBody RuntimeReportJobStatusResponse getRuntimeJobStatus(@RequestParam("body") String bodyJson) throws IOException {
        RuntimeReportJobStatusRequest request = JsonUtils.fromJson(bodyJson, RuntimeReportJobStatusRequest.class);
        RuntimeReportJobStatusResponse response = ecobeeMockApiService.getRuntimeJobStatus(request.getJobId());
        return response;
    }

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
    @GetMapping("tstatgroups/{thermostatGroupID}/thermostats")
    public ResponseEntity<Object> retrieveThermostats(@PathVariable String thermostatGroupID,
            @RequestParam(name = "enrollment_state", required = false) ZeusThermostatState state,
            @RequestParam(name = "thermostat_ids", required = false) List<String> thermostatIds) {
        int createDeviceCode = zeusEcobeeDataConfiguration.getCreateDevice();
        if (createDeviceCode == 0) {
            if (thermostatIds == null) {
                return new ResponseEntity<>(responseFactory.getThermostatsInGroup(thermostatGroupID), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(responseFactory.retrieveThermostats(thermostatIds), HttpStatus.OK);
            }
        } else if (createDeviceCode == 1) {
            return new ResponseEntity<>(getUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
        } else if (createDeviceCode == 3) {
            return new ResponseEntity<>(getNotFoundResponse(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(getBadRequestResponse(), HttpStatus.BAD_REQUEST);
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
        ZeusShowPushConfig config = new ZeusShowPushConfig();
        config.setPrivateKey("142f8801bc58d69f5100bd2779d75c9e36011244");
        config.setReportingUrl("http://abcenergy.com/ecobee/runtimedata");
        int getShowPushConfigCode = zeusEcobeeDataConfiguration.getShowPushConfiguration();
        if (getShowPushConfigCode == 0) {
            return new ResponseEntity<>(config, HttpStatus.OK);
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
        
        ZeusCreatePushConfig createConfig = new ZeusCreatePushConfig();
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
        responseMap.put("username", "user123");
        responseMap.put("utility_id", "utility-123");
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
    public ResponseEntity<Object> getAllGroups() {
        int getGroupCode = zeusEcobeeDataConfiguration.getGetGroup();
        if (getGroupCode == 0) {
            return new ResponseEntity<>(responseFactory.retrieveGroups(), HttpStatus.OK);
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