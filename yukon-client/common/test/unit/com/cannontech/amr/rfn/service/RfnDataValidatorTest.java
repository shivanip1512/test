package com.cannontech.amr.rfn.service;

import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RfnDataValidatorTest {

    private RfnDataValidator rfnDataValidator;

    private GlobalSettingDao mockGlobalSettingDao;

    @BeforeEach
    public void setUp() {
        rfnDataValidator = new RfnDataValidatorImpl();
        mockGlobalSettingDao = EasyMock.createNiceMock(GlobalSettingDao.class);
        EasyMock.expect(mockGlobalSettingDao.getInteger(GlobalSettingType.RFN_INCOMING_DATA_TIMESTAMP_LIMIT))
        .andReturn(6); // Filter data more than six months old
        EasyMock.replay(mockGlobalSettingDao);

        ReflectionTestUtils.setField(rfnDataValidator, "globalSettingDao", mockGlobalSettingDao);
    }

    @Test
    public void testIsTimestampValid() {
        DateTime timeBeforeYear2k = new DateTime("1999-02-17T19:01:36+0000");
        DateTime timeMoreThanOneYearLater = new DateTime().plusYears(1).plusDays(1);
        Instant now = new Instant();

        assertFalse(rfnDataValidator.isTimestampValid(new Instant(timeBeforeYear2k), now));
        assertFalse(rfnDataValidator.isTimestampValid(new Instant(timeMoreThanOneYearLater), now));
        assertTrue(rfnDataValidator.isTimestampValid(now, new Instant()));
    }

/*    @Test
    public void testIsTimestampRecent() {
        DateTime sixMonthsAndOneSecondAgo = new DateTime().minusMonths(6).minusSeconds(1);
        DateTime oneYearAndOneDayFromNow = new DateTime().plusYears(1).plusDays(1);
       // Using 27 here because I don't want this test to break in February
        DateTime notQuiteSixMonthsAgo = new DateTime().minusMonths(5).minusDays(27);
        Instant now = new Instant();

        assertFalse(rfnDataValidator.isTimestampRecent(new Instant(sixMonthsAndOneSecondAgo), now));
        assertFalse(rfnDataValidator.isTimestampRecent(new Instant(oneYearAndOneDayFromNow), now));
        assertTrue(rfnDataValidator.isTimestampRecent(new Instant(notQuiteSixMonthsAgo), now));

    }*/

}
