package com.cannontech.stars.dr.hardware.builder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.DigiGateway;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.GatewayDeviceDao;
import com.cannontech.stars.dr.hardware.model.HardwareDto;

public class DigiGatewayBuilder implements HardwareBuilder {

    private GatewayDeviceDao gatewayDeviceDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private PaoDefinitionService paoDefinitionService;
    
    @Override
    public void createDevice(HardwareDto hardwareDto) {
        DigiGateway digiGateway = createDigiGateway(hardwareDto);
        
        gatewayDeviceDao.createDigiGateway(digiGateway);
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), hardwareDto.getDeviceId());
        
        List<PointBase> points = paoDefinitionService.createAllPointsForPao(digiGateway);
        MultiDBPersistent pointMulti = new MultiDBPersistent();
        pointMulti.getDBPersistentVector().addAll(points);
        
        try {
            PointUtil.insertIntoDB(pointMulti);
        } catch (TransactionException e) {
            //TODO
        }
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
    
    @Autowired
    public void setPaoDefinitionSerivice(PaoDefinitionService paoDefinitionService) {
        this.paoDefinitionService = paoDefinitionService;
    }
}
