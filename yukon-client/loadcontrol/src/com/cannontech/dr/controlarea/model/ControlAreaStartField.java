package com.cannontech.dr.controlarea.model;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaStartField extends ControlAreaBackingFieldBase {

    private SystemDateFormattingService systemDateFormattingService;
    
    @Override
    public String getFieldName() {
        return "START";
    }
    
    @Override
    public Object getControlAreaValue(LMControlArea controlArea, YukonUserContext userContext) {
        Calendar startDate = null;
        if (controlArea.getDailyStartTime() > -1) {
            startDate = systemDateFormattingService.getSystemCalendar();
            startDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
            startDate.set(GregorianCalendar.MINUTE, 0);
            startDate.set(GregorianCalendar.SECOND, controlArea.getDailyStartTime());
        }

        if (startDate == null || startDate.before(CtiUtilities.get1990GregCalendar())) {
            return blankFieldResolvable;
        }
        
        ResolvableTemplate template = new ResolvableTemplate(getKey(getFieldName()));
        template.addData("date", startDate.getTime());
        return template;
    }
    
    @Override
    public Comparator<ControllablePao> getSorter(YukonUserContext userContext) {
        return new Comparator<ControllablePao>() {

            @Override
            public int compare(ControllablePao pao1, ControllablePao pao2) {
                LMControlArea controlArea1 = getControlAreaFromYukonPao(pao1);
                LMControlArea controlArea2 = getControlAreaFromYukonPao(pao2);
                if (controlArea1 == controlArea2) {
                    return 0;
                }
                if (controlArea1 == null) {
                    return 1;
                }
                if (controlArea2 == null) {
                    return -1;
                }
                Integer state1 = controlArea1.getDailyStartTime();
                Integer state2 = controlArea2.getDailyStartTime();
                int retVal = state1.compareTo(state2);
                return retVal;
            }};
    }

    @Autowired
    public void setSystemDateFormattingService(SystemDateFormattingService systemDateFormattingService) {
        this.systemDateFormattingService = systemDateFormattingService;
    }

}
