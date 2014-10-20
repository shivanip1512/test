package com.cannontech.common.rfn.service.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.GatewayActionResponse;
import com.cannontech.common.rfn.message.gateway.GatewayActionResult;
import com.cannontech.common.rfn.message.gateway.GatewayConnectRequest;
import com.cannontech.common.rfn.message.gateway.GatewayConnectionTestRequest;
import com.cannontech.common.rfn.message.gateway.GatewayConnectionTestResponse;
import com.cannontech.common.rfn.message.gateway.GatewayConnectionTestResult;
import com.cannontech.common.rfn.message.gateway.GatewayCreateRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDataRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDeleteRequest;
import com.cannontech.common.rfn.message.gateway.GatewayEditRequest;
import com.cannontech.common.rfn.message.gateway.GatewaySaveData;
import com.cannontech.common.rfn.message.gateway.GatewayScheduleDeleteRequest;
import com.cannontech.common.rfn.message.gateway.GatewayScheduleRequest;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.yukon.IDatabaseCache;

public class RfnGatewayServiceImpl implements RfnGatewayService {

    private static final Logger log = YukonLogManager.getLogger(RfnGatewayServiceImpl.class);
    
    private static final String gatewayUpdateRequestCparm = "RFN_GATEWAY_UPDATE_REQUEST";
    private static final String gatewayActionRequestCparm = "RFN_GATEWAY_ACTION_REQUEST";
    
    private static final String gatewayUpdateRequestQueue = "yukon.qr.obj.common.rfn.GatewayUpdateRequest";
    private static final String gatewayActionRequestQueue = "yukon.qr.obj.common.rfn.GatewayActionRequest";
    
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private IDatabaseCache cache;
    
    private RfnGatewayDataCache dataCache;
    private ConnectionFactory connectionFactory;
    private ConfigurationSource configurationSource;
    private RfnDeviceCreationService rfnDeviceCreationService;
    private PaoLocationDao paoLocationDao;
    
    //Created in post-contruct
    private RequestReplyTemplate<GatewayUpdateResponse> updateRequestTemplate;
    private RequestReplyTemplate<GatewayActionResponse> actionRequestTemplate;
    private RequestReplyTemplate<GatewayConnectionTestResponse> connectionTestRequestTemplate;
    
    @Autowired
    public RfnGatewayServiceImpl(RfnGatewayDataCache dataCache, 
                                 ConnectionFactory connectionFactory, 
                                 ConfigurationSource configurationSource, 
                                 RfnDeviceCreationService rfnDeviceCreationService, 
                                 PaoLocationDao paoLocationDao) {
        
        this.dataCache = dataCache;
        this.connectionFactory = connectionFactory;
        this.configurationSource = configurationSource;
        this.rfnDeviceCreationService = rfnDeviceCreationService;
        this.paoLocationDao = paoLocationDao;
    }
    
    @PostConstruct
    public void init() {
        updateRequestTemplate = new RequestReplyTemplateImpl<GatewayUpdateResponse>(gatewayUpdateRequestCparm, 
                configurationSource, connectionFactory, gatewayUpdateRequestQueue, false);
        actionRequestTemplate = new RequestReplyTemplateImpl<GatewayActionResponse>(gatewayActionRequestCparm,
                configurationSource, connectionFactory, gatewayActionRequestQueue, false);
        connectionTestRequestTemplate = 
                new RequestReplyTemplateImpl<GatewayConnectionTestResponse>(gatewayActionRequestCparm, 
                configurationSource, connectionFactory, gatewayActionRequestQueue, false);
    }
    
    @Override
    public Set<RfnGateway> getAllGateways() {
        Set<RfnGateway> rfnGateways = new HashSet<RfnGateway>();
        // Get all base RfnDevices
        List<RfnDevice> gatewayDevices = rfnDeviceDao.getDevicesByPaoType(PaoType.RFN_GATEWAY);
        for (RfnDevice gatewayDevice : gatewayDevices) {
            // Get PAO name
            String name = cache.getAllPaosMap().get(gatewayDevice.getPaoIdentifier().getPaoId()).getPaoName();
            // Get available RfnGatewayData from cache via non-blocking call
            RfnGatewayData gatewayData = dataCache.getIfPresent(gatewayDevice.getPaoIdentifier());
            RfnGateway rfnGateway = new RfnGateway(name, gatewayDevice.getPaoIdentifier(), 
                                                   gatewayDevice.getRfnIdentifier(), gatewayData);
            // Get PaoLocation from PaoLocationDao
            PaoLocation gatewayLocation = paoLocationDao.getLocation(gatewayDevice.getPaoIdentifier().getPaoId());
            if (gatewayLocation != null) {
                rfnGateway.setLocation(gatewayLocation);
            }
            rfnGateways.add(rfnGateway);
        }
        return rfnGateways;
    }
    
    @Override
    public RfnGateway getGatewayByPaoId(PaoIdentifier paoIdentifier)
            throws NetworkManagerCommunicationException {
        // Get base RfnDevice
        RfnDevice gwDevice = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        // Get PAO name
        String name = cache.getAllPaosMap().get(gwDevice.getPaoIdentifier().getPaoId()).getPaoName();
        // Get RfnGatewayData from cache
        RfnGatewayData gatewayData = dataCache.get(paoIdentifier);
        RfnGateway rfnGateway = new RfnGateway(name, gwDevice.getPaoIdentifier(),
                                               gwDevice.getRfnIdentifier(),
                                               gatewayData);
        // Get PaoLocation from PaoLocationDao
        PaoLocation gatewayLoc =
            paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId());
        if (gatewayLoc != null) {
            rfnGateway.setLocation(gatewayLoc);
        }
        return rfnGateway;
    }
    
    @Override
    public PaoIdentifier createGateway(GatewaySettings settings) 
            throws NetworkManagerCommunicationException, GatewayUpdateException {
        
        // Send the request
        GatewayCreateRequest request = buildGatewayCreateRequest(settings);
        log.debug("Attempting to create a new gateway: " + request);
        BlockingJmsReplyHandler<GatewayUpdateResponse> replyHandler = new BlockingJmsReplyHandler<>(GatewayUpdateResponse.class);
        updateRequestTemplate.send(request, replyHandler);
        
        // Wait for the response
        GatewayUpdateResponse response;
        try {
            response = replyHandler.waitForCompletion();
            log.debug("Gateway creation response: " + response);
        } catch (ExecutionException e) {
            throw new NetworkManagerCommunicationException("Gateway creation failed due to a communication error " +
                    "with Network Manager.", e);
        }
        
        // Parse the response
        if (response.getResult() == GatewayUpdateResult.SUCCESSFUL) {
            // Create the device in Yukon DB
            RfnDevice gateway = rfnDeviceCreationService.createGateway(settings.getName(), response.getRfnIdentifier());
            PaoIdentifier gatewayIdentifier = gateway.getPaoIdentifier();
            // Force the data cache to update
            dataCache.get(gatewayIdentifier);
            
            // Add location data
            Double latitude = settings.getLatitude();
            Double longitude = settings.getLongitude();
            if (latitude != null && longitude != null) {
                PaoLocation location = new PaoLocation();
                location.setPaoIdentifier(gatewayIdentifier);
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                paoLocationDao.save(location);
            }
            
            return gatewayIdentifier;
        }
        throw new GatewayUpdateException("Gateway creation failed");
    }
    
    private GatewayCreateRequest buildGatewayCreateRequest(GatewaySettings settings) {
        GatewayCreateRequest request = new GatewayCreateRequest();
        GatewaySaveData data = new GatewaySaveData();
        data.setIpAddress(settings.getIpAddress());
        data.setUser(settings.getUser());
        data.setAdmin(settings.getAdmin());
        data.setSuperAdmin(settings.getSuperAdmin());
        request.setData(data);
        return request;
    }
    
    @Override
    public boolean updateGateway(RfnGateway gateway) throws NetworkManagerCommunicationException {
        // Determine if change is local Yukon DB change (i.e. name) or remote Network Manager change.
        PaoIdentifier paoIdentifier = gateway.getPaoIdentifier();
        RfnGateway existingGateway = getGatewayByPaoId(paoIdentifier);
        RfnGatewayData existingGatewayData = existingGateway.getData();
        RfnGatewayData newGatewayData = gateway.getData();
        GatewaySaveData editData = new GatewaySaveData();
        boolean sendGatewayEditRequest = false;
        if (newGatewayData.getIpAddress() != null && !newGatewayData.getIpAddress().equals(existingGatewayData.getIpAddress())) {
            editData.setIpAddress(newGatewayData.getIpAddress());
            sendGatewayEditRequest = true;
        }
        if (newGatewayData.getAdmin() != null && !newGatewayData.getAdmin().equals(existingGatewayData.getAdmin())) {
            editData.setAdmin(newGatewayData.getAdmin());
            sendGatewayEditRequest = true;
        }
        if (newGatewayData.getSuperAdmin() != null && !newGatewayData.getSuperAdmin().equals(existingGatewayData.getSuperAdmin())) {
            editData.setSuperAdmin(newGatewayData.getSuperAdmin());
            sendGatewayEditRequest = true;
        }
        
        // If necessary, send GatewayEditRequest
        if(sendGatewayEditRequest) {
            GatewayEditRequest request = new GatewayEditRequest();
            request.setRfnIdentifier(existingGateway.getRfnIdentifier());
            request.setData(editData);
            
            log.debug("Sending gateway edit request: " + request);
            BlockingJmsReplyHandler<GatewayUpdateResponse> replyHandler = new BlockingJmsReplyHandler<>(GatewayUpdateResponse.class);
            updateRequestTemplate.send(request, replyHandler);
            
            // Wait for the response
            GatewayUpdateResponse response;
            try {
                response = replyHandler.waitForCompletion();
                log.debug("Gateway edit response: " + response);
            } catch (ExecutionException e) {
                throw new NetworkManagerCommunicationException("Gateway edit failed due to a communication error " +
                        "with Network Manager.", e);
            }
            
            // Parse GatewayUpdateResponse
            if (response.getResult() == GatewayUpdateResult.SUCCESSFUL) {
                // Force the data cache to update
                dataCache.remove(paoIdentifier);
                // Note: for lazy loading, comment out this line.
                dataCache.get(paoIdentifier);
            } else {
                log.info("Edit gateway " + paoIdentifier + " result: " + response.getResult());
                return false;
            }
        }
        
        // Update yukon database
        if (gateway.getName() != null && !gateway.getName().equals(existingGateway.getName())) {
            deviceDao.changeName(existingGateway, gateway.getName());
        }
        if (gateway.getLocation() != null && !gateway.getLocation().equals(existingGateway.getLocation())) {
            paoLocationDao.save(gateway.getLocation());
        }
        return true;
    }

    @Override
    public boolean deleteGateway(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException {
        RfnDevice gateway = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        
        GatewayDeleteRequest request = new GatewayDeleteRequest();
        request.setRfnIdentifier(gateway.getRfnIdentifier());

        BlockingJmsReplyHandler<GatewayUpdateResponse> replyHandler =
            new BlockingJmsReplyHandler<>(GatewayUpdateResponse.class);
        updateRequestTemplate.send(request, replyHandler);
        
        // Parse GatewayUpdateResponse on temp queue
        try {
            GatewayUpdateResponse response = replyHandler.waitForCompletion();
            if (response.getResult() == GatewayUpdateResult.SUCCESSFUL) {
                // Delete from yukon database, cache
                deviceDao.removeDevice(gateway);
                dataCache.remove(paoIdentifier);
                return true;
            } else {
                log.info("Delete gateway " + paoIdentifier + " result: " + response.getResult());
                return false;
            }
        } catch (ExecutionException e) {
            throw new NetworkManagerCommunicationException("Gateway delete failed due to a communication error with " +
                    "Network Manager.", e);
        }
    }
    
    @Override
    public boolean testConnection(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException {
        RfnDevice gateway = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        RfnGatewayData gatewayData = dataCache.get(paoIdentifier);
        
        //Build request
        GatewayConnectionTestRequest request = new GatewayConnectionTestRequest();
        request.setRfnIdentifier(gateway.getRfnIdentifier());
        request.setIpAddress(gatewayData.getIpAddress());
        request.setAdmin(gatewayData.getAdmin());
        request.setSuperAdmin(gatewayData.getSuperAdmin());
        
        return sendConnectionRequest(request);
    }
    
    @Override
    public boolean testConnection(String ipAddress, Authentication user, Authentication admin, 
                                  Authentication superAdmin) throws NetworkManagerCommunicationException {
        //Build request
        GatewayConnectionTestRequest request = new GatewayConnectionTestRequest();
        request.setRfnIdentifier(null); //request is not for an existing gateway
        request.setIpAddress(ipAddress);
        request.setAdmin(admin);
        request.setUser(user);
        request.setSuperAdmin(superAdmin);
        
        return sendConnectionRequest(request);
    }
    
    private boolean sendConnectionRequest(GatewayConnectionTestRequest request) throws NetworkManagerCommunicationException {
        BlockingJmsReplyHandler<GatewayConnectionTestResponse> replyHandler = 
                new BlockingJmsReplyHandler<>(GatewayConnectionTestResponse.class);
                
        //Send request
        log.debug("Sending connection request: " + request);
        connectionTestRequestTemplate.send(request, replyHandler);
        
        //Parse response
        try {
            GatewayConnectionTestResponse response = replyHandler.waitForCompletion();
            return response.getResult() == GatewayConnectionTestResult.SUCCESSFUL;
        } catch (ExecutionException e) {
            throw new NetworkManagerCommunicationException("Gateway connection test failed due to a communication " +
                    "error with Network Manager.", e);
        }
    }
    
    @Override
    public boolean connectGateway(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException {
        return performGatewayConnectionAction(paoIdentifier, ConnectionStatus.CONNECTED);
    }

    @Override
    public boolean disconnectGateway(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException {
        return performGatewayConnectionAction(paoIdentifier, ConnectionStatus.DISCONNECTED);
    }
    
    private boolean performGatewayConnectionAction(PaoIdentifier paoIdentifier, ConnectionStatus action)
            throws NetworkManagerCommunicationException {
        
        RfnDevice gateway = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        
        GatewayConnectRequest request = new GatewayConnectRequest();
        request.setRfnIdentifier(gateway.getRfnIdentifier());
        request.setStatus(action);
        
        String actionString = (action == ConnectionStatus.CONNECTED) ? "connect" : "disconnect";
        return sendActionRequest(request, actionString);
    }
    
    @Override
    public boolean collectData(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException {
        RfnDevice gateway = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        
        GatewayDataRequest request = new GatewayDataRequest();
        request.setRfnIdentifier(gateway.getRfnIdentifier());
        
        return sendActionRequest(request, "data collection");
    }

    @Override
    public boolean setCollectionSchedule(PaoIdentifier paoIdentifier, String cronExpression)
            throws NetworkManagerCommunicationException {
        
        RfnDevice gateway = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        
        GatewayScheduleRequest request = new GatewayScheduleRequest();
        request.setRfnIdentifier(gateway.getRfnIdentifier());
        request.setSchedule(cronExpression);
        
        return sendActionRequest(request, "collection schedule update");
    }

    @Override
    public boolean deleteCollectionSchedule(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException {
        RfnDevice gateway = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        
        GatewayScheduleDeleteRequest request = new GatewayScheduleDeleteRequest();
        request.setRfnIdentifier(gateway.getRfnIdentifier());
        
        return sendActionRequest(request, "collection schedule delete");
    }
    
    private boolean sendActionRequest(Serializable request, String logActionString) 
            throws NetworkManagerCommunicationException {
        
        //Send request
        BlockingJmsReplyHandler<GatewayActionResponse> replyHandler = 
                new BlockingJmsReplyHandler<>(GatewayActionResponse.class);
        actionRequestTemplate.send(request, replyHandler);
        
        //Parse response
        try {
            GatewayActionResponse response = replyHandler.waitForCompletion();
            return response.getResult() == GatewayActionResult.SUCCESSFUL;
        } catch (ExecutionException e) {
            throw new NetworkManagerCommunicationException("Gateway " + logActionString + " failed due to a " +
                    "communication error with Network Manager.", e);
        }
    }
    
}