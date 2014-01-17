package com.cannontech.web.deviceConfiguration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.deviceConfiguration.model.DisplayableConfigurationData;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_CONFIG)
public class DeviceConfigurationController {
    
	@Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private ObjectFormattingService objectFormattingService;
	
    /**
     * A small class that wraps a JAXB category type to provide us the getValue() method
     * since JAXB only gives us value(), which can't be accessed in JSP-EL world.
     */
    public final static class DisplayableCategoryType implements DisplayableEnum {
        CategoryType categoryType;
        
        public DisplayableCategoryType(CategoryType categoryType) {
            this.categoryType = categoryType;
        }

        public String getValue() {
            return categoryType.value();
        }
        
        @Override
        public String getFormatKey() {
            return "yukon.web.modules.tools.configs.category." + categoryType.value() + ".title";
        }
    }
    
    @RequestMapping("home")
    public String home(ModelMap model, YukonUserContext context) {
        List<LightDeviceConfiguration> configurations = deviceConfigurationDao.getAllLightDeviceConfigurations();

        List<DisplayableConfigurationData> displayables = new ArrayList<>();
        for (LightDeviceConfiguration config : configurations) {
            displayables.add(
                new DisplayableConfigurationData(
                    config, 
                    deviceConfigurationDao.getNumberOfDevicesForConfiguration(config.getConfigurationId())));
        }
        
        model.addAttribute("configurations", displayables);
        
        model.addAttribute("categories", deviceConfigurationDao.getAllDeviceConfigurationCategories());
        
        List<DisplayableCategoryType> categoryTypes = new ArrayList<>();
        for (CategoryType type : CategoryType.values()) {
            categoryTypes.add(new DisplayableCategoryType(type));
        }
        
        List<DisplayableCategoryType> sortedTypes = 
            objectFormattingService.sortDisplayableValues(categoryTypes, null, null, context);
        
        model.addAttribute("categoryTypes", sortedTypes);
        
        model.addAttribute("mode", PageEditMode.VIEW);
        
        model.addAttribute("editingRoleProperty", YukonRoleProperty.ADMIN_EDIT_CONFIG);
        
        return "home.jsp";
    }
}
