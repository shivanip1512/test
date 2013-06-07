package com.cannontech.dr.program.model;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.user.YukonUserContext;

public class ProgramStateField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "STATE";
    }
    
    @Override
    public Object getProgramValue(Program program, YukonUserContext userContext) {
        ProgramState state = ProgramState.valueOf(program.getProgramStatus());
        return buildResolvable(getFieldName()  + "." + state.name());
    }

    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                Program program1 = getProgramFromYukonPao(pao1);
                Program program2 = getProgramFromYukonPao(pao2);
                if (program1 == program2) {
                    return 0;
                }
                if (program1 == null) {
                    return 1;
                }
                if (program2 == null) {
                    return -1;
                }

                return new CompareToBuilder().append(program2.getProgramStatus(),
                                                     program1.getProgramStatus())
                                             .append(program1.getDisableFlag(),
                                                     program2.getDisableFlag())
                                             .toComparison();
            }};
    }

}
