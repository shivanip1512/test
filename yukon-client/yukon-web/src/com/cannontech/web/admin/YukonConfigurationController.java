package com.cannontech.web.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.Pair;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingCategory;
import com.cannontech.system.GlobalSettingSubCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingEditorDao;
import com.cannontech.system.dao.GlobalSettingUpdateDao;
import com.cannontech.system.model.GlobalSetting;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.support.MappedPropertiesHelper;
import com.cannontech.web.support.MappedPropertiesHelper.MappableProperty;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
@RequestMapping("/config/*")
public class YukonConfigurationController {
    
    @Autowired private GlobalSettingEditorDao globalSettingEditorDao;
    @Autowired private GlobalSettingUpdateDao globalSettingUpdateDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
    private LoadingCache<GlobalSettingSubCategory, MappedPropertiesHelper<GlobalSetting>> helperLookup;
    private Map<GlobalSettingSubCategory, String> iconMap;
    
    @PostConstruct
    public void setupHelperLookup() {
        helperLookup = CacheBuilder.newBuilder().concurrencyLevel(1).build(new CacheLoader<GlobalSettingSubCategory, MappedPropertiesHelper<GlobalSetting>>() {
            @Override
            public MappedPropertiesHelper<GlobalSetting> load(GlobalSettingSubCategory category) throws Exception {
                return getHelper(category);
            }
        });
        
        Builder<GlobalSettingSubCategory, String> b = ImmutableMap.builder();
        b.put(GlobalSettingSubCategory.AMI, "meter1");
        b.put(GlobalSettingSubCategory.AUTHENTICATION, "lock");
        b.put(GlobalSettingSubCategory.DR, "lightbulb");
        b.put(GlobalSettingSubCategory.YUKON_SERVICES, "gear");
        b.put(GlobalSettingSubCategory.WEB_SERVER, "world");
        b.put(GlobalSettingSubCategory.BILLING, "money_bag");
        b.put(GlobalSettingSubCategory.CALC_HISTORICAL, "calculator");
        b.put(GlobalSettingSubCategory.GRAPHING, "stats_lines");
        b.put(GlobalSettingSubCategory.MULTISPEAK, "access_point");
        b.put(GlobalSettingSubCategory.VOICE, "phone");
        b.put(GlobalSettingSubCategory.MISC, "folder");
        iconMap = b.build();
    }
    
    private MappedPropertiesHelper<GlobalSetting> getHelper(GlobalSettingSubCategory category) {
        Map<GlobalSettingType, GlobalSetting> settings = globalSettingEditorDao.getSettingsForCategory(category);
        
        MappedPropertiesHelper<GlobalSetting> mappedPropertiesHelper = new MappedPropertiesHelper<GlobalSetting>("values");
        for (GlobalSetting setting : settings.values()) {
            GlobalSettingType type = setting.getType();
            mappedPropertiesHelper.add(type.name(), setting, type.getType());
        }
        MappedPropertiesHelper<GlobalSetting> result = mappedPropertiesHelper;
        return result;
    }
    
    @RequestMapping
    public String view(ModelMap model, final YukonUserContext context) {
        final MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        
        Comparator<Pair<GlobalSettingSubCategory, String>> sorter = new Comparator<Pair<GlobalSettingSubCategory, String>>() {
            @Override
            public int compare(Pair<GlobalSettingSubCategory, String> o1, Pair<GlobalSettingSubCategory, String> o2) {
                if (o1 == o2)
                    return 0;
                
                String localName1 = accessor.getMessage(o1.getFirst());
                String localName2 = accessor.getMessage(o2.getFirst());
                return localName1.compareToIgnoreCase(localName2);
            }
        };
        
        List<Pair<GlobalSettingSubCategory, String>> systemSetup = Lists.newArrayList();
        systemSetup.add(Pair.of(GlobalSettingSubCategory.AMI, iconMap.get(GlobalSettingSubCategory.AMI)));
        systemSetup.add(Pair.of(GlobalSettingSubCategory.AUTHENTICATION, iconMap.get(GlobalSettingSubCategory.AUTHENTICATION)));
        systemSetup.add(Pair.of(GlobalSettingSubCategory.DR, iconMap.get(GlobalSettingSubCategory.DR)));
        systemSetup.add(Pair.of(GlobalSettingSubCategory.YUKON_SERVICES, iconMap.get(GlobalSettingSubCategory.YUKON_SERVICES)));
        systemSetup.add(Pair.of(GlobalSettingSubCategory.WEB_SERVER, iconMap.get(GlobalSettingSubCategory.WEB_SERVER)));
        Collections.sort(systemSetup, sorter);
        
        List<Pair<GlobalSettingSubCategory, String>> application = Lists.newArrayList();
        application.add(Pair.of(GlobalSettingSubCategory.BILLING, iconMap.get(GlobalSettingSubCategory.BILLING)));
        application.add(Pair.of(GlobalSettingSubCategory.CALC_HISTORICAL, iconMap.get(GlobalSettingSubCategory.CALC_HISTORICAL)));
        application.add(Pair.of(GlobalSettingSubCategory.GRAPHING, iconMap.get(GlobalSettingSubCategory.GRAPHING)));
        Collections.sort(application, sorter);
        
        List<Pair<GlobalSettingSubCategory, String>> integration = Lists.newArrayList();
        integration.add(Pair.of(GlobalSettingSubCategory.MULTISPEAK, iconMap.get(GlobalSettingSubCategory.MULTISPEAK)));
        integration.add(Pair.of(GlobalSettingSubCategory.VOICE, iconMap.get(GlobalSettingSubCategory.VOICE)));
        Collections.sort(integration, sorter);
        
        List<Pair<GlobalSettingSubCategory, String>> other = Lists.newArrayList();
        other.add(Pair.of(GlobalSettingSubCategory.MISC, iconMap.get(GlobalSettingSubCategory.MISC)));
        
        List<Pair<GlobalSettingCategory, List<Pair<GlobalSettingSubCategory, String>>>> categories = Lists.newArrayList();
        categories.add(Pair.of(GlobalSettingCategory.SYSTEM_SETUP, systemSetup));
        categories.add(Pair.of(GlobalSettingCategory.APPLICATION, application));
        categories.add(Pair.of(GlobalSettingCategory.INTEGRATION, integration));
        categories.add(Pair.of(GlobalSettingCategory.OTHER, other));
        
        model.addAttribute("categories", categories);
        
        return "config/view.jsp";
    }
    
    @RequestMapping
    public String edit(ModelMap model, YukonUserContext context, GlobalSettingSubCategory category) throws ExecutionException {
        model.addAttribute("category", category);
        
        Map<GlobalSettingType, Pair<Object, String>> settings = globalSettingEditorDao.getValuesAndCommentsForCategory(category);
        
        GlobalSettingsEditorBean command = new GlobalSettingsEditorBean();
        command.setCategory(category);
        command.setValues(Maps.transformValues(settings, new Function<Pair<Object, String>, Object>() {
            @Override
            public Object apply(Pair<Object, String> input) {
                return input.getFirst();
            }
        }));
        command.setComments(Maps.transformValues(settings, new Function<Pair<Object, String>, String>() {
            @Override
            public String apply(Pair<Object, String> input) {
                return input.getSecond();
            }
        }));
        
        model.addAttribute("command", command);
        
        setupModelMap(context, model, category);
        
        return "config/category.jsp";
    }
    
    private void setupModelMap(YukonUserContext context, ModelMap model, GlobalSettingSubCategory category) throws ExecutionException {
        final MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        MappedPropertiesHelper<GlobalSetting> mappedPropertiesHelper = helperLookup.get(category);
        
        Comparator<MappedPropertiesHelper.MappableProperty<GlobalSetting, ?>> comparator = new Comparator<MappedPropertiesHelper.MappableProperty<GlobalSetting, ?>>() {
            
            @Override
            public int compare(MappedPropertiesHelper.MappableProperty<GlobalSetting, ?> o1, MappedPropertiesHelper.MappableProperty<GlobalSetting, ?> o2) {
                String o1Text = accessor.getMessage(o1.getExtra().getType().getFormatKey());
                String o2Text = accessor.getMessage(o2.getExtra().getType().getFormatKey());
                return o1Text.compareToIgnoreCase(o2Text);
            }
        };
        
        Collections.sort(mappedPropertiesHelper.getMappableProperties(), comparator);
        
        model.addAttribute("mappedPropertiesHelper", mappedPropertiesHelper);
        model.addAttribute("category", category);
        model.addAttribute("category_icon", iconMap.get(category));
    }
    
    @RequestMapping(value="update", method=RequestMethod.POST, params="save")
    public String save(HttpServletRequest request, 
                       @ModelAttribute("command")GlobalSettingsEditorBean command, BindingResult result, 
                       YukonUserContext context, 
                       ModelMap map, 
                       FlashScope flashScope) throws Exception {
        
        GlobalSettingSubCategory category = command.getCategory();
        if (result.hasErrors()) {
            setupModelMap(context, map, category);

            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "config/category.jsp";
        }
        
        
        List<GlobalSetting> settings = Lists.newArrayList(adjustSettings(command));
        globalSettingUpdateDao.updateSettings(settings, context.getYukonUser());
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        String categoryName = accessor.getMessage(category);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.config.updateSuccessful", categoryName));
        setupModelMap(context, map, category);
        return "redirect:/adminSetup/config/view";
    }
    
    private List<GlobalSetting> adjustSettings(final GlobalSettingsEditorBean command ) throws ExecutionException {
        MappedPropertiesHelper<GlobalSetting> helper = helperLookup.get(command.getCategory());
        List<GlobalSetting> settings = Lists.transform(helper.getMappableProperties(), new Function<MappableProperty<GlobalSetting, ?>, GlobalSetting>() {
            @Override
            public GlobalSetting apply(MappableProperty<GlobalSetting, ?> input) {
                GlobalSetting setting = input.getExtra();
                setting.setValue(command.getValues().get(setting.getType()));
                setting.setComments(command.getComments().get(setting.getType()));
                return setting;
            }
        });
        
        return settings;
    }

    public static class GlobalSettingsEditorBean {
        
        private GlobalSettingSubCategory category;
        
        private Map<GlobalSettingType, Object> values = Maps.newLinkedHashMap();
        private Map<GlobalSettingType, String> comments = Maps.newLinkedHashMap();
        
        public Map<GlobalSettingType, Object> getValues() {
            return values;
        }
        
        public void setValues(Map<GlobalSettingType, Object> values) {
            this.values = values;
        }
        
        public Map<GlobalSettingType, String> getComments() {
            return comments;
        }
        
        public void setComments(Map<GlobalSettingType, String> comments) {
            this.comments = comments;
        }
        
        public GlobalSettingSubCategory getCategory() {
            return category;
        }
        
        public void setCategory(GlobalSettingSubCategory category) {
            this.category = category;
        }
    }
    
    @InitBinder
    public void initialize(WebDataBinder webDataBinder, GlobalSettingSubCategory category) throws ExecutionException {
        EnumPropertyEditor.register(webDataBinder, GlobalSettingType.class);
        
        MappedPropertiesHelper<GlobalSetting> helper = helperLookup.get(category);
        helper.register(webDataBinder);
    }
    
}