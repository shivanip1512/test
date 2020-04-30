package com.cannontech.common.device.config.service.impl;

import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.IN_SYNC;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.OUT_OF_SYNC;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.UNKNOWN;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.UNREAD;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.UNASSIGNED;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.UNCONFIRMED;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction.ASSIGN;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction.UNASSIGN;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction.SEND;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction.READ;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus.IN_PROGRESS;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus.SUCCESS;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;
import org.junit.Test;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState;
import com.cannontech.common.device.config.model.DeviceConfigState;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;

/**
 * This unit test verifies that states transitions match the the state transition diagram in diagram in
 * https://confluence-prod.tcc.etn.com/display/EEST/Device+Configuration+Summary
 */
public class DeviceConfigServiceImplTest {

    private DeviceConfigServiceImpl impl = new DeviceConfigServiceImpl();
    private Map<Integer, DeviceConfigState> statesInDatabase = new HashMap<>();
    private int oneHour = 3600000;
    
    private SimpleDevice one = new SimpleDevice(1, PaoType.RFN420FL);
    private SimpleDevice two = new SimpleDevice(2, PaoType.RFN420FL);
    private SimpleDevice three = new SimpleDevice(3, PaoType.RFN420FL);
    private SimpleDevice four = new SimpleDevice(4, PaoType.RFN420FL);
    private SimpleDevice five = new SimpleDevice(5, PaoType.RFN420FL);
    private SimpleDevice six = new SimpleDevice(6, PaoType.RFN420FL);
    private SimpleDevice seven = new SimpleDevice(7, PaoType.MCT420CL);
    private SimpleDevice eight = new SimpleDevice(8, PaoType.MCT420CL);
    
    private List<SimpleDevice> all = List.of(one, two, three, four, five, six, seven, eight);
    
    {
        Instant startTime = Instant.now();
        Instant stopTime = Instant.now();
        stopTime = stopTime.plus(oneHour);
        //device with id 1 one will be missing from this map
        statesInDatabase.put(2, new DeviceConfigState(two.getDeviceId(), UNKNOWN, UNASSIGN, SUCCESS, startTime, null, null));
        statesInDatabase.put(3, new DeviceConfigState(three.getDeviceId(), UNREAD, ASSIGN, SUCCESS, startTime, stopTime, null));
        statesInDatabase.put(4, new DeviceConfigState(four.getDeviceId(), IN_SYNC, READ, SUCCESS, startTime, stopTime, null)); 
        statesInDatabase.put(5, new DeviceConfigState(five.getDeviceId(), OUT_OF_SYNC, READ, SUCCESS, startTime, stopTime, null));
        statesInDatabase.put(6, new DeviceConfigState(six.getDeviceId(), UNASSIGNED, READ, SUCCESS, startTime, stopTime, null));
        statesInDatabase.put(7, new DeviceConfigState(seven.getDeviceId(), UNCONFIRMED, SEND, SUCCESS, startTime, stopTime, null));
        statesInDatabase.put(8, new DeviceConfigState(eight.getDeviceId(), IN_SYNC, READ, IN_PROGRESS, startTime, stopTime, null));
    }
    
    @Test
    public final void build_new_states_for_assign_action() throws Exception {     
        // device is not in the states table -> UNREAD
        checkState("buildNewStatesForAssignAction", one, UNREAD);
        // UNKNOWN -> UNREAD
        checkState("buildNewStatesForAssignAction", two, UNREAD);
        // was UNREAD remains UNREAD
        checkState("buildNewStatesForAssignAction", three, UNREAD);
        // was IN_SYNC -> send verify
        checkState("buildNewStatesForAssignAction", four, null);
        // was OUT_OF_SYNC -> send verify
        checkState("buildNewStatesForAssignAction", five, null);
        // was UNASSIGNED -> send verify
        checkState("buildNewStatesForAssignAction", six, null);
        // was UNCONFIRMED remains UNCONFIRMED
        checkState("buildNewStatesForAssignAction", seven, UNCONFIRMED);
        // in progress -> NO CHANGE
        checkState("buildNewStatesForAssignAction", eight, null);
    }
    
    @Test
    public final void build_new_states_for_unassign_action() throws Exception {
        // device is not in states table -> NO CHANGE
        checkState("buildNewStatesForUnassignAction", one, null);
        // was UNKNOWN -> NO CHANGE
        checkState("buildNewStatesForUnassignAction", two, null);
        // was UNREAD -> UNKNOWN
        checkState("buildNewStatesForUnassignAction", three, UNKNOWN);
        // was IN_SYNC -> UNASSIGNED
        checkState("buildNewStatesForUnassignAction", four, UNASSIGNED);
        // was OUT_OF_SYNC -> UNASSIGNED
        checkState("buildNewStatesForUnassignAction", five, UNASSIGNED);
        // was UNASSIGNED -> send verify
        checkState("buildNewStatesForUnassignAction", six, null);
        // was UNCONFIRMED remains UNCONFIRMED
        checkState("buildNewStatesForUnassignAction", seven, UNCONFIRMED);
    }
    
    @Test
    public final void get_devices_to_verify() throws Exception {
        Method method = DeviceConfigServiceImpl.class.getDeclaredMethod("getDevicesToVerify", List.class, Map.class, List.class);
        method.setAccessible(true);
        List<SimpleDevice> expected = List.of(four, five, six);
        List<SimpleDevice> devicesToVerify = (List<SimpleDevice>) method.invoke(impl, all, statesInDatabase, List.of(IN_SYNC, OUT_OF_SYNC, UNASSIGNED));
        
        assertEquals(devicesToVerify, expected);
        
        expected = List.of(four, five, six);
        devicesToVerify = (List<SimpleDevice>) method.invoke(impl, all, statesInDatabase, List.of(IN_SYNC, OUT_OF_SYNC, UNASSIGNED));
        
        assertEquals(devicesToVerify, expected);
    }

    /**
     * Validates that stateAfterChange is the same as the new state returned by the service. If stateAfterChange is null, the state table will remain unchanged in the database.
     */
    private void checkState(String methodName, SimpleDevice device, ConfigState stateAfterChange)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method = DeviceConfigServiceImpl.class.getDeclaredMethod(methodName, List.class, Map.class, Instant.class, Instant.class);
        method.setAccessible(true);
        Set<DeviceConfigState> newStates = (Set<DeviceConfigState>) method.invoke(impl, List.of(device), statesInDatabase, Instant.now(), Instant.now());
        ConfigState newState = null;
        if(!newStates.isEmpty()) {
            newState = newStates.iterator().next().getState();
        }
        assertEquals(newState, stateAfterChange);
    }    
}

