package com.cannontech.dr.program.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramStopField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "STOP";
    }
    
    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        // return dashes if stop time is null, <1990 or >= 2035
        if (hasBlankStopTime(program)) {
            return blankFieldResolvable;
        } else {
            ResolvableTemplate template = new ResolvableTemplate(getKey(getFieldName()));
            template.addData("date", program.getStopTime().getTime());
            return template;
        }
    }
    
    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                LMProgramBase program1 = getProgramFromYukonPao(pao1);
                LMProgramBase program2 = getProgramFromYukonPao(pao2);

                if (hasBlankStopTime(program1)) {
                    program1 = null;
                }
                if (hasBlankStopTime(program2)) {
                    program2 = null;
                }

                if (program1 == program2) {
                    return 0;
                }

                if (program1 == null) {
                    return 1;
                }
                if (program2 == null) {
                    return -1;
                }

                int retVal = program1.getStopTime().compareTo(program2.getStopTime());
                return retVal;
            }};
    }        

    protected boolean hasBlankStopTime(LMProgramBase program) {
        return program == null || program.getDisableFlag()
            || program.getStopTime() == null
            || program.getStopTime().before(CtiUtilities.get1990GregCalendar())
            || !program.getStopTime().before(CtiUtilities.get2035GregCalendar());
    }
}
