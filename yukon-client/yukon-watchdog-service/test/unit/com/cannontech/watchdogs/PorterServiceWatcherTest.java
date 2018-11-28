package com.cannontech.watchdogs;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MockConfigurationSource;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdogs.impl.PorterServiceWatcher;
import com.cannontech.watchdogs.impl.ServiceStatusWatchdogImpl.ServiceStatus;

public class PorterServiceWatcherTest {
    private PorterServiceWatcher watcher;

    class WatcherMockConfigurationSource extends MockConfigurationSource {
        @Override
        public boolean getBoolean(MasterConfigBoolean developmentMode) {
            return false;
        }
    }
    
    @Before
    public void setUp() throws NoSuchMethodException, SecurityException {
        watcher = new PorterServiceWatcher(new WatcherMockConfigurationSource());
    }

    @Test
    public void test_shouldSendWarning_firt_stopped() {
        boolean result = ReflectionTestUtils.invokeMethod(watcher, "shouldSendWarning", YukonServices.PORTER, ServiceStatus.STOPPED);
        assertTrue(!result);
    }

    @Test
    public void test_shouldSendWarning_running() {
        boolean result = ReflectionTestUtils.invokeMethod(watcher, "shouldSendWarning", YukonServices.PORTER, ServiceStatus.RUNNING);
        assertTrue(!result);
    }

    @Test
    public void test_shouldSendWarning_consecutive_second_stopped() {
        ReflectionTestUtils.invokeMethod(watcher, "shouldSendWarning", YukonServices.PORTER, ServiceStatus.STOPPED);
        boolean result = ReflectionTestUtils.invokeMethod(watcher, "shouldSendWarning", YukonServices.PORTER, ServiceStatus.STOPPED);
        assertTrue(result);
    }
    
    @Test
    public void test_shouldSendWarning_stopped_running() {
        ReflectionTestUtils.invokeMethod(watcher, "shouldSendWarning", YukonServices.PORTER, ServiceStatus.STOPPED);
        boolean result = ReflectionTestUtils.invokeMethod(watcher, "shouldSendWarning", YukonServices.PORTER, ServiceStatus.RUNNING);
        assertTrue(!result);
    }
    

}
