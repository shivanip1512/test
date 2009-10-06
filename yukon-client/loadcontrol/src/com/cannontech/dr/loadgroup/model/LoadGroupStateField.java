package com.cannontech.dr.loadgroup.model;

import java.util.Comparator;

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
    public Comparator<DisplayablePao> getSorter(final boolean isDescending,
                                                YukonUserContext userContext) {
        return new Comparator<DisplayablePao>() {

            @Override
            public int compare(DisplayablePao pao1, DisplayablePao pao2) {
                LMDirectGroupBase group1 = getGroupFromYukonPao(pao1);
                LMDirectGroupBase group2 = getGroupFromYukonPao(pao2);
                if (group1 == group2) {
                    return 0;
                }
                if (group1 == null) {
                    return isDescending ? -1 : 1;
                }
                if (group2 == null) {
                    return isDescending ? 1 : -1;
                }
                Integer state1 = group1.getGroupControlState();
                Integer state2 = group2.getGroupControlState();
                int retVal = state1.compareTo(state2);
                return isDescending ? (0 - retVal) : retVal;
            }};
    }

}
