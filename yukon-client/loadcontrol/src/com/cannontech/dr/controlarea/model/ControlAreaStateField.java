package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaStateField extends ControlAreaBackingFieldBase {

    @Override
    public String getFieldName() {
        return "STATE";
    }
    
    @Override
    public Object getControlAreaValue(LMControlArea controlArea, YukonUserContext userContext) {
        ControlAreaState state = ControlAreaState.valueOf(controlArea.getControlAreaState());
        return buildResolvable(getFieldName()  + "." + state.name());
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

                return new CompareToBuilder().append(controlArea2.getControlAreaState(),
                                                     controlArea1.getControlAreaState())
                                             .append(controlArea1.getDisableFlag(),
                                                     controlArea2.getDisableFlag())
                                             .toComparison();
            }};
    }

}
