package com.cannontech.web.capcontrol.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class FeederValidator extends SimpleValidator<CapControlFeeder> {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDao paoDao;


    public FeederValidator() {
        super(CapControlFeeder.class);
    }

    @Override
    public void doValidation(CapControlFeeder feeder, Errors errors) {

        validateName(feeder, errors);
    }

    private void validateName(CapControlFeeder feeder, Errors errors) {

        Integer id = feeder.getId();
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");

        if (!paoDao.isNameAvailable(feeder.getName(), PaoType.CAP_CONTROL_FEEDER)) {

            if (id == null) {
                //For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                //For edit, we can use the name it already has
                String existingName = dbCache.getAllPaosMap().get(id).getPaoName();
                if (!existingName.equals(feeder.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }
}
