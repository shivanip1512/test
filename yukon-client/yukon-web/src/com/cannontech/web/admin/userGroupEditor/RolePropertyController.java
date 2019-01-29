package com.cannontech.web.admin.userGroupEditor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.DescriptiveRoleProperty;
import com.cannontech.core.roleproperties.GroupRolePropertyValueCollection;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyEditorDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.support.MappedPropertiesHelper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class RolePropertyController {
    
    @Autowired private RolePropertyEditorDao rpEditorDao;
    @Autowired private YukonGroupDao roleGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private UsersEventLogService usersEventLogService;
    
    private static final String key = "yukon.web.modules.adminSetup.auth.role.";
    private RolePropertyValidator validator = new RolePropertyValidator();
    private LoadingCache<YukonRole, MappedPropertiesHelper<DescriptiveRoleProperty>> helperLookup;
    
    @PostConstruct
    public void init() {
        helperLookup = CacheBuilder.newBuilder().concurrencyLevel(1)
        .build(new CacheLoader<YukonRole, MappedPropertiesHelper<DescriptiveRoleProperty>>() {
            @Override
            public MappedPropertiesHelper<DescriptiveRoleProperty> load(YukonRole from) {
                return getHelper(from);
            }
        });
    }
    
    /* VIEW */
    @RequestMapping("role-groups/{roleGroupId}/roles/{roleId}")
    public String view(YukonUserContext context, ModelMap model, 
            @PathVariable int roleGroupId, @PathVariable int roleId) {
        
        LiteYukonGroup roleGroup = roleGroupDao.getLiteYukonGroup(roleGroupId);
        YukonRole role = YukonRole.getForId(roleId);
        
        GroupRolePropertyValueCollection propertyValues = rpEditorDao.getForGroupAndRole(roleGroup, role, true);
        
        Map<YukonRoleProperty, Object> valueMap = propertyValues.getValueMap();
        
        GroupRolePropertyEditorBean command = new GroupRolePropertyEditorBean();
        command.setValues(valueMap);
        
        model.addAttribute("command", command);
        setupModelMap(context, model, roleGroup, role);
        
        return "userGroupEditor/roles.jsp";
    }
    
    private void setupModelMap(YukonUserContext context, ModelMap model, LiteYukonGroup roleGroup, YukonRole role) {
        
        final MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(context);
        MappedPropertiesHelper<DescriptiveRoleProperty> mappedPropertiesHelper = helperLookup.getUnchecked(role);
        
        Comparator<MappedPropertiesHelper.MappableProperty<DescriptiveRoleProperty, ?>> comparator = 
                new Comparator<MappedPropertiesHelper.MappableProperty<DescriptiveRoleProperty, ?>>() {
            @Override
            public int compare(MappedPropertiesHelper.MappableProperty<DescriptiveRoleProperty, ?> o1, 
                    MappedPropertiesHelper.MappableProperty<DescriptiveRoleProperty, ?> o2) {
                String o1Text = accessor.getMessage(o1.getExtra().getKey());
                String o2Text = accessor.getMessage(o2.getExtra().getKey());
                return o1Text.compareToIgnoreCase(o2Text);
            }
        };
        
        Collections.sort(mappedPropertiesHelper.getMappableProperties(), comparator);
        
        model.addAttribute("mappedPropertiesHelper", mappedPropertiesHelper);
        model.addAttribute("roleGroupName", roleGroup.getGroupName());
        model.addAttribute("roleName", accessor.getMessage(role.getFormatKey()));
        model.addAttribute("roleId", role.getRoleId());
        model.addAttribute("roleGroupId", roleGroup.getGroupID());
        model.addAttribute("showDelete", role.getCategory().isSystem() ? false : true);
    }
    
    private MappedPropertiesHelper<DescriptiveRoleProperty> getHelper(YukonRole role) {
        
        Map<YukonRoleProperty, DescriptiveRoleProperty> descriptiveRoleProperties = 
                rpEditorDao.getDescriptiveRoleProperties(role);
        
        MappedPropertiesHelper<DescriptiveRoleProperty> mappedPropertiesHelper = 
                new MappedPropertiesHelper<DescriptiveRoleProperty>("values");
        for (DescriptiveRoleProperty descriptiveRoleProperty : descriptiveRoleProperties.values()) {
            YukonRoleProperty yukonRoleProperty = descriptiveRoleProperty.getYukonRoleProperty();
            mappedPropertiesHelper.add(yukonRoleProperty.name(), descriptiveRoleProperty, yukonRoleProperty.getType());
        }
        MappedPropertiesHelper<DescriptiveRoleProperty> result = mappedPropertiesHelper;
        
        return result;
    }
    
    /* UPDATE */
    @RequestMapping(value="role-groups/{roleGroupId}/roles/{roleId}", method=RequestMethod.POST, params="save")
    public String save(@ModelAttribute("command") GroupRolePropertyEditorBean command, BindingResult result, 
                       YukonUserContext context, ModelMap model, FlashScope flash, 
                       @PathVariable int roleGroupId, @PathVariable int roleId) {
        
        LiteYukonGroup liteYukonGroup = roleGroupDao.getLiteYukonGroup(roleGroupId);
        YukonRole role = YukonRole.getForId(roleId);
        validator.doValidation(command, result);
        if (result.hasErrors()) {
            setupModelMap(context, model, liteYukonGroup, role);

            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            return "userGroupEditor/roles.jsp";
        }
        
        GroupRolePropertyValueCollection propertyValues = rpEditorDao.getForGroupAndRole(liteYukonGroup, role, true);
        GroupRolePropertyValueCollection beforeUpdatePropertyValues = rpEditorDao.getForGroupAndRole(liteYukonGroup, role, true);
        
        propertyValues.putAll(command.getValues());
        
        rpEditorDao.save(propertyValues);

        beforeUpdatePropertyValues.getValueMap().entrySet().stream().forEach(e -> {
            String oldValue = "Blank";
            String newValue = "Blank";
            if (propertyValues.getValueMap().get(e.getKey()) != null && e.getValue() != null) {
                oldValue = e.getValue().toString();
                newValue = propertyValues.getValueMap().get(e.getKey()).toString();
            } else if (propertyValues.getValueMap().get(e.getKey()) == null && e.getValue() != null) {
                oldValue = e.getValue().toString();
            } else if (propertyValues.getValueMap().get(e.getKey()) != null && e.getValue() == null) {
                newValue = propertyValues.getValueMap().get(e.getKey()).toString();
            }
            if (!oldValue.equals(newValue)) {
                usersEventLogService.rolePropertyUpdated(liteYukonGroup.getGroupName(), role, e.getKey(), oldValue,
                    newValue, context.getYukonUser());
            }
        });
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(context);
        String roleName = accessor.getMessage(role);
        flash.setConfirm(new YukonMessageSourceResolvable(key + "update.success", roleName));
        
        return "redirect:/admin/role-groups/" + roleGroupId;
    }
    
    /* DELETE */
    @RequestMapping(value="role-groups/{roleGroupId}/roles/{roleId}", method=RequestMethod.POST, params="delete")
    public String delete(ModelMap model, FlashScope flash, 
            @PathVariable int roleGroupId, @PathVariable int roleId, LiteYukonUser user) {
        LiteYukonGroup liteYukonGroup = roleGroupDao.getLiteYukonGroup(roleGroupId);
        YukonRole role = YukonRole.getForId(roleId);
        rpEditorDao.removeRoleFromGroup(roleGroupId, roleId);
        usersEventLogService.roleRemoved(liteYukonGroup.getGroupName(), role, user);
        flash.setConfirm(new YukonMessageSourceResolvable(key + "group.updateSuccessful"));
        
        return "redirect:/admin/role-groups/" + roleGroupId;
    }
    
    public static class GroupRolePropertyEditorBean {
        
        private Map<YukonRoleProperty, Object> values = Maps.newLinkedHashMap();
        
        public Map<YukonRoleProperty, Object> getValues() {
            return values;
        }
        
        public void setValues(Map<YukonRoleProperty, Object> values) {
            this.values = values;
        }
    }
    
    @InitBinder
    public void initialize(WebDataBinder binder, @PathVariable int roleId) {
        
        EnumPropertyEditor.register(binder, YukonRoleProperty.class);
        MappedPropertiesHelper<DescriptiveRoleProperty> helper = helperLookup.getUnchecked(YukonRole.getForId(roleId));
        helper.register(binder);
    }
    
}