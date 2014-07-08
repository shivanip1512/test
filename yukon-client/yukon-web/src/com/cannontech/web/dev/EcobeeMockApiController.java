package com.cannontech.web.dev;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.dr.ecobee.message.AuthenticationRequest;
import com.cannontech.dr.ecobee.message.AuthenticationResponse;
import com.cannontech.dr.ecobee.message.BaseResponse;
import com.cannontech.dr.ecobee.message.DeviceDataResponse;
import com.cannontech.dr.ecobee.message.DeviceRequest;
import com.cannontech.dr.ecobee.message.DrRequest;
import com.cannontech.dr.ecobee.message.DrResponse;
import com.cannontech.dr.ecobee.message.HierarchyResponse;
import com.cannontech.dr.ecobee.message.SetRequest;
import com.cannontech.dr.ecobee.message.StandardResponse;
import com.cannontech.dr.ecobee.message.partial.Status;
import com.cannontech.web.security.annotation.AuthorizeByCparm;
import com.cannontech.web.security.annotation.IgnoreCsrfCheck;

@Controller
@RequestMapping("/mockecobee")
@AuthorizeByCparm(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)
public class EcobeeMockApiController {
    @Autowired private EcobeeMockApiService ecobeeMockApiService;
    @Autowired private EcobeeDataConfiguration ecobeeDataConfiguration;
    @RequestMapping(value = "runtimeReport", method = RequestMethod.GET)
    public @ResponseBody DeviceDataResponse runtimeReport(@RequestParam("body") String bodyJson) throws IOException {
        return ecobeeMockApiService.getRuntimeReport();
    }

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
    public @ResponseBody StandardResponse thermostatHierarchy(HttpEntity<DeviceRequest> requestEntity) {
        StandardResponse standardResponse = null;
        DeviceRequest request = requestEntity.getBody();
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
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public @ResponseBody AuthenticationResponse register(@RequestBody AuthenticationRequest request) {
        return new AuthenticationResponse("TK1", new Status(ecobeeDataConfiguration.getAuthenticate(), "Authenticated!"));
    }

}