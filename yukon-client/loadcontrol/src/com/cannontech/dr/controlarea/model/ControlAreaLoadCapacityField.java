package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import com.cannontech.dr.model.ControllablePao;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaLoadCapacityField extends ControlAreaBackingFieldBase {

    @Override
    public String getFieldName() {
        return "LOAD_CAPACITY";
    }
    
    @Override
    public Object getControlAreaValue(LMControlArea controlArea, YukonUserContext userContext) {
        return buildResolvable(getFieldName(), new Double(0.0));
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
                Double state1 = 0.0;  // TO BE DETERMINED
                Double state2 = 0.0;
                int retVal = state1.compareTo(state2);
                return retVal;
            }};
    }

}
