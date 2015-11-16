package com.cannontech.web.stars.dr.operator.inventory.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.CollationUtils;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.dao.StaticLoadGroupMappingDao;
import com.cannontech.stars.dr.hardware.model.StarsStaticLoadGroupMapping;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.serialize.AddressingGroup;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareConfig;
import com.cannontech.web.stars.dr.operator.hardware.validator.ColdLoadPickupValidator;
import com.cannontech.web.stars.dr.operator.hardware.validator.TamperDetectValidator;
import com.cannontech.web.stars.dr.operator.inventory.configuration.model.Group;
import com.cannontech.web.stars.dr.operator.inventory.configuration.model.NewConfigSettings;
import com.cannontech.web.stars.dr.operator.inventory.model.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.model.collection.MemoryCollectionProducer;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.NewLmConfigHelper;
import com.cannontech.web.stars.dr.operator.inventory.service.impl.NewLmConfigHelper.NewLmConfigTask;
import com.cannontech.web.util.SpringWebUtil;
import com.google.common.collect.ImmutableSet;

@CheckCparm(MasterConfigBoolean.SEND_INDIVIDUAL_SWITCH_CONFIG)
@Controller
public class NewConfigController {
    
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private EnergyCompanySettingDao ecSettingsDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private NewLmConfigHelper helper;
    @Autowired private MemoryCollectionProducer collectionProducer;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private StaticLoadGroupMappingDao staticLoadGroupMappingDao;
    @Autowired private LoadGroupDao loadGroupDao;
    @Autowired private StarsDatabaseCache cache;
    @Autowired @Qualifier("inventoryTasks") private RecentResultsCache<AbstractInventoryTask> resultsCache;
    
    private final ColdLoadPickupValidator coldLoadPickupValidator = new ColdLoadPickupValidator();
    private final TamperDetectValidator tamperDetectValidator = new TamperDetectValidator();
    
    private final static String key = "yukon.web.modules.operator.inventory.config.new.";
    private Set<HardwareConfigType> routableConfigs = ImmutableSet.of(
            HardwareConfigType.EXPRESSCOM,
            HardwareConfigType.VERSACOM,
            HardwareConfigType.SA205,
            HardwareConfigType.SA305,
            HardwareConfigType.SA_SIMPLE);
    
    public enum NewAction { SUCCESSFUL, FAILED, UNSUPPORTED }
    
    @RequestMapping("/operator/inventory/actions/config/new")
    public String setup(HttpServletRequest req, ModelMap model, YukonUserContext userContext, String type) 
    throws ServletRequestBindingException, InstantiationException {
        
        HardwareConfigType configType = setupModel(req, model, userContext, type);
        
        /** Settings Bean */
        HardwareConfig config = new HardwareConfig(configType);
        NewConfigSettings settings = new NewConfigSettings();
        settings.setConfig(config);
        model.addAttribute("settings", settings);
        
        return "operator/inventory/config/new.jsp";
    }
    
    @RequestMapping(value="/operator/inventory/actions/config/send", method=RequestMethod.POST)
    public String send(HttpServletRequest req, ModelMap model, FlashScope flash, String action, 
            InventoryCollection collection, YukonUserContext userContext) 
    throws ServletRequestBindingException, InstantiationException {
        
        NewConfigSettings settings = new NewConfigSettings();
        HardwareConfigType type = HardwareConfigType.valueOf(ServletUtil.getParameter(req, "config.type"));
        HardwareConfig config = new HardwareConfig(type);
        settings.setConfig(config);
        
        BindingResult result = SpringWebUtil.bind(model, req, settings, "settings", key);
        
        EnergyCompany ec = ecDao.getEnergyCompany(userContext.getYukonUser());
        boolean trackHardware = ecSettingsDao.getBoolean(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING, ec.getId());
        
        if (trackHardware) {
            settings.getConfig().getAddressingInfo().validate(result, "config.");
            coldLoadPickupValidator.validate(config, result);
            if (type.isHasTamperDetect()) {
                tamperDetectValidator.validate(config, result);
            }
        }
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            
            setupModel(req, model, userContext, settings.getConfig().getType().name());
            
            return "operator/inventory/config/new.jsp";
        }
        
        settings.setBatch(action.equalsIgnoreCase("batch"));
        
        NewLmConfigTask task = helper.new NewLmConfigTask(collection, userContext, settings);
        String taskId = helper.startTask(task);
        
        model.addAttribute("taskId", taskId);
        collectionFactory.addCollectionToModelMap(req, model);
        
        return "redirect:" + taskId + "/status";
    }
    
    @RequestMapping("/operator/inventory/actions/config/{taskId}/status")
    public String view(ModelMap model, @PathVariable String taskId) {
        
        NewLmConfigTask task = (NewLmConfigTask) resultsCache.getResult(taskId);
        model.addAttribute("task", task);
        model.addAttribute("inventoryCollection", task.getCollection());
        
        return "operator/inventory/config/new.status.jsp";
    }
    
    /** Setup the model for the new config page and return the determined config type. */
    private HardwareConfigType setupModel(HttpServletRequest req, ModelMap model, YukonUserContext userContext, String type)
    throws ServletRequestBindingException, InstantiationException {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        InventoryCollection collection = collectionFactory.addCollectionToModelMap(req, model);
        
        Set<HardwareType> hardwareTypes = new HashSet<>();
        Set<HardwareConfigType> configTypes = new HashSet<>();
        Map<HardwareConfigType, Integer> configToDevices = new HashMap<>();
        for (InventoryIdentifier identifier : collection.getList()) {
            HardwareType hardwareType = identifier.getHardwareType();
            HardwareConfigType configType = hardwareType.getHardwareConfigType();
            hardwareTypes.add(hardwareType);
            configTypes.add(configType);
            Integer deviceCount = configToDevices.get(configType);
            deviceCount = deviceCount == null ? 1 : ++deviceCount;
            configToDevices.put(configType, deviceCount);
        }
        
        List<HardwareType> distinctHardwareTypes = new ArrayList<>(hardwareTypes);
        Collections.sort(distinctHardwareTypes, CollationUtils.enumComparator(accessor));
        model.addAttribute("hardwareTypes", distinctHardwareTypes);
        
        List<HardwareConfigType> distinctConfigTypes = new ArrayList<>(configTypes);
        Collections.sort(distinctConfigTypes, CollationUtils.enumComparator(accessor));
        model.addAttribute("configs", distinctConfigTypes);
        model.addAttribute("configToDevices", configToDevices);
        HardwareConfigType configType = type == null ? distinctConfigTypes.get(0) : HardwareConfigType.valueOf(type);
        
        LiteYukonUser user = userContext.getYukonUser();
        EnergyCompany ec = ecDao.getEnergyCompany(user);
        
        boolean trackHardware = ecSettingsDao.getBoolean(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING, ec.getId());
        model.addAttribute("trackHardware", trackHardware);
        
        /** Load Groups */
        if (!trackHardware) {
            
            List<Group> groups = new ArrayList<>();
            String batchToggle = globalSettingDao.getString(GlobalSettingType.BATCHED_SWITCH_COMMAND_TOGGLE);
            boolean useStatic = StarsUtils.BATCH_SWITCH_COMMAND_MANUAL.equals(batchToggle)
                    && VersionTools.staticLoadGroupMappingExists();
            
            if (useStatic) {
                for (StarsStaticLoadGroupMapping group : staticLoadGroupMappingDao.getAll()) {
                    groups.add(Group.of(group.getLoadGroupID(), group.getLoadGroupName()));
                }
            } else {
                
                LiteStarsEnergyCompany lsec = cache.getEnergyCompany(ec.getId());
                StarsEnergyCompanySettings settings = lsec.getStarsEnergyCompanySettings(StarsYukonUser.newInstance(user));
                StarsEnrollmentPrograms categories = settings.getStarsEnrollmentPrograms();
                
                for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
                    for (int j = 0; j < categories.getStarsApplianceCategory(i).getStarsEnrLMProgramCount(); j++) {
                        StarsEnrLMProgram program = categories.getStarsApplianceCategory(i).getStarsEnrLMProgram(j);
                        for (int k = 0; k < program.getAddressingGroupCount(); k++) {
                            AddressingGroup group = program.getAddressingGroup(k);
                            if (group.getEntryID() > 0) {
                                groups.add(Group.of(group.getEntryID(), group.getContent()));
                            }
                        }
                    }
                }
            }
            
            model.addAttribute("groups", groups);
        }
        
        /** Routes */
        boolean routableConfig = routableConfigs.contains(configType);
        boolean hasRoutableDeviceTypes = false;
        
        for (HardwareType hardwareType : distinctHardwareTypes) {
            if (hardwareType.isRoutable()) {
                hasRoutableDeviceTypes = true;
                break;
            }
        }
        
        model.addAttribute("showRoute", routableConfig && hasRoutableDeviceTypes);
        List<LiteYukonPAObject> routes = ecDao.getAllRoutes(ec);
        model.addAttribute("routes", routes);
        LiteYukonPAObject defaultRoute = defaultRouteService.getDefaultRoute(ec);
        model.addAttribute("defaultRoute", defaultRoute);
        
        return configType;
    }
    
    @RequestMapping("/operator/inventory/actions/config/new-action")
    public String action(ModelMap model, String taskId, YukonUserContext userContext, NewAction type) {
        
        NewLmConfigTask task = (NewLmConfigTask) resultsCache.getResult(taskId);
        String code;
        Iterable<InventoryIdentifier> inventory;
        
        if (type == NewAction.SUCCESSFUL) {
            code = key + "successful.description";
            inventory = task.getSuccessful();
        } else if (type == NewAction.FAILED) {
            code = key + "failed.description";
            inventory = task.getFailed();
        } else {
            code = key + "unsupported.description";
            inventory = task.getUnsupported();
        }
        
        String description = messageResolver.getMessageSourceAccessor(userContext).getMessage(code);
        InventoryCollection collection = collectionProducer.createCollection(inventory.iterator(), description);
        model.addAttribute("inventoryCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        
        return "redirect:/stars/operator/inventory/inventoryActions";
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null) {
            MessageCodesResolver resolver = new YukonMessageCodeResolver(key);
            binder.setMessageCodesResolver(resolver);
        }
    }
    
}