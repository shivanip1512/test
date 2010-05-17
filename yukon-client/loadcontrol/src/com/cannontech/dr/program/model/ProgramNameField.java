package com.cannontech.dr.program.model;

import java.util.Comparator;

import com.cannontech.common.pao.ControllablePaoComparator;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramNameField extends ProgramBackingFieldBase {

    @Override
    public String getFieldName() {
        return "NAME";
    }
    
    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        return buildResolvable(getFieldName(), program.getYukonName());
    }

    @Override
    public Comparator<ControllablePao> getSorter(YukonUserContext userContext) {
        return new ControllablePaoComparator();
    }

}
