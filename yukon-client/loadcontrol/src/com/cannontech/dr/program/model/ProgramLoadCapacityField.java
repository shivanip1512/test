package com.cannontech.dr.program.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramLoadCapacityField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "LOAD_CAPACITY";
    }
    
    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        return buildResolvable(getFieldName(), new Double(0.0));
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
                Double state1 = 0.0;  // TO BE DETERMINED
                Double state2 = 0.0;
                int retVal = state1.compareTo(state2);
                return retVal;
            }};
    }       

}
