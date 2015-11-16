package com.cannontech.web.admin.userGroupEditor;

import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.joda.time.DateTimeZone;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;

import com.cannontech.web.admin.userGroupEditor.RolePropertyController.GroupRolePropertyEditorBean;

public class RolePropertyValidator extends SimpleValidator<GroupRolePropertyEditorBean> {
  
    private final static String baseKey = "yukon.web.modules.adminSetup.config.error.";

    public RolePropertyValidator() {
        super(GroupRolePropertyEditorBean.class);
    }

    @Override
    protected void doValidation(GroupRolePropertyEditorBean groupRolePropertyEditorBean, Errors errors) {
        Map<YukonRoleProperty, Object> rolePropertyValuesMap = groupRolePropertyEditorBean.getValues();

        if (rolePropertyValuesMap.containsKey(YukonRoleProperty.DEFAULT_TIMEZONE)) {
            String timeZoneId = (String) rolePropertyValuesMap.get(YukonRoleProperty.DEFAULT_TIMEZONE);
            YukonValidationUtils.checkExceedsMaxLength(errors, "values[DEFAULT_TIMEZONE]", timeZoneId, 1000);
            if (StringUtils.isNotBlank(timeZoneId)) {
                try {
                    TimeZone timeZone=CtiUtilities.getValidTimeZone(timeZoneId);
                    DateTimeZone.forTimeZone(timeZone);
                } catch (BadConfigurationException e) {
                    errors.rejectValue("values[DEFAULT_TIMEZONE]", baseKey + "invalidTimeZone");
                } catch (IllegalArgumentException e){
                    errors.rejectValue("values[DEFAULT_TIMEZONE]", baseKey + "invalidJodaTimeZone");
                }
            }
           
        }
        if (rolePropertyValuesMap.containsKey(YukonRoleProperty.HOME_URL)) {
            String homeUrl = (String) rolePropertyValuesMap.get(YukonRoleProperty.HOME_URL);
            if (!YukonValidationUtils.isUrlPath(homeUrl)) {
                errors.rejectValue("values[HOME_URL]", baseKey + "invalidURL");
            }
        }
        if (rolePropertyValuesMap.containsKey(YukonRoleProperty.LOG_IN_URL)) {
            String loginUrl = (String) rolePropertyValuesMap.get(YukonRoleProperty.LOG_IN_URL);
            if (!YukonValidationUtils.isUrlPath(loginUrl)) {
                errors.rejectValue("values[LOG_IN_URL]", baseKey + "invalidURL");
            }
        }
        if(rolePropertyValuesMap.containsKey(YukonRoleProperty.DATA_UPDATER_DELAY_MS)){
            Integer dataUpdaterValue = (Integer)rolePropertyValuesMap.get(YukonRoleProperty.DATA_UPDATER_DELAY_MS);
           YukonValidationUtils.checkIsPositiveInt(errors,"values[DATA_UPDATER_DELAY_MS]", dataUpdaterValue);
                
            }
        
       if(rolePropertyValuesMap.containsKey(YukonRoleProperty.SESSION_TIMEOUT)){
           Integer sessionTimeOutValue =(Integer)rolePropertyValuesMap.get(YukonRoleProperty.SESSION_TIMEOUT);
           YukonValidationUtils.checkIsPositiveInt(errors, "values[SESSION_TIMEOUT]", sessionTimeOutValue);
       }
    }
}

