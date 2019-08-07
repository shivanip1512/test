package com.cannontech.web.support.waterNode.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.dr.itron.service.impl.ItronCommunicationServiceImpl;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.support.waterNode.details.WaterNodeDetails;
import com.cannontech.web.support.waterNode.batteryLevel.WaterNodeBatteryLevel;
import com.cannontech.web.support.waterNode.csvDao.WaterNodeCSVDao;
import com.cannontech.web.support.waterNode.dao.WaterNodeDao;

public class WaterNodeServiceImplTest {
    private static WaterNodeServiceImpl waterNodeServiceImplTest;
    Instant startTime = new Instant(1560000000000L);
    Instant stopTime = startTime.plus(Duration.standardDays(6));
    List<WaterNodeDetails> nodeDetails;
    
    @Before
    public void init() {
          WaterNodeDao mockWaterNodeDao = EasyMock.createNiceMock(WaterNodeDao.class);
          WaterNodeCSVDao mockWaterNodeCSVDao = EasyMock.createNiceMock(WaterNodeCSVDao.class);
          waterNodeServiceImplTest = new WaterNodeServiceImpl(mockWaterNodeDao, mockWaterNodeCSVDao);
    }
    
    @Test
    public void test_delete() throws IOException {
        Double normalVoltage = 3.6;
        WaterNodeDetails testHSCDetails = new WaterNodeDetails();//this node has a high sleeping current
        testHSCDetails.setSerialNumber("1");
        testHSCDetails.setPaObjectId(1000);
        testHSCDetails.setMeterNumber("1");
        testHSCDetails.setName("TEST");
        testHSCDetails.setType("TEST_TYPE");
        //initialize new array of waterNodeDetails to test
        for (int i = 0; i<150; i++) {
            testHSCDetails.addTimestamp(startTime.plus(Duration.standardHours(i)));
            testHSCDetails.addVoltage(normalVoltage-2*i*.0001);//depletion rate of -0.2mV/hr
        }
        
        ReflectionTestUtils.invokeMethod(waterNodeServiceImplTest, "setHighSleepingCurrent", testHSCDetails);
        Assert.assertEquals("High sleeping current not present", true, testHSCDetails.getHighSleepingCurrentIndicator());
        
        Double lowVoltage = 3.42;
        WaterNodeDetails testLOWDetails = new WaterNodeDetails();//this node has a high sleeping current
        testLOWDetails.setSerialNumber("2");
        testLOWDetails.setPaObjectId(2000);
        testLOWDetails.setMeterNumber("2");
        testLOWDetails.setName("TEST");
        testLOWDetails.setType("TEST_TYPE");
        //initialize new array of waterNodeDetails to test
        for (int i = 0; i<150; i++) {
            testLOWDetails.addTimestamp(startTime.plus(Duration.standardHours(i)));
            testLOWDetails.addVoltage(lowVoltage);//depletion rate of -0.2mV/hr
        }
        
        ReflectionTestUtils.invokeMethod(waterNodeServiceImplTest, "setNodeCategory", testLOWDetails, stopTime);
        Assert.assertEquals("Battery voltage not in LOW category", WaterNodeBatteryLevel.LOW, testLOWDetails.getBatteryLevel());
        
    }
}
