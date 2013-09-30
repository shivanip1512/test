package com.cannontech.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.system.GlobalSettingSubCategory;
import com.cannontech.system.dao.ThemeDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class ThemeController {

    @Autowired private ThemeDao themeDao;
    
    @RequestMapping("/config/theme")
    public String theme(ModelMap model, YukonUserContext context) {
        
        model.addAttribute("defaultThemes", themeDao.getDefaultThemes());
        model.addAttribute("themes", themeDao.getNonDefaultThemes());
        
        model.addAttribute("category", GlobalSettingSubCategory.THEMES);
        model.addAttribute("category_icon", "icon-32-brush");
        
        return "config/theme.jsp";
    }

}