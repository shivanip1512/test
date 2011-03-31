package com.cannontech.web.stars.dr.operator.hardware;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.Pair;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.thirdparty.digi.model.GatewayDto;
import com.cannontech.stars.dr.thirdparty.digi.model.ZigbeeDeviceDto;
import com.cannontech.stars.dr.thirdparty.digi.service.ZigbeeWebService;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.model.DigiGateway;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.service.ZigbeeDeviceService;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;

@Controller
@RequestMapping("/operator/hardware/gateway/*")
public class ZigbeeGatewayController {

    private ZigbeeWebService zigbeeWebService;
    private GatewayDeviceDao gatewayDeviceDao;
    private HardwareUiService hardwareUiService;
    private ZigbeeDeviceService zigbeeDeviceService;
    private LMHardwareBaseDao lmHardwareBaseDao;
    
    /* Gateway Configuration Page */
    @RequestMapping
    public String view(YukonUserContext context, ModelMap model, AccountInfoFragment fragment, int inventoryId) {
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
        model.addAttribute("mode", PageEditMode.VIEW);
        
    	int energyCompanyId = fragment.getEnergyCompanyId();
        int accountId = fragment.getAccountId();
        HardwareDto hardwareDto = hardwareUiService.getHardwareDto(inventoryId, energyCompanyId, accountId);
        model.addAttribute("gatewayId", hardwareDto.getDeviceId());
        
    	DigiGateway digiGateway = gatewayDeviceDao.getDigiGateway(hardwareDto.getDeviceId());
        GatewayDto gatewayDto = zigbeeDeviceService.createGatewayDto(digiGateway, hardwareDto);
        
        model.addAttribute("displayName", gatewayDto.getSerialNumber());
        model.addAttribute("inventoryId", inventoryId);
        model.addAttribute("gatewayDto", gatewayDto);
        
        List<Pair<InventoryIdentifier, ZigbeeDeviceDto>> zigbeeDevices = zigbeeDeviceService.buildZigbeeDeviceDtoList(fragment.getAccountId());
        model.addAttribute("zigbeeDevices", zigbeeDevices);
        
        return "operator/hardware/gateway.jsp";
    }
    
    @RequestMapping(value="gatewayAction", params="commission", method=RequestMethod.POST)
    public String commission(ModelMap model, FlashScope flash, int gatewayId, int accountId, int inventoryId) {
        zigbeeWebService.installGateway(gatewayId);
        
        String gatewaySerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(gatewayId);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.gateway.gatewayCommissioned", gatewaySerialNumber));
        
        return redirectView(model, accountId, inventoryId);
    }
    
    @RequestMapping(value="gatewayAction", params="decommission", method=RequestMethod.POST)
    public String decommission(ModelMap model, FlashScope flash, int gatewayId, int accountId, int inventoryId) {
        zigbeeWebService.removeGateway(gatewayId);
        
        String gatewaySerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(gatewayId);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.gateway.gatewayDecommissioned", gatewaySerialNumber));
        
        return redirectView(model, accountId, inventoryId);
    }
    
    @RequestMapping(value="gatewayAction", params="sendTextMsg", method=RequestMethod.POST)
    public String sendTextMessage(ModelMap model, FlashScope flash, int gatewayId, int accountId, int inventoryId, String message) {
        zigbeeWebService.sendTextMessage(gatewayId, message);
        
        String gatewaySerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(gatewayId);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.gateway.messageSent", gatewaySerialNumber));
        
        return redirectView(model, accountId, inventoryId);
    }
    
    @RequestMapping(value="deviceAction", params="add", method=RequestMethod.POST)
    public String add(ModelMap model, FlashScope flash, int deviceId, int gatewayId, int accountId, int inventoryId) {
    	gatewayDeviceDao.assignDeviceToGateway(deviceId, gatewayId);

    	String deviceSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(deviceId);
    	String gatewaySerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(gatewayId);
    	flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.gateway.thermostatAdded", deviceSerialNumber, gatewaySerialNumber));
    	
    	return redirectView(model, accountId, inventoryId);
    }
    
    @RequestMapping(value="deviceAction", params="remove", method=RequestMethod.POST)
    public String remove(ModelMap model, FlashScope flash, int deviceId, int gatewayId, int accountId, int inventoryId) {
    	//Send Digi Unlink commands?
    	gatewayDeviceDao.unassignDeviceFromGateway(deviceId);
    	
    	String deviceSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(deviceId);
        String gatewaySerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(gatewayId);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.gateway.thermostatRemoved", deviceSerialNumber, gatewaySerialNumber));
    	
    	return redirectView(model, accountId, inventoryId);
    }
    
    @RequestMapping(value="deviceAction", params="uninstall", method=RequestMethod.POST)
    public String uninstall(ModelMap model, FlashScope flash, int deviceId, int gatewayId, int accountId, int inventoryId) {
        zigbeeWebService.uninstallStat(deviceId, gatewayId);
        
        String deviceSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(deviceId);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.gateway.thermostatUninstalled", deviceSerialNumber));
        
        return redirectView(model, accountId, inventoryId);
    }
    
    @RequestMapping(value="deviceAction", params="install", method=RequestMethod.POST)
    public String install(ModelMap model, FlashScope flash, int deviceId, int gatewayId, int accountId, int inventoryId) {
        zigbeeWebService.installStat(deviceId, gatewayId);
        
        String deviceSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(deviceId);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.gateway.thermostatInstalled", deviceSerialNumber));
        
        return redirectView(model, accountId, inventoryId);
    }

    private String redirectView(ModelMap model, int accountId, int inventoryId) {
        model.addAttribute("accountId", accountId);
        model.addAttribute("inventoryId", inventoryId);
        return "redirect:view";
    }
    
    @Autowired
    public void setHardwareUiService(HardwareUiService hardwareUiService) {
    	this.hardwareUiService = hardwareUiService;
    }
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
    
    @Autowired
    public void setZigbeeWebservice(ZigbeeWebService zigbeeWebservice) {
    	this.zigbeeWebService = zigbeeWebservice;
    }
    
    @Autowired
    public void setZigbeeDeviceService(ZigbeeDeviceService zigbeeDeviceService) {
        this.zigbeeDeviceService = zigbeeDeviceService;
    }
    
    @Autowired
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }
    
}