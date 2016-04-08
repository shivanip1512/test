package com.cannontech.web.capcontrol.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.capcontrol.CapControlSubstation;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class SubstationValidator extends SimpleValidator<CapControlSubstation> {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDao paoDao;


    public SubstationValidator() {
        super(CapControlSubstation.class);
    }

    @Override
    public void doValidation(CapControlSubstation substation, Errors errors) {

        validateName(substation, errors);
    }

    private void validateName(CapControlSubstation substation, Errors errors) {

        Integer id = substation.getId();
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
        
        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(substation.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }

        if (!paoDao.isNameAvailable(substation.getName(), PaoType.CAP_CONTROL_SUBSTATION)) {

            if (id == null) {
                //For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                //For edit, we can use the name it already has
                String existingName = dbCache.getAllPaosMap().get(id).getPaoName();
                if (!existingName.equals(substation.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }
}
