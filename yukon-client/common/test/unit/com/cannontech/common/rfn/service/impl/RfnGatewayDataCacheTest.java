package com.cannontech.common.rfn.service.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.AppMode;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.ConflictType;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.ConnectionType;
import com.cannontech.common.rfn.message.gateway.DataSequence;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.LastCommStatus;
import com.cannontech.common.rfn.message.gateway.Radio;
import com.cannontech.common.rfn.message.gateway.RadioType;
import com.cannontech.common.rfn.message.gateway.SequenceBlock;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.common.util.jms.JmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.core.dao.NotFoundException;

public class RfnGatewayDataCacheTest {
    
    @Test
    public void testGetWithCachedValue() throws NetworkManagerCommunicationException {
        RfnGatewayDataCache cache = new RfnGatewayDataCacheImpl(null, null, null);
        
        //create data
        PaoIdentifier paoIdentifier = new PaoIdentifier(1, PaoType.RFN_GATEWAY);
        RfnGatewayData originalData = buildTestData("Test Gateway 1");
        
        //add data to cache
        cache.put(paoIdentifier, originalData);
        
        //retrieve data from cache
        RfnGatewayData cachedData = cache.get(paoIdentifier);
        
        //test equality
        Assert.assertEquals("Cached data does not match original data.", cachedData, originalData);
    }
    
    @Test
    public void testGetWithUncachedValue() throws NetworkManagerCommunicationException {
        //setup
        RfnDeviceDao fakeRfnDeviceDao = new FakeRfnDeviceDao();
        RfnGatewayDataCacheImpl cache = new RfnGatewayDataCacheImpl(null, null, fakeRfnDeviceDao);
        FakeRequestReplyTemplate fakeTemplate = new FakeRequestReplyTemplate();
        ReflectionTestUtils.setField(cache, "requestTemplate", fakeTemplate);
        
        //retrieve uncached data
        fakeTemplate.setMode(FakeRequestReplyTemplate.Mode.REPLY);
        PaoIdentifier paoIdentifier = new PaoIdentifier(1, PaoType.RFN_GATEWAY);
        RfnGatewayData data = cache.get(paoIdentifier);
        Assert.assertNotNull("Retrieved null data.", data);
        
        //test that data was cached correctly after first request
        RfnGatewayData sameData = cache.get(paoIdentifier);
        Assert.assertEquals("Cached data doesn't match.", data, sameData);
        //sendCount should not have incremented, because the data was cached
        int sendCount = fakeTemplate.getSendCount();
        Assert.assertEquals("Cache is reloading value unnecessarily.", 1, sendCount);
    }
    
    @Test(expected=NetworkManagerCommunicationException.class)
    public void testJmsExceptionWithUncachedValue() throws NetworkManagerCommunicationException {
        //setup
        RfnDeviceDao fakeRfnDeviceDao = new FakeRfnDeviceDao();
        RfnGatewayDataCacheImpl cache = new RfnGatewayDataCacheImpl(null, null, fakeRfnDeviceDao);
        FakeRequestReplyTemplate fakeTemplate = new FakeRequestReplyTemplate();
        ReflectionTestUtils.setField(cache, "requestTemplate", fakeTemplate);
        
        //attempt to get uncached value, causing jms exception
        fakeTemplate.setMode(FakeRequestReplyTemplate.Mode.EXCEPTION);
        PaoIdentifier paoIdentifier = new PaoIdentifier(1, PaoType.RFN_GATEWAY);
        cache.get(paoIdentifier); //NetworkManagerCommunicationException thrown here
    }
    
    @Test(expected=NetworkManagerCommunicationException.class)
    public void testTimeoutWithUncachedValue() throws NetworkManagerCommunicationException {
        //setup
        RfnDeviceDao fakeRfnDeviceDao = new FakeRfnDeviceDao();
        RfnGatewayDataCacheImpl cache = new RfnGatewayDataCacheImpl(null, null, fakeRfnDeviceDao);
        FakeRequestReplyTemplate fakeTemplate = new FakeRequestReplyTemplate();
        ReflectionTestUtils.setField(cache, "requestTemplate", fakeTemplate);
        
        //attempt to get uncached value, causing jms timeout
        fakeTemplate.setMode(FakeRequestReplyTemplate.Mode.TIMEOUT);
        PaoIdentifier paoIdentifier = new PaoIdentifier(1, PaoType.RFN_GATEWAY);
        cache.get(paoIdentifier); //NetworkManagerCommunicationException thrown here
    }
    
    private RfnGatewayData buildTestData(String gatewayName) {
        RfnGatewayData.Builder builder = new RfnGatewayData.Builder();
        
        Authentication userAuth = new Authentication();
        userAuth.setPassword("userPass");
        userAuth.setUsername("user");
        Authentication adminAuth = new Authentication();
        adminAuth.setPassword("adminPass");
        adminAuth.setUsername("admin");
        Authentication superAdminAuth = new Authentication();
        superAdminAuth.setPassword("superAdminPass");
        superAdminAuth.setUsername("superAdmin");
        
        Radio radio1 = new Radio();
        radio1.setType(RadioType.EKANET_915);
        radio1.setTimestamp(1407776853);
        radio1.setMacAddress("01:23:45:67:89:ab");
        Radio radio2 = new Radio();
        radio2.setType(RadioType.BLUETOOTH);
        radio2.setTimestamp(1407776854);
        radio2.setMacAddress("01:23:45:67:89:ac");
        Set<Radio> radios = new HashSet<>();
        radios.add(radio1);
        radios.add(radio2);
        
        SequenceBlock block = new SequenceBlock();
        block.setStart(1000000);
        block.setEnd(1100000);
        Set<SequenceBlock> blocks1 = new HashSet<>();
        blocks1.add(block);
        DataSequence sequence1 = new DataSequence();
        sequence1.setType(DataType.GATEWAY_LOGS);
        sequence1.setCompletionPercentage(100.00);
        sequence1.setBlocks(blocks1);
        Set<DataSequence> sequences = new HashSet<>();
        sequences.add(sequence1);
        
        Set<ConflictType> versionConflicts = new HashSet<>();
        versionConflicts.add(ConflictType.APPLICATION);
        
        builder.user(userAuth)
               .admin(adminAuth)
               .superAdmin(superAdminAuth)
               .collectionSchedule("1 0 * * *")
               .connectionStatus(ConnectionStatus.CONNECTED)
               .connectionType(ConnectionType.TCP_IP)
               .hardwareVersion("1.0.0.0")
               .ipAddress("127.0.0.1")
               .lastCommStatus(LastCommStatus.SUCCESSFUL)
               .lastCommStatusTimestamp(1407776855)
               .mode(AppMode.FAIL_SAFE)
               .name(gatewayName)
               .port("12345")
               .radios(radios)
               .radioVersion("1.0.0.2")
               .releaseVersion("1.0.0.3")
               .routeColor((short)1)
               .sequences(sequences)
               .softwareVersion("1.0.0.4")
               .upperStackVersion("1.0.0.5")
               .versionConflicts(versionConflicts);
        return builder.build();
    }
    
    private static class FakeRequestReplyTemplate implements RequestReplyTemplate<GatewayDataResponse> {
        private static enum Mode {
            REPLY,
            EXCEPTION,
            TIMEOUT;
        }
        
        private Mode mode = Mode.REPLY;
        private int sendCount = 0;
        
        public void setMode(Mode mode) {
            this.mode = mode;
        }
        
        public int getSendCount() {
            return sendCount;
        }
        
        @Override
        public <Q extends Serializable> void send(Q requestPayload, JmsReplyHandler<GatewayDataResponse> callback) {
            sendCount++;
            
            switch (mode) {
                case REPLY:
                    GatewayDataResponse response = new GatewayDataResponse();
                    response.setRfnIdentifier(new RfnIdentifier("10000", "CPS", "RFN_GATEWAY"));
                    callback.handleReply(response);
                    callback.complete();
                    break;
                case EXCEPTION:
                    callback.handleException(new Exception("Exception thrown for testing purposes"));
                    callback.complete();
                    break;
                case TIMEOUT:
                default:
                    callback.handleTimeout();
                    callback.complete();
                    break;
            }
        }
    }
    
    private static class FakeRfnDeviceDao implements RfnDeviceDao {
        @Override
        public RfnDevice getDeviceForExactIdentifier(RfnIdentifier rfnIdentifier)
                throws NotFoundException {
            throw new MethodNotImplementedException();
        }

        @Override
        public RfnDevice getDevice(YukonPao pao) {
            throw new MethodNotImplementedException();
        }

        @Override
        public <T extends YukonPao> Map<T, RfnIdentifier> getRfnIdentifiersByPao(Iterable<T> paos) {
            throw new MethodNotImplementedException();
        }

        @Override
        public RfnDevice getDeviceForId(int deviceId) throws NotFoundException {
            YukonPao pao = new YukonPao() {
                @Override
                public PaoIdentifier getPaoIdentifier() {
                    return new PaoIdentifier(1, PaoType.RFN_GATEWAY);
                }
            };
            RfnIdentifier rfnId = new RfnIdentifier("10000", "CPS", "RF_GATEWAY");
            return new RfnDevice(pao, rfnId);
        }

        @Override
        public void updateDevice(RfnDevice device) throws NotFoundException {
            throw new MethodNotImplementedException();
        }
    }
}
