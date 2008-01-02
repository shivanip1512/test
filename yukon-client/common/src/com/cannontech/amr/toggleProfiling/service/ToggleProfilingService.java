package com.cannontech.amr.toggleProfiling.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.device.DeviceLoadProfile;

public interface ToggleProfilingService {
    
    public void toggleProfilingForDevice(int deviceId, int channelNum, boolean newToggleVal);
    
    public void scheduleToggleProfilingForDevice(int deviceId, int channelNum, boolean newToggleVal, Date toggleDate, LiteYukonUser user);
    
    public boolean getToggleValueForDevice(int deviceId, int channelNum);
    
    public boolean getToggleValueForDevice(DeviceLoadProfile deviceLoadProfile, int channelNum);
    
    public DeviceLoadProfile getDeviceLoadProfile(int deviceId);
    
    public List<Map<String, Object>> getToggleJobInfos(int deviceId, int channel);
    public void disableScheduledJob(int deviceId, int channel, boolean newToggleVal);

}
