package com.cannontech.dr.eatonCloud.service.impl.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoImplTest;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.eatonCloud.model.EatonCloudChannel;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDeviceV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesValueV1;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class EatonCloudDataReadServiceImplTest {
    
    private PaoDefinitionDao paoDefinitionDao;
    
    private static final Logger log = YukonLogManager.getLogger(EatonCloudDataReadServiceImplTest.class);
    private static final String testGuid = "415c885c-e5fc-4bc4-b7e1-f39356eb813a";
    
    @Test
    public void testParseValueDataToPointBoolean() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method parseMethod = EatonCloudDataReadServiceImpl.class.getDeclaredMethod("parsePointValue", EatonCloudChannel.class, EatonCloudTimeSeriesValueV1.class, LiteYukonPAObject.class, String.class);
        EatonCloudDataReadServiceImpl dataReadService = new EatonCloudDataReadServiceImpl();
        parseMethod.setAccessible(true);
        Date timestampDate = new Date(1618854415l * 1000);
        LiteYukonPAObject pao = new LiteYukonPAObject(123456789);
        pao.setPaoName("PAO NAME");
        
        Double pointValue = (Double) parseMethod.invoke(dataReadService, EatonCloudChannel.ACTIVATION_STATUS_R1, new EatonCloudTimeSeriesValueV1(timestampDate.getTime(), "True"), pao, testGuid);
        assertTrue(pointValue.equals(1.0));
        
        pointValue = (Double) parseMethod.invoke(dataReadService, EatonCloudChannel.ACTIVATION_STATUS_R1, new EatonCloudTimeSeriesValueV1(timestampDate.getTime(), "False"), pao, testGuid);
        assertTrue(pointValue.equals(0.0));
        
        pointValue = (Double) parseMethod.invoke(dataReadService, EatonCloudChannel.ACTIVATION_STATUS_R1, new EatonCloudTimeSeriesValueV1(timestampDate.getTime(), "ERROR INVALID"), pao, testGuid);
        assertNull(pointValue);
    }
    
    @Test
    public void testParseValueDataToPointInteger() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method parseMethod = EatonCloudDataReadServiceImpl.class.getDeclaredMethod("parsePointValue", EatonCloudChannel.class, EatonCloudTimeSeriesValueV1.class, LiteYukonPAObject.class, String.class);
        EatonCloudDataReadServiceImpl dataReadService = new EatonCloudDataReadServiceImpl();
        parseMethod.setAccessible(true);
        Date timestampDate = new Date(1618854415l * 1000);
        LiteYukonPAObject pao = new LiteYukonPAObject(123456789);
        pao.setPaoName("PAO NAME");
        
        Double pointData = (Double) parseMethod.invoke(dataReadService, EatonCloudChannel.RUNTIME_R1, new EatonCloudTimeSeriesValueV1(timestampDate.getTime(), "1617814415"), pao, testGuid);
        assertTrue(pointData.equals(1617814415.0));
        
        pointData = (Double) parseMethod.invoke(dataReadService, EatonCloudChannel.RUNTIME_R1, new EatonCloudTimeSeriesValueV1(timestampDate.getTime(), "NAN"), pao, testGuid);
        assertNull(pointData);
    }
    
    @Test
    public void testParseValueDataToPointDouble() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method parseMethod = EatonCloudDataReadServiceImpl.class.getDeclaredMethod("parsePointValue", EatonCloudChannel.class, EatonCloudTimeSeriesValueV1.class, LiteYukonPAObject.class, String.class);
        EatonCloudDataReadServiceImpl dataReadService = new EatonCloudDataReadServiceImpl();
        parseMethod.setAccessible(true);
        Date timestampDate = new Date(1618854415l * 1000);
        LiteYukonPAObject pao = new LiteYukonPAObject(123456789);
        pao.setPaoName("PAO NAME");

        Double pointData = (Double) parseMethod.invoke(dataReadService, EatonCloudChannel.VOLTAGE, new EatonCloudTimeSeriesValueV1(timestampDate.getTime(), "2658"), pao, testGuid);
        assertTrue(pointData.equals(265.8));

        pointData = (Double) parseMethod.invoke(dataReadService, EatonCloudChannel.FREQUENCY, new EatonCloudTimeSeriesValueV1(timestampDate.getTime(), "60000"), pao, testGuid);
        assertTrue(pointData.equals(60.0));

        pointData = (Double) parseMethod.invoke(dataReadService, EatonCloudChannel.RUNTIME_R1, new EatonCloudTimeSeriesValueV1(timestampDate.getTime(), "NAN"), pao, testGuid);
        assertNull(pointData);
    }
    
    @Test
    public void testChunkRequests() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Method chunkRequestsMethod = EatonCloudDataReadServiceImpl.class.getDeclaredMethod("chunkRequests", List.class);
        EatonCloudDataReadServiceImpl dataReadService = new EatonCloudDataReadServiceImpl();
        paoDefinitionDao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao();

        List<EatonCloudTimeSeriesDeviceV1> requests = new ArrayList<>();
        
        // Can change to adjust number of devices added
        int numDevices = 200;
        for (int i = 0; i < numDevices; i++) {
            String randGuid = UUID.randomUUID().toString();
            
            // Can change to test other devices
            PaoType device = PaoType.LCR6200C;
            Set<BuiltInAttribute> deviceAttributes = paoDefinitionDao.getDefinedAttributes(device).stream()
                    .map(attributeDefinition -> attributeDefinition.getAttribute())
                    .collect(Collectors.toSet());;
            Set<String> deviceTags = EatonCloudChannel.getTagsForAttributes(deviceAttributes);
            
            requests.add(new EatonCloudTimeSeriesDeviceV1(randGuid, StringUtils.join(deviceTags, ',')));
        }
        
        List<List<EatonCloudTimeSeriesDeviceV1>> chunkedRequests = (List<List<EatonCloudTimeSeriesDeviceV1>>) chunkRequestsMethod.invoke(dataReadService, requests);
        
        // Calculated from max of 1000 tags/chunk
        int numChunks = 4;
        int tagsPerChunk = 990;
        int tagsInLastChunk = 630;
        
        assertEquals(numChunks, chunkedRequests.size());
        
        for (int i = 0; i < numChunks-1; i++) {
            assertEquals(tagsPerChunk, chunkedRequests.get(i)
                    .stream().collect(Collectors.summingInt(d -> Lists.newArrayList(Splitter.on(",").split(d.getTagTrait())).size())));
        }
        
        assertEquals(tagsInLastChunk, chunkedRequests.get(numChunks-1)
                .stream().collect(Collectors.summingInt(d -> Lists.newArrayList(Splitter.on(",").split(d.getTagTrait())).size())));
    }
}
