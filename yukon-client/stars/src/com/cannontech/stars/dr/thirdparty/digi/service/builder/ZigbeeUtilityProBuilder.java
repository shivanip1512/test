package com.cannontech.stars.dr.thirdparty.digi.service.builder;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
import com.cannontech.stars.dr.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.stars.dr.thirdparty.digi.model.DigiGateway;
import com.cannontech.stars.dr.thirdparty.digi.model.ZigbeeThermostat;

public class ZigbeeUtilityProBuilder implements HardwareTypeExtensionProvider {

    private ZigbeeDeviceDao zigbeeDeviceDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private PaoDefinitionService paoDefinitionService;
    private AttributeService attributeService;
    
    @Override
    public HardwareType getType() {
        return HardwareType.UTILITY_PRO_ZIGBEE;
    }
    
    @Override
    public void retrieveDevice(HardwareDto hardwareDto) {
        //TODO Check to make sure deviceId was set before getting here. was liteInventoryBase.getDeviceID()       
        ZigbeeThermostat zigbeeThermostat = zigbeeDeviceDao.getZigbeeUtilPro(hardwareDto.getDeviceId()); 
        
        hardwareDto.setInstallCode(zigbeeThermostat.getInstallCode());
        
        LitePoint linkPt = attributeService.getPointForAttribute(zigbeeThermostat, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        hardwareDto.setCommissionedId(linkPt.getLiteID());
    }
    
    @Override
    public void validateDevice(HardwareDto hardwareDto, Errors errors) {
        /* Install Code */
        if (StringUtils.isBlank(hardwareDto.getInstallCode())) {
            errors.rejectValue("installCode", "yukon.web.modules.operator.hardware.error.required");
        }
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
        
        List<PointBase> points = paoDefinitionService.createAllPointsForPao(thermostat);
        MultiDBPersistent pointMulti = new MultiDBPersistent();
        pointMulti.getDBPersistentVector().addAll(points);
        
        try {
            PointUtil.insertIntoDB(pointMulti);
        } catch (TransactionException e) {
            //TODO
        }

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
    
    @Autowired
    public void setPaoDefinitionSerivice(PaoDefinitionService paoDefinitionService) {
        this.paoDefinitionService = paoDefinitionService;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}
