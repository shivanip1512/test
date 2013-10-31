package com.cannontech.web.admin.userGroupEditor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.DescriptiveRoleProperty;
import com.cannontech.core.roleproperties.GroupRolePropertyValueCollection;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyEditorDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.support.MappedPropertiesHelper;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

@Controller("/roleEditor/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class RolePropertyController {

    private RolePropertyEditorDao rolePropertyEditorDao;
    private YukonGroupDao yukonGroupDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private Map<YukonRole, MappedPropertiesHelper<DescriptiveRoleProperty>> helperLookup;
    
    @PostConstruct
    public void setupHelperLookup() {
        helperLookup = new MapMaker().concurrencyLevel(1).makeComputingMap(new Function<YukonRole, MappedPropertiesHelper<DescriptiveRoleProperty>>() {
            @Override
            public MappedPropertiesHelper<DescriptiveRoleProperty> apply(YukonRole from) {
                return getHelper(from);
            }
        });
    }
    
    @RequestMapping(method=RequestMethod.GET, value="view")
    public String view(YukonUserContext context, ModelMap map, FlashScope flashScope, int roleGroupId, int roleId) throws Exception {
        LiteYukonGroup liteYukonGroup = yukonGroupDao.getLiteYukonGroup(roleGroupId);
        YukonRole role = YukonRole.getForId(roleId);
        
        GroupRolePropertyValueCollection propertyValues = rolePropertyEditorDao.getForGroupAndRole(liteYukonGroup, role, true);
        
        Map<YukonRoleProperty, Object> valueMap = propertyValues.getValueMap();
        
        GroupRolePropertyEditorBean command = new GroupRolePropertyEditorBean();
        command.setValues(valueMap);
        
        map.addAttribute("command", command);
        
        setupModelMap(context, map, liteYukonGroup, role);
        return "userGroupEditor/roles.jsp";
    }

    private void setupModelMap(YukonUserContext context, ModelMap map, LiteYukonGroup liteYukonGroup, YukonRole role) {
        final MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        MappedPropertiesHelper<DescriptiveRoleProperty> mappedPropertiesHelper = helperLookup.get(role);
        
        Comparator<MappedPropertiesHelper.MappableProperty<DescriptiveRoleProperty, ?>> comparator = new Comparator<MappedPropertiesHelper.MappableProperty<DescriptiveRoleProperty, ?>>() {
            
            @Override
            public int compare(MappedPropertiesHelper.MappableProperty<DescriptiveRoleProperty, ?> o1, MappedPropertiesHelper.MappableProperty<DescriptiveRoleProperty, ?> o2) {
                String o1Text = messageSourceAccessor.getMessage(o1.getExtra().getKey());
                String o2Text = messageSourceAccessor.getMessage(o2.getExtra().getKey());
                return o1Text.compareToIgnoreCase(o2Text);
            }
        };
        
        Collections.sort(mappedPropertiesHelper.getMappableProperties(), comparator);
        
        map.addAttribute("mappedPropertiesHelper", mappedPropertiesHelper);
        map.addAttribute("roleGroupName", liteYukonGroup.getGroupName());
        map.addAttribute("roleName", messageSourceAccessor.getMessage(role.getFormatKey()));
        map.addAttribute("roleId", role.getRoleId());
        map.addAttribute("roleGroupId", liteYukonGroup.getGroupID());
        map.addAttribute("showDelete", role.getCategory().isSystem() ? false : true);
    }

    private MappedPropertiesHelper<DescriptiveRoleProperty> getHelper(YukonRole yukonRole) {
        Map<YukonRoleProperty, DescriptiveRoleProperty> descriptiveRoleProperties = rolePropertyEditorDao.getDescriptiveRoleProperties(yukonRole);
        
        MappedPropertiesHelper<DescriptiveRoleProperty> mappedPropertiesHelper = new MappedPropertiesHelper<DescriptiveRoleProperty>("values");
        for (DescriptiveRoleProperty descriptiveRoleProperty : descriptiveRoleProperties.values()) {
            YukonRoleProperty yukonRoleProperty = descriptiveRoleProperty.getYukonRoleProperty();
            mappedPropertiesHelper.add(yukonRoleProperty.name(), descriptiveRoleProperty, yukonRoleProperty.getType());
        }
        MappedPropertiesHelper<DescriptiveRoleProperty> result = mappedPropertiesHelper;
        return result;
    }    

    @RequestMapping(value="update", method=RequestMethod.POST, params="save")
    public String save(HttpServletRequest request, @ModelAttribute("command")GroupRolePropertyEditorBean command, BindingResult result, 
                       YukonUserContext context, ModelMap map, FlashScope flashScope, int roleGroupId, int roleId) throws Exception {

        LiteYukonGroup liteYukonGroup = yukonGroupDao.getLiteYukonGroup(roleGroupId);
        YukonRole role = YukonRole.getForId(roleId);
        if (result.hasErrors()) {
            setupModelMap(context, map, liteYukonGroup, role);

            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "userGroupEditor/roles.jsp";
        }
        
        GroupRolePropertyValueCollection propertyValues = rolePropertyEditorDao.getForGroupAndRole(liteYukonGroup, role, true);
        
        propertyValues.putAll(command.getValues());

        rolePropertyEditorDao.save(propertyValues);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.roles.loginGroupRoleUpdated"));
        setupModelMap(context, map, liteYukonGroup, role);
        return "redirect:/adminSetup/roleGroup/view";
    }
    
    @RequestMapping(value="update", method=RequestMethod.POST, params="delete")
    public String delete(ModelMap map, FlashScope flash, int roleGroupId, int roleId) {
        rolePropertyEditorDao.removeRoleFromGroup(roleGroupId, roleId);
        map.addAttribute("roleGroupId", roleGroupId);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.roleGroupEditor.updateSuccessful"));
        return "redirect:/adminSetup/roleGroup/view";
    }
    
    @RequestMapping(value="update", method=RequestMethod.POST, params="cancel")
    public String cancel(ModelMap map, FlashScope flash, int roleGroupId, int roleId) {
        map.addAttribute("roleGroupId", roleGroupId);
        return "redirect:/adminSetup/roleGroup/view";
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
    public void initialize(WebDataBinder webDataBinder, int roleId) {
        EnumPropertyEditor.register(webDataBinder, YukonRoleProperty.class);
        
        MappedPropertiesHelper<DescriptiveRoleProperty> helper = helperLookup.get(YukonRole.getForId(roleId));
        helper.register(webDataBinder);
    }
    
    @Autowired
    public void setRolePropertyEditorDao(RolePropertyEditorDao rolePropertyEditorDao) {
        this.rolePropertyEditorDao = rolePropertyEditorDao;
    }

    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}