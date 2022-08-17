package com.cannontech.simulators.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.archive.RfnDeviceArchiveRequest;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.DeviceArchiveSimulatorRequest;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

public class DeviceArchiveSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getLogger(DeviceArchiveSimulatorMessageHandler.class);
    private JmsTemplate jmsTemplate;
    @Autowired @Qualifier("longRunning") private Executor executor;
    private boolean isRunning = false;

    public DeviceArchiveSimulatorMessageHandler() {
        super(SimulatorType.DEVICE_ARCHIVE);
    }

    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            if (simulatorRequest instanceof DeviceArchiveSimulatorRequest) {  
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                isRunning = true;
                                DeviceArchiveSimulatorRequest request = (DeviceArchiveSimulatorRequest) simulatorRequest;
                                Map<Long, RfnIdentifier> rfnIdentifiers = new HashMap<>();
                                for (int i = request.getSerialFrom(); i <= request.getSerialTo(); i++) {
                                    rfnIdentifiers.put(Long.valueOf(i), new RfnIdentifier(Integer.toString(i),
                                                                                    request.getManufacturer(),
                                                                                    request.getModel()));

                                }
                                RfnDeviceArchiveRequest response = new RfnDeviceArchiveRequest();
                                response.setRfnIdentifiers(rfnIdentifiers);
                                jmsTemplate.convertAndSend(JmsApiDirectory.RFN_DEVICE_ARCHIVE.getQueue().getName(), response);
                            } catch (Exception e) {
                                log.error("Asynchronous device creation failed", e);
                            } finally {
                                isRunning = false;
                            }
                        }
                    };
                    if(!isRunning) {
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
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}
