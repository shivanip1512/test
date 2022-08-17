package com.cannontech.stars.dr.jms.message;

import java.util.Date;

import com.cannontech.loadcontrol.service.data.ProgramStatusType;

public class DrProgramStatusJmsMessage extends DrJmsMessage {

    private static final long serialVersionUID = 1L;
    private Integer programGearHistId;
    private String programName;
    private ProgramStatusType programStatusType;
    private String gearName;
    private Date startDateTime;
    private Date stopDateTime;
    private Date gearChangeTime;

    public Integer getProgramGearHistId() {
        return programGearHistId;
    }

    public void setProgramGearHistId(Integer programGearHistId) {
        this.programGearHistId = programGearHistId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public ProgramStatusType getProgramStatusType() {
        return programStatusType;
    }

    public void setProgramStatusType(ProgramStatusType programStatusType) {
        this.programStatusType = programStatusType;
    }

    public String getGearName() {
        return gearName;
    }

    public void setGearName(String gearName) {
        this.gearName = gearName;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getStopDateTime() {
        return stopDateTime;
    }

    public void setStopDateTime(Date stopDateTime) {
        this.stopDateTime = stopDateTime;
    }

    public Date getGearChangeTime() {
        return gearChangeTime;
    }

    public void setGearChangeTime(Date gearChangeTime) {
        this.gearChangeTime = gearChangeTime;
    }

}
