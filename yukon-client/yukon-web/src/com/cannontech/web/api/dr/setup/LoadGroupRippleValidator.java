package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.dr.setup.LoadGroupRipple;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;

@Service
public class LoadGroupRippleValidator extends LoadGroupSetupValidator<LoadGroupRipple> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private RolePropertyDao rolePropertyDao;
    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";
    private final static String invalidKey = "yukon.web.modules.dr.setup.error.invalid";
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
        lmValidatorHelper.validateRoute(errors, loadGroup.getRouteId());

        lmValidatorHelper.checkIfFieldRequired("shedTime", errors, loadGroup.getShedTime(), "Shed Time");

        lmValidatorHelper.checkIfFieldRequired("control", errors, loadGroup.getControl(), "Control");

        lmValidatorHelper.checkIfFieldRequired("restore", errors, loadGroup.getRestore(), "Restore");

        if (!errors.hasFieldErrors("shedTime")) {
            TimeIntervals shedTime = TimeIntervals.fromSeconds(loadGroup.getShedTime());
            if (!TimeIntervals.getRippleShedtime().contains(shedTime)) {
                errors.rejectValue("shedTime", invalidKey, new Object[] { "Shed Time" }, "");
            }
        }

        if (!errors.hasFieldErrors("control")) {
            if (!loadGroup.getControl().matches(zeroOnePattern)) {
                errors.rejectValue("control", key + "ripple.invalidValue", new Object[] { "Control" }, "");
            }
        }

        if (!errors.hasFieldErrors("restore")) {
            if (!loadGroup.getRestore().matches(zeroOnePattern)) {
                errors.rejectValue("restore", key + "ripple.invalidValue", new Object[] { "Restore" }, "");
            }
        }

        if (!errors.hasFieldErrors("control")) {

            if ((specialRipple & SHOW_SPECIAL_RIPPLE) != 0) {
                YukonValidationUtils.checkExactLength("control", errors, loadGroup.getControl(), "Control", 34);
            } else {
                YukonValidationUtils.checkExactLength("control", errors, loadGroup.getControl(), "Control", 50);
            }
        }

        if (!errors.hasFieldErrors("restore")) {

            if ((specialRipple & SHOW_SPECIAL_RIPPLE) != 0) {
                YukonValidationUtils.checkExactLength("restore", errors, loadGroup.getRestore(), "Restore", 34);
            } else {
                YukonValidationUtils.checkExactLength("restore", errors, loadGroup.getRestore(), "Restore", 50);
            }
        }

        if ((specialRipple & SHOW_SPECIAL_RIPPLE) != 0) {
            lmValidatorHelper.checkIfFieldRequired("group", errors, loadGroup.getGroup(), "Group");
            lmValidatorHelper.checkIfFieldRequired("areaCode", errors, loadGroup.getAreaCode(), "Area Code");
        } else if (loadGroup.getGroup() != null || loadGroup.getAreaCode() != null) {
            errors.reject(key + "notAllowedGroupAndAreaCode");
        }
    }
}
