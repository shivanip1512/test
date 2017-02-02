package com.cannontech.common.rfn.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.common.rfn.model.RfnRelay;
import com.cannontech.common.rfn.service.RfnRelayService;

public class RfnRelayServiceImpl implements RfnRelayService {
    
    @Autowired private RfnDeviceDao rfnDeviceDao;
    
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

}