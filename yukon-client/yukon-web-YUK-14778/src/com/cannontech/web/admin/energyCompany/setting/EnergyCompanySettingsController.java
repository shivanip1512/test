package com.cannontech.web.admin.energyCompany.setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.map.LazyMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompanySetting;
import com.cannontech.stars.energyCompany.model.SettingCategory;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.support.MappedPropertiesHelper;

@Controller
@RequestMapping("/energyCompany/settings/*")
public class EnergyCompanySettingsController {
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnergyCompanyService energyCompanyService;

    private MappedPropertiesHelper<EnergyCompanySettingType> mappedPropertiesHelper;

    private final Validator settingsValidator =
            new SimpleValidator<SettingsBean>(SettingsBean.class) {
                @Override
                public void doValidation(SettingsBean settingsBean, Errors errors) {
                    
                    EnergyCompanySetting currentSetting;
                    for (EnergyCompanySetting newSetting : settingsBean.getSettings().values()) {
                        // Comments have to be 1000 chars or less

                        YukonValidationUtils.checkExceedsMaxLength(errors, "settings["+newSetting.getType()+"].comments" , newSetting.getComments(), 1000);
                        
                        // validate the correct ecId for each setting
                        if (newSetting.getEnergyCompanyId() != null) {
                            errors.rejectValue("settings["+newSetting.getType()+"].value", "yukon.web.modules.adminSetup.energyCompanySettings.wrongEcId");
                        }
                        
                        currentSetting = ecSettingDao.getSetting(newSetting.getType(), settingsBean.getEcId());

                        if (!newSetting.isEnabled() && !ObjectUtils.equals(currentSetting.getValue(), newSetting.getValue())) {
                            errors.rejectValue("settings["+newSetting.getType()+"].value", "yukon.web.modules.adminSetup.energyCompanySettings.disabledInput");
                        }else{
                            switch (newSetting.getType()) {
                            case ADMIN_EMAIL_ADDRESS:
                                if (StringUtils.isNotBlank((String)newSetting.getValue()) && !com.cannontech.util.Validator.isEmailAddress((String)newSetting.getValue())) {
                                    errors.rejectValue("settings["+newSetting.getType()+"].value", "yukon.web.modules.adminSetup.energyCompanySettings.email.invalid");
                                }
                                break;
                            case BROADCAST_OPT_OUT_CANCEL_SPID:
                            	YukonValidationUtils.checkRange(errors, "settings["+newSetting.getType()+"].value", (Integer)newSetting.getValue(), 1, 65534, true);
                                break;
                            case ENERGY_COMPANY_DEFAULT_TIME_ZONE:
                                if (StringUtils.isNotBlank((String)newSetting.getValue())){
                                    try{
                                        TimeZone timeZone=CtiUtilities.getValidTimeZone((String)newSetting.getValue());
                                        DateTimeZone.forTimeZone(timeZone);
                                    }
                                    catch (BadConfigurationException e) {
                                        errors.rejectValue("settings["+newSetting.getType()+"].value", "yukon.web.modules.adminSetup.energyCompanySettings.timezone.invalid");
                                    }catch (IllegalArgumentException e){
                                        errors.rejectValue("settings["+newSetting.getType()+"].value", "yukon.web.modules.adminSetup.energyCompanySettings.timezone.invalidJodaTimeZone");
                                    }
                                }
                                break;
                            default:
                                break;
        
                            }
                        }
                    }
                }
            };
            
    @RequestMapping("view")
    public String view(YukonUserContext context, ModelMap model, int ecId, EnergyCompanyInfoFragment ecInfoFragment) {
        if (!energyCompanyService.canEditEnergyCompany(context.getYukonUser(), ecId)) {
            throw new NotAuthorizedException("User " + context.getYukonUser().getUsername() + " is not authorized to edit energy company with id " + ecId);
        }
        
        Map<EnergyCompanySettingType,EnergyCompanySetting> settings = new HashMap<>();

        EnergyCompanySetting setting = null;
        for (EnergyCompanySettingType type : EnergyCompanySettingType.values()) {
            setting = ecSettingDao.getSetting(type,ecId);
            settings.put(type,setting);
        }
        SettingsBean settingsBean = new SettingsBean();
        settingsBean.setSettings(settings);
        settingsBean.setEcId(ecId);
        
        return energyCompanySettingsView(model, ecId, ecInfoFragment, settingsBean);
    }

    @RequestMapping(value="save", method=RequestMethod.POST)
    public String save(@ModelAttribute("settingsBean") SettingsBean settingsBean, BindingResult bindingResult,
            YukonUserContext userContext, EnergyCompanyInfoFragment ecInfoFragment, ModelMap model,
            FlashScope flashScope) throws Exception {
        int ecId = settingsBean.getEcId();

        if (!energyCompanyService.canEditEnergyCompany(userContext.getYukonUser(), ecId)) {
            throw new NotAuthorizedException("User " + userContext.getYukonUser().getUsername()
                + " is not authorized to edit energy company with id " + ecId);
        }

        settingsValidator.validate(settingsBean, bindingResult);

        model.addAttribute("ecId", ecId);
        model.addAttribute("mappedPropertiesHelper", mappedPropertiesHelper);
        model.addAttribute("settingsBean", settingsBean);
        
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return energyCompanySettingsView(model, ecId, ecInfoFragment, settingsBean);
        }

        EnergyCompanySetting setting;
        for (EnergyCompanySetting changedSetting : settingsBean.getSettings().values()) {
            setting = ecSettingDao.getSetting(changedSetting.getType(), ecId);
            setting.setComments(changedSetting.getComments());
            setting.setValue(changedSetting.getValue());
            setting.setEnabled(changedSetting.isEnabled());
            ecSettingDao.updateSetting(setting, userContext.getYukonUser(), ecId);
        }

        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.energyCompanySettings.updateSuccessful"));
        return "redirect:view";
    }

    private String energyCompanySettingsView(ModelMap model, int ecId, EnergyCompanyInfoFragment ecInfoFragment,
            SettingsBean settingsBean) {
        model.addAttribute("ecId", ecId);

        model.addAttribute("settingsBean", settingsBean);
        model.addAttribute("mappedPropertiesHelper", mappedPropertiesHelper);
        model.addAttribute("energyCompanyName", ecDao.getEnergyCompany(ecId).getName());
        model.addAttribute("categories", SettingCategory.values());
        model.addAttribute("energyCompanyInfoFragment", ecInfoFragment);

        return "energyCompany/settings/view.jsp";
    }
    
    public static class SettingsBean {
        private int ecId;
        private Map<EnergyCompanySettingType, EnergyCompanySetting> settings 
            = LazyMap.lazyMap(new HashMap<EnergyCompanySettingType, EnergyCompanySetting>(),
                FactoryUtils.instantiateFactory(EnergyCompanySetting.class));

        public Map<EnergyCompanySettingType,EnergyCompanySetting> getSettings() {
            return settings;
        }

        public void setSettings(Map<EnergyCompanySettingType,EnergyCompanySetting> settings) {
            this.settings = settings;
        }

        public int getEcId() {
            return ecId;
        }

        public void setEcId(int ecId) {
            this.ecId = ecId;
        }
    }

    @InitBinder
    public void initialize(WebDataBinder webDataBinder) throws ExecutionException {
        EnumPropertyEditor.register(webDataBinder, EnergyCompanySettingType.class);

        mappedPropertiesHelper = new MappedPropertiesHelper<EnergyCompanySettingType>("settings");
        for (EnergyCompanySettingType type : EnergyCompanySettingType.values()) {
            mappedPropertiesHelper.add(type.name(), "value", type, type.getType());
        }
        mappedPropertiesHelper.register(webDataBinder);
    }
}
