package com.cannontech.stars.dr.thirdparty.digi.service.builder;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.service.PaoCreationService;
import com.cannontech.common.pao.service.PaoTemplate;
import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.common.pao.service.providers.fields.YukonPaObjectFields;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.dao.provider.fields.UtilityProZigbeeFields;
import com.cannontech.thirdparty.digi.model.ZigbeeThermostat;
import com.google.common.collect.ClassToInstanceMap;

public class ZigbeeUtilityProBuilder implements HardwareTypeExtensionProvider {

    private PaoCreationService paoCreationService;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private DeviceDao deviceDao;
    private AttributeService attributeService;
    
    
    @Override
    public HardwareType getType() {
        return HardwareType.UTILITY_PRO_ZIGBEE;
    }
    
    @Override
    public void retrieveDevice(HardwareDto hardwareDto) {
        ZigbeeThermostat zigbeeThermostat = zigbeeDeviceDao.getZigbeeUtilPro(hardwareDto.getDeviceId()); 
        
        hardwareDto.setInstallCode(zigbeeThermostat.getInstallCode());
        hardwareDto.setMacAddress(zigbeeThermostat.getMacAddress());
        
        LitePoint linkPt = attributeService.getPointForAttribute(zigbeeThermostat, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        hardwareDto.setCommissionedId(linkPt.getLiteID());
    }
    
    @Override
    public void validateDevice(HardwareDto hardwareDto, Errors errors) {
        /* Install Code */
        if (StringUtils.isBlank(hardwareDto.getInstallCode())) {
            errors.rejectValue("installCode", "yukon.web.modules.operator.hardware.error.required");
        }
        
        /* MAC Address*/
        if (StringUtils.isBlank(hardwareDto.getMacAddress())) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.required");
        }
    }
    
    @Override
    public void createDevice(HardwareDto hardwareDto) {
        //Build up all the fields for inserting a Zigbee Util Pro.        
        YukonPaObjectFields yukonPaObjectFields = new YukonPaObjectFields();
        //Serial Number is unique, using that as the PaoName
        yukonPaObjectFields.setName(hardwareDto.getSerialNumber());
        
        UtilityProZigbeeFields tStatFields = new UtilityProZigbeeFields();
        tStatFields.setInstallCode(hardwareDto.getInstallCode());
        tStatFields.setMacAddress(hardwareDto.getMacAddress());
        
        //Build Template and call Pao Creation Service
        ClassToInstanceMap<PaoTemplatePart> paoFields = paoCreationService.createFieldMap();
        paoFields.put(UtilityProZigbeeFields.class, tStatFields);
        paoFields.put(YukonPaObjectFields.class, yukonPaObjectFields);
        
        PaoTemplate paoTemplate = new PaoTemplate(PaoType.ZIGBEEUTILPRO, paoFields);
        
        PaoIdentifier paoIdentifier = paoCreationService.createPao(paoTemplate);
        
        //Update the Stars table with the device id
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), 
                                                                                 paoIdentifier.getPaoId());
    }

    @Override
    public void updateDevice(HardwareDto hardwareDto) {
        ZigbeeThermostat thermostat = new ZigbeeThermostat();
        
        thermostat.setInstallCode(hardwareDto.getInstallCode());
        thermostat.setMacAddress(hardwareDto.getMacAddress());
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
        deviceDao.removeDevice(thermostat);
    }

    @Autowired
    public void setPaoCreationService(PaoCreationService paoCreationService) {
        this.paoCreationService = paoCreationService;
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
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}
