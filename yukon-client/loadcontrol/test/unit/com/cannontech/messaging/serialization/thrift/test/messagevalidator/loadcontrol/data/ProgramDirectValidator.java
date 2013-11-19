package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMGroupDigiSep;
import com.cannontech.loadcontrol.data.LMGroupEmetcon;
import com.cannontech.loadcontrol.data.LMGroupExpresscom;
import com.cannontech.loadcontrol.data.LMGroupGolay;
import com.cannontech.loadcontrol.data.LMGroupMCT;
import com.cannontech.loadcontrol.data.LMGroupPoint;
import com.cannontech.loadcontrol.data.LMGroupRipple;
import com.cannontech.loadcontrol.data.LMGroupSA105;
import com.cannontech.loadcontrol.data.LMGroupSA205;
import com.cannontech.loadcontrol.data.LMGroupSA305;
import com.cannontech.loadcontrol.data.LMGroupSADigital;
import com.cannontech.loadcontrol.data.LMGroupVersacom;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ProgramDirectValidator extends AutoInitializedClassValidator<LMProgramDirect> {

    private boolean generateProgramDirectLists;

    public ProgramDirectValidator() {
        super(LMProgramDirect.class);
        generateProgramDirectLists = true;
    }

    @Override
    public void populateExpectedValue(LMProgramDirect ctrlObj, RandomGenerator generator) {

        ctrlObj.setCurrentGearNumber(generator.generateInt() + 1);
        ctrlObj.setLastGroupControlled(generator.generateInt());
        ctrlObj.setDirectStartTime(generator.generateCalendar());
        ctrlObj.setDirectStopTime(generator.generateCalendar());
        ctrlObj.setNotifyActiveTime(generator.generateCalendar());
        ctrlObj.setNotifyInactiveTime(generator.generateCalendar());
        ctrlObj.setStartedRampingOut(generator.generateCalendar());
        ctrlObj.setTriggerOffset(generator.generateInt());
        ctrlObj.setTriggerRestoreOffset(generator.generateInt());
        ctrlObj.setConstraintOverride(generator.generateBoolean());

        ctrlObj.setDirectGearVector(getDefaultObjectListFor(LMProgramDirectGear.class, generator));

        List<LMGroupBase> list = new ArrayList<>();

        list.add(getAutoValidatorFor(LMGroupDigiSep.class).getDefaultObject(generator));
        list.add(getAutoValidatorFor(LMGroupEmetcon.class).getDefaultObject(generator));
        list.add(getAutoValidatorFor(LMGroupExpresscom.class).getDefaultObject(generator));
        list.add(getAutoValidatorFor(LMGroupGolay.class).getDefaultObject(generator));
        list.add(getAutoValidatorFor(LMGroupMCT.class).getDefaultObject(generator));
        list.add(getAutoValidatorFor(LMGroupPoint.class).getDefaultObject(generator));
        list.add(getAutoValidatorFor(LMGroupRipple.class).getDefaultObject(generator));
        list.add(getAutoValidatorFor(LMGroupSA105.class).getDefaultObject(generator));
        list.add(getAutoValidatorFor(LMGroupSA205.class).getDefaultObject(generator));
        list.add(getAutoValidatorFor(LMGroupSA305.class).getDefaultObject(generator));
        list.add(getAutoValidatorFor(LMGroupSADigital.class).getDefaultObject(generator));
        list.add(getAutoValidatorFor(LMGroupVersacom.class).getDefaultObject(generator));
        ctrlObj.setLoadControlGroupVector(list);

        
        // This validator is creating sub item of LMProgramDirect itself.
        // So in order to prevent infinite recursion, we only generate one level and then stop.
        // Note that this is not thread safe but test usually don't run in multiple threads
        if (this.generateProgramDirectLists) {   
            this.generateProgramDirectLists = false;

            Vector<LMProgramDirect> vect = getDefaultObjectVectorFor(LMProgramDirect.class, generator, 1);
            vect.elementAt(0).setProgramStatus(generator.generateInt(1, 9));
            ctrlObj.setActiveMasterPrograms(vect);

            vect = getDefaultObjectVectorFor(LMProgramDirect.class, generator, 1);
            vect.elementAt(0).setProgramStatus(generator.generateInt(1, 9));
            ctrlObj.setActiveSubordinatePrograms(vect);

            this.generateProgramDirectLists = true;
        }
        else {
            ctrlObj.setActiveMasterPrograms(new Vector<>());
            ctrlObj.setActiveSubordinatePrograms(new Vector<>());
        }
    }
}
