package com.cannontech.dr.loadgroup.model;

import java.util.Comparator;

import com.cannontech.dr.model.ControllablePao;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupLoadCapacityField extends LoadGroupBackingFieldBase {

    @Override
    public String getFieldName() {
        return "LOAD_CAPACITY";
    }
    
    @Override
    public Object getGroupValue(LMDirectGroupBase group, YukonUserContext userContext) {
        return buildResolvable(getFieldName(), new Double(0.0));
    }
    
    @Override
    public Comparator<ControllablePao> getSorter(YukonUserContext userContext) {
        return new Comparator<ControllablePao>() {

            @Override
            public int compare(ControllablePao pao1, ControllablePao pao2) {
                LMDirectGroupBase group1 = getGroupFromYukonPao(pao1);
                LMDirectGroupBase group2 = getGroupFromYukonPao(pao2);
                if (group1 == group2) {
                    return 0;
                }
                if (group1 == null) {
                    return 1;
                }
                if (group2 == null) {
                    return -1;
                }
                Double state1 = 0.0;  // TO BE DETERMINED
                Double state2 = 0.0;
                int retVal = state1.compareTo(state2);
                return retVal;
            }};
    }       

}
