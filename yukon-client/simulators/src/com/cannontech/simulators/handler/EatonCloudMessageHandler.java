package com.cannontech.simulators.handler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.eatonCloud.model.EatonCloudRetrievalUrl;
import com.cannontech.dr.eatonCloud.model.EatonCloudVersion;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.eatonCloud.model.EatonCloudDataGenerator;
import com.cannontech.simulators.eatonCloud.model.EatonCloudDataV1;
import com.cannontech.simulators.eatonCloud.model.EatonCloudFakeTimeseriesDataV1;
import com.cannontech.simulators.message.request.EatonCloudSimulatorDeviceCreateRequest;
import com.cannontech.simulators.message.request.EatonCloudSimulatorRequest;
import com.cannontech.simulators.message.request.EatonCloudSimulatorSettingsUpdateRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.EatonCloudSimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.system.dao.GlobalSettingDao;

/**
 * This class gets a message EatonCloudSimulatorRequest from WS, which contains a method information to be called using reflection on the data object specified for that version.
 * 
 * If attempting to simulate the data for a new version add an entry
 * EatonCloudVersion.V2, new EatonCloudv2Data() to map the new version to a new object.
 */
public class EatonCloudMessageHandler extends SimulatorMessageHandler {
    @Autowired GlobalSettingDao settingDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private EatonCloudFakeTimeseriesDataV1 eatonCloudFakeTimeseriesDataV1;
    private static final Logger log = YukonLogManager.getLogger(EatonCloudMessageHandler.class);

    public EatonCloudMessageHandler() {
        super(SimulatorType.EATON_CLOUD);
    }

    private Map<EatonCloudVersion, EatonCloudDataGenerator> data;
    //Map.of(EatonCloudVersion.V2, new EatonCloudv2Data());
    
    @PostConstruct
    void init() {
        data = Map.of(EatonCloudVersion.V1, new EatonCloudDataV1(eatonCloudFakeTimeseriesDataV1));
    }
    
    private Map<EatonCloudRetrievalUrl, Integer> statuses = Arrays.stream(EatonCloudRetrievalUrl.values())
            .collect(Collectors.toMap(v -> v, v -> HttpStatus.OK.value()));
    
    private Map<EatonCloudRetrievalUrl, Integer> successPercentages= new HashMap<>();
   
    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            if (simulatorRequest instanceof EatonCloudSimulatorRequest) {
                EatonCloudSimulatorRequest request = (EatonCloudSimulatorRequest) simulatorRequest;
                if (data.containsKey(request.getUrl().getVersion())) {
                    try {
                        //status requested to be returned by the user
                        int status = statuses.get(request.getUrl());
                        EatonCloudDataGenerator generator = data.get(request.getUrl().getVersion());
                        generator.setSettingDao(settingDao);
                        generator.setStatus(status);
                        generator.setSuccessPercentage(HttpStatus.OK.value() == status && successPercentages
                                .get(request.getUrl()) != null ? successPercentages.get(request.getUrl()) : 100);
                        
                        generator.setNextValueHelper(nextValueHelper);
                        Method method = generator.getClass().getMethod(request.getMethod(), request.getParamClasses());
                        return (EatonCloudSimulatorResponse) method.invoke(generator, request.getParamValues());
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "Unable to use reflection to call method " + request.getMethod() + " to get data", e);
                    }
                }
            } else if (simulatorRequest instanceof EatonCloudSimulatorSettingsUpdateRequest) {
                EatonCloudSimulatorSettingsUpdateRequest request = (EatonCloudSimulatorSettingsUpdateRequest) simulatorRequest;
                statuses = request.getStatuses();
                successPercentages = request.getSuccessPercentages();
                return new SimulatorResponseBase(true);
            } else if (simulatorRequest instanceof EatonCloudSimulatorDeviceCreateRequest) {
                EatonCloudSimulatorDeviceCreateRequest request = (EatonCloudSimulatorDeviceCreateRequest) simulatorRequest;
                EatonCloudDataGenerator generator = data.get(request.getVersion());
                generator.setSettingDao(settingDao);
                if (request.isComplete()) {
                    // auto creation is done
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
            log.error("Exception handling request: " + simulatorRequest, e);
            throw e;
        }
    }
}
