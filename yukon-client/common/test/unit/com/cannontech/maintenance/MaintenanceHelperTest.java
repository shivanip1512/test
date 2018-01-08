package com.cannontech.maintenance;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.util.TimeUtil;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.model.GlobalSetting;

public class MaintenanceHelperTest {

    private MaintenanceHelper mh;

    @Before
    public void setUp() {
        mh = new MaintenanceHelper();
    }

    @Test
    public void test_getNextRunTime_NoDaySelected() throws Exception {
        GlobalSettingDao globalSettingDao = createNiceMock(GlobalSettingDao.class);
        globalSettingDao.getSetting(EasyMock.anyObject());
        expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                if ((GlobalSettingType) getCurrentArguments()[0] == GlobalSettingType.BUSINESS_DAYS) {
                    GlobalSetting globalSetting = new GlobalSetting(GlobalSettingType.BUSINESS_DAYS, "NNNNNNN");
                    return globalSetting;
                } else if ((GlobalSettingType) getCurrentArguments()[0] == GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS) {
                    GlobalSetting globalSetting =
                        new GlobalSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS, "NNNNNNN");
                    return globalSetting;
                } else if ((GlobalSettingType) getCurrentArguments()[0] == GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME) {
                    GlobalSetting globalSetting =
                        new GlobalSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME, "480,1080");
                    return globalSetting;
                } else if ((GlobalSettingType) getCurrentArguments()[0] == GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME) {
                    GlobalSetting globalSetting =
                        new GlobalSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME, "1140,1320");
                    return globalSetting;
                }
                return null;
            }
        }).anyTimes();
        replay(globalSettingDao);
        ReflectionTestUtils.setField(mh, "globalSettingDao", globalSettingDao);
        DateTime currentTime = new DateTime();
        DateTime nextRunTime = mh.getNextRunTime();
        // Ideally nextRunTime must be equal to currentTime but value getting from mh.getNextRunTime() won't be exactly equal to 
        // currentTime. Hence comparing nextRunTime value with currentTime & (currentTime + delay) i.e. nextRunTime must lies in 
        // between currentTime & (currentTime + 5 minutes).
        boolean isExpected = TimeUtil.isDateEqualOrAfter(nextRunTime,currentTime) && nextRunTime.isBefore(currentTime.plusMinutes(5));
        assertTrue("NextRunTime", isExpected);
    }

    @Test(expected = Exception.class)
    public void test_getNextRunTime_noRunWindowAvailable() throws Exception {
        GlobalSettingDao globalSettingDao = createNiceMock(GlobalSettingDao.class);
        globalSettingDao.getSetting(EasyMock.anyObject());
        expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                if ((GlobalSettingType) getCurrentArguments()[0] == GlobalSettingType.BUSINESS_DAYS) {
                    GlobalSetting globalSetting = new GlobalSetting(GlobalSettingType.BUSINESS_DAYS, "YYYYYYY");
                    return globalSetting;
                } else if ((GlobalSettingType) getCurrentArguments()[0] == GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS) {
                    GlobalSetting globalSetting =
                        new GlobalSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS, "YYYYYYY");
                    return globalSetting;
                } else if ((GlobalSettingType) getCurrentArguments()[0] == GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME) {
                    GlobalSetting globalSetting =
                        new GlobalSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME, "420,1140");
                    return globalSetting;
                } else if ((GlobalSettingType) getCurrentArguments()[0] == GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME) {
                    GlobalSetting globalSetting =
                        new GlobalSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME, "1140,2580");
                    return globalSetting;
                }
                return null;
            }
        }).anyTimes();
        replay(globalSettingDao);
        ReflectionTestUtils.setField(mh, "globalSettingDao", globalSettingDao);
        mh.getNextRunTime();
    }
}
