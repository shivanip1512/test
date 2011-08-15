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
import com.cannontech.common.pao.YukonPao;
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
import com.cannontech.thirdparty.digi.dao.provider.fields.ZigbeeEndpointFields;
import com.cannontech.thirdparty.model.ZigbeeEndpoint;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.cannontech.util.Validator;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class ZigbeeEndpointBuilder implements HardwareTypeExtensionProvider {

    private PaoCreationService paoCreationService;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private DeviceDao deviceDao;
    private AttributeService attributeService;
    private ZigbeeWebService zigbeeWebService;
    private GatewayDeviceDao gatewayDeviceDao;
    private static ImmutableSet<HardwareType> supportedTypes;
    
    static {
        Builder<HardwareType> builder = ImmutableSet.builder();
        builder.add(HardwareType.UTILITY_PRO_ZIGBEE);
        builder.add(HardwareType.LCR_6200_ZIGBEE);
        builder.add(HardwareType.LCR_6600_ZIGBEE);
        supportedTypes = builder.build();
    }
    
    @Override
    public ImmutableSet<HardwareType> getTypes() {
        return supportedTypes;
    }
    
    @Override
    public void retrieveDevice(HardwareDto hardwareDto) {
        ZigbeeEndpoint zigbeeEndPoint = zigbeeDeviceDao.getZigbeeEndPoint(hardwareDto.getDeviceId()); 
        
        hardwareDto.setInstallCode(zigbeeEndPoint.getInstallCode());
        hardwareDto.setMacAddress(zigbeeEndPoint.getMacAddress());
        hardwareDto.setDestinationEndPointId(zigbeeEndPoint.getDestinationEndPointId());
        hardwareDto.setNodeId(zigbeeEndPoint.getNodeId());
        
        LitePoint linkPt = attributeService.getPointForAttribute(zigbeeEndPoint, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        hardwareDto.setCommissionedId(linkPt.getLiteID());
        
        hardwareDto.setGatewayId(zigbeeEndPoint.getGatewayId());
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
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.required");
        } else if (!Validator.isInstallCode(macAddress)) {
            /*This is NOT an error...*/
            //Using isInstallCode to Validate the EUI-64 version of the MAC Address since ZigBee uses it instead of the standard MAC-48
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.format.eui64");
        }
    }
    
    @Override
    public void createDevice(HardwareDto hardwareDto) {
        //Build up all the fields for inserting a Zigbee End Point.
        //Serial Number is unique, using that as the PaoName
        YukonPaObjectFields yukonPaObjectFields = new YukonPaObjectFields(hardwareDto.getSerialNumber());
        
        ZigbeeEndpointFields tStatFields = new ZigbeeEndpointFields(hardwareDto.getInstallCode(),
                                                                        hardwareDto.getMacAddress(),
                                                                        1,/*Constant place holder until Firmware change*/
                                                                        0/*Constant place holder until Firmware change*/);

        //Build Template and call Pao Creation Service
        ClassToInstanceMap<PaoTemplatePart> paoFields = paoCreationService.createFieldMap();
        paoFields.put(ZigbeeEndpointFields.class, tStatFields);
        paoFields.put(YukonPaObjectFields.class, yukonPaObjectFields);
        
        PaoTemplate paoTemplate = new PaoTemplate(PaoType.ZIGBEE_ENDPOINT, paoFields);
        
        PaoIdentifier paoIdentifier = paoCreationService.createPao(paoTemplate);
        
        //Update the Stars table with the device id
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), 
                                                                                 paoIdentifier.getPaoId());
    }

    @Override
    public void updateDevice(HardwareDto hardwareDto) {
        ZigbeeEndpoint endpoint = new ZigbeeEndpoint();
        
        endpoint.setInstallCode(hardwareDto.getInstallCode());
        endpoint.setMacAddress(hardwareDto.getMacAddress());
        endpoint.setPaoIdentifier(new PaoIdentifier(hardwareDto.getDeviceId(), PaoType.ZIGBEE_ENDPOINT));
        endpoint.setName(hardwareDto.getSerialNumber());
        endpoint.setGatewayId(hardwareDto.getGatewayId());
        endpoint.setNodeId(hardwareDto.getNodeId());
        endpoint.setDestinationEndPointId(hardwareDto.getDestinationEndPointId());
        
        zigbeeDeviceDao.updateZigbeeEndPoint(endpoint);
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardwareDto.getInventoryId(), hardwareDto.getDeviceId());
        
        return;
    }

    @Override
    @Transactional
    public void preDeleteCleanup(YukonPao pao, InventoryIdentifier inventoryId) {
        decommissionAndUnassignDevice(inventoryId.getInventoryId(),pao);        
    }
    
    @Override
    @Transactional
    public void deleteDevice(YukonPao pao, InventoryIdentifier id) {
        zigbeeDeviceDao.deleteZigbeeEndPoint(pao.getPaoIdentifier().getPaoId());
        deviceDao.removeDevice(new SimpleDevice(pao));
    }

    @Override
    public void moveDeviceToInventory(YukonPao pao, InventoryIdentifier inventoryId) {
        decommissionAndUnassignDevice(inventoryId.getInventoryId(),pao);
    };
    
    /**
     * Sends the Decommission commands and follows up with an unassigning from the gateway in yukon.
     * 
     * @param inventoryId
     * @param device
     */
    private void decommissionAndUnassignDevice(int inventoryId, YukonPao device) {
        Integer gatewayId = zigbeeDeviceDao.findGatewayIdForInventory(inventoryId);
        if (gatewayId != null) {
            //Send Decommission command.
            zigbeeWebService.uninstallEndPoint(gatewayId, device.getPaoIdentifier().getPaoId());
            
            //Remove from gateway
            gatewayDeviceDao.unassignDeviceFromGateway(device.getPaoIdentifier().getPaoId());
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