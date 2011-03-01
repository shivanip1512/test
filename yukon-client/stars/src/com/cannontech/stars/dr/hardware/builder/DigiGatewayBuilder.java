package com.cannontech.stars.dr.hardware.builder;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.DigiGateway;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.GatewayDeviceDao;
import com.cannontech.stars.dr.hardware.model.HardwareDto;

public class DigiGatewayBuilder implements HardwareBuilder {

    private GatewayDeviceDao gatewayDeviceDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    
    @Override
    public void createDevice(HardwareDto hardwareDto) {
        DigiGateway digiGateway = createDigiGateway(hardwareDto);
        
        gatewayDeviceDao.createDigiGateway(digiGateway);
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), hardwareDto.getDeviceId());
    }

    @Override
    public void deleteDevice(HardwareDto hardwareDto) {
        DigiGateway digiGateway = createDigiGateway(hardwareDto);
        
        gatewayDeviceDao.deleteDigiGateway(digiGateway);
    }

    @Override
    public HardwareType getType() {
        return HardwareType.DIGI_GATEWAY;
    }

    @Override
    public void updateDevice(HardwareDto hardwareDto) {
        DigiGateway digiGateway = createDigiGateway(hardwareDto);
        
        gatewayDeviceDao.updateDigiGateway(digiGateway);
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), hardwareDto.getDeviceId());
    }

    private DigiGateway createDigiGateway(HardwareDto hardwareDto) {
        DigiGateway digiGateway = new DigiGateway();
        
        digiGateway.setPaoIdentifier(new PaoIdentifier(hardwareDto.getDeviceId(), PaoType.DIGIGATEWAY));
        digiGateway.setFirmwareVersion(hardwareDto.getFirmwareVersion());
        digiGateway.setMacAddress(hardwareDto.getMacAddress());
        
        return digiGateway;
    }
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
    
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
}
