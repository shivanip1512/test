package com.cannontech.web.capcontrol.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.capcontrol.dao.DmvTestDao;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.capcontrol.DmvTest;

@Service
public class DmvTestValidator extends SimpleValidator<DmvTest>{

    @Autowired DmvTestDao dmvTestDao;
    
    public DmvTestValidator() {
        super(DmvTest.class);
    }

    @Override
    protected void doValidation(DmvTest target, Errors errors) {
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");   
        YukonValidationUtils.checkExceedsMaxLength(errors, "name", target.getName(), 100);
        YukonValidationUtils.checkRange(errors, "pollingInterval", target.getPollingInterval(), 15, 300, true);
        YukonValidationUtils.checkRange(errors, "dataGatheringDuration", target.getDataGatheringDuration(), 1, 30, true);
        YukonValidationUtils.checkRange(errors, "stepSize", target.getStepSize(), 0.75, 5.0, true);
        YukonValidationUtils.checkRange(errors, "commSuccPercentage", target.getCommSuccPercentage(), 0, 100, true);
        if (!dmvTestDao.isUniqueDmvTestName(target.getName(), target.getDmvTestId())) {
            errors.rejectValue("name", "yukon.web.error.nameConflict");
        }

    }

}
