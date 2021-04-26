package com.cannontech.dr.pxmw.service.impl.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.pxmw.model.MWChannel;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesValueV1;
import com.cannontech.message.dispatch.message.PointData;

public class PxMWDataReadServiceImplTest {
    
    private static final Logger log = YukonLogManager.getLogger(PxMWDataReadServiceImplTest.class);

    /*
    @Test 
    public void testParseValueDataToPointBoolean() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method parseMethod = PxMWDataReadServiceImpl.class.getDeclaredMethod("parseValueDataToPoint", MWChannel.class, PxMWTimeSeriesValueV1.class);
        PxMWDataReadServiceImpl dataReadService = new PxMWDataReadServiceImpl();
        parseMethod.setAccessible(true);
        Date timestampDate = new Date(1618854415l * 1000);
        
        PointData pointData = (PointData) parseMethod.invoke(dataReadService, MWChannel.ACTIVATION_STATUS_R1, new PxMWTimeSeriesValueV1(timestampDate.getTime(), "True"));
        assertTrue(pointData.getValue() == 1.0);
        assertTrue(pointData.getTimeStamp().equals(timestampDate));
        
        pointData = (PointData) parseMethod.invoke(dataReadService, MWChannel.ACTIVATION_STATUS_R1, new PxMWTimeSeriesValueV1(timestampDate.getTime(), "False"));
        assertTrue(pointData.getValue() == 0.0);
        assertTrue(pointData.getTimeStamp().equals(timestampDate));
        
        pointData = (PointData) parseMethod.invoke(dataReadService, MWChannel.ACTIVATION_STATUS_R1, new PxMWTimeSeriesValueV1(timestampDate.getTime(), "ERROR INVALID"));
        assertNull(pointData);
    }
    
    @Test
    public void testParseValueDataToPointInteger() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method parseMethod = PxMWDataReadServiceImpl.class.getDeclaredMethod("parseValueDataToPoint", MWChannel.class, PxMWTimeSeriesValueV1.class);
        PxMWDataReadServiceImpl dataReadService = new PxMWDataReadServiceImpl();
        parseMethod.setAccessible(true);
        Date timestampDate = new Date(1618854415l * 1000);
        
        PointData pointData = (PointData) parseMethod.invoke(dataReadService, MWChannel.RUNTIME_R1, new PxMWTimeSeriesValueV1(timestampDate.getTime(), "1617814415"));
        assertTrue(pointData.getValue() == 1617814415);
        assertTrue(pointData.getTimeStamp().equals(timestampDate));
        
        pointData = (PointData) parseMethod.invoke(dataReadService, MWChannel.RUNTIME_R1, new PxMWTimeSeriesValueV1(timestampDate.getTime(), "NAN"));
        assertNull(pointData);
    }
    
    @Test
    public void testParseValueDataToPointDouble() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method parseMethod = PxMWDataReadServiceImpl.class.getDeclaredMethod("parseValueDataToPoint", MWChannel.class, PxMWTimeSeriesValueV1.class);
        PxMWDataReadServiceImpl dataReadService = new PxMWDataReadServiceImpl();
        parseMethod.setAccessible(true);
        Date timestampDate = new Date(1618854415l * 1000);
        
        PointData pointData = (PointData) parseMethod.invoke(dataReadService, MWChannel.VOLTAGE, new PxMWTimeSeriesValueV1(timestampDate.getTime(), "119.1123"));
        assertTrue(pointData.getValue() == 119.1123);
        assertTrue(pointData.getTimeStamp().equals(timestampDate));
        
        pointData = (PointData) parseMethod.invoke(dataReadService, MWChannel.RUNTIME_R1, new PxMWTimeSeriesValueV1(timestampDate.getTime(), "NAN"));
        assertNull(pointData);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testBuildRequests() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method buildRequestsMethod = PxMWDataReadServiceImpl.class.getDeclaredMethod("buildRequests", Collection.class, Set.class);
        PxMWDataReadServiceImpl dataReadService = new PxMWDataReadServiceImpl();
        buildRequestsMethod.setAccessible(true);
        
        Set<String> guids = new HashSet(Arrays.asList("d87e6130-d2d4-4b28-8fb0-5b774e15e69e",
                "b241990b-3766-4ede-bd78-686aa4512a2c",
                "816c0b6d-f696-4229-a92d-149ba2ab852e",
                "c619ee6f-b254-4ccb-b5d5-fa60892ce7f6",
                "53c0c599-ecf3-4462-9472-9bfae2bc25d8"));
        
        Set<String> fiveElementTagList = new HashSet<String>(Arrays.asList("111222", "222333", "333444", "444555", "555666"));
        Set<String> twelveElementTagList = new HashSet<String>(Arrays.asList("111222", "222333", "333444",
                                                                             "444555", "555666", "666777",
                                                                             "777888", "888999", "999111",
                                                                             "101010", "111111", "121212"));
        
        List<List<PxMWTimeSeriesDeviceV1>> requests = (List<List<PxMWTimeSeriesDeviceV1>>) buildRequestsMethod.invoke(dataReadService, guids, fiveElementTagList);
        assertEquals(requests.size(), 1);
        Set<String> requestGuids = new HashSet<>();
        Set<String> requestTags = new HashSet<>();
        for (List<PxMWTimeSeriesDeviceV1> request : requests) {
            for (PxMWTimeSeriesDeviceV1 device : request) {
                requestGuids.add(device.getDeviceGuid());
                requestTags.addAll(new HashSet<String>(Arrays.asList(device.getTagTrait().split(","))));
            }
        }
        assertEquals(requestTags, fiveElementTagList);
        assertEquals(requestGuids, guids);

        requests = (List<List<PxMWTimeSeriesDeviceV1>>) buildRequestsMethod.invoke(dataReadService, guids, twelveElementTagList);
        assertEquals(requests.size(), 2);
        for (List<PxMWTimeSeriesDeviceV1> request : requests) {
            for (PxMWTimeSeriesDeviceV1 device : request) {
                requestGuids.add(device.getDeviceGuid());
                requestTags.addAll(Arrays.asList(device.getTagTrait().split(",")));
            }
        }
        assertEquals(requestTags, twelveElementTagList);
        assertEquals(requestGuids, guids);
    }
*/
}
