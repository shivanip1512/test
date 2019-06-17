package com.cannontech.dr.program.model;

import org.joda.time.DateTime;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramStopTimeField extends ProgramStopField {

    @Override
    public String getFieldName() {
        return "STOP_TIME";
    }
    
    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        DateTime endOfProgramControlDay =
            new DateTime(program.getStartTime().getTimeInMillis()).withTimeAtStartOfDay().plusHours(24);
        if (hasBlankStopTime(program)) {
            return blankFieldResolvable;
        } else if (program.getStopTime().getTime().after(endOfProgramControlDay.toDate())) {
            // If stop time is not in the same day, return "--"
            return blankValueResolvable;
        } else {
            ResolvableTemplate template = new ResolvableTemplate(getKey(getFieldName()));
            template.addData("date", program.getStopTime().getTime());
            return template;
        }
    }
}
