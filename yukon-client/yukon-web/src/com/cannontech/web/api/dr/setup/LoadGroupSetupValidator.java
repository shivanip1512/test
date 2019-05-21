package com.cannontech.web.api.dr.setup;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupBase;
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
        // Type 
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", key + "type.required");

        // Group Name
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "groupName.required");
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", loadGroup.getName(), 60);
        }
        if (!errors.hasFieldErrors("name")) {
            Integer paoId = loadGroup.getId();

            // Check if a load group with this name already exists
            if (loadGroup.getType() != null
                && (paoId == null || !(StringUtils.equals(paoDao.getYukonPAOName(paoId), loadGroup.getName())))) {
                LiteYukonPAObject unique = paoDao.findUnique(loadGroup.getName(), loadGroup.getType());
                if (unique != null) {
                    errors.rejectValue("name", key + "groupName.unique");
                }
            }
        }
        
        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(loadGroup.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }

        // kWCapacity
        YukonValidationUtils.checkIsPositiveDouble(errors, "kWCapacity", loadGroup.getkWCapacity());
        if (!errors.hasFieldErrors("kWCapacity")) {
            YukonValidationUtils.checkRange(errors, "kWCapacity", loadGroup.getkWCapacity(), 0.0, 99999.999, true);
        }
    }

}
