package com.cannontech.common.rfn.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.AppMode;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.ConflictType;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.ConnectionType;
import com.cannontech.common.rfn.message.gateway.DataSequence;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.message.gateway.GatewayArchiveRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDataRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.LastCommStatus;
import com.cannontech.common.rfn.message.gateway.Radio;
import com.cannontech.common.rfn.message.gateway.RadioType;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequest;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAck;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAckType;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponse;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponseType;
import com.cannontech.common.rfn.message.gateway.SequenceBlock;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
import com.cannontech.common.rfn.model.SimulatedCertificateReplySettings;
import com.cannontech.common.rfn.model.SimulatedGatewayDataSettings;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnGatewaySimulatorService;
import com.google.common.collect.Sets;

//Switch info logs to debug
public class RfnGatewaySimulatorServiceImpl implements RfnGatewaySimulatorService {
    private static final Logger log = YukonLogManager.getLogger(RfnGatewaySimulatorServiceImpl.class);
    private static final String dataQueue = "yukon.qr.obj.common.rfn.GatewayDataRequest";
    private static final String upgradeQueue = "yukon.qr.obj.common.rfn.GatewayUpgradeRequest";
    private static final String dataAndUpgradeResponseQueue = "yukon.qr.obj.common.rfn.GatewayData";
    private static final String archiveRequestQueue = "yukon.qr.obj.common.rfn.GatewayArchiveRequest";
    
    private volatile boolean autoDataReplyActive = false;
    private volatile boolean autoDataReplyStopping = false;
    private volatile boolean autoUpgradeReplyActive = false;
    private volatile boolean autoUpgradeReplyStopping = false;
    
    @Autowired ConnectionFactory connectionFactory;
    private JmsTemplate jmsTemplate;
    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setReceiveTimeout(5000);
    }
    
    @Override
    public boolean startAutoDataReply(SimulatedGatewayDataSettings settings) {
        if (autoDataReplyActive) {
            return false;
        } else {
            Thread autoDataThread = getAutoDataRunnerThread(settings);
            autoDataThread.start();
            autoDataReplyActive = true;
            return true;
        }
    }

    @Override
    public void stopAutoDataReply() {
        autoDataReplyStopping = true;
    }

    @Override
    public boolean startAutoCertificateReply(SimulatedCertificateReplySettings settings) {
        if (autoUpgradeReplyActive) {
            return false;
        } else {
            Thread autoCertificateThread = getAutoCertificateThread(settings);
            autoCertificateThread.start();
            autoUpgradeReplyActive = true;
            return true;
        }
    }

    @Override
    public void stopAutoCertificateReply() {
        autoUpgradeReplyStopping = true;
    }

    @Override
    public void sendGatewayDataResponse(GatewayDataResponse response) {
        jmsTemplate.convertAndSend(dataAndUpgradeResponseQueue, response);
    }

    @Override
    public void sendGatewayArchiveRequest(String name, String serial, boolean isGateway2) {
        
        GatewayArchiveRequest request = new GatewayArchiveRequest();
        
        request.setName(name);
        
        String model = isGateway2 ? RfnDeviceCreationService.GATEWAY_2_MODEL_STRING : RfnDeviceCreationService.GATEWAY_1_MODEL_STRING;
        RfnIdentifier rfnIdentifier = new RfnIdentifier(serial, "CPS", model);
        request.setRfnIdentifier(rfnIdentifier);
        
        jmsTemplate.convertAndSend(archiveRequestQueue, request);
    }
    
    /**
     * Generate a thread that monitors the gateway certificate upgrade request queue, handles requests, 
     * and sends responses.
     */
    private Thread getAutoCertificateThread(SimulatedCertificateReplySettings settings) {
        Thread autoCertificateThread = new Thread() {
            @Override
            public void run() {
                log.info("Auto certificate update reply thread starting up.");
                while (!autoUpgradeReplyStopping) {
                    try {
                        Object message = jmsTemplate.receive(upgradeQueue);
                        if (message != null && message instanceof ObjectMessage) {
                            log.info("Processing certificate upgrade message");
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            RfnGatewayUpgradeRequest request = (RfnGatewayUpgradeRequest) requestMessage.getObject();
                            
                            RfnGatewayUpgradeRequestAck ack = setUpCertificateUpgradeAck(request, settings);
                            
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), ack);
                            
                            if (settings != null && 
                                    (settings.getAckType() == RfnGatewayUpgradeRequestAckType.ACCEPTED_FULLY ||
                                    settings.getAckType() == RfnGatewayUpgradeRequestAckType.ACCEPTED_PARTIALLY)) {
                                
                                List<RfnGatewayUpgradeResponse> responses = setUpCertificateUpgradeResponses(request, settings);
                                
                                for (RfnGatewayUpgradeResponse response : responses) {
                                    jmsTemplate.convertAndSend(dataAndUpgradeResponseQueue, response);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error occurred in auto data reply.", e);
                    }
                }
                
                log.info("Auto certificate update reply thread shutting down.");
                autoUpgradeReplyStopping = false;
                autoUpgradeReplyActive = false;
            }
            
            
        };
        
        return autoCertificateThread;
    }
    
    /**
     * Generate a thread that monitors the gateway data request queue, handles requests, and sends responses.
     */
    private Thread getAutoDataRunnerThread(SimulatedGatewayDataSettings settings) {
        Thread autoDataRunner = new Thread() {
            @Override
            public void run() {
                log.info("Auto data reply thread starting up.");
                while (!autoDataReplyStopping) {
                    try {
                        Object message = jmsTemplate.receive(dataQueue);
                        if (message != null && message instanceof ObjectMessage) {
                            log.info("Processing gateway data message");
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            GatewayDataRequest request = (GatewayDataRequest) requestMessage.getObject();
                            
                            GatewayDataResponse response = setUpDataResponse(request.getRfnIdentifier(), settings);
                            
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response);
                        }
                    } catch (Exception e) {
                        log.error("Error occurred in auto data reply.", e);
                    }
                }
                
                log.info("Auto data reply thread shutting down.");
                autoDataReplyStopping = false;
                autoDataReplyActive = false;
            }
            
            
        };
        return autoDataRunner;
    }
    
    /**
     * Builds an upgrade request acknowledgement message based on the specified request and settings. (This is the
     * initial response to the request, acknowledging that the request was received and giving an overall status.)
     */
    private RfnGatewayUpgradeRequestAck setUpCertificateUpgradeAck(RfnGatewayUpgradeRequest request, 
                                                                   SimulatedCertificateReplySettings settings) {
        
        RfnGatewayUpgradeRequestAck ack = new RfnGatewayUpgradeRequestAck();
        
        ack.setUpgradeId(request.getUpgradeId());
        
        if (settings != null && settings.getAckType() != null) {
            ack.setRequestAckType(settings.getAckType());
        } else {
            ack.setRequestAckType(RfnGatewayUpgradeRequestAckType.ACCEPTED_FULLY);
        }
        
        ack.setBeingUpgradedRfnIdentifiers(new HashSet<>());
        ack.setInvalidRfnIdentifiers(new HashMap<>());
        ack.setInvalidSuperAdminPasswordRfnIdentifiers(new HashMap<>());
        ack.setLastUpgradeInProcessRfnIdentifiers(new HashMap<>());
        if (settings != null && settings.getDeviceUpdateStatus() != null) {
            GatewayCertificateUpdateStatus deviceUpdateStatus = settings.getDeviceUpdateStatus();
            switch (deviceUpdateStatus) {
                case REQUEST_ACCEPTED:
                    ack.setBeingUpgradedRfnIdentifiers(request.getRfnIdentifiers());
                    break;
                case INVALID_RFN_ID:
                    Map<RfnIdentifier, String> idInvalids = new HashMap<>();
                    for (RfnIdentifier rfnId : request.getRfnIdentifiers()) {
                        idInvalids.put(rfnId, "");
                    }
                    ack.setInvalidRfnIdentifiers(idInvalids);
                    break;
                case INVALID_SUPER_ADMIN_PASSWORD:
                    Map<RfnIdentifier, String> passInvalids = new HashMap<>();
                    for (RfnIdentifier rfnId : request.getRfnIdentifiers()) {
                        passInvalids.put(rfnId, "");
                    }
                    ack.setInvalidSuperAdminPasswordRfnIdentifiers(passInvalids);
                    break;
                case ALREADY_IN_PROGRESS:
                    Map<RfnIdentifier, String> inProgressIds = new HashMap<>();
                    for (RfnIdentifier rfnId : request.getRfnIdentifiers()) {
                        inProgressIds.put(rfnId, "");
                    }
                    ack.setLastUpgradeInProcessRfnIdentifiers(inProgressIds);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported status: " + deviceUpdateStatus);
            }
            
        } else {
            ack.setBeingUpgradedRfnIdentifiers(request.getRfnIdentifiers());
        }
        
        ack.setRequestAckMessage("PLACEHOLDER MESSAGE");
        
        return ack;
    }
    
    /**
     * Builds upgrade response messages for rfnIdentifiers based on the specified request and settings. (This is the 
     * second response to the request, async on a separate queue, which gives the status of an individual gateway
     * certificate update).
     */
    private List<RfnGatewayUpgradeResponse> setUpCertificateUpgradeResponses(RfnGatewayUpgradeRequest request, 
                                                                             SimulatedCertificateReplySettings settings) {
        
        List<RfnGatewayUpgradeResponse> responses = new ArrayList<>();
        
        for (RfnIdentifier rfnIdentifier : request.getRfnIdentifiers()) {
            RfnGatewayUpgradeResponse response = new RfnGatewayUpgradeResponse();
            
            response.setUpgradeId(request.getUpgradeId());
            response.setRfnIdentifier(rfnIdentifier);
            response.setResponseType(RfnGatewayUpgradeResponseType.COMPLETED);
            //NM stuff we don't care about
            response.setGatewayMessageType((short) 1);
            response.setFragmentId(1);
            response.setGatewayMessageStatus((byte) 1);
            
            responses.add(response);
        }
        
        return responses;
    }
    
    private GatewayDataResponse setUpDataResponse(RfnIdentifier rfnId, SimulatedGatewayDataSettings settings) {
        GatewayDataResponse response = new GatewayDataResponse();
        
        if (settings != null && settings.isReturnGwy800Model()) {
            RfnIdentifier modifiedId = new RfnIdentifier(rfnId.getSensorSerialNumber(), 
                                                         rfnId.getSensorManufacturer(), 
                                                         RfnDeviceCreationService.GATEWAY_2_MODEL_STRING);
            response.setRfnIdentifier(modifiedId);
        } else {
            response.setRfnIdentifier(rfnId);
        }
        
        Authentication admin = new Authentication();
        admin.setUsername("admin");
        admin.setPassword("password");
        response.setAdmin(admin);
        
        Authentication superAdmin = new Authentication();
        superAdmin.setUsername("superAdmin");
        superAdmin.setPassword("superPassword");
        response.setSuperAdmin(superAdmin);
        
        response.setIpAddress("123.123.123.123");
        response.setPort("1234");
        response.setConnectionType(ConnectionType.TCP_IP);
        
        response.setConnectionStatus(ConnectionStatus.CONNECTED);
        response.setLastCommStatus(LastCommStatus.SUCCESSFUL);
        response.setLastCommStatusTimestamp(Instant.now().getMillis());
        
        response.setUpperStackVersion("1.0");
        response.setSoftwareVersion("2.0");
        response.setReleaseVersion("3.0");
        response.setRadioVersion("4.0");
        response.setHardwareVersion("5.0");
        response.setVersionConflicts(new HashSet<ConflictType>());
        
        response.setCollectionSchedule("0 0 * * * ?");
        response.setRouteColor((short) 1);
        response.setMode(AppMode.NORMAL);
        
        Set<Radio> radios = new HashSet<>();
        Radio radio = new Radio();
        radio.setType(RadioType.EKANET_915);
        radio.setTimestamp(Instant.now().getMillis());
        radio.setMacAddress("01:23:45:67:89:ab");
        radios.add(radio);
        response.setRadios(radios);
        
        Set<DataSequence> sequences = new HashSet<>();
        
        DataSequence gatewayLogsSequence = new DataSequence();
        gatewayLogsSequence.setType(DataType.GATEWAY_LOGS);
        gatewayLogsSequence.setCompletionPercentage(100);
        SequenceBlock gatewayLogsBlock = new SequenceBlock();
        gatewayLogsBlock.setStart(Instant.now().minus(Duration.standardDays(7)).getMillis());
        gatewayLogsBlock.setEnd(Instant.now().getMillis());
        gatewayLogsSequence.setBlocks(Sets.newHashSet(gatewayLogsBlock));
        sequences.add(gatewayLogsSequence);
        
        DataSequence generalDataSequence = new DataSequence();
        generalDataSequence.setType(DataType.GENERAL_PURPOSE_DATA);
        generalDataSequence.setCompletionPercentage(100);
        SequenceBlock generalDataBlock = new SequenceBlock();
        generalDataBlock.setStart(Instant.now().minus(Duration.standardDays(7)).getMillis());
        generalDataBlock.setEnd(Instant.now().getMillis());
        generalDataSequence.setBlocks(Sets.newHashSet(generalDataBlock));
        sequences.add(generalDataSequence);
        
        DataSequence nodeAlarmsSequence = new DataSequence();
        nodeAlarmsSequence.setType(DataType.NODE_ALARMS);
        nodeAlarmsSequence.setCompletionPercentage(100);
        SequenceBlock nodeAlarmsBlock = new SequenceBlock();
        nodeAlarmsBlock.setStart(Instant.now().minus(Duration.standardDays(7)).getMillis());
        nodeAlarmsBlock.setEnd(Instant.now().getMillis());
        nodeAlarmsSequence.setBlocks(Sets.newHashSet(nodeAlarmsBlock));
        sequences.add(nodeAlarmsSequence);
        
        DataSequence nodeDataSequence = new DataSequence();
        nodeDataSequence.setType(DataType.NODE_DATA);
        nodeDataSequence.setCompletionPercentage(100);
        SequenceBlock nodeDataBlock = new SequenceBlock();
        nodeDataBlock.setStart(Instant.now().minus(Duration.standardDays(7)).getMillis());
        nodeDataBlock.setEnd(Instant.now().getMillis());
        nodeDataSequence.setBlocks(Sets.newHashSet(nodeDataBlock));
        sequences.add(nodeDataSequence);
        
        DataSequence nodeLogsSequence = new DataSequence();
        nodeLogsSequence.setType(DataType.NODE_LOGS);
        nodeLogsSequence.setCompletionPercentage(100);
        SequenceBlock nodeLogsBlock = new SequenceBlock();
        nodeLogsBlock.setStart(Instant.now().minus(Duration.standardDays(7)).getMillis());
        nodeLogsBlock.setEnd(Instant.now().getMillis());
        nodeLogsSequence.setBlocks(Sets.newHashSet(nodeLogsBlock));
        sequences.add(nodeLogsSequence);
        
        DataSequence sensorDataSequence = new DataSequence();
        sensorDataSequence.setType(DataType.SENSOR_DATA);
        sensorDataSequence.setCompletionPercentage(100);
        SequenceBlock sensorDataBlock = new SequenceBlock();
        sensorDataBlock.setStart(Instant.now().minus(Duration.standardDays(7)).getMillis());
        sensorDataBlock.setEnd(Instant.now().getMillis());
        sensorDataSequence.setBlocks(Sets.newHashSet(sensorDataBlock));
        sequences.add(sensorDataSequence);
        
        response.setSequences(sequences);
        
        return response;
    }
    
    public boolean isAutoDataReplyActive() {
        return autoDataReplyActive;
    }
    
    public boolean isAutoUpgradeReplyActive() {
        return autoUpgradeReplyActive;
    }
}
