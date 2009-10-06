package com.cannontech.dr.loadgroup.model;

import java.util.Comparator;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupLoadCapacityField extends LoadGroupBackingFieldBase {

    @Override
    public String getFieldName() {
        return "LOAD_CAPACITY";
    }
    
    @Override
    public MessageSourceResolvable getGroupValue(LMDirectGroupBase group, YukonUserContext userContext) {
        return buildResolvable(getFieldName(), new Double(0.0));
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
                Double state1 = 0.0;  // TO BE DETERMINED
                Double state2 = 0.0;
                int retVal = state1.compareTo(state2);
                return isDescending ? (0 - retVal) : retVal;
            }};
    }       

}
