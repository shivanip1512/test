package com.cannontech.web.api.dr.gear.setup.fields.validator;

import com.cannontech.common.dr.gear.setup.fields.ProgramGearFields;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.database.db.device.lm.GearControlMethod;

public abstract class ProgramGearFieldsValidator <T extends ProgramGearFields> extends SimpleValidator<T>  {

    
    public ProgramGearFieldsValidator(Class<T> objectType) {
        super(objectType);
    }

    public abstract GearControlMethod getControlMethod();

}
