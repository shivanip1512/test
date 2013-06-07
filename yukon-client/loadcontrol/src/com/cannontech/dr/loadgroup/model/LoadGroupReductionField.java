package com.cannontech.dr.loadgroup.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupReductionField extends LoadGroupBackingFieldBase {

    @Override
    public String getFieldName() {
        return "REDUCTION";
    }
    
    @Override
    public Object getGroupValue(DirectGroupBase group, YukonUserContext userContext) {
        return buildResolvable(getFieldName(), group.getReduction());
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
                if (group1 == null || group1.getReduction() == null) {
                    return -1;
                }
                if (group2 == null || group2.getReduction() == null) {
                    return 1;
                }
                Double reduction1 = group1.getReduction();
                Double reduction2 = group2.getReduction();
                int retVal = reduction1.compareTo(reduction2);
                return retVal;
            }};
    }

}
