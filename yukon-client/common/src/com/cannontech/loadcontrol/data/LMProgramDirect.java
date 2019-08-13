package com.cannontech.loadcontrol.data;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;

public class LMProgramDirect extends LMProgramBase implements IGearProgram
{
	private Integer currentGearNumber = null;
	private Integer lastGroupControlled = null;
	private GregorianCalendar directStartTime = null;
	private GregorianCalendar directStopTime = null;
	private GregorianCalendar notifyActiveTime = null;
	private GregorianCalendar notifyInactiveTime = null;	
	private GregorianCalendar startedRampingOut = null;
	private Integer triggerOffset = null;
	private Integer triggerRestoreOffset = null;	
	private boolean constraintOverride = false;	
    private String addtionalInfo = null;
    private String originSource;

    /**
	 * @return Returns the triggerOffset.
	 */
    public Integer getTriggerOffset() {
		return triggerOffset;
	}
	/**
	 * @param triggerOffset The triggerOffset to set.
	 */
	public void setTriggerOffset(Integer triggerOffset) {
		this.triggerOffset = triggerOffset;
	}
	/**
	 * @return Returns the triggerRestoreOffset.
	 */
	public Integer getTriggerRestoreOffset() {
		return triggerRestoreOffset;
	}
	/**
	 * @param triggerRestoreOffset The triggerRestoreOffset to set.
	 */
	public void setTriggerRestoreOffset(Integer triggerRestoreOffset) {
		this.triggerRestoreOffset = triggerRestoreOffset;
	}
	private List<LMProgramDirectGear> directGearVector = null;
	private Vector activeMasterProgramsVector = null;
	private Vector activeSubordinateProgramsVector = null;

    public LMProgramDirect()
    {
    	super();
    }

    public LMManualControlRequest createScheduledStartMsg(Date start,
            Date stop, int gearNumber, Date notifyTime, String additionalInfo,
            int constraintFlag, ProgramOriginSource programOriginSource) {
        LMManualControlRequest msg = new LMManualControlRequest();
        java.util.GregorianCalendar cStart = new java.util.GregorianCalendar();
        java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
        cStart.setTime(start);
        cStop.setTime(stop);

        msg.setStartTime(cStart);
        msg.setStopTime(cStop);
        msg.setStartGear(gearNumber);

        if (additionalInfo != null)
            msg.setAddditionalInfo(additionalInfo);

        msg.setYukonID(getYukonID().intValue());
        msg.setStartPriority(getStartPriority().intValue());

        msg.setCommand(LMManualControlRequest.SCHEDULED_START);
        msg.setConstraintFlag(constraintFlag);
        msg.setOriginSource(programOriginSource.getDatabaseRepresentation());
        return msg;
    }

    public LMManualControlRequest createScheduledStopMsg(Date start, Date stop,
            int gearNumber, String additionalInfo, ProgramOriginSource programOriginSource) {
        LMManualControlRequest msg = new LMManualControlRequest();
        java.util.GregorianCalendar cStart = new java.util.GregorianCalendar();
        java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
        cStart.setTime(start);
        cStop.setTime(stop);

        msg.setStartTime(cStart);
        msg.setStopTime(cStop);
        msg.setStartGear(gearNumber);

        if (additionalInfo != null)
            msg.setAddditionalInfo(additionalInfo);

        msg.setYukonID(getYukonID().intValue());
        msg.setStartPriority(getStartPriority().intValue());

        msg.setCommand(LMManualControlRequest.SCHEDULED_STOP);
        msg.setOriginSource(programOriginSource.getDatabaseRepresentation());
        return msg;
    }

    public LMManualControlRequest createStartStopNowMsg(Date stopTime,
            int gearNumber, String additionalInfo, boolean isStart,
            int constraintFlag, ProgramOriginSource programOriginSource) {
        LMManualControlRequest msg = new LMManualControlRequest();
        java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
        cStop.setTime(stopTime);

        msg.setStartGear(gearNumber);

        if (stopTime != null)
            msg.setStopTime(cStop);

        if (additionalInfo != null)
            msg.setAddditionalInfo(additionalInfo);

        msg.setYukonID(getYukonID().intValue());
        msg.setStartPriority(getStartPriority().intValue());

        if (isStart) {
            msg.setCommand(LMManualControlRequest.START_NOW);
            msg.setConstraintFlag(constraintFlag);
        } else
            msg.setCommand(LMManualControlRequest.STOP_NOW);
        msg.setOriginSource(programOriginSource.getDatabaseRepresentation());
        return msg;
    }

    public java.lang.Integer getCurrentGearNumber() {
        return currentGearNumber;
    }

    public LMProgramDirectGear getCurrentGear() {
        for (LMProgramDirectGear gear : directGearVector) {           
            if (currentGearNumber.equals(gear.getGearNumber())) {
                return gear;
            }
        }
        return null;
    }

    public List<LMProgramDirectGear> getDirectGearVector() {
        return directGearVector;
    }

    public java.util.GregorianCalendar getDirectStartTime() {
        return directStartTime;
    }

    public java.util.GregorianCalendar getDirectStopTime() {
        return directStopTime;
    }

    public java.lang.Integer getLastGroupControlled() {
        return lastGroupControlled;
    }

    public java.util.GregorianCalendar getStartTime() {
        return getDirectStartTime();
    }

    public java.util.GregorianCalendar getStopTime() {
        return getDirectStopTime();
    }

    public void setCurrentGearNumber(Integer newCurrentGearNumber) {
        currentGearNumber = newCurrentGearNumber;
    }

    public void setDirectGearVector(
            List<LMProgramDirectGear> newDirectGearVector) {
        directGearVector = newDirectGearVector;
    }

    public void setDirectStartTime(GregorianCalendar newDirectStartTime) {
    	directStartTime = newDirectStartTime;
    }

    public void setDirectStopTime(java.util.GregorianCalendar newDirectStopTime) {
    	directStopTime = newDirectStopTime;
    }

    public void setLastGroupControlled(Integer newLastGroupControlled) {
    	lastGroupControlled = newLastGroupControlled;
    }

    public java.util.GregorianCalendar getStartedRampingOut() {
		return startedRampingOut;
	}

	/**
	 * @param calendar
	 */
	public void setStartedRampingOut(GregorianCalendar calendar) {
		startedRampingOut = calendar;
	}

	/**
	 * @return Returns the activeMasterProgramsVector.
	 */
	public Vector getActiveMasterPrograms() {
		return activeMasterProgramsVector;
	}

	/**
	 * @param activeMasterProgramsVector The activeMasterProgramsVector to set.
	 */
	public void setActiveMasterPrograms(Vector activeMasterProgramsVector) {
		this.activeMasterProgramsVector = activeMasterProgramsVector;
	}

	/**
	 * @return Returns the activeSubordinateProgramsVector.
	 */
	public Vector getActiveSubordinatePrograms() {
		return activeSubordinateProgramsVector;
	}

	/**
	 * @param activeSubordinateProgramsVector The activeSubordinateProgramsVector to set.
	 */
    public void setActiveSubordinatePrograms(
            Vector activeSubordinateProgramsVector) {
        this.activeSubordinateProgramsVector = activeSubordinateProgramsVector;
    }

    public GregorianCalendar getNotifyActiveTime() {
        return notifyActiveTime;
    }

    public void setNotifyActiveTime(GregorianCalendar notifyActiveTime) {
        this.notifyActiveTime = notifyActiveTime;
    }

    public GregorianCalendar getNotifyInactiveTime() {
        return notifyInactiveTime;
    }

    public void setNotifyInactiveTime(GregorianCalendar notifyInactiveTime) {
        this.notifyInactiveTime = notifyInactiveTime;
    }

	public boolean isConstraintOverride() {
		return constraintOverride;
	}

	public void setConstraintOverride(boolean b) {
		constraintOverride = b;
	}
    public String getAddtionalInfo() {
        return addtionalInfo;
    }
    public void setAddtionalInfo(String addtionalInfo) {
        this.addtionalInfo = addtionalInfo;
    }

    @Override
    public String getOriginSource() {
        return originSource;
    }

    public void setOriginSource(String originSource) {
        this.originSource = originSource;
    }
}
