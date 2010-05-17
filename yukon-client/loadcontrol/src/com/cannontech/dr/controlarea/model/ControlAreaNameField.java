package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import com.cannontech.common.pao.ControllablePaoComparator;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

public class ControlAreaNameField extends ControlAreaBackingFieldBase {

    @Override
    public String getFieldName() {
        return "NAME";
    }
    
    @Override
    public Object getControlAreaValue(LMControlArea controlArea, YukonUserContext userContext) {
        return buildResolvable(getFieldName(), controlArea.getYukonName());
    }

    @Override
    public Comparator<ControllablePao> getSorter(YukonUserContext userContext) {
        return new ControllablePaoComparator();
    }
}
