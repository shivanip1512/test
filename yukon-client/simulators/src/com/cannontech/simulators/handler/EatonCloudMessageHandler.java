package com.cannontech.simulators.handler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.eatonCloud.model.EatonCloudRetrievalUrl;
import com.cannontech.dr.eatonCloud.model.EatonCloudVersion;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.eatonCloud.model.EatonCloudDataGenerator;
import com.cannontech.simulators.message.request.EatonCloudSimulatorDeviceCreateRequest;
import com.cannontech.simulators.message.request.EatonCloudSimulatorRequest;
import com.cannontech.simulators.message.request.EatonCloudSimulatorSettingsUpdateRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.EatonCloudSimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

/**
 * This class gets a message EatonCloudSimulatorRequest from WS, which contains a method information to be called using reflection on the data object specified for that version.
 */
public class EatonCloudMessageHandler extends SimulatorMessageHandler {

    private static final Logger log = YukonLogManager.getLogger(EatonCloudMessageHandler.class);

    public EatonCloudMessageHandler() {
        super(SimulatorType.EATON_CLOUD);
    }

    @Autowired private List<EatonCloudDataGenerator> data;
    
    
    private Map<EatonCloudRetrievalUrl, Integer> statuses = Arrays.stream(EatonCloudRetrievalUrl.values())
            .collect(Collectors.toMap(v -> v, v -> HttpStatus.OK.value()));
    
    private Map<EatonCloudRetrievalUrl, Integer> successPercentages= new HashMap<>();
   
    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            if (simulatorRequest instanceof EatonCloudSimulatorRequest) {
                EatonCloudSimulatorRequest request = (EatonCloudSimulatorRequest) simulatorRequest;

                try {
                    // status requested to be returned by the user
                    int status = statuses.get(request.getUrl());
                    EatonCloudDataGenerator generator = getGenerator(request.getUrl().getVersion());
                    generator.setStatus(status);
                    generator.setSuccessPercentage(HttpStatus.OK.value() == status && successPercentages
                            .get(request.getUrl()) != null ? successPercentages.get(request.getUrl()) : 100);

                    Method method = generator.getClass().getMethod(request.getMethod(), request.getParamClasses());
                    return (EatonCloudSimulatorResponse) method.invoke(generator, request.getParamValues());
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "Unable to use reflection to call method " + request.getMethod() + " to get data", e);
                }

            } else if (simulatorRequest instanceof EatonCloudSimulatorSettingsUpdateRequest) {
                EatonCloudSimulatorSettingsUpdateRequest request = (EatonCloudSimulatorSettingsUpdateRequest) simulatorRequest;
                statuses = request.getStatuses();
                successPercentages = request.getSuccessPercentages();
                return new SimulatorResponseBase(true);
            } else if (simulatorRequest instanceof EatonCloudSimulatorDeviceCreateRequest) {
                EatonCloudSimulatorDeviceCreateRequest request = (EatonCloudSimulatorDeviceCreateRequest) simulatorRequest;
                EatonCloudDataGenerator generator = getGenerator(request.getVersion());
                if (request.isComplete()) {
                    // auto creation is done
                    generator.setCreateRequest(null);
                } else {
                    generator.setCreateRequest(request);
                }
                return new SimulatorResponseBase(true);
            }

            throw new IllegalArgumentException(
                    "Unsupported request type received: " + simulatorRequest.getClass().getCanonicalName());

        } catch (Exception e) {
            log.error("Exception handling request: " + simulatorRequest, e);
            throw e;
        }
    }

    private EatonCloudDataGenerator getGenerator(EatonCloudVersion version) {
        EatonCloudDataGenerator generator = data.stream()
                .filter(d -> d.getDataGenerator(version) != null)
                .findFirst()
                .orElse(null);
        if(generator == null) {
            throw new RuntimeException("Data generator for " + version + " is not found.");
        }
        return generator;
    }
}
