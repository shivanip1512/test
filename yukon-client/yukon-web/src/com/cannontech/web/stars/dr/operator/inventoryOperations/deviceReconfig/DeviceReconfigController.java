package com.cannontech.web.stars.dr.operator.inventoryOperations.deviceReconfig;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.events.loggers.InventoryConfigEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.CommandScheduleDao;
import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.CommandSchedule;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem.Status;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventoryOperations.deviceReconfig.model.DeviceReconfigOptions;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.collection.MemoryCollectionProducer;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEVICE_RECONFIG)
public class DeviceReconfigController {

    private InventoryCollectionFactoryImpl inventoryCollectionFactory;
    private CommandScheduleDao commandScheduleDao;
    private InventoryConfigEventLogService inventoryConfigEventLogService;
    private InventoryConfigTaskDao inventoryConfigTaskDao;
    private MemoryCollectionProducer memoryCollectionProducer;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private EnergyCompanyDao energyCompanyDao;

    private class OptionsValidator extends SimpleValidator<DeviceReconfigOptions> {

        OptionsValidator() {
            super(DeviceReconfigOptions.class);
        }

        @Override
        protected void doValidation(DeviceReconfigOptions target, Errors errors) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.modules.operator.deviceReconfig.error.required.name");
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", target.getName(), 250);
        }
    }

    @RequestMapping(value="/operator/inventory/inventoryOperations/deviceReconfig/setup", method=RequestMethod.GET)
    public void setup(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
        
        inventoryCollectionFactory.addCollectionToModelMap(request, modelMap);
        
        DeviceReconfigOptions deviceReconfigOptions = new DeviceReconfigOptions();
        modelMap.addAttribute("deviceReconfigOptions", deviceReconfigOptions);

        LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(userContext.getYukonUser());
        List<CommandSchedule> schedules = commandScheduleDao.getAll(energyCompany.getEnergyCompanyID());
        modelMap.addAttribute("schedules", schedules);
    }
    
    @RequestMapping(value="/operator/inventory/inventoryOperations/deviceReconfig/save", method=RequestMethod.POST)
    public String save(@ModelAttribute("deviceReconfigOptions") DeviceReconfigOptions deviceReconfigOptions, BindingResult bindingResult, 
                       HttpServletRequest request, ModelMap modelMap, FlashScope flashScope, YukonUserContext userContext) throws ServletRequestBindingException {

        int energyCompanyId = energyCompanyDao.getEnergyCompany(userContext.getYukonUser()).getEnergyCompanyID();
        OptionsValidator validator = new OptionsValidator();
        validator.validate(deviceReconfigOptions, bindingResult);

        if(bindingResult.hasErrors()) {
            /* Add errors to flash scope */
            inventoryCollectionFactory.addCollectionToModelMap(request, modelMap);
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "operator/inventory/inventoryOperations/deviceReconfig/setup.jsp";
        }

        try {
            inventoryConfigTaskDao.create(deviceReconfigOptions.getName(), deviceReconfigOptions.isSendInService(), inventoryCollectionFactory.createCollection(request), energyCompanyId);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("name", "yukon.web.modules.operator.deviceReconfig.error.unavailable.name");
            inventoryCollectionFactory.addCollectionToModelMap(request, modelMap);
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "operator/inventory/inventoryOperations/deviceReconfig/setup.jsp";
        }
        
        /* Log Event */
        inventoryConfigEventLogService.taskCreated(userContext.getYukonUser(), deviceReconfigOptions.getName());
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.deviceReconfig.creationSuccessful", deviceReconfigOptions.getName()));
        
        return "redirect:../home";
    }
    
    @RequestMapping(value="/operator/inventory/inventoryOperations/deviceReconfig/save", method=RequestMethod.POST, params="cancelButton")
    public String cancel(@ModelAttribute("deviceReconfigOptions") DeviceReconfigOptions deviceReconfigOptions, HttpServletRequest request, ModelMap modelMap) 
        throws ServletRequestBindingException {

        inventoryCollectionFactory.addCollectionToModelMap(request, modelMap);
        
        return "redirect:/spring/stars/operator/inventory/inventoryOperations/inventoryActions";
    }
    
    @RequestMapping(value="/operator/inventory/inventoryOperations/deviceReconfig/status", method=RequestMethod.GET)
    public String status(HttpServletRequest request, ModelMap modelMap, int taskId) throws ServletRequestBindingException {
        
        InventoryConfigTask task = inventoryConfigTaskDao.getById(taskId);
        modelMap.addAttribute("task", task);
        modelMap.addAttribute("taskName", task.getTaskName());
        
        return "operator/inventory/inventoryOperations/deviceReconfig/status.jsp";
    }
    
    @RequestMapping(value="/operator/inventory/inventoryOperations/deviceReconfig/newOperation", method=RequestMethod.GET)
    public String newOperation(HttpServletRequest request, ModelMap modelMap, int taskId, String type, YukonUserContext userContext){
        
        InventoryConfigTask task = inventoryConfigTaskDao.getById(taskId);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        List<InventoryIdentifier> inventory = Lists.newArrayList();
        String descriptionHint = "";
        Status status = Status.valueOf(type);
        inventory = inventoryConfigTaskDao.getSuccessFailList(taskId, status); 
        
        if (status == Status.SUCCESS) {
            String successList = messageSourceAccessor.getMessage("yukon.common.collection.inventory.successList");
            descriptionHint = task.getTaskName() + " " + successList;
        } else {
            String failedList = messageSourceAccessor.getMessage("yukon.common.collection.inventory.failedList");
            descriptionHint = task.getTaskName() + " " + failedList;
        }
        
        InventoryCollection temporaryCollection = memoryCollectionProducer.createCollection(inventory.iterator(), descriptionHint);
        modelMap.addAllAttributes(temporaryCollection.getCollectionParameters());
        
        return "redirect:/spring/stars/operator/inventory/inventoryOperations/inventoryActions";
    }
    
    @RequestMapping(value="/operator/inventory/inventoryActions/deviceReconfig/delete", method=RequestMethod.POST)
    public String delete(HttpServletRequest request, ModelMap modelMap, int taskId, YukonUserContext userContext) throws ServletRequestBindingException {
        InventoryConfigTask task = inventoryConfigTaskDao.getById(taskId);
        inventoryConfigTaskDao.delete(taskId);
        inventoryConfigEventLogService.taskDeleted(userContext.getYukonUser(), task.getTaskName());
        return "redirect:/spring/stars/operator/inventory/inventoryOperations/home";
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
    public void setInventoryConfigEventLogService(InventoryConfigEventLogService inventoryConfigEventLogService) {
        this.inventoryConfigEventLogService = inventoryConfigEventLogService;
    }
    
    @Autowired
    public void setInventoryConfigTaskDao(InventoryConfigTaskDao inventoryConfigTaskDao) {
        this.inventoryConfigTaskDao = inventoryConfigTaskDao;
    }
    
    @Autowired
    public void setMemoryCollectionProducer(MemoryCollectionProducer memoryCollectionProducer) {
        this.memoryCollectionProducer = memoryCollectionProducer;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }
}