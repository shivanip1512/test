package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.dr.setup.LoadGroupRipple;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;

@Service
public class LoadGroupRippleValidator extends LoadGroupSetupValidator<LoadGroupRipple> {

    @Autowired private LMApiValidatorHelper lmApiValidatorHelper;
    @Autowired private RolePropertyDao rolePropertyDao;
    private final static String zeroOnePattern = "^[01]+$";
    public static final Integer SHOW_SPECIAL_RIPPLE = 0x10000000;

    public LoadGroupRippleValidator() {
        super(LoadGroupRipple.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupRipple.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupRipple loadGroup, Errors errors) {

        LiteYukonUser user = ApiRequestContext.getContext().getLiteYukonUser();
        long specialRipple = Long.parseLong(rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DATABASE_EDITOR_OPTIONAL_PRODUCT_DEV, user), 16);
        // Route ID is mandatory for Ripple Load Group and should exists
        lmApiValidatorHelper.validateRoute(errors, loadGroup.getRouteId());

        YukonApiValidationUtils.checkIfFieldRequired("shedTime", errors, loadGroup.getShedTime(), "Shed Time");

        YukonApiValidationUtils.checkIfFieldRequired("control", errors, loadGroup.getControl(), "Control");

        YukonApiValidationUtils.checkIfFieldRequired("restore", errors, loadGroup.getRestore(), "Restore");

        if (!errors.hasFieldErrors("shedTime")) {
            TimeIntervals shedTime = TimeIntervals.fromSeconds(loadGroup.getShedTime());
            if (!TimeIntervals.getRippleShedtime().contains(shedTime)) {
                errors.rejectValue("shedTime", ApiErrorDetails.INVALID_VALUE.getCodeString());
            }
        }

        if (!errors.hasFieldErrors("control")) {
            if (!loadGroup.getControl().matches(zeroOnePattern)) {
                errors.rejectValue("control", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "Binary value" }, "");
            }
        }

        if (!errors.hasFieldErrors("restore")) {
            if (!loadGroup.getRestore().matches(zeroOnePattern)) {
                errors.rejectValue("restore", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "Binary value" }, "");
            }
        }

        if (!errors.hasFieldErrors("control")) {

            if ((specialRipple & SHOW_SPECIAL_RIPPLE) != 0) {
                YukonApiValidationUtils.checkExactLength("control", errors, loadGroup.getControl(), "Control", 34);
            } else {
                YukonApiValidationUtils.checkExactLength("control", errors, loadGroup.getControl(), "Control", 50);
            }
        }

        if (!errors.hasFieldErrors("restore")) {

            if ((specialRipple & SHOW_SPECIAL_RIPPLE) != 0) {
                YukonApiValidationUtils.checkExactLength("restore", errors, loadGroup.getRestore(), "Restore", 34);
            } else {
                YukonApiValidationUtils.checkExactLength("restore", errors, loadGroup.getRestore(), "Restore", 50);
            }
        }

        if ((specialRipple & SHOW_SPECIAL_RIPPLE) != 0) {
            YukonApiValidationUtils.checkIfFieldRequired("group", errors, loadGroup.getGroup(), "Group");
            YukonApiValidationUtils.checkIfFieldRequired("areaCode", errors, loadGroup.getAreaCode(), "Area Code");
        } else if (loadGroup.getGroup() != null || loadGroup.getAreaCode() != null) {
            errors.reject(ApiErrorDetails.BAD_REQUEST.getCodeString());
        }
    }
}
