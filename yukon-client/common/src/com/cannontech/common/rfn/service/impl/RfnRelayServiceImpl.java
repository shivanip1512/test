package com.cannontech.common.rfn.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.common.rfn.model.RfnRelay;
import com.cannontech.common.rfn.service.RfnDeviceDeletionMessageService;
import com.cannontech.common.rfn.service.RfnRelayService;
import com.cannontech.core.dao.DeviceDao;

public class RfnRelayServiceImpl implements RfnRelayService {
    
    private static final Logger log = YukonLogManager.getLogger(RfnRelayServiceImpl.class); 
    
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private RfnDeviceDeletionMessageService rfnDeviceDeletionMessageService;

    @Override
    public Set<RfnRelay> getAllRelays() {
        
        List<RfnDevice> devices = rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfRelayTypes());
        Set<RfnRelay> relays = getRelaysFromDevices(devices);
        
        return relays;
    }
    
    public Set<RfnRelay> getRelaysOfType(PaoType type) {
        
        Set<RfnRelay> relays = getAllRelays()
                                   .stream()
                                   .filter(relay -> relay.getType().equals(type))
                                   .collect(Collectors.toSet());

        return relays;
    }
    
    @Override
    public Set<RfnRelay> searchRelays(RfnDeviceSearchCriteria criteria, List<PaoType> relayTypes) {
        
        List<RfnDevice> devices = rfnDeviceDao.searchDevicesByPaoTypes(relayTypes, criteria);
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
        try {
            rfnDeviceDeletionMessageService.sendRfnDeviceDeletionRequest(id);
            deviceDao.removeDevice(id);
            return true;
        } catch (Exception e) {
            log.error("Unable to delete relay with id " + id, e);
            return false;
        }

    }

}