package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import java.util.Vector;

import com.cannontech.common.pao.PaoType;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramCurtailment;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.data.LMProgramEnergyExchange;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ControlAreaItemValidator extends AutoInitializedClassValidator<LMControlArea> {
    private static final PaoType[] poaType = { PaoType.MCT440_2131B, PaoType.MCT440_2132B, PaoType.MCT440_2133B };

    public ControlAreaItemValidator() {
        super(LMControlArea.class);
    }

    @Override
    public void populateExpectedValue(LMControlArea ctrlObj, RandomGenerator generator) {
        ctrlObj.setYukonID(generator.generateInt());
        generator.generateString(); // PaoCategory
        generator.generateString(); // PaoClass
        ctrlObj.setYukonName(generator.generateString());
        ctrlObj.setYukonType(generator.generateChoice(poaType));        
        ctrlObj.setYukonDescription(generator.generateString());
        ctrlObj.setDisableFlag(generator.generateBoolean());
        ctrlObj.setDefOperationalState(generator.generateString());
        ctrlObj.setControlInterval(generator.generateInt());
        ctrlObj.setMinResponseTime(generator.generateInt());
        ctrlObj.setDefDailyStartTime(generator.generateInt());
        ctrlObj.setDefDailyStopTime(generator.generateInt());
        ctrlObj.setRequireAllTriggersActiveFlag(generator.generateBoolean());
        ctrlObj.setNextCheckTime(generator.generateCalendar());
        ctrlObj.setNewPointDataReceivedFlag(generator.generateBoolean());
        ctrlObj.setUpdatedFlag(generator.generateBoolean());
        ctrlObj.setControlAreaStatusPointId(generator.generateInt());
        ctrlObj.setControlAreaState(generator.generateInt());
        ctrlObj.setCurrentPriority(generator.generateInt());
        ctrlObj.setCurrentDailyStartTime(generator.generateInt());
        ctrlObj.setCurrentDailyStopTime(generator.generateInt());
        ctrlObj.setTriggerVector(getDefaultObjectVectorFor(LMControlAreaTrigger.class, generator));

        Vector<LMProgramBase> programVect = new Vector<>();

        programVect.add(getDefaultObjectFor(LMProgramCurtailment.class, generator));        
        programVect.add(getDefaultObjectFor(LMProgramDirect.class, generator));
        programVect.add(getDefaultObjectFor(LMProgramEnergyExchange.class, generator));
        
        ctrlObj.setLmProgramVector(programVect);
    }
}
