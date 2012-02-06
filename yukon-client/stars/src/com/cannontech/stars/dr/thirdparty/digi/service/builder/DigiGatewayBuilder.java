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
import com.cannontech.common.pao.model.CompleteDigiGateway;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.hardware.builder.impl.HardwareTypeExtensionProvider;
import com.cannontech.stars.dr.hardware.model.Hardware;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.cannontech.util.Validator;
import com.google.common.collect.ImmutableSet;

public class DigiGatewayBuilder implements HardwareTypeExtensionProvider {

    private Logger log = YukonLogManager.getLogger(DigiGatewayBuilder.class);
    
    private @Autowired PaoPersistenceService paoPersistenceService;
    private @Autowired GatewayDeviceDao gatewayDeviceDao;
    private @Autowired StarsInventoryBaseDao starsInventoryBaseDao;
    private @Autowired AttributeService attributeService;
    private @Autowired ZigbeeWebService zigbeeWebService;
    
    @Override
    public void retrieveDevice(Hardware hardware) {
        DigiGateway digiGateway = gatewayDeviceDao.getDigiGateway(hardware.getDeviceId());
        
        hardware.setMacAddress(digiGateway.getMacAddress());
        hardware.setFirmwareVersion(digiGateway.getFirmwareVersion());
        
        LitePoint linkPt = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        hardware.setCommissionedId(linkPt.getLiteID());
    }
    
    @Override
    public void validateDevice(Hardware hardware, Errors errors) {

        /* MAC Address*/
        String macAddress = hardware.getMacAddress();
        if (StringUtils.isBlank(macAddress)) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.required");
        } else if (!Validator.isMacAddress(macAddress)) {
            errors.rejectValue("macAddress", "yukon.web.modules.operator.hardware.error.format.eui48");
        }
    }
    
    @Override
    public void createDevice(Hardware hardware) {
        CompleteDigiGateway digiGateway = new CompleteDigiGateway();
        digiGateway.setPaoName(hardware.getSerialNumber());
        digiGateway.setDigiId(hardware.getCommissionedId());
        digiGateway.setFirmwareVersion(hardware.getFirmwareVersion());
        digiGateway.setMacAddress(hardware.getMacAddress());
        
        paoPersistenceService.createPao(digiGateway, PaoType.DIGIGATEWAY);
        
        //Update the Stars table with the device id
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardware.getInventoryId(), 
                                                          digiGateway.getPaoIdentifier().getPaoId());
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
        paoPersistenceService.deletePao(pao.getPaoIdentifier());
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
    public void updateDevice(Hardware hardware) {
        PaoIdentifier digiIdentifier = new PaoIdentifier(hardware.getDeviceId(), PaoType.DIGIGATEWAY);
        CompleteDigiGateway digiGateway = paoPersistenceService.retreivePao(digiIdentifier, CompleteDigiGateway.class);
        
        digiGateway.setDigiId(hardware.getCommissionedId());
        if (hardware.getFirmwareVersion() != null) {
            digiGateway.setFirmwareVersion(hardware.getFirmwareVersion());
        }
        if (hardware.getMacAddress() != null) {
            digiGateway.setMacAddress(hardware.getMacAddress());
        }
        
        paoPersistenceService.updatePao(digiGateway);
        
        starsInventoryBaseDao.updateInventoryBaseDeviceId(hardware.getInventoryId(), hardware.getDeviceId());
    }
}
