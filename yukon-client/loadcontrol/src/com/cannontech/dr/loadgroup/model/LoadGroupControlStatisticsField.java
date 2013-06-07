package com.cannontech.dr.loadgroup.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupControlStatisticsField extends LoadGroupBackingFieldBase {

    @Override
    public String getFieldName() {
        return "CONTROL_STATISTICS";
    }
    
    @Override
    public Object getGroupValue(DirectGroupBase group, YukonUserContext userContext) {
        Object[] result = {
                CtiUtilities.decodeSecondsToTime(group.getCurrentHoursDaily()),
                CtiUtilities.decodeSecondsToTime(group.getCurrentHoursMonthly()),
                CtiUtilities.decodeSecondsToTime(group.getCurrentHoursSeasonal()),
                CtiUtilities.decodeSecondsToTime(group.getCurrentHoursAnnually()) };
        return buildResolvable(getFieldName(), result);
    }
    
    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                
                DirectGroupBase group1 = getGroupFromYukonPao(pao1);
                DirectGroupBase group2 = getGroupFromYukonPao(pao2);
                if (group1 == group2) {
                    return 0;
                }
                if (group1 == null || group1.getCurrentHoursDaily() == null) {
                    return 1;
                }
                if (group2 == null || group2.getCurrentHoursDaily() == null) {
                    return -1;
                }
                Integer state1 = group1.getCurrentHoursDaily();
                Integer state2 = group2.getCurrentHoursDaily();
                int retVal = state1.compareTo(state2);
                return retVal;
            }};
    }

}
