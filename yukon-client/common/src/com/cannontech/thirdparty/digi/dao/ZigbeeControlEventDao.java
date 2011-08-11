package com.cannontech.thirdparty.digi.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.thirdparty.digi.model.ZigbeeControlledDevice;

public interface ZigbeeControlEventDao {
    public void createNewEventMapping(int eventId, int groupId, Instant startTime);
    
    public void associateControlHistory(int eventId, int controlHistoryId);
    
    public int findCurrentEventId(int groupId) throws NotFoundException;    
    
    public void insertDeviceControlEvent(int eventId, int deviceId);
    
    public void updateDeviceAck(boolean ack, int eventId, int deviceId);
    
    public void updateDeviceStartTime(Instant startTime, int eventId, int deviceId);
    
    public void updateDeviceStopTime(Instant stopTime, int eventId, int deviceId, boolean canceled);
    
    public List<ZigbeeControlledDevice> getDeviceEvents(Instant startDate, Instant stopDate);
}
