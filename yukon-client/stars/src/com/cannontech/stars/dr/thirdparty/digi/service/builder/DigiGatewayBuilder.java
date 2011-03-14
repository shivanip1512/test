package com.cannontech.stars.dr.thirdparty.digi.service.builder;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.stars.dr.thirdparty.digi.model.DigiGateway;

public class DigiGatewayBuilder implements HardwareTypeExtensionProvider {

    private GatewayDeviceDao gatewayDeviceDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private PaoDefinitionService paoDefinitionService;
    private AttributeService attributeService;
    
    @Override
    public void retrieveDevice(HardwareDto hardwareDto) {
        //TODO Check to make sure deviceId was set before getting here. was liteInventoryBase.getDeviceID()
        DigiGateway digiGateway = gatewayDeviceDao.getDigiGateway(hardwareDto.getDeviceId());
        
        hardwareDto.setMacAddress(digiGateway.getMacAddress());
        hardwareDto.setFirmwareVersion(digiGateway.getFirmwareVersion());
        
        LitePoint linkPt = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        hardwareDto.setCommissionedId(linkPt.getLiteID());
    }
    
    @Override
    public void validateDevice(HardwareDto hardwareDto, Errors errors) {        

        /* MAC Address*/
        if (StringUtils.isBlank(hardwareDto.getMacAddress())) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.required");
        }
        
        /* Firmware Version */
        if (StringUtils.isBlank(hardwareDto.getFirmwareVersion())) {
            errors.rejectValue("firmwareVersion", "yukon.web.modules.operator.hardware.error.required");
        }
    }
    
    @Override
    public void createDevice(HardwareDto hardwareDto) {
        DigiGateway digiGateway = buildDigiGateway(hardwareDto);
        
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
        DigiGateway digiGateway = buildDigiGateway(hardwareDto);
        
        gatewayDeviceDao.deleteDigiGateway(digiGateway);
    }

    @Override
    public HardwareType getType() {
        return HardwareType.DIGI_GATEWAY;
    }

    @Override
    public void updateDevice(HardwareDto hardwareDto) {
        DigiGateway digiGateway = buildDigiGateway(hardwareDto);
        
        gatewayDeviceDao.updateDigiGateway(digiGateway);
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), hardwareDto.getDeviceId());
    }

    private DigiGateway buildDigiGateway(HardwareDto hardwareDto) {
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
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}
