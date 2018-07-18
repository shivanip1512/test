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
import com.cannontech.amr.rfn.message.read.RfnMeterReadRequest;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlDisconnectSimulatorSettings;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlReadSimulatorSettings;
import com.cannontech.dr.rfn.service.RfnMeterReadAndControlSimulatorService;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;

public class RfnMeterReadAndControlSimulatorServiceImpl implements RfnMeterReadAndControlSimulatorService {

    private static final Logger log = YukonLogManager.getLogger(RfnMeterReadAndControlSimulatorServiceImpl.class);
    private static final String meterReadRequestQueue = "yukon.qr.obj.amr.rfn.MeterReadRequest";
    private static final String meterDisconnectRequestQueue = "yukon.qr.obj.amr.rfn.MeterDisconnectRequest";
    private RfnDataSimulatorStatus status = new RfnDataSimulatorStatus();
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    private RfnMeterReadAndControlDisconnectSimulatorSettings disconnectSettings;
    private RfnMeterReadAndControlReadSimulatorSettings readSettings;
    
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
    public synchronized void startSimulator(RfnMeterReadAndControlDisconnectSimulatorSettings settings) {
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
    public boolean startMeterReadReply(RfnMeterReadAndControlReadSimulatorSettings settings) {
        if (meterReadReplyActive) {
            return false;
        } else {
            Thread meterReadThread = getMeterReadThread(settings);
            meterReadThread.start();
            
            meterReadReplyActive = true;
            return true;
        }
    }
    
    @Override
    public void stopMeterReadReply() {
        if (meterReadReplyActive) {
            meterReadReplyStopping = true;
            readSettings = null;
        }
    }
    
    @Override
    public boolean startMeterDisconnectReply(RfnMeterReadAndControlDisconnectSimulatorSettings settings) {
        
        if (meterDisconnectReplyActive) {
            return false;
        } else {
            
            saveDisconnectSettings(settings);
            if (disconnectSettings == null) {
                disconnectSettings = settings;
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
            disconnectSettings = null;
        }
    }
    
    public void saveDisconnectSettings(RfnMeterReadAndControlDisconnectSimulatorSettings settings) {
        log.debug("Saving RFN_METER_READ_AND_CONTROL settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_REPLY1, settings.getDisconnectReply1());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_REPLY2, settings.getDisconnectReply2());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_FAIL_RATE_1, settings.getDisconnectReply1FailPercent());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_FAIL_RATE_2, settings.getDisconnectReply2FailPercent());
    }
    
    @Override
    public RfnMeterReadAndControlDisconnectSimulatorSettings getCurrentDisconnectSettings() {
        if (disconnectSettings == null) {
            log.debug("Getting RFN_METER_READ_AND_CONTROL SimulatorSettings from db.");
            RfnMeterReadAndControlDisconnectSimulatorSettings simulatorSettings = new RfnMeterReadAndControlDisconnectSimulatorSettings();
            simulatorSettings.setDisconnectReply1(RfnMeterDisconnectInitialReplyType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_REPLY1)));
            simulatorSettings.setDisconnectReply1FailPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_FAIL_RATE_1));
            simulatorSettings.setDisconnectReply2(RfnMeterDisconnectConfirmationReplyType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_REPLY2)));
            simulatorSettings.setDisconnectReply2FailPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_FAIL_RATE_2));
            disconnectSettings = simulatorSettings;
        }
        return disconnectSettings;
    }
    
    public void saveReadSettings(RfnMeterReadAndControlReadSimulatorSettings settings) {
        log.debug("Saving RFN_METER_READ_AND_CONTROL settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_REPLY1, settings.getReadReply1());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_REPLY2, settings.getReadReply2());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_1, settings.getReadReply1FailPercent());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_2, settings.getReadReply2FailPercent());
    }
    
    @Override
    public RfnMeterReadAndControlReadSimulatorSettings getCurrentReadSettings() {
        if (readSettings == null) {
            log.debug("Getting RFN_METER_READ_AND_CONTROL SimulatorSettings from db.");
            RfnMeterReadAndControlReadSimulatorSettings simulatorSettings = new RfnMeterReadAndControlReadSimulatorSettings();
            simulatorSettings.setReadReply1(RfnMeterReadingReplyType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_REPLY1)));
            simulatorSettings.setReadReply1FailPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_1));
            simulatorSettings.setReadReply2(RfnMeterReadingDataReplyType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_REPLY2)));
            simulatorSettings.setReadReply2FailPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_2));
            readSettings = simulatorSettings;
        }
        return readSettings;
    }
    
    // Will be used for auto start of simulator
    @Override
    public void startSimulatorWithCurrentSettings() {
        startSimulator(getCurrentSettings());
    }
    
    @Override
    public RfnMeterReadAndControlDisconnectSimulatorSettings getCurrentSettings() {
        // Read settings not added yet, will be added as part of read implementation
        return getCurrentDisconnectSettings();
    }

    /**
     * Generate a thread that monitors the meter read request queue, handles requests, and sends responses.
     */
    private Thread getMeterReadThread(RfnMeterReadAndControlReadSimulatorSettings settings) {
        Thread meterReadRunner = new Thread() {
            @Override
            public void run() {
                
                while (!meterReadReplyStopping) {
                    try {
                        
                        Object message = jmsTemplate.receive(meterReadRequestQueue);
                        if (message != null && message instanceof ObjectMessage) {
                            
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            // Unimplemented Because Read Functionality has not been implemented yet
                            RfnMeterReadRequest request = (RfnMeterReadRequest) requestMessage.getObject();
                            
                            //jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response);
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
     * Generate a thread that monitors the meter disconnect request queue, handles requests, and sends responses.
     */
    private Thread getMeterDisconnectThread(RfnMeterReadAndControlDisconnectSimulatorSettings settings) {
        Thread meterDisconnectRunner = new Thread() {
            @Override
            public void run() {
                while (!meterDisconnectReplyStopping) {
                    try {
                        Object message = jmsTemplate.receive(meterDisconnectRequestQueue);
                        if (message != null && message instanceof ObjectMessage) {
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            RfnMeterDisconnectRequest request = (RfnMeterDisconnectRequest) requestMessage.getObject();
                            
                            RfnMeterDisconnectInitialReply response1 = setUpDisconnectInitialResponse(settings);
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
    
    private RfnMeterDisconnectInitialReply setUpDisconnectInitialResponse(RfnMeterReadAndControlDisconnectSimulatorSettings settings) {
        RfnMeterDisconnectInitialReply response = new RfnMeterDisconnectInitialReply();
        
        // Calculates Fail Rates for disconnect and connect
        if(replyWithFailure(settings.getDisconnectReply1FailPercent())) {
            response.setReplyType(settings.getDisconnectReply1());
        } else {
            response.setReplyType(RfnMeterDisconnectInitialReplyType.OK);
        }
        
        return response;
    }
    
    private RfnMeterDisconnectConfirmationReply setUpDisconnectConfirmationResponse(RfnMeterDisconnectRequest request, RfnMeterReadAndControlDisconnectSimulatorSettings settings) {
        RfnMeterDisconnectConfirmationReply response = new RfnMeterDisconnectConfirmationReply();
        
        // Calculates Fail Rates for disconnect and connect
        if(replyWithFailure(settings.getDisconnectReply2FailPercent())) {
            response.setReplyType(settings.getDisconnectReply2());
        } else {
            response.setReplyType(RfnMeterDisconnectConfirmationReplyType.SUCCESS);
        }
        
        // Echo back of whatever user provides
        response.setState(RfnMeterDisconnectState.getForType(request.getAction()));
        
        return response;
    }
    
    // Returns true if reply should take user inputed failure, false otherwise
    private boolean replyWithFailure(int failureRate) {
        Random r = new Random();
        int n = r.nextInt(100) + 1; // Generates a random number between 1 and 100
        return n <= failureRate;    // If the number is over the fail rate, then the test succeeds
    }
    
    @Override
    public boolean isMeterReadReplyActive() {
        return meterReadReplyActive;
    }
    
    @Override
    public boolean isMeterDisconnectReplyActive() {
        return meterDisconnectReplyActive;
    }
    
    @Override
    public boolean isMeterReadReplyStopping() {
        return meterReadReplyStopping;
    }
    
    @Override
    public boolean isMeterDisconnectReplyStopping() {
        return meterDisconnectReplyStopping;
    }

    @Override
    public RfnDataSimulatorStatus getStatus() {
        return status;
    }
}
