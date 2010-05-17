package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import com.cannontech.dr.model.ControllablePao;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaPriorityField extends ControlAreaBackingFieldBase {

    @Override
    public String getFieldName() {
        return "PRIORITY";
    }
    
    @Override
    public Object getControlAreaValue(LMControlArea controlArea, YukonUserContext userContext) {
        int result = controlArea.getCurrentPriority() <= 0 ? 1 : controlArea.getCurrentPriority();
        return buildResolvable(getFieldName(), result);
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
                Integer priority1 = controlArea1.getCurrentPriority() <= 0
                    ? 1 : controlArea1.getCurrentPriority();
                Integer priority2 = controlArea2.getCurrentPriority() <= 0
                    ? 1 : controlArea2.getCurrentPriority();
                int retVal = priority1.compareTo(priority2);
                return retVal;
            }};
    }

}
