package com.cannontech.web.dev;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;
import com.cannontech.dr.pxmw.model.v1.PxMWChannelValuesRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCredentialsV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataRequestV1;
import com.cannontech.dr.pxmw.service.impl.v1.PxMWCommunicationServiceImplV1;
import com.cannontech.simulators.message.request.PxMWSimulatorRequest;
import com.cannontech.simulators.message.response.PxMWSimulatorResponse;
import com.cannontech.web.security.annotation.CheckCparm;

@RestController
@RequestMapping("/pxMiddleware/api/v1")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
/**
 * This controller is used to simulate API responses. The message is sent to Simulator service which will generate a fake data,
 * representing a response, to be send to the calling service.
 * 
 * Adding a new method:
 * 1. Verify that you are looking at PxMWSimulatorApiController[X] of the correct version.
 *      a. If the controller doesn't exist create a new one PxMWSimulatorApiController[Your Version]
 * 2. Verify that PxMWCommunicationServiceImplV[X] contains the method you want to simulate.
 * 3. Add a new method following the example below
 * 4. Find a corresponding class PxMWDataV[X] in the simulator service and add a method
 *      a. If class doesn't exist for the version you are working on, create the class and look at PxMWMessageHandler for instructions
 * For example search for any method names in this file (ex:devicesV1)
 *
 */
public class PxMWSimulatorApiControllerV1 {
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;
    private static final Logger log = YukonLogManager.getLogger(PxMWCommunicationServiceImplV1.class);
    
    @GetMapping("/deviceprofile/{id}")
    public ResponseEntity<Object> deviceprofileV1(@PathVariable String id) {
        try {
            PxMWSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new PxMWSimulatorRequest(PxMWRetrievalUrl.DEVICE_PROFILE_BY_GUID_V1, "deviceProfileV1",
                            new Class[] { String.class }, new Object[] { id }), PxMWSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/sites/{id}/devices")
    public ResponseEntity<Object> devicesV1(@PathVariable String id, @RequestParam(required=false) Boolean recursive,
            @RequestParam(required=false) Boolean includeDetail) {
        try {
            PxMWSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new PxMWSimulatorRequest(PxMWRetrievalUrl.DEVICES_BY_SITE_V1, "devicesV1",
                            new Class[] { String.class, Boolean.class, Boolean.class },
                            new Object[] { id, recursive, includeDetail }), PxMWSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/devices/{id}/timeseries/latest")
    public ResponseEntity<Object> timeseriesLatestV1(@PathVariable String id, @RequestParam String tags) {
        try {
            PxMWSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new PxMWSimulatorRequest(PxMWRetrievalUrl.DEVICE_TIMESERIES_LATEST, "getTimeseriesLatestV1",
                            new Class[] { String.class, String.class }, new Object[] { id, tags }), PxMWSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/devices/{id}/commands/getchannelvalues")
    public ResponseEntity<Object> getChannelValuesV1(@PathVariable String id, @RequestBody PxMWChannelValuesRequestV1 pxMWChannelValuesRequestV1) {
        try {
            PxMWSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new PxMWSimulatorRequest(PxMWRetrievalUrl.DEVICE_GET_CHANNEL_VALUES_V1, "getChannelValuesV1",
                            new Class[] { String.class, List.class }, new Object[] { id, pxMWChannelValuesRequestV1.getTags() }), PxMWSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/security/token")
    public ResponseEntity<Object> token(@RequestBody PxMWCredentialsV1  pxMWCredentialsV1) {
        try {
            PxMWSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new PxMWSimulatorRequest(PxMWRetrievalUrl.SECURITY_TOKEN, "token",
                            new Class[] {}, new Object[] {}), PxMWSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/devices/cloudenable")
    public ResponseEntity<Object> cloudEnableV1(@RequestParam String id, @RequestParam Boolean state) {
        try {
            PxMWSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new PxMWSimulatorRequest(PxMWRetrievalUrl.CLOUD_ENABLE, "cloudEnableV1",
                            new Class[] { String.class, Boolean.class},
                            new Object[] { id, state }), PxMWSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/devices/{id}/commands/{command_instance_id}")
    public ResponseEntity<Object> sendCommandV1(@PathVariable String id, @PathVariable String command_instance_id,
            @RequestBody PxMWCommandRequestV1 pxMWCommandRequestV1) {
        try {
            PxMWSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new PxMWSimulatorRequest(PxMWRetrievalUrl.COMMANDS, "sendCommandV1",
                            new Class[] { String.class, String.class, PxMWCommandRequestV1.class },
                            new Object[] { id, command_instance_id, pxMWCommandRequestV1 }),
                            PxMWSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/devices/timeseries/")
    public ResponseEntity<Object> getTimeSeriesValues(@RequestBody PxMWTimeSeriesDataRequestV1 pxMWTimeSeriesDataRequestV1) {
        try {
            PxMWSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new PxMWSimulatorRequest(PxMWRetrievalUrl.TREND_DATA_RETRIEVAL, "getTrendDataV1",
                            new Class[] { String.class, String.class, PxMWTimeSeriesDataRequestV1.class },
                            new Object[] { pxMWTimeSeriesDataRequestV1 }),
                            PxMWSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
