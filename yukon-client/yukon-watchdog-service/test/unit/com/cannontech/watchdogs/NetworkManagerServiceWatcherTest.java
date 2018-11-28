package com.cannontech.watchdogs;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MockConfigurationSource;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdogs.impl.NetworkManagerWatcher;
import com.cannontech.watchdogs.impl.ServiceStatusWatchdogImpl.ServiceStatus;

public class NetworkManagerServiceWatcherTest {
    private NetworkManagerWatcher watcher;
    private NetworkManagerWatcher optionalWatcher;
    
    class WatcherMockConfigurationSource extends MockConfigurationSource {
        private boolean developmentMode = false;
        
        @Override
        public boolean getBoolean(MasterConfigBoolean value) {
            if (value.equals(MasterConfigBoolean.DEVELOPMENT_MODE))
                return developmentMode;
            else
                throw new UnsupportedOperationException("Only DEVELOPMENT_MODE is implemented");
        }

        public void setDevelopmentMode(boolean developmentMode) {
            this.developmentMode = developmentMode;
        }
    }

    @Before
    public void setUp() throws NoSuchMethodException, SecurityException {
        // This watcher marks NM as not optional, which is the standard case
        watcher = new NetworkManagerWatcher(new WatcherMockConfigurationSource());
        
        // This mock config source will report development mode = true, which makes this watcher consider NM as optional.
        WatcherMockConfigurationSource developmentModeConfigSource = new WatcherMockConfigurationSource();
        developmentModeConfigSource.setDevelopmentMode(true);
        optionalWatcher = new NetworkManagerWatcher(developmentModeConfigSource);
    }

    @Test
    public void test_shouldSendWarning_first_stopped() {
        boolean result = ReflectionTestUtils.invokeMethod(watcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.STOPPED);
        assertTrue(!result);
    }

    @Test
    public void test_shouldSendWarning_running() {
        boolean result = ReflectionTestUtils.invokeMethod(watcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.RUNNING);
        assertTrue(!result);
    }

    @Test
    public void test_shouldSendWarning_consecutive_second_stopped() {
        ReflectionTestUtils.invokeMethod(watcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.STOPPED);
        boolean result = ReflectionTestUtils.invokeMethod(watcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.STOPPED);
        assertTrue(result);
    }
    
    @Test
    public void test_shouldSendWarning_stopped_running() {
        ReflectionTestUtils.invokeMethod(watcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.STOPPED);
        boolean result = ReflectionTestUtils.invokeMethod(watcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.RUNNING);
        assertTrue(!result);
    }
    
    
    // All of the cases below this line use the Optional watcher which simulates development mode = true
    @Test
    public void test_shouldSendWarning_first_stopped_optional() {
        boolean result = ReflectionTestUtils.invokeMethod(optionalWatcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.STOPPED);
        assertTrue(!result);
    }

    @Test
    public void test_shouldSendWarning_running_optional() {
        boolean result = ReflectionTestUtils.invokeMethod(optionalWatcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.RUNNING);
        assertTrue(!result);
    }

    // In this case it is Optional due to the master.cfg setting, so no messages will go out.
    @Test
    public void test_shouldSendWarning_consecutive_second_stopped_optional() {
        ReflectionTestUtils.invokeMethod(optionalWatcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.STOPPED);
        boolean result = ReflectionTestUtils.invokeMethod(optionalWatcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.STOPPED);
        assertTrue(!result);
    }
    
    @Test
    public void test_shouldSendWarning_stopped_running_optional() {
        ReflectionTestUtils.invokeMethod(optionalWatcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.STOPPED);
        boolean result = ReflectionTestUtils.invokeMethod(optionalWatcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.RUNNING);
        assertTrue(!result);
    }
    
    @Test
    public void test_shouldSendWarning_running_stopped_optional() {
        ReflectionTestUtils.invokeMethod(optionalWatcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.RUNNING);
        boolean result = ReflectionTestUtils.invokeMethod(optionalWatcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.STOPPED);
        assertTrue(!result);
    }

    // In this case it is Optional due to the master.cfg setting, so we must see it running, then stopped twice to have an alert.
    @Test
    public void test_shouldSendWarning_running_stopped_stopped_optional() {
        ReflectionTestUtils.invokeMethod(optionalWatcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.RUNNING);
        ReflectionTestUtils.invokeMethod(optionalWatcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.STOPPED);
        boolean result = ReflectionTestUtils.invokeMethod(optionalWatcher, "shouldSendWarning", YukonServices.NETWORKMANAGER, ServiceStatus.STOPPED);
        assertTrue(result);
    }
}
