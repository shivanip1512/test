package com.cannontech.web.capcontrol.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
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
        checkForNameConflicts(capbank, errors, false);

    }
    
    private void validateCBCName(CapBank capbank, Errors errors) {
        checkForNameConflicts(capbank, errors, true);
        if(capbank.getName().equals(capbank.getCbcControllerName())){
            errors.rejectValue("name", "yukon.web.error.valuesCannotBeSame");
            errors.rejectValue("cbcControllerName", "yukon.web.error.valuesCannotBeSame");
        }
    }
    
    private void checkForNameConflicts(CapBank capbank, Errors errors, boolean checkCBCName) {
        Integer id = capbank.getId();
        String fieldName = checkCBCName ? "cbcControllerName" : "name";
        PaoType paoType = checkCBCName ? capbank.getCbcType() : capbank.getPaoType();
        String fieldValue = checkCBCName ? capbank.getCbcControllerName() : capbank.getName();
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, "yukon.web.error.isBlank");
        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(capbank.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }

        if (!paoDao.isNameAvailable(fieldValue, paoType)) {

            if (id == null) {
                //For create, we must have an available name
                errors.rejectValue(fieldName, "yukon.web.error.nameConflict");
            } else {
                //For edit, we can use the name it already has
                String existingName = dbCache.getAllPaosMap().get(id).getPaoName();
                if (!existingName.equals(fieldValue)) {
                    errors.rejectValue(fieldName, "yukon.web.error.nameConflict");
                }
            }
        }
    }
}
