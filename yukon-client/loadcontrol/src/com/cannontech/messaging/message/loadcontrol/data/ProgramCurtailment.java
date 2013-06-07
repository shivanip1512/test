package com.cannontech.messaging.message.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;

public class ProgramCurtailment extends Program{

    public static String STATUS_NULL = "Null";
    public static String STATUS_SCHEDULED = "Scheduled";
    public static String STATUS_NOTIFIED = "Notified";
    public static String STATUS_CANCELED = "Canceled";
    public static String STATUS_ACTIVE = "Active";
    public static String STATUS_STOP_EARLY = "StopEarly";
    public static String STATUS_COMPLETED = "Completed";

    private int minNotifyTime;
    private String heading = null;
    private String messageHeader = null;
    private String messageFooter = null;
    private int ackTimeLimit;
    private String canceledMsg = null;
    private String stoppedEarlyMsg = null;
    private int curtailReferenceId;
    private java.util.GregorianCalendar actionDateTime = null;
    private java.util.GregorianCalendar notificationDateTime = null;
    private java.util.GregorianCalendar curtailmentStartTime = null;
    private java.util.GregorianCalendar curtailmentStopTime = null;
    private String runStatus = null;
    private String additionalInfo = null;

    @Override
    public ManualControlRequestMessage createScheduledStartMsg(java.util.Date start, java.util.Date stop,
        int gearNumber, java.util.Date notifyTime, String additionalInfo, int constraintFlag) {
        ManualControlRequestMessage msg = new ManualControlRequestMessage();
        java.util.GregorianCalendar cStart = new java.util.GregorianCalendar();
        cStart.setTime(start);

        java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
        cStop.setTime(stop);

        java.util.GregorianCalendar cNotif = new java.util.GregorianCalendar();
        cNotif.setTime((notifyTime == null ? new java.util.Date() : notifyTime));

        msg.setStartTime(cStart);
        msg.setStopTime(cStop);
        msg.setNotifyTime(cNotif);

        if (additionalInfo != null)
            msg.setAddditionalInfo(additionalInfo);

        msg.setCommand(com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage.SCHEDULED_START);
        msg.setStartPriority(getStartPriority().intValue());

        msg.setYukonId(getYukonId().intValue());
        msg.setConstraintFlag(constraintFlag);

        return msg;
    }

    @Override
    public ManualControlRequestMessage createScheduledStopMsg(java.util.Date start, java.util.Date stop,
        int gearNumber, String additionalInfo) {
        com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage msg =
            new com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage();
        java.util.GregorianCalendar cStart = new java.util.GregorianCalendar();
        cStart.setTime(start);
        java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
        cStop.setTime(stop);

        msg.setStartTime(cStart);
        msg.setStopTime(cStop);

        if (additionalInfo != null)
            msg.setAddditionalInfo(additionalInfo);

        msg.setCommand(com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage.SCHEDULED_STOP);
        msg.setStartPriority(getStartPriority().intValue());

        msg.setYukonId(getYukonId().intValue());

        return msg;
    }

    @Override
    public com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage createStartStopNowMsg(
        java.util.Date stopTime, int gearNumber, String additionalInfo, boolean isStart, int constraintFlag) {
        com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage msg =
            new com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage();
        java.util.GregorianCalendar cStop = new java.util.GregorianCalendar();
        cStop.setTime(stopTime);

        msg.setStartGear(gearNumber);

        if (stopTime != null)
            msg.setStopTime(cStop);

        if (additionalInfo != null)
            msg.setAddditionalInfo(additionalInfo);

        msg.setYukonId(getYukonId().intValue());
        msg.setStartPriority(getStartPriority().intValue());

        if (isStart) {
            msg.setCommand(com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage.START_NOW);
            msg.setConstraintFlag(constraintFlag);
        }
        else
            msg.setCommand(com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage.STOP_NOW);

        return msg;
    }

    public int getAckTimeLimit() {
        return ackTimeLimit;
    }

    public java.util.GregorianCalendar getActionDateTime() {
        return actionDateTime;
    }

    public java.lang.String getAdditionalInfo() {
        return additionalInfo;
    }

    public java.lang.String getCanceledMsg() {
        return canceledMsg;
    }

    public java.util.GregorianCalendar getCurtailmentStartTime() {
        return curtailmentStartTime;
    }

    public java.util.GregorianCalendar getCurtailmentStopTime() {
        return curtailmentStopTime;
    }

    public int getCurtailReferenceId() {
        return curtailReferenceId;
    }

    public int getDuration() {
        return (int) ((getCurtailmentStopTime().getTime().getTime() - getCurtailmentStartTime().getTime().getTime()) / (60L * 1000L));
    }

    public java.lang.String getHeading() {
        return heading;
    }

    public java.lang.String getMessageFooter() {
        return messageFooter;
    }

    public java.lang.String getMessageHeader() {
        return messageHeader;
    }

    public int getMinNotifyTime() {
        return minNotifyTime;
    }

    public java.util.GregorianCalendar getNotificationDateTime() {
        return notificationDateTime;
    }

    public java.lang.String getRunStatus() {
        return runStatus;
    }

    public java.util.GregorianCalendar getStartTime() {
        return getCurtailmentStartTime();
    }

    // Overrides our supers getStoppedControlling()
    @Override
    public java.util.GregorianCalendar getStoppedControlling() {
        return getCurtailmentStopTime();
    }

    public java.lang.String getStoppedEarlyMsg() {
        return stoppedEarlyMsg;
    }

    public java.util.GregorianCalendar getStopTime() {
        return getCurtailmentStopTime();
    }

    public void setAckTimeLimit(int newAckTimeLimit) {
        ackTimeLimit = newAckTimeLimit;
    }

    public void setActionDateTime(java.util.GregorianCalendar newActionDateTime) {
        actionDateTime = newActionDateTime;
    }

    public void setAdditionalInfo(java.lang.String newAdditionalInfo) {
        additionalInfo = newAdditionalInfo;
    }

    public void setCanceledMsg(java.lang.String newCanceledMsg) {
        canceledMsg = newCanceledMsg;
    }

    public void setCurtailmentStartTime(java.util.GregorianCalendar newCurtailmentStartTime) {
        curtailmentStartTime = newCurtailmentStartTime;
    }

    public void setCurtailmentStopTime(java.util.GregorianCalendar newCurtailmentStopTime) {
        curtailmentStopTime = newCurtailmentStopTime;
    }

    public void setCurtailReferenceId(int newCurtailReferenceId) {
        curtailReferenceId = newCurtailReferenceId;
    }

    public void setHeading(java.lang.String newHeading) {
        heading = newHeading;
    }

    public void setMessageFooter(java.lang.String newMessageFooter) {
        messageFooter = newMessageFooter;
    }

    public void setMessageHeader(java.lang.String newMessageHeader) {
        messageHeader = newMessageHeader;
    }

    public void setMinNotifyTime(int newMinNotifyTime) {
        minNotifyTime = newMinNotifyTime;
    }

    public void setNotificationDateTime(java.util.GregorianCalendar newNotificationDateTime) {
        notificationDateTime = newNotificationDateTime;
    }

    public void setRunStatus(java.lang.String newRunStatus) {
        runStatus = newRunStatus;
    }

    public void setStoppedEarlyMsg(java.lang.String newStoppedEarlyMsg) {
        stoppedEarlyMsg = newStoppedEarlyMsg;
    }    
}
