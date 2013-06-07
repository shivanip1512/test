package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.cannontech.user.YukonUserContext;

public class ControlAreaLoadCapacityField extends ControlAreaBackingFieldBase {

    @Override
    public String getFieldName() {
        return "LOAD_CAPACITY";
    }
    
    @Override
    public Object getControlAreaValue(ControlAreaItem controlArea, YukonUserContext userContext) {
        return buildResolvable(getFieldName(), new Double(0.0));
    }

    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                ControlAreaItem controlArea1 = getControlAreaFromYukonPao(pao1);
                ControlAreaItem controlArea2 = getControlAreaFromYukonPao(pao2);
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
