package com.cannontech.stars.dr.optout.service;

import junit.framework.Assert;

import org.easymock.EasyMockSupport;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.OpenInterval;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.roleproperties.dao.impl.RolePropertyDaoImpl;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.stars.dr.optout.service.impl.OptOutServiceImpl;

public class OptOutServiceTest extends EasyMockSupport {
    private OptOutServiceImpl optOutService;

    private static final DateTimeZone centralTimeZone = DateTimeZone.forOffsetHoursMinutes(5, 0);
    private static final DateTimeFormatter dateTimeFormmater = DateTimeFormat.forPattern("MM/dd/yyyy").withZone(centralTimeZone);

    private static final LiteYukonGroup residentialGroup_One = new LiteYukonGroup();
    private static final LiteYukonGroup residentialGroup_Two = new LiteYukonGroup();
    private static final LiteYukonGroup residentialGroup_Three = new LiteYukonGroup();
    {
        residentialGroup_One.setGroupID(1);
        residentialGroup_One.setGroupDescription("Test residential login group");
        residentialGroup_One.setGroupName("Test Group");

        residentialGroup_Two.setGroupID(2);
        residentialGroup_Two.setGroupDescription("Test residential login group");
        residentialGroup_Two.setGroupName("Test Group Two");

        residentialGroup_Three.setGroupID(3);
        residentialGroup_Three.setGroupDescription("Test residential login group");
        residentialGroup_Three.setGroupName("Test Group Three");
    }
    
    private static final String optOutLimitRolePropertyValue_residentialGroup_One = "[{start:4;stop:10;limit:2},{start:11;stop:3;limit:3}]";
    private static final String optOutLimitRolePropertyValue_residentialGroup_ParctialLimit = "[{start:4;stop:10;limit:2}]";
    private static final String optOutLimitRolePropertyValue_residentialGroup_NoLimit = "";
    
    private static final DateTime date_One = dateTimeFormmater.parseDateTime("01/12/2012");
    private static final DateTime date_Two = dateTimeFormmater.parseDateTime("05/16/2012");
    private static final DateTime date_Three = dateTimeFormmater.parseDateTime("11/2/2012");
    private static final DateTime date_Four = dateTimeFormmater.parseDateTime("01/12/2010");

    private static final RolePropertyDao rolePropertyDaoMock = new RolePropertyDaoImpl() {
        @Override
        public String getPropertyStringValue(LiteYukonGroup liteYukonGroup, com.cannontech.core.roleproperties.YukonRoleProperty roleProperty) {
            switch (roleProperty) {
                case RESIDENTIAL_OPT_OUT_LIMITS:
                    switch (liteYukonGroup.getGroupID()) {
                        case 1:
                            return optOutLimitRolePropertyValue_residentialGroup_One;
                        case 2:
                            return optOutLimitRolePropertyValue_residentialGroup_ParctialLimit;
                        case 3:
                            return optOutLimitRolePropertyValue_residentialGroup_NoLimit;
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

        optOutService.setRolePropertyDao(rolePropertyDaoMock);
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
        
        Assert.assertEquals(dateTimeFormmater.parseDateTime("11/1/2011").toInstant(), findOptOutLimitInterval.getStart().toInstant());
        Assert.assertEquals(dateTimeFormmater.parseDateTime("04/1/2012").toInstant(), findOptOutLimitInterval.getEnd().toInstant());
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