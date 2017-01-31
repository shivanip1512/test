package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.ServiceCompanyDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.AddByRange;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.AddByRangeHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.AddByRangeHelper.AddByRangeTask;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.INVENTORY)
@RequestMapping("/operator/inventory/abr/*")
public class AddHardwareByRangeController {
    
    @Autowired private AddByRangeHelper addByRangeHelper;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private MemoryCollectionProducer collectionProducer;
    @Autowired private PaoDao paoDao;
    @Autowired private ServiceCompanyDao serviceCompanyDao;
    @Autowired private StarsDatabaseCache starsDbCache;
    @Autowired private YukonListDao listDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired @Qualifier("inventoryTasks") private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    private AbrValidator validator = new AbrValidator();
    
    private class AbrValidator extends SimpleValidator<AddByRange> {
        
        public AbrValidator() {
            super(AddByRange.class);
        }
        
        @Override
        public void doValidation(AddByRange abr, Errors errors) {
            YukonListEntry entry = listDao.getYukonListEntry(abr.getHardwareTypeId());
            HardwareType type = HardwareType.valueOf(entry.getYukonDefID());
            validateSN(errors, abr.getFrom(), "from", type);
            validateSN(errors, abr.getTo(), "to", type);
            if (!errors.hasErrors() && Long.parseLong(abr.getFrom()) > Long.parseLong(abr.getTo())) {
                errors.rejectValue("from", "yukon.web.modules.operator.abr.error.sn.fromGreaterThanTo");
            }
        }
        
        private void validateSN(Errors errors, String sn, String path, HardwareType type) {
            if (StringUtils.isBlank(sn)) {
                errors.rejectValue(path, "yukon.web.modules.operator.abr.error.sn."+path+".required");
            } else {
                if (!StringUtils.isNumeric(sn)) {
                    errors.rejectValue(path, "yukon.web.modules.operator.abr.error."+path+".nonNumericSerialNumber");
                }
                // For LCR 3102's the serial number must be a valid integer since it has to match the
                // address in DeviceCarrierSettings which is a varchar(18)
                if (type == HardwareType.LCR_3102) {
                   try {
                       Integer.parseInt(sn);
                   } catch(NumberFormatException e) {
                       errors.rejectValue(path, "yukon.web.modules.operator.abr.error."+path+".tooLong.lcr3102");
                   }
                } else {
                    YukonValidationUtils.checkExceedsMaxLength(errors, path, sn, 30);
                }
            }
        }
    }
    
    @RequestMapping(value="view", method = RequestMethod.GET)
    public String view(ModelMap model, YukonUserContext userContext, int hardwareTypeId, String taskId) {
        
        YukonListEntry entry = listDao.getYukonListEntry(hardwareTypeId); 
        
        if (taskId != null) {
            model.addAttribute("mode", PageEditMode.VIEW);
            
            AddByRangeTask task = (AddByRangeTask) resultsCache.getResult(taskId);
            model.addAttribute("task", task);
            model.addAttribute("abr", task.getAbr());
            setupModel(model, task.getAbr(), userContext, entry);
        } else {
            model.addAttribute("mode", PageEditMode.CREATE);
            
            AddByRange abr = new AddByRange();
            abr.setHardwareTypeId(hardwareTypeId);
            model.addAttribute("abr", abr);
            setupModel(model, abr, userContext, entry);
        }
        
        return "operator/inventory/abr/setup.jsp";
    }
    
    private void setupModel(ModelMap model, AddByRange abr, YukonUserContext userContext,
            YukonListEntry hardwareTypeEntry) {
        
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        String defaultRoute;
        try {
            int defaultRouteId = defaultRouteService.getDefaultRouteId(ec);
            defaultRoute = paoDao.getYukonPAOName(defaultRouteId);
            defaultRoute = accessor.getMessage("yukon.common.route.default", defaultRoute);
        } catch(NotFoundException e) {
            defaultRoute = accessor.getMessage("yukon.common.route.default.none");
        }
        
        HardwareType hardwareType = inventoryDao.getHardwareTypeById(abr.getHardwareTypeId());
        HardwareClass hardwareClass = hardwareType.getHardwareClass();
        
        List<LiteYukonPAObject> routes = ecDao.getAllRoutes(ec);
        
        boolean showVoltage = !hardwareType.isZigbee() && !hardwareClass.isGateway() && !hardwareClass.isThermostat();
        model.addAttribute("showVoltage", showVoltage);
        
        List<Integer> energyCompanyIds = Lists.transform(ec.getAncestors(true), EnergyCompanyDao.TO_ID_FUNCTION);
        
        model.addAttribute("hardwareTypeId", hardwareTypeEntry.getEntryID());
        model.addAttribute("type", hardwareTypeEntry.getEntryText());
        model.addAttribute("ecId", ec.getId());
        model.addAttribute("serviceCompanies", serviceCompanyDao.getAllServiceCompanies(energyCompanyIds));
        model.addAttribute("defaultRoute", defaultRoute);
        model.addAttribute("routes", routes);
        model.addAttribute("none", accessor.getMessage("yukon.common.none.choice"));
    }
    
    @RequestMapping(value="do", params="start", method = RequestMethod.GET)
    public String startTask(ModelMap model, YukonUserContext userContext, @ModelAttribute("abr") AddByRange abr,
            BindingResult result, FlashScope flash) {
        
        validator.validate(abr, result);
        YukonListEntry typeEntry = listDao.getYukonListEntry(abr.getHardwareTypeId());
        HardwareType type = HardwareType.valueOf(typeEntry.getYukonDefID());
        if (result.hasErrors()) {
            // Add errors to flash scope
            model.addAttribute("mode", PageEditMode.CREATE);
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            setupModel(model, abr, userContext, typeEntry);
            return "operator/inventory/abr/setup.jsp";
        }
        
        model.addAttribute("mode", PageEditMode.VIEW);
        setupModel(model, abr, userContext, typeEntry);
        
        AddByRangeTask task = addByRangeHelper.new AddByRangeTask(userContext, type, abr);
        String taskId = addByRangeHelper.startTask(task);
        model.addAttribute("taskId", taskId);
        
        return "redirect:view";
    }
    
    @RequestMapping(value="do", params="cancel", method = RequestMethod.GET)
    public String cancel(FlashScope flash, String taskId) {
        
        if (StringUtils.isNotBlank(taskId)) {
            AbstractInventoryTask task = resultsCache.getResult(taskId);
            task.cancel();
            int processed = task.getCompletedItems(); 
            flash.setWarning(new YukonMessageSourceResolvable("yukon.web.modules.operator.abr.canceled", processed));
        }
        
        return "redirect:../home";
    }

    @RequestMapping("newOperation")
    public String newOperation(ModelMap model, String taskId, YukonUserContext userContext) {
        
        AddByRangeTask task = (AddByRangeTask) resultsCache.getResult(taskId);
        String description = messageResolver.getMessageSourceAccessor(userContext).getMessage(
            "yukon.web.modules.operator.abr.successCollectionDescription");
        InventoryCollection temporaryCollection =
                collectionProducer.createCollection(task.getSuccessful().iterator(), description);
        model.addAttribute("inventoryCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());
        
        return "redirect:../inventoryActions";
    }
    
    @RequestMapping("viewFailed")
    public String viewFailed(ModelMap model, String taskId) {
        
        AddByRangeTask task = (AddByRangeTask) resultsCache.getResult(taskId);
        model.addAttribute("failed", task.getFailed());
        
        return "operator/inventory/abr/failed.jsp";
    }
    
}