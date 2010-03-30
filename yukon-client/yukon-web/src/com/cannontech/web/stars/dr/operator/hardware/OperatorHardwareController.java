package com.cannontech.web.stars.dr.operator.hardware;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
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
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.validator.YukonValidationUtils;
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
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.model.LMHardwareClass;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareDto;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareService;
import com.cannontech.web.stars.dr.operator.hardware.validator.HardwareDtoValidator;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.util.JsonView;
import com.google.common.collect.ListMultimap;

@RequestMapping("/operator/hardware/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
public class OperatorHardwareController {
    public StarsDatabaseCache starsDatabaseCache;
    private HardwareService hardwareService;
    private EnergyCompanyDao energyCompanyDao;
    private PaoDao paoDao;
    private ServiceCompanyDao serviceCompanyDao;
    private AddressDao addressDao;
    private RolePropertyDao rolePropertyDao;
    private HardwareDtoValidator hardwareDtoValidator;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    /* HARDWARE LIST */
    @RequestMapping
    public String hardwareList(YukonUserContext userContext, ModelMap modelMap, AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        ListMultimap<LMHardwareClass, HardwareDto> hardwareMap = hardwareService.getHardwareMapForAccount(accountInfoFragment.getAccountId(), accountInfoFragment.getEnergyCompanyId());
        
        modelMap.addAttribute("switches", hardwareMap.get(LMHardwareClass.SWITCH));
        modelMap.addAttribute("meters", hardwareMap.get(LMHardwareClass.METER));
        modelMap.addAttribute("thermostats", hardwareMap.get(LMHardwareClass.THERMOSTAT));
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        modelMap.addAttribute("accountId", accountInfoFragment.getAccountId());
        modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());
        
        return "operator/hardware/hardwareList.jsp";
    }
    
    @RequestMapping
    public String hardwareEdit(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext,  AccountInfoFragment accountInfoFragment, int inventoryId) {
        
        hardwareService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), accountInfoFragment.getAccountId());
        
        HardwareDto hardwareDto = hardwareService.getHardwareDto(inventoryId, accountInfoFragment.getEnergyCompanyId());
        modelMap.addAttribute("hardwareDto", hardwareDto);
        modelMap.addAttribute("displayName", hardwareDto.getDisplayName());
        
        /* Setup HardwareInfo ModelMap */
        setupHardwareInfoModelMap(accountInfoFragment, inventoryId, modelMap, userContext);
        
        return "/operator/hardware/hardwareEdit.jsp";
    }
    
    @RequestMapping
    public String updateHardware(@ModelAttribute("hardwareDto") HardwareDto hardwareDto, BindingResult bindingResult,
                                 ModelMap modelMap, 
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 AccountInfoFragment accountInfoFragment,
                                 int inventoryId) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        hardwareService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), accountInfoFragment.getAccountId());
        
        boolean statusChange = false;
        
        /* Validate and Update*/
        try {
            hardwareDtoValidator.validate(hardwareDto, bindingResult);
            
            if(!bindingResult.hasErrors()) {
                
                hardwareDto.setInventoryId(inventoryId);
                statusChange = hardwareService.updateHardware(hardwareDto);
            }
        } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.unavailable");
        }
        
        /* Setup HardwareInfo ModelMap */
        setupHardwareInfoModelMap(accountInfoFragment, inventoryId, modelMap, userContext);
        if (bindingResult.hasErrors()) {
            
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            modelMap.addAttribute("displayName", hardwareDto.getDisplayName());
            return "/operator/hardware/hardwareEdit.jsp";
        } 
        
        /* If the device status was changed, spawn an event for it */
        if(statusChange){
            EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, hardwareDto.getDeviceStatusEntryId(), inventoryId, request.getSession());
        }
        
        /* Flash hardware updated */
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareEdit.hardwareUpdated"));
        
        return "redirect:hardwareEdit";
    }
    
    @RequestMapping
    public String deleteHardware(ModelMap modelMap, YukonUserContext userContext, HttpServletRequest request, FlashScope flashScope, AccountInfoFragment accountInfoFragment,
                                 int inventoryId, String deleteOption) throws Exception {
        
        hardwareService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), accountInfoFragment.getAccountId());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        
        /* Delete this hardware or just take it off the account and put in back in the warehouse */
        boolean delete = deleteOption.equalsIgnoreCase("delete");
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        hardwareService.deleteHardware(delete, inventoryId, accountInfoFragment.getAccountId(), energyCompany);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        if(delete) {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareEdit.hardwareDeleted"));
        } else {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareEdit.hardwareRemoved"));
        }
        return "redirect:hardwareList";
    }
    

    @RequestMapping
    public View createYukonDevice(ModelMap modelMap, String deviceName, int inventoryId, HttpServletResponse response) throws ServletRequestBindingException {
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
    
    @RequestMapping
    public String serviceCompanyInfo(ModelMap modelMap, YukonUserContext userContext, int serviceCompanyId) throws ServletRequestBindingException {
        ServiceCompanyDto serviceCompanyDto = serviceCompanyDao.getCompanyById(serviceCompanyId);
        LiteAddress serviceCompanyAddress = addressDao.getByAddressId(serviceCompanyDto.getAddressId());
        Address address = Address.getDisplayableAddress(serviceCompanyAddress);
        
        modelMap.addAttribute("companyName", serviceCompanyDto.getCompanyName());
        modelMap.addAttribute("address", address);
        modelMap.addAttribute("mainPhoneNumber", serviceCompanyDto.getMainPhoneNumber());
        
        return "/operator/hardware/serviceCompanyInfo.jsp";
    }
    
    /* INIT BINDER */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateType dateValidationType = new DateType();
        binder.registerCustomEditor(Date.class, "fieldInstallDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldReceiveDate", dateValidationType.getPropertyEditor());
        binder.registerCustomEditor(Date.class, "fieldRemoveDate", dateValidationType.getPropertyEditor());
    }

    /* HELPERS */
    private void setupHardwareInfoModelMap(AccountInfoFragment accountInfoFragment, Integer inventoryId, ModelMap modelMap, YukonUserContext userContext){
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        String defaultRoute;
        try {
            /* Apperently 'getDefaultRouteID' is not a simple getter and relies heavily on deep magic. */
            defaultRoute = paoDao.getYukonPAOName(energyCompany.getDefaultRouteID());
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardwareEdit.defaultRouteLabel") + defaultRoute;
        } catch(NotFoundException e) {
            defaultRoute = messageSourceAccessor.getMessage("yukon.web.modules.operator.hardwareEdit.defaultRouteNoneLabel");
        }
        modelMap.addAttribute("defaultRoute", defaultRoute);
        
        LiteYukonPAObject[] routes = energyCompany.getAllRoutes();
        modelMap.addAttribute("routes", routes);
        
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
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}