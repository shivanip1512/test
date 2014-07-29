package com.cannontech.dr.program.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramPriorityField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "PRIORITY";
    }
    
    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        return buildResolvable(getFieldName(),
                               program.getStartPriority() <= 0
                                   ? 1 : program.getStartPriority());
    }

    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                LMProgramBase program1 = getProgramFromYukonPao(pao1);
                LMProgramBase program2 = getProgramFromYukonPao(pao2);
                if (program1 == program2) {
                    return 0;
                }
                if (program1 == null) {
                    return 1;
                }
                if (program2 == null) {
                    return -1;
                }
                Integer priority1 = program1.getStartPriority() <= 0
                    ? 1 : program1.getStartPriority();
                Integer priority2 = program2.getStartPriority() <= 0
                    ? 1 : program2.getStartPriority();
                int retVal = priority1.compareTo(priority2);
                return retVal;
            }};
    }

}
