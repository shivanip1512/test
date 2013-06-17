package com.cannontech.yukon.api.loadManagement.mocks;

import java.util.Date;

import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;

public class MockProgramStatus extends ProgramStatus {

    private String programName;
    private int programStatus;
    private Date startDateTime;
    private Date stopDateTime;
    private String gearName;
    
    public MockProgramStatus(String programName, int programStatus, String startDateTimeStr, String stopDateTimeStr, String gearName) {
        super(null);
        this.programName = programName;
        this.programStatus = programStatus;
        this.startDateTime = Iso8601DateUtil.parseIso8601Date(startDateTimeStr);
        this.stopDateTime = null;
        if (stopDateTimeStr != null) {
            this.stopDateTime = Iso8601DateUtil.parseIso8601Date(stopDateTimeStr);
        }
        this.gearName = gearName;
    }

    public String getProgramName() {
        return programName;
    }
    public boolean isActive() {
        return programStatus == LMProgramBase.STATUS_ACTIVE;
    }
    public Date getStartTime() {
        return startDateTime;
    }
    public Date getStopTime() {
        return stopDateTime;
    }
    public String getGearName() {
        return gearName;
    }
}