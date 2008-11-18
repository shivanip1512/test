package com.cannontech.yukon.api.loadManagement;

import java.util.Date;

import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.yukon.api.util.XmlUtils;

public class MockProgramStatus extends ProgramStatus {

    private String programName;
    private int programStatus;
    private Date startDateTime;
    private Date stopDateTime;
    private String gearName;
    
    MockProgramStatus(String programName, int programStatus, String startDateTimeStr, String stopDateTimeStr, String gearName) {
        super(null);
        this.programName = programName;
        this.programStatus = programStatus;
        this.startDateTime = XmlUtils.parseDate(startDateTimeStr);
        this.stopDateTime = null;
        if (stopDateTimeStr != null) {
            this.stopDateTime = XmlUtils.parseDate(stopDateTimeStr);
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