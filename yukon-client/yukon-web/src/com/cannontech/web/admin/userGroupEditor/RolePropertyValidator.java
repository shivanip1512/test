package com.cannontech.web.admin.userGroupEditor;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
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
                    CtiUtilities.getValidTimeZone(timeZoneId);
                } catch (BadConfigurationException e) {
                    errors.rejectValue("values[DEFAULT_TIMEZONE]", baseKey + "invalidTimeZone");
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
    }
}