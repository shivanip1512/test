package com.cannontech.common.rfn.service.impl;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.GatewayCreateRequest;
import com.cannontech.common.rfn.message.gateway.GatewaySaveData;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;

public class RfnGatewayServiceImpl implements RfnGatewayService {
    
    private static final Logger log = YukonLogManager.getLogger(RfnGatewayServiceImpl.class);
    
    private RfnGatewayDataCache dataCache;
    private ConnectionFactory connectionFactory;
    private ConfigurationSource configurationSource;
    private RfnDeviceCreationService rfnDeviceCreationService;
    private PaoLocationDao paoLocationDao;
    
    //Created in post-construct
    private RequestReplyTemplate<GatewayUpdateResponse> updateRequestTemplate;
    
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
    }
    
    @Override
    public Set<RfnGateway> getAllGateways() {
        //TODO: Get all base RfnDevices - new method RfnDeviceDao.getDevicesByPaoType
        //TODO: Get RfnGatewayData from cache
        //TODO: Get PaoLocation from PaoLocationDao
        return null;
    }

    @Override
    public RfnGateway getGatewayByPaoId(PaoIdentifier paoIdentifier) {
        //TODO: Get base RfnDevice via RfnDeviceDao.getDeviceForId
        //TODO: Get RfnGatewayData from cache
        //TODO: Get PaoLocation from PaoLocationDao
        return null;
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
    public boolean updateGateway(RfnGateway gateway) {
        //TODO: Determine if change is local Yukon DB change (i.e. name) or remote Network Manager change.
        //TODO: If necessary, send GatewayUpdateRequest on yukon.qr.common.rfn.GatewayUpdateRequest queue
        //TODO: If necessary, parse GatewayUpdateResponse on temp queue
        //TODO: Update yukon database, cache
        return false;
    }

    @Override
    public boolean deleteGateway(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayDeleteRequest on yukon.qr.common.rfn.GatewayUpdateRequest queue
        //TODO: Parse GatewayUpdateResponse on temp queue
        //TODO: Delete from yukon database, cache - DeviceDao.removeDevice()
        return false;
    }

    @Override
    public boolean testConnection(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayConnectionTestRequest on yukon.qr.common.rf.GatewayActionRequest queue
        //TODO: Parse GatewayConnectionTestResponse on temp queue
        return false;
    }

    @Override
    public boolean connectGateway(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayConnectRequest on yukon.qr.common.rf.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        return false;
    }

    @Override
    public boolean disconnectGateway(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayConnectRequest on yukon.qr.common.rf.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        return false;
    }
    
    @Override
    public boolean collectData(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayCollectionRequest on yukon.qr.common.rf.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        return false;
    }

    @Override
    public boolean setCollectionSchedule(PaoIdentifier paoIdentifier, String cronExpression) {
        //TODO: Send GatewayScheduleRequest on yukon.qr.common.rf.GatewayActionRequest queue
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