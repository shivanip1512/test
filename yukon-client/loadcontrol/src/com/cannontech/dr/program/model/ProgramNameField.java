package com.cannontech.dr.program.model;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.user.YukonUserContext;

public class ProgramNameField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "NAME";
    }
    
    @Override
    public Object getProgramValue(Program program, YukonUserContext userContext) {
        return buildResolvable(getFieldName(), program.getYukonName());
    }

    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        return new DisplayablePaoComparator();
    }

}
