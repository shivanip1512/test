package com.cannontech.common.rfn.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.mock.FakeRequestReplyTemplate;
import com.cannontech.common.mock.FakeRequestReplyTemplate.Mode;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;

public class RfnGatewayServiceTest {
    private RfnGatewayServiceImpl service;
    
    private Authentication user;
    private Authentication admin;
    private Authentication superAdmin;
    private PaoLocation location;
    
    private final String ipAddress = "123.123.123.123";
    private final String name = "Test Gateway";
    private final String gatewayName = "Test Gateway";
    private final PaoIdentifier gatewayPaoId = new PaoIdentifier(100, PaoType.RFN_GATEWAY);
    private static final RfnIdentifier gatewayRfnId = new RfnIdentifier("10000", "CPS", "RF_GATEWAY");
    
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
        
        location = new PaoLocation();
        location.setLatitude(1.0);
        location.setLongitude(2.0);
        location.setPaoIdentifier(gatewayPaoId);
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

        PaoLocationDao paoLocationDao = new EmptyPaoLocationDao();

        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        // Expecting a call to retrieve gateway data for each RFN_GATEWAY rfnDevice.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andReturn(createEmptyRfnGatewayData(gatewayRfnId));
        EasyMock.expect(gatewayDataCache.get(paoIdentifier)).andReturn(createEmptyRfnGatewayData(gateway2RfnId));
        EasyMock.replay(gatewayDataCache);

        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, null, paoLocationDao);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        Assert.assertEquals("Expecting 2 gateways", 2, service.getAllGateways().size());
    }
    
    @Test
    public void test_getGatewayByPaoId() throws NetworkManagerCommunicationException {
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve an RfnDevice from rfnDeviceDao.
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andReturn(createRfnDevice(gatewayPaoId, gatewayRfnId));
        EasyMock.replay(rfnDeviceDao);
        
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
        RfnGateway rfnGateway = service.getGatewayByPaoId(gatewayPaoId);
        Assert.assertEquals("PaoIdentifier does not match", gatewayPaoId, rfnGateway.getPaoIdentifier());
        Assert.assertEquals("RfnIdentifier does not match", gatewayRfnId, rfnGateway.getRfnIdentifier());
        Assert.assertEquals("PaoLocation does not match", paoLocation, rfnGateway.getLocation());
    }
    
    @Test(expected=NetworkManagerCommunicationException.class)
    public void test_getGatewayByPaoId_communicationError() throws NetworkManagerCommunicationException {
        RfnDeviceDao rfnDeviceDao = EasyMock.createStrictMock(RfnDeviceDao.class);
        // Expecting a call to retrieve an RfnDevice from rfnDeviceDao (return value not used in this test).
        EasyMock.expect(rfnDeviceDao.getDeviceForId(gatewayPaoId.getPaoId())).andReturn(createRfnDevice(gatewayPaoId, gatewayRfnId));
        EasyMock.replay(rfnDeviceDao);
        
        PaoLocationDao paoLocationDao = new EmptyPaoLocationDao();
        
        RfnGatewayDataCache gatewayDataCache = EasyMock.createStrictMock(RfnGatewayDataCache.class);
        // Expecting a call to retrieve gateway data for this pao ID, throw exception to simulate communication error.
        EasyMock.expect(gatewayDataCache.get(gatewayPaoId)).andThrow(new NetworkManagerCommunicationException("Communication Error"));
        EasyMock.replay(gatewayDataCache);
        
        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, null, paoLocationDao);
        ReflectionTestUtils.setField(service, "rfnDeviceDao", rfnDeviceDao);
        service.getGatewayByPaoId(gatewayPaoId);
    }
    
    @Test(expected=NetworkManagerCommunicationException.class)
    public void test_createGateway_communicationError() throws NetworkManagerCommunicationException, GatewayUpdateException {
        //dependencies can be null, because they're not called in this test scenario
        service = new RfnGatewayServiceImpl(null, null, null, null, null);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.EXCEPTION);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        service.createGateway(name, ipAddress, location, user, admin, superAdmin);
    }
    
    @Test(expected=NetworkManagerCommunicationException.class)
    public void test_createGateway_timeoutError() throws NetworkManagerCommunicationException, GatewayUpdateException {
        //dependencies can be null, because they're not called in this test scenario
        service = new RfnGatewayServiceImpl(null, null, null, null, null);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.EXCEPTION);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        service.createGateway(name, ipAddress, location, user, admin, superAdmin);
    }
    
    @Test(expected=GatewayUpdateException.class)
    public void test_createGateway_failedResponse() throws NetworkManagerCommunicationException, GatewayUpdateException {
        //dependencies can be null, because they're not called in this test scenario
        service = new RfnGatewayServiceImpl(null, null, null, null, null);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setGatewayUpdateResult(GatewayUpdateResult.FAILED);
        fakeTemplate.setMode(Mode.REPLY);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        service.createGateway(name, ipAddress, location, user, admin, superAdmin);
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
        paoLocationDao.save(location);
        EasyMock.expectLastCall().once();
        EasyMock.replay(paoLocationDao);
        
        //connectionFactory and configurationSource can be null - they are only used by the RequestReplyTemplate,
        //which is replaced by the FakeUpdateRequestReplyTemplate in this test
        service = new RfnGatewayServiceImpl(gatewayDataCache, null, null, rfnDeviceCreationService, paoLocationDao);
        
        FakeUpdateRequestReplyTemplate fakeTemplate = new FakeUpdateRequestReplyTemplate();
        fakeTemplate.setMode(Mode.REPLY);
        ReflectionTestUtils.setField(service, "updateRequestTemplate", fakeTemplate);
        
        service.createGateway(name, ipAddress, location, user, admin, superAdmin);
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
            throw new EmptyResultDataAccessException(1);
        }

        @Override
        public void save(PaoLocation location) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveAll(Iterable<PaoLocation> locations) {
            throw new UnsupportedOperationException();
        }
    }
}
