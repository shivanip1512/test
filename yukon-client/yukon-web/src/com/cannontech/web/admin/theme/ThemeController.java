package com.cannontech.web.admin.theme;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.theme.dao.ThemeDao;
import com.cannontech.web.admin.theme.model.Theme;
import com.cannontech.web.admin.theme.model.ThemePropertyType;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.resources.ResourceCache;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.support.MappedPropertiesHelper;
import com.google.common.collect.ImmutableMap;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class ThemeController {

    @Autowired private ResourceCache resourceCache;
    @Autowired private ThemeDao themeDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private ResourceLoader loader;
    
    private SimpleValidator<Theme> validator = new SimpleValidator<Theme>(Theme.class) {
        @Override
        protected void doValidation(Theme target, Errors errors) {
            if (StringUtils.isNotBlank(target.getName())) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "name", target.getName(), 255);
            } else {
                YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.required");
            }
        }
    };
    
    @RequestMapping(value="/config/themes", method=RequestMethod.GET)
    public String themes(ModelMap model, YukonUserContext context) {
        
        buildModel(model, context, null, PageEditMode.VIEW);
        return "config/themes.jsp";
    }
    
    @RequestMapping(value="/config/themes/{id}", method=RequestMethod.GET)
    public String view(ModelMap model, YukonUserContext context, @PathVariable int id) {
        
        buildModel(model, context, themeDao.getTheme(id), PageEditMode.VIEW);
        return "config/themes.jsp";
    }
    
    @RequestMapping(value="/config/themes/{id}/edit", method=RequestMethod.GET)
    public String edit(ModelMap model, YukonUserContext context, @PathVariable int id) {
        
        buildModel(model, context, themeDao.getTheme(id), PageEditMode.EDIT);
        model.addAttribute("cancelId", id);
        return "config/themes.jsp";
    }
    
    @RequestMapping(value="/config/themes/{id}/copy", method=RequestMethod.GET)
    public String copy(ModelMap model, YukonUserContext context, @PathVariable int id) {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        
        Theme original = themeDao.getTheme(id);
        String copyName = accessor.getMessage("yukon.common.copyof", original.getName());
        
        Theme copy = new Theme();
        copy.setName(copyName);
        copy.setProperties(ImmutableMap.copyOf(original.getProperties()));
        
        buildModel(model, context, copy, PageEditMode.CREATE);
        model.addAttribute("cancelId", original.getThemeId());
        
        return "config/themes.jsp";
    }
    
    @RequestMapping(value="/config/themes/create", method=RequestMethod.GET)
    public String create(ModelMap model, YukonUserContext context) {
        
        Theme theme = new Theme();
        for(ThemePropertyType type : ThemePropertyType.values()) {
            theme.getProperties().put(type, type.getDefaultValue());
        }
        
        buildModel(model, context, theme, PageEditMode.CREATE);
        
        return "config/themes.jsp";
    }
    
    @RequestMapping(value="/config/themes", method=RequestMethod.POST)
    public String create(ModelMap model, 
                         YukonUserContext context,
                         @ModelAttribute("command") Theme theme,
                         BindingResult result,
                         FlashScope flash) {
        validator.validate(theme, result);
        if (result.hasErrors()) {
            return error(result, flash, model, context, theme, PageEditMode.CREATE);
        }
        
        themeDao.saveTheme(theme);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.config.themes.created", theme.getName()));
        return "redirect:/adminSetup/config/themes/" + theme.getThemeId();
    }
    
    @RequestMapping(value="/config/themes/{id}", method=RequestMethod.PUT)
    public String update(ModelMap model, 
                         YukonUserContext context,
                         @ModelAttribute("command") Theme theme,
                         BindingResult result,
                         FlashScope flash) throws IOException {
        
        validator.validate(theme, result);
        if (result.hasErrors()) {
            return error(result, flash, model, context, theme, PageEditMode.EDIT);
        }
        
        themeDao.saveTheme(theme);
        if (theme.isCurrentTheme()) {
            resourceCache.reloadAll();
        }
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.config.themes.updated", theme.getName()));
        return "redirect:/adminSetup/config/themes/" + theme.getThemeId();
    }
    
    @RequestMapping(value="/config/themes/{id}", method=RequestMethod.DELETE)
    public String delete(ModelMap model, 
                         YukonUserContext context,
                         FlashScope flash,
                         @PathVariable int id) {
        
        themeDao.deleteTheme(id);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.config.themes.deleted"));
        return "redirect:/adminSetup/config/themes";
    }
    
    @RequestMapping(value="/config/themes/{id}/use", method=RequestMethod.GET)
    public String use(ModelMap model, YukonUserContext context, @PathVariable int id) throws IOException {
        
        themeDao.setCurrentTheme(id);
        resourceCache.reloadAll();
        
        return "redirect:/adminSetup/config/themes/" + id;
    }
    
    private void buildModel(ModelMap model, YukonUserContext context, Theme theme, PageEditMode mode) {
        
        List<Theme> themes = themeDao.getThemes();
        for (Theme i : themes) {
            if (theme == null) {
                if (i.isCurrentTheme()) {
                    theme = i;
                    break;
                }
            }
        }
        model.addAttribute("themes", themes);
        model.addAttribute("command", theme);
        model.addAttribute("mode", mode);
        
        final MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        MappedPropertiesHelper<ThemePropertyType> mappedPropertiesHelper = new MappedPropertiesHelper<ThemePropertyType>("properties");
        for (ThemePropertyType property : theme.getProperties().keySet()) {
            mappedPropertiesHelper.add(property.name(), property, property.getInputType());
        }
        Comparator<MappedPropertiesHelper.MappableProperty<ThemePropertyType, ?>> comparator = new Comparator<MappedPropertiesHelper.MappableProperty<ThemePropertyType, ?>>() {
            @Override
            public int compare(MappedPropertiesHelper.MappableProperty<ThemePropertyType, ?> o1, MappedPropertiesHelper.MappableProperty<ThemePropertyType, ?> o2) {
                String o1Text = accessor.getMessage(o1.getExtra());
                String o2Text = accessor.getMessage(o2.getExtra());
                return o1Text.compareToIgnoreCase(o2Text);
            }
        };
        Collections.sort(mappedPropertiesHelper.getMappableProperties(), comparator);
        model.addAttribute("mappedPropertiesHelper", mappedPropertiesHelper);
    }

    private String error(BindingResult result, FlashScope flash, ModelMap model, YukonUserContext context, Theme theme, PageEditMode mode) {
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
        flash.setError(messages);
        buildModel(model, context, theme, mode);
        return "config/themes.jsp";
    }
    
}