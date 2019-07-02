package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class NoControlGearFields implements ProgramGearFields {

    private WhenToChangeFields whenToChangeFields;

    public WhenToChangeFields getWhenToChangeFields() {
        return whenToChangeFields;
    }

    public void setWhenToChangeFields(WhenToChangeFields whenToChangeFields) {
        this.whenToChangeFields = whenToChangeFields;
    }

    @Override
    public void buildModel(LMProgramDirectGear lmProgramDirectGear) {
        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(lmProgramDirectGear);
        setWhenToChangeFields(whenToChangeFields);

    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear lmProgramDirectGear) {
        whenToChangeFields.buildDBPersistent(lmProgramDirectGear);
    }

}
