package com.cannontech.common.rfn.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.events.loggers.EndpointEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.message.gateway.GatewayActionResponse;
import com.cannontech.common.rfn.message.gateway.GatewayActionResult;
import com.cannontech.common.rfn.message.gateway.GatewayCollectionRequest;
import com.cannontech.common.rfn.message.gateway.GatewayConfigResult;
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
import com.cannontech.common.rfn.message.gateway.GatewaySetConfigRequest;
import com.cannontech.common.rfn.message.gateway.GatewaySetConfigResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.model.GatewayConfiguration;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.model.RfnGwy800;
import com.cannontech.common.rfn.model.RfnVirtualGateway;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.service.RfnGatewayFirmwareUpgradeService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class RfnGatewayServiceImpl implements RfnGatewayService {

    private static final Logger log = YukonLogManager.getLogger(RfnGatewayServiceImpl.class);
    
    private static final String gatewayUpdateRequestCparm = "RFN_GATEWAY_UPDATE_REQUEST";
    private static final String gatewayActionRequestCparm = "RFN_GATEWAY_ACTION_REQUEST";

    private static final String gatewayUpdateRequestQueue = "yukon.qr.obj.common.rfn.GatewayUpdateRequest";
    private static final String gatewayActionRequestQueue = "yukon.qr.obj.common.rfn.GatewayActionRequest";
    
    // Autowired in constructor
    private ConfigurationSource configSource;
    private ConnectionFactory connectionFactory;
    private DeviceDao deviceDao;
    private EndpointEventLogService endpointEventLogService;
    private GlobalSettingDao globalSettingDao;
    private PaoLocationDao paoLocationDao;
    private RfnDeviceCreationService creationService;
    private RfnDeviceDao rfnDeviceDao;
    private RfnGatewayDataCache dataCache;
    @Autowired private IDatabaseCache cache;
    @Autowired private RfnGatewayFirmwareUpgradeService rfnFirmwareUpgradeService;
    @Autowired private AsyncDynamicDataSource dataSource;
    @Autowired private AttributeService attributeService;
       
    // Created in post-construct
    private RequestReplyTemplate<GatewayUpdateResponse> updateRequestTemplate;
    private RequestReplyTemplate<GatewayActionResponse> actionRequestTemplate;
    private RequestReplyTemplate<GatewayConnectionTestResponse> connectionTestRequestTemplate;
    private RequestReplyTemplate<GatewaySetConfigResponse> configRequestTemplate;
    
    private Pattern prefixPattern = Pattern.compile("[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::\\/64");
    
    @Autowired
    public RfnGatewayServiceImpl(
            ConfigurationSource configSource,
            ConnectionFactory connectionFactory,
            DeviceDao deviceDao,
            EndpointEventLogService endpointEventLogService,
            GlobalSettingDao globalSettingDao,
            PaoLocationDao paoLocationDao,
            RfnDeviceCreationService creationService,
            RfnDeviceDao rfnDeviceDao,
            RfnGatewayDataCache dataCache) {

        this.configSource = configSource;
        this.connectionFactory = connectionFactory;
        this.deviceDao = deviceDao;
        this.endpointEventLogService = endpointEventLogService;
        this.globalSettingDao = globalSettingDao;
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
        configRequestTemplate = new RequestReplyTemplateImpl<>(JmsApiDirectory.RF_GATEWAY_SET_CONFIG.getName(),
                configSource, connectionFactory, JmsApiDirectory.RF_GATEWAY_SET_CONFIG.getQueue().getName(), false);
    }
    
    @Override
    public Set<RfnGateway> getAllGateways() {
        return getGateways(PaoType.getRfGatewayTypes());
    }
    
    @Override
    public Set<RfnGateway> getAllLegacyGateways() {
        return getGateways(Lists.newArrayList(PaoType.RFN_GATEWAY));
    }
    
    @Override
    public Set<RfnGateway> getAllNonLegacyGateways() {
        return getGateways(Lists.newArrayList(PaoType.GWY800, PaoType.VIRTUAL_GATEWAY));
    }
    
    private Set<RfnGateway> getGateways(Collection<PaoType> types) {
        List<RfnDevice> devices = rfnDeviceDao.getDevicesByPaoTypes(types);
        Set<RfnGateway> gateways = getGatewaysFromDevices(devices);
        
        return gateways;
    }
    
    @Override
    public Set<RfnGateway> getGatewaysWithData(Collection<PaoType> paoTypes) throws NmCommunicationException {

        List<RfnDevice> devices = rfnDeviceDao.getDevicesByPaoTypes(paoTypes);

        Set<RfnGateway> gateways = new HashSet<>();

        for (RfnDevice device : devices) {
            RfnGatewayData data = dataCache.get(device.getPaoIdentifier());
            RfnGateway gateway = buildRfnGateway(device, device.getName(), data);
            gateways.add(gateway);
        }

        return gateways;
    }

    @Override
    public Set<RfnGateway> getAllGatewaysWithUpdateServer() throws NmCommunicationException {
        
        String defaultUpdateServerUrl = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);
        Set<RfnGateway> gateways = getGatewaysWithData(PaoType.getRfGatewayTypes());
        Map<String, String>  upgradeVersions = rfnFirmwareUpgradeService.getFirmwareUpdateServerVersions();
        
        gateways.forEach(gateway -> {
            if (gateway.getData() != null) {
                String updateServerUrl = gateway.getData().getUpdateServerUrl();
                if (updateServerUrl == null) {
                    updateServerUrl = defaultUpdateServerUrl;
                }
                String upgradeVersion = upgradeVersions.get(updateServerUrl);
                gateway.setUpgradeVersion(upgradeVersion); 
            }
        });

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
        RfnDevice device;
        try {
            device = rfnDeviceDao.getDeviceForId(paoId);
        } catch (Exception e) {
            // Allows clicking on a Gateway Template to not throw a Yukon Exception
            LiteYukonPAObject litePao = cache.getAllPaosMap().get(paoId);
            return new RfnGateway(litePao.getPaoName(), litePao.getPaoIdentifier(), null, null);
        }
        
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
        } else if (paoId.getPaoType() == PaoType.VIRTUAL_GATEWAY){
            gateway = new RfnVirtualGateway(name, paoId, rfId, data);
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
    public Set<RfnGateway> getGatewaysByPaoIdsWithData(Iterable<Integer> paoIds) throws NmCommunicationException {
        
        List<RfnDevice> devices = rfnDeviceDao.getDevicesByPaoIds(paoIds);
        Set<RfnGateway> gateways = getGatewaysFromDevicesWithData(devices);
        
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
    
    /**
     * Builds gateway from RfnDevices. Gateway data may be null, and no exception will be thrown.
     */
    private Set<RfnGateway> getGatewaysFromDevices(Iterable<RfnDevice> devices) {
        
        Set<RfnGateway> gateways = new HashSet<>();
        for (RfnDevice device : devices) {
            // Get PAO name
            PaoIdentifier paoId = device.getPaoIdentifier();

            // Get available RfnGatewayData from cache via non-blocking call. May be null.
            RfnGatewayData data = dataCache.getIfPresent(paoId);
            
            //Create gateway object
            RfnGateway rfnGateway = buildRfnGateway(device, device.getName(), data);
            gateways.add(rfnGateway);
        }
        return gateways;
    }
    
    /**
     * Builds gateways from RfnDevices. Requires gateway data to be populated, causing an exception to be thrown if the data 
     * can't be retrieved from Network Manager.
     * @throws NmCommunicationException 
     */
    private Set<RfnGateway> getGatewaysFromDevicesWithData(Iterable<RfnDevice> devices) throws NmCommunicationException {
        
        Set<RfnGateway> gateways = new HashSet<>();
        for (RfnDevice device : devices) {
            // Get PAO name
            PaoIdentifier paoId = device.getPaoIdentifier();
            
            RfnGatewayData data = dataCache.get(paoId);
            
            //Create gateway object
            RfnGateway rfnGateway = buildRfnGateway(device, device.getName(), data);
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
                endpointEventLogService.locationUpdated(gateway.getName(), location, user);
            }
            
            return gateway;
        }
        
        throw new GatewayUpdateException("Gateway creation failed", response.getResult());
    }
    
    private GatewayCreateRequest buildGatewayCreateRequest(GatewaySettings settings) {
        
        GatewayCreateRequest request = new GatewayCreateRequest();
        GatewaySaveData data = new GatewaySaveData();
        data.setIpAddress(settings.getIpAddress());
        if(settings.getPort() != null) {
            data.setPort(settings.getPort());
        }
        data.setName(settings.getName());
        
        data.setAdmin(settings.getAdmin());
        data.setSuperAdmin(settings.getSuperAdmin());
        
        if (settings.isUseDefault()) {
            String updateServerUrl = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);
            Authentication updateServerAuth = new Authentication();
            updateServerAuth.setUsername(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_USER));
            updateServerAuth.setPassword(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_PASSWORD));
            
            data.setUpdateServerUrl(updateServerUrl);
            data.setUpdateServerLogin(updateServerAuth);
        } else {
            data.setUpdateServerUrl(settings.getUpdateServerUrl());
            data.setUpdateServerLogin(settings.getUpdateServerLogin());
        }
        
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
        if (newGatewayData.getName() != null 
                && !newGatewayData.getName().equals(existingGatewayData.getName())) {
            editData.setName(newGatewayData.getName());
            sendGatewayEditRequest = true;
        }
        if (newGatewayData.getPort() != null 
                && !newGatewayData.getPort().equals(existingGatewayData.getPort())) {
            editData.setPort(Integer.valueOf(newGatewayData.getPort()));
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
                // Update yukon database
                if (gateway.getName() != null && !gateway.getName().equals(existingGateway.getName())) {
                    // (Also sends DB change message)
                    deviceDao.changeName(existingGateway, gateway.getName());
                }
                // Force the data cache to update
                dataCache.remove(paoIdentifier);
                // Note: for lazy loading, comment out this line.
                dataCache.get(paoIdentifier);
            } else {
                log.info("Edit gateway " + paoIdentifier + " result: " + result);
                return result;
            }
        }
        
        if (gateway.getLocation() != null && !gateway.getLocation().equals(existingGateway.getLocation())) {
            paoLocationDao.save(gateway.getLocation());
            PaoLocation location = gateway.getLocation();
            endpointEventLogService.locationUpdated(gateway.getName(), location, user);
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


        String defaultUpdateServer = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);

        String updateServerUrl = gateway.getData().getUpdateServerUrl();
        settings.setUpdateServerUrl(updateServerUrl);
        settings.setUpdateServerLogin(gateway.getData().getUpdateServerLogin());
        
        if(StringUtils.isBlank(updateServerUrl) || updateServerUrl.equals(defaultUpdateServer)) {
            settings.setUseDefault(true);
        }

        return settings;
    }
    
    @Override
    public void updateGateways(Iterable<RfnGateway> gateways, LiteYukonUser user) throws NmCommunicationException {

        for (RfnGateway gateway : gateways) {
            updateGateway(gateway, user);
        }
    }
    
    @Override
    public Multimap<Short, RfnGateway> getDuplicateColorGateways(Collection<RfnGateway> gateways) {
        Multimap<Short, RfnGateway> duplicateColorGateways = HashMultimap.create();
        Map<Short, RfnGateway> collisionCheckMap = new HashMap<>();
        
        for (RfnGateway gateway : gateways) {
            RfnGatewayData data = gateway.getData();
            
            // Insert all gateways by color into the collision check map and check for insert conflicts, which are
            // duplicates.
            if (data != null) {
                RfnGateway duplicateGateway = collisionCheckMap.put(data.getRouteColor(), gateway);
                
                // When a duplicate is found, add it to the duplicate gateways map
                if (duplicateGateway != null) {
                    duplicateColorGateways.put(data.getRouteColor(), duplicateGateway);
                    duplicateColorGateways.put(data.getRouteColor(), gateway);
                }
            }
        }
        return duplicateColorGateways;
    }

    @Override
    public void generatePointData(RfnDevice gateway, BuiltInAttribute attribute, double value,
            boolean tagsPointMustArchive, Long time) {

        boolean pointCreated = attributeService.createPointForAttribute(gateway, attribute);
        LitePoint point = attributeService.getPointForAttribute(gateway, attribute);
        if (pointCreated) {
            log.info("Created point " + point + " for " + attribute + " device:" + gateway.getRfnIdentifier());
        }

        PointData pointData = new PointData();
        pointData = new PointData();
        pointData.setId(point.getLiteID());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setValue(value);
        pointData.setType(point.getPointType());
        pointData.setTagsPointMustArchive(tagsPointMustArchive);
        if (time != null) {
            pointData.setTime(new Date(time));
        }
        log.debug("Creating point data for " + pointData + " device:" + gateway.getRfnIdentifier() + " archive tags="
            + tagsPointMustArchive);

        dataSource.putValue(pointData);
    }
    
    @Override
    public void generatePointData(RfnDevice gateway, BuiltInAttribute attribute, double value,
                                  boolean tagsPointMustArchive) { 
        generatePointData(gateway, attribute, value, tagsPointMustArchive, null);
    }
    
    @Override
    public GatewayConfigResult updateIpv6Prefix(RfnGateway gateway, String newIpv6Prefix)
            throws NmCommunicationException, DuplicateException, IllegalArgumentException {
        if (isDuplicateIpv6Prefix(newIpv6Prefix)) {
            throw new DuplicateException("Ipv6Prefix:" + newIpv6Prefix + " already exists.");
        }

        if (!prefixPattern.matcher(newIpv6Prefix).matches()) {
            throw new IllegalArgumentException("Ipv6Prefix:" + newIpv6Prefix + " is not in correct format.");
        }
        
        BlockingJmsReplyHandler<GatewaySetConfigResponse> reply =
            new BlockingJmsReplyHandler<>(GatewaySetConfigResponse.class);
        GatewaySetConfigRequest request = new GatewaySetConfigRequest();
        request.setRfnIdentifier(gateway.getRfnIdentifier());
        request.setIpv6Prefix(newIpv6Prefix);
        configRequestTemplate.send(request, reply);
        try {
            GatewaySetConfigResponse response = reply.waitForCompletion();
            return response.getIpv6PrefixResult();
        } catch (ExecutionException e) {
            throw new NmCommunicationException(
                "Update Ipv6 prefix failed due to a communication error with Network Manager.", e);
        }
    }
    
    @Override
    public GatewayConfiguration gatewayAsConfiguration(RfnGateway gateway) {
        GatewayConfiguration configuration = new GatewayConfiguration();
        configuration.setId(gateway.getPaoIdentifier().getPaoId());
        configuration.setIpv6Prefix(gateway.getData().getIpv6Prefix());

        while (configuration.getIpv6Prefix() == null) {
            String newPrefix = generateNewIpv6Prefix("FD30:0000:0000:");
            if (!isDuplicateIpv6Prefix(newPrefix)) {
                configuration.setIpv6Prefix(newPrefix);
                break;
            }
        }

        return configuration;
    }
    
    private String generateNewIpv6Prefix(String first3Parts) {
        String newPrefix;
        
        for (short i = 0; i < Short.MAX_VALUE; i++) {
            newPrefix = buildPrefixLastPart(first3Parts, i);
            if (!isDuplicateIpv6Prefix(newPrefix)) {
                return newPrefix;
            }
        }
        
        // This is us giving up after exhausting Short.MAX_VALUE possible prefixes as duplicates
        return first3Parts + "0000::/64";
    }
    
    private static String buildPrefixLastPart(String first3Parts, short value) {
        String lastPart = Integer.toHexString(value).toUpperCase(); //Convert value to hex, uppercase
        lastPart = Strings.padStart(lastPart, 4, '0');              //Pad with 0s, if needed
        return first3Parts + lastPart + "::/64";
    }
    
    private boolean isDuplicateIpv6Prefix(String prefix) {
        return dataCache.getCache()
                        .asMap()
                        .values()
                        .stream()
                        .anyMatch(d -> d.getIpv6Prefix() != null && d.getIpv6Prefix().equals(prefix));
    }
}