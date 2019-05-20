package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class LoadGroupSetupValidator extends SimpleValidator<LoadGroupBase> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    @Autowired private PaoDao paoDao;

    public LoadGroupSetupValidator() {
        super(LoadGroupBase.class);
    }

    @Override
    protected void doValidation(LoadGroupBase loadGroup, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", key + "type.required");
        PaoType type = loadGroup.getType();

        // Group Name
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "groupName.required");
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", loadGroup.getName(), 60);
        }
        if (!errors.hasFieldErrors("name")) {
            LiteYukonPAObject unique = paoDao.findUnique(loadGroup.getName(), type);
            if (unique != null) {
                errors.rejectValue("name", key + "groupName.unique");
            }
        }
        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(loadGroup.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }
    }

}
