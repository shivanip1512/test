package com.cannontech.web.stars.dr.operator.inventory.configuration;

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
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.hardware.dao.CommandScheduleDao;
import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.CommandSchedule;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.stars.dr.hardware.service.HardwareConfigService.Status;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.inventory.configuration.model.DeviceReconfigOptions;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEVICE_RECONFIG)
public class DeviceReconfigController {
    
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private CommandScheduleDao commandScheduleDao;
    @Autowired private InventoryConfigEventLogService configEventLogService;
    @Autowired private InventoryConfigTaskDao configTaskDao;
    @Autowired private MemoryCollectionProducer collectionProducer;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private EnergyCompanyDao ecDao;
    
    private static final String key = "yukon.web.modules.operator.inventory.config.schedule.";
    
    private class OptionsValidator extends SimpleValidator<DeviceReconfigOptions> {
        
        OptionsValidator() {
            super(DeviceReconfigOptions.class);
        }
        
        @Override
        protected void doValidation(DeviceReconfigOptions target, Errors errors) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "error.required.name");
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", target.getName(), 250);
        }
    }
    
    @RequestMapping(value="/operator/inventory/deviceReconfig/setup", method=RequestMethod.GET)
    public void setup(HttpServletRequest request, ModelMap model, YukonUserContext userContext) 
    throws ServletRequestBindingException {
        
        DeviceReconfigOptions options = new DeviceReconfigOptions();
        model.addAttribute("deviceReconfigOptions", options);
        
        setupModelMap(request, model, userContext);
    }
    
    private void setupModelMap(HttpServletRequest req, ModelMap model, YukonUserContext userContext) {
        
        collectionFactory.addCollectionToModelMap(req, model);
        
        EnergyCompany energyCompany = ecDao.getEnergyCompany(userContext.getYukonUser());
        List<CommandSchedule> schedules = commandScheduleDao.getAll(energyCompany.getId());
        model.addAttribute("schedules", schedules);
    }
    
    @RequestMapping(value="/operator/inventory/deviceReconfig/save", method=RequestMethod.POST)
    public String save(@ModelAttribute("deviceReconfigOptions") DeviceReconfigOptions options, BindingResult result, 
                       HttpServletRequest req, ModelMap model, FlashScope flash, YukonUserContext userContext) {
        
        LiteYukonUser user = userContext.getYukonUser();
        int energyCompanyId = ecDao.getEnergyCompany(user).getId();
        OptionsValidator validator = new OptionsValidator();
        validator.validate(options, result);
        
        if (result.hasErrors()) {
            /* Add errors to flash scope */
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            setupModelMap(req, model, userContext);
            
            return "operator/inventory/deviceReconfig/setup.jsp";
        }
        
        try {
            configTaskDao.create(options.getName(), options.isSendInService(), options.isSendOutOfService(),
                collectionFactory.createCollection(req), energyCompanyId, user);
        } catch (DataIntegrityViolationException e) {
            
            result.rejectValue("name", key + "error.unavailable.name");
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            setupModelMap(req, model, userContext);
            
            return "operator/inventory/deviceReconfig/setup.jsp";
        }
        
        /* Log Event */
        configEventLogService.taskCreated(user, options.getName());
        
        flash.setConfirm(new YukonMessageSourceResolvable(key + "creationSuccessful", options.getName()));
        
        return "redirect:../home";
    }
    
    @RequestMapping(value="/operator/inventory/deviceReconfig/status", method=RequestMethod.GET)
    public String status(ModelMap model, int taskId) {
        
        InventoryConfigTask task = configTaskDao.getById(taskId);
        model.addAttribute("task", task);
        model.addAttribute("taskName", task.getTaskName());
        
        return "operator/inventory/deviceReconfig/status.jsp";
    }
    
    @RequestMapping(value="/operator/inventory/deviceReconfig/newOperation", method=RequestMethod.GET)
    public String newOperation(ModelMap model, int taskId, String type, YukonUserContext userContext) {
        
        InventoryConfigTask task = configTaskDao.getById(taskId);
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        List<InventoryIdentifier> inventory = Lists.newArrayList();
        String descriptionHint = "";
        Status status = Status.valueOf(type);
        inventory = configTaskDao.getSuccessFailList(taskId, status); 
        
        if (status == Status.SUCCESS) {
            String successList = accessor.getMessage("yukon.common.collection.inventory.successList");
            descriptionHint = task.getTaskName() + " " + successList;
        } else {
            String failedList = accessor.getMessage("yukon.common.collection.inventory.failedList");
            descriptionHint = task.getTaskName() + " " + failedList;
        }
        
        InventoryCollection collection = collectionProducer.createCollection(inventory.iterator(), descriptionHint);
        model.addAllAttributes(collection.getCollectionParameters());
        
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
    @RequestMapping(value="/operator/inventory/inventoryActions/deviceReconfig/delete", method=RequestMethod.POST)
    public String delete(int taskId, LiteYukonUser user, FlashScope flash) {
        
        InventoryConfigTask task = configTaskDao.getById(taskId);
        configTaskDao.delete(taskId);
        configEventLogService.taskDeleted(user, task.getTaskName());
        flash.setConfirm(new YukonMessageSourceResolvable(key + "deletionSuccessful", task.getTaskName()));
        
        return "redirect:/stars/operator/inventory/home";
    }
    
}