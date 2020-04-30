package com.cannontech.common.device.config.service.impl;

import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.IN_SYNC;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.OUT_OF_SYNC;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.UNKNOWN;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.UNREAD;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.UNASSIGNED;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState.UNCONFIRMED;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction.ASSIGN;
import static com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastAction.SEND;
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
    Map<Integer, DeviceConfigState> statesInDatabase = new HashMap<>();
    Instant startTime = Instant.now();
    Instant stopTime = Instant.now();
    private int oneHour = 3600000;
    {
        stopTime = stopTime.plus(oneHour);
        //device with id 1 one will be missing from this map
        statesInDatabase.put(2, new DeviceConfigState(2, UNKNOWN, SEND, IN_PROGRESS, startTime, null, null));
        statesInDatabase.put(3, new DeviceConfigState(3, UNREAD, ASSIGN, SUCCESS, startTime, stopTime, null));
        statesInDatabase.put(4, new DeviceConfigState(4, IN_SYNC, ASSIGN, SUCCESS, startTime, stopTime, null)); 
        statesInDatabase.put(5, new DeviceConfigState(5, OUT_OF_SYNC, ASSIGN, SUCCESS, startTime, stopTime, null));
        statesInDatabase.put(6, new DeviceConfigState(6, UNASSIGNED, ASSIGN, SUCCESS, startTime, stopTime, null));
        statesInDatabase.put(7, new DeviceConfigState(7, UNCONFIRMED, ASSIGN, SUCCESS, startTime, stopTime, null));
    }
    
    @Test
    public final void build_new_states_for_assign_action() throws Exception {
        try {
            
            //Starting state:Unknown Action:Assign Status:Success New state:Unread
            //device is not in the database 
            checkState("buildNewStatesForAssignAction", new SimpleDevice(1,  PaoType.RFN420FL), UNREAD);
            
            //Starting state:Unknown Action:Assign Status:In Progress
            //no change, action is not allowed while status is in In Progress
            checkState("buildNewStatesForAssignAction", new SimpleDevice(2,  PaoType.RFN420FL), null);
            
            //Starting state:Unread Action:Assign Status:Success New state:Unread
            //State remains the same, other table info changes such as start and stop time etc
            checkState("buildNewStatesForAssignAction", new SimpleDevice(3,  PaoType.RFN420FL), UNREAD);
            
            //The cases below we are sending a verify, the state table will not change by assign
            checkState("buildNewStatesForAssignAction", new SimpleDevice(4,  PaoType.RFN420FL), null);
            checkState("buildNewStatesForAssignAction", new SimpleDevice(5,  PaoType.RFN420FL), null);
            checkState("buildNewStatesForAssignAction", new SimpleDevice(6,  PaoType.RFN420FL), null);
            
            //Starting state:Unconfirmed Action:Assign Status:Success New state:Unconfirmed
            //State remains the same, other table info changes such as start and stop time etc
            checkState("buildNewStatesForAssignAction", new SimpleDevice(7,  PaoType.MCT420CL), UNCONFIRMED);
            
        } catch (Exception e) {
            throw e;
        }
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
    
    @Test
    public final void build_new_states_for_unassign_action() {
        
    }
}

