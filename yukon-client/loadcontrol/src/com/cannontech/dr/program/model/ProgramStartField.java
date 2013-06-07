package com.cannontech.dr.program.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.user.YukonUserContext;

public class ProgramStartField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "START";
    }
    
    @Override
    public Object getProgramValue(Program program, YukonUserContext userContext) {
        if (hasBlankStartTime(program)) {
            return blankFieldResolvable;
        } else {
            ResolvableTemplate template = new ResolvableTemplate(getKey(getFieldName()));
            template.addData("date", program.getStartTime().getTime());
            return template;
        }
    }
    
    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                Program program1 = getProgramFromYukonPao(pao1);
                Program program2 = getProgramFromYukonPao(pao2);

                if (hasBlankStartTime(program1)) {
                    program1 = null;
                }
                if (hasBlankStartTime(program2)) {
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

                int retVal = program1.getStartTime().compareTo(program2.getStartTime());
                return retVal;
            }};
    }        

    private boolean hasBlankStartTime(Program program) {
        return program == null || program.getDisableFlag()
            || program.getStartTime() == null
            || program.getStartTime().before(CtiUtilities.get1990GregCalendar());
    }
}
