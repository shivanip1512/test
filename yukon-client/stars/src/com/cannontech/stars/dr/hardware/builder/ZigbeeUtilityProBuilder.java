package com.cannontech.stars.dr.hardware.builder;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.ZigbeeThermostat;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.ZigbeeDeviceDao;
import com.cannontech.stars.dr.hardware.model.HardwareDto;

public class ZigbeeUtilityProBuilder implements HardwareBuilder {

    private ZigbeeDeviceDao zigbeeDeviceDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    
    @Override
    public HardwareType getType() {
        return HardwareType.UTILITY_PRO_ZIGBEE;
    }
    
    @Override
    public void createDevice(HardwareDto hardwareDto) {
        ZigbeeThermostat thermostat = new ZigbeeThermostat();
        
        thermostat.setInstallCode(hardwareDto.getInstallCode());
        thermostat.setPaoIdentifier(new PaoIdentifier(hardwareDto.getDeviceId(), PaoType.ZIGBEEUTILPRO));
        
        //Do we really want to use the Serial number as the name?
        thermostat.setName(hardwareDto.getSerialNumber());
        
        zigbeeDeviceDao.createZigbeeUtilPro(thermostat);
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), hardwareDto.getDeviceId());
        
        return;
    }

    @Override
    public void updateDevice(HardwareDto hardwareDto) {
        ZigbeeThermostat thermostat = new ZigbeeThermostat();
        
        thermostat.setInstallCode(hardwareDto.getInstallCode());
        thermostat.setPaoIdentifier(new PaoIdentifier(hardwareDto.getDeviceId(), PaoType.ZIGBEEUTILPRO));
        thermostat.setName(hardwareDto.getSerialNumber());
        
        zigbeeDeviceDao.updateZigbeeUtilPro(thermostat);
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), hardwareDto.getDeviceId());
        
        return;
    }
    
    @Override
    public void deleteDevice(HardwareDto hardwareDto) {
        ZigbeeThermostat thermostat = new ZigbeeThermostat();
        
        thermostat.setInstallCode(hardwareDto.getInstallCode());
        thermostat.setPaoIdentifier(new PaoIdentifier(hardwareDto.getDeviceId(), PaoType.ZIGBEEUTILPRO));
        
        zigbeeDeviceDao.deleteZigbeeUtilPro(thermostat);
    }

    @Autowired
    public void setZigbeeDeviceDao(ZigbeeDeviceDao zigbeeDeviceDao) {
        this.zigbeeDeviceDao = zigbeeDeviceDao;
    }
    
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
}
