package com.cannontech.stars.dr.thirdparty.digi.service.builder;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.service.PaoCreationService;
import com.cannontech.common.pao.service.PaoTemplate;
import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.common.pao.service.providers.fields.DeviceFields;
import com.cannontech.common.pao.service.providers.fields.YukonPaObjectFields;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.provider.fields.DigiGatewayFields;
import com.cannontech.thirdparty.digi.dao.provider.fields.ZigbeeGatewayFields;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.cannontech.util.Validator;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MutableClassToInstanceMap;

public class DigiGatewayBuilder implements HardwareTypeExtensionProvider {

    private Logger log = YukonLogManager.getLogger(DigiGatewayBuilder.class);
    
    private GatewayDeviceDao gatewayDeviceDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private AttributeService attributeService;
    private PaoCreationService paoCreationService;
    private ZigbeeWebService zigbeeWebService;
    
    @Override
    public void retrieveDevice(HardwareDto hardwareDto) {
        DigiGateway digiGateway = gatewayDeviceDao.getDigiGateway(hardwareDto.getDeviceId());
        
        hardwareDto.setMacAddress(digiGateway.getMacAddress());
        hardwareDto.setFirmwareVersion(digiGateway.getFirmwareVersion());
        
        LitePoint linkPt = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        hardwareDto.setCommissionedId(linkPt.getLiteID());
    }
    
    @Override
    public void validateDevice(HardwareDto hardwareDto, Errors errors) {

        /* MAC Address*/
        String macAddress = hardwareDto.getMacAddress();
        if (StringUtils.isBlank(macAddress)) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.required");
        } else if (!Validator.isMacAddress(macAddress)) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.format.eui48");
        }
    }
    
    @Override
    public void createDevice(HardwareDto hardwareDto) {
        //Build up all the fields for inserting a digiGateway.
        YukonPaObjectFields yukonPaObjectFields = new YukonPaObjectFields(hardwareDto.getSerialNumber());
        DigiGatewayFields digiGatewayFields = buildDigiGatewayFields(hardwareDto);
        ZigbeeGatewayFields zigbeeGatewayFields = buildZigbeeGatewayFields(hardwareDto);
        
        //Build Template and call Pao Creation Service
        ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();
        paoFields.put(DigiGatewayFields.class, digiGatewayFields);
        paoFields.put(ZigbeeGatewayFields.class, zigbeeGatewayFields);
        paoFields.put(DeviceFields.class, new DeviceFields());
        paoFields.put(YukonPaObjectFields.class, yukonPaObjectFields);
        
        PaoTemplate paoTemplate = new PaoTemplate(PaoType.DIGIGATEWAY, paoFields);
        
        PaoIdentifier paoIdentifier = paoCreationService.createPao(paoTemplate);
        
        //Update the Stars table with the device id
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), 
                                                                                 paoIdentifier.getPaoId());
    }

    @Override
    @Transactional
    public void preDeleteCleanup(YukonPao pao, InventoryIdentifier inventoryId) {
        //Send Decommission command for devices
        try {
            decommissionGatewayAndUnassignDevices(pao.getPaoIdentifier().getPaoId());
        } catch (DigiWebServiceException e) {
            //Log error and move on. This shouldn't stop us from finishing the process
            log.error(e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void deleteDevice(YukonPao pao, InventoryIdentifier inventoryId) {   
        paoCreationService.deletePao(pao.getPaoIdentifier());
    }

    @Override
    public void moveDeviceToInventory(YukonPao pao, InventoryIdentifier inventoryId) {
        //Send Decommission command for devices
        try {
            decommissionGatewayAndUnassignDevices(pao.getPaoIdentifier().getPaoId());
        } catch (DigiWebServiceException e) {
            //Log error and move on. This shouldn't stop us from finishing the process
            log.error(e.getMessage());
        }
        
        //Remove devices from gateway
        gatewayDeviceDao.removeDevicesFromGateway(pao.getPaoIdentifier().getPaoId());
    };

    private void decommissionGatewayAndUnassignDevices(int gatewayId) {
        List<ZigbeeDevice> devices = gatewayDeviceDao.getAssignedZigbeeDevices(gatewayId);
        for (ZigbeeDevice device : devices) {
            zigbeeWebService.uninstallEndPoint(gatewayId, device.getZigbeeDeviceId());
        }

        //Decommission from iDigi
        zigbeeWebService.removeGateway(gatewayId);
    }
    
    @Override
    public ImmutableSet<HardwareType> getTypes() {
        return ImmutableSet.of(HardwareType.DIGI_GATEWAY);
    }

    @Override
    public void updateDevice(HardwareDto hardwareDto) {
        ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();
        paoFields.put(YukonPaObjectFields.class, new YukonPaObjectFields(hardwareDto.getSerialNumber()));
        paoFields.put(DeviceFields.class, new DeviceFields());
        paoFields.put(DigiGatewayFields.class, buildDigiGatewayFields(hardwareDto));
        paoFields.put(ZigbeeGatewayFields.class, buildZigbeeGatewayFields(hardwareDto));
        
        PaoTemplate template = new PaoTemplate(PaoType.DIGIGATEWAY, paoFields);
        
        paoCreationService.updatePao(hardwareDto.getDeviceId(), template);
        
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), hardwareDto.getDeviceId());
    }

    private DigiGatewayFields buildDigiGatewayFields(HardwareDto hardwareDto) {
        DigiGatewayFields fields = new DigiGatewayFields();

        //The DigiId is set later after we commission the device on Digi
        //Default to the value in hardwareDto
        fields.setDigiId(hardwareDto.getCommissionedId());
        
        return fields;
    }    
    
    private ZigbeeGatewayFields buildZigbeeGatewayFields(HardwareDto hardwareDto) {
    	ZigbeeGatewayFields fields = new ZigbeeGatewayFields();
    	
    	fields.setFirmwareVersion(hardwareDto.getFirmwareVersion());
    	fields.setMacAddress(hardwareDto.getMacAddress());
    	
    	return fields;
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
    public void setPaoCreationService(PaoCreationService paoCreationService) {
        this.paoCreationService = paoCreationService;
    }

    @Autowired
    public void setZigbeeWebService(ZigbeeWebService zigbeeWebService) {
        this.zigbeeWebService = zigbeeWebService;
    }

}
