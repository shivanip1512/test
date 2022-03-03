package com.cannontech.common.rfn.simulation.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.GatewayArchiveRequest;
import com.cannontech.common.rfn.message.gateway.GatewayConfigResult;
import com.cannontech.common.rfn.message.gateway.GatewayCreateRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDataRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.GatewayDeleteRequest;
import com.cannontech.common.rfn.message.gateway.GatewayEditRequest;
import com.cannontech.common.rfn.message.gateway.GatewayFirmwareUpdateRequestResult;
import com.cannontech.common.rfn.message.gateway.GatewaySaveData;
import com.cannontech.common.rfn.message.gateway.GatewaySetConfigRequest;
import com.cannontech.common.rfn.message.gateway.GatewaySetConfigResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.message.gateway.RfnGatewayFirmwareUpdateRequest;
import com.cannontech.common.rfn.message.gateway.RfnGatewayFirmwareUpdateResponse;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequest;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAck;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAckType;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponse;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponseType;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionRequest;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResponse;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResult;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.rfn.simulation.SimulatedCertificateReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareVersionReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedGatewayDataSettings;
import com.cannontech.common.rfn.simulation.SimulatedUpdateReplySettings;
import com.cannontech.common.rfn.simulation.service.RfnGatewaySimulatorService;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApi;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.common.util.jms.api.JmsApiDirectoryHelper;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;

//Switch info logs to debug
public class RfnGatewaySimulatorServiceImpl implements RfnGatewaySimulatorService {
    private static final Logger log = YukonLogManager.getLogger(RfnGatewaySimulatorServiceImpl.class);
    
    private static Map<RfnIdentifier, GatewaySaveData> gatewayDataCache = new HashMap<>();
    
    private AtomicBoolean autoDataReplyActive = new AtomicBoolean(false);
    private AtomicBoolean autoDataReplyStopping = new AtomicBoolean(false);
    private SimulatedGatewayDataSettings gatewayDataSettings;

    private AtomicBoolean autoUpdateReplyActive = new AtomicBoolean(false);
    private AtomicBoolean autoUpdateReplyStopping = new AtomicBoolean(false);
    private SimulatedUpdateReplySettings updateReplySettings;

    private AtomicBoolean autoCertificateUpgradeReplyActive = new AtomicBoolean(false);
    private AtomicBoolean autoCertificateUpgradeReplyStopping = new AtomicBoolean(false);
    private SimulatedCertificateReplySettings certificateSettings;

    private AtomicBoolean autoFirmwareReplyActive = new AtomicBoolean(false);
    private AtomicBoolean autoFirmwareReplyStopping = new AtomicBoolean(false);
    private SimulatedFirmwareReplySettings firmwareSettings;

    private AtomicBoolean autoFirmwareVersionReplyActive = new AtomicBoolean(false);
    private AtomicBoolean autoFirmwareVersionReplyStopping = new AtomicBoolean(false);
    private SimulatedFirmwareVersionReplySettings firmwareVersionSettings;

    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private YukonJmsTemplate jmsTemplate;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    
    private YukonJmsTemplate rfGatewayDataUnsolicitedJmsTemplate;
    private YukonJmsTemplate rfGatewayArchiveJmsTemplate;
    private YukonJmsTemplate rfGatewayDeleteFromNmJmsTemplate;
    private YukonJmsTemplate rfGatewayFirmwareUpgradeJmsTemplate;
    private YukonJmsTemplate rfGatewayFirmwareUpgradeResponseJmsTemplate;
    private YukonJmsTemplate rfUpdateServerAvailableVersionJmsTemplate;
    private YukonJmsTemplate rfGatewayCertificateUpdateJmsTemplate;
    private YukonJmsTemplate rfGatewayUpdateJmsTemplate;
    private YukonJmsTemplate rfGatewaySetConfigJmsTemplate;
    private YukonJmsTemplate rfGatewayDataJmsTemplate;

    public static final Duration incomingMessageWait = Duration.standardSeconds(1);

    @PostConstruct
    public void init() {
        rfGatewayDataUnsolicitedJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RF_GATEWAY_DATA_UNSOLICITED);
        rfGatewayArchiveJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RF_GATEWAY_ARCHIVE);
        rfGatewayDeleteFromNmJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RF_GATEWAY_DELETE_FROM_NM);
        rfGatewayFirmwareUpgradeJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RF_GATEWAY_FIRMWARE_UPGRADE,
                incomingMessageWait);
        rfGatewayFirmwareUpgradeResponseJmsTemplate = jmsTemplateFactory
                .createResponseTemplate(JmsApiDirectory.RF_GATEWAY_FIRMWARE_UPGRADE);
        rfUpdateServerAvailableVersionJmsTemplate = jmsTemplateFactory
                .createTemplate(JmsApiDirectory.RF_UPDATE_SERVER_AVAILABLE_VERSION, incomingMessageWait);
        rfGatewayCertificateUpdateJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RF_GATEWAY_CERTIFICATE_UPDATE,
                incomingMessageWait);
        JmsApi<?, ?, ?> requestQueue = JmsApiDirectoryHelper.requireMatchingQueueNames(
                JmsApiDirectory.RF_GATEWAY_CREATE, JmsApiDirectory.RF_GATEWAY_EDIT, JmsApiDirectory.RF_GATEWAY_DELETE);
        rfGatewayUpdateJmsTemplate = jmsTemplateFactory.createTemplate(requestQueue, incomingMessageWait);
        rfGatewaySetConfigJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RF_GATEWAY_SET_CONFIG,
                incomingMessageWait);
        rfGatewayDataJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RF_GATEWAY_DATA, incomingMessageWait);
    }

    @Override
    public boolean startAutoDataReply(SimulatedGatewayDataSettings settings) {
        if (autoDataReplyActive.get()) {
            return false;
        } else {
            Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
            gateways.forEach(gateway -> {
                GatewayDataResponse response = setUpDataResponse(gateway.getRfnIdentifier(), settings);
                rfGatewayDataUnsolicitedJmsTemplate.convertAndSend(response);
            });
            Thread autoDataThread = getAutoDataRunnerThread(settings);
            autoDataThread.start();
            saveSettings(settings);
            gatewayDataSettings = settings;
            autoDataReplyActive.set(true);
            return true;
        }
    }

    @Override
    public void stopAutoDataReply() {
        if (autoDataReplyActive.get()) {
            autoDataReplyStopping.set(true);
            gatewayDataSettings = null;
            gatewayDataCache.clear();
        }
    }
    
    @Override
    public boolean startAutoUpdateReply(SimulatedUpdateReplySettings settings) {
        if (autoUpdateReplyActive.get()) {
            return false;
        } else {
            Thread autoUpdateThread = getAutoUpdateThread(settings);
            autoUpdateThread.start();
            saveSettings(settings);
            updateReplySettings = settings;
            autoUpdateReplyActive.set(true);;
            return true;
        }
    }
    
    @Override
    public void stopAutoUpdateReply() {
        if (autoUpdateReplyActive.get()) {
            autoUpdateReplyStopping.set(true);
            updateReplySettings = null;
        }
    }
    
    @Override
    public boolean startAutoCertificateReply(SimulatedCertificateReplySettings settings) {
        if (autoCertificateUpgradeReplyActive.get()) {
            return false;
        } else {
            Thread autoCertificateThread = getAutoCertificateThread(settings);
            autoCertificateThread.start();
            saveSettings(settings);
            certificateSettings = settings;
            autoCertificateUpgradeReplyActive.set(true);;
            return true;
        }
    }

    @Override
    public void stopAutoCertificateReply() {
        if (autoCertificateUpgradeReplyActive.get()) {
            autoCertificateUpgradeReplyStopping.set(true);
            certificateSettings = null;
        }
    }
    
    @Override
    public boolean startAutoFirmwareReply(SimulatedFirmwareReplySettings settings) {
        if (autoFirmwareReplyActive.get()) {
            return false;
        } else {
            Thread autoFirmwareThread = getAutoFirmwareThread(settings);
            autoFirmwareThread.start();
            saveSettings(settings);
            firmwareSettings = settings;
            autoFirmwareReplyActive.set(true);
            return true;
        }
    }
    
    @Override
    public void stopAutoFirmwareReply() {
        if (autoFirmwareReplyActive.get()) {
            autoFirmwareReplyStopping.set(true);
            firmwareSettings = null;
        }
    }
    
    @Override
    public boolean startAutoFirmwareVersionReply(SimulatedFirmwareVersionReplySettings settings) {
        if (autoFirmwareVersionReplyActive.get()) {
            return false;
        } else {
            Thread autoFirmwareVersionThread = getAutoFirmwareVersionThread(settings);
            autoFirmwareVersionThread.start();
            saveSettings(settings);
            firmwareVersionSettings = settings;
            autoFirmwareVersionReplyActive.set(true);
            return true;
        }
    }
    
    @Override
    public void stopAutoFirmwareVersionReply() {
        if (autoFirmwareVersionReplyActive.get()) {
            autoFirmwareVersionReplyStopping.set(true);
            firmwareVersionSettings = null;
        }
    }
    
    @Override
    public void sendGatewayDataResponse(String serial, String model, SimulatedGatewayDataSettings settings) {
        
        RfnIdentifier rfnIdentifier = createGatewayRfnIdentifier(serial, model);

        GatewayDataResponse response = setUpDataResponse(rfnIdentifier, settings);
        rfGatewayDataUnsolicitedJmsTemplate.convertAndSend(response);
    }

    @Override
    public void sendGatewayArchiveRequest(String serial, String model) {
        
        GatewayArchiveRequest request = new GatewayArchiveRequest();
        request.setRfnIdentifier(createGatewayRfnIdentifier(serial, model));
        rfGatewayArchiveJmsTemplate.convertAndSend(request);
    }

    /**
     * Return RfnIdentifier instance based on serial and model number.
     */
    private RfnIdentifier createGatewayRfnIdentifier(String serial, String model) {
        String manufacturer = RfnDeviceCreationService.GW_MANUFACTURER_EATON;
        return new RfnIdentifier(serial, manufacturer, model);
    }

    @Override
    public void sendGatewayDeleteRequest(String serial, String model) {

        GatewayDeleteRequest request = new GatewayDeleteRequest();
        RfnIdentifier rfnIdentifier = createGatewayRfnIdentifier(serial, model);
        request.setRfnIdentifier(rfnIdentifier);

        rfGatewayDeleteFromNmJmsTemplate.convertAndSend(request);
    }

    private Thread getAutoFirmwareThread(SimulatedFirmwareReplySettings settings) {
        Thread autoFirmwareThread = new Thread() {
            @Override
            public void run() {
                log.info("Auto firmware reply thread starting up.");
                while (!autoFirmwareReplyStopping.get()) {
                    try {
                        Object message = rfGatewayFirmwareUpgradeJmsTemplate.receive();
                        if (message != null && message instanceof ObjectMessage) {
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            RfnGatewayFirmwareUpdateRequest request = 
                                    (RfnGatewayFirmwareUpdateRequest) requestMessage.getObject();
                            
                            log.info("Sending firmware upgrade response for updateId: " + request.getUpdateId() +
                                     ", gateway: " + request.getGateway());
                            RfnGatewayFirmwareUpdateResponse response = setUpFirmwareUpdateResponse(request, settings);
                            rfGatewayFirmwareUpgradeResponseJmsTemplate.convertAndSend(response);
                        }
                    } catch (Exception e) {
                        log.error("Error occurred in auto firmware reply.", e);
                    }
                }
                
                log.info("Auto firmware reply thread shutting down.");
                autoFirmwareReplyStopping.set(false);;
                autoFirmwareReplyActive.set(false);
            }
        };
        
        return autoFirmwareThread;
    }
    
    /**
     * Generate a thread that monitors the firmware server available version request queue, handles requests, and sends
     * responses.
     */
    private Thread getAutoFirmwareVersionThread(SimulatedFirmwareVersionReplySettings settings) {
        Thread autoFirmwareVersionThread = new Thread() {
            @Override
            public void run() {
                log.info("Auto firmware server version reply thread starting up.");
                while (!autoFirmwareVersionReplyStopping.get()) {
                    try {
                        Object message = rfUpdateServerAvailableVersionJmsTemplate.receive();
                        if (message != null && message instanceof ObjectMessage) {
                            log.info("Processing firmware server available version message.");
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            RfnUpdateServerAvailableVersionRequest request = 
                                    (RfnUpdateServerAvailableVersionRequest) requestMessage.getObject();
                            
                            RfnUpdateServerAvailableVersionResponse response = 
                                    setUpFirmwareVersionResponse(request, settings);
                            
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response);
                        }
                    } catch (Exception e) {
                        log.error("Error occurred in auto firmware server version reply.", e);
                    }
                }
                
                log.info("Auto firmware server version reply thread shutting down.");
                autoFirmwareVersionReplyStopping.set(false);
                autoFirmwareVersionReplyActive.set(false);
            }
        };
        
        return autoFirmwareVersionThread;
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
                while (!autoCertificateUpgradeReplyStopping.get()) {
                    try {
                        Object message = rfGatewayCertificateUpdateJmsTemplate.receive();
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
                                    rfGatewayDataUnsolicitedJmsTemplate.convertAndSend(response);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error occurred in auto data reply.", e);
                    }
                }
                
                log.info("Auto certificate update reply thread shutting down.");
                autoCertificateUpgradeReplyStopping.set(false);
                autoCertificateUpgradeReplyActive.set(false);
            }
            
            
        };
        
        return autoCertificateThread;
    }
    
    /**
     * Generate a thread that monitors the gateway update request queue, handles requests, and sends responses.
     */
    private Thread getAutoUpdateThread(SimulatedUpdateReplySettings settings) {
        Thread autoUpdateRunner = new Thread() {
            @Override
            public void run() {
                log.info("Auto update reply thread starting up.");
                while (!autoUpdateReplyStopping.get()) {
                    try {
                        processGatewayUpdateMsg(settings);
                        processIpv6PrefixUpdateMsg(settings);
                    } catch (Exception e) {
                        log.error("Error occurred in update reply.", e);
                    }
                }
                
                log.info("Auto update reply thread shutting down.");
                autoUpdateReplyStopping.set(false);
                autoUpdateReplyActive.set(false);
            }
        };
        return autoUpdateRunner;
    }
    
    private void processGatewayUpdateMsg(SimulatedUpdateReplySettings settings) throws JMSException {
        Object message = rfGatewayUpdateJmsTemplate.receive();
        if (message != null && message instanceof ObjectMessage) {
            log.info("Processing gateway update message.");
            ObjectMessage requestMessage = (ObjectMessage) message;
            Serializable request = requestMessage.getObject();
            
            GatewayUpdateResponse response = setUpUpdateResponse(request, settings);

            if (requestMessage.getJMSReplyTo() != null) {
                jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response);
            } else if (request instanceof GatewayEditRequest) {
                // Update gateway message is also sent to sync gateway names, no reply is required.
                GatewayEditRequest editRequest = (GatewayEditRequest) request;
                log.info("Processing message to update name to "+ editRequest.getData().getName());
            }
        }
    }
    
    private void processIpv6PrefixUpdateMsg(SimulatedUpdateReplySettings settings) throws JMSException {
        Object message = rfGatewaySetConfigJmsTemplate.receive();
        if (message != null && message instanceof ObjectMessage) {
            log.info("Processing Ipv6 prefix update message.");
            ObjectMessage requestMessage = (ObjectMessage) message;
            
            GatewaySetConfigRequest request = (GatewaySetConfigRequest) requestMessage.getObject();
            
            if(settings.getIpv6PrefixUpdateResult() == GatewayConfigResult.SUCCESSFUL) {
                GatewayDataResponse gwResponse = setUpDataResponse(request.getRfnIdentifier(), getGatewayDataSettings());
                gwResponse.setIpv6Prefix(request.getIpv6Prefix());
                rfGatewayDataUnsolicitedJmsTemplate.convertAndSend(gwResponse);
            }
            
            GatewaySetConfigResponse response = new GatewaySetConfigResponse();
            response.setRfnIdentifier(request.getRfnIdentifier());
            response.setIpv6PrefixResult(settings.getIpv6PrefixUpdateResult());
            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response);
        }
    }
    
    /**
     * Generate a thread that monitors the gateway data request queue, handles requests, and sends responses.
     */
    private Thread getAutoDataRunnerThread(SimulatedGatewayDataSettings settings) {
        Thread autoDataRunner = new Thread() {
            @Override
            public void run() {
                log.info("Auto data reply thread starting up.");
                while (!autoDataReplyStopping.get()) {
                    try {
                        
                        Object message = rfGatewayDataJmsTemplate.receive();
                        if (message != null && message instanceof ObjectMessage) {
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
                autoDataReplyStopping.set(false);
                autoDataReplyActive.set(false);
            }
            
            
        };
        return autoDataRunner;
    }
    
    /**
     * Builds a firmware server available version response based on the request and settings.
     */
    private RfnUpdateServerAvailableVersionResponse setUpFirmwareVersionResponse(
            RfnUpdateServerAvailableVersionRequest request, SimulatedFirmwareVersionReplySettings settings) {
        
        RfnUpdateServerAvailableVersionResponse response = new RfnUpdateServerAvailableVersionResponse();
        response.setUpdateServerURL(request.getUpdateServerUrl());
        response.setResult(settings.getResult());
        response.setAvailableVersion(settings.getVersion());
        
        return response;
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
     * Builds firmware update response message for gateway rfnIdentifier based on the specified request.
     */
    private RfnGatewayFirmwareUpdateResponse setUpFirmwareUpdateResponse(RfnGatewayFirmwareUpdateRequest request,
                                                                          SimulatedFirmwareReplySettings settings) {
        
        RfnGatewayFirmwareUpdateResponse response = new RfnGatewayFirmwareUpdateResponse();
        response.setUpdateId(request.getUpdateId());
        response.setGateway(request.getGateway());
        if (settings != null) {
            response.setResult(settings.getResultType());
        } else {
            response.setResult(GatewayFirmwareUpdateRequestResult.ACCEPTED);
        }
        
        return response;
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
    
    /**
     * Builds an update response for a create, edit or delete request based on the specified settings. The data provided
     * in a create or edit request is cached for use in data replies, so the simulator "remembers" edited gateway data.
     */
    private GatewayUpdateResponse setUpUpdateResponse(Serializable request, SimulatedUpdateReplySettings settings) {
        GatewayUpdateResponse response = new GatewayUpdateResponse();
        if (request instanceof GatewayCreateRequest) {
            GatewayCreateRequest createRequest = (GatewayCreateRequest) request;
            RfnIdentifier rfnId = new RfnIdentifier(generateGatewaySerial(), RfnDeviceCreationService.GW_MANUFACTURER_EATON, RfnDeviceCreationService.GATEWAY_1_MODEL_STRING);
            // Cache the data so that it can be used to respond to data requests
            if (settings.getCreateResult() == GatewayUpdateResult.SUCCESSFUL) {
                cacheGatewayData(rfnId, createRequest.getData());
            }
            response.setRfnIdentifier(rfnId);
            response.setResult(settings.getCreateResult());
        } else if (request instanceof GatewayEditRequest) {
            GatewayEditRequest editRequest = (GatewayEditRequest) request;
            // Cache the data so that it can be used to respond to data requests
            if (settings.getEditResult() == GatewayUpdateResult.SUCCESSFUL) {
                cacheGatewayData(editRequest.getRfnIdentifier(), editRequest.getData());
            }
            response.setRfnIdentifier(editRequest.getRfnIdentifier());
            response.setResult(settings.getEditResult());
        } else if (request instanceof GatewayDeleteRequest) {
            GatewayDeleteRequest deleteRequest = (GatewayDeleteRequest) request;
            response.setRfnIdentifier(deleteRequest.getRfnIdentifier());
            response.setResult(settings.getDeleteResult());
        }
        return response;
    }
    
    /**
     * Builds a data response based on the specified rfn identifier and data settings. Any cached data from create or
     * edit requests will be used in this response. All data values not in the cache will be set to default values.
     */
    private GatewayDataResponse setUpDataResponse(RfnIdentifier rfnId, SimulatedGatewayDataSettings settings) {
        // If we are forcing this
        if (settings != null && settings.isReturnGwy800Model()) {
            rfnId = new RfnIdentifier(rfnId.getSensorSerialNumber(), 
                                      rfnId.getSensorManufacturer(), 
                                      RfnDeviceCreationService.GATEWAY_2_MODEL_STRING);
        }
        
        if (gatewayDataCache.get(rfnId) == null) {
            gatewayDataCache.put(rfnId, DefaultGatewaySimulatorData.getDefaultGatewayData());
        }

        GatewaySaveData cachedData = gatewayDataCache.get(rfnId);
        
        cachedData.setNmIpAddress(StringUtils.isEmpty(cachedData.getNmIpAddress()) ? "127.0.0.1" : cachedData.getNmIpAddress());
        cachedData.setNmPort(cachedData.getNmPort() == null ? RfnGatewayService.GATEWAY_DEFAULT_PORT : cachedData.getNmPort());
        cachedData.setPort(cachedData.getPort() == null ? RfnGatewayService.GATEWAY_DEFAULT_PORT : cachedData.getPort());
                                                                          
        GatewayDataResponse response = 
                DefaultGatewaySimulatorData.buildDataResponse(rfnId, cachedData, settings);

        
        
        return response;
    }
    
    /**
     * Randomly generates a serial between 7,000,000,000 and 8,000,000,000. Yes, this could cause collisions, but it's
     * easy, and the risk is fairly low.
     */
    private String generateGatewaySerial() {
        Random random = new Random();
        long serial = 7_000_000_000L + random.nextInt(1_000_000_000);
        return Long.toString(serial);
    }
    
    /**
     * Add the gateway data to the cache, to be put into data replies for this device.
     */
    private void cacheGatewayData(RfnIdentifier rfnId, GatewaySaveData newData) {
        synchronized (gatewayDataCache) {
            GatewaySaveData oldData = gatewayDataCache.get(rfnId);
            if (oldData != null) {
                if (newData.getAdmin() == null && oldData.getAdmin() != null) {
                    newData.setAdmin(oldData.getAdmin());
                }
                if (newData.getIpAddress() == null && oldData.getIpAddress() != null) {
                    newData.setIpAddress(oldData.getIpAddress());
                }
                if (newData.getSuperAdmin() == null && oldData.getSuperAdmin() != null) {
                    newData.setSuperAdmin(oldData.getSuperAdmin());
                }
                if (newData.getUpdateServerLogin() == null && oldData.getUpdateServerLogin() != null) {
                    newData.setUpdateServerLogin(oldData.getUpdateServerLogin());
                }
                if (newData.getUpdateServerUrl() == null && oldData.getUpdateServerUrl() != null) {
                    newData.setUpdateServerUrl(oldData.getUpdateServerUrl());
                }
                if (newData.getNmIpAddress() == null && oldData.getNmIpAddress()  != null) {
                    newData.setNmIpAddress(oldData.getNmIpAddress());
                }
                if (newData.getNmPort() == null && oldData.getNmPort() != null) {
                    newData.setNmPort(oldData.getNmPort());
                }
            }
            gatewayDataCache.put(rfnId, newData);
        }
    }
    
    @Override
    public boolean isAutoDataReplyActive() {
        return autoDataReplyActive.get();
    }
    
    @Override
    public boolean isAutoUpdateReplyActive() {
        return autoUpdateReplyActive.get();
    }
    
    @Override
    public boolean isAutoCertificateUpgradeReplyActive() {
        return autoCertificateUpgradeReplyActive.get();
    }
    
    @Override
    public boolean isAutoFirmwareReplyActive() {
        return autoFirmwareReplyActive.get();
    }
    
    @Override
    public boolean isAutoFirmwareVersionReplyActive() {
        return autoFirmwareVersionReplyActive.get();
    }
    
    @Override
    public boolean isAutoDataReplyStopping() {
        return autoDataReplyStopping.get();
    }
    
    @Override
    public boolean isAutoUpdateReplyStopping() {
        return autoUpdateReplyStopping.get();
    }
    
    @Override
    public boolean isAutoCertificateUpgradeReplyStopping() {
        return autoCertificateUpgradeReplyStopping.get();
    }
    
    @Override
    public boolean isAutoFirmwareReplyStopping() {
        return autoFirmwareReplyStopping.get();
    }
    
    @Override
    public boolean isAutoFirmwareVersionReplyStopping() {
        return autoFirmwareVersionReplyStopping.get();
    }
    
    public void saveSettings(SimulatedGatewayDataSettings settings) {
        log.debug("Saving RFN_GATEWAY data settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_DATA_STREAMING_LOADING, settings.getCurrentDataStreamingLoading());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_RETURN_GWY800_MODEL, settings.isReturnGwy800Model());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_NUM_NOT_READY_NODES, settings.getNumberOfNotReadyNodes());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_NUM_READY_NODES, settings.getNumberOfReadyNodes());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_FAILSAFE_MODE, settings.isFailsafeMode());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_CONNECTION_STATUS, settings.getConnectionStatus());
    }
    
    public void saveSettings(SimulatedUpdateReplySettings settings) {
        log.debug("Saving RFN_GATEWAY update settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_UPDATE_CREATE_RESULT, settings.getCreateResult());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_UPDATE_EDIT_RESULT, settings.getEditResult());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_UPDATE_DELETE_RESULT, settings.getDeleteResult());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_IPV6_PREFIX_UPDATE_RESULT, settings.getIpv6PrefixUpdateResult());
    }
    
    public void saveSettings(SimulatedCertificateReplySettings settings) {
        log.debug("Saving RFN_GATEWAY certificate settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_UPDATE_ACK_TYPE, settings.getAckType());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_UPDATE_STATUS_TYPE, settings.getDeviceUpdateStatus());
    }
    
    public void saveSettings(SimulatedFirmwareReplySettings settings) {
        log.debug("Saving RFN_GATEWAY firmware settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_FIRWARE_RESULT_TYPE, settings.getResultType());
    }
    
    public void saveSettings(SimulatedFirmwareVersionReplySettings settings) {
        log.debug("Saving RFN_GATEWAY firmware version settings to the YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_FIRWARE_REPLY_TYPE, settings.getResult());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_FIRWARE_VERSION, settings.getVersion());
    }
     
    @Override
    public SimulatedGatewayDataSettings getGatewayDataSettings() {
        if (gatewayDataSettings == null) {
            log.debug("Getting RFN_GATEWAY data settings from db.");
            SimulatedGatewayDataSettings settings = new SimulatedGatewayDataSettings();
            settings.setCurrentDataStreamingLoading(yukonSimulatorSettingsDao.getDoubleValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_DATA_STREAMING_LOADING));
            settings.setReturnGwy800Model(yukonSimulatorSettingsDao.getBooleanValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_RETURN_GWY800_MODEL));
            settings.setNumberOfNotReadyNodes(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_NUM_NOT_READY_NODES));
            settings.setNumberOfReadyNodes(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_NUM_READY_NODES));
            settings.setFailsafeMode(yukonSimulatorSettingsDao.getBooleanValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_FAILSAFE_MODE));
            settings.setConnectionStatus(ConnectionStatus.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_CONNECTION_STATUS)));
            gatewayDataSettings = settings;
        }
        return gatewayDataSettings;
    }
    
    @Override
    public SimulatedUpdateReplySettings getGatewayUpdateSettings() {
        if (updateReplySettings == null) {
            log.debug("Getting RFN_GATEWAY update settings from db.");
            SimulatedUpdateReplySettings settings = new SimulatedUpdateReplySettings();
            settings.setCreateResult(GatewayUpdateResult.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_UPDATE_CREATE_RESULT)));
            settings.setEditResult(GatewayUpdateResult.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_UPDATE_EDIT_RESULT)));
            settings.setDeleteResult(GatewayUpdateResult.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_UPDATE_DELETE_RESULT)));
            settings.setIpv6PrefixUpdateResult(GatewayConfigResult.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_IPV6_PREFIX_UPDATE_RESULT)));
            updateReplySettings = settings;
        }
        return updateReplySettings;
    }
    
    @Override
    public SimulatedCertificateReplySettings getCertificateSettings() {
        if (certificateSettings == null) {
            log.debug("Getting RFN_GATEWAY certificate settings from db.");
            SimulatedCertificateReplySettings settings = new SimulatedCertificateReplySettings();
            settings.setAckType(RfnGatewayUpgradeRequestAckType.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_UPDATE_ACK_TYPE)));
            settings.setDeviceUpdateStatus(GatewayCertificateUpdateStatus.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_UPDATE_STATUS_TYPE)));
            certificateSettings = settings;
        }
        return certificateSettings;
    }

    @Override
    public SimulatedFirmwareReplySettings getFirmwareSettings() {
        if (firmwareSettings == null) {
            log.debug("Getting RFN_GATEWAY firmware settings from db.");
            SimulatedFirmwareReplySettings settings = new SimulatedFirmwareReplySettings();
            settings.setResultType(GatewayFirmwareUpdateRequestResult.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_FIRWARE_RESULT_TYPE)));
            firmwareSettings = settings;
        }
        return firmwareSettings;
    }

    @Override
    public SimulatedFirmwareVersionReplySettings getFirmwareVersionSettings() {
        if (firmwareVersionSettings == null) {
            log.debug("Getting RFN_GATEWAY firmware version settings from db.");
            SimulatedFirmwareVersionReplySettings settings = new SimulatedFirmwareVersionReplySettings();
            settings.setResult(RfnUpdateServerAvailableVersionResult.valueOf(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_FIRWARE_REPLY_TYPE)));
            settings.setVersion(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.GATEWAY_SIMULATOR_FIRWARE_VERSION));
            firmwareVersionSettings = settings;
        }
        return firmwareVersionSettings;
    }

    @Override
    public void startSimulatorWithCurrentSettings() {        
        startAutoDataReply(getGatewayDataSettings());
        startAutoUpdateReply(getGatewayUpdateSettings());
        startAutoCertificateReply(getCertificateSettings());
        startAutoFirmwareReply(getFirmwareSettings());
        startAutoFirmwareVersionReply(getFirmwareVersionSettings());
    }
}
