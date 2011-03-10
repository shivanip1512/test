package com.cannontech.core.dao;

import java.util.Set;

import com.cannontech.database.data.device.lm.SepDeviceClass;

public interface SepDeviceClassDao {

    public Set<SepDeviceClass> getSepDeviceClassesByDeviceId(int deviceId);

    public int deleteByDeviceId(int deviceId);

    public void save(Set<SepDeviceClass> sepDeviceClasses, int deviceId);

}
