package com.cannontech.stars.dr.digi.builder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.model.CompleteZbEndpoint;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.model.ZigbeeEndpoint;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.cannontech.util.Validator;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class ZigbeeEndpointBuilder implements HardwareTypeExtensionProvider {

    private final Logger log = YukonLogManager.getLogger(ZigbeeEndpointBuilder.class);
    
    private @Autowired PaoPersistenceService paoPersistenceService;
    private @Autowired ZigbeeDeviceDao zigbeeDeviceDao;
    private @Autowired InventoryBaseDao inventoryBaseDao;
    private @Autowired AttributeService attributeService;
    private @Autowired ZigbeeWebService zigbeeWebService;
    private @Autowired GatewayDeviceDao gatewayDeviceDao;
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
    public void retrieveDevice(Hardware hardware) {
        ZigbeeEndpoint zigbeeEndPoint = zigbeeDeviceDao.getZigbeeEndPoint(hardware.getDeviceId()); 
        
        hardware.setInstallCode(zigbeeEndPoint.getInstallCode());
        hardware.setMacAddress(zigbeeEndPoint.getMacAddress());
        hardware.setDestinationEndPointId(zigbeeEndPoint.getDestinationEndPointId());
        hardware.setNodeId(zigbeeEndPoint.getNodeId());
        
        LitePoint linkPt = attributeService.getPointForAttribute(zigbeeEndPoint, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        hardware.setCommissionedId(linkPt.getLiteID());
        
        hardware.setGatewayId(zigbeeEndPoint.getGatewayId());
    }
    
    @Override
    public void validateDevice(Hardware hardware, Errors errors) {
        /* Install Code */
        String installCode = hardware.getInstallCode();
        if (StringUtils.isBlank(installCode)) {
            errors.rejectValue("installCode", "yukon.web.modules.operator.hardware.error.required");
        } else if (!Validator.isInstallCode(installCode)){
            errors.rejectValue("installCode", "yukon.web.modules.operator.hardware.error.format");
        }
        
        /* MAC Address*/
        String macAddress = hardware.getMacAddress();
        if (StringUtils.isBlank(macAddress)) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.required");
        } else if (!Validator.isInstallCode(macAddress)) {
            /*This is NOT an error...*/
            //Using isInstallCode to Validate the EUI-64 version of the MAC Address since ZigBee uses it instead of the standard MAC-48
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.format.eui64");
        }
    }
    
    @Override
    public void createDevice(Hardware hardware) {
        CompleteZbEndpoint zbEndpoint = new CompleteZbEndpoint();
        zbEndpoint.setPaoName(hardware.getSerialNumber());
        zbEndpoint.setInstallCode(hardware.getInstallCode());
        zbEndpoint.setMacAddress(hardware.getMacAddress());

        paoPersistenceService.createPaoWithDefaultPoints(zbEndpoint, PaoType.ZIGBEE_ENDPOINT);
        
        //Update the Stars table with the device id
        inventoryBaseDao.updateInventoryBaseDeviceId(hardware.getInventoryId(), 
                                                          zbEndpoint.getPaObjectId());
    }

    @Override
    public void updateDevice(Hardware hardware) {
        PaoIdentifier zbIdentifier = new PaoIdentifier(hardware.getDeviceId(), PaoType.ZIGBEE_ENDPOINT);
        CompleteZbEndpoint zbEndpoint = paoPersistenceService.retreivePao(zbIdentifier, CompleteZbEndpoint.class);
        
        zbEndpoint.setEndPointId(hardware.getDestinationEndPointId());
        zbEndpoint.setNodeId(hardware.getNodeId());
        if (hardware.getInstallCode() != null) {
            zbEndpoint.setInstallCode(hardware.getInstallCode());
        }
        if (hardware.getMacAddress() != null) {
            zbEndpoint.setMacAddress(hardware.getMacAddress());
        }
        
        paoPersistenceService.updatePao(zbEndpoint);
        
        gatewayDeviceDao.updateDeviceToGatewayAssignment(hardware.getDeviceId(), hardware.getGatewayId());
        inventoryBaseDao.updateInventoryBaseDeviceId(hardware.getInventoryId(), hardware.getDeviceId());
        
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
        paoPersistenceService.deletePao(pao.getPaoIdentifier());
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
            try {
                zigbeeWebService.uninstallEndPoint(gatewayId, device.getPaoIdentifier().getPaoId());
            } catch (DigiWebServiceException e) {
                //Log error and move on. This shouldn't stop us from finishing the process
                log.error(e.getMessage());
            }
            
            //Remove from gateway
            gatewayDeviceDao.unassignDeviceFromGateway(device.getPaoIdentifier().getPaoId());
        }
    }
}