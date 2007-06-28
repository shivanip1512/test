package com.cannontech.common.device.profilePeak.dao;

import java.util.List;

import com.cannontech.common.device.profilePeak.model.ProfilePeakResult;
import com.cannontech.common.device.profilePeak.model.ProfilePeakResultType;

public interface ProfilePeakDao {

    public ProfilePeakResult getResult(int deviceId, ProfilePeakResultType type);

    public void saveResults(int deviceId, List<ProfilePeakResult> result);
}
