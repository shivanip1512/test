package com.cannontech.web.stars.dr.operator.hardware;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.model.DigiGateway;
import com.cannontech.common.model.ZigbeeThermostat;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.AttributeServiceImpl;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.pao.service.PointServiceImpl;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.GatewayDeviceDao;
import com.cannontech.stars.dr.hardware.model.GatewayDto;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.hardware.model.ZigbeeDeviceAssignment;
import com.cannontech.stars.dr.hardware.model.ZigbeeDeviceDto;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.dr.hardware.service.ZigbeeWebService;
import com.cannontech.stars.dr.hardware.service.impl.ZigbeeDeviceService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.util.JsonView;
import com.google.common.collect.Lists;

@RequestMapping("/operator/hardware/gateway/*")
@Controller
public class ZigbeeGatewayController {

    private ZigbeeDeviceService zigbeeDeviceService;
    private ZigbeeWebService zigbeeWebService;
    private GatewayDeviceDao gatewayDeviceDao;
    private HardwareUiService hardwareUiService;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private YukonListDao yukonListDao;
    private AttributeService attributeService;
    
    /* Gateway Configuration Page */
    @RequestMapping
    public String configuration(YukonUserContext userContext, ModelMap modelMap, 
                                AccountInfoFragment accountInfoFragment, int inventoryId) {
    	HardwareDto hardwareDto = hardwareUiService.getHardwareDto( inventoryId, 
				   													accountInfoFragment.getEnergyCompanyId(), 
				   													accountInfoFragment.getAccountId());
    	DigiGateway digiGateway = gatewayDeviceDao.getDigiGateway(hardwareDto.getDeviceId());
        
        GatewayDto gatewayDto = createGatewayDto(digiGateway,hardwareDto);
        
        return loadConfigurationPage(userContext,modelMap,accountInfoFragment,inventoryId,gatewayDto,digiGateway);
    }
    
    private String loadConfigurationPage(YukonUserContext userContext, ModelMap modelMap, 
            							 AccountInfoFragment accountInfoFragment, int inventoryId,
            							 GatewayDto gatewayDto, DigiGateway digiGateway) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        modelMap.addAttribute("inventoryId", inventoryId);
        modelMap.addAttribute("gatewayDto", gatewayDto);
        
        //Get list of assigned devices
        List<ZigbeeDeviceAssignment> assignedDeviceIds = zigbeeDeviceService.getAssignedDevices(digiGateway.getPaoIdentifier().getPaoId());
        List<ZigbeeDeviceDto> assignedDevices = buildZigbeeDeviceDtoList(assignedDeviceIds);
        
        modelMap.addAttribute("assignedDevices", assignedDevices);
        
    	return "operator/hardware/gateway.jsp";
    }
    
    /**
     * Creates a ZigbeeDeviceDto for each device assignment and returns a List
     * 
     * @param assignments
     * @return
     */
    private List<ZigbeeDeviceDto> buildZigbeeDeviceDtoList(List<ZigbeeDeviceAssignment> assignments) {
    	List<ZigbeeDeviceDto> deviceList = Lists.newArrayList();
    	
    	for (ZigbeeDeviceAssignment assignment : assignments) {
    		deviceList.add(buildZigbeeDeviceDto(assignment.getDeviceId()));
    	}
    	
    	return deviceList;
    }
    
    /**
     * Creates a single ZigbeeDeviceDto from the deviceId (paoId)
     * 
     * @param deviceId
     * @return
     */
    private ZigbeeDeviceDto buildZigbeeDeviceDto(int deviceId) {
    	ZigbeeDeviceDto device = new ZigbeeDeviceDto();
		
    	LiteStarsLMHardware lmHardware = (LiteStarsLMHardware)starsInventoryBaseDao.getByDeviceId(deviceId);
        YukonListEntry deviceTypeEntry = yukonListDao.getYukonListEntry(lmHardware.getLmHardwareTypeID());
    	
        device.setDeviceId(deviceId);
		device.setDeviceType(deviceTypeEntry.getEntryText());
		device.setSerialNumber(lmHardware.getManufacturerSerialNumber());
        
		ZigbeeThermostat tStat = new ZigbeeThermostat();
		tStat.setPaoIdentifier(new PaoIdentifier(deviceId, PaoType.ZIGBEEUTILPRO));
		
		LitePoint connPt = attributeService.getPointForAttribute(tStat, BuiltInAttribute.CONNECTION_STATUS);
        LitePoint linkPt = attributeService.getPointForAttribute(tStat, BuiltInAttribute.ZIGBEE_LINK_STATUS);
		
        device.setConnectionStatusId(connPt.getLiteID());
		device.setCommissionId(linkPt.getLiteID());
        
        return device;
    }
    
    @RequestMapping
    public String updateGateway(@ModelAttribute("gatewayDto") GatewayDto gatewayDto, 
                                YukonUserContext userContext, ModelMap modelMap, FlashScope flashScope,
                                AccountInfoFragment accountInfoFragment, int inventoryId) {
    	//do update
    	HardwareDto hardwareDto = hardwareUiService.getHardwareDto(inventoryId, accountInfoFragment.getEnergyCompanyId(), accountInfoFragment.getAccountId());
    	
    	DigiGateway digiGateway = new DigiGateway();
    	
    	digiGateway.setPaoIdentifier(new PaoIdentifier(hardwareDto.getDeviceId(), PaoType.DIGIGATEWAY));
    	
    	digiGateway.setDigiId(gatewayDto.getDigiId());
    	digiGateway.setFirmwareVersion(gatewayDto.getFirmwareVersion());
    	digiGateway.setMacAddress(gatewayDto.getMacAddress());
    	
    	try{
    		gatewayDeviceDao.updateDigiGateway(digiGateway);
    		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneWizard.success.saved"));
    		//TODO Change this to a more accurate error..!!>!..!!
    	} catch ( Exception e ) {
    		flashScope.setError(new YukonMessageSourceResolvable("yukon.web.error.fieldErrorExists"));
    	}
    	//
    	
        return loadConfigurationPage(userContext,modelMap,accountInfoFragment,inventoryId,gatewayDto,digiGateway);
    }
    
    private String redirectToConfigurationPage( YukonUserContext userContext, ModelMap modelMap, 
    											AccountInfoFragment accountInfoFragment, int inventoryId) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        modelMap.addAttribute("inventoryId", inventoryId);
		
		return "redirect:/spring/stars/operator/hardware/gateway/configuration";
	}
    
    @RequestMapping
    public String commission(YukonUserContext userContext, ModelMap modelMap, 
                            AccountInfoFragment accountInfoFragment, int inventoryId) {        
        //Get deviceId from a dao/service or a gateway
        int deviceId = starsInventoryBaseDao.getByInventoryId(inventoryId).getDeviceID();
        
        zigbeeWebService.installGateway(deviceId);
        
        return redirectToConfigurationPage(userContext,modelMap,accountInfoFragment,inventoryId);
    }
    
    @RequestMapping
    public String decommission(YukonUserContext userContext, ModelMap modelMap, 
                            AccountInfoFragment accountInfoFragment, int inventoryId) {       
        //Get deviceId from a dao/service or a gateway
        int deviceId = starsInventoryBaseDao.getByInventoryId(inventoryId).getDeviceID();
        
        zigbeeWebService.removeGateway(deviceId);
        
        return redirectToConfigurationPage(userContext,modelMap,accountInfoFragment,inventoryId);
    }
    
    @RequestMapping
    public String installStat(YukonUserContext userContext, ModelMap modelMap, 
                            AccountInfoFragment accountInfoFragment, int deviceId, int gatewayInvId) {
        zigbeeWebService.installStat(deviceId);
        
        return redirectToConfigurationPage(userContext,modelMap,accountInfoFragment,gatewayInvId);
    }
    
    @RequestMapping
    public String uninstallStat(YukonUserContext userContext, ModelMap modelMap, 
                            AccountInfoFragment accountInfoFragment, int deviceId, int gatewayInvId) {
    	return redirectToConfigurationPage(userContext,modelMap,accountInfoFragment,gatewayInvId);
    }
    
    @RequestMapping
    public String sendTextMessage(YukonUserContext userContext, ModelMap modelMap, 
                            AccountInfoFragment accountInfoFragment, int deviceId, String message, int gatewayInventoryId) {
        zigbeeWebService.sendTextMessage(deviceId,message);
        
        LiteInventoryBase inventory = starsInventoryBaseDao.getByDeviceId(deviceId);
        
        return redirectToConfigurationPage(userContext,modelMap,accountInfoFragment,gatewayInventoryId);
    }
    
    @RequestMapping
    public String reportAllDevices(YukonUserContext userContext, ModelMap modelMap, 
                            	   AccountInfoFragment accountInfoFragment, int inventoryId) {
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        modelMap.addAttribute("inventoryId", inventoryId);
        
        String result = zigbeeWebService.getAllDevices();
        
        return redirectToConfigurationPage(userContext,modelMap,accountInfoFragment,inventoryId);
    }
    
    @RequestMapping
    public String assignDevice(YukonUserContext userContext, AccountInfoFragment accountInfoFragment, 
    		                   ModelMap modelMap, int gatewayId, int deviceId) {
    	
    	LiteInventoryBase gateway = starsInventoryBaseDao.getByInventoryId(gatewayId);
    	LiteInventoryBase device = starsInventoryBaseDao.getByInventoryId(deviceId);
    	
    	zigbeeDeviceService.assignDeviceToGateway(device.getDeviceID(), gateway.getDeviceID());
    	
    	modelMap.addAttribute("accountId", accountInfoFragment.getAccountId());
    	modelMap.addAttribute("inventoryId", gatewayId);
    	
    	ZigbeeDeviceDto zigbeeDto = buildZigbeeDeviceDto(device.getDeviceID());
    	modelMap.addAttribute("zigbeeDto", zigbeeDto);
    	
    	return "operator/hardware/addDeviceTableRow.jsp";
    }
    
    @RequestMapping
    public JsonView unassignDevice(YukonUserContext userContext, ModelMap modelMap, 
    							AccountInfoFragment accountInfoFragment, int deviceId) {
    	//Send Digi Unlink commands?
    	
    	zigbeeDeviceService.unassignDeviceFromGateway(deviceId);
    	
    	return new JsonView();
    }
    
    private GatewayDto createGatewayDto(DigiGateway digiGateway, HardwareDto hardwareDto) {
    	GatewayDto gatewayDto = new GatewayDto();
        
        gatewayDto.setSerialNumber(hardwareDto.getSerialNumber());
        gatewayDto.setGatewayType(hardwareDto.getDisplayType());
        
        gatewayDto.setMacAddress(digiGateway.getMacAddress());
        gatewayDto.setDigiId(digiGateway.getDigiId());
        gatewayDto.setFirmwareVersion(digiGateway.getFirmwareVersion());
        
        LitePoint connPt = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.CONNECTION_STATUS);
        LitePoint linkPt = attributeService.getPointForAttribute(digiGateway, BuiltInAttribute.ZIGBEE_LINK_STATUS);
        
        gatewayDto.setConnectionStatusId(connPt.getLiteID());
        gatewayDto.setGatewayStatusId(linkPt.getLiteID());
        
        return gatewayDto;
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
    public void setZigbeeDeviceService(ZigbeeDeviceService zigbeeDeviceService) {
        this.zigbeeDeviceService = zigbeeDeviceService;
    }
    
    @Autowired
    public void setZigbeeWebservice(ZigbeeWebService zigbeeWebservice) {
    	this.zigbeeWebService = zigbeeWebservice;
    }
    
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
    	this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao){
        this.yukonListDao = yukonListDao;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}
