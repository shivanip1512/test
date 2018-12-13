package com.cannontech.dr.nest.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.v3.CustomerEnrollment;
import com.cannontech.yukon.IDatabaseCache;
public class NestServiceImplTest {
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    NestServiceImpl impl;
    
    @Before
    public void init() {
        impl = new NestServiceImpl();
    }
    
    @Test
    public void test_createDurationStr() {
        Calendar cal = Calendar.getInstance();
        Date startTime = new Date();
        cal.setTime(startTime);
        cal.add(Calendar.HOUR_OF_DAY, 2);
        Date stopTime = cal.getTime();
        String expectedDurationStr = "7200s";
        String actualDurationStr = impl.createDurationStr(startTime, stopTime);
        Assert.assertEquals(expectedDurationStr, actualDurationStr);
    }
    
    @Test
    public void test_adjustStopTime() {
        Calendar cal = Calendar.getInstance();
        Date startTime = new Date();
        cal.setTime(startTime);
        cal.add(Calendar.HOUR_OF_DAY, 2);
        Date stopTime2hrsAfter = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 3);
        Date stopTime5hrsAfter = cal.getTime();
        
        Date actualStopTimeNull = impl.adjustStopTime(startTime, null);
        Date actualStopTime2hrsAfter = impl.adjustStopTime(startTime, stopTime2hrsAfter);
        Date actualStopTime5hrsAfter = impl.adjustStopTime(startTime, stopTime5hrsAfter);
        
        Seconds secondsNull = Seconds.secondsBetween(new DateTime(startTime), new DateTime(actualStopTimeNull));
        Seconds seconds2hrsAfter = Seconds.secondsBetween(new DateTime(startTime), new DateTime(actualStopTime2hrsAfter));
        Seconds seconds5hrsAfter = Seconds.secondsBetween(new DateTime(startTime), new DateTime(actualStopTime5hrsAfter));
        int actualSecondsNullInt = secondsNull.getSeconds();
        int actualSeconds2hrsAfterInt = seconds2hrsAfter.getSeconds();
        int actualSeconds5hrsAfterInt = seconds5hrsAfter.getSeconds();
        
        int expectedSecondNullInt = 14400;
        int expectedSeconds2hrsAfterInt = 7200;
        int expectedSeconds5hrsAfterInt = 14400;
        
        Assert.assertEquals(expectedSecondNullInt, actualSecondsNullInt);
        Assert.assertEquals(expectedSeconds2hrsAfterInt, actualSeconds2hrsAfterInt);
        Assert.assertEquals(expectedSeconds5hrsAfterInt, actualSeconds5hrsAfterInt);
    }

    @Test
    public void test_createEnrollment() {
        String accountNumber = "1";
        
        LiteCustomer customerNoAltTracking = new LiteCustomer();
        customerNoAltTracking.setAltTrackingNumber(null);
        
        LiteCustomer customerValidAltTracking = new LiteCustomer();
        customerValidAltTracking.setAltTrackingNumber("647");
        
        CustomerEnrollment actualCustomerEnrollment = impl.createEnrollment(customerValidAltTracking, accountNumber);
        Assert.assertEquals("647", actualCustomerEnrollment.getCustomerId());
        
        exception.expect(NestException.class);
        impl.createEnrollment(customerNoAltTracking, accountNumber);
        
    }
    
    @Test
    public void test_createEnrollmentNoneTracking() {
        String accountNumber = "1";

        LiteCustomer customerNoneAltTracking = new LiteCustomer();
        customerNoneAltTracking.setAltTrackingNumber("(none)");
        
        exception.expect(NestException.class);
        impl.createEnrollment(customerNoneAltTracking, accountNumber);
        
    }
    
    @Test
    public void test_getNestGroupForProgramGroupOf2() {
        ArrayList<Integer> groupOf2 = new ArrayList<>();
        groupOf2.add(1);
        groupOf2.add(2);
        
        int programId = 1;
        
        LiteYukonPAObject lyp = new LiteYukonPAObject(1, "nestProgram", PaoType.LM_NEST_PROGRAM, "", "");
        
        IDatabaseCache dbCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(dbCache.getAllLMPrograms()).andReturn(Collections.singletonList((lyp)));
        EasyMock.replay(dbCache);
        ReflectionTestUtils.setField(impl, "dbCache", dbCache);
        
        exception.expect(NestException.class);
        impl.getNestGroupForProgram(groupOf2, programId);
    }
    
    @Test
    public void test_getNestGroupForProgramEmptyGroup() {
        ArrayList<Integer> groupEmpty = new ArrayList<>();
        
        int programId = 1;
        
        LiteYukonPAObject lyp = new LiteYukonPAObject(1, "nestName", PaoType.LM_GROUP_ECOBEE, "", "");

        
        IDatabaseCache dbCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(dbCache.getAllLMGroups()).andReturn(Collections.singletonList((lyp)));
        EasyMock.replay(dbCache);
        ReflectionTestUtils.setField(impl, "dbCache", dbCache);
        
        exception.expect(NestException.class);
        impl.getNestGroupForProgram(groupEmpty, programId);
    }
    
    @Test
    public void test_getNestGroupForProgramValid() {
        ArrayList<Integer> groupOf1 = new ArrayList<>();
        groupOf1.add(1);
        
        int programId = 1;
        
        LiteYukonPAObject lyp = new LiteYukonPAObject(1, "nestName", PaoType.LM_GROUP_NEST, "", "");

        
        IDatabaseCache dbCache = EasyMock.createStrictMock(IDatabaseCache.class);
        EasyMock.expect(dbCache.getAllLMGroups()).andReturn(Collections.singletonList((lyp)));
        EasyMock.replay(dbCache);
        ReflectionTestUtils.setField(impl, "dbCache", dbCache);

        LiteYukonPAObject actual = impl.getNestGroupForProgram(groupOf1, programId);
        
        Assert.assertEquals(lyp, actual);
    }
}
