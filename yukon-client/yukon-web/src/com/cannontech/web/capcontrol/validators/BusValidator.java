package com.cannontech.web.capcontrol.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class BusValidator extends SimpleValidator<CapControlSubBus> {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDao paoDao;


    public BusValidator() {
        super(CapControlSubBus.class);
    }

    @Override
    public void doValidation(CapControlSubBus bus, Errors errors) {

        validateName(bus, errors);
    }

    private void validateName(CapControlSubBus bus, Errors errors) {

        Integer id = bus.getId();
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");

        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(bus.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }

        if (!paoDao.isNameAvailable(bus.getName(), PaoType.CAP_CONTROL_SUBBUS)) {

            if (id == null) {
                //For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                //For edit, we can use the name it already has
                String existingName = dbCache.getAllPaosMap().get(id).getPaoName();
                if (!existingName.equals(bus.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }
}
