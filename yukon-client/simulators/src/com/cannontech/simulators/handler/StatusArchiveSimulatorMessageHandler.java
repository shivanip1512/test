package com.cannontech.simulators.handler;

import static com.cannontech.common.stream.StreamUtils.not;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.status.RfnStatusArchiveRequest;
import com.cannontech.amr.rfn.message.status.type.MeterDisconnectStatus;
import com.cannontech.amr.rfn.message.status.type.MeterInfo;
import com.cannontech.amr.rfn.message.status.type.MeterInfoStatus;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.archive.RfnDeviceArchiveRequest;
import com.cannontech.common.rfn.simulation.service.NmNetworkSimulatorService;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.request.DemandResetStatusArchiveSimulatorRequest;
import com.cannontech.simulators.message.request.DeviceArchiveSimulatorRequest;
import com.cannontech.simulators.message.request.MeterInfoStatusArchiveSimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class StatusArchiveSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(StatusArchiveSimulatorMessageHandler.class);
    @Autowired @Qualifier("longRunning") private Executor executor;
    @Autowired private NmNetworkSimulatorService nmNetworkSimulatorService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public StatusArchiveSimulatorMessageHandler() {
        super(SimulatorType.STATUS_ARCHIVE);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            if (simulatorRequest instanceof DemandResetStatusArchiveSimulatorRequest) {
                if (!isRunning.compareAndExchange(false, true)) {
                    executor.execute(() -> {
                        try {
                            DemandResetStatusArchiveSimulatorRequest request = (DemandResetStatusArchiveSimulatorRequest) simulatorRequest;
                            Set<RfnIdentifier> devices =
                                rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfMeterTypes()).stream().map(
                                    device -> device.getRfnIdentifier()).collect(Collectors.toSet());
                            nmNetworkSimulatorService.sendDemandResetStatusArchiveRequest(devices,
                                request.getMessageCount(), request.getDemandResetStatusCode());
                        } catch (Exception e) {
                            log.error("Asynchronous device creation failed", e);
                        } finally {
                            isRunning.set(false);
                        }
                    });
                    return new SimulatorResponseBase(true);
                }
            } else if (simulatorRequest instanceof MeterInfoStatusArchiveSimulatorRequest) {  
                if (!isRunning.compareAndExchange(false, true)) {
                    executor.execute(() -> {
                        try {
                            var report = (MeterInfoStatusArchiveSimulatorRequest) simulatorRequest;

                            var info = new MeterInfo();

                            info.setMeterDisconnectStatus(report.getDisconnectStatus());
                            info.setMeterConfigurationID(report.getMeterConfigurationId());
                            
                            nmNetworkSimulatorService.sendMeterInfoStatusArchiveRequest(report.getRfnIdentifiers(), report.getTimestamp(), info);
                        } catch (Exception e) {
                            log.error("Asynchronous device creation failed", e);
                        } finally {
                            isRunning.set(false);
                        }
                    });
                    return new SimulatorResponseBase(true);
                }
            } 
        } catch (Exception e) {
            log.error("Exception handling request: " + simulatorRequest);
            throw e;
        }
        return new SimulatorResponseBase(false);
    }
}
