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
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListDefs;
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
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.hardware.model.LMHardwareClass;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
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
import com.google.common.collect.Lists;

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
    
    /* HARDWARE LIST PAGE*/
    @RequestMapping
    public String hardwareList(YukonUserContext userContext, ModelMap modelMap, AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        ListMultimap<LMHardwareClass, HardwareDto> hardwareMap = hardwareService.getHardwareMapForAccount(accountInfoFragment.getAccountId(), accountInfoFragment.getEnergyCompanyId());
        
        modelMap.addAttribute("switches", hardwareMap.get(LMHardwareClass.SWITCH));
        modelMap.addAttribute("meters", hardwareMap.get(LMHardwareClass.METER));
        modelMap.addAttribute("thermostats", hardwareMap.get(LMHardwareClass.THERMOSTAT));
        
        modelMap.addAttribute("switchClass", LMHardwareClass.SWITCH);
        modelMap.addAttribute("thermostatClass", LMHardwareClass.THERMOSTAT);
        modelMap.addAttribute("meterClass", LMHardwareClass.METER);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        return "operator/hardware/hardwareList.jsp";
    }
    
    /* HARDWARE CREATE PAGE*/
    @RequestMapping
    public String hardwareCreate(YukonUserContext userContext, ModelMap modelMap, AccountInfoFragment accountInfoFragment, String hardwareClass) throws ServletRequestBindingException {
        modelMap.addAttribute("displayName", "Create Hardware");
        
        LMHardwareClass lmHardwareClass = LMHardwareClass.valueOf(hardwareClass.toUpperCase());
        List<DeviceTypeOpiton> deviceTypeOptions = Lists.newArrayList();
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        
        List<YukonListEntry> deviceTypeList = energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE).getYukonListEntries();
        for(YukonListEntry deviceTypeEntry : deviceTypeList) {
            HardwareType type = HardwareType.valueOf(deviceTypeEntry.getYukonDefID());
            if(type.getLMHardwareClass() == lmHardwareClass) {
                DeviceTypeOpiton option = new DeviceTypeOpiton();
                option.setDisplayName(deviceTypeEntry.getEntryText());
                option.setHardwareTypeEntryId(deviceTypeEntry.getEntryID());
                deviceTypeOptions.add(option);
            }
        }
        
        modelMap.addAttribute("deviceTypes", deviceTypeOptions);
        
        if(lmHardwareClass == LMHardwareClass.SWITCH) {
            modelMap.addAttribute("displayTypeKey", ".switchTypesLabel");
        } else if (lmHardwareClass == LMHardwareClass.THERMOSTAT) {
            modelMap.addAttribute("displayTypeKey", ".thermostatTypesLabel");
        } else {
            modelMap.addAttribute("displayTypeKey", ".meterTypesLabel");
        }
        
        if(lmHardwareClass != LMHardwareClass.METER) {
            modelMap.addAttribute("showSerialNumber", true);
            modelMap.addAttribute("serialNumberEditable", true);
            modelMap.addAttribute("showRoute", true);
        }
        
        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setHardwareClass(lmHardwareClass);
        hardwareDto.setFieldInstallDate(new Date());
        modelMap.addAttribute("hardwareDto", hardwareDto);
        
        setupHardwareEditModelMap(accountInfoFragment, null, modelMap, userContext, false);
        
        return "operator/hardware/hardwareEdit.jsp";
    }
    
    /* HARDWARE EDIT PAGE*/
    @RequestMapping
    public String hardwareEdit(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext,  AccountInfoFragment accountInfoFragment, int inventoryId) {
        
        hardwareService.validateInventoryAgainstAccount(Collections.singletonList(inventoryId), accountInfoFragment.getAccountId());
        modelMap.addAttribute("editMode", true);
        
        HardwareDto hardwareDto = hardwareService.getHardwareDto(inventoryId, accountInfoFragment.getEnergyCompanyId());
        modelMap.addAttribute("hardwareDto", hardwareDto);
        modelMap.addAttribute("displayName", hardwareDto.getDisplayName());
        
        boolean inventoryChecking = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, userContext.getYukonUser());
        
        /* For switches and tstats, if they have inventory checking turned off they can edit the serial number. */
        HardwareType hardwareType = hardwareDto.getHardwareType(); 
        if(!inventoryChecking && hardwareType.getLMHardwareClass() != LMHardwareClass.METER) {
            modelMap.addAttribute("serialNumberEditable", true);
        }
        
        /* For switches and tstats, show serial number instead of device name and show the route. */
        if(hardwareType.getLMHardwareClass() != LMHardwareClass.METER) {
            modelMap.addAttribute("showRoute", true);
            modelMap.addAttribute("showSerialNumber", true);
        }
        
        if(hardwareType.isSwitch() && hardwareType.isTwoWay()) {
            modelMap.addAttribute("showTwoWay", true);
        }
        
        setupHardwareEditModelMap(accountInfoFragment, inventoryId, modelMap, userContext, true);
        
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
        } catch (ObjectInOtherEnergyCompanyException e) {
            bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.unavailable");
        }
        
        setupHardwareEditModelMap(accountInfoFragment, inventoryId, modelMap, userContext, true);
        
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
    public String createHardware(@ModelAttribute("hardwareDto") HardwareDto hardwareDto, BindingResult bindingResult,
                                 ModelMap modelMap, 
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        String cancelButton = ServletRequestUtils.getStringParameter(request, "cancel");
        if (cancelButton != null) { /* Cancel Creation */
            AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
            return "redirect:hardwareList";
        }
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES_CREATE, userContext.getYukonUser());
        int inventoryId = -1;
        
        try {
            hardwareDtoValidator.validate(hardwareDto, bindingResult);
        
            if(!bindingResult.hasErrors()) {
                inventoryId = hardwareService.createHardware(hardwareDto, accountInfoFragment.getAccountId(), userContext);
            }
        } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
            bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.unavailable");
        } catch (ObjectInOtherEnergyCompanyException e) {
            bindingResult.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.unavailable");
        }
        
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
            return "/operator/hardware/hardwareEdit.jsp";
        }
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.hardwareEdit.hardwareCreated"));
        setupHardwareEditModelMap(accountInfoFragment, inventoryId, modelMap, userContext, true);
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
    private void setupHardwareEditModelMap(AccountInfoFragment accountInfoFragment, Integer inventoryId, ModelMap modelMap, YukonUserContext userContext, boolean editing){
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
        
        
        modelMap.addAttribute("serviceCompanies", energyCompanyDao.getAllInheritedServiceCompanies(energyCompany.getEnergyCompanyID()));
        
        
        if(editing){
            if(inventoryId != null) {
                modelMap.addAttribute("inventoryId", inventoryId);
                
                /* Device Status History */
                List<EventInventory> deviceStatusHistory = EventInventory.retrieveEventInventories(inventoryId);
                modelMap.addAttribute("deviceStatusHistory", deviceStatusHistory);
                
                /* Hardware History */
                modelMap.addAttribute("hardwareHistory", hardwareService.getHardwareHistory(inventoryId));
            }
        
            List<Warehouse> warehouses = energyCompany.getWarehouses();
            modelMap.addAttribute("warehouses", warehouses);
            boolean inventoryChecking = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_INVENTORY_CHECKING, userContext.getYukonUser());
            modelMap.addAttribute("inventoryChecking", inventoryChecking);
        }
    }
    
    /* DEVICE TYPE SELECT OPTIONS WRAPPER */
    public static class DeviceTypeOpiton {
        private int hardwareTypeEntryId;
        private String displayName;
        
        public int getHardwareTypeEntryId() {
            return hardwareTypeEntryId;
        }
        public void setHardwareTypeEntryId(int hardwareTypeEntryId) {
            this.hardwareTypeEntryId = hardwareTypeEntryId;
        }
        public String getDisplayName() {
            return displayName;
        }
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
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