package com.cannontech.stars.dr.optout.service;

import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMockSupport;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.util.OpenInterval;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.roleproperties.dao.impl.RolePropertyDaoImpl;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.stars.dr.optout.model.OptOutLimit;
import com.cannontech.stars.dr.optout.service.impl.OptOutServiceImpl;

public class OptOutServiceTest extends EasyMockSupport {
    private OptOutServiceImpl optOutService;

    private static final DateTimeZone centralTimeZone = DateTimeZone.forOffsetHoursMinutes(5, 0);
    private static final DateTimeFormatter dateTimeFormmater = DateTimeFormat.forPattern("MM/dd/yyyy").withZone(centralTimeZone);

    private static final LiteYukonGroup residentialGroup_One = new LiteYukonGroup();
    private static final LiteYukonGroup residentialGroup_Two = new LiteYukonGroup();
    private static final LiteYukonGroup residentialGroup_Three = new LiteYukonGroup();
    private static final LiteYukonGroup residentialGroup_Four = new LiteYukonGroup();
    static {
        residentialGroup_One.setGroupID(1);
        residentialGroup_One.setGroupDescription("Test residential login group");
        residentialGroup_One.setGroupName("Test Group");

        residentialGroup_Two.setGroupID(2);
        residentialGroup_Two.setGroupDescription("Test residential login group");
        residentialGroup_Two.setGroupName("Test Group Two");
        
        residentialGroup_Three.setGroupID(3);
        residentialGroup_Three.setGroupDescription("Test residential login group");
        residentialGroup_Three.setGroupName("Test Group Three");

        residentialGroup_Four.setGroupID(4);
        residentialGroup_Four.setGroupDescription("Test residential login group");
        residentialGroup_Four.setGroupName("Test Group Four");
    }
    
    private static final String optOutLimit_twoValues = "[{start:4,stop:10,limit:2},{start:11,stop:3,limit:3}]";
    private static final String optOutLimit_oneValue = "[{start:10,stop:9,limit:1}]";
    private static final String optOutLimit_oneValue2 = "[{\"start\":4,\"stop\":10,\"limit\":2}]";
    private static final String optOutLimit_singleQuoteJson = "[{'start':4,'stop':10,'limit':2}]";
    private static final String optOutLimit_superfluousJson = "[{\"start\":4,\"stop\":10,\"limit\":2,\"anotherLimit\":3,\"anotherStart\":20}]";
    private static final String optOutLimit_missingInfoJson = "[{\"start\":4,\"stop\":10}]";
    private static final String optOutLimit_empty = "";
    
    private static final DateTime date_One = dateTimeFormmater.parseDateTime("01/12/2012");
    private static final DateTime date_Two = dateTimeFormmater.parseDateTime("05/16/2012");
    private static final DateTime date_Three = dateTimeFormmater.parseDateTime("11/2/2012");
    private static final DateTime date_Four = dateTimeFormmater.parseDateTime("01/12/2010");
    private static final DateTime date_Five = dateTimeFormmater.parseDateTime("10/3/2010");

    private static final RolePropertyDao rolePropertyDaoMock = new RolePropertyDaoImpl() {
        @Override
        public String getPropertyStringValue(LiteYukonGroup liteYukonGroup, com.cannontech.core.roleproperties.YukonRoleProperty roleProperty) {
            switch (roleProperty) {
                case RESIDENTIAL_OPT_OUT_LIMITS:
                    switch (liteYukonGroup.getGroupID()) {
                        case 1:
                            return optOutLimit_twoValues;
                        case 2:
                            return optOutLimit_oneValue2;
                        case 3:
                            return optOutLimit_empty;
                        case 4:
                            return optOutLimit_oneValue;
                        case 5:
                            return optOutLimit_singleQuoteJson;
                        case 6: 
                            return optOutLimit_superfluousJson;
                        case 7: 
                            return optOutLimit_missingInfoJson;
                        default:
                            throw new EmptyResultDataAccessException(1);
                    }
                default:
                    throw new EmptyResultDataAccessException(1);
            }
        }
    };
    
    @Before
    public void setup() {
        optOutService = new OptOutServiceImpl();
        ReflectionTestUtils.setField(optOutService, "rolePropertyDao", rolePropertyDaoMock);
    }

    @Test
    public void testSingleQuoteJson() {
        List<OptOutLimit> optOutList = optOutService.findCurrentOptOutLimit(new LiteYukonGroup(5));
        Assert.assertEquals(optOutList.size(), 1);
        Assert.assertEquals(optOutList.get(0).getLimit(), 2);
        Assert.assertEquals((int)optOutList.get(0).getStartMonth(), 4);
        Assert.assertEquals((int)optOutList.get(0).getStopMonth(), 10);
    }

    @Test
    public void testSuperfluousJson() {
        List<OptOutLimit> optOutList = optOutService.findCurrentOptOutLimit(new LiteYukonGroup(6));
        Assert.assertEquals(optOutList.size(), 1);
        Assert.assertEquals(optOutList.get(0).getLimit(), 2);
        Assert.assertEquals((int)optOutList.get(0).getStartMonth(), 4);
        Assert.assertEquals((int)optOutList.get(0).getStopMonth(), 10);
    }

    @Test(expected=RuntimeException.class)
    public void testMissingInfoJson() {
        optOutService.findCurrentOptOutLimit(new LiteYukonGroup(7));
    }

    /**
     * Opt Out Limits   (--3----[3/4]------2--------[10/11]------)
     * Intersection Date                   |
     */
    @Test
    public void testFindOptOutLimitInterval_StartMonthBeforeStopMonth() {
        OpenInterval findOptOutLimitInterval = optOutService.findOptOutLimitInterval(date_Two, centralTimeZone, residentialGroup_One);
        
        Assert.assertEquals(dateTimeFormmater.parseDateTime("04/1/2012").toInstant(), findOptOutLimitInterval.getStart().toInstant());
        Assert.assertEquals(dateTimeFormmater.parseDateTime("11/1/2012").toInstant(), findOptOutLimitInterval.getEnd().toInstant());
    }

    /**
     * Opt Out Limits   (--3----[3/4]------2--------[10/11]------)
     * Intersection Date  |
     */
    @Test
    public void testFindOptOutLimitInterval_StopMonthBeforeStartMonth_One() {
        OpenInterval findOptOutLimitInterval = optOutService.findOptOutLimitInterval(date_One, centralTimeZone, residentialGroup_One);
        
        Assert.assertEquals(dateTimeFormmater.parseDateTime("11/1/2011").toInstant(), findOptOutLimitInterval.getStart().toInstant());
        Assert.assertEquals(dateTimeFormmater.parseDateTime("04/1/2012").toInstant(), findOptOutLimitInterval.getEnd().toInstant());
    }
    
    /**
     * Opt Out Limits   (--3----[3/4]------2--------[10/11]------)
     * Intersection Date                                           |
     */
    @Test
    public void testFindOptOutLimitInterval_StopMonthBeforeStartMonth_Two() {
        OpenInterval findOptOutLimitInterval = optOutService.findOptOutLimitInterval(date_Three, centralTimeZone, residentialGroup_One);
        
        Assert.assertEquals(dateTimeFormmater.parseDateTime("11/1/2012").toInstant(), findOptOutLimitInterval.getStart().toInstant());
        Assert.assertEquals(dateTimeFormmater.parseDateTime("04/1/2013").toInstant(), findOptOutLimitInterval.getEnd().toInstant());
    }

    /**
     * Opt Out Limits   (------------------[9/10]------)
     * Intersection Date  |
     */
    @Test
    public void testFindOptOutLimitInterval_StopMonthBeforeStartMonth_Five() {
        OpenInterval findOptOutLimitInterval = optOutService.findOptOutLimitInterval(date_Four, centralTimeZone, residentialGroup_Four);
        
        Assert.assertEquals(dateTimeFormmater.parseDateTime("10/1/2009").toInstant(), findOptOutLimitInterval.getStart().toInstant());
        Assert.assertEquals(dateTimeFormmater.parseDateTime("10/1/2010").toInstant(), findOptOutLimitInterval.getEnd().toInstant());
    }
    
    /**
     * Opt Out Limits   (------------------[9/10]------)
     * Intersection Date  |
     */
    @Test
    public void testFindOptOutLimitInterval_StopMonthBeforeStartMonth_Six() {
        OpenInterval findOptOutLimitInterval = optOutService.findOptOutLimitInterval(date_Five, centralTimeZone, residentialGroup_Four);
        
        Assert.assertEquals(dateTimeFormmater.parseDateTime("10/1/2010").toInstant(), findOptOutLimitInterval.getStart().toInstant());
        Assert.assertEquals(dateTimeFormmater.parseDateTime("10/1/2011").toInstant(), findOptOutLimitInterval.getEnd().toInstant());
    }

    /**
     * Opt Out Limits   (--3----[3/4]------2--------[10/11]------)
     * Intersection Date  |
     */
    @Test
    public void testFindOptOutLimitInterval_StopMonthBeforeStartMonth_Four() {
        OpenInterval findOptOutLimitInterval = optOutService.findOptOutLimitInterval(date_Four, centralTimeZone, residentialGroup_One);
        
        Assert.assertEquals(dateTimeFormmater.parseDateTime("11/1/2009").toInstant(), findOptOutLimitInterval.getStart().toInstant());
        Assert.assertEquals(dateTimeFormmater.parseDateTime("04/1/2010").toInstant(), findOptOutLimitInterval.getEnd().toInstant());
    }
    
    
    /**
     * Opt Out Limits   (        [4]------2--------[10]      ) 
     * Intersection Date                   |
     */
    @Test
    public void testFindOptOutLimitInterval_StartMonthBeforeStopMonth_PartialLimit() {
        OpenInterval findOptOutLimitInterval = optOutService.findOptOutLimitInterval(date_Two, centralTimeZone, residentialGroup_Two);
        
        Assert.assertEquals(dateTimeFormmater.parseDateTime("04/1/2012").toInstant(), findOptOutLimitInterval.getStart().toInstant());
        Assert.assertEquals(dateTimeFormmater.parseDateTime("11/1/2012").toInstant(), findOptOutLimitInterval.getEnd().toInstant());
    }

    /**
     * Opt Out Limits   (        [4]------2--------[10]        )
     * Intersection Date  |
     */
    @Test
    public void testFindOptOutLimitInterval_StopMonthBeforeStartMonth_PartialLimit_One() {
        OpenInterval findOptOutLimitInterval = optOutService.findOptOutLimitInterval(date_One, centralTimeZone, residentialGroup_Two);
        
        Assert.assertNull(findOptOutLimitInterval);
    }
    
    /**
     * Opt Out Limits   (         [4]------2--------[10]          )
     * Intersection Date                                       |
     */
    @Test
    public void testFindOptOutLimitInterval_StopMonthBeforeStartMonth_PartialLimit_Two() {
        OpenInterval findOptOutLimitInterval = optOutService.findOptOutLimitInterval(date_Three, centralTimeZone, residentialGroup_Two);
        
        Assert.assertNull(findOptOutLimitInterval);
    }

    /**
     * Opt Out Limits   (                                                )
     * Intersection Date   |
     */
    @Test
    public void testFindOptOutLimitInterval_StopMonthBeforeStartMonth_NoLimits() {
        OpenInterval findOptOutLimitInterval = optOutService.findOptOutLimitInterval(date_One, centralTimeZone, residentialGroup_Three);
        
        Assert.assertNull(findOptOutLimitInterval);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testFindOptOutLimitInterval_nullIntersectingDate() {
        optOutService.findOptOutLimitInterval(null, centralTimeZone, residentialGroup_One);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFindOptOutLimitInterval_nullDateTimeZone() {
        optOutService.findOptOutLimitInterval(date_One, null, residentialGroup_One);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFindOptOutLimitInterval_nullResidentialGroup() {
        optOutService.findOptOutLimitInterval(date_One, centralTimeZone, null);
    }
}