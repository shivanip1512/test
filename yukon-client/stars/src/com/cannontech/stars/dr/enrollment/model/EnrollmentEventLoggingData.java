package com.cannontech.stars.dr.enrollment.model;

import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.program.model.Program;

public class EnrollmentEventLoggingData {

    private LMHardwareBase lmHardwareBase;
    private String loadGroupName = "";
    private String programName = "";
    
    public EnrollmentEventLoggingData(LMHardwareBase lmHardwareBase, LoadGroup loadGroup, Program program) {
        this.lmHardwareBase = lmHardwareBase;
        setLoadGroupName(loadGroup);
        setProgramName(program);

    }

    public EnrollmentEventLoggingData(LMHardwareBase lmHardwareBase, LoadGroup loadGroup, String programName) {
        this.lmHardwareBase = lmHardwareBase;
        setLoadGroupName(loadGroup);
        this.programName = programName;
    }

    public String getManufacturerSerialNumber() {
        return lmHardwareBase.getManufacturerSerialNumber();
    }
    
    public String getProgramName() {
        return programName;
    }
    
    private void setProgramName(Program program) {
        if (program != null) {
            programName = program.getProgramName();
        }
    }
    
    public String getLoadGroupName() {
        return loadGroupName;
    }
    
    private void setLoadGroupName(LoadGroup loadGroup) {
        if (loadGroup != null) {
            this.loadGroupName = loadGroup.getName();
        }
    }
}
