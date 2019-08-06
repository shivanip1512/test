package com.cannontech.dr.program.model;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;
import java.text.DateFormat;

public class ProgramStopTimeField extends ProgramStopField {

    @Autowired private DateFormattingService dateFormattingService;
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
            DateFormat dateFormatter = dateFormattingService.getDateFormatter(DateFormattingService.DateFormatEnum.TIME24H, YukonUserContext.system);
            return dateFormatter.format(program.getStopTime().getTime()) + " *";
        } else {
            ResolvableTemplate template = new ResolvableTemplate(getKey(getFieldName()));
            template.addData("date", program.getStopTime().getTime());
            return template;
        }
    }
}
