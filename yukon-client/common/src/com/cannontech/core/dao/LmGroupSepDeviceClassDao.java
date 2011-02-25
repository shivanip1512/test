package com.cannontech.core.dao;

import java.util.Set;

import com.cannontech.database.data.device.lm.SepDeviceClass;

public interface LmGroupSepDeviceClassDao {

    public Set<SepDeviceClass> getClassSetByDeviceId(int deviceId);

    public int removeByDeviceId(int deviceId);

    public void save(Set<SepDeviceClass> deviceClassList, int deviceId);

}
