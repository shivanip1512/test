package com.cannontech.web.dev;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.dr.eatonCloud.model.EatonCloudRetrievalUrl;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCredentialsV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudJobRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDataRequestV1;
import com.cannontech.dr.eatonCloud.service.impl.v1.EatonCloudCommunicationServiceImplV1;
import com.cannontech.simulators.message.request.EatonCloudSimulatorRequest;
import com.cannontech.simulators.message.response.EatonCloudSimulatorResponse;
import com.cannontech.web.security.annotation.CheckCparm;

@RestController
@RequestMapping("/api")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
/**
 * This controller is used to simulate API responses. The message is sent to Simulator service which will generate a fake data,
 * representing a response, to be send to the calling service.
 * 
 * Adding a new method:
 * 1. Verify that you are looking at EatonCloudSimulatorApiController[X] of the correct version.
 * a. If the controller doesn't exist create a new one EatonCloudSimulatorApiController[Your Version]
 * 2. Verify that EatonCloudCommunicationServiceImplV[X] contains the method you want to simulate.
 * 3. Add a new method following the example below
 * 4. Find a corresponding class EatonCloudDataV[X] in the simulator service and add a method
 * a. If class doesn't exist for the version you are working on, create the class and look at EatonCloudMessageHandler for
 * instructions
 * For example search for any method names in this file (ex:devicesV1)
 *
 */
public class EatonCloudSimulatorApiControllerV1 {
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;
    private static final Logger log = YukonLogManager.getLogger(EatonCloudCommunicationServiceImplV1.class);

    @GetMapping("/v1/sites/{id}/devices")
    public ResponseEntity<Object> devicesV1(@PathVariable String id, @RequestParam(required = false) Boolean recursive,
            @RequestParam(required = false) Boolean includeDetail, @RequestHeader(name = "Authorization") String header) {
        try {
            EatonCloudSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new EatonCloudSimulatorRequest(EatonCloudRetrievalUrl.DEVICES_BY_SITE, "devicesV1",
                            new Class[] { String.class, Boolean.class, Boolean.class },
                            new Object[] { id, recursive, includeDetail }, header), EatonCloudSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error("Error", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/v1/security/serviceaccount/token")
    public ResponseEntity<Object> token(@RequestBody EatonCloudCredentialsV1 eatonCloudCredentialsV1) {
        try {
            EatonCloudSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new EatonCloudSimulatorRequest(EatonCloudRetrievalUrl.SECURITY_TOKEN, "token",
                            new Class[] {}, new Object[] {}, eatonCloudCredentialsV1.getSecret()),
                            EatonCloudSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error("Error", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/v1/devices/{id}/commands/{command_instance_id}")
    public ResponseEntity<Object> sendCommandV1(@PathVariable String id, @PathVariable String command_instance_id,
            @RequestBody EatonCloudCommandRequestV1 eatonCloudCommandRequestV1,
            @RequestHeader(name = "Authorization") String header) {
        try {
            EatonCloudSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new EatonCloudSimulatorRequest(EatonCloudRetrievalUrl.COMMANDS, "sendCommandV1",
                            new Class[] { String.class, String.class, EatonCloudCommandRequestV1.class },
                            new Object[] { id, command_instance_id, eatonCloudCommandRequestV1 }, header),
                            EatonCloudSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error("Error", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/v1/devices/timeseries/")
    public ResponseEntity<Object> timeseriesV1(@RequestBody EatonCloudTimeSeriesDataRequestV1 eatonCloudTimeSeriesDataRequestV1,
            @RequestHeader(name = "Authorization") String header) {
        try {
            EatonCloudSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new EatonCloudSimulatorRequest(EatonCloudRetrievalUrl.TREND_DATA_RETRIEVAL, "timeseriesV1",
                            new Class[] { EatonCloudTimeSeriesDataRequestV1.class },
                            new Object[] { eatonCloudTimeSeriesDataRequestV1 }, header),
                            EatonCloudSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (Exception e) {
            log.error("Error", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/v1/accesscontrol/sites")
    public ResponseEntity<Object> sitesV1(@RequestParam(required = true) String userId,
            @RequestHeader(name = "Authorization") String header) {
        try {
            EatonCloudSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new EatonCloudSimulatorRequest(EatonCloudRetrievalUrl.SITES, "sitesV1",
                            new Class[] { String.class },
                            new Object[] { userId }, header), EatonCloudSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error("Error", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/v1/devices/{deviceId}")
    public ResponseEntity<Object> detailsV1(@PathVariable String deviceId, @RequestParam(required = false) Boolean recursive,
            @RequestHeader(name = "Authorization") String header) {
        try {
            EatonCloudSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new EatonCloudSimulatorRequest(EatonCloudRetrievalUrl.DEVICE_DETAIL, "detailsV1",
                            new Class[] { String.class, Boolean.class },
                            new Object[] { deviceId, recursive }, header), EatonCloudSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error("Error", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/v1/security/serviceaccount/{serviceAccountId}")
    public ResponseEntity<Object> serviceAccountV1(@PathVariable String serviceAccountId,
            @RequestHeader(name = "Authorization") String header) {
        try {
            EatonCloudSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new EatonCloudSimulatorRequest(EatonCloudRetrievalUrl.ACCOUNT_DETAIL, "serviceAccountV1",
                            new Class[] { String.class },
                            new Object[] { serviceAccountId }, header), EatonCloudSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error("Error", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/v1/security/serviceaccount/{serviceAccountId}/secret/{secretName}/rotate")
    public ResponseEntity<Object> rotateV1(@PathVariable String serviceAccountId, @PathVariable String secretName,
            @RequestHeader(name = "Authorization") String header) {
        try {
            EatonCloudSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new EatonCloudSimulatorRequest(EatonCloudRetrievalUrl.ROTATE_ACCOUNT_SECRET, "rotateV1",
                            new Class[] { String.class, String.class },
                            new Object[] { serviceAccountId, secretName }, header), EatonCloudSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error("Error", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/v1/job/immediate")
    public ResponseEntity<Object> createJob(
            @RequestBody EatonCloudJobRequestV1 eatonCloudJobRequestV1,
            @RequestHeader(name = "Authorization") String header) {
        try {
            EatonCloudSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new EatonCloudSimulatorRequest(EatonCloudRetrievalUrl.JOB, "createJobV1",
                            new Class[] { EatonCloudJobRequestV1.class },
                            new Object[] { eatonCloudJobRequestV1 }, header),
                            EatonCloudSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error("Error", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/v1/job/immediate/{id}")
    public ResponseEntity<Object> getJobStatus(@PathVariable String id,
            @RequestHeader(name = "Authorization") String header) {
        try {
            EatonCloudSimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new EatonCloudSimulatorRequest(EatonCloudRetrievalUrl.JOB_STATUS, "jobStatusV1",
                            new Class[] { String.class },
                            new Object[] {id}, header), EatonCloudSimulatorResponse.class);
            return new ResponseEntity<>(response.getResponse(), HttpStatus.valueOf(response.getStatus()));
        } catch (ExecutionException e) {
            log.error("Error", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
