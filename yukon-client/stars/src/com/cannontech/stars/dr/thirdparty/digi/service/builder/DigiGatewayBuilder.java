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
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.provider.fields.GatewayFields;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.google.common.collect.ClassToInstanceMap;

public class DigiGatewayBuilder implements HardwareTypeExtensionProvider {

    private GatewayDeviceDao gatewayDeviceDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private AttributeService attributeService;
    private DeviceDao deviceDao;
    private PaoCreationService paoCreationService;
    
    @Override
    public void retrieveDevice(HardwareDto hardwareDto) {
        DigiGateway digiGateway = gatewayDeviceDao.getDigiGateway(hardwareDto.getDeviceId());
        
        hardwareDto.setMacAddress(digiGateway.getMacAddress());
        hardwareDto.setFirmwareVersion(digiGateway.getFirmwareVersion());
        
        LitePoint linkPt = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        hardwareDto.setCommissionedId(linkPt.getLiteID());
        
        LitePoint connectStatus = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.CONNECTION_STATUS);
        hardwareDto.setConnectStatusId(connectStatus.getLiteID());
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
        //Build up all the fields for inserting a digiGateway.        
        YukonPaObjectFields yukonPaObjectFields = new YukonPaObjectFields(hardwareDto.getSerialNumber());
        GatewayFields gatewayFields = buildGatewayFields(hardwareDto);
        
        //Build Template and call Pao Creation Service
        ClassToInstanceMap<PaoTemplatePart> paoFields = paoCreationService.createFieldMap();
        paoFields.put(GatewayFields.class, gatewayFields);
        paoFields.put(YukonPaObjectFields.class, yukonPaObjectFields);
        
        PaoTemplate paoTemplate = new PaoTemplate(PaoType.DIGIGATEWAY, paoFields);
        
        PaoIdentifier paoIdentifier = paoCreationService.createPao(paoTemplate);
        
        //Update the Stars table with the device id
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), 
                                                                                 paoIdentifier.getPaoId());
    }

    @Override
    public void deleteDevice(HardwareDto hardwareDto) {
        DigiGateway digiGateway = buildDigiGateway(hardwareDto);
        
        gatewayDeviceDao.deleteDigiGateway(digiGateway);
        
        deviceDao.removeDevice(digiGateway);
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

    private GatewayFields buildGatewayFields(HardwareDto hardwareDto) {
        GatewayFields fields = new GatewayFields();
        
        fields.setFirmwareVersion(hardwareDto.getFirmwareVersion());
        fields.setMacAddress(hardwareDto.getMacAddress());

        //The DigiId is set later after we commission the device on Digi
        //Default to the value in hardwareDto
        fields.setDigiId(hardwareDto.getCommissionedId());
        
        return fields;
    }    
    private DigiGateway buildDigiGateway(HardwareDto hardwareDto) {
        DigiGateway digiGateway = new DigiGateway();
        
        digiGateway.setPaoIdentifier(new PaoIdentifier(hardwareDto.getDeviceId(), PaoType.DIGIGATEWAY));
        digiGateway.setFirmwareVersion(hardwareDto.getFirmwareVersion());
        digiGateway.setMacAddress(hardwareDto.getMacAddress());
        //Serial Number is unique, using that as the PaoName
        digiGateway.setName(hardwareDto.getSerialNumber());
        digiGateway.setDigiId(hardwareDto.getCommissionedId());
        
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
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Autowired
    public void setPaoCreationService(PaoCreationService paoCreationService) {
        this.paoCreationService = paoCreationService;
    }
}
