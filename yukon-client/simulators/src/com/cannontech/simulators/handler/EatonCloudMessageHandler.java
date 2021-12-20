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
import com.cannontech.simulators.message.request.EatonCloudSimulatorStatisticsRequest;
import com.cannontech.simulators.message.request.EatonCloudSimulatorStatisticsResponse;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.EatonCloudSimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

/**
 * This class gets a message EatonCloudSimulatorRequest from WS, which contains a method information to be called using reflection on the data object specified for that version.
 */
public class EatonCloudMessageHandler extends SimulatorMessageHandler {

    private static final Logger log = YukonLogManager.getLogger(EatonCloudMessageHandler.class);

    public EatonCloudMessageHandler() {
        super(SimulatorType.EATON_CLOUD);
    }

    @Autowired private List<EatonCloudDataGenerator> data;
    @Autowired private GlobalSettingDao settingDao;
    
    
    private Map<EatonCloudRetrievalUrl, Integer> statuses = Arrays.stream(EatonCloudRetrievalUrl.values())
            .collect(Collectors.toMap(v -> v, v -> HttpStatus.OK.value()));
    
    private Map<EatonCloudRetrievalUrl, Integer> successPercentages= new HashMap<>();
   
    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            if (simulatorRequest instanceof EatonCloudSimulatorRequest) {
                EatonCloudSimulatorRequest request = (EatonCloudSimulatorRequest) simulatorRequest;
                EatonCloudDataGenerator generator = getGenerator(request.getUrl().getVersion());
                initSettings(request.getUrl(), generator);
                Method method;
                try {
                    if (request.getUrl() == EatonCloudRetrievalUrl.SECURITY_TOKEN) {
                        String secret = settingDao.getString(GlobalSettingType.EATON_CLOUD_SECRET2);
                        if (secret.equals(request.getHeader())) {
                            initSettings(EatonCloudRetrievalUrl.SECURITY_TOKEN2, generator);
                            method = generator.getClass().getMethod("token2", request.getParamClasses());
                        } else {
                            method = generator.getClass().getMethod("token1", request.getParamClasses());
                        }

                    } else {
                        log.info("Header:{} ", request.getHeader());
                        String token = request.getHeader().replaceAll("Bearer ", "");
                        checkTokenRetrievalFailure(generator.getToken1(), token,
                                EatonCloudRetrievalUrl.SECURITY_TOKEN, generator);

                        checkTokenRetrievalFailure(generator.getToken2(), token,
                                EatonCloudRetrievalUrl.SECURITY_TOKEN2, generator);
                        method = generator.getClass().getMethod(request.getMethod(), request.getParamClasses());
                    }
                    return (EatonCloudSimulatorResponse) method.invoke(generator, request.getParamValues());
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "Unable to use reflection to call method " + request.getMethod() + " to get data", e);
                }

            } else if (simulatorRequest instanceof EatonCloudSimulatorSettingsUpdateRequest) {
                EatonCloudSimulatorSettingsUpdateRequest request = (EatonCloudSimulatorSettingsUpdateRequest) simulatorRequest;
                if (request.isResetSecretsExpireTime()) {
                    EatonCloudDataGenerator generator = getGenerator(request.getVersion());
                    generator.expireSecrets();
                } else {
                    statuses = request.getStatuses();
                    successPercentages = request.getSuccessPercentages();
                }
                return new SimulatorResponseBase(true);
            } else if (simulatorRequest instanceof EatonCloudSimulatorStatisticsRequest) {
                EatonCloudSimulatorStatisticsRequest request = (EatonCloudSimulatorStatisticsRequest) simulatorRequest;
                EatonCloudDataGenerator generator = getGenerator(request.getVersion());
                return new EatonCloudSimulatorStatisticsResponse(generator.getToken1(), generator.getExpiryTime1(),
                        generator.getToken2(), generator.getExpiryTime2());
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

    void checkTokenRetrievalFailure(String cachedToken, String headerToken, EatonCloudRetrievalUrl tokenUrl,
            EatonCloudDataGenerator generator) {
        if (headerToken.equals(cachedToken)) {
            int status = statuses.get(tokenUrl);
            if (status != HttpStatus.OK.value()) {
                generator.setStatus(status);
            }
        }
    }

    private void initSettings(EatonCloudRetrievalUrl url, EatonCloudDataGenerator generator) {
        int status = statuses.get(url);
        generator.setStatus(status);
        generator.setSuccessPercentage(HttpStatus.OK.value() == status && successPercentages
                .get(url) != null ? successPercentages.get(url) : 100);
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
