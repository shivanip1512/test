package com.cannontech.dr.loadgroup.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.user.YukonUserContext;

public class LoadGroupNameField extends LoadGroupBackingFieldBase {

    @Override
    public String getFieldName() {
        return "NAME";
    }
    
    @Override
    public Object getGroupValue(DirectGroupBase group, YukonUserContext userContext) {
        return buildResolvable(getFieldName(), group.getYukonName());
    }

    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new DisplayablePaoComparator();
    }

}
