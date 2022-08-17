package com.cannontech.maintenance;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.model.GlobalSetting;

public class MaintenanceHelperTest {

    private MaintenanceHelper mh;
    private Duration minimumRunWindow = new Duration(1800000); // 30 minutes

    @Before
    public void setUp() {
        mh = new MaintenanceHelper();
    }

    @Test
    public void test_getNextRunTime_NoDaySelected() throws Exception {
        setMockGlobalSettingDao("NNNNNNN", "NNNNNNN", "480,1080", "1140,1320");
        Instant startTime = Instant.now();
        Interval nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
        Interval expectedInterval = new Interval(new DateTime(startTime), new DateTime(startTime).plusDays(1));
        boolean isExpected = nextRunTime.equals(expectedInterval);
        assertTrue("NextRunTime", isExpected);
    }

   @Test
    public void test_getNextRunTime_noRunWindowAvailable() throws Exception {
        setMockGlobalSettingDao("YYYYYYY", "YYYYYYY", "420,1140", "1140,2580");
        Instant startTime = Instant.now();
        Interval nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
        boolean isExpected = false;
        if (nextRunTime == null) {
            isExpected = true;
        }
        assertTrue("NextRunTime", isExpected);
    }

   @Test
   public void test_getNextRunTime_oneRunWindowAvailable() throws Exception {
       setMockGlobalSettingDao("YYYYYYY", "YYYYYYN", "420,1140", "1140,1860");

       // 2018-08-04 is a Saturday, the only valid window ever is Saturday 19:00 to 7:00 the next day 
       // Early (18:59pm)
       Instant startTime = new DateTime(2018, 8, 4, 18, 59).toDateTime().toInstant();
       Interval nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
       assertEquals(new DateTime(2018, 8, 4, 19, 0), new DateTime(nextRunTime.getStartMillis()));
       assertEquals(new DateTime(2018, 8, 5, 7, 0), new DateTime(nextRunTime.getEndMillis()));
       
       // 2017-08-04 was Friday (Past date),the only valid coming window is Saturday 19:00 to Sunday 7:00 the next day
       startTime = new DateTime(2017, 8, 4, 18, 59).toDateTime().toInstant();
       nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
       assertEquals(new DateTime(2017, 8, 5, 19, 0), new DateTime(nextRunTime.getStartMillis()));
       assertEquals(new DateTime(2017, 8, 6, 7, 0), new DateTime(nextRunTime.getEndMillis()));

       // Start of setting (19:00pm)
       startTime = new DateTime(2018, 8, 4, 19, 00).toDateTime().toInstant();
       nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
       assertEquals(new DateTime(2018, 8, 4, 19, 0), new DateTime(nextRunTime.getStartMillis()));
       assertEquals(new DateTime(2018, 8, 5, 7, 0), new DateTime(nextRunTime.getEndMillis()));

       // middle of setting (19:30pm)
       startTime = new DateTime(2018, 8, 4, 19, 30).toDateTime().toInstant();
       nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
       assertEquals(new DateTime(2018, 8, 4, 19, 30), new DateTime(nextRunTime.getStartMillis()));
       assertEquals(new DateTime(2018, 8, 5, 7, 0), new DateTime(nextRunTime.getEndMillis()));

       // late part of setting (4:00am next day)
       startTime = new DateTime(2018, 8, 5, 4, 00).toDateTime().toInstant();
       nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
       assertEquals(new DateTime(2018, 8, 5, 4, 0), new DateTime(nextRunTime.getStartMillis()));
       assertEquals(new DateTime(2018, 8, 5, 7, 0), new DateTime(nextRunTime.getEndMillis()));

       // Duration +1 minutes left (31 minutes)
       startTime = new DateTime(2018, 8, 5, 6, 29).toDateTime().toInstant();
       nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
       assertEquals(new DateTime(2018, 8, 5, 6, 29), new DateTime(nextRunTime.getStartMillis()));
       assertEquals(new DateTime(2018, 8, 5, 7, 0), new DateTime(nextRunTime.getEndMillis()));
       
       
       // Exactly Duration time left (30 minutes). Should allow for this.
       startTime = new DateTime(2018, 8, 5, 6, 30).toDateTime().toInstant();
       nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
       assertEquals(new DateTime(2018, 8, 5, 6, 30), new DateTime(nextRunTime.getStartMillis()));
       assertEquals(new DateTime(2018, 8, 5, 7, 0), new DateTime(nextRunTime.getEndMillis()));
       
       // 1 minute less than Duration time left (29 minutes)
       startTime = new DateTime(2018, 8, 5, 6, 31).toDateTime().toInstant();
       nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
       assertEquals(new DateTime(2018, 8, 11, 19, 0), new DateTime(nextRunTime.getStartMillis()));
       assertEquals(new DateTime(2018, 8, 12, 7, 0), new DateTime(nextRunTime.getEndMillis()));
   }
   
   @Test
   public void test_getNextRunTime_onlyBusinessDaysSelected() throws Exception {
       setMockGlobalSettingDao("NYYYYYY", "NNNNNNN", "360,1200", "1140,1860");
       // Business Days [-- Mon Tue Wed Th Fri Sat] Hour [6:00 to 20:00]
       // External Maintenance Days [None Selected] Hour [19:00-7:00]
       // start time is 2018-03-28 (Wednesday) at 13:00, next valid window after start time is Wednesday 20.00 to Thursday 6.00
       Instant startTime = new DateTime(2018, 3, 28, 13, 0).toDateTime().toInstant();
       Interval nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
       // Expected interval Wednesday 8:00 PM to Thursday 6:00 AM 
       assertEquals(new DateTime(2018, 3, 28, 20, 0), new DateTime(nextRunTime.getStartMillis()));
       assertEquals(new DateTime(2018, 3, 29, 6, 0), new DateTime(nextRunTime.getEndMillis()));
   }
   
   @Test
   public void test_getNextRunTime_onlyExtMaintenanceDaysSelected() throws Exception {
       setMockGlobalSettingDao("NNNNNNN", "YYYYYYN", "360,1200", "360,1200");
       // Business Days [None Selected / Hour [6:00 to 20:00]
       // External Maintenance Days [Sun Mon Tue Wed Th Fri --] Hour [6:00-20:00]
       // start time is 2018-03-30 (Friday) at 13:00, next valid window after 
       // start time is Friday 20.00 (end of exclusion hour) to Sunday 6.00 (start of exclusion hour)
       Instant startTime = new DateTime(2018, 3, 30, 13, 0).toDateTime().toInstant();
       Interval nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
       assertEquals(new DateTime(2018, 3, 30, 20, 0), new DateTime(nextRunTime.getStartMillis()));
       assertEquals(new DateTime(2018, 4, 1, 6, 0), new DateTime(nextRunTime.getEndMillis()));
   }
   
   @Test
   public void test_getNextRunTime_crossedYear() throws Exception {
       setMockGlobalSettingDao("NYYYYYY", "NNNNNNN", "360,1200",  "360,1200");
       // Business Days [None Selected / Hour [6:00 to 20:00]
       // External Maintenance Days [Sun Mon Tue Wed Th Fri --] Hour [6:00-20:00]
       // start time is 2018-03-30 (Friday) at 13:00, next valid window after 
       // start time is Friday 20.00 (end of exclusion hour) to Sunday 6.00 (start of exclusion hour)
       Instant startTime = new DateTime(2018, 12, 31, 18, 0).toDateTime().toInstant();
       Interval nextRunTime = mh.getNextAvailableRunTime(startTime, minimumRunWindow);
       assertEquals(new DateTime(2018, 12, 31, 20, 0), new DateTime(nextRunTime.getStartMillis()));
       assertEquals(new DateTime(2019, 1, 1, 6, 0), new DateTime(nextRunTime.getEndMillis()));
   }

    private void setMockGlobalSettingDao(String businessDays, String externalMaintDays,
            String businessHoursStartStop, String externalMaintHoursStartStop) {

        GlobalSettingDao globalSettingDao = createNiceMock(GlobalSettingDao.class);
        globalSettingDao.getSetting(EasyMock.anyObject());

        expectLastCall().andAnswer(() -> {
            if ((GlobalSettingType) getCurrentArguments()[0] == GlobalSettingType.BUSINESS_DAYS) {
                GlobalSetting globalSetting = new GlobalSetting(GlobalSettingType.BUSINESS_DAYS,
                                                                businessDays);
                return globalSetting;
            } else if ((GlobalSettingType) getCurrentArguments()[0] == GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS) {
                // All external maintenance days are selected except Saturday
                GlobalSetting globalSetting = new GlobalSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS,
                                                                externalMaintDays);
                return globalSetting;
            } else if ((GlobalSettingType) getCurrentArguments()[0] == GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME) {
                GlobalSetting globalSetting = new GlobalSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME,
                                                                businessHoursStartStop);
                return globalSetting;
            } else if ((GlobalSettingType) getCurrentArguments()[0] == GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME) {
                GlobalSetting globalSetting = new GlobalSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME,
                                                                externalMaintHoursStartStop);
                return globalSetting;
            }
            return null;
        }).anyTimes();

        replay(globalSettingDao);
        ReflectionTestUtils.setField(mh, "globalSettingDao", globalSettingDao);
    }
}
