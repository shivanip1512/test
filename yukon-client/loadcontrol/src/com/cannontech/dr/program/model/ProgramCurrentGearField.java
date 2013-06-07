package com.cannontech.dr.program.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.messaging.message.loadcontrol.data.GearProgram;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.messaging.message.loadcontrol.data.ProgramDirectGear;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.NaturalOrderComparator;

public class ProgramCurrentGearField extends ProgramBackingFieldBase {
    private final static NaturalOrderComparator comparator =
        new NaturalOrderComparator();

    @Override
    public String getFieldName() {
        return "CURRENT_GEAR";
    }
    
    @Override
    public Object getProgramValue(Program program, YukonUserContext userContext) {
        
        if (program instanceof GearProgram) {
            ProgramDirectGear gear = ((GearProgram) program).getCurrentGear();
            if (gear != null) {
                return buildResolvable(getFieldName(), gear.getGearName());
            }
        }
        return blankFieldResolvable;
    }

    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                Program program1 = getProgramFromYukonPao(pao1);
                Program program2 = getProgramFromYukonPao(pao2);

                ProgramDirectGear gear1 = null;
                ProgramDirectGear gear2 = null;
                if (program1 != null && program1 instanceof GearProgram) {
                    gear1 = ((GearProgram) program1).getCurrentGear();
                }
                if (program2 != null && program2 instanceof GearProgram) {
                    gear2 = ((GearProgram) program2).getCurrentGear();
                }

                if (gear1 == gear2) {
                    return 0;
                }
                if (gear1 == null) {
                    return 1;
                }
                if (gear2 == null) {
                    return -1;
                }
                int retVal = comparator.compare(gear1.getGearName(), gear2.getGearName());
                return retVal;
            }};
    }

}
