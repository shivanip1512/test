package com.cannontech.web.api.dr.setup;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;

public class LoadGroupSetupValidator<T extends LoadGroupBase> extends SimpleValidator<T> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    @Autowired private PaoDao paoDao;
    @Autowired private LoadGroupValidatorHelper loadGroupValidatorHelper;

    public LoadGroupSetupValidator() {
        super((Class<T>) LoadGroupBase.class);
    }
    
    public LoadGroupSetupValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T loadGroup, Errors errors) {
        // Type 
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", key + "required", new Object[] { "Type" } );

        // Group Name
        loadGroupValidatorHelper.validateGroupName(loadGroup.getName(), errors);
        if (!errors.hasFieldErrors("name")) {
            Integer paoId = loadGroup.getId();

            // Check if a load group with this name already exists
            if (loadGroup.getType() != null
                && (paoId == null || !(StringUtils.equals(paoDao.getYukonPAOName(paoId), loadGroup.getName())))) {
                loadGroupValidatorHelper.validateUniqueGroupName(loadGroup.getName(), loadGroup.getType(), errors);
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
