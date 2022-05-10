package com.cannontech.dr.eatonCloud.service.impl.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import com.cannontech.common.events.loggers.EatonCloudEventLogService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.TimeService;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandResponseV1;
import com.cannontech.dr.eatonCloud.service.impl.EatonCloudSendControlServiceImpl;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
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
    
    @InjectMocks
    EatonCloudSendControlServiceImpl service;

    @Test
    public void testSendInitialShedCommand() {
        MockitoAnnotations.openMocks(this);
        
        when(timeService.now()).thenReturn(Instant.now());
        
        
        LMEatonCloudScheduledCycleCommand command = new LMEatonCloudScheduledCycleCommand();
        command.setIsRampIn(true);
        command.setIsRampOut(true);
        command.setVirtualRelayId(1);
        command.setDutyCyclePeriod(2);
        command.setDutyCyclePercentage(0);
        command.setCriticality(0);
        command.setGroupId(0);
        
        Map<Integer, String> guids = new HashMap<>();
        guids.put(1, "test");
        Set<Integer> deviceIds = new HashSet<Integer>(1);
        when(deviceDao.getGuids(deviceIds)).thenReturn(guids);
        
        
        EatonCloudCommandResponseV1 response = new EatonCloudCommandResponseV1(HttpStatus.OK.value(), null);
        when(eatonCloudCommunicationService.sendCommand("test",
                new EatonCloudCommandRequestV1(anyString(), any()))).thenReturn(response);
        
        service.sendInitialShedCommand(0, new HashSet<>(), command, 1);
        
        Instant now = Instant.now();
        verify(recentEventParticipationDao, times(1)).updateDeviceControlEvent("1", 1, ControlEventDeviceStatus.SUCCESS_RECEIVED,
                now, null, null);
        verify(eatonCloudEventLogService, times(1)).sendShed(null, "1", "1", "1", 0, 2, 0, 1);
        
        /*
         *      eatonCloudEventLogService.sendShed(deviceName,
                            guid,
                            eventId.toString(),
                            String.valueOf(tryNumber),
                            command.getDutyCyclePercentage(),
                            command.getDutyCyclePeriod(),
                            command.getCriticality(),
                            command.getVirtualRelayId());
                            
                            LMEatonCloudScheduledCycleCommand[groupId=<null>,
                            controlStartDateTime=<null>,controlEndDateTime=<null>,isRampIn=true,isRampOut=true,cyclingOption=<null>,
                            dutyCyclePercentage=<null>,dutyCyclePeriod=2,criticality=<null>,virtualRelayId=1]

         */
    }
}
