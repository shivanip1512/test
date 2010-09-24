package com.cannontech.web.support;

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

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.roleproperties.DescriptiveRoleProperty;
import com.cannontech.core.roleproperties.GroupRolePropertyValueCollection;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyEditorDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.input.type.InputType;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.csrf.CsrfTokenService;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/setup/*")
@CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
public class SetupController {

    private RolePropertyEditorDao rolePropertyEditorDao;
    private RoleDao roleDao;
    private CsrfTokenService csrfTokenService;
    
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
    public String view(HttpServletRequest request, ModelMap map, FlashScope flashScope) throws Exception {

        //Load default system role properties
        LiteYukonGroup liteYukonGroup = roleDao.getGroup( YukonGroupRoleDefs.GRP_YUKON );
        YukonRole role = YukonRole.SYSTEM;
        GroupRolePropertyValueCollection propertyValues = rolePropertyEditorDao.getForGroupAndRole(liteYukonGroup, role, true);
        
        Map<YukonRoleProperty, Object> valueMap = propertyValues.getValueMap();
        
        GroupRolePropertyEditorBean command = new GroupRolePropertyEditorBean();
        command.setValues(valueMap);
        
        map.addAttribute("command", command);
        
        setupModelMap(map, liteYukonGroup, role);
        return "setup.jsp";
    }

    private void setupModelMap(ModelMap map, LiteYukonGroup liteYukonGroup, YukonRole role) {
        MappedPropertiesHelper<DescriptiveRoleProperty> mappedPropertiesHelper = helperLookup.get(role);
        map.addAttribute("mappedPropertiesHelper", mappedPropertiesHelper);
        
        map.addAttribute("role", role);
        map.addAttribute("group", liteYukonGroup);
    }

    private MappedPropertiesHelper<DescriptiveRoleProperty> getHelper(YukonRole yukonRole) {
        Map<YukonRoleProperty, DescriptiveRoleProperty> descriptiveRoleProperties = rolePropertyEditorDao.getDescriptiveRoleProperties(yukonRole);
        
        Function<DescriptiveRoleProperty, InputType<?>> function1 = new Function<DescriptiveRoleProperty, InputType<?>>() {
            public InputType<?> apply(DescriptiveRoleProperty from) {
                return from.getYukonRoleProperty().getType();
            }
        };
        Function<DescriptiveRoleProperty, String> function2 = new Function<DescriptiveRoleProperty, String>() {
            public String apply(DescriptiveRoleProperty from) {
                return from.getYukonRoleProperty().name();
            }
        };
        MappedPropertiesHelper<DescriptiveRoleProperty> mappedPropertiesHelper1 = new MappedPropertiesHelper<DescriptiveRoleProperty>("values");
        for (DescriptiveRoleProperty t : descriptiveRoleProperties.values()) {
            mappedPropertiesHelper1.add(function2.apply(t), t, function1.apply(t));
        }
        MappedPropertiesHelper<DescriptiveRoleProperty> mappedPropertiesHelper = mappedPropertiesHelper1;
        return mappedPropertiesHelper;
    }    

    @RequestMapping(method=RequestMethod.POST, value="save")
    public String save(HttpServletRequest request, @ModelAttribute("command")GroupRolePropertyEditorBean command, BindingResult result, ModelMap map, FlashScope flashScope) throws Exception {
        csrfTokenService.checkRequest(request, result);
        LiteYukonGroup liteYukonGroup = roleDao.getGroup( YukonGroupRoleDefs.GRP_YUKON );
        YukonRole role = YukonRole.SYSTEM;
        if (result.hasErrors()) {
            setupModelMap(map, liteYukonGroup, role);

            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "setup.jsp";
        }
        
        GroupRolePropertyValueCollection propertyValues = rolePropertyEditorDao.getForGroupAndRole(liteYukonGroup, role, true);
        
        propertyValues.putAll(command.getValues());

        rolePropertyEditorDao.save(propertyValues);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.support.setup.loginGroupRoleUpdated"));
        return "redirect:view";
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
    public void initialize(WebDataBinder webDataBinder) {
        EnumPropertyEditor.register(webDataBinder, YukonRoleProperty.class);
        
        MappedPropertiesHelper<DescriptiveRoleProperty> helper = helperLookup.get(YukonRole.SYSTEM);
        helper.register(webDataBinder);
    }
    
    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
    
    @Autowired
    public void setRolePropertyEditorDao(RolePropertyEditorDao rolePropertyEditorDao) {
        this.rolePropertyEditorDao = rolePropertyEditorDao;
    }
    
    @Autowired
    public void setCsrfTokenService(CsrfTokenService csrfTokenService) {
        this.csrfTokenService = csrfTokenService;
    }
}