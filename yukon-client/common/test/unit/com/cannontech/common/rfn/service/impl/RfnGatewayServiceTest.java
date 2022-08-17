package com.cannontech.common.rfn.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.EndpointEventLogService;
import com.cannontech.common.mock.FakeRequestReplyTemplate;
import com.cannontech.common.mock.FakeRequestReplyTemplate.Mode;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.pao.model.PaoLocationDetails;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.message.location.Origin;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.model.RfnGwy800;
import com.cannontech.common.rfn.model.RfnVirtualGateway;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class RfnGatewayServiceTest {
    
    private RfnGatewayServiceImpl service;
    private Map<Integer, LiteYukonPAObject> paos;
    
    //Gateway data
    private GatewaySettings settings;
    private Authentication admin;
    private Authentication superAdmin;
    private Double latitude = 1.0;
    private Double longitude = 2.0;
    private final String ipAddress = "123.123.123.123";
    private final String gatewayName = "Test Gateway";
    private final PaoIdentifier gatewayPaoId = new PaoIdentifier(100, PaoType.RFN_GATEWAY);
    private static final RfnIdentifier gatewayRfnId = new RfnIdentifier("10000", "CPS", "RFGateway");
    
    //Gateway 2.0 data
    private GatewaySettings settings2;
    private Authentication admin2;
    private Authentication superAdmin2;
    private Double latitude2 = 3.0;
    private Double longitude2 = 4.0;
    private final String ipAddress2 = "123.123.123.124";
    private final static String gateway2Name = "Test Gateway 2";
    private final PaoIdentifier gateway2PaoId = new PaoIdentifier(101, PaoType.GWY800);
    private static final RfnIdentifier gateway2RfnId = new RfnIdentifier("10001", "CPS", "RFGateway2");
    
    //WIFI Super Meter Virtual Gateway
    private GatewaySettings settings3;
    private Authentication admin3;
    private Authentication superAdmin3;
    private Double latitude3 = 5.0;
    private Double longitude3 = 6.0;
    private final String ipAddress3 = "123.123.123.125";
    private final Integer port3 = 32035;
    private final static String gateway3Name = "Test Gateway 3";
    private final PaoIdentifier gateway3PaoId = new PaoIdentifier(102, PaoType.VIRTUAL_GATEWAY);
    private static final RfnIdentifier gateway3RfnId = new RfnIdentifier("10002", "CPS", "VGW");
    
    @Before
    public void init() {
        
        //Set up gateway
        admin = new Authentication();
        admin.setUsername("testAdmin");
        admin.setPassword("testAdminPass");
        superAdmin = new Authentication();
        superAdmin.setUsername("testSuperAdmin");
        superAdmin.setPassword("testSuperAdminPass");
        settings = new GatewaySettings();
        settings.setName(gatewayName);
        settings.setAdmin(admin);
        settings.setSuperAdmin(superAdmin);
        settings.setLatitude(latitude);
        settings.setLongitude(longitude);
        LiteYukonPAObject gwPao = new LiteYukonPAObject(gatewayPaoId.getPaoId());
        gwPao.setPaoName(gatewayName);
        
        //Set up gateway 2.0
        admin2 = new Authentication();
        admin2.setUsername("testAdmin2");
        admin2.setPassword("testAdminPass2");
        superAdmin2 = new Authentication();
        superAdmin2.setUsername("testSuperAdmin2");
        superAdmin2.setPassword("testSuperAdminPass2");
        settings2 = new GatewaySettings();
        settings2.setName(gateway2Name);
        settings2.setAdmin(admin2);
        settings2.setSuperAdmin(superAdmin2);
        settings2.setLatitude(latitude2);
        settings2.setLongitude(longitude2);
        LiteYukonPAObject gw2Pao = new LiteYukonPAObject(gateway2PaoId.getPaoId());
        gw2Pao.setPaoName(gateway2Name);
        
        //Set up WIFI Super Meter Virtual Gateway
        admin3 = new Authentication();
        admin3.setUsername("testAdmin3");
        admin3.setPassword("testAdminPass3");
        superAdmin3 = new Authentication();
        superAdmin3.setUsername("testSuperAdmin3");
        superAdmin3.setPassword("testSuperAdminPass3");
        settings3 = new GatewaySettings();
        settings3.setName(gateway3Name);
        settings3.setAdmin(admin3);
        settings3.setSuperAdmin(superAdmin3);
        settings3.setLatitude(latitude3);
        settings3.setLongitude(longitude3);
        LiteYukonPAObject gw3Pao = new LiteYukonPAObject(gateway3PaoId.getPaoId());
        gw3Pao.setPaoName(gateway3Name);
        
        paos = new HashMap<>();
        paos.put(gatewayPaoId.getPaoId(), gwPao);
        paos.put(gateway2PaoId.getPaoId(), gw2Pao);
        paos.put(gateway3PaoId.getPaoId(), gw3Pao);
    }
    
    private static RfnGatewayData createEmptyRfnGatewayData(RfnIdentifier rfnIdentifier) {
        GatewayDataResponse gatewayDataResponse = new GatewayDataResponse();
        gatewayDataResponse.setRfnIdentifier(rfnIdentifier);
        return new RfnGatewayData(gatewayDataResponse, gateway2Name);
    }
    
    private static RfnDevice createRfnDevice(String name, final PaoIdentifier paoIdentifier, RfnIdentifier rfnIdentifier) {
        return new RfnDevice(name, new YukonPao() {
            @Override
            public PaoIdentifier getPaoIdentifier() {
                return paoIdentifier;
            }
        }, rfnIdentifier);
    }
    
    @Test
    public void test_getAllGateways() {
        // Setup gateway and gateway 2.0 RfnDevices.
        List<RfnDevice> rfnDevices = new ArrayList<>();
        rfnDevices.add(createRfnDevice(gatewayName, gatewayPaoId, gatewayRfnId)); //Gateway
        rfnDevices.add(createRfnDevice(gateway2Name, gateway2PaoId, gateway2RfnId)); //Gateway 2.0
        rfnDevices.add(createRfnDevice(gateway3Name, gateway3PaoId, gateway3RfnId)); //WIFI Super Meter Virtual Gateway
        
        // Expect a call to retrieve all gateway devices from rfnDeviceDao.
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfGatewayTypes()))
                .andReturn(rfnDevices);
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name for each gateway.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve gateway data for each gateway rfnDevice.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.getIfPresent(gatewayPaoId))
                .andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.expect(gatewayDataCache.getIfPresent(gateway2PaoId))
                .andReturn(createEmptyRfnGatewayData(gateway2RfnId));
        EasyMock.expect(gatewayDataCache.getIfPresent(gateway3PaoId))
                .andReturn(createEmptyRfnGatewayData(gateway3RfnId));
        EasyMock.replay(gatewayDataCache);
        
        PaoLocationDao paoLocationDao = new EmptyPaoLocationDao();
        
        // Do the service call
        service = new RfnGatewayServiceImpl(null, null, null, null, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        Set<RfnGateway> allGateways = service.getAllGateways();
        
        // Test that we got the expected number of gateways from the service
        Assert.assertEquals("Expecting 3 gateways", 3, allGateways.size());
        
        // Test that we got the expected values
        RfnGateway rfnGateway = new RfnGateway(gatewayName, gatewayPaoId, gatewayRfnId, createEmptyRfnGatewayData(gatewayRfnId));
        RfnGwy800 rfnGateway2 = new RfnGwy800(gateway2Name, gateway2PaoId, gateway2RfnId, createEmptyRfnGatewayData(gateway2RfnId));
        RfnVirtualGateway rfnGateway3 = new RfnVirtualGateway(gateway3Name, gateway3PaoId, gateway3RfnId, createEmptyRfnGatewayData(gateway3RfnId));
        Assert.assertTrue("Gateway not retrieved from getAllGateways()", allGateways.contains(rfnGateway));
        Assert.assertTrue("Gateway 2.0 not retrieved from getAllGateways()", allGateways.contains(rfnGateway2));
        Assert.assertTrue("WIFI Super Meter Virtual Gateway not retrieved from getAllGateways()", allGateways.contains(rfnGateway3));
    }
    
    @Test
    public void test_getGatewayByPaoId() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao.
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId()))
                .andReturn(createRfnDevice(gatewayName, gatewayPaoId, gatewayRfnId));
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap())
                .andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve PaoLocation for this pao ID.
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        PaoLocation paoLocation = new PaoLocation(gatewayPaoId, latitude, longitude);
        EasyMock.expect(paoLocationDao.getLocation(gatewayPaoId.getPaoId()))
                .andReturn(paoLocation);
        EasyMock.replay(paoLocationDao);
        
        // Expect a call to retrieve gateway data for this pao ID.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId))
                .andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.replay(gatewayDataCache);
        
        // Do the service call
        service = new RfnGatewayServiceImpl(null, null, null, null, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        RfnGateway rfnGateway = service.getGatewayByPaoIdWithData(gatewayPaoId.getPaoId());
        
        // Test that we got the expected values
        Assert.assertEquals("PaoIdentifier does not match", gatewayPaoId, rfnGateway.getPaoIdentifier());
        Assert.assertEquals("RfnIdentifier does not match", gatewayRfnId, rfnGateway.getRfnIdentifier());
        Assert.assertEquals("PaoLocation does not match", paoLocation, rfnGateway.getLocation());
    }
    
    @Test
    public void test_getGatewayByPaoId_withGateway2() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao.
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gateway2PaoId.getPaoId()))
                .andReturn(createRfnDevice(gateway2Name, gateway2PaoId, gateway2RfnId));
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap())
                .andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve PaoLocation for this pao ID.
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        PaoLocation paoLocation = new PaoLocation(gateway2PaoId, latitude2, longitude2);
        EasyMock.expect(paoLocationDao.getLocation(gateway2PaoId.getPaoId()))
                .andReturn(paoLocation);
        EasyMock.replay(paoLocationDao);
        
        // Expect a call to retrieve gateway data for this pao ID.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gateway2PaoId))
                .andReturn(createEmptyRfnGatewayData(gateway2RfnId));
        EasyMock.replay(gatewayDataCache);
        
        // Do the service call
        service = new RfnGatewayServiceImpl(null, null, null, null, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        RfnGateway rfnGateway = service.getGatewayByPaoIdWithData(gateway2PaoId.getPaoId());
        
        // Test that we got the expected values
        Assert.assertEquals("PaoIdentifier does not match", gateway2PaoId, rfnGateway.getPaoIdentifier());
        Assert.assertEquals("RfnIdentifier does not match", gateway2RfnId, rfnGateway.getRfnIdentifier());
        Assert.assertEquals("PaoLocation does not match", paoLocation, rfnGateway.getLocation());
        Assert.assertTrue("Gateway 2.0 returned as incorrect type.", rfnGateway instanceof RfnGwy800);
    }
    
    @Test
    public void test_getGatewayByPaoId_withGateway3() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao.
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gateway3PaoId.getPaoId()))
                .andReturn(createRfnDevice(gateway3Name, gateway3PaoId, gateway3RfnId));
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap())
                .andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve PaoLocation for this pao ID.
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        PaoLocation paoLocation = new PaoLocation(gateway3PaoId, latitude3, longitude3);
        EasyMock.expect(paoLocationDao.getLocation(gateway3PaoId.getPaoId()))
                .andReturn(paoLocation);
        EasyMock.replay(paoLocationDao);
        
        // Expect a call to retrieve gateway data for this pao ID.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gateway3PaoId))
                .andReturn(createEmptyRfnGatewayData(gateway3RfnId));
        EasyMock.replay(gatewayDataCache);
        
        // Do the service call
        service = new RfnGatewayServiceImpl(null, null, null, null, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        RfnGateway rfnGateway = service.getGatewayByPaoIdWithData(gateway3PaoId.getPaoId());
        
        // Test that we got the expected values
        Assert.assertEquals("PaoIdentifier does not match", gateway3PaoId, rfnGateway.getPaoIdentifier());
        Assert.assertEquals("RfnIdentifier does not match", gateway3RfnId, rfnGateway.getRfnIdentifier());
        Assert.assertEquals("PaoLocation does not match", paoLocation, rfnGateway.getLocation());
        Assert.assertTrue("WIFI Super Meter Virtual Gateway returned as incorrect type.", rfnGateway instanceof RfnVirtualGateway);
    }
    
    @Test(expected=NotFoundException.class)
    public void test_getGatewayByPaoId_NotFoundException() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao.
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId()))
                .andThrow(new NotFoundException("Unknown rfn device Id " + gatewayPaoId.getPaoId()));
        EasyMock.replay(rfnDeviceDao);
        
        // Do the service call. NotFoundException should be thrown.
        service = new RfnGatewayServiceImpl(null, null, null, null, null, null, null, rfnDeviceDao, null);
        service.getGatewayByPaoIdWithData(gatewayPaoId.getPaoId());
    }
    
    @Test(expected=NmCommunicationException.class)
    public void test_getGatewayByPaoId_communicationError() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao (return value not used in this test).
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId()))
                .andReturn(createRfnDevice(gatewayName, gatewayPaoId, gatewayRfnId));
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap())
                .andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve gateway data for this pao ID
        // Throw exception to simulate communication error.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andThrow(new NmCommunicationException("Communication Error"));
        EasyMock.replay(gatewayDataCache);
        
        // Do the service call. NmCommunicationException should be thrown
        PaoLocationDao paoLocationDao = new EmptyPaoLocationDao();
        service = new RfnGatewayServiceImpl(null, null, null, null, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        service.getGatewayByPaoIdWithData(gatewayPaoId.getPaoId());
    }
    
    @Test(expected=NmCommunicationException.class)
    public void test_createGateway_communicationError() throws NmCommunicationException, GatewayUpdateException {
        
        // Dependencies can be null, because they're not called in this test scenario
        service = new RfnGatewayServiceImpl(null, null, null, null, null, null, null, null, null);
        
        // Set up the template to call exceptionThrown() on the callback.
        // This simulates a communication/network error.
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.EXCEPTION);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Do the service call
        service.createGateway(settings, null);
    }
    
    @Test(expected=NmCommunicationException.class)
    public void test_createGateway_timeoutError() throws NmCommunicationException, GatewayUpdateException {
        
        // Dependencies can be null, because they're not called in this test scenario
        service = new RfnGatewayServiceImpl(null, null, null, null, null, null, null, null, null);
        
        // Set up the template to call handleTimeout() on the callback.
        // This simulates a timeout, which gets wrapped in a communication exception.
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.TIMEOUT);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Do the service call
        service.createGateway(settings, null);
    }
    
    @Test(expected=GatewayUpdateException.class)
    public void test_createGateway_failedResponse() throws NmCommunicationException, GatewayUpdateException {
        
        // Dependencies can be null, because they're not called in this test scenario
        service = new RfnGatewayServiceImpl(null, null, null, null, null, null, null, null, null);
        
        // Set up the template to reply with a "failed" result.
        // This causes a GatewayUpdateException to be thrown.
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setGatewayUpdateResult(GatewayUpdateResult.FAILED);
        fakeTemplate.setMode(Mode.REPLY);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Do the service call
        service.createGateway(settings, null);
    }
    
    @Test
    public void test_createGateway_successResponse() throws NmCommunicationException, GatewayUpdateException {
        
        // Expect a call to create a gateway
        RfnDeviceCreationService rfnDeviceCreationService = EasyMock.createStrictMock(RfnDeviceCreationService.class);
        EasyMock.expect(rfnDeviceCreationService.createGateway(gatewayName, gatewayRfnId))
                .andReturn(new RfnDevice(gatewayName, gatewayPaoId, gatewayRfnId));
        EasyMock.replay(rfnDeviceCreationService);
        
        // Expect a call to retrieve gateway data from the cache
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId))
                .andReturn(null); //return value not used in this case
        EasyMock.replay(gatewayDataCache);
        
        // Expect a call to save location data for the gateway
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        Capture<PaoLocation> paoLocationArg = new Capture<>();
        paoLocationDao.save(EasyMock.capture(paoLocationArg));
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        // ConnectionFactory and configurationSource can be null - they are only used by the RequestReplyTemplate,
        // which is replaced by the FakeUpdateRequestReplyTemplate in this test
        EndpointEventLogService eventLog = EasyMock.createNiceMock(EndpointEventLogService.class);
        service = new RfnGatewayServiceImpl(null, null, null, eventLog, null, paoLocationDao, rfnDeviceCreationService, null, gatewayDataCache);
        
        // Set up the template to reply with a "success" result.
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Do the service call
        service.createGateway(settings, null);
        
        Assert.assertEquals("Incorrect paoId saved.", gatewayPaoId, paoLocationArg.getValue().getPaoIdentifier());
        Assert.assertEquals("Incorrect latitude saved.", latitude, new Double(paoLocationArg.getValue().getLatitude()));
        Assert.assertEquals("Incorrect longitude saved.", longitude, new Double(
            paoLocationArg.getValue().getLongitude()));
    }
    
    @Test
    public void test_createGateway_successResponseWithGateway2() throws NmCommunicationException, GatewayUpdateException {
        
        // Expect a call to create a gateway
        RfnDeviceCreationService rfnDeviceCreationService = EasyMock.createStrictMock(RfnDeviceCreationService.class);
        EasyMock.expect(rfnDeviceCreationService.createGateway(gateway2Name, gateway2RfnId))
                .andReturn(new RfnDevice(gateway2Name, gateway2PaoId, gateway2RfnId));
        EasyMock.replay(rfnDeviceCreationService);
        
        // Expect a call to retrieve gateway data from the cache
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gateway2PaoId))
                .andReturn(null); //return value not used in this case
        EasyMock.replay(gatewayDataCache);
                
        // Expect a call to save location data for the gateway
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        Capture<PaoLocation> paoLocationArg = new Capture<>();
        paoLocationDao.save(EasyMock.capture(paoLocationArg));
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        // ConnectionFactory and configurationSource can be null - they are only used by the RequestReplyTemplate,
        // which is replaced by the FakeUpdateRequestReplyTemplate in this test
        EndpointEventLogService eventLog = EasyMock.createNiceMock(EndpointEventLogService.class);
        service = new RfnGatewayServiceImpl(null, null, null, eventLog, null, paoLocationDao, rfnDeviceCreationService, null, gatewayDataCache);
        
        // Set up the template to reply with a "success" result.
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        fakeTemplate.setResponseRfnIdentifier(gateway2RfnId);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Do the service call
        service.createGateway(settings2, null);
      
        Assert.assertEquals("Incorrect paoId saved.", gateway2PaoId, paoLocationArg.getValue().getPaoIdentifier());
        Assert.assertEquals("Incorrect latitude saved.", latitude2, new Double(paoLocationArg.getValue().getLatitude()));
        Assert.assertEquals("Incorrect longitude saved.", longitude2, new Double(
            paoLocationArg.getValue().getLongitude()));
    }
    
    @Test
    public void test_createGateway_successResponseWithGateway3() throws NmCommunicationException, GatewayUpdateException {
        
        // Expect a call to create a gateway
        RfnDeviceCreationService rfnDeviceCreationService = EasyMock.createStrictMock(RfnDeviceCreationService.class);
        EasyMock.expect(rfnDeviceCreationService.createGateway(gateway3Name, gateway3RfnId))
                .andReturn(new RfnDevice(gateway3Name, gateway3PaoId, gateway3RfnId));
        EasyMock.replay(rfnDeviceCreationService);
        
        // Expect a call to retrieve gateway data from the cache
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gateway3PaoId))
                .andReturn(null); //return value not used in this case
        EasyMock.replay(gatewayDataCache);
                
        // Expect a call to save location data for the gateway
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        Capture<PaoLocation> paoLocationArg = new Capture<>();
        paoLocationDao.save(EasyMock.capture(paoLocationArg));
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        // ConnectionFactory and configurationSource can be null - they are only used by the RequestReplyTemplate,
        // which is replaced by the FakeUpdateRequestReplyTemplate in this test
        EndpointEventLogService eventLog = EasyMock.createNiceMock(EndpointEventLogService.class);
        service = new RfnGatewayServiceImpl(null, null, null, eventLog, null, paoLocationDao, rfnDeviceCreationService, null, gatewayDataCache);
        
        // Set up the template to reply with a "success" result.
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        fakeTemplate.setResponseRfnIdentifier(gateway3RfnId);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Do the service call
        service.createGateway(settings3, null);
      
        Assert.assertEquals("Incorrect paoId saved.", gateway3PaoId, paoLocationArg.getValue().getPaoIdentifier());
        Assert.assertEquals("Incorrect latitude saved.", latitude3, new Double(paoLocationArg.getValue().getLatitude()));
        Assert.assertEquals("Incorrect longitude saved.", longitude3, new Double(
            paoLocationArg.getValue().getLongitude()));
    }
    
    @Test
    public void test_updateGateway_updateLocalSuccess() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        RfnDevice gwDevice = createRfnDevice(gatewayName, gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId()))
                .andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve gateway data from the cache.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId))
                .andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.replay(gatewayDataCache);
        
        // Expect a call to retrieve existing location.
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        EasyMock.expect(paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId()))
                .andReturn(null);
        
        // Expect new location to be saved.
        PaoLocation location = new PaoLocation(null, latitude, longitude);
        paoLocationDao.save(location);
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        // Expect device name to be changed.
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        deviceDao.changeName(EasyMock.isA(YukonDevice.class), EasyMock.eq("New Name"));
        EasyMock.expectLastCall();
        EasyMock.replay(deviceDao);
        
        // Build the RfnGateway object
        RfnGatewayData rfnGatewayData = createEmptyRfnGatewayData(gatewayRfnId);
        RfnGateway rfnGateway = new RfnGateway("New Name", gatewayPaoId, gatewayRfnId, rfnGatewayData);
        rfnGateway.setLocation(location);
        
        // Do the service call
        EndpointEventLogService eventLog = EasyMock.createNiceMock(EndpointEventLogService.class);
        service = new RfnGatewayServiceImpl(null, null, deviceDao, eventLog, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        GatewayUpdateResult result = service.updateGateway(rfnGateway, null);
        Assert.assertEquals("Failed to update gateway", GatewayUpdateResult.SUCCESSFUL, result);
    }
    
    @Test
    public void test_updateGateway_updateLocalSuccessWithGateway2() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        RfnDevice gwDevice = createRfnDevice(gateway2Name, gateway2PaoId, gateway2RfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gateway2PaoId.getPaoId()))
                .andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve gateway data from the cache.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gateway2PaoId))
                .andReturn(createEmptyRfnGatewayData(gateway2RfnId));
        EasyMock.replay(gatewayDataCache);
        
        // Expect a call to retrieve existing location.
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        EasyMock.expect(paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId()))
                .andReturn(null);
        
        // Expect new location to be saved.
        PaoLocation location = new PaoLocation(null, latitude2, longitude2);
        paoLocationDao.save(location);
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        // Expect device name to be changed.
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        deviceDao.changeName(EasyMock.isA(YukonDevice.class), EasyMock.eq("New Name"));
        EasyMock.expectLastCall();
        EasyMock.replay(deviceDao);
        
        // Build the RfnGateway object
        RfnGatewayData rfnGatewayData = createEmptyRfnGatewayData(gateway2RfnId);
        RfnGateway rfnGateway = new RfnGateway("New Name", gateway2PaoId, gateway2RfnId, rfnGatewayData);
        rfnGateway.setLocation(location);
        
        // Do the service call 
        EndpointEventLogService eventLog = EasyMock.createNiceMock(EndpointEventLogService.class);
        service = new RfnGatewayServiceImpl(null, null, deviceDao, eventLog, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        GatewayUpdateResult result = service.updateGateway(rfnGateway, null);
        Assert.assertEquals("Failed to update gateway", GatewayUpdateResult.SUCCESSFUL, result);
    }
    
    @Test
    public void test_updateGateway_updateLocalSuccessWithGateway3() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        RfnDevice gwDevice = createRfnDevice(gateway3Name, gateway3PaoId, gateway3RfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gateway3PaoId.getPaoId()))
                .andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve gateway data from the cache.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gateway3PaoId))
                .andReturn(createEmptyRfnGatewayData(gateway3RfnId));
        EasyMock.replay(gatewayDataCache);
        
        // Expect a call to retrieve existing location.
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        EasyMock.expect(paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId()))
                .andReturn(null);
        
        // Expect new location to be saved.
        PaoLocation location = new PaoLocation(null, latitude3, longitude3);
        paoLocationDao.save(location);
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        // Expect device name to be changed.
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        deviceDao.changeName(EasyMock.isA(YukonDevice.class), EasyMock.eq("New Name"));
        EasyMock.expectLastCall();
        EasyMock.replay(deviceDao);
        
        // Build the RfnGateway object
        RfnGatewayData rfnGatewayData = createEmptyRfnGatewayData(gateway3RfnId);
        RfnGateway rfnGateway = new RfnGateway("New Name", gateway3PaoId, gateway3RfnId, rfnGatewayData);
        rfnGateway.setLocation(location);
        
        // Do the service call 
        EndpointEventLogService eventLog = EasyMock.createNiceMock(EndpointEventLogService.class);
        service = new RfnGatewayServiceImpl(null, null, deviceDao, eventLog, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        GatewayUpdateResult result = service.updateGateway(rfnGateway, null);
        Assert.assertEquals("Failed to update gateway", GatewayUpdateResult.SUCCESSFUL, result);
    }
    
    //TODO Gateway 2.0 support?
    @Test
    public void test_updateGateway_successResponse() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        RfnDevice gwDevice = createRfnDevice(gatewayName, gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId()))
                .andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve gateway data from the cache.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        
        // Expect forced refresh of cached data.
        gatewayDataCache.remove(gatewayPaoId);
        EasyMock.expectLastCall();
        
        // Should be returning the updated data, but the return value is not used, so we are just creating an empty one.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId))
                .andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.replay(gatewayDataCache);
        
        // Expect call to retrieve existing location.
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        EasyMock.expect(paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId()))
                .andReturn(null);
        
        // Expect new location to be saved.
        PaoLocation location = new PaoLocation(null, latitude, longitude);
        paoLocationDao.save(location);
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        // Expect device name to be changed.
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        deviceDao.changeName(EasyMock.isA(YukonDevice.class), EasyMock.eq("New Name"));
        EasyMock.expectLastCall();
        EasyMock.replay(deviceDao);
        
        // Inject all the mocks into the service
        EndpointEventLogService eventLog = EasyMock.createNiceMock(EndpointEventLogService.class);
        service = new RfnGatewayServiceImpl(null, null, deviceDao, eventLog, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        
        // Set up the template to reply with a "success" result.
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Build the RfnGateway object
        GatewayDataResponse gatewayDataResponse = new GatewayDataResponse();
        gatewayDataResponse.setRfnIdentifier(gatewayRfnId);
        gatewayDataResponse.setIpAddress(ipAddress);
        gatewayDataResponse.setAdmin(admin);
        gatewayDataResponse.setSuperAdmin(superAdmin);
        RfnGatewayData rfnGatewayData = new RfnGatewayData(gatewayDataResponse, gatewayName);
        RfnGateway rfnGateway = new RfnGateway("New Name", gatewayPaoId, gatewayRfnId, rfnGatewayData);
        rfnGateway.setLocation(location);
        
        //Do the service call
        GatewayUpdateResult result = service.updateGateway(rfnGateway, null);
        Assert.assertEquals("Failed to update gateway", GatewayUpdateResult.SUCCESSFUL, result);
    }
    
    @Test
    public void test_updateGateway_successResponseWithGateway2() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        RfnDevice gwDevice = createRfnDevice(gateway2Name, gateway2PaoId, gateway2RfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gateway2PaoId.getPaoId()))
                .andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve gateway data from the cache.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gateway2PaoId)).andReturn(createEmptyRfnGatewayData(gateway2RfnId));
        
        // Expect forced refresh of cached data.
        gatewayDataCache.remove(gateway2PaoId);
        EasyMock.expectLastCall();
        
        // Should be returning the updated data, but the return value is not used, so we are just creating an empty one.
        EasyMock.expect(gatewayDataCache.get(gateway2PaoId))
                .andReturn(createEmptyRfnGatewayData(gateway2RfnId));
        EasyMock.replay(gatewayDataCache);
        
        // Expect call to retrieve existing location.
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        EasyMock.expect(paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId()))
                .andReturn(null);
        
        // Expect new location to be saved.
        PaoLocation location = new PaoLocation(null, latitude2, longitude2);
        paoLocationDao.save(location);
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        // Expect device name to be changed.
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        deviceDao.changeName(EasyMock.isA(YukonDevice.class), EasyMock.eq("New Name"));
        EasyMock.expectLastCall();
        EasyMock.replay(deviceDao);
        
        // Inject all the mocks into the service
        EndpointEventLogService eventLog = EasyMock.createNiceMock(EndpointEventLogService.class);
        service = new RfnGatewayServiceImpl(null, null, deviceDao, eventLog, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        
        // Set up the template to reply with a "success" result.
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Build the RfnGateway object
        GatewayDataResponse gatewayDataResponse = new GatewayDataResponse();
        gatewayDataResponse.setRfnIdentifier(gateway2RfnId);
        gatewayDataResponse.setIpAddress(ipAddress2);
        gatewayDataResponse.setAdmin(admin2);
        gatewayDataResponse.setSuperAdmin(superAdmin2);
        RfnGatewayData rfnGatewayData = new RfnGatewayData(gatewayDataResponse, gatewayName);
        RfnGateway rfnGateway = new RfnGateway("New Name", gateway2PaoId, gateway2RfnId, rfnGatewayData);
        rfnGateway.setLocation(location);
        
        //Do the service call
        GatewayUpdateResult result = service.updateGateway(rfnGateway, null);
        Assert.assertEquals("Failed to update gateway", GatewayUpdateResult.SUCCESSFUL, result);
    }
    
    @Test
    public void test_updateGateway_successResponseWithGateway3() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        RfnDevice gwDevice = createRfnDevice(gateway3Name, gateway3PaoId, gateway3RfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gateway3PaoId.getPaoId()))
                .andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve gateway data from the cache.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gateway3PaoId)).andReturn(createEmptyRfnGatewayData(gateway3RfnId));
        
        // Expect forced refresh of cached data.
        gatewayDataCache.remove(gateway3PaoId);
        EasyMock.expectLastCall();
        
        // Should be returning the updated data, but the return value is not used, so we are just creating an empty one.
        EasyMock.expect(gatewayDataCache.get(gateway3PaoId))
                .andReturn(createEmptyRfnGatewayData(gateway3RfnId));
        EasyMock.replay(gatewayDataCache);
        
        // Expect call to retrieve existing location.
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        EasyMock.expect(paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId()))
                .andReturn(null);
        
        // Expect new location to be saved.
        PaoLocation location = new PaoLocation(null, latitude3, longitude3);
        paoLocationDao.save(location);
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        // Expect device name to be changed.
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        deviceDao.changeName(EasyMock.isA(YukonDevice.class), EasyMock.eq("New Name"));
        EasyMock.expectLastCall();
        EasyMock.replay(deviceDao);
        
        // Inject all the mocks into the service
        EndpointEventLogService eventLog = EasyMock.createNiceMock(EndpointEventLogService.class);
        service = new RfnGatewayServiceImpl(null, null, deviceDao, eventLog, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        
        // Set up the template to reply with a "success" result.
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Build the RfnGateway object
        GatewayDataResponse gatewayDataResponse = new GatewayDataResponse();
        gatewayDataResponse.setRfnIdentifier(gateway3RfnId);
        gatewayDataResponse.setIpAddress(ipAddress3);
        gatewayDataResponse.setPort(port3.toString());
        gatewayDataResponse.setAdmin(admin3);
        gatewayDataResponse.setSuperAdmin(superAdmin3);
        RfnGatewayData rfnGatewayData = new RfnGatewayData(gatewayDataResponse, gatewayName);
        RfnGateway rfnGateway = new RfnGateway("New Name", gateway3PaoId, gateway3RfnId, rfnGatewayData);
        rfnGateway.setLocation(location);
        
        //Do the service call
        GatewayUpdateResult result = service.updateGateway(rfnGateway, null);
        Assert.assertEquals("Failed to update gateway", GatewayUpdateResult.SUCCESSFUL, result);
    }
    
    @Test
    public void test_updateGateway_failedResponse() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        RfnDevice gwDevice = createRfnDevice(gatewayName, gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve gateway data.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        
        // Expect a forced refresh of cached data.
        gatewayDataCache.remove(gatewayPaoId);
        EasyMock.expectLastCall();
        
        // Should be returning the updated data, but return value not used so we are just creating an empty one.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId))
                .andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.replay(gatewayDataCache);
        
        // Expect a call to retrieve existing location.
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        EasyMock.expect(paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId()))
                .andReturn(null);
        EasyMock.replay(paoLocationDao);
        
        // Expect no calls to the DeviceDao
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.replay(deviceDao);
        
        // Inject all the mocks into the service
        service = new RfnGatewayServiceImpl(null, null, deviceDao, null, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        
        // Set up the template to reply with a "failed" result
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        fakeTemplate.setGatewayUpdateResult(GatewayUpdateResult.FAILED);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Build the RfnGateway object
        GatewayDataResponse gatewayDataResponse = new GatewayDataResponse();
        gatewayDataResponse.setRfnIdentifier(gatewayRfnId);
        gatewayDataResponse.setIpAddress(ipAddress);
        gatewayDataResponse.setAdmin(admin);
        gatewayDataResponse.setSuperAdmin(superAdmin);
        RfnGatewayData rfnGatewayData = new RfnGatewayData(gatewayDataResponse, gatewayName);
        RfnGateway rfnGateway = new RfnGateway("New Name", gatewayPaoId, gatewayRfnId, rfnGatewayData);
        PaoLocation location = new PaoLocation(null, latitude, longitude);
        rfnGateway.setLocation(location);
        
        // Do the service call
        GatewayUpdateResult result = service.updateGateway(rfnGateway, null);
        Assert.assertEquals("Unexpected gateway update", GatewayUpdateResult.FAILED, result);
    }
    
    @Test(expected=NmCommunicationException.class)
    public void test_updateGateway_NetworkManagerCommunicationException() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        RfnDevice gwDevice = createRfnDevice(gatewayName, gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId()))
                .andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        // Expect a call to retrieve PAO name.
        IDatabaseCache cache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(cache.getAllPaosMap()).andReturn(paos);
        EasyMock.replay(cache);
        
        // Expect a call to retrieve gateway data.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        
        // Expect a forced refresh of cached data.
        gatewayDataCache.remove(gatewayPaoId);
        EasyMock.expectLastCall();
        
        // Should be returning the updated data, but return value not used so we are just creating an empty one.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId))
                .andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.replay(gatewayDataCache);
        
        // Expect a call to retrieve existing location.
        PaoLocationDao paoLocationDao = EasyMock.createStrictMock(PaoLocationDao.class);
        EasyMock.expect(paoLocationDao.getLocation(gwDevice.getPaoIdentifier().getPaoId()))
                .andReturn(null);
        EasyMock.replay(paoLocationDao);
        
        // Expect no calls to DeviceDao
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.replay(deviceDao);
        
        // Inject all the mocks into the service
        service = new RfnGatewayServiceImpl(null, null, deviceDao, null, null, paoLocationDao, null, rfnDeviceDao, gatewayDataCache);
        
        // Set up the template to call handleException() on the callback.
        // This simulates a network error.
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.EXCEPTION);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Build the RfnGateway object
        GatewayDataResponse gatewayDataResponse = new GatewayDataResponse();
        gatewayDataResponse.setRfnIdentifier(gatewayRfnId);
        gatewayDataResponse.setIpAddress(ipAddress);
        gatewayDataResponse.setAdmin(admin);
        gatewayDataResponse.setSuperAdmin(superAdmin);
        RfnGatewayData rfnGatewayData = new RfnGatewayData(gatewayDataResponse, gatewayName);
        RfnGateway rfnGateway = new RfnGateway("New Name", gatewayPaoId, gatewayRfnId, rfnGatewayData);
        PaoLocation location = new PaoLocation(null, latitude, longitude);
        rfnGateway.setLocation(location);
        
        // Do the service call.
        // NmCommunicationException should be thrown.
        service.updateGateway(rfnGateway, null);
    }
    
    @Test
    public void test_deleteGateway_successResponse() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        RfnDevice gwDevice = createRfnDevice(gatewayName, gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId()))
                .andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        // Expect the device to be deleted through deviceDao.
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        deviceDao.removeDevice(gwDevice);
        EasyMock.expectLastCall();
        EasyMock.replay(deviceDao);
        
        // Expect the device to be removed from cache.
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        gatewayDataCache.remove(gatewayPaoId);
        EasyMock.expectLastCall();
        EasyMock.replay(gatewayDataCache);
        
        // Inject mocks into the service
        service = new RfnGatewayServiceImpl(null, null, deviceDao, null, null, null, null, rfnDeviceDao, gatewayDataCache);
        
        // Set up template to reply with a "success" response
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Do the service call
        boolean gatewayWasDeleted = service.deleteGateway(gatewayPaoId);
        Assert.assertTrue("Failed to delete gateway", gatewayWasDeleted);
    }
    
    @Test
    public void test_deleteGateway_failedResponse() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        RfnDevice gwDevice = createRfnDevice(gatewayName, gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId()))
                .andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        // Expect no calls (no devices removed).
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.replay(deviceDao);
        
        // Expect no calls (no gateways removed from cache).
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.replay(gatewayDataCache);
        
        // Inject mocks into the service
        
        service = new RfnGatewayServiceImpl(null, null, deviceDao, null, null, null, null, rfnDeviceDao, gatewayDataCache);
        
        // Set up template to reply with a "failed" response
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        fakeTemplate.setGatewayUpdateResult(GatewayUpdateResult.FAILED);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Do the service call
        boolean gatewayWasDeleted = service.deleteGateway(gatewayPaoId);
        Assert.assertFalse("Gateway unexpectedly deleted", gatewayWasDeleted);
    }
    
    @Test(expected=NmCommunicationException.class)
    public void test_deleteGateway_NetworkManagerCommunicationException() throws NmCommunicationException {
        
        // Expect a call to retrieve an RfnDevice from rfnDeviceDao (to get RfnIdentifier).
        RfnDevice gwDevice = createRfnDevice(gatewayName, gatewayPaoId, gatewayRfnId);
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId()))
                .andReturn(gwDevice);
        EasyMock.replay(rfnDeviceDao);
        
        // Expect no calls (no devices removed).
        DeviceDao deviceDao = EasyMock.createStrictMock(DeviceDao.class);
        EasyMock.replay(deviceDao);
        
        // Expect no calls (no gateways removed from cache).
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        EasyMock.replay(gatewayDataCache);
        
        // Inject mocks into the service
        service = new RfnGatewayServiceImpl(null, null, deviceDao, null, null, null, null, rfnDeviceDao, gatewayDataCache);
        
        // Set up template to throw an exception.
        // This simulates a communication error.
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.EXCEPTION);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        // Do the service call.
        // NmCommunicationException should be thrown.
        service.deleteGateway(gatewayPaoId);
    }
    
    /**
     * Fake template that can be configured to reply with an appropriate object or throw exceptions.
     */
    private static class FakeUpdateRequestReplyTemplate extends FakeRequestReplyTemplate<GatewayUpdateResponse> {
        private GatewayUpdateResult gatewayUpdateResult = GatewayUpdateResult.SUCCESSFUL;
        private RfnIdentifier responseRfnIdentifier = gatewayRfnId;
        
        public void setGatewayUpdateResult(GatewayUpdateResult newResult) {
            gatewayUpdateResult = newResult;
        }
        
        @Override
        protected <Q extends Serializable> GatewayUpdateResponse buildResponse(Q request) {
            GatewayUpdateResponse response = new GatewayUpdateResponse();
            response.setResult(gatewayUpdateResult);
            response.setRfnIdentifier(responseRfnIdentifier);
            return response;
        }
        
        public void setResponseRfnIdentifier(RfnIdentifier rfnIdentifier) {
            responseRfnIdentifier = rfnIdentifier;
        }
    }
    
    /** Do-nothing PaoLocationDao stub */
    private static class EmptyPaoLocationDao implements PaoLocationDao {
        @Override
        public Set<PaoLocation> getLocations(Iterable<? extends YukonPao> paos) {
            return new HashSet<>();
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
        public List<PaoLocation> getAllLocations() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void delete(int paoId) {
            throw new UnsupportedOperationException();

        }

        @Override
        public void delete(Origin origin) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void save(List<PaoLocation> location) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<PaoLocation> getLocations(Origin origin) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<PaoLocationDetails> getPaoLocationDetails(List<Integer> paoIds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<PaoLocation> getLocations(Set<Integer> paoIds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<PaoLocation> getLocationsByGateway(Set<Integer> gatewayIds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<SimpleDevice> getDevicesWithoutLocationByGateway(List<Integer> gatewayIds) {
            throw new UnsupportedOperationException();
        }
    }
}
