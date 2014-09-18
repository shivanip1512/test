package com.cannontech.common.rfn.service.impl;

import java.io.Serializable;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.mock.FakeRequestReplyTemplate;
import com.cannontech.common.mock.FakeRequestReplyTemplate.Mode;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResponse;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
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
    
}
