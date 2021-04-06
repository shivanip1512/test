package com.cannontech.web.dev;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import com.cannontech.dr.ecobee.message.ZeusErrorResponse;
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
            @RequestParam(name = "enrollment_state") ZeusThermostatState state,
            @RequestParam(name = "thermostat_ids") List<String> thermostatIds) {
        int createDeviceCode = zeusEcobeeDataConfiguration.getCreateDevice();
        if (createDeviceCode == 0) {
            return new ResponseEntity<>(responseFactory.retrieveThermostats(thermostatIds), HttpStatus.OK);
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
}