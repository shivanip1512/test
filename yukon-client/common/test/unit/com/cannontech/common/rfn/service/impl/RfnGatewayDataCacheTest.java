package com.cannontech.common.rfn.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.mock.FakeRequestReplyTemplate;
import com.cannontech.common.mock.FakeRfnDeviceDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
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
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;

public class RfnGatewayDataCacheTest {
    
    @Test
    public void test_get_withCachedValue() throws NmCommunicationException {
        RfnGatewayDataCache cache = new RfnGatewayDataCacheImpl(null, null, null);
        
        //create data
        PaoIdentifier paoIdentifier = new PaoIdentifier(1, PaoType.RFN_GATEWAY);
        RfnGatewayData originalData = buildTestData("Test Gateway 1");
        
        //add data to cache
        cache.put(paoIdentifier, originalData);
        
        //retrieve data from cache
        RfnGatewayData cachedData = cache.get(paoIdentifier);
        
        //test equality
        assertEquals(cachedData, originalData, "Cached data does not match original data.");
    }
    
    @Test
    public void test_get_withUncachedValue() throws NmCommunicationException {
        //setup
        RfnDeviceDao fakeRfnDeviceDao = new FakeRfnDeviceDao();
        RfnGatewayDataCacheImpl cache = new RfnGatewayDataCacheImpl(null, fakeRfnDeviceDao, null);
        FakeGatewayRequestReplyTemplate fakeTemplate = new FakeGatewayRequestReplyTemplate();
        ReflectionTestUtils.setField(cache, "requestTemplate", fakeTemplate);

        //retrieve uncached data
        fakeTemplate.setMode(FakeRequestReplyTemplate.Mode.REPLY);
        PaoIdentifier paoIdentifier = new PaoIdentifier(1, PaoType.RFN_GATEWAY);
        RfnGatewayData data = cache.get(paoIdentifier);
        assertNotNull(data, "Retrieved null data.");
        
        //test that data was cached correctly after first request
        RfnGatewayData sameData = cache.get(paoIdentifier);
        assertEquals(data, sameData, "Cached data doesn't match.");
        //sendCount should not have incremented, because the data was cached
        int sendCount = fakeTemplate.getSendCount();
        assertEquals(1, sendCount, "Cache is reloading value unnecessarily.");
    }
    
    @Test
    public void test_get_jmsExceptionWithUncachedValue() throws NmCommunicationException {
        //setup
        RfnDeviceDao fakeRfnDeviceDao = new FakeRfnDeviceDao();
        RfnGatewayDataCacheImpl cache = new RfnGatewayDataCacheImpl(null, fakeRfnDeviceDao, null);
        FakeGatewayRequestReplyTemplate fakeTemplate = new FakeGatewayRequestReplyTemplate();
        ReflectionTestUtils.setField(cache, "requestTemplate", fakeTemplate);
        
        //attempt to get uncached value, causing jms exception
        fakeTemplate.setMode(FakeRequestReplyTemplate.Mode.EXCEPTION);
        PaoIdentifier paoIdentifier = new PaoIdentifier(1, PaoType.RFN_GATEWAY);
        Assertions.assertThrows(NmCommunicationException.class, () -> {
            cache.get(paoIdentifier);//NetworkManagerCommunicationException thrown here
        });
    }
    
    @Test
    public void test_get_timeoutWithUncachedValue() throws NmCommunicationException {
        //setup
        RfnDeviceDao fakeRfnDeviceDao = new FakeRfnDeviceDao();
        RfnGatewayDataCacheImpl cache = new RfnGatewayDataCacheImpl(null, fakeRfnDeviceDao, null);
        FakeGatewayRequestReplyTemplate fakeTemplate = new FakeGatewayRequestReplyTemplate();
        ReflectionTestUtils.setField(cache, "requestTemplate", fakeTemplate);
        
        //attempt to get uncached value, causing jms timeout
        fakeTemplate.setMode(FakeRequestReplyTemplate.Mode.TIMEOUT);
        PaoIdentifier paoIdentifier = new PaoIdentifier(1, PaoType.RFN_GATEWAY);
        Assertions.assertThrows(NmCommunicationException.class, () -> {
            cache.get(paoIdentifier);//NetworkManagerCommunicationException thrown here
        });
    }
    
    @Test
    public void test_remove() throws NmCommunicationException {
        //setup
        RfnDeviceDao fakeRfnDeviceDao = new FakeRfnDeviceDao();
        RfnGatewayDataCacheImpl cache = new RfnGatewayDataCacheImpl(null, fakeRfnDeviceDao, null);
        FakeGatewayRequestReplyTemplate fakeTemplate = new FakeGatewayRequestReplyTemplate();
        ReflectionTestUtils.setField(cache, "requestTemplate", fakeTemplate);
        //create data
        PaoIdentifier paoIdentifier = new PaoIdentifier(1, PaoType.RFN_GATEWAY);
        RfnGatewayData data = buildTestData("Test Gateway 1");
        //add data to cache
        cache.put(paoIdentifier, data);
        //retrieve data from cache
        RfnGatewayData cachedData = cache.get(paoIdentifier);
        //check that data was retrieved from cache
        assertNotNull(cachedData, "Retrieved null data.");
        
        //remove data from cache
        cache.remove(paoIdentifier);
        
        //set mode to throw exception if cache attempts to request missing data
        fakeTemplate.setMode(FakeRequestReplyTemplate.Mode.EXCEPTION);
        // attempt to retrieve the remove data from the cache, should throw exception when it
        // attempts to request the data because it is missing
        Assertions.assertThrows(NmCommunicationException.class, () -> {
            cache.get(paoIdentifier);
        });
    }
    
    @Test
    public void test_get_afterMultipleUpdates() {
        RfnGatewayDataCache cache = new RfnGatewayDataCacheImpl(null, null, null);
        
        //create data
        PaoIdentifier paoIdentifier = new PaoIdentifier(1, PaoType.RFN_GATEWAY);
        RfnGatewayData firstData = buildTestData("Test Gateway 1");
        
        RfnGatewayData.Builder builder = new RfnGatewayData.Builder();
        RfnGatewayData secondData = builder.copyOf(firstData)
                                           .connectionStatus(ConnectionStatus.DISCONNECTED)
                                           .build();
        
        //add data to cache
        cache.put(paoIdentifier, firstData);
        cache.put(paoIdentifier, secondData);
        
        //retrieve data from cache
        RfnGatewayData cachedData = cache.getIfPresent(paoIdentifier);
        
        //test equality
        assertEquals(cachedData, secondData, "Cached data does not match latest data update.");
    }
    
    private RfnGatewayData buildTestData(String gatewayName) {
        RfnGatewayData.Builder builder = new RfnGatewayData.Builder();
        
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
        radio1.setVersion("V_10_7");
        Radio radio2 = new Radio();
        radio2.setType(RadioType.BLUETOOTH);
        radio2.setTimestamp(1407776854);
        radio2.setMacAddress("01:23:45:67:89:ac");
        radio2.setVersion("V_10_7");
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
        
        builder.admin(adminAuth)
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
               .versionConflicts(versionConflicts)
               .currentDataStreamingLoading(25.0)
               .maxDataStreamingLoading(100.0)
               .gwTotalNodes(100)
               .gwTotalNodesNoInfo(100)
               .gwTotalNodesWithInfo(100)
               .gwTotalNodesWithSN(100)
               .gwTotalNotReadyNodes(100)
               .gwTotalReadyNodes(100);
        return builder.build();
    }
    
    private static class FakeGatewayRequestReplyTemplate extends FakeRequestReplyTemplate<GatewayDataResponse> {
        
        @Override
        protected <Q extends Serializable> GatewayDataResponse buildResponse(Q request) {
            GatewayDataResponse response = new GatewayDataResponse();
            response.setRfnIdentifier(new RfnIdentifier("10000", "EATON", "RFGateway"));
            return response;
        }
    }
}
