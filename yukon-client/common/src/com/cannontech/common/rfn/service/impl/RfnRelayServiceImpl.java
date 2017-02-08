package com.cannontech.common.rfn.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.common.rfn.model.RfnRelay;
import com.cannontech.common.rfn.service.RfnRelayService;
import com.cannontech.core.dao.DeviceDao;

public class RfnRelayServiceImpl implements RfnRelayService {
    
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private DeviceDao deviceDao;

    @Override
    public Set<RfnRelay> getAllRelays() {
        
        List<RfnDevice> devices = rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfRelayTypes());
        Set<RfnRelay> relays = getRelaysFromDevices(devices);
        
        return relays;
    }
    
    @Override
    public Set<RfnRelay> searchRelays(RfnDeviceSearchCriteria criteria) {
        
        List<RfnDevice> devices = rfnDeviceDao.searchDevicesByPaoTypes(PaoType.getRfRelayTypes(), criteria);
        Set<RfnRelay> relays = getRelaysFromDevices(devices);
        
        return relays;
    }
    
    private Set<RfnRelay> getRelaysFromDevices(List<RfnDevice> devices) {
        Set<RfnRelay> relays = new HashSet<RfnRelay>();
        devices.forEach(device -> {
            relays.add(RfnRelay.of(device));
        });
        return relays;
    }

    @Override
    public boolean deleteRelay(int id) {
        RfnDevice device = rfnDeviceDao.getDeviceForId(id);
        try {
            deviceDao.removeDevice(device);
            return true;
        } catch (Exception e) {
            Log.error(e);
            return false;
        }

    }

}