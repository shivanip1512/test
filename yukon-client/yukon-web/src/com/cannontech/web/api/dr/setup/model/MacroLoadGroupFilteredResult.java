package com.cannontech.web.api.dr.setup.model;

import java.util.List;

import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMPaoDto;

public class MacroLoadGroupFilteredResult {

    private LMDto macro;
    private List<LMPaoDto> assignedLoadGroups;

    public LMDto getMacro() {
        return macro;
    }

    public void setMacro(LMDto macro) {
        this.macro = macro;
    }

    public List<LMPaoDto> getAssignedLoadGroups() {
        return assignedLoadGroups;
    }

    public void setAssignedLoadGroups(List<LMPaoDto> assignedLoadGroups) {
        this.assignedLoadGroups = assignedLoadGroups;
    }

}
