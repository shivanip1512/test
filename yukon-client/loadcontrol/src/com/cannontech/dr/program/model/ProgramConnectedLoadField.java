package com.cannontech.dr.program.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;

public class ProgramConnectedLoadField extends ProgramBackingFieldBase {

    @Autowired EstimatedLoadService estimatedLoadService;

    @Override
    public String getFieldName() {
        return "CONNECTED_LOAD";
    }

    @Override
    public Object getProgramValue(LMProgramBase program, YukonUserContext userContext) {
        return estimatedLoadService.getConnectedLoad(program.getPaoIdentifier());
    }

}
