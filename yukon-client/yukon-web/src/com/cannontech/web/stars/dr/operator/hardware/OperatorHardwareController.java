package com.cannontech.web.stars.dr.operator.hardware;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.stars.event.EventInventory;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareDto;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareService;
import com.cannontech.web.stars.dr.operator.hardware.validator.HardwareDtoValidator;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.util.JsonView;

@Controller
public class OperatorHardwareController {
    public StarsDatabaseCache starsDatabaseCache;
    private HardwareService hardwareService;
    private EnergyCompanyDao energyCompanyDao;
    private PaoDao paoDao;
    private ServiceCompanyDao serviceCompanyDao;
    private AddressDao addressDao;
    private RolePropertyDao rolePropertyDao;
    private HardwareDtoValidator hardwareDtoValidator;
    
    /* HARDWARE LIST */
    @RequestMapping(value = "/operator/hardware/hardwareList")
    public String hardwareList(YukonUserContext userContext, ModelMap modelMap, AccountInfoFragment accountInfoFragment,
                               int accountId) throws ServletRequestBindingException {
        
        setupHardwareInfoModelMap(accountInfoFragment, accountId, null, modelMap, userContext);
        return "operator/hardware/hardwareList.jsp";
    }
    
    @RequestMapping(value = "/operator/hardware/hardwareEdit")
    public String hardwareEdit(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext,  AccountInfoFragment accountInfoFragment, 
                               int energyCompanyId,
                               int accountId,
                               int inventoryId) {
        HardwareDto hardwareDto = hardwareService.getHardwareDto(inventoryId, userContext);
        modelMap.addAttribute("hardwareDto", hardwareDto);
        
        /* Setup HardwareInfo ModelMap */
        setupHardwareInfoModelMap(accountInfoFragment, accountId, inventoryId, modelMap, userContext);
        
        return "/operator/hardware/hardwareEdit.jsp";
    }
    
    @RequestMapping(value = "/operator/hardware/updateHardware")
    public String updateHardware(@ModelAttribute("hardwareDto") HardwareDto hardwareDto, BindingResult bindingResult,
                                 ModelMap modelMap, 
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 AccountInfoFragment accountInfoFragment,
                                 int energyCompanyId,
                                 int accountId,
                                 int inventoryId) {
        
        boolean statusChange = false;
        
        /* Validate and Update*/
        try {
            hardwareDtoValidator.validate(hardwareDto, bindingResult);
            
            if(!bindingResult.hasErrors()) {
                
                hardwareDto.setInventoryId(inventoryId);
                statusChange = hardwareService.updateHardware(hardwareDto);
            }
        } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.serialNumberAlreadyInUse");
        } finally {
            /* Setup HardwareInfo ModelMap */
            setupHardwareInfoModelMap(accountInfoFragment, accountId, inventoryId, modelMap, userContext);
            if (bindingResult.hasErrors()) {
                flashScope.setBindingResult(bindingResult);
                return "/operator/hardware/hardwareEdit.jsp";
            } 
        }
        
        /* If the device status was changed, spawn an event for it */
        if(statusChange){
            EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, hardwareDto.getDeviceStatusEntryId(), inventoryId, request.getSession());
        }
        
        /* Flash hardware updated */
        flashScope.setConfirm(Collections.singletonList(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareEdit.hardwareUpdated")));
        
        return "redirect:hardwareEdit";
    }
    
    @RequestMapping(value = "/operator/hardware/deleteHardware")
    public String deleteHardware(ModelMap modelMap, YukonUserContext userContext, HttpServletRequest request, FlashScope flashScope, AccountInfoFragment accountInfoFragment,
                                 int energyCompanyId, 
                                 int accountId, 
                                 int inventoryId, 
                                 String deleteOption) throws Exception {
        
        /* Delete this hardware or just take it off the account and put in back in the warehouse */
        boolean delete = deleteOption.equalsIgnoreCase("delete");
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        hardwareService.deleteHardware(delete, inventoryId, accountId, energyCompany);
        
        setupHardwareInfoModelMap(accountInfoFragment, accountId, null, modelMap, userContext);
        
        flashScope.setConfirm(Collections.singletonList(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareEdit.hardwareDeleted")));
        return "redirect:hardwareList";
    }
    
     /* INIT BINDER */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateType dateValidationType = new DateType();
        binder.registerCustomEditor(Date.class, "fieldInstallDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldReceiveDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldRemoveDate", dateValidationType.getPropertyEditor());
    }

    /* HARDWARE INFO WRAPPER */
    public static class HardwareInfo {
        
        private HardwareDto hardwareDto = new HardwareDto();
        
        public HardwareDto getHardwareDto() {
            return hardwareDto;
        }
        public void setHardwareDto(HardwareDto hardwareDto) {
            this.hardwareDto = hardwareDto;
        }
    }
    
    @RequestMapping(value = "/operator/hardware/createYukonDevice")
    public View createYukonDevice(ModelMap modelMap, String deviceName, int accountId, int inventoryId, HttpServletResponse response) throws ServletRequestBindingException {
        response.setContentType("text/plain");
        int deviceId = -1;
        try {
            SimpleDevice yukonDevice = hardwareService.createTwoWayDevice(inventoryId, deviceName);
            deviceId = yukonDevice.getDeviceId();
        } catch (StarsTwoWayLcrYukonDeviceCreationException e) {
            modelMap.addAttribute("errorOccurred", Boolean.TRUE);
            modelMap.addAttribute("errorMsg", e.getMessage());
            return new JsonView();
        }
        modelMap.addAttribute("errorOccurred", Boolean.FALSE);
        modelMap.addAttribute("deviceId", deviceId);
        
        return new JsonView();
    }
    
    @RequestMapping(value = "/operator/hardware/serviceCompanyInfo")
    public String serviceCompanyInfo(ModelMap modelMap, YukonUserContext userContext, int serviceCompanyId) throws ServletRequestBindingException {
        ServiceCompanyDto serviceCompanyDto = serviceCompanyDao.getCompanyById(serviceCompanyId);
        LiteAddress serviceCompanyAddress = addressDao.getByAddressId(serviceCompanyDto.getAddressId());
        
        modelMap.addAttribute("companyName", serviceCompanyDto.getCompanyName());
        
        if(isValidInfoPart(serviceCompanyAddress.getLocationAddress1())) {
            modelMap.addAttribute("locationAddress1", serviceCompanyAddress.getLocationAddress1());
        }
        
        if(isValidInfoPart(serviceCompanyAddress.getLocationAddress2())) {
            modelMap.addAttribute("locationAddress2", serviceCompanyAddress.getLocationAddress2());
        }
        
        String locationAddress3 = getLocationAddressLine3(serviceCompanyAddress);
        if(StringUtils.isNotBlank(locationAddress3)) {
            modelMap.addAttribute("locationAddress3", locationAddress3);
        }
        
        if(isValidInfoPart(serviceCompanyDto.getMainPhoneNumber())) {
            modelMap.addAttribute("mainPhoneNumber", serviceCompanyDto.getMainPhoneNumber());
        }
        
        return "/operator/hardware/serviceCompanyInfo.jsp";
    }
    
    /* HELPERS */
    
    private void setupHardwareInfoModelMap(AccountInfoFragment accountInfoFragment, int accountId, Integer inventoryId, ModelMap modelMap, YukonUserContext userContext){
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        
        String defaultRoute;
        try {
            /* Apperently 'getDefaultRouteID' is not a simple getter and relies heavily on deep magic. */
            defaultRoute = paoDao.getYukonPAOName(energyCompany.getDefaultRouteID());
            defaultRoute = "Default - " + defaultRoute;
        } catch(NotFoundException e) {
            defaultRoute = "Default - (None)";
        }
        modelMap.addAttribute("defaultRoute", defaultRoute);
        
        LiteYukonPAObject[] routes = energyCompany.getAllRoutes();
        modelMap.addAttribute("routes", routes);
        
        modelMap.addAttribute("accountId", accountId);
        modelMap.addAttribute("inventoryId", inventoryId);
        
        modelMap.addAttribute("serviceCompanies", energyCompanyDao.getAllInheritedServiceCompanies(energyCompany.getEnergyCompanyID()));
        
        List<Warehouse> warehouses = energyCompany.getWarehouses();
        modelMap.addAttribute("warehouses", warehouses);
        
        if(inventoryId != null) {
            /* Device Status History */
            List<EventInventory> deviceStatusHistory = EventInventory.retrieveEventInventories(inventoryId);
            modelMap.addAttribute("deviceStatusHistory", deviceStatusHistory);
            
            /* Hardware History */
            modelMap.addAttribute("hardwareHistory", hardwareService.getHardwareHistory(inventoryId));
        }
        
        boolean inventoryChecking = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, userContext.getYukonUser());
        modelMap.addAttribute("inventoryChecking", inventoryChecking);
    }
    
    private boolean isValidInfoPart(String part) {
        return StringUtils.isNotBlank(part) && !part.equalsIgnoreCase(CtiUtilities.STRING_NONE);
    }
    
    private String getLocationAddressLine3(LiteAddress serviceCompanyAddress) {
        String locationAddress3 = "";
        if(isValidInfoPart(serviceCompanyAddress.getCityName())){
            locationAddress3 += serviceCompanyAddress.getCityName();
        }
        
        if(isValidInfoPart(serviceCompanyAddress.getStateCode())){
            if(StringUtils.isBlank(locationAddress3)){
                locationAddress3 += serviceCompanyAddress.getStateCode();
            } else {
                locationAddress3 += ", " + serviceCompanyAddress.getStateCode();
            }
        }
        
        if(isValidInfoPart(serviceCompanyAddress.getZipCode())){
            if(StringUtils.isBlank(locationAddress3)){
                locationAddress3 += serviceCompanyAddress.getZipCode();
            } else {
                locationAddress3 += " " + serviceCompanyAddress.getZipCode();
            }
        }
        return locationAddress3;
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

    @Autowired
    public void setHardwareService(HardwareService hardwareService) {
        this.hardwareService = hardwareService;
    }
    
    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setServiceCompanyDao(ServiceCompanyDao serviceCompanyDao) {
        this.serviceCompanyDao = serviceCompanyDao;
    }
    
    @Autowired
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setHardwareDtoValidator(HardwareDtoValidator hardwareDtoValidator) {
        this.hardwareDtoValidator = hardwareDtoValidator;
    }
}