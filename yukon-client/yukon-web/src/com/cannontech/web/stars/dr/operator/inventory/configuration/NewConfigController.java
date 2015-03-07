package com.cannontech.web.stars.dr.operator.inventory.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.i18n.CollationUtils;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.collection.InventoryCollectionFactoryImpl;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareConfig;
import com.cannontech.web.stars.dr.operator.inventory.configuration.model.NewConfigSettings;
import com.cannontech.web.util.SpringWebUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@CheckRoleProperty(YukonRoleProperty.SN_UPDATE_RANGE)
@Controller
public class NewConfigController {
    
    @Autowired private InventoryCollectionFactoryImpl collectionFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private EnergyCompanySettingDao ecSettingsDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @RequestMapping("/operator/inventory/actions/config/new")
    public String setup(HttpServletRequest req, ModelMap model, YukonUserContext userContext, String type) 
    throws ServletRequestBindingException {
        
        HardwareConfigType configType = setupModel(req, model, userContext, type);
        
        /** Settings Bean */
        HardwareConfig config = new HardwareConfig(configType);
        NewConfigSettings settings = new NewConfigSettings();
        settings.setConfig(config);
        model.addAttribute("settings", settings);
        
        return "operator/inventory/config/new.jsp";
    }
    
    @RequestMapping(value="/operator/inventory/actions/config/send", method=RequestMethod.POST)
    public String send(HttpServletRequest req, ModelMap model, FlashScope flash, 
            InventoryCollection collection, YukonUserContext userConext) 
    throws ServletRequestBindingException {
        
        NewConfigSettings settings = new NewConfigSettings();
        HardwareConfigType type = HardwareConfigType.valueOf(ServletUtil.getParameter(req, "config.type"));
        HardwareConfig config = new HardwareConfig(type);
        settings.setConfig(config);
        
        BindingResult result = SpringWebUtil.bind(model, req, settings, "settings", 
                "yukon.web.modules.operator.inventory.config.new.");
        
        settings.getConfig().getAddressingInfo().validate(result, "config.");
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            
            setupModel(req, model, userConext, settings.getConfig().getType().name());
            
            return "operator/inventory/config/new.jsp";
        }
        
        return null;
    }
    
    private HardwareConfigType setupModel(HttpServletRequest req, ModelMap model, YukonUserContext userContext, String type) 
    throws ServletRequestBindingException {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        InventoryCollection collection = collectionFactory.addCollectionToModelMap(req, model);
        
        Set<HardwareType> hardwareTypes = new HashSet<>();
        Set<HardwareConfigType> configTypes = new HashSet<>();
        ListMultimap<HardwareConfigType, HardwareType> configToType = ArrayListMultimap.create();
        for (InventoryIdentifier identifier : collection.getList()) {
            HardwareType hardwareType = identifier.getHardwareType();
            HardwareConfigType config = hardwareType.getHardwareConfigType();
            hardwareTypes.add(hardwareType);
            configTypes.add(config);
            configToType.put(config, hardwareType);
        }
        
        List<HardwareType> distinctHardwareTypes = new ArrayList<>(hardwareTypes);
        Collections.sort(distinctHardwareTypes, CollationUtils.enumComparator(accessor));
        model.addAttribute("hardwareTypes", distinctHardwareTypes);
        
        List<HardwareConfigType> distinctConfigTypes = new ArrayList<>(configTypes);
        Collections.sort(distinctConfigTypes, CollationUtils.enumComparator(accessor));
        model.addAttribute("configs", distinctConfigTypes);
        model.addAttribute("configToType", configToType.asMap());
        HardwareConfigType configType = type == null ? distinctConfigTypes.get(0) : HardwareConfigType.valueOf(type);
        
        EnergyCompany ec = ecDao.getEnergyCompany(userContext.getYukonUser());
        
        boolean trackHardware = ecSettingsDao.getBoolean(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING, ec.getId());
        model.addAttribute("trackHardware", trackHardware);
        
        /** Routes */
        // TODO Figure out if routing option is shown based on device types in collection
        model.addAttribute("showRoute", true);
        List<LiteYukonPAObject> routes = ecDao.getAllRoutes(ec);
        model.addAttribute("routes", routes);
        LiteYukonPAObject defaultRoute = defaultRouteService.getDefaultRoute(ec);
        model.addAttribute("defaultRoute", defaultRoute);
        
        return configType;
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            MessageCodesResolver resolver = new YukonMessageCodeResolver("yukon.web.modules.operator.inventory.config.new.");
            binder.setMessageCodesResolver(resolver);
        }
    }
    
}