package com.cannontech.common.alert.service.impl;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.alert.alarms.AlarmAlert;
import com.cannontech.common.alert.model.IdentifiableAlert;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.user.checker.NullUserChecker;

public class AlertServiceImplTest {
    
    private AlertServiceImpl alertService = null;
    
    @Before
    public void setup() {
        alertService = new AlertServiceImpl();
    }

    @Test
    public void testInitialize() {
        final long millisInHour = 60 * 60 * 1000;
        long maxAge;
        
        alertService.setRoleDao(new MockRoleDao("5"));
        alertService.setupMaxAge();
        maxAge = alertService.getMaxAge();
        assertEquals(18000000l, maxAge);
        
        alertService.setRoleDao(new MockRoleDao(".5"));
        alertService.setupMaxAge();
        maxAge = alertService.getMaxAge();
        assertEquals(1800000l, maxAge);
        
        alertService.setRoleDao(new MockRoleDao(" "));
        alertService.setupMaxAge();
        maxAge = alertService.getMaxAge();
        assertEquals(Long.MAX_VALUE, maxAge);
        
        alertService.setRoleDao(new MockRoleDao("aba"));
        alertService.setupMaxAge();
        maxAge = alertService.getMaxAge();
        assertEquals(Long.MAX_VALUE, maxAge);
    }

    @Test
    public void testAutoRemove() throws InterruptedException {
        alertService.setMaxAge(50); // shorter than our 10ms sleep a few lines down
        
        AlarmAlert alert1 = new AlarmAlert(new Date(), new ResolvableTemplate("a"));
        alert1.setUserChecker(new NullUserChecker());
        AlarmAlert alert2 = new AlarmAlert(new Date(), new ResolvableTemplate("b"));
        alert2.setUserChecker(new NullUserChecker());
        alertService.add(alert1);
        int countForUser = alertService.getCountForUser(null);
        assertEquals(1, countForUser);
        
        Thread.sleep(100);
        
        alertService.add(alert2);
        // after 2 was added, 1 should have been deleted
        countForUser = alertService.getCountForUser(null);
        assertEquals(1, countForUser);
        
        Collection<IdentifiableAlert> all = alertService.getAll(null);
        assertEquals(1, all.size());
        
        IdentifiableAlert returnedAlert = all.iterator().next();
        assertEquals("b", returnedAlert.getMessage().getCode());
        
    }

    
    
    private class MockRoleDao implements RoleDao {
        private String returnValue;
        
        public MockRoleDao(String returnValue) {
            this.returnValue = returnValue;
        }

        @Override
        public String getGlobalPropertyValue(int rolePropertyID_) {
            return returnValue;
        }

        @Override
        public LiteYukonRole getLiteRole(Integer rolePropID) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LiteYukonRoleProperty[] getRoleProperties(int roleID_) {
            throw new UnsupportedOperationException();
        }
        
    }

}
