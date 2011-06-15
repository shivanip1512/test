package com.cannontech.stars.dr.thirdparty.digi.service.builder;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
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
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.dao.provider.fields.UtilityProZigbeeFields;
import com.cannontech.thirdparty.model.ZigbeeThermostat;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.cannontech.util.Validator;
import com.google.common.collect.ClassToInstanceMap;

public class ZigbeeUtilityProBuilder implements HardwareTypeExtensionProvider {

    private PaoCreationService paoCreationService;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private DeviceDao deviceDao;
    private AttributeService attributeService;
    private ZigbeeWebService zigbeeWebService;
    private GatewayDeviceDao gatewayDeviceDao;
    
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
        
        LitePoint connectStatus = attributeService.getPointForAttribute(zigbeeThermostat, BuiltInAttribute.CONNECTION_STATUS);
        hardwareDto.setConnectStatusId(connectStatus.getLiteID());
        
        hardwareDto.setGatewayId(zigbeeThermostat.getGatewayId());
    }
    
    @Override
    public void validateDevice(HardwareDto hardwareDto, Errors errors) {
        /* Install Code */
        String installCode = hardwareDto.getInstallCode();
        if (StringUtils.isBlank(installCode)) {
            errors.rejectValue("installCode", "yukon.web.modules.operator.hardware.error.required");
        } else if (!Validator.isInstallCode(installCode)){
            errors.rejectValue("installCode", "yukon.web.modules.operator.hardware.error.format");
        }
        
        /* MAC Address*/
        String macAddress = hardwareDto.getMacAddress();
        if (StringUtils.isBlank(macAddress)) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.required.eui64");
        } else if (!Validator.isInstallCode(macAddress)) {
            /*This is NOT an error..*/
            //Using isInstallCode to Validate the EUI-64 version of the MAC Address since ZigBee uses it instead of the standard MAC-48
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.format.eui64");
        }
    }
    
    @Override
    public void createDevice(HardwareDto hardwareDto) {
        //Build up all the fields for inserting a Zigbee Util Pro.        
        //Serial Number is unique, using that as the PaoName
        YukonPaObjectFields yukonPaObjectFields = new YukonPaObjectFields(hardwareDto.getSerialNumber());
        
        UtilityProZigbeeFields tStatFields = new UtilityProZigbeeFields(hardwareDto.getInstallCode(),
                                                                        hardwareDto.getMacAddress(),
                                                                        1/*Constant place holder until Firmware change*/);

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
        thermostat.setGatewayId(hardwareDto.getGatewayId());
        
        zigbeeDeviceDao.updateZigbeeUtilPro(thermostat);
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), hardwareDto.getDeviceId());
        
        return;
    }

    @Override
    @Transactional
    public void preDeleteCleanup(PaoIdentifier pao, InventoryIdentifier inventoryId) {
        decommissionDevice(inventoryId.getInventoryId(),pao.getPaoId());        
    }
    
    @Override
    @Transactional
    public void deleteDevice(PaoIdentifier pao, InventoryIdentifier id) {
        zigbeeDeviceDao.deleteZigbeeUtilPro(pao.getPaoId());
        deviceDao.removeDevice(new SimpleDevice(pao));
    }

    @Override
    public void moveDeviceToInventory(PaoIdentifier pao, InventoryIdentifier inventoryId) {
        decommissionDevice(inventoryId.getInventoryId(),pao.getPaoId());
    };
    
    private void decommissionDevice(int inventoryId, int deviceId) {
        Integer gatewayId = zigbeeDeviceDao.findGatewayIdForInventory(inventoryId);        
        if (gatewayId != null) {
            //Send Decommission command.
            zigbeeWebService.uninstallStat(gatewayId, deviceId);
            
            //Remove from gateway
            gatewayDeviceDao.unassignDeviceFromGateway(deviceId);
        }
    }
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
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
    
    @Autowired
    public void setZigbeeWebService(ZigbeeWebService zigbeeWebService) {
        this.zigbeeWebService = zigbeeWebService;
    }
}
