package com.cannontech.common.rfn.simulation.service.impl;

import java.io.Serializable;
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
import com.cannontech.common.rfn.message.gateway.GatewayCreateRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDataRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.GatewayDeleteRequest;
import com.cannontech.common.rfn.message.gateway.GatewayEditRequest;
import com.cannontech.common.rfn.message.gateway.GatewayFirmwareUpdateRequestResult;
import com.cannontech.common.rfn.message.gateway.GatewaySaveData;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResponse;
import com.cannontech.common.rfn.message.gateway.LastCommStatus;
import com.cannontech.common.rfn.message.gateway.Radio;
import com.cannontech.common.rfn.message.gateway.RadioType;
import com.cannontech.common.rfn.message.gateway.RfnGatewayFirmwareUpdateRequest;
import com.cannontech.common.rfn.message.gateway.RfnGatewayFirmwareUpdateResponse;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequest;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAck;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAckType;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponse;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponseType;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionRequest;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResponse;
import com.cannontech.common.rfn.message.gateway.SequenceBlock;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.simulation.SimulatedCertificateReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareVersionReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedGatewayDataSettings;
import com.cannontech.common.rfn.simulation.SimulatedUpdateReplySettings;
import com.cannontech.common.rfn.simulation.service.RfnGatewaySimulatorService;
import com.cannontech.common.util.MethodNotImplementedException;
import com.google.common.collect.Sets;

//Switch info logs to debug
public class RfnGatewaySimulatorServiceImpl implements RfnGatewaySimulatorService {
    private static final Logger log = YukonLogManager.getLogger(RfnGatewaySimulatorServiceImpl.class);
    private static final String dataQueue = "yukon.qr.obj.common.rfn.GatewayDataRequest";
    private static final String gatewayUpdateQueue = "yukon.qr.obj.common.rfn.GatewayUpdateRequest";
    private static final String certificateUpgradeQueue = "yukon.qr.obj.common.rfn.GatewayUpgradeRequest";
    private static final String dataAndUpgradeResponseQueue = "yukon.qr.obj.common.rfn.GatewayData";
    private static final String archiveRequestQueue = "yukon.qr.obj.common.rfn.GatewayArchiveRequest";
    private static final String firmwareUpgradeRequestQueue = "yukon.qr.obj.common.rfn.RfnGatewayFirmwareUpdateRequest";
    private static final String firmwareUpgradeResponseQueue = "yukon.qr.obj.common.rfn.RfnGatewayFirmwareUpdateResponse";
    private static final String firmwareAvailableVersionQueue = "yukon.qr.obj.common.rfn.UpdateServerAvailableVersionRequest";
    
    private static final int incomingMessageWaitMillis = 1000;
    
    private static Map<RfnIdentifier, GatewaySaveData> gatewayDataCache = new HashMap<>();
    
    private volatile boolean autoDataReplyActive;
    private volatile boolean autoDataReplyStopping;
    private volatile SimulatedGatewayDataSettings gatewayDataSettings;
    
    private volatile boolean autoUpdateReplyActive;
    private volatile boolean autoUpdateReplyStopping;
    private volatile SimulatedUpdateReplySettings updateReplySettings;
    
    private volatile boolean autoCertificateUpgradeReplyActive;
    private volatile boolean autoCertificateUpgradeReplyStopping;
    private volatile SimulatedCertificateReplySettings certificateSettings;
    
    private volatile boolean autoFirmwareReplyActive;
    private volatile boolean autoFirmwareReplyStopping;
    private volatile SimulatedFirmwareReplySettings firmwareSettings;
    
    private volatile boolean autoFirmwareVersionReplyActive;
    private volatile boolean autoFirmwareVersionReplyStopping;
    private volatile SimulatedFirmwareVersionReplySettings firmwareVersionSettings;

    @Autowired ConnectionFactory connectionFactory;
    private JmsTemplate jmsTemplate;
    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setReceiveTimeout(incomingMessageWaitMillis);
    }
    
    @Override
    public boolean startAutoDataReply(SimulatedGatewayDataSettings settings) {
        if (autoDataReplyActive) {
            return false;
        } else {
            Thread autoDataThread = getAutoDataRunnerThread(settings);
            autoDataThread.start();
            gatewayDataSettings = settings;
            autoDataReplyActive = true;
            return true;
        }
    }

    @Override
    public void stopAutoDataReply() {
        autoDataReplyStopping = true;
        gatewayDataSettings = null;
    }
    
    @Override
    public boolean startAutoUpdateReply(SimulatedUpdateReplySettings settings) {
        if (autoUpdateReplyActive) {
            return false;
        } else {
            Thread autoUpdateThread = getAutoUpdateThread(settings);
            autoUpdateThread.start();
            updateReplySettings = settings;
            autoUpdateReplyActive = true;
            return true;
        }
    }
    
    @Override
    public void stopAutoUpdateReply() {
        autoUpdateReplyStopping = true;
        updateReplySettings = null;
    }
    
    @Override
    public boolean startAutoCertificateReply(SimulatedCertificateReplySettings settings) {
        if (autoCertificateUpgradeReplyActive) {
            return false;
        } else {
            Thread autoCertificateThread = getAutoCertificateThread(settings);
            autoCertificateThread.start();
            certificateSettings = settings;
            autoCertificateUpgradeReplyActive = true;
            return true;
        }
    }

    @Override
    public void stopAutoCertificateReply() {
        autoCertificateUpgradeReplyStopping = true;
        certificateSettings = null;
    }
    
    @Override
    public boolean startAutoFirmwareReply(SimulatedFirmwareReplySettings settings) {
        if (autoFirmwareReplyActive) {
            return false;
        } else {
            Thread autoFirmwareThread = getAutoFirmwareThread(settings);
            autoFirmwareThread.start();
            firmwareSettings = settings;
            autoFirmwareReplyActive = true;
            return true;
        }
    }
    
    @Override
    public void stopAutoFirmwareReply() {
        autoFirmwareReplyStopping = true;
        firmwareSettings = null;
    }
    
    @Override
    public boolean startAutoFirmwareVersionReply(SimulatedFirmwareVersionReplySettings settings) {
        if (autoFirmwareVersionReplyActive) {
            return false;
        } else {
            Thread autoFirmwareVersionThread = getAutoFirmwareVersionThread(settings);
            autoFirmwareVersionThread.start();
            firmwareVersionSettings = settings;
            autoFirmwareVersionReplyActive = true;
            return true;
        }
    }
    
    @Override
    public void stopAutoFirmwareVersionReply() {
        autoFirmwareVersionReplyStopping = true;
        firmwareVersionSettings = null;
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
    
    private Thread getAutoFirmwareThread(SimulatedFirmwareReplySettings settings) {
        Thread autoFirmwareThread = new Thread() {
            @Override
            public void run() {
                log.info("Auto firmware reply thread starting up.");
                while (!autoFirmwareReplyStopping) {
                    try {
                        Object message = jmsTemplate.receive(firmwareUpgradeRequestQueue);
                        if (message != null && message instanceof ObjectMessage) {
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            RfnGatewayFirmwareUpdateRequest request = 
                                    (RfnGatewayFirmwareUpdateRequest) requestMessage.getObject();
                            
                            log.info("Sending firmware upgrade response for updateId: " + request.getUpdateId() +
                                     ", gateway: " + request.getGateway());
                            RfnGatewayFirmwareUpdateResponse response = setUpFirmwareUpdateResponse(request, settings);
                            jmsTemplate.convertAndSend(firmwareUpgradeResponseQueue, response);
                        }
                    } catch (Exception e) {
                        log.error("Error occurred in auto firmware reply.", e);
                    }
                }
                
                log.info("Auto firmware update reply thread shutting down.");
                autoFirmwareReplyStopping = false;
                autoFirmwareReplyActive = false;
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
                while (!autoFirmwareVersionReplyStopping) {
                    try {
                        Object message = jmsTemplate.receive(firmwareAvailableVersionQueue);
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
                
                log.info("Auto firmware server available version reply thread shutting down.");
                autoFirmwareVersionReplyStopping = false;
                autoFirmwareVersionReplyActive = false;
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
                while (!autoCertificateUpgradeReplyStopping) {
                    try {
                        Object message = jmsTemplate.receive(certificateUpgradeQueue);
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
                autoCertificateUpgradeReplyStopping = false;
                autoCertificateUpgradeReplyActive = false;
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
                log.info("Auto gateway update reply thread starting up.");
                while (!autoUpdateReplyStopping) {
                    try {
                        Object message = jmsTemplate.receive(gatewayUpdateQueue);
                        if (message != null && message instanceof ObjectMessage) {
                            log.info("Processing gateway update message.");
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            Serializable request = requestMessage.getObject();
                            
                            GatewayUpdateResponse response = setUpUpdateResponse(request, settings);
                            
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), response);
                        }
                    } catch (Exception e) {
                        log.error("Error occurred in update reply.", e);
                    }
                }
                
                log.info("Auto update reply thread shutting down.");
                autoUpdateReplyStopping = false;
                autoUpdateReplyActive = false;
            }
        };
        return autoUpdateRunner;
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
    
    private GatewayUpdateResponse setUpUpdateResponse(Serializable request, SimulatedUpdateReplySettings settings) {
        GatewayUpdateResponse response = new GatewayUpdateResponse();
        if (request instanceof GatewayCreateRequest) {
            //TODO handle gateway create
            
            //GatewayCreateRequest createRequest = (GatewayCreateRequest) request;
            //GatewayUpdateResponse response = new GatewayUpdateResponse();
            //response.setRfnIdentifier(?);
            throw new MethodNotImplementedException("Simulator currently does not handle gateway create messages (yet)!");
        } else if (request instanceof GatewayEditRequest) {
            GatewayEditRequest editRequest = (GatewayEditRequest) request;
            // Cache the data so that it can be used to respond to data requests
            cacheGatewayData(editRequest);
            
            response.setRfnIdentifier(editRequest.getRfnIdentifier());
            response.setResult(settings.getEditResult());
        } else if (request instanceof GatewayDeleteRequest) {
            GatewayDeleteRequest deleteRequest = (GatewayDeleteRequest) request;
            response.setRfnIdentifier(deleteRequest.getRfnIdentifier());
            response.setResult(settings.getDeleteResult());
        }
        return response;
    }
    
    private void cacheGatewayData(GatewayEditRequest request) {
        RfnIdentifier id = request.getRfnIdentifier();
        GatewaySaveData newData = request.getData();
        
        synchronized (gatewayDataCache) {
            GatewaySaveData oldData = gatewayDataCache.get(id);
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
            }
            gatewayDataCache.put(id, newData);
        }
    }
    
    private GatewayDataResponse setUpDataResponse(RfnIdentifier rfnId, SimulatedGatewayDataSettings settings) {
        GatewayDataResponse response = new GatewayDataResponse();
        
        RfnIdentifier returnedRfnId = rfnId;
        if (settings != null && settings.isReturnGwy800Model()) {
            returnedRfnId = new RfnIdentifier(rfnId.getSensorSerialNumber(), 
                                                         rfnId.getSensorManufacturer(), 
                                                         RfnDeviceCreationService.GATEWAY_2_MODEL_STRING);
        }
        response.setRfnIdentifier(returnedRfnId);
        
        // Check the cache - if any edits have been made to the data for this gateway, return the edited data instead
        // of the default values.
        boolean hasCachedData = gatewayDataCache.containsKey(returnedRfnId);
             
        GatewaySaveData cachedData = gatewayDataCache.get(returnedRfnId);
        if (hasCachedData && cachedData.getAdmin() != null) {
            response.setAdmin(cachedData.getAdmin());
        } else {
            Authentication admin = new Authentication();
            admin.setUsername("admin");
            admin.setPassword("password");
            response.setAdmin(admin);
        }
        if (hasCachedData && cachedData.getAdmin() != null) {
            response.setIpAddress(cachedData.getIpAddress());
        } else {
            response.setIpAddress("123.123.123.123");
        }
        if (hasCachedData && cachedData.getSuperAdmin() != null) {
            response.setSuperAdmin(cachedData.getSuperAdmin());
        } else {
            Authentication superAdmin = new Authentication();
            superAdmin.setUsername("superAdmin");
            superAdmin.setPassword("superPassword");
            response.setSuperAdmin(superAdmin);
        }
        if (hasCachedData && cachedData.getUpdateServerLogin() != null) {
            response.setUpdateServerLogin(cachedData.getUpdateServerLogin());
        } else {
            Authentication updateServerAdmin = new Authentication();
            updateServerAdmin.setUsername("updateAdmin");
            updateServerAdmin.setPassword("updatePassword");
            response.setUpdateServerLogin(updateServerAdmin);
        }
        if (hasCachedData && cachedData.getUpdateServerUrl() != null) {
            response.setUpdateServerUrl(cachedData.getUpdateServerUrl());
        } else {
            response.setUpdateServerUrl("http://127.0.0.1:8081/simulatedUpdateServer/latest/");
        }
        
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
        response.setRouteColor((short) 123);
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
    
    @Override
    public boolean isAutoDataReplyActive() {
        return autoDataReplyActive;
    }
    
    @Override
    public boolean isAutoUpdateReplyActive() {
        return autoUpdateReplyActive;
    }
    
    @Override
    public boolean isAutoCertificateUpgradeReplyActive() {
        return autoCertificateUpgradeReplyActive;
    }
    
    @Override
    public boolean isAutoFirmwareReplyActive() {
        return autoFirmwareReplyActive;
    }
    
    @Override
    public boolean isAutoFirmwareVersionReplyActive() {
        return autoFirmwareVersionReplyActive;
    }
    
    @Override
    public SimulatedGatewayDataSettings getGatewayDataSettings() {
        return gatewayDataSettings;
    }
    
    @Override
    public SimulatedUpdateReplySettings getGatewayUpdateSettings() {
        return updateReplySettings;
    }
    
    @Override
    public SimulatedCertificateReplySettings getCertificateSettings() {
        return certificateSettings;
    }

    @Override
    public SimulatedFirmwareReplySettings getFirmwareSettings() {
        return firmwareSettings;
    }

    @Override
    public SimulatedFirmwareVersionReplySettings getFirmwareVersionSettings() {
        return firmwareVersionSettings;
    }
    
    @Override
    public int getNumberOfSimulatorsRunning() {
        int simulatorsRunning = 0;
        
        if (isAutoDataReplyActive()) {
            simulatorsRunning++;
        }
        if (isAutoUpdateReplyActive()) {
            simulatorsRunning++;
        }
        if (isAutoCertificateUpgradeReplyActive()) {
            simulatorsRunning++;
        }
        if (isAutoFirmwareReplyActive()) {
            simulatorsRunning++;
        }
        if (isAutoFirmwareVersionReplyActive()) {
            simulatorsRunning++;
        }
        
        return simulatorsRunning;
    }
}
