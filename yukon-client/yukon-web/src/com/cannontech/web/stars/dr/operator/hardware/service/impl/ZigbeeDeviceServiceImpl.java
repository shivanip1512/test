package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.thirdparty.digi.model.GatewayDto;
import com.cannontech.stars.dr.thirdparty.digi.model.ZigbeeDeviceDto;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.thirdparty.model.ZigbeeDeviceAssignment;
import com.cannontech.thirdparty.model.ZigbeeThermostat;
import com.cannontech.web.stars.dr.operator.hardware.service.ZigbeeDeviceService;
import com.google.common.collect.Lists;

public class ZigbeeDeviceServiceImpl implements ZigbeeDeviceService {
    
    private YukonListDao yukonListDao;
    private AttributeService attributeService;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private GatewayDeviceDao gatewayDeviceDao;
    private YukonEnergyCompanyService yukonEnergyCompanyService;

    @Override
    public GatewayDto createGatewayDto(DigiGateway digiGateway, HardwareDto hardwareDto) {
        GatewayDto gatewayDto = new GatewayDto();
        
        gatewayDto.setSerialNumber(hardwareDto.getSerialNumber());
        gatewayDto.setGatewayType(hardwareDto.getDisplayType());
        
        gatewayDto.setMacAddress(digiGateway.getMacAddress());
        gatewayDto.setDigiId(digiGateway.getDigiId());
        gatewayDto.setFirmwareVersion(digiGateway.getFirmwareVersion());
        
        LitePoint connPt = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.ZIGBEE_CONNECTION_STATUS);
        LitePoint linkPt = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        
        gatewayDto.setConnectionStatusId(connPt.getLiteID());
        gatewayDto.setGatewayStatusId(linkPt.getLiteID());
        
        return gatewayDto;
    }
    
    @Override
    public List<Pair<InventoryIdentifier, ZigbeeDeviceDto>> buildZigbeeDeviceDtoList(int accountId) {
        YukonEnergyCompany yukonEc = yukonEnergyCompanyService.getEnergyCompanyByAccountId(accountId);
        int definitionId = HardwareType.UTILITY_PRO_ZIGBEE.getDefinitionId();
        YukonListEntry zigbeeDeviceTypeListEntry = yukonListDao.getYukonListEntry(definitionId, yukonEc);
        
        int typeId = zigbeeDeviceTypeListEntry.getEntryID();
        List<ZigbeeDeviceAssignment> assignments = gatewayDeviceDao.getZigbeeDevicesForAccount(accountId, Lists.newArrayList(typeId));
        
        List<Pair<InventoryIdentifier, ZigbeeDeviceDto>> deviceList = Lists.newArrayList();
        
        for (ZigbeeDeviceAssignment assignment : assignments) {
            ZigbeeDeviceDto dto = buildZigbeeDeviceDto(assignment.getDeviceId());
            InventoryIdentifier gateway = gatewayDeviceDao.findGatewayByDeviceMapping(assignment.getDeviceId());
            Pair<InventoryIdentifier, ZigbeeDeviceDto> pair = new Pair<InventoryIdentifier, ZigbeeDeviceDto>(gateway, dto);
            deviceList.add(pair);
        }
        
        return deviceList;
    }
    
    @Override
    public ZigbeeDeviceDto buildZigbeeDeviceDto(int deviceId) {
        ZigbeeDeviceDto device = new ZigbeeDeviceDto();
        
        LiteStarsLMHardware lmHardware = (LiteStarsLMHardware)starsInventoryBaseDao.getByDeviceId(deviceId);
        YukonListEntry deviceTypeEntry = yukonListDao.getYukonListEntry(lmHardware.getLmHardwareTypeID());
        
        HardwareType type = HardwareType.valueOf(deviceTypeEntry.getYukonDefID());
        device.setInventoryIdentifier(new InventoryIdentifier(lmHardware.getInventoryID(), type));
        
        device.setDeviceId(deviceId);
        device.setDeviceType(deviceTypeEntry.getEntryText());
        device.setSerialNumber(lmHardware.getManufacturerSerialNumber());
        
        ZigbeeThermostat tStat = new ZigbeeThermostat();
        tStat.setPaoIdentifier(new PaoIdentifier(deviceId, PaoType.ZIGBEEUTILPRO));
        
        LitePoint connPt = attributeService.getPointForAttribute(tStat, BuiltInAttribute.ZIGBEE_CONNECTION_STATUS);
        LitePoint linkPt = attributeService.getPointForAttribute(tStat, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        
        device.setConnectionStatusId(connPt.getLiteID());
        device.setCommissionId(linkPt.getLiteID());
        device.setGatewayId(gatewayDeviceDao.findGatewayIdForDeviceId(deviceId));
        
        return device;
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao){
        this.yukonListDao = yukonListDao;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
    
    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }
    
}