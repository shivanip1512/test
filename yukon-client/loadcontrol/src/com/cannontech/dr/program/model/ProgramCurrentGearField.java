package com.cannontech.dr.program.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.user.YukonUserContext;

public class ProgramCurrentGearField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "CURRENT_GEAR";
    }
    
    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        
        if (program instanceof IGearProgram) {
            LMProgramDirectGear gear = ((IGearProgram) program).getCurrentGear();
            if (gear != null) {
                return buildResolvable(getFieldName(), gear.getGearName());
            }
        }
        return blankFieldResolvable;
    }

    @Override
    public Comparator<DisplayablePao> getSorter(final boolean isDescending,
                                                YukonUserContext userContext) {
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                LMProgramBase program1 = getProgramFromYukonPao(pao1);
                LMProgramBase program2 = getProgramFromYukonPao(pao2);

                LMProgramDirectGear gear1 = null;
                LMProgramDirectGear gear2 = null;
                if (program1 != null && program1 instanceof IGearProgram) {
                    gear1 = ((IGearProgram) program1).getCurrentGear();
                }
                if (program2 != null && program2 instanceof IGearProgram) {
                    gear2 = ((IGearProgram) program2).getCurrentGear();
                }

                if (gear1 == gear2) {
                    return 0;
                }
                if (gear1 == null) {
                    return isDescending ? - 1 : 1;
                }
                if (gear2 == null) {
                    return isDescending ? 1 : -1;
                }
                int retVal = gear1.getGearName().compareTo(gear2.getGearName());
                return isDescending ? (0 - retVal) : retVal;
            }};
    }

}
