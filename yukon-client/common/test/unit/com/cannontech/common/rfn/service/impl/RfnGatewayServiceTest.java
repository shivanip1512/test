package com.cannontech.common.rfn.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.mock.FakeRequestReplyTemplate;
import com.cannontech.common.mock.FakeRequestReplyTemplate.Mode;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class RfnGatewayServiceTest {
    private RfnGatewayServiceImpl service;
    
    private Authentication user;
    private Authentication admin;
    private Authentication superAdmin;
    private Double latitude = 1.0;
    private Double longitude = 2.0;
    
    private final String ipAddress = "123.123.123.123";
    private final String name = "Test Gateway";
    private final String gatewayName = "Test Gateway";
    private final PaoIdentifier gatewayPaoId = new PaoIdentifier(100, PaoType.RFN_GATEWAY);
    private static final RfnIdentifier gatewayRfnId = new RfnIdentifier("10000", "CPS", "RF_GATEWAY");
    
    private Map<Integer, LiteYukonPAObject> paos;
    
    @Before
    public void init() {
        user = new Authentication();
        user.setDefaultUser(true);
        user.setUsername("testUser");
        user.setPassword("testUserPass");
        
        admin = new Authentication();
        admin.setDefaultUser(false);
        admin.setUsername("testAdmin");
        admin.setPassword("testAdminPass");
        
        superAdmin = new Authentication();
        superAdmin.setDefaultUser(true);
        superAdmin.setUsername("testSuperAdmin");
        superAdmin.setPassword("testSuperAdminPass");
        
        paos = new HashMap<Integer, LiteYukonPAObject>();
        LiteYukonPAObject gwPao = new LiteYukonPAObject(gatewayPaoId.getPaoId());
        gwPao.setPaoName(name);
        paos.put(gatewayPaoId.getPaoId(), gwPao);
    }
    
    private static RfnGatewayData createEmptyRfnGatewayData(RfnIdentifier rfnIdentifier) {
        GatewayDataResponse gatewayDataResponse = new GatewayDataResponse();
        gatewayDataResponse.setRfnIdentifier(rfnIdentifier);
        return new RfnGatewayData(gatewayDataResponse);
    }
    
    private static RfnDevice createRfnDevice(final PaoIdentifier paoIdentifier,
                                             RfnIdentifier rfnIdentifier) {
        return new RfnDevice(new YukonPao() {
            @Override
            public PaoIdentifier getPaoIdentifier() {
                return paoIdentifier;
            }
        }, rfnIdentifier);
    }
    
    @Test
    public void test_getAllGateways() throws NetworkManagerCommunicationException {
        // Setup 2 gateway RfnDevices.
        List<RfnDevice> rfnDevices = new ArrayList<RfnDevice>();
        rfnDevices.add(createRfnDevice(gatewayPaoId, gatewayRfnId));
        PaoIdentifier paoIdentifier = new PaoIdentifier(1, PaoType.RFN_GATEWAY);
        RfnIdentifier gateway2RfnId = new RfnIdentifier("10001", "CPS", "RF_GATEWAY");
        rfnDevices.add(createRfnDevice(paoIdentifier, gateway2RfnId));
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve all RFN_GATEWAY devices from rfnDeviceDao.
        EasyMock.expect(rfnDeviceDao.getDevicesByPaoType(PaoType.RFN_GATEWAY))
            .andReturn(rfnDevices);
        EasyMock.replay(rfnDeviceDao);
        
        // Add another second gateway PAO to the map.
        LiteYukonPAObject gwPao = new LiteYukonPAObject(paoIdentifier.getPaoId());
        gwPao.setPaoName("GW2");
        paos.put(paoIdentifier.getPaoId(), gwPao);
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        // Expecting a call to retrieve PAO name for each gateway.
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);

        PaoLocationDao paoLocationDao = new EmptyPaoLocationDao();

        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        // Expecting a call to retrieve gateway data for each RFN_GATEWAY rfnDevice.
        EasyMock.expect(gatewayDataCache.getIfPresent(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.expect(gatewayDataCache.getIfPresent(paoIdentifier)).andReturn(createEmptyRfnGatewayData(gateway2RfnId));
        EasyMock.replay(gatewayDataCache);

        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, null, paoLocationDao);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        ReflectionTestUtils.setField(service, "cache", cache);
        Assert.assertEquals("Expecting 2 gateways", 2, service.getAllGateways().size());
    }
    
    @Test
    public void test_getGatewayByPaoId() throws NetworkManagerCommunicationException {
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve an RfnDevice from rfnDeviceDao.
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andReturn(createRfnDevice(gatewayPaoId, gatewayRfnId));
        EasyMock.replay(rfnDeviceDao);
        
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        // Expecting a call to retrieve PAO name.
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        PaoLocation paoLocation = new PaoLocation();
        paoLocation.setPaoIdentifier(gatewayPaoId);
        paoLocation.setLatitude(1);
        paoLocation.setLongitude(10);
        // Expecting a call to retrieve PaoLocation for this pao ID.
        EasyMock.expect(paoLocationDao.getLocation(gatewayPaoId.getPaoId())).andReturn(paoLocation);
        EasyMock.replay(paoLocationDao);
        
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        // Expecting a call to retrieve gateway data for this pao ID.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.replay(gatewayDataCache);
        
        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, null, paoLocationDao);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        ReflectionTestUtils.setField(service, "cache", cache);
        RfnGateway rfnGateway = service.getGatewayByPaoId(gatewayPaoId);
        Assert.assertEquals("PaoIdentifier does not match", gatewayPaoId, rfnGateway.getPaoIdentifier());
        Assert.assertEquals("RfnIdentifier does not match", gatewayRfnId, rfnGateway.getRfnIdentifier());
        Assert.assertEquals("PaoLocation does not match", paoLocation, rfnGateway.getLocation());
    }
    
    @Test(expected=NotFoundException.class)
    public void test_getGatewayByPaoId_NotFoundException() throws NetworkManagerCommunicationException {
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve an RfnDevice from rfnDeviceDao.
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andThrow(new NotFoundException("Unknown rfn device Id " + gatewayPaoId.getPaoId()));
        EasyMock.replay(rfnDeviceDao);
        
        service = new RfnGatewayServiceImpl(null, null, null, null, null);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        service.getGatewayByPaoId(gatewayPaoId);
    }
    
    @Test(expected=NetworkManagerCommunicationException.class)
    public void test_getGatewayByPaoId_communicationError() throws NetworkManagerCommunicationException {
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve an RfnDevice from rfnDeviceDao (return value not used in this test).
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andReturn(createRfnDevice(gatewayPaoId, gatewayRfnId));
        EasyMock.replay(rfnDeviceDao);
        
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        // Expecting a call to retrieve PAO name.
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        PaoLocationDao paoLocationDao = new EmptyPaoLocationDao();
        
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        // Expecting a call to retrieve gateway data for this pao ID, throw exception to simulate communication error.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andThrow(new NetworkManagerCommunicationException("Communication Error"));
        EasyMock.replay(gatewayDataCache);
        
        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, null, paoLocationDao);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        ReflectionTestUtils.setField(service, "cache", cache);
        service.getGatewayByPaoId(gatewayPaoId);
    }
    
    @Test(expected=NetworkManagerCommunicationException.class)
    public void test_createGateway_communicationError() throws NetworkManagerCommunicationException, GatewayUpdateException {
        //dependencies can be null, because they're not called in this test scenario
        service = new RfnGatewayServiceImpl(null, null, null, null, null);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.EXCEPTION);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        GatewaySettings settings = new GatewaySettings();
        settings.setName(name);
        settings.setUser(user);
        settings.setAdmin(admin);
        settings.setSuperAdmin(superAdmin);
        settings.setLatitude(latitude);
        settings.setLongitude(longitude);
        
        service.createGateway(settings);
    }
    
    @Test(expected=NetworkManagerCommunicationException.class)
    public void test_createGateway_timeoutError() throws NetworkManagerCommunicationException, GatewayUpdateException {
        //dependencies can be null, because they're not called in this test scenario
        service = new RfnGatewayServiceImpl(null, null, null, null, null);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.EXCEPTION);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        GatewaySettings settings = new GatewaySettings();
        settings.setName(name);
        settings.setUser(user);
        settings.setAdmin(admin);
        settings.setSuperAdmin(superAdmin);
        settings.setLatitude(latitude);
        settings.setLongitude(longitude);
        
        service.createGateway(settings);
    }
    
    @Test(expected=GatewayUpdateException.class)
    public void test_createGateway_failedResponse() throws NetworkManagerCommunicationException, GatewayUpdateException {
        //dependencies can be null, because they're not called in this test scenario
        service = new RfnGatewayServiceImpl(null, null, null, null, null);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setGatewayUpdateResult(GatewayUpdateResult.FAILED);
        fakeTemplate.setMode(Mode.REPLY);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        GatewaySettings settings = new GatewaySettings();
        settings.setName(name);
        settings.setUser(user);
        settings.setAdmin(admin);
        settings.setSuperAdmin(superAdmin);
        settings.setLatitude(latitude);
        settings.setLongitude(longitude);
        
        service.createGateway(settings);
    }
    
    @Test
    public void test_createGateway_successResponse() throws NetworkManagerCommunicationException, GatewayUpdateException {
        RfnDeviceCreationService rfnDeviceCreationService = EasyMock.createStrictMock(RfnDeviceCreationService.class);
        EasyMock.expect(rfnDeviceCreationService.createGateway(gatewayName, gatewayRfnId))
                .andReturn(new RfnDevice(gatewayPaoId, gatewayRfnId));
        EasyMock.replay(rfnDeviceCreationService);
        
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(null); //return value not used in this case
        EasyMock.replay(gatewayDataCache);
        
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        PaoLocation location = new PaoLocation();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        
        paoLocationDao.save(location);
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        //connectionFactory and configurationSource can be null - they are only used by the RequestReplyTemplate,
        //which is replaced by the FakeUpdateRequestReplyTemplate in this test
        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, rfnDeviceCreationService, paoLocationDao);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        GatewaySettings settings = new GatewaySettings();
        settings.setName(name);
        settings.setUser(user);
        settings.setAdmin(admin);
        settings.setSuperAdmin(superAdmin);
        settings.setLatitude(latitude);
        settings.setLongitude(longitude);
        
        service.createGateway(settings);
    }
    
    @Test
    public void test_updateGateway_updateLocalSuccess() throws NetworkManagerCommunicationException {
        RfnDevice gwDevice = createRfnDevice(gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        // Expecting a call to retrieve PAO name.
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        // Expecting call to retrieve gateway data.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.replay(gatewayDataCache);
        
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        // Expecting call to retrieve existing location.
        EasyMock.expect(paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId())).andReturn(null);
        // Expecting new location to be saved.
        
        PaoLocation location = new PaoLocation();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        
        paoLocationDao.save(location);
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        // Expecting device name to be changed.
        deviceDao.changeName(EasyMock.isA(YukonDevice.class), EasyMock.eq("New Name"));
        EasyMock.expectLastCall();
        EasyMock.replay(deviceDao);
        
        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, null, paoLocationDao);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        ReflectionTestUtils.setField(service, "deviceDao", deviceDao);
        ReflectionTestUtils.setField(service, "cache", cache);
        
        RfnGatewayData rfnGatewayData = createEmptyRfnGatewayData(gatewayRfnId);
        RfnGateway rfnGateway = new RfnGateway("New Name", gatewayPaoId, gatewayRfnId, rfnGatewayData);
        rfnGateway.setLocation(location);
        
        Assert.assertTrue("Failed to update gateway", service.updateGateway(rfnGateway));
    }
    
    @Test
    public void test_updateGateway_successResponse() throws NetworkManagerCommunicationException {
        RfnDevice gwDevice = createRfnDevice(gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        // Expecting a call to retrieve PAO name.
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        // Expecting call to retrieve gateway data.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        // Expecting forced refresh of cached data.
        gatewayDataCache.remove(gatewayPaoId);
        EasyMock.expectLastCall();
        // Should be returning the updated data, but return value not used so we are just creating an empty one.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.replay(gatewayDataCache);
        
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        // Expecting call to retrieve existing location.
        EasyMock.expect(paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId())).andReturn(null);
        // Expecting new location to be saved.
        PaoLocation location = new PaoLocation();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        paoLocationDao.save(location);
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        // Expecting device name to be changed.
        deviceDao.changeName(EasyMock.isA(YukonDevice.class), EasyMock.eq("New Name"));
        EasyMock.expectLastCall();
        EasyMock.replay(deviceDao);
        
        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, null, paoLocationDao);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        ReflectionTestUtils.setField(service, "deviceDao", deviceDao);
        ReflectionTestUtils.setField(service, "cache", cache);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);

        GatewayDataResponse gatewayDataResponse = new GatewayDataResponse();
        gatewayDataResponse.setRfnIdentifier(gatewayRfnId);
        gatewayDataResponse.setIpAddress(ipAddress);
        gatewayDataResponse.setAdmin(admin);
        gatewayDataResponse.setSuperAdmin(superAdmin);
        RfnGatewayData rfnGatewayData = new RfnGatewayData(gatewayDataResponse);
        RfnGateway rfnGateway = new RfnGateway("New Name", gatewayPaoId, gatewayRfnId, rfnGatewayData);
        rfnGateway.setLocation(location);
        
        Assert.assertTrue("Failed to update gateway", service.updateGateway(rfnGateway));
    }
    
    @Test
    public void test_updateGateway_failedResponse() throws NetworkManagerCommunicationException {
        RfnDevice gwDevice = createRfnDevice(gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        // Expecting a call to retrieve PAO name.
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        // Expecting call to retrieve gateway data.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        // Expecting forced refresh of cached data.
        gatewayDataCache.remove(gatewayPaoId);
        EasyMock.expectLastCall();
        // Should be returning the updated data, but return value not used so we are just creating an empty one.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.replay(gatewayDataCache);
        
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        // Expecting call to retrieve existing location.
        EasyMock.expect(paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId())).andReturn(null);
        EasyMock.replay(paoLocationDao);
        
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        // Expect no changes
        EasyMock.replay(deviceDao);
        
        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, null, paoLocationDao);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        ReflectionTestUtils.setField(service, "deviceDao", deviceDao);
        ReflectionTestUtils.setField(service, "cache", cache);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        fakeTemplate.setGatewayUpdateResult(GatewayUpdateResult.FAILED);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        GatewayDataResponse gatewayDataResponse = new GatewayDataResponse();
        gatewayDataResponse.setRfnIdentifier(gatewayRfnId);
        gatewayDataResponse.setIpAddress(ipAddress);
        gatewayDataResponse.setAdmin(admin);
        gatewayDataResponse.setSuperAdmin(superAdmin);
        RfnGatewayData rfnGatewayData = new RfnGatewayData(gatewayDataResponse);
        RfnGateway rfnGateway = new RfnGateway("New Name", gatewayPaoId, gatewayRfnId, rfnGatewayData);
        
        PaoLocation location = new PaoLocation();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        
        rfnGateway.setLocation(location);
        
        Assert.assertFalse("Unexpected gateway update", service.updateGateway(rfnGateway));
    }
    
    @Test(expected=NetworkManagerCommunicationException.class)
    public void test_updateGateway_NetworkManagerCommunicationException() throws NetworkManagerCommunicationException {
        RfnDevice gwDevice = createRfnDevice(gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        // Expecting a call to retrieve PAO name.
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        // Expecting call to retrieve gateway data.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        // Expecting forced refresh of cached data.
        gatewayDataCache.remove(gatewayPaoId);
        EasyMock.expectLastCall();
        // Should be returning the updated data, but return value not used so we are just creating an empty one.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.replay(gatewayDataCache);
        
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        // Expecting call to retrieve existing location.
        EasyMock.expect(paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId())).andReturn(null);
        EasyMock.replay(paoLocationDao);
        
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        // Expect no changes
        EasyMock.replay(deviceDao);
        
        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, null, paoLocationDao);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        ReflectionTestUtils.setField(service, "deviceDao", deviceDao);
        ReflectionTestUtils.setField(service, "cache", cache);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.EXCEPTION);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        GatewayDataResponse gatewayDataResponse = new GatewayDataResponse();
        gatewayDataResponse.setRfnIdentifier(gatewayRfnId);
        gatewayDataResponse.setIpAddress(ipAddress);
        gatewayDataResponse.setAdmin(admin);
        gatewayDataResponse.setSuperAdmin(superAdmin);
        RfnGatewayData rfnGatewayData = new RfnGatewayData(gatewayDataResponse);
        RfnGateway rfnGateway = new RfnGateway("New Name", gatewayPaoId, gatewayRfnId, rfnGatewayData);
        
        PaoLocation location = new PaoLocation();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        
        rfnGateway.setLocation(location);
        
        service.updateGateway(rfnGateway);
    }
    
    @Test
    public void test_deleteGateway_successResponse() throws NetworkManagerCommunicationException {
        RfnDevice gwDevice = createRfnDevice(gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        // Expecting device to be deleted through deviceDao.
        deviceDao.removeDevice(gwDevice);
        EasyMock.expectLastCall();
        EasyMock.replay(deviceDao);
        
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        // Expecting device to be removed from cache.
        gatewayDataCache.remove(gatewayPaoId);
        EasyMock.expectLastCall();
        EasyMock.replay(gatewayDataCache);
        
        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, null, null);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        ReflectionTestUtils.setField(service, "deviceDao", deviceDao);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        Assert.assertTrue("Failed to delete gateway", service.deleteGateway(gatewayPaoId));
    }
    
    @Test
    public void test_deleteGateway_failedResponse() throws NetworkManagerCommunicationException {
        RfnDevice gwDevice = createRfnDevice(gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        // Expecting no calls (no devices removed).
        EasyMock.replay(deviceDao);
        
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        // Expecting no calls (no gateways removed from cache).
        EasyMock.replay(gatewayDataCache);
        
        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, null, null);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        ReflectionTestUtils.setField(service, "deviceDao", deviceDao);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        fakeTemplate.setGatewayUpdateResult(GatewayUpdateResult.FAILED);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        Assert.assertFalse("Gateway unexpectedly deleted", service.deleteGateway(gatewayPaoId));
    }
    
    @Test(expected=NetworkManagerCommunicationException.class)
    public void test_deleteGateway_NetworkManagerCommunicationException() throws NetworkManagerCommunicationException {
        RfnDevice gwDevice = createRfnDevice(gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        // Expecting no calls (no devices removed).
        EasyMock.replay(deviceDao);
        
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        // Expecting no calls (no gateways removed from cache).
        EasyMock.replay(gatewayDataCache);
        
        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, null, null);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        ReflectionTestUtils.setField(service, "deviceDao", deviceDao);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.EXCEPTION);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        service.deleteGateway(gatewayPaoId);
    }
    
    /**
     * Fake template that can be configured to reply with an appropriate object or throw exceptions.
     */
    private static class FakeUpdateRequestReplyTemplate extends FakeRequestReplyTemplate<GatewayUpdateResponse> {
        private GatewayUpdateResult gatewayUpdateResult = GatewayUpdateResult.SUCCESSFUL;
        
        public void setGatewayUpdateResult(GatewayUpdateResult newResult) {
            this.gatewayUpdateResult = newResult;
        }
        
        @Override
        protected <Q extends Serializable> GatewayUpdateResponse buildResponse(Q request) {
            GatewayUpdateResponse response = new GatewayUpdateResponse();
            response.setResult(gatewayUpdateResult);
            response.setRfnIdentifier(gatewayRfnId);
            return response;
        }
    }
    
    private static class EmptyPaoLocationDao implements PaoLocationDao {
        @Override
        public Set<PaoLocation> getLocations(Iterable<? extends YukonPao> paos) {
            return new HashSet<PaoLocation>();
        }

        @Override
        public PaoLocation getLocation(int paoId) {
            return null;
        }

        @Override
        public void save(PaoLocation location) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveAll(Iterable<PaoLocation> locations) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<PaoLocation> getAllLocations() {
            throw new UnsupportedOperationException();
        }
    }
}
