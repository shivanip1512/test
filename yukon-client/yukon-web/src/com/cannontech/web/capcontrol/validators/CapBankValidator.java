package com.cannontech.web.capcontrol.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class CapBankValidator extends SimpleValidator<CapBank> {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDao paoDao;


    public CapBankValidator() {
        super(CapBank.class);
    }

    @Override
    public void doValidation(CapBank capbank, Errors errors) {

        validateName(capbank, errors);
        if (capbank.getCreateCBC()) {
            validateCBCName(capbank, errors);
        }

    }

    private void validateName(CapBank capbank, Errors errors) {

        Integer id = capbank.getId();
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");

        if (!paoDao.isNameAvailable(capbank.getName(), PaoType.CAPBANK)) {

            if (id == null) {
                //For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                //For edit, we can use the name it already has
                String existingName = dbCache.getAllPaosMap().get(id).getPaoName();
                if (!existingName.equals(capbank.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }
    
    private void validateCBCName(CapBank capbank, Errors errors) {

        Integer id = capbank.getId();
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "cbcControllerName", "yukon.web.error.isBlank");

        if (!paoDao.isNameAvailable(capbank.getCbcControllerName(), capbank.getCbcType())) {

            if (id == null) {
                //For create, we must have an available name
                errors.rejectValue("cbcControllerName", "yukon.web.error.nameConflict");
            } else {
                //For edit, we can use the name it already has
                String existingName = dbCache.getAllPaosMap().get(id).getPaoName();
                if (!existingName.equals(capbank.getName())) {
                    errors.rejectValue("cbcControllerName", "yukon.web.error.nameConflict");
                }
            }
        }
        if(capbank.getName().equals(capbank.getCbcControllerName())){
            errors.rejectValue("name", "yukon.web.error.valuesCannotBeSame");
            errors.rejectValue("cbcControllerName", "yukon.web.error.valuesCannotBeSame");
        }
    }
}
