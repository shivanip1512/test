package com.cannontech.dr.controlarea.model;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaStopField extends ControlAreaBackingFieldBase {

    private SystemDateFormattingService systemDateFormattingService;
    
    @Override
    public String getFieldName() {
        return "STOP";
    }
    
    @Override
    public Object getControlAreaValue(LMControlArea controlArea, YukonUserContext userContext) {
        Calendar stopDate = null;
        if (controlArea.getDailyStopTime() > -1) {
            stopDate = systemDateFormattingService.getSystemCalendar();
            stopDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
            stopDate.set(GregorianCalendar.MINUTE, 0);
            stopDate.set(GregorianCalendar.SECOND, controlArea.getDailyStopTime());
        }

        if (stopDate == null || stopDate.before(CtiUtilities.get1990GregCalendar())
                || stopDate.compareTo(CtiUtilities.get2035GregCalendar()) >= 0) {
            return blankFieldResolvable;
        }
        
        ResolvableTemplate template = new ResolvableTemplate(getKey(getFieldName()));
        template.addData("date", stopDate.getTime());
        return template;
    }
    
    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
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
                Integer state1 = controlArea1.getDailyStopTime();
                Integer state2 = controlArea2.getDailyStopTime();
                int retVal = state1.compareTo(state2);
                return retVal;
            }};
    }

    @Autowired
    public void setSystemDateFormattingService(SystemDateFormattingService systemDateFormattingService) {
        this.systemDateFormattingService = systemDateFormattingService;
    }

}
