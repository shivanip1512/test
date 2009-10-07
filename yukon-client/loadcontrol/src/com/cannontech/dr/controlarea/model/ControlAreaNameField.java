package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.dr.model.DisplayablePaoComparator;
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
    public Comparator<DisplayablePao> getSorter(boolean isDescending,
                                                YukonUserContext userContext) {
        return new DisplayablePaoComparator(userContext, isDescending);
    }

}
