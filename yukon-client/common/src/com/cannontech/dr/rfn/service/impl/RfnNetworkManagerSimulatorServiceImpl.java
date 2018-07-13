package com.cannontech.dr.rfn.service.impl;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectRequest;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.GatewayDataRequest;
import com.cannontech.common.rfn.message.gateway.MeterReadResponse;
import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.RfnNetworkManagerSimulatorSettings;
import com.cannontech.dr.rfn.service.RfnNetworkManagerSimulatorService;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;

public class RfnNetworkManagerSimulatorServiceImpl implements RfnNetworkManagerSimulatorService {

    private static final Logger log = YukonLogManager.getLogger(RfnNetworkManagerSimulatorServiceImpl.class);
    private static final String meterReadRequestQueue = "yukon.qr.obj.amr.rfn.MeterReadRequest";
    private static final String meterDisconnectRequestQueue = "yukon.qr.obj.amr.rfn.MeterDisconnectRequest";
    private RfnDataSimulatorStatus status = new RfnDataSimulatorStatus();
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    private RfnNetworkManagerSimulatorSettings settings;
    
    private volatile boolean meterReadReplyActive;
    private volatile boolean meterReadReplyStopping;
    
    private volatile boolean meterDisconnectReplyActive;
    private volatile boolean meterDisconnectReplyStopping;
    
    private static final int incomingMessageWaitMillis = 1000;
    private JmsTemplate jmsTemplate;
    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(false);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.setReceiveTimeout(incomingMessageWaitMillis);
    }
    
    // Currently unused
    @Override
    public synchronized void startSimulator(RfnNetworkManagerSimulatorSettings settings) {
        if (!status.isRunning().get()) {
            status = new RfnDataSimulatorStatus();
            status.setRunning(new AtomicBoolean(true));
            status.setStartTime(new Instant());
        }
    }
    
    // Currently unused
    @Override
    public void stopSimulator() {
        status.setStopTime(new Instant());
        status.setRunning(new AtomicBoolean(false));
    }
    
    @Override
    public boolean startMeterReadReply(RfnNetworkManagerSimulatorSettings settings) {
        if (meterReadReplyActive) {
            return false;
        } else {
            Thread meterReadThread = getMeterReadThread(settings);
            meterReadThread.start();
            //saveSettings(settings);
            //updateReplySettings = settings;
            meterReadReplyActive = true;
            return true;
        }
    }
    
    @Override
    public void stopMeterReadReply() {
        if (meterReadReplyActive) {
            meterReadReplyStopping = true;
            //updateReplySettings = null;
        }
    }
    
    @Override
    public boolean startMeterDisconnectReply(RfnNetworkManagerSimulatorSettings settings) {
        
        if (meterDisconnectReplyActive) {
            return false;
        } else {
            
            settings.setDisconnectReply1(RfnMeterDisconnectInitialReplyType.OK);
            settings.setDisconnectReply1FailPercent(0);
            settings.setDisconnectReply2(RfnMeterDisconnectConfirmationReplyType.SUCCESS);
            settings.setDisconnectReply2FailPercent(0);
            
            saveDisconnectSettings(settings);
            if (this.settings == null) {
                this.settings = settings;
            }
            
            Thread meterDisconnectThread = getMeterDisconnectThread(settings);
            meterDisconnectThread.start();
            
            meterDisconnectReplyActive = true;
            return true;
        }
    }
    
    @Override
    public void stopMeterDisconnectReply() {
        if (meterDisconnectReplyActive) {
            meterDisconnectReplyStopping = true;
            //updateReplySettings = null;
        }
    }
    
    public void saveDisconnectSettings(RfnNetworkManagerSimulatorSettings settings) {
        log.debug("Saving RFN_METER_NETWORK settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_NETWORK_SIMULATOR_DISCONNECT_REPLY1, settings.getDisconnectReply1());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_NETWORK_SIMULATOR_DISCONNECT_REPLY2, settings.getDisconnectReply2());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_NETWORK_SIMULATOR_DISCONNECT_FAIL_RATE_1, settings.getDisconnectReply1FailPercent());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_NETWORK_SIMULATOR_DISCONNECT_FAIL_RATE_2, settings.getDisconnectReply2FailPercent());
    }
    
    public RfnNetworkManagerSimulatorSettings getCurrentDisconnectSettings() {
        if (settings == null) {
            log.debug("Getting RFN_METER_NETWORK SimulatorSettings from db.");
            RfnNetworkManagerSimulatorSettings simulatorSettings = new RfnNetworkManagerSimulatorSettings();
            simulatorSettings.setDisconnectReply1(RfnMeterDisconnectInitialReplyType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_NETWORK_SIMULATOR_DISCONNECT_REPLY1)));
            simulatorSettings.setDisconnectReply1FailPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_NETWORK_SIMULATOR_DISCONNECT_FAIL_RATE_1));
            simulatorSettings.setDisconnectReply2(RfnMeterDisconnectConfirmationReplyType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_NETWORK_SIMULATOR_DISCONNECT_REPLY2)));
            simulatorSettings.setDisconnectReply2FailPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_NETWORK_SIMULATOR_DISCONNECT_FAIL_RATE_2));
            settings = simulatorSettings;
        }
        return settings;
    }
    
    @Override
    public void startSimulatorWithCurrentSettings() {
        startSimulator(getCurrentSettings());
    }
    
    @Override
    public RfnNetworkManagerSimulatorSettings getCurrentSettings() {
        return getCurrentDisconnectSettings();
    }

    /**
     * Generate a thread that monitors the gateway data request queue, handles requests, and sends responses.
     */
    private Thread getMeterReadThread(RfnNetworkManagerSimulatorSettings settings) {
        Thread meterReadRunner = new Thread() {
            @Override
            public void run() {
                
                while (!meterReadReplyStopping) {
                    try {
                        
                        Object message = jmsTemplate.receive(meterReadRequestQueue);
                        if (message != null && message instanceof ObjectMessage) {
                            
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            GatewayDataRequest request = (GatewayDataRequest) requestMessage.getObject();
                            
                            MeterReadResponse response = setUpReadResponse(request.getRfnIdentifier(), settings);
                            
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response);
                        }
                    } catch (Exception e) {
                        log.error("Error occurred in meter read reply.", e);
                    }
                }
                
                log.info("Meter read thread shutting down.");
                meterReadReplyStopping = false;
                meterReadReplyActive = false;
            }
            
            
        };
        return meterReadRunner;
    }
    
    /**
     * Generate a thread that monitors the gateway data request queue, handles requests, and sends responses.
     */
    private Thread getMeterDisconnectThread(RfnNetworkManagerSimulatorSettings settings) {
        Thread meterDisconnectRunner = new Thread() {
            @Override
            public void run() {
                while (!meterDisconnectReplyStopping) {
                    try {
                        Object message = jmsTemplate.receive(meterDisconnectRequestQueue);
                        if (message != null && message instanceof ObjectMessage) {
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            RfnMeterDisconnectRequest request = (RfnMeterDisconnectRequest) requestMessage.getObject();
                            
                            RfnMeterDisconnectInitialReply response1 = setUpDisconnectInitialResponse(request.getRfnIdentifier(), settings);
                            RfnMeterDisconnectConfirmationReply response2 = setUpDisconnectConfirmationResponse(request, settings);
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response1);
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response2);
                            
                        }
                    } catch (Exception e) {
                        log.error("Error occurred in meter disconnect reply.", e);
                    }
                }
                
                log.info("Meter dissconnect thread shutting down.");
                meterDisconnectReplyStopping = false;
                meterDisconnectReplyActive = false;
            }
            
            
        };
        return meterDisconnectRunner;
    }
    
    private MeterReadResponse setUpReadResponse(RfnIdentifier rfnId, RfnNetworkManagerSimulatorSettings settings) {
        MeterReadResponse response = new MeterReadResponse();
        
        return response;
    }
    
    private RfnMeterDisconnectInitialReply setUpDisconnectInitialResponse(RfnIdentifier rfnId, RfnNetworkManagerSimulatorSettings settings) {
        RfnMeterDisconnectInitialReply response = new RfnMeterDisconnectInitialReply();
        
        // Will send out the type specified by the user when the UI is implemented
        response.setReplyType(settings.getDisconnectReply1());
        
        return response;
    }
    
    private RfnMeterDisconnectConfirmationReply setUpDisconnectConfirmationResponse(RfnMeterDisconnectRequest request, RfnNetworkManagerSimulatorSettings settings) {
        RfnMeterDisconnectConfirmationReply response = new RfnMeterDisconnectConfirmationReply();
        
        // Will send out the type specified by the user when the UI is implemented
        response.setReplyType(settings.getDisconnectReply2());
        // Calculates Fail Rates for disconnect and connect
         if(settings.getDisconnectReply2() == RfnMeterDisconnectConfirmationReplyType.SUCCESS && generateFailOrNot(0)) {
             response.setReplyType(RfnMeterDisconnectConfirmationReplyType.FAILURE);
         } else if (settings.getDisconnectReply2() == RfnMeterDisconnectConfirmationReplyType.FAILURE && generateFailOrNot(0)) {
             response.setReplyType(RfnMeterDisconnectConfirmationReplyType.SUCCESS);
         }
        // Echo back of whatever user provides
        response.setState(RfnMeterDisconnectState.getForType(request.getAction()));
        
        return response;
    }
    
    // Returns true if reply should fail, false otherwise
    private boolean generateFailOrNot(int failureRate) {
        Random r = new Random();
        int n = r.nextInt(100) + 1; // Generates a random number between 1 and 100
        
        if (n <= failureRate) { // If the number is under the fail rate, then the test fails
            return true;
        } else {
            return false;
        }
    }
    
  //@Override
    public boolean isMeterReadReplyActive() {
        return meterReadReplyActive;
    }
    
    //@Override
    public boolean isMeterDisconnectReplyActive() {
        return meterDisconnectReplyActive;
    }
    
    //@Override
    public boolean isMeterReadReplyStopping() {
        return meterReadReplyStopping;
    }
    
    //@Override
    public boolean isMeterDisconnectReplyStopping() {
        return meterDisconnectReplyStopping;
    }

    @Override
    public RfnDataSimulatorStatus getStatus() {
        return status;
    }
}
