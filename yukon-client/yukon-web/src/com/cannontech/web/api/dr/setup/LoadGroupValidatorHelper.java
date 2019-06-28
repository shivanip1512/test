package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Helper class for Load group validation
 */
public class LoadGroupValidatorHelper {
    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";
    @Autowired private PaoDao paoDao;

    /**
     * Checks whether the Load Group name is empty or not
     */
    public void checkIfEmptyGroupName(String loadGroupName, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "required", new Object[] { "Group Name" });
    }

    /**
     * Checks whether the Load Group name is valid or not
     */
    public void validateGroupName(String loadGroupName, Errors errors) {
        checkIfEmptyGroupName(loadGroupName, errors);
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", loadGroupName, 60);
            if (!PaoUtils.isValidPaoName(loadGroupName)) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }
    }

    /**
     * Checks whether the Load Group name is unique or not
     */
    public void validateUniqueGroupName(String loadGroupName, PaoType loadGroupType, Errors errors) {
        LiteYukonPAObject unique = paoDao.findUnique(loadGroupName, loadGroupType);
        if (unique != null) {
            errors.rejectValue("name", key + "groupName.unique");
        }
    }
}
