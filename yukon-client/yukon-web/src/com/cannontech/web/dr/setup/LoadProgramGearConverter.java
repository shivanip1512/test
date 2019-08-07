package com.cannontech.web.dr.setup;

import org.apache.logging.log4j.core.Logger;
import org.springframework.core.convert.converter.Converter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.gear.setup.fields.ProgramGearFields;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.setup.LMModelFactory;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class LoadProgramGearConverter implements Converter<String, ProgramGear> {
    private static final Logger log = YukonLogManager.getLogger(LoadProgramGearConverter.class);

    @Override
    public ProgramGear convert(String gearType) {
        ProgramGear programGear = new ProgramGear();
        GearControlMethod gearControlMethod = null;
        try {
            gearControlMethod = GearControlMethod.getGearControlMethod(gearType);
        } catch (IllegalArgumentException e) {
            log.error(gearControlMethod + " Gear type doesn't match with existing gear types", e);
        }
        ProgramGearFields gearFields = LMModelFactory.createProgramGearFields(gearControlMethod);
        programGear.setFields(gearFields);
        return programGear;
    }
}
