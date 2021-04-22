package com.cannontech.simulators.handler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;
import com.cannontech.dr.pxmw.model.PxMWVersion;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.PxMWSimulatorDeviceCreateRequest;
import com.cannontech.simulators.message.request.PxMWSimulatorRequest;
import com.cannontech.simulators.message.request.PxMWSimulatorSettingsUpdateRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.PxMWSimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.simulators.pxmw.model.PxMWDataGenerator;
import com.cannontech.simulators.pxmw.model.PxMWDataV1;

/**
 * This class gets a message PxMWSimulatorRequest from WS, which contains a method information to be called using reflection on the data object specified for that version.
 * 
 * If attempting to simulate the data for a new version add an entry
 * PxMWVersion.V2, new PxMWv2Data() to map the new version to a new object.
 */
public class PxMWMessageHandler extends SimulatorMessageHandler {
    @Autowired private DeviceDao deviceDao;
    @Autowired private NextValueHelper nextValueHelper;
    private static final Logger log = YukonLogManager.getLogger(PxMWMessageHandler.class);

    public PxMWMessageHandler() {
        super(SimulatorType.PX_MIDDLEWARE);
    }

    private Map<PxMWVersion, PxMWDataGenerator> data;
    //Map.of(PxMWVersion.V2, new PxMWv2Data());
    
    @PostConstruct
    void init() {
        data = Map.of(PxMWVersion.V1, new PxMWDataV1(deviceDao));
    }
    
    private Map<PxMWRetrievalUrl, Integer> statuses = Arrays.stream(PxMWRetrievalUrl.values())
            .collect(Collectors.toMap(v -> v, v -> HttpStatus.OK.value()));
   
    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        
        try {
            if (simulatorRequest instanceof PxMWSimulatorRequest) {
                PxMWSimulatorRequest request = (PxMWSimulatorRequest) simulatorRequest;
                if (data.containsKey(request.getUrl().getVersion())) {
                    try {
                        //status requested to be returned by the user
                        int status = statuses.get(request.getUrl());
                        PxMWDataGenerator generator = data.get(request.getUrl().getVersion());
                        generator.setStatus(status);
                        generator.setNextValueHelper(nextValueHelper);
                        Method method = generator.getClass().getMethod(request.getMethod(), request.getParamClasses());
                        return (PxMWSimulatorResponse) method.invoke(generator, request.getParamValues());
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "Unable to use reflection to call method " + request.getMethod() + " to get data", e);
                    }
                }
            } else if (simulatorRequest instanceof PxMWSimulatorSettingsUpdateRequest) {
                PxMWSimulatorSettingsUpdateRequest request = (PxMWSimulatorSettingsUpdateRequest) simulatorRequest;
                statuses = request.getStatuses();
                return new SimulatorResponseBase(true);
            } else if (simulatorRequest instanceof PxMWSimulatorDeviceCreateRequest) {
                PxMWSimulatorDeviceCreateRequest request = (PxMWSimulatorDeviceCreateRequest) simulatorRequest;
                PxMWDataGenerator generator = data.get(request.getVersion());
                if (request.isComplete()) {
                    //auto creation is done
                    generator.setCreateRequest(null);
                } else {
                    generator.setCreateRequest(request);
                    generator.setNextValueHelper(nextValueHelper);
                }
                return new SimulatorResponseBase(true);
            }

            throw new IllegalArgumentException(
                    "Unsupported request type received: " + simulatorRequest.getClass().getCanonicalName());

        } catch (Exception e) {
            log.error("Exception handling request: " + simulatorRequest);
            throw e;
        }
    }
}
