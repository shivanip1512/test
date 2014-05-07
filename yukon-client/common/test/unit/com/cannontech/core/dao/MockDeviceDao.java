package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class MockDeviceDao implements DeviceDao {
    @Override
    public SimpleDevice getYukonDevice(int paoId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public SimpleDevice getYukonDevice(LiteYukonPAObject yukonPAObject) {
        throw new MethodNotImplementedException();
    }

    @Override
    public SimpleDevice getYukonDeviceObjectById(int deviceId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public SimpleDevice getYukonDeviceObjectByName(String name) {
        throw new MethodNotImplementedException();
    }

    @Override
    public SimpleDevice findYukonDeviceObjectByName(String name) {
        throw new MethodNotImplementedException();
    }

    @Override
    public SimpleDevice getYukonDeviceObjectByMeterNumber(String meterNumber) {
        throw new MethodNotImplementedException();
    }

    @Override
    public SimpleDevice getYukonDeviceObjectByAddress(Long address) {
        throw new MethodNotImplementedException();
    }

    @Override
    public LiteDeviceMeterNumber getLiteDeviceMeterNumber(int deviceID) {
        throw new MethodNotImplementedException();
    }

    @Override
    public LiteYukonPAObject getLiteYukonPaobjectByMeterNumber(String meterNumber) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LiteYukonPAObject> getLiteYukonPaobjectListByMeterNumber(String meterNumber) {
        throw new MethodNotImplementedException();
    }

    @Override
    public LiteYukonPAObject getLiteYukonPaobjectByDeviceName(String deviceName) {
        throw new MethodNotImplementedException();
    }

    @Override
    public LiteYukonPAObject getLiteYukonPAObject(String deviceName, int category, int paoClass,
                                                  int type) {
        throw new MethodNotImplementedException();
    }

    @Override
    public LiteYukonPAObject getLiteYukonPAObject(String deviceName, String category,
                                                  String paoClass, String type) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Integer> getDevicesByPort(int portId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Integer> getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void enableDevice(YukonDevice device) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void disableDevice(YukonDevice device) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void removeDevice(YukonDevice device) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void changeRoute(YukonDevice device, int newRouteId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void changeName(YukonDevice device, String newName) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void changeAddress(YukonDevice device, int newAddress) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void changeMeterNumber(YukonDevice device, String newMeterNumber) {
        throw new MethodNotImplementedException();
    }

    @Override
    public String getFormattedName(YukonDevice device) {
        throw new MethodNotImplementedException();
    }

    @Override
    public String getFormattedName(int deviceId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public SimpleDevice getYukonDeviceForDevice(DeviceBase oldDevice) {
        throw new MethodNotImplementedException();
    }

    @Override
    public PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader() {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<SimpleDevice> getDevicesForRouteId(int routeId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public int getRouteDeviceCount(int routeId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<SimpleDevice> getYukonDeviceObjectByIds(Iterable<Integer> ids) {
        throw new MethodNotImplementedException();
    }

}
