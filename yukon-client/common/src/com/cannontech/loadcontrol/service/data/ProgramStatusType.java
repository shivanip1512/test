package com.cannontech.loadcontrol.service.data;

import com.cannontech.loadcontrol.ProgramUtils;
import com.cannontech.loadcontrol.data.LMProgramBase;

public enum ProgramStatusType {

    ACTIVE("Active") {
        @Override
        public boolean checkProgramStatus(ProgramStatus programStatus) {
            return programStatus.isActive();
        }

        @Override
        public boolean checkProgramStatus(LMProgramBase programBase) {
            return ProgramUtils.isActive(programBase);
        }
    },
    SCHEDULED("Scheduled") {
        @Override
        public boolean checkProgramStatus(ProgramStatus programStatus) {
            return programStatus.isScheduled();
        }

        @Override
        public boolean checkProgramStatus(LMProgramBase programBase) {
            return ProgramUtils.isScheduled(programBase);
        }
    },
    INACTIVE("Inactive") {
        @Override
        public boolean checkProgramStatus(ProgramStatus programStatus) {
            return programStatus.isInactive();
        }

        @Override
        public boolean checkProgramStatus(LMProgramBase programBase) {
            return ProgramUtils.isInactive(programBase);
        }
    };

    private final String programStatusType;

    private ProgramStatusType(String programStatusType) {
        this.programStatusType = programStatusType;
    }

    public abstract boolean checkProgramStatus(ProgramStatus programStatus);

    public abstract boolean checkProgramStatus(LMProgramBase programBase);

    public final static String getStatus(ProgramStatus programStatus) {

        String status = "";
        if (programStatus.isActive()) {
            status = ACTIVE.programStatusType;
        } else if (programStatus.isScheduled()) {
            status = SCHEDULED.programStatusType;
        } else if (programStatus.isInactive()) {
            status = INACTIVE.programStatusType;
        }
        return status;

    };

}
