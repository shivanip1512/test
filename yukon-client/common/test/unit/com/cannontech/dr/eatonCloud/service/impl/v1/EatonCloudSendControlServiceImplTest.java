package com.cannontech.dr.eatonCloud.service.impl.v1;

import java.util.HashSet;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import com.cannontech.core.dao.DeviceDao;
import com.cannontech.dr.eatonCloud.service.impl.EatonCloudSendControlServiceImpl;
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;

public class EatonCloudSendControlServiceImplTest {
    
    @Test
    public void testSendInitialShedCommand() {
        EatonCloudSendControlServiceImpl service = new EatonCloudSendControlServiceImpl();
        
        LMEatonCloudScheduledCycleCommand command = new LMEatonCloudScheduledCycleCommand();
        command.setIsRampIn(true);
        command.setIsRampOut(true);
        command.setVirtualRelayId(1);
        command.setDutyCyclePeriod(2);
        
        DeviceDao mockDeviceDao = EasyMock.createMock(DeviceDao.class);
        
        service.sendInitialShedCommand(0, new HashSet<>(), command, 1);
    }
}
