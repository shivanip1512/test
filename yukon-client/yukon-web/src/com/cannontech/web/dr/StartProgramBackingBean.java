/**
 * 
 */
package com.cannontech.web.dr;

import com.cannontech.user.YukonUserContext;

public class StartProgramBackingBean extends StartProgramBackingBeanBase {
    private int programId;
    private int gearNumber;
    
    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public int getGearNumber() {
        return gearNumber;
    }

    public void setGearNumber(int gearNumber) {
        this.gearNumber = gearNumber;
    }

    public void initDefaults(YukonUserContext userContext) {
        super.initDefaults(userContext);
        gearNumber = 1;
    }
}