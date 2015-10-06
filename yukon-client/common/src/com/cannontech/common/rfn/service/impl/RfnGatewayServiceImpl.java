package com.cannontech.common.rfn.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.events.loggers.EndpointEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.message.gateway.GatewayActionResponse;
import com.cannontech.common.rfn.message.gateway.GatewayActionResult;
import com.cannontech.common.rfn.message.gateway.GatewayCollectionRequest;
import com.cannontech.common.rfn.message.gateway.GatewayConnectRequest;
import com.cannontech.common.rfn.message.gateway.GatewayConnectionTestRequest;
import com.cannontech.common.rfn.message.gateway.GatewayConnectionTestResponse;
import com.cannontech.common.rfn.message.gateway.GatewayConnectionTestResult;
import com.cannontech.common.rfn.message.gateway.GatewayCreateRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDeleteRequest;
import com.cannontech.common.rfn.message.gateway.GatewayEditRequest;
import com.cannontech.common.rfn.message.gateway.GatewaySaveData;
import com.cannontech.common.rfn.message.gateway.GatewayScheduleDeleteRequest;
import com.cannontech.common.rfn.message.gateway.GatewayScheduleRequest;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionRequest;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResponse;
import com.cannontech.common.rfn.message.gateway.RfnUpdateServerAvailableVersionResult;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.model.RfnGwy800;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.NMConfigurationService;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Sets;

public class RfnGatewayServiceImpl implements RfnGatewayService {

    private static final Logger log = YukonLogManager.getLogger(RfnGatewayServiceImpl.class);
    
    private static final String gatewayUpdateRequestCparm = "RFN_GATEWAY_UPDATE_REQUEST";
    private static final String gatewayActionRequestCparm = "RFN_GATEWAY_ACTION_REQUEST";
    private static final String rfnUpdateServerAvailableVersionRequestCparm =
        "RFN_UPDATE_SERVER_AVAILABLE_VERSION_REQUEST";

    private static final String gatewayUpdateRequestQueue = "yukon.qr.obj.common.rfn.GatewayUpdateRequest";
    private static final String gatewayActionRequestQueue = "yukon.qr.obj.common.rfn.GatewayActionRequest";
    private static final String gatewayDataRequestQueue = "yukon.qr.obj.common.rfn.GatewayDataRequest";
    
    
    // Autowired in constructor
    private ConfigurationSource configSource;
    private ConnectionFactory connectionFactory;
    private DeviceDao deviceDao;
    private EndpointEventLogService endpointEventLogService;
    private GlobalSettingDao globalSettingDao;
    private IDatabaseCache dbCache;
    private NMConfigurationService nmConfigurationService;
    private PaoLocationDao paoLocationDao;
    private RfnDeviceCreationService creationService;
    private RfnDeviceDao rfnDeviceDao;
    private RfnGatewayDataCache dataCache;

       
    // Created in post-construct
    private RequestReplyTemplate<GatewayUpdateResponse> updateRequestTemplate;
    private RequestReplyTemplate<GatewayActionResponse> actionRequestTemplate;
    private RequestReplyTemplate<GatewayConnectionTestResponse> connectionTestRequestTemplate;
    private RequestReplyTemplate<RfnUpdateServerAvailableVersionResponse> rfnUpdateServerAvailableVersionTemplate;
    
    @Autowired
    public RfnGatewayServiceImpl(
            ConfigurationSource configSource,
            ConnectionFactory connectionFactory,
            DeviceDao deviceDao,
            EndpointEventLogService endpointEventLogService,
            GlobalSettingDao globalSettingDao,
            IDatabaseCache dbCache,
            NMConfigurationService nmConfigurationService,
            PaoLocationDao paoLocationDao,
            RfnDeviceCreationService creationService,
            RfnDeviceDao rfnDeviceDao,
            RfnGatewayDataCache dataCache) {

        this.configSource = configSource;
        this.connectionFactory = connectionFactory;
        this.deviceDao = deviceDao;
        this.endpointEventLogService = endpointEventLogService;
        this.globalSettingDao = globalSettingDao;
        this.dbCache = dbCache;
        this.nmConfigurationService = nmConfigurationService;
        this.paoLocationDao = paoLocationDao;
        this.creationService = creationService;
        this.rfnDeviceDao = rfnDeviceDao;
        this.dataCache = dataCache;
    }
    
    @PostConstruct
    public void init() {
        updateRequestTemplate = new RequestReplyTemplateImpl<>(gatewayUpdateRequestCparm, 
                configSource, connectionFactory, gatewayUpdateRequestQueue, false);
        actionRequestTemplate = new RequestReplyTemplateImpl<>(gatewayActionRequestCparm,
                configSource, connectionFactory, gatewayActionRequestQueue, false);
        connectionTestRequestTemplate = 
                new RequestReplyTemplateImpl<>(gatewayActionRequestCparm, 
                configSource, connectionFactory, gatewayActionRequestQueue, false);
        rfnUpdateServerAvailableVersionTemplate =
                new RequestReplyTemplateImpl<>(rfnUpdateServerAvailableVersionRequestCparm, configSource, 
                connectionFactory, gatewayDataRequestQueue, false);
    }
    
    @Override
    public Set<RfnGateway> getAllGateways() {
        
        List<RfnDevice> devices = rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfGatewayTypes());
        Set<RfnGateway> gateways = getGatewaysFromDevices(devices);
        
        return gateways;
    }
    
    @Override
    public Set<RfnGateway> getAllGatewaysWithData() throws NmCommunicationException {

        List<RfnDevice> devices = rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfGatewayTypes());

        Set<RfnGateway> gateways = new HashSet<>();

        for (RfnDevice device : devices) {
            RfnGatewayData data = dataCache.get(device.getPaoIdentifier());
            RfnGateway gateway = buildRfnGateway(device, device.getName(), data);
            gateways.add(gateway);
        }

        return gateways;
    }

    @Override
    public RfnGateway getGatewayByPaoIdWithData(int paoId) throws NmCommunicationException {
        
        // Get base RfnDevice
        RfnDevice device = rfnDeviceDao.getDeviceForId(paoId);
        
        // Get RfnGatewayData from cache
        RfnGatewayData data = dataCache.get(device.getPaoIdentifier());
        
        return buildRfnGateway(device, device.getName(), data);
    }
    
    @Override
    public RfnGateway getGatewayByPaoId(int paoId) {
        
        // Get base RfnDevice
        RfnDevice device = rfnDeviceDao.getDeviceForId(paoId);
        // Get RfnGatewayData from cache
        RfnGatewayData data = dataCache.getIfPresent(device.getPaoIdentifier());
        
        return buildRfnGateway(device, device.getName(), data);
    }
    
    private RfnGateway buildRfnGateway(RfnDevice device, String name, RfnGatewayData data) {
        
        RfnGateway gateway;
        RfnIdentifier rfId = device.getRfnIdentifier();
        PaoIdentifier paoId = device.getPaoIdentifier();
        
        if (paoId.getPaoType() == PaoType.GWY800) {
            gateway = new RfnGwy800(name, paoId, rfId, data);
        } else {
            gateway = new RfnGateway(name, paoId, rfId, data);
        }
        
        // Get PaoLocation from PaoLocationDao
        PaoLocation location = paoLocationDao.getLocation(paoId.getPaoId());
        if (location != null) {
            gateway.setLocation(location);
        }
        
        return gateway;
    }
    
    @Override
    public Set<RfnGateway> getGatewaysByPaoIds(Iterable<Integer> paoIds) {
        
        List<RfnDevice> devices = rfnDeviceDao.getDevicesByPaoIds(paoIds);
        Set<RfnGateway> gateways = getGatewaysFromDevices(devices);
        
        return gateways;
    }
    
    @Override
    public Map<Integer, RfnGateway> getAllGatewaysByPaoId() {
        
        Set<RfnGateway> allGateways = getAllGateways();
        Map<Integer, RfnGateway> paoIdToGateway = new HashMap<>();
        for(RfnGateway gateway : allGateways) {
            paoIdToGateway.put(gateway.getPaoIdentifier().getPaoId(), gateway);
        }
        
        return paoIdToGateway;
    }
    
    private Set<RfnGateway> getGatewaysFromDevices(Iterable<RfnDevice> devices) {
        
        Set<RfnGateway> gateways = new HashSet<RfnGateway>();
        for (RfnDevice device : devices) {
            // Get PAO name
            PaoIdentifier paoId = device.getPaoIdentifier();
            String name = dbCache.getAllPaosMap().get(paoId.getPaoId()).getPaoName();
            // Get available RfnGatewayData from cache via non-blocking call. May be null.
            RfnGatewayData data = dataCache.getIfPresent(paoId);
            
            //Create gateway object
            RfnGateway rfnGateway = buildRfnGateway(device, name, data);
            gateways.add(rfnGateway);
        }
        return gateways;
    }
    
    @Override
    public RfnDevice createGateway(GatewaySettings settings, LiteYukonUser user) throws NmCommunicationException, GatewayUpdateException {
        
        // Send the request
        GatewayCreateRequest request = buildGatewayCreateRequest(settings);
        log.debug("Attempting to create a new gateway: " + request);
        BlockingJmsReplyHandler<GatewayUpdateResponse> replyHandler = 
                new BlockingJmsReplyHandler<>(GatewayUpdateResponse.class);
        updateRequestTemplate.send(request, replyHandler);
        
        // Wait for the response
        GatewayUpdateResponse response;
        try {
            response = replyHandler.waitForCompletion();
            log.debug("Gateway creation response: " + response);
        } catch (ExecutionException e) {
            throw new NmCommunicationException("Gateway creation failed due to a communication error " +
                    "with Network Manager.", e);
        }
        
        // Parse the response
        if (response.getResult() == GatewayUpdateResult.SUCCESSFUL) {
            // Create the device in Yukon DB (This also sends a DB Change message)
            RfnDevice gateway = creationService.createGateway(settings.getName(), response.getRfnIdentifier());
            PaoIdentifier gatewayIdentifier = gateway.getPaoIdentifier();
            // Force the data cache to update
            dataCache.get(gatewayIdentifier);
            
            // Add location data
            Double latitude = settings.getLatitude();
            Double longitude = settings.getLongitude();
            if (latitude != null && longitude != null) {
                PaoLocation location = new PaoLocation(gatewayIdentifier, latitude, longitude);
                paoLocationDao.save(location);
                endpointEventLogService.locationUpdated(gateway.getPaoIdentifier(), location, user);
            }
            
            return gateway;
        }
        
        throw new GatewayUpdateException("Gateway creation failed", response.getResult());
    }
    
    private GatewayCreateRequest buildGatewayCreateRequest(GatewaySettings settings) {
        
        GatewayCreateRequest request = new GatewayCreateRequest();
        GatewaySaveData data = new GatewaySaveData();
        data.setIpAddress(settings.getIpAddress());
        
        data.setAdmin(settings.getAdmin());
        data.setSuperAdmin(settings.getSuperAdmin());
        data.setUpdateServerUrl(settings.getUpdateServerUrl());
        data.setUpdateServerLogin(settings.getUpdateServerLogin());
        request.setData(data);
        
        return request;
    }
    
    @Override
    public GatewayUpdateResult updateGateway(RfnGateway gateway, LiteYukonUser user) throws NmCommunicationException {
        
        // Determine if change is local Yukon DB change (i.e. name) or remote Network Manager change.
        PaoIdentifier paoIdentifier = gateway.getPaoIdentifier();
        RfnGateway existingGateway = getGatewayByPaoIdWithData(paoIdentifier.getPaoId());
        RfnGatewayData existingGatewayData = existingGateway.getData();
        RfnGatewayData newGatewayData = gateway.getData();
        
        GatewaySaveData editData = new GatewaySaveData();
        boolean sendGatewayEditRequest = false;
        
        if (newGatewayData.getIpAddress() != null 
                && !newGatewayData.getIpAddress().equals(existingGatewayData.getIpAddress())) {
            editData.setIpAddress(newGatewayData.getIpAddress());
            sendGatewayEditRequest = true;
        }
        if (newGatewayData.getAdmin() != null 
                && !newGatewayData.getAdmin().equals(existingGatewayData.getAdmin())) {
            editData.setAdmin(newGatewayData.getAdmin());
            sendGatewayEditRequest = true;
        }
        if (newGatewayData.getSuperAdmin() != null 
                && !newGatewayData.getSuperAdmin().equals(existingGatewayData.getSuperAdmin())) {
            editData.setSuperAdmin(newGatewayData.getSuperAdmin());
            sendGatewayEditRequest = true;
        }
        if (newGatewayData.getUpdateServerUrl() != null 
                && !newGatewayData.getUpdateServerUrl().equals(existingGatewayData.getUpdateServerUrl())) {
            editData.setUpdateServerUrl(newGatewayData.getUpdateServerUrl());
            sendGatewayEditRequest = true;
        }
        if (newGatewayData.getUpdateServerLogin() != null 
                && !newGatewayData.getUpdateServerLogin().equals(existingGatewayData.getUpdateServerLogin())) {
            editData.setUpdateServerLogin(newGatewayData.getUpdateServerLogin());
            sendGatewayEditRequest = true;
        }
        
        GatewayUpdateResult result = GatewayUpdateResult.SUCCESSFUL;
        
        // If necessary, send GatewayEditRequest
        if (sendGatewayEditRequest) {
            GatewayEditRequest request = new GatewayEditRequest();
            request.setRfnIdentifier(existingGateway.getRfnIdentifier());
            request.setData(editData);
            
            log.debug("Sending gateway edit request: " + request);
            BlockingJmsReplyHandler<GatewayUpdateResponse> replyHandler = 
                    new BlockingJmsReplyHandler<>(GatewayUpdateResponse.class);
            updateRequestTemplate.send(request, replyHandler);
            
            // Wait for the response
            GatewayUpdateResponse response;
            try {
                response = replyHandler.waitForCompletion();
                log.debug("Gateway edit response: " + response);
            } catch (ExecutionException e) {
                throw new NmCommunicationException("Gateway edit failed due to a communication error " +
                        "with Network Manager.", e);
            }
            
            // Parse GatewayUpdateResponse
            result = response.getResult();
            if (result == GatewayUpdateResult.SUCCESSFUL) {
                // Force the data cache to update
                dataCache.remove(paoIdentifier);
                // Note: for lazy loading, comment out this line.
                dataCache.get(paoIdentifier);
            } else {
                log.info("Edit gateway " + paoIdentifier + " result: " + result);
                return result;
            }
        }
        
        // Update yukon database
        if (gateway.getName() != null && !gateway.getName().equals(existingGateway.getName())) {
            // (Also sends DB change message)
            deviceDao.changeName(existingGateway, gateway.getName());
        }
        if (gateway.getLocation() != null && !gateway.getLocation().equals(existingGateway.getLocation())) {
            paoLocationDao.save(gateway.getLocation());
            PaoLocation location = gateway.getLocation();
            endpointEventLogService.locationUpdated(gateway.getPaoIdentifier(), location, user);
        }

        return result;
    }
    
    @Override
    public boolean deleteGateway(PaoIdentifier paoIdentifier) throws NmCommunicationException {
        
        RfnDevice gateway = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        GatewayDeleteRequest request = new GatewayDeleteRequest();
        request.setRfnIdentifier(gateway.getRfnIdentifier());
        
        BlockingJmsReplyHandler<GatewayUpdateResponse> replyHandler =
            new BlockingJmsReplyHandler<>(GatewayUpdateResponse.class);
        updateRequestTemplate.send(request, replyHandler);
        
        // Parse GatewayUpdateResponse on temp queue
        try {
            GatewayUpdateResponse response = replyHandler.waitForCompletion();
            if (response.getResult() == GatewayUpdateResult.SUCCESSFUL || response.getResult() == GatewayUpdateResult.FAILED_UNKNOWN_GATEWAY) {
                // Delete from yukon database and cache, and send DB change message
                deviceDao.removeDevice(gateway);
                dataCache.remove(paoIdentifier);
                return true;
            } else {
                log.info("Delete gateway " + paoIdentifier + " result: " + response.getResult());
                return false;
            }
        } catch (ExecutionException e) {
            throw new NmCommunicationException("Gateway delete failed due to a communication error with " +
                    "Network Manager.", e);
        }
    }
    
    @Override
    public boolean testConnection(int deviceId, String ipAddress, String username, String password) 
            throws NmCommunicationException {
        
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        
        Authentication auth = new Authentication();
        auth.setUsername(username);
        auth.setPassword(password);
        
        // Build request
        GatewayConnectionTestRequest request = new GatewayConnectionTestRequest();
        request.setRfnIdentifier(device.getRfnIdentifier());
        request.setIpAddress(ipAddress);
        request.setAuthentication(auth);
        
        return sendConnectionRequest(request);
    }
    
    @Override
    public boolean testConnection(int deviceId) throws NmCommunicationException {
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        
        // Build request
        GatewayConnectionTestRequest request = new GatewayConnectionTestRequest();
        request.setRfnIdentifier(device.getRfnIdentifier());
        
        return sendConnectionRequest(request);
    }
    
    private boolean sendConnectionRequest(GatewayConnectionTestRequest request) 
            throws NmCommunicationException {
        
        BlockingJmsReplyHandler<GatewayConnectionTestResponse> replyHandler = 
                new BlockingJmsReplyHandler<>(GatewayConnectionTestResponse.class);
                
        // Send request
        log.debug("Sending connection request: " + request);
        connectionTestRequestTemplate.send(request, replyHandler);
        
        // Parse response
        try {
            GatewayConnectionTestResponse response = replyHandler.waitForCompletion();
            return response.getResult() == GatewayConnectionTestResult.SUCCESSFUL;
        } catch (ExecutionException e) {
            throw new NmCommunicationException("Gateway connection test failed due to a communication " +
                    "error with Network Manager.", e);
        }
    }
    
    @Override
    public boolean connectGateway(PaoIdentifier paoIdentifier) throws NmCommunicationException {
        return performGatewayConnectionAction(paoIdentifier, ConnectionStatus.CONNECTED);
    }
    
    @Override
    public boolean disconnectGateway(PaoIdentifier paoIdentifier) throws NmCommunicationException {
        return performGatewayConnectionAction(paoIdentifier, ConnectionStatus.DISCONNECTED);
    }
    
    private boolean performGatewayConnectionAction(PaoIdentifier paoIdentifier, ConnectionStatus action)
            throws NmCommunicationException {
        
        RfnDevice gateway = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        
        GatewayConnectRequest request = new GatewayConnectRequest();
        request.setRfnIdentifier(gateway.getRfnIdentifier());
        request.setStatus(action);
        
        String actionString = (action == ConnectionStatus.CONNECTED) ? "connect" : "disconnect";
        return sendActionRequest(request, actionString);
    }
    
    @Override
    public boolean collectData(PaoIdentifier paoIdentifier, DataType... types) 
            throws NmCommunicationException {
        
        RfnDevice gateway = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        GatewayCollectionRequest request = new GatewayCollectionRequest();
        request.setTypes(Sets.newHashSet(types));
        request.setRfnIdentifier(gateway.getRfnIdentifier());
        
        return sendActionRequest(request, "data collection");
    }
    
    @Override
    public boolean setCollectionSchedule(PaoIdentifier paoIdentifier, String cronExpression)
            throws NmCommunicationException {
        
        RfnDevice gateway = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        
        GatewayScheduleRequest request = new GatewayScheduleRequest();
        request.setRfnIdentifier(gateway.getRfnIdentifier());
        request.setSchedule(cronExpression);
        
        return sendActionRequest(request, "collection schedule update");
    }
    
    @Override
    public boolean deleteCollectionSchedule(PaoIdentifier paoIdentifier) throws NmCommunicationException {
        
        RfnDevice gateway = rfnDeviceDao.getDeviceForId(paoIdentifier.getPaoId());
        
        GatewayScheduleDeleteRequest request = new GatewayScheduleDeleteRequest();
        request.setRfnIdentifier(gateway.getRfnIdentifier());
        
        return sendActionRequest(request, "collection schedule delete");
    }
    
    private boolean sendActionRequest(Serializable request, String logActionString) 
            throws NmCommunicationException {
        
        //Send request
        BlockingJmsReplyHandler<GatewayActionResponse> replyHandler = 
                new BlockingJmsReplyHandler<>(GatewayActionResponse.class);
        actionRequestTemplate.send(request, replyHandler);
        
        //Parse response
        try {
            GatewayActionResponse response = replyHandler.waitForCompletion();
            return response.getResult() == GatewayActionResult.SUCCESSFUL;
        } catch (ExecutionException e) {
            throw new NmCommunicationException("Gateway " + logActionString + " failed due to a " +
                    "communication error with Network Manager.", e);
        }
    }
    
    @Override
    public void clearCache() {
        dataCache.getCache().asMap().clear();
    }
    
    @Override
    public GatewaySettings gatewayAsSettings(RfnGateway gateway) {
        GatewaySettings settings = new GatewaySettings();

        settings.setId(gateway.getPaoIdentifier().getPaoId());
        settings.setName(gateway.getName());
        settings.setIpAddress(gateway.getData().getIpAddress());
        settings.setAdmin(gateway.getData().getAdmin());
        settings.setSuperAdmin(gateway.getData().getSuperAdmin());
        if (gateway.getLocation() != null) {
            settings.setLatitude(gateway.getLocation().getLatitude());
            settings.setLongitude(gateway.getLocation().getLongitude());
        }

        if(nmConfigurationService.isFirmwareUpdateSupported()) {

            String defaultUpdateServer = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);

            String updateServerUrl = gateway.getData().getUpdateServerUrl();
            settings.setUpdateServerUrl(updateServerUrl);
            settings.setUpdateServerLogin(gateway.getData().getUpdateServerLogin());

            if(StringUtils.isBlank(updateServerUrl) || updateServerUrl.equals(defaultUpdateServer)) {
                settings.setUseDefault(true);
            }
        }

        return settings;
    }
    
    @Override
    public Map<String, Object> listAllGatewaysWithUpdateServerAvailableVersion() throws NmCommunicationException {
        Set<RfnGateway> gateways = getAllGateways();
        Map<String, Object> updateServerAvailableVersionMap = new HashMap<String, Object>();
        // iterating all rfnGateways to set the RfnUpdateData with available version for the update server
        for (RfnGateway rfnGateway : gateways) {
            if (rfnGateway.getData() != null) {
                RfnGatewayData rfnGatewayData = rfnGateway.getData();
                RfnUpdateServerAvailableVersionRequest rfnUpdateServerAvailableVersionRequest =
                    new RfnUpdateServerAvailableVersionRequest();
                // setting update server to the rfnUpdateServerAvailableVersionRequest for which available
                // version has to be fetched
                rfnUpdateServerAvailableVersionRequest.setUpdateServerUrl(rfnGatewayData.getUpdateServerUrl());

                RfnUpdateServerAvailableVersionResponse response =
                    getUpdateServerAvailableVersionRequest(rfnUpdateServerAvailableVersionRequest,
                        "Fetch Available Version for Update Server");

                if (response != null && response.getResult() == RfnUpdateServerAvailableVersionResult.SUCCESS) {
                    updateServerAvailableVersionMap.put(rfnGatewayData.getUpdateServerUrl(),
                        response.getAvailableVersion());
                }
            }
        }
        return updateServerAvailableVersionMap;
    }

    /**
     * This method communicates to the NM and fetches the available version for update server
     * 
     * @param request
     * @param logActionString
     * @return
     * @throws NmCommunicationException
     */
    private RfnUpdateServerAvailableVersionResponse getUpdateServerAvailableVersionRequest(Serializable request,
            String logActionString) throws NmCommunicationException {
        RfnUpdateServerAvailableVersionResponse response = null;
        BlockingJmsReplyHandler<RfnUpdateServerAvailableVersionResponse> replyHandler =
            new BlockingJmsReplyHandler<>(RfnUpdateServerAvailableVersionResponse.class);
        log.debug("Sending fetch request for UpdateServer's available version");
        rfnUpdateServerAvailableVersionTemplate.send(request, replyHandler);
        try {
            response = replyHandler.waitForCompletion();
            log.debug("Response = " + response.getResult());
        } catch (ExecutionException e) {
            throw new NmCommunicationException(logActionString
                + "action failed due to communication error with Network Manager.", e);
        }
        return response;
    }

    @Override
    public void updateGateways(Iterable<RfnGateway> gateways, LiteYukonUser user) throws NmCommunicationException {

        for (RfnGateway gateway : gateways) {
            updateGateway(gateway, user);
        }
    }

}