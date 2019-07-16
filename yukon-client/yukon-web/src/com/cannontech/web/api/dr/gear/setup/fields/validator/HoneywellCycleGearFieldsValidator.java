package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.HoneywellCycleGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class HoneywellCycleGearFieldsValidator extends ProgramGearFieldsValidator<HoneywellCycleGearFields> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    
    public HoneywellCycleGearFieldsValidator() {
        super(HoneywellCycleGearFields.class);
    }

    public HoneywellCycleGearFieldsValidator(Class<HoneywellCycleGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.HoneywellCycle;
    }

    @Override
    protected void doValidation(HoneywellCycleGearFields target, Errors errors) {
        lmValidatorHelper.checkIfFieldRequired("howToStopControl", errors, target.getHowToStopControl(), "How To Stop Control");
        //TODO validate honeywell fields
    }

}
