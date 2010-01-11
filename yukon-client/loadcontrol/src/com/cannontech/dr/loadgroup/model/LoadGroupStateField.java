package com.cannontech.dr.loadgroup.model;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupStateField extends LoadGroupBackingFieldBase {

    @Override
    public String getFieldName() {
        return "STATE";
    }
    
    @Override
    public Object getGroupValue(LMDirectGroupBase group, YukonUserContext userContext) {
        LoadGroupState state = LoadGroupState.valueOf(group.getGroupControlState());
        return buildResolvable(getFieldName()  + "." + state.name());
    }

    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                LMDirectGroupBase loadGroup1 = getGroupFromYukonPao(pao1);
                LMDirectGroupBase loadGroup2 = getGroupFromYukonPao(pao2);
                if (loadGroup1 == loadGroup2) {
                    return 0;
                }
                if (loadGroup1 == null) {
                    return 1;
                }
                if (loadGroup2 == null) {
                    return -1;
                }

                return new CompareToBuilder().append(loadGroup2.getGroupControlState(),
                                                     loadGroup1.getGroupControlState())
                                             .append(loadGroup1.getDisableFlag(),
                                                     loadGroup2.getDisableFlag())
                                             .toComparison();
            }};
    }

}
