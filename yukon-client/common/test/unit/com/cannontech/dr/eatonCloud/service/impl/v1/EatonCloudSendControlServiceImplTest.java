package com.cannontech.dr.eatonCloud.service.impl.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.events.loggers.EatonCloudEventLogService;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.TimeService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandResponseV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.service.impl.EatonCloudSendControlServiceImpl;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudDataReadService;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;

public class EatonCloudSendControlServiceImplTest {
    @Mock
    DeviceDao deviceDao;
    @Mock
    IDatabaseCache dbCache;
    @Mock
    EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Mock
    RecentEventParticipationDao recentEventParticipationDao;
    @Mock
    EatonCloudEventLogService eatonCloudEventLogService;
    @Mock
    TimeService timeService;
    @Mock
    SmartNotificationEventCreationService smartNotificationEventCreationService;
    @Mock
    GlobalSettingDao settingDao;
    @Mock
    ConfigurationSource configurationSource;
    @Mock
    ScheduledExecutor scheduledExecutor;
    @Mock
    EatonCloudDataReadService eatonCloudDataReadService;
    
    @InjectMocks
    EatonCloudSendControlServiceImpl service;
    
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void sendInitialShedCommand_OneDeviceResponseOK_SUCCESS_RECEIVED() {
        Instant now = Instant.now();
        when(timeService.now()).thenReturn(now);
        LMEatonCloudScheduledCycleCommand command = createCommand();
        
        Map<Integer, String> guids = getGuids();
        Set<Integer> deviceIds = new HashSet<Integer>();
        when(deviceDao.getGuids(deviceIds)).thenReturn(guids);
        
        EatonCloudCommandResponseV1 response = new EatonCloudCommandResponseV1(HttpStatus.OK.value(), null);
        when(eatonCloudCommunicationService.sendCommand("test",
                new EatonCloudCommandRequestV1(anyString(), any()))).thenReturn(response);
        
        service.sendInitialShedCommand(0, new HashSet<>(), command, 1);
        
        verify(recentEventParticipationDao, times(1)).updateDeviceControlEvent("1", 1, ControlEventDeviceStatus.SUCCESS_RECEIVED,
                now, null, null);
        verify(eatonCloudEventLogService, times(1)).sendShed(null, "test", "1", "1", 0, 2, 0, 1);
        verify(smartNotificationEventCreationService, times(0)).send(any(), anyList());
    }
    
    @Test
    public void sendInitialShedCommand_OneDeviceResponseBAD_REQUEST_FAILED_WILL_RETRY() {        
        Instant now = Instant.now();
        when(timeService.now()).thenReturn(now);
        
        LMEatonCloudScheduledCycleCommand command = createCommand();
        
        List<LiteYukonPAObject> groups = getGroups();
        when(dbCache.getAllLMGroups()).thenReturn(groups);
        
        List<LiteYukonPAObject> programs = getPrograms();
        when(dbCache.getAllLMPrograms()).thenReturn(programs);
        
        Map<Integer, String> guids = getGuids();
        Set<Integer> deviceIds = new HashSet<Integer>();
        deviceIds.add(1);
        when(deviceDao.getGuids(deviceIds)).thenReturn(guids);
        
        
        EatonCloudCommandResponseV1 response = new EatonCloudCommandResponseV1(HttpStatus.BAD_REQUEST.value(), null);
        when(eatonCloudCommunicationService.sendCommand("test",
                new EatonCloudCommandRequestV1(anyString(), any()))).thenReturn(response);
        Set<Integer> devices = new HashSet<>();
        devices.add(1);
        service.sendInitialShedCommand(0, devices, command, 1);
        
        verify(smartNotificationEventCreationService, times(1)).send(any(), anyList());
        verify(recentEventParticipationDao, times(1)).updateDeviceControlEvent("1", 1, ControlEventDeviceStatus.FAILED_WILL_RETRY,
                now, null, null);
        verify(eatonCloudEventLogService, times(1)).sendShedFailed(null, "test", "1", "1", 0, 2, 0, 1,"See log for details");
    }
    
    @Test
    public void sendInitialShedCommand_OneDeviceResponseNoConnection_FAILED_WILL_RETRY() {
        Instant now = Instant.now();
        when(timeService.now()).thenReturn(now);
        
        
        LMEatonCloudScheduledCycleCommand command = createCommand();
        
        List<LiteYukonPAObject> groups = getGroups();
        when(dbCache.getAllLMGroups()).thenReturn(groups);
        
        List<LiteYukonPAObject> programs = getPrograms();
        when(dbCache.getAllLMPrograms()).thenReturn(programs);
        
        Map<Integer, String> guids = getGuids();
        Set<Integer> deviceIds = new HashSet<Integer>();
        deviceIds.add(1);
        when(deviceDao.getGuids(deviceIds)).thenReturn(guids);
        
        when(eatonCloudCommunicationService.sendCommand("test",
                new EatonCloudCommandRequestV1(anyString(), any()))).thenThrow(new EatonCloudCommunicationExceptionV1());
        Set<Integer> devices = new HashSet<>();
        devices.add(1);
        service.sendInitialShedCommand(0, devices, command, 1);
        
        verify(smartNotificationEventCreationService, times(1)).send(any(), anyList());
        verify(recentEventParticipationDao, times(1)).updateDeviceControlEvent("1", 1, ControlEventDeviceStatus.FAILED_WILL_RETRY,
                now, "A communication error has occurred. Please see logs for more details", null);
        verify(eatonCloudEventLogService, times(1)).sendShedFailed(null, "test", "1", "1", 0, 2, 0, 1,"A communication error has occurred. Please see logs for more details");
    }

    @Test
    //method/condition/result
    public void init_NullSiteGuid_EarlyReturn() {
        when(settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID)).thenReturn(null);
        service.init();
        verify(recentEventParticipationDao, times(0)).failWillRetryDevices(null);
    }
    
    @Test
    public void testInit_NoDevicesToRead_NoDataCollected() {
        // TODO Use ArgumentCaptor to force a run of the executor.
        when(settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID)).thenReturn("1");
        Set<Integer> devicesToRead = new HashSet<Integer>();
        devicesToRead.add(1);
        when(recentEventParticipationDao.getDeviceIdsByExternalEventIdAndStatuses(1, List.of(ControlEventDeviceStatus.SUCCESS_RECEIVED))).thenReturn(devicesToRead);
        service.init();
        verify(configurationSource, times(1)).getInteger(MasterConfigInteger.EATON_CLOUD_NOTIFICATION_COMMAND_FAILURE_PERCENT, 25);
        verify(recentEventParticipationDao, times(1)).failWillRetryDevices(null);
        Range<Instant> range = null;
        verify(eatonCloudDataReadService, times(1)).collectDataForRead(devicesToRead, range, "READ AFTER SHED");
    }
    
    private LMEatonCloudScheduledCycleCommand createCommand() {
        LMEatonCloudScheduledCycleCommand command = new LMEatonCloudScheduledCycleCommand();
        command.setIsRampIn(true);
        command.setIsRampOut(true);
        command.setVirtualRelayId(1);
        command.setDutyCyclePeriod(2);
        command.setDutyCyclePercentage(0);
        command.setCriticality(0);
        command.setGroupId(0);
        return command;
    }
    
    private List<LiteYukonPAObject> getPrograms() {
        List<LiteYukonPAObject> programs = new ArrayList<>();
        LiteYukonPAObject program = new LiteYukonPAObject(0);
        program.setPaoName("Program 0");
        programs.add(program);
        return programs;
    }

    private List<LiteYukonPAObject> getGroups() {
        List<LiteYukonPAObject> groups = new ArrayList<>();
        LiteYukonPAObject group = new LiteYukonPAObject(0);
        group.setPaoName("Group 0");
        groups.add(group);
        return groups;
    }
    
    private Map<Integer, String> getGuids() {
        Map<Integer, String> guids = new HashMap<>();
        guids.put(1, "test");
        return guids;
    }
}
