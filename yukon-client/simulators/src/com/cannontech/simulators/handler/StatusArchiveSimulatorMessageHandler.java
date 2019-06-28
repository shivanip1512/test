package com.cannontech.simulators.handler;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.simulation.service.NmNetworkSimulatorService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.request.StatusArchiveSimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class StatusArchiveSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(StatusArchiveSimulatorMessageHandler.class);
    @Autowired @Qualifier("longRunning") private Executor executor;
    @Autowired private NmNetworkSimulatorService nmNetworkSimulatorService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    private boolean isRunning = false;

    public StatusArchiveSimulatorMessageHandler() {
        super(SimulatorType.STATUS_ARCHIVE);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            if (simulatorRequest instanceof StatusArchiveSimulatorRequest) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            isRunning = true;
                            StatusArchiveSimulatorRequest request = (StatusArchiveSimulatorRequest) simulatorRequest;
                            Set<RfnIdentifier> devices =
                                rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfMeterTypes()).stream().map(
                                    device -> device.getRfnIdentifier()).collect(Collectors.toSet());
                            nmNetworkSimulatorService.sendDemandResetStatusArchiveRequest(devices,
                                request.getMessageCount());
                        } catch (Exception e) {
                            log.error("Asynchronous device creation failed", e);
                        } finally {
                            isRunning = false;
                        }
                    }
                };
                if (!isRunning) {
                    executor.execute(thread);
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
