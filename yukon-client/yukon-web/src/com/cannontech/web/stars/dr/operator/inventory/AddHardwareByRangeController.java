package com.cannontech.web.stars.dr.operator.inventory;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.AddByRange;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.AddByRangeHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.AddByRangeHelper.AddByRangeTask;

@Controller
@CheckRole(YukonRole.INVENTORY)
@RequestMapping("/operator/inventory/abr/*")
public class AddHardwareByRangeController {
    
    @Autowired private InventoryDao InventoryDao;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private PaoDao paoDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private AddByRangeHelper helper;
    @Autowired private MemoryCollectionProducer memoryCollectionProducer;
    private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    private AbrValidator validator = new AbrValidator();
    
    private class AbrValidator extends SimpleValidator<AddByRange> {
        
        public AbrValidator() {
            super(AddByRange.class);
        }

        @Override
        public void doValidation(AddByRange abr, Errors errors) {
            YukonListEntry entry = yukonListDao.getYukonListEntry(abr.getHardwareTypeId());
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
                /* For LCR 3102's the serial number must be a valid integer since it has to match the 
                 * address in DeviceCarrierSettings which is a varchar(18) */
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

    @RequestMapping
    public String view(ModelMap model, YukonUserContext context, int hardwareTypeId, String taskId) {
        YukonListEntry entry = yukonListDao.getYukonListEntry(hardwareTypeId); 
        
        if (taskId != null) {
            model.addAttribute("mode", PageEditMode.VIEW);
            
            AddByRangeTask task = (AddByRangeTask) resultsCache.getResult(taskId);
            model.addAttribute("task", task);
            model.addAttribute("abr", task.getAbr());
            setupModel(model, task.getAbr(), context, entry);
        } else {
            model.addAttribute("mode", PageEditMode.CREATE);
            
            AddByRange abr = new AddByRange();
            abr.setHardwareTypeId(hardwareTypeId);
            model.addAttribute("abr", abr);
            setupModel(model, abr, context, entry);
        }
        
        return "operator/inventory/abr/setup.jsp";
    }
    
    private void setupModel(ModelMap model, AddByRange abr, YukonUserContext context, YukonListEntry hardwareTypeEntry) {
        YukonEnergyCompany ec = yukonEnergyCompanyService.getEnergyCompanyByOperator(context.getYukonUser());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ec);
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        
        String defaultRoute;
        try {
            defaultRoute = paoDao.getYukonPAOName(energyCompany.getDefaultRouteId());
            defaultRoute = accessor.getMessage("yukon.web.modules.operator.hardware.defaultRoute") + defaultRoute;
        } catch(NotFoundException e) {
            defaultRoute = accessor.getMessage("yukon.web.modules.operator.hardware.defaultRouteNone");
        }
        
        HardwareType hardwareType = InventoryDao.getHardwareTypeById(abr.getHardwareTypeId());
        HardwareClass hardwareClass = hardwareType.getHardwareClass();
        List<LiteYukonPAObject> routes = energyCompany.getAllRoutes();
        
        boolean showVoltage = !hardwareType.isZigbee() && !hardwareClass.isGateway() && !hardwareClass.isThermostat();
        model.addAttribute("showVoltage", showVoltage);
        
        model.addAttribute("hardwareTypeId", hardwareTypeEntry.getEntryID());
        model.addAttribute("type", hardwareTypeEntry.getEntryText());
        model.addAttribute("ecId", ec.getEnergyCompanyId());
        model.addAttribute("serviceCompanies", energyCompanyDao.getAllInheritedServiceCompanies(energyCompany.getEnergyCompanyId()));
        model.addAttribute("defaultRoute", defaultRoute);
        model.addAttribute("routes", routes);
        model.addAttribute("none", accessor.getMessage("yukon.web.defaults.none"));
    }
    
    @RequestMapping(value="do", params="start")
    public String startTask(ModelMap model, YukonUserContext context, 
                        @ModelAttribute("abr") AddByRange abr, 
                        BindingResult result,
                        FlashScope flash) {
        
        validator.validate(abr, result);
        YukonListEntry typeEntry = yukonListDao.getYukonListEntry(abr.getHardwareTypeId());
        HardwareType type = HardwareType.valueOf(typeEntry.getYukonDefID());
        if (result.hasErrors()) {
            /* Add errors to flash scope */
            model.addAttribute("mode", PageEditMode.CREATE);
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            setupModel(model, abr, context, typeEntry);
            return "operator/inventory/abr/setup.jsp";
        }
        
        model.addAttribute("mode", PageEditMode.VIEW);
        setupModel(model, abr, context, typeEntry);
        
        AddByRangeTask task = helper.new AddByRangeTask(context, type, abr);
        String taskId = helper.startTask(task);
        model.addAttribute("taskId", taskId);
        
        return "redirect:view";
    }
    
    @RequestMapping(value="do", params="cancel")
    public String cancel(ModelMap model, FlashScope flash, String taskId) {
        if (StringUtils.isNotBlank(taskId)) {
            AbstractInventoryTask task = resultsCache.getResult(taskId);
            task.cancel();
            int processed = task.getCompletedItems(); 
            flash.setWarning(new YukonMessageSourceResolvable("yukon.web.modules.operator.abr.canceled", processed));
        }
        return "redirect:../home";
    }
    
    @RequestMapping
    public String newOperation(ModelMap model, String taskId, YukonUserContext context) {
        AddByRangeTask task = (AddByRangeTask) resultsCache.getResult(taskId);
        String description = resolver.getMessageSourceAccessor(context).getMessage("yukon.web.modules.operator.abr.successCollectionDescription");
        InventoryCollection temporaryCollection = memoryCollectionProducer.createCollection(task.getSuccessful().iterator(), description);
        model.addAttribute("inventoryCollection", temporaryCollection);
        model.addAllAttributes(temporaryCollection.getCollectionParameters());
        return "redirect:../inventoryActions";
    }
    
    @RequestMapping
    public String viewFailed(ModelMap model, String taskId, YukonUserContext context) {
        AddByRangeTask task = (AddByRangeTask) resultsCache.getResult(taskId);
        model.addAttribute("failed", task.getFailed());
        return "operator/inventory/abr/failed.jsp";
    }
    
    @Resource(name="inventoryTaskResultsCache")
    public void setResultsCache(RecentResultsCache<AbstractInventoryTask> resultsCache) {
        this.resultsCache = resultsCache;
    }
    
}