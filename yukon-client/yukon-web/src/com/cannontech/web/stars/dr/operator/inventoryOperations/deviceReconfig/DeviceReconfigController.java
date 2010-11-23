package com.cannontech.web.stars.dr.operator.inventoryOperations.deviceReconfig;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.events.loggers.DeviceReconfigEventLogService;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.dao.CommandScheduleDao;
import com.cannontech.stars.dr.hardware.model.CommandSchedule;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventoryOperations.deviceReconfig.model.DeviceReconfigOptions;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEVICE_RECONFIG)
public class DeviceReconfigController {

    private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    private DeviceReconfigOptionsValidator validator;
    private CommandScheduleDao commandScheduleDao;
    private DeviceReconfigEventLogService deviceReconfigEventLogService;

    @RequestMapping(value="/operator/inventory/inventoryOperations/deviceReconfig/setup", method=RequestMethod.GET)
    public String setup(HttpServletRequest request, ModelMap modelMap) throws ServletRequestBindingException {
        
        addCollectionToModelMap(request, modelMap);
        
        DeviceReconfigOptions deviceReconfigOptions = new DeviceReconfigOptions();
        modelMap.addAttribute("deviceReconfigOptions", deviceReconfigOptions);
        
        List<CommandSchedule> schedules = commandScheduleDao.getAll();
        modelMap.addAttribute("schedules", schedules);
        
        return "operator/inventory/inventoryOperations/deviceReconfig/setup.jsp";
    }
    
    @RequestMapping(value="/operator/inventory/inventoryOperations/deviceReconfig/save", method=RequestMethod.POST)
    public String save(@ModelAttribute("deviceReconfigOptions") DeviceReconfigOptions deviceReconfigOptions, BindingResult bindingResult, 
                       HttpServletRequest request, ModelMap modelMap, FlashScope flashScope, YukonUserContext userContext) throws ServletRequestBindingException {
        
        validator.validate(deviceReconfigOptions, bindingResult);
        
        if(bindingResult.hasErrors()) {
            /* Add errors to flash scope */
            addCollectionToModelMap(request, modelMap);
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "operator/inventory/inventoryOperations/deviceReconfig/setup.jsp";
        }
        
        /* TODO add with dao */
        
        /* Log Event */
        deviceReconfigEventLogService.taskCreated(userContext.getYukonUser(), deviceReconfigOptions.getName());
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.deviceReconfig.creationSuccessful", deviceReconfigOptions.getName()));
        
        return "redirect:/spring/stars/operator/inventory/inventoryOperations/home";
    }
    
    @RequestMapping(value="/operator/inventory/inventoryOperations/deviceReconfig/save", method=RequestMethod.POST, params="cancelButton")
    public String cancel(@ModelAttribute("deviceReconfigOptions") DeviceReconfigOptions deviceReconfigOptions, HttpServletRequest request, ModelMap modelMap) 
        throws ServletRequestBindingException {

        addCollectionToModelMap(request, modelMap);
        
        return "redirect:/spring/stars/operator/inventory/inventoryOperations/inventoryActions";
    }
    
    public void addCollectionToModelMap(HttpServletRequest request, ModelMap modelMap) throws ServletRequestBindingException {
        InventoryCollection inventoryCollection = inventoryCollectionFactory.createCollection(request);
        modelMap.addAttribute("inventoryCollection", inventoryCollection);
        modelMap.addAllAttributes(inventoryCollection.getCollectionParameters());
    }
    
    @Autowired
    public void setDeviceReconfigOptionsValidator(DeviceReconfigOptionsValidator validator) {
        this.validator = validator;
    }
    
    @Autowired
    public void setInventoryCollectionFactory(InventoryCollectionFactoryImpl inventoryCollectionFactory) {
        this.inventoryCollectionFactory = inventoryCollectionFactory;
    }
    
    @Autowired
    public void setCommandScheduleDao(CommandScheduleDao commandScheduleDao) {
        this.commandScheduleDao = commandScheduleDao;
    }
    
    @Autowired
    public void setDeviceReconfigEventLogService(DeviceReconfigEventLogService deviceReconfigEventLogService) {
        this.deviceReconfigEventLogService = deviceReconfigEventLogService;
    }
}