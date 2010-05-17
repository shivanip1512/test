package com.cannontech.dr.loadgroup.model;

import java.util.Comparator;

import com.cannontech.common.pao.ControllablePaoComparator;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupNameField extends LoadGroupBackingFieldBase {

    @Override
    public String getFieldName() {
        return "NAME";
    }
    
    @Override
    public Object getGroupValue(LMDirectGroupBase group, YukonUserContext userContext) {
        return buildResolvable(getFieldName(), group.getYukonName());
    }

    @Override
    public Comparator<ControllablePao> getSorter(YukonUserContext userContext) {
        return new ControllablePaoComparator();
    }

}
