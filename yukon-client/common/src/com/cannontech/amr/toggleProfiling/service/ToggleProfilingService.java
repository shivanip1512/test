package com.cannontech.amr.toggleProfiling.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.user.YukonUserContext;

public interface ToggleProfilingService {
    public void startProfilingForDevice(int deviceId, int channelNum);
    public void stopProfilingForDevice(int deviceId, int channelNum);

    public void scheduleStartProfilingForDevice(int deviceId, int channelNum, Date toggleDate,
                                                YukonUserContext userContext);

    public void scheduleStopProfilingForDevice(int deviceId, int channelNum, Date toggleDate,
                                               YukonUserContext userContext);

    public boolean getToggleValueForDevice(int deviceId, int channelNum);

    public boolean getToggleValueForDevice(DeviceLoadProfile deviceLoadProfile, int channelNum);

    public DeviceLoadProfile getDeviceLoadProfile(int deviceId);

    public List<Map<String, Object>> getToggleJobInfos(int deviceId, int channel);

    public void disableScheduledStart(int deviceId, int channel);
    public void disableScheduledStop(int deviceId, int channel);

    public Instant getScheduledStart(int deviceId, int channel);
}
