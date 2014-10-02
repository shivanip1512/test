package com.cannontech.common.rfn.service.impl;

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
import com.cannontech.common.rfn.message.gateway.GatewayActionResponse;
import com.cannontech.common.rfn.message.gateway.GatewayCollectionRequest;
import com.cannontech.common.rfn.message.gateway.GatewayConnectRequest;
import com.cannontech.common.rfn.message.gateway.GatewayConnectionTestRequest;
import com.cannontech.common.rfn.message.gateway.GatewayCreateRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDeleteRequest;
import com.cannontech.common.rfn.message.gateway.GatewayEditRequest;
import com.cannontech.common.rfn.message.gateway.GatewaySaveData;
import com.cannontech.common.rfn.message.gateway.GatewayScheduleDeleteRequest;
import com.cannontech.common.rfn.message.gateway.GatewayScheduleRequest;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
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
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;

public class RfnGatewayServiceImpl implements RfnGatewayService {

    private static final Logger log = YukonLogManager.getLogger(RfnGatewayServiceImpl.class);
    
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoDao paoDao;
    
    private RfnGatewayDataCache dataCache;
    private ConnectionFactory connectionFactory;
    private ConfigurationSource configurationSource;
    private RfnDeviceCreationService rfnDeviceCreationService;
    private PaoLocationDao paoLocationDao;
    
    //Created in post-contruct
    private RequestReplyTemplate<GatewayUpdateResponse> updateRequestTemplate;
    private RequestReplyTemplate<GatewayActionResponse> actionRequestTemplate;
    
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
        updateRequestTemplate = new RequestReplyTemplateImpl<GatewayUpdateResponse>("RFN_GATEWAY_UPDATE_REQUEST", 
                configurationSource, connectionFactory, "yukon.qr.obj.common.rfn.GatewayUpdateRequest", false);
        actionRequestTemplate = new RequestReplyTemplateImpl<GatewayActionResponse>("RFN_GATEWAY_ACTION_REQUEST",
                configurationSource, connectionFactory, "yukon.qr.obj.common.rfn.GatewayActionRequest", false);
    }
    
    @Override
    public Set<RfnGateway> getAllGateways() {
        Set<RfnGateway> rfnGateways = new HashSet<RfnGateway>();
        // Get all base RfnDevices - new method RfnDeviceDao.getDevicesByPaoType
        List<RfnDevice> gateways = rfnDeviceDao.getDevicesByPaoType(PaoType.RFN_GATEWAY);
        for (RfnDevice gwDevice : gateways) {
            // Get PAO name
            String name = paoDao.getYukonPAOName(gwDevice.getPaoIdentifier().getPaoId());
            // Get RfnGatewayData from cache
            try {
                RfnGatewayData gatewayData = dataCache.get(gwDevice.getPaoIdentifier());
                RfnGateway rfnGateway =
                    new RfnGateway(name, gwDevice.getPaoIdentifier(),
                                   gwDevice.getRfnIdentifier(),
                                   gatewayData);
                // Get PaoLocation from PaoLocationDao
                PaoLocation gatewayLoc =
                    paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId());
                if (gatewayLoc != null) {
                    rfnGateway.setLocation(gatewayLoc);
                }
                rfnGateways.add(rfnGateway);
            } catch (NetworkManagerCommunicationException e) {
                log.warn("caught exception in getAllGateways", e);
            }
        }
        return rfnGateways;
    }

    @Override
    public RfnGateway getGatewayByPaoId(PaoIdentifier paoIdentifier)
            throws NetworkManagerCommunicationException {
        // Get base RfnDevice via RfnDeviceDao.getDeviceForId
        RfnDevice gwDevice = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        // Get PAO name
        String name = paoDao.getYukonPAOName(paoIdentifier.getPaoId());
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
    public PaoIdentifier createGateway(String name, String ipAddress, PaoLocation location, Authentication user, 
                                       Authentication admin, Authentication superAdmin) 
                                       throws NetworkManagerCommunicationException, GatewayUpdateException {
        
        //Send the request
        GatewayCreateRequest request = buildGatewayCreateRequest(ipAddress, user, admin, superAdmin);
        log.debug("Attempting to create a new gateway: " + request);
        BlockingJmsReplyHandler<GatewayUpdateResponse> replyHandler = new BlockingJmsReplyHandler<>(GatewayUpdateResponse.class);
        updateRequestTemplate.send(request, replyHandler);
        
        //Wait for the response
        GatewayUpdateResponse response;
        try {
            response = replyHandler.waitForCompletion();
            log.debug("Gateway creation response: " + response);
        } catch (ExecutionException e) {
            throw new NetworkManagerCommunicationException("Gateway creation failed due to a communication error.", e);
        }
        
        //Parse the response
        if (response.getResult() == GatewayUpdateResult.SUCCESSFUL) {
            //Create the device in Yukon DB
            RfnDevice gateway = rfnDeviceCreationService.createGateway(name, response.getRfnIdentifier());
            PaoIdentifier gatewayIdentifier = gateway.getPaoIdentifier();
            //Force the data cache to update
            dataCache.get(gatewayIdentifier);
            //Add location data
            if (location != null) {
                location.setPaoIdentifier(gatewayIdentifier);
                paoLocationDao.save(location);
            }
            return gatewayIdentifier;
        }
        throw new GatewayUpdateException("Gateway creation failed");
    }
    
    @Override
    public boolean updateGateway(RfnGateway gateway) throws NetworkManagerCommunicationException {
        // Determine if change is local Yukon DB change (i.e. name) or remote Network Manager change.
        PaoIdentifier paoIdentifier = gateway.getPaoIdentifier();
        RfnGateway existingGateway = getGatewayByPaoId(paoIdentifier);
        RfnGatewayData existingGwData = existingGateway.getData();
        RfnGatewayData newGwData = gateway.getData();
        GatewaySaveData editData = new GatewaySaveData();
        boolean sendGatewayEditRequest = false;
        if (newGwData.getIpAddress() != null && !newGwData.getIpAddress().equals(existingGwData.getIpAddress())) {
            editData.setIpAddress(newGwData.getIpAddress());
            sendGatewayEditRequest = true;
        }
        if (newGwData.getUser() != null && !newGwData.getUser().equals(existingGwData.getUser())) {
            editData.setUser(newGwData.getUser());
            sendGatewayEditRequest = true;
        }
        if (newGwData.getAdmin() != null && !newGwData.getAdmin().equals(existingGwData.getAdmin())) {
            editData.setAdmin(newGwData.getAdmin());
            sendGatewayEditRequest = true;
        }
        if (newGwData.getSuperAdmin() != null && !newGwData.getSuperAdmin().equals(existingGwData.getSuperAdmin())) {
            editData.setSuperAdmin(newGwData.getSuperAdmin());
            sendGatewayEditRequest = true;
        }
        
        
        // If necessary, send GatewayEditRequest on yukon.qr.obj.common.rfn.GatewayUpdateRequest queue
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
                throw new NetworkManagerCommunicationException("Gateway edit failed due to a communication error.", e);
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
    public boolean deleteGateway(PaoIdentifier paoIdentifier)
            throws NetworkManagerCommunicationException {
        RfnDevice gwDevice = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        GatewayDeleteRequest request = new GatewayDeleteRequest();
        request.setRfnIdentifier(gwDevice.getRfnIdentifier());

        BlockingJmsReplyHandler<GatewayUpdateResponse> replyHandler =
            new BlockingJmsReplyHandler<GatewayUpdateResponse>(GatewayUpdateResponse.class);
        // Send GatewayDeleteRequest on yukon.qr.obj.common.rfn.GatewayUpdateRequest queue
        updateRequestTemplate.send(request, replyHandler);
        try {
            // Parse GatewayUpdateResponse on temp queue
            GatewayUpdateResponse response = replyHandler.waitForCompletion();
            if (response.getResult() == GatewayUpdateResult.SUCCESSFUL) {
                // Delete from yukon database, cache - DeviceDao.removeDevice()
                deviceDao.removeDevice(gwDevice);
                dataCache.remove(paoIdentifier);
                return true;
            } else {
                log.info("Delete gateway " + paoIdentifier + " result: " + response.getResult());
                return false;
            }
        } catch (ExecutionException e) {
            throw new NetworkManagerCommunicationException("Failed to delete gateway in Network Manager.",
                                                           e);
        }
    }

    @Override
    public boolean testConnection(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayConnectionTestRequest on yukon.qr.obj.common.rfn.GatewayActionRequest queue
        //TODO: Parse GatewayConnectionTestResponse on temp queue
        return false;
    }

    @Override
    public boolean connectGateway(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayConnectRequest on yukon.qr.obj.common.rfn.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        return false;
    }

    @Override
    public boolean disconnectGateway(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayConnectRequest on yukon.qr.obj.common.rfn.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        return false;
    }
    
    @Override
    public boolean collectData(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayCollectionRequest on yukon.qr.obj.common.rfn.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        return false;
    }

    @Override
    public boolean setCollectionSchedule(PaoIdentifier paoIdentifier, String cronExpression) {
        //TODO: Send GatewayScheduleRequest on yukon.qr.obj.common.rfn.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        //TODO: Update cache
        return false;
    }

    @Override
    public boolean deleteCollectionSchedule(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayScheduleDeleteRequest on yukon.qr.obj.common.rfn.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        //TODO: Update cache
        return false;
    }
    
    private GatewayCreateRequest buildGatewayCreateRequest(String ipAddress, Authentication user, Authentication admin,
                                                           Authentication superAdmin) {
        GatewayCreateRequest request = new GatewayCreateRequest();
        GatewaySaveData data = new GatewaySaveData();
        data.setIpAddress(ipAddress);
        data.setUser(user);
        data.setAdmin(admin);
        data.setSuperAdmin(superAdmin);
        request.setData(data);
        return request;
    }
}