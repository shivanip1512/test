package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.cannontech.user.YukonUserContext;

public class ControlAreaNameField extends ControlAreaBackingFieldBase {

    @Override
    public String getFieldName() {
        return "NAME";
    }
    
    @Override
    public Object getControlAreaValue(ControlAreaItem controlArea, YukonUserContext userContext) {
        return buildResolvable(getFieldName(), controlArea.getYukonName());
    }

    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new DisplayablePaoComparator();
    }
}
