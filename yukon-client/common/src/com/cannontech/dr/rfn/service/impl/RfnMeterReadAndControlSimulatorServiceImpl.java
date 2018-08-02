package com.cannontech.dr.rfn.service.impl;

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReply;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectRequest;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.read.RfnMeterReadDataReply;
import com.cannontech.amr.rfn.message.read.RfnMeterReadReply;
import com.cannontech.amr.rfn.message.read.RfnMeterReadRequest;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlDisconnectSimulatorSettings;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlReadSimulatorSettings;
import com.cannontech.dr.rfn.service.RfnMeterDataSimulatorService;
import com.cannontech.dr.rfn.service.RfnMeterReadAndControlSimulatorService;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;

public class RfnMeterReadAndControlSimulatorServiceImpl implements RfnMeterReadAndControlSimulatorService {

    private static final Logger log = YukonLogManager.getLogger(RfnMeterReadAndControlSimulatorServiceImpl.class);
    private static final String meterReadingArchiveRequestQueue = "yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest";
    private static final String meterReadRequestQueue = "yukon.qr.obj.amr.rfn.MeterReadRequest";
    private static final String meterDisconnectRequestQueue = "yukon.qr.obj.amr.rfn.MeterDisconnectRequest";
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private RfnDeviceDao dao;
    @Autowired private RfnMeterDataSimulatorService rfnMeterDataSimulatorService;
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
    
    @Override
    public boolean startMeterReadReply(RfnMeterReadAndControlReadSimulatorSettings settings) {
        if (meterReadReplyActive) {
            return false;
        } else {
            
            saveReadSettings(settings);
            readSettings = settings;
            
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
            disconnectSettings = settings;
            
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
        log.debug("Saving RFN_METER_READ_CONTROL settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_REPLY1, settings.getDisconnectReply1());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_REPLY2, settings.getDisconnectReply2());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_FAIL_RATE_1, settings.getDisconnectReply1FailPercent());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_CONTROL_SIMULATOR_DISCONNECT_FAIL_RATE_2, settings.getDisconnectReply2FailPercent());
    }
    
    @Override
    public RfnMeterReadAndControlDisconnectSimulatorSettings getDisconnectSettings() {
        if (disconnectSettings == null) {
            log.debug("Getting RFN_METER_READ_CONTROL SimulatorSettings from db.");
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
        log.debug("Saving RFN_METER_READ_CONTROL settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_REPLY1, settings.getReadReply1());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_REPLY2, settings.getReadReply2());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_1, settings.getReadReply1FailPercent());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_2, settings.getReadReply2FailPercent());
    }
    
    @Override
    public RfnMeterReadAndControlReadSimulatorSettings getReadSettings() {
        if (readSettings == null) {
            log.debug("Getting RFN_METER_READ_CONTROL SimulatorSettings from db.");
            RfnMeterReadAndControlReadSimulatorSettings simulatorSettings = new RfnMeterReadAndControlReadSimulatorSettings();
            simulatorSettings.setReadReply1(RfnMeterReadingReplyType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_REPLY1)));
            simulatorSettings.setReadReply1FailPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_1));
            simulatorSettings.setReadReply2(RfnMeterReadingDataReplyType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_REPLY2)));
            simulatorSettings.setReadReply2FailPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_READ_SIMULATOR_READ_FAIL_RATE_2));
            readSettings = simulatorSettings;
        }
        return readSettings;
    }
    
    @Override
    public void startSimulatorWithCurrentSettings() {
        startMeterDisconnectReply(getDisconnectSettings());
        startMeterReadReply(getReadSettings());
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
                            RfnMeterReadRequest request = (RfnMeterReadRequest) requestMessage.getObject();
                            
                            RfnMeterReadReply response1 = setUpReadInitialResponse(settings);
                            RfnMeterReadDataReply response2 = setUpReadDataResponse(request, settings);
                            
                            // Sends the responses to Meter Read Queue
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response1);
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response2);
                            
                            // Sends response2 to the Meter Archive Request Queue
                            RfnMeterReadingArchiveRequest archiveRequest = new RfnMeterReadingArchiveRequest();
                            archiveRequest.setReadingType(RfnMeterReadingType.INTERVAL);
                            archiveRequest.setData(response2.getData());
                            archiveRequest.setDataPointId(1);
                            jmsTemplate.convertAndSend(meterReadingArchiveRequestQueue, archiveRequest);
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
    
    private RfnMeterReadReply setUpReadInitialResponse(RfnMeterReadAndControlReadSimulatorSettings settings) {
        RfnMeterReadReply response = new RfnMeterReadReply();
        
        // Calculates Fail Rates for read
        if(replyWithFailure(settings.getReadReply1FailPercent())) {
            response.setReplyType(settings.getReadReply1());
        } else {
            response.setReplyType(RfnMeterReadingReplyType.OK);
        }
        
        return response;
    }
    
    private RfnMeterReadDataReply setUpReadDataResponse(RfnMeterReadRequest request, RfnMeterReadAndControlReadSimulatorSettings settings) {
        RfnMeterReadDataReply response = new RfnMeterReadDataReply();
        
        // Calculates Fail Rates for read
        if(replyWithFailure(settings.getReadReply2FailPercent())) {
            response.setReplyType(settings.getReadReply2());
        } else {
            response.setReplyType(RfnMeterReadingDataReplyType.OK);
            RfnDevice device = dao.getDeviceForExactIdentifier(request.getRfnIdentifier());
            response.setData(rfnMeterDataSimulatorService.createReadingForType(device, DateTime.now(), RfnMeterReadingType.INTERVAL, DateTime.now()));
        }
        
        return response;
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
    
    /**
     * This method is used to calculate the chance of a request failing with the inputed failure message instead of succeeding.
     * @param failureRate Integer that is the inputed failure rate.
     * @return True if the reply should take the user inputed failure, and false otherwise.
     */
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
}
