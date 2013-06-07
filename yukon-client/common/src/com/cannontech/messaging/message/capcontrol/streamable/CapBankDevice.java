package com.cannontech.messaging.message.capcontrol.streamable;

import com.cannontech.util.CapControlConst;

public class CapBankDevice extends StreamableCapObject {

    private final static String[] IGNORE_REASON = { "LOCAL_CONTROL", "FAULT_CURRENT_CONTROL", "EMERGENCY_VOLTAGE",
                                                   "TIME_SCHEDULE", "VOLTAGE", "DIGITAL1", "ANALOG1", "DIGITAL2",
                                                   "ANALOG2", "DIGITAL3", "ANALOG3", "DIGITAL4", "TEMPERATURE",
                                                   "REMOTE", "NEUTRAL_LOCKOUT", "BROWN_OUT", "BAD_ACTIVE_RELAY" };

    private int maxDailyOperation;
    private boolean maxOperationDisableFlag;
    private boolean maxDailyOperationHitFlag;
    private boolean ovuvSituationFlag;
    private int controlStatusQuality;

    private boolean alarmInhibit;
    private boolean controlInhibit;
    private String operationalState = null;
    private String controllerType = null;
    private int controlDeviceId;
    private int bankSize;
    private String typeOfSwitch = null;
    private String switchManufacture = null;
    private String mapLocationId = null;
    private float controlOrder;
    private float tripOrder;
    private float closeOrder;

    private int statusPointId;
    private int controlStatus;
    private int operationAnalogPointId;
    private int totalOperations;
    private java.util.Date lastStatusChangeTime = null;
    private int tagControlStatus;

    private int recloseDelay;
    private int origFeederId = 0;
    private int currentDailyOperations;
    private boolean ignoreFlag;
    private int ignoreReason;

    private boolean ovUVDisabled = true;
    private String controlDeviceType = null;
    private String beforeVars = new String();
    private String afterVars = new String();
    private String percentChange = new String();
    private boolean localControlFlag = false;
    private String partialPhaseInfo = new String();

    public CapBankDevice() {
        super();
    }

    public CapBankDevice(int paoId_, String paoCategory_, String paoClass_, String paoName_, String paoType_,
                         String paoDescription_, boolean paoDisableFlag_) {
        super(paoId_, paoCategory_, paoClass_, paoName_, paoType_, paoDescription_, paoDisableFlag_);
    }

    public String getPartialPhaseInfo() {
        return partialPhaseInfo;
    }

    public void setPartialPhaseInfo(String partialPhaseInfo) {
        this.partialPhaseInfo = partialPhaseInfo;
    }

    public boolean getMaxDailyOperationHitFlag() {
        return maxDailyOperationHitFlag;
    }

    public void setMaxDailyOperationHitFlag(boolean maxDailyOperationHitFlag) {
        this.maxDailyOperationHitFlag = maxDailyOperationHitFlag;
    }

    public boolean getOvuvSituationFlag() {
        return ovuvSituationFlag;
    }

    public void setOvuvSituationFlag(boolean ovuvSituationFlag) {
        this.ovuvSituationFlag = ovuvSituationFlag;
    }

    public boolean getLocalControlFlag() {
        return localControlFlag;
    }

    public void setLocalControlFlag(boolean localControlFlag) {
        this.localControlFlag = localControlFlag;
    }

    public boolean getOvUVDisabled() {
        // here to fake the return
        // boolean retVal = ((Math.random() * 100) > 50) ? true : false;
        return ovUVDisabled;
    }

    public void setOvUVDisabled(boolean ovUVDisabled) {
        this.ovUVDisabled = ovUVDisabled;
    }

    public boolean isIgnoreFlag() {
        return ignoreFlag;
    }

    public int getIgnoreReason() {
        return ignoreReason;
    }

    public boolean isAlarmInhibit() {
        return alarmInhibit;
    }

    public int getBankSize() {
        return bankSize;
    }

    public int getControlDeviceId() {
        return controlDeviceId;
    }

    public boolean isControlInhibit() {
        return controlInhibit;
    }

    public java.lang.String getControllerType() {
        return controllerType;
    }

    public float getControlOrder() {
        return controlOrder;
    }

    public int getControlStatus() {
        return controlStatus;
    }

    public int getTotalOperations() {
        return totalOperations;
    }

    public java.util.Date getLastStatusChangeTime() {
        return lastStatusChangeTime;
    }

    public String getMapLocationId() {
        return mapLocationId;
    }

    public java.lang.String getOperationalState() {
        return operationalState;
    }

    public int getOperationAnalogPointId() {
        return operationAnalogPointId;
    }

    public int getStatusPointId() {
        return statusPointId;
    }

    public java.lang.String getSwitchManufacture() {
        return switchManufacture;
    }

    public int getTagControlStatus() {
        return tagControlStatus;
    }

    public java.lang.String getTypeOfSwitch() {
        return typeOfSwitch;
    }

    public static boolean isInAnyCloseState(CapBankDevice capBank) {
        if (capBank != null)
            return isStatusClosed(capBank.getControlStatus());

        return false;
    }

    public static boolean isInAnyOpenState(CapBankDevice capBank) {
        if (capBank != null) {
            if (capBank.getControlStatus() == CapControlConst.BANK_OPEN ||
                capBank.getControlStatus() == CapControlConst.BANK_OPEN_PENDING ||
                capBank.getControlStatus() == CapControlConst.BANK_OPEN_FAIL ||
                capBank.getControlStatus() == CapControlConst.BANK_OPEN_QUESTIONABLE) {
                return true;
            }
        }

        return false;
    }

    public void setIgnoreFlag(boolean ignoreFlag) {
        this.ignoreFlag = ignoreFlag;
    }

    public void setIgnoreReason(int ignoreReason) {
        this.ignoreReason = ignoreReason;
    }

    public static boolean isStatusClosed(int status) {
        if (status == CapControlConst.BANK_CLOSE || status == CapControlConst.BANK_CLOSE_FAIL ||
            status == CapControlConst.BANK_CLOSE_PENDING || status == CapControlConst.BANK_CLOSE_QUESTIONABLE) {
            return true;
        }
        else
            return false;
    }

    public void setAlarmInhibit(boolean newAlarmInhibit) {
        alarmInhibit = newAlarmInhibit;
    }

    public void setBankSize(int newBankSize) {
        bankSize = newBankSize;
    }

    public void setControlDeviceId(int newControlDeviceId) {
        controlDeviceId = newControlDeviceId;
    }

    public void setControlInhibit(boolean newControlInhibit) {
        controlInhibit = newControlInhibit;
    }

    public void setControllerType(java.lang.String newControllerType) {
        controllerType = newControllerType;
    }

    public void setControlOrder(float newControlOrder) {
        controlOrder = newControlOrder;
    }

    public void setControlStatus(int newValue) {
        controlStatus = newValue;
    }

    public void setTotalOperations(int newCurrentDailyOperations) {
        totalOperations = newCurrentDailyOperations;
    }

    public void setLastStatusChangeTime(java.util.Date newLastStatusChangeTime) {
        lastStatusChangeTime = newLastStatusChangeTime;
    }

    public void setMapLocationId(String newMapLocationId) {
        mapLocationId = newMapLocationId;
    }

    public void setOperationalState(java.lang.String newOperationalState) {
        operationalState = newOperationalState;
    }

    public void setOperationAnalogPointId(int newValue) {
        operationAnalogPointId = newValue;
    }

    public void setStatusPointId(int newValue) {
        statusPointId = newValue;
    }

    public void setSwitchManufacture(java.lang.String newSwitchManufacture) {
        switchManufacture = newSwitchManufacture;
    }

    public void setTagControlStatus(int newTagControlStatus) {
        tagControlStatus = newTagControlStatus;
    }

    public void setTypeOfSwitch(java.lang.String newTypeOfSwitch) {
        typeOfSwitch = newTypeOfSwitch;
    }

    /**
     * Returns the origFeederId.
     * @return int
     */
    public int getOrigFeederId() {
        return origFeederId;
    }

    /**
     * Sets the origFeederId.
     * @param origFeederId The origFeederId to set
     */
    public void setOrigFeederId(int origFeederId) {
        this.origFeederId = origFeederId;
    }

    public boolean isBankMoved() {
        return getOrigFeederId() != 0;
    }

    public int getRecloseDelay() {
        return recloseDelay;
    }

    public void setRecloseDelay(int integer) {
        recloseDelay = integer;
    }

    public int getMaxDailyOperation() {
        return maxDailyOperation;
    }

    public boolean getMaxOperationDisableFlag() {
        return maxOperationDisableFlag;
    }

    public void setMaxDailyOperation(int integer) {
        maxDailyOperation = integer;
    }

    public void setMaxOperationDisableFlag(boolean boolean1) {
        maxOperationDisableFlag = boolean1;
    }

    public int getCurrentDailyOperations() {
        return currentDailyOperations;
    }

    public void setCurrentDailyOperations(int integer) {
        currentDailyOperations = integer;
    }

    public static String getIgnoreReason(int idx) {
        return IGNORE_REASON[idx];
    }

    public float getTripOrder() {
        return tripOrder;
    }

    public void setTripOrder(float tripOrder) {
        this.tripOrder = tripOrder;
    }

    public float getCloseOrder() {
        return closeOrder;
    }

    public void setCloseOrder(float closeOrder) {
        this.closeOrder = closeOrder;
    }

    public String getControlDeviceType() {
        return controlDeviceType;
    }

    public void setControlDeviceType(String controlDeviceType) {
        this.controlDeviceType = controlDeviceType;
    }

    public String getBeforeVars() {
        return beforeVars;
    }

    public void setBeforeVars(String beforeVars) {
        this.beforeVars = beforeVars;
    }

    public String getAfterVars() {
        return afterVars;
    }

    public void setAfterVars(String afterVars) {
        this.afterVars = afterVars;
    }

    public String getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(String percentChange) {
        this.percentChange = percentChange;
    }

    public int getControlStatusQuality() {
        return controlStatusQuality;
    }

    public void setControlStatusQuality(int controlStatusQuality) {
        this.controlStatusQuality = controlStatusQuality;
    }

    public String getControlStatusQualityString() {
        String retVal = "";
        switch (getControlStatusQuality()) {
            case CapControlConst.CC_PARTIAL_QUAL: {
                retVal = "-P";
                break;
            }
            case CapControlConst.CC_SIGNIFICANT_QUAL: {
                retVal = "-S";
                break;
            }
            case CapControlConst.CC_ABNORMAL_QUAL: {
                retVal = "-Q";
                break;
            }
            case CapControlConst.CC_UNSOLICITED_QUAL: {
                retVal = "-U";
                break;
            }
            case CapControlConst.CC_COMMFAIL_QUAL: {
                retVal = "-CF";
                break;
            }
            case CapControlConst.CC_FAIL_QUAL:
            case CapControlConst.CC_NO_CONTROL_QUAL:
            case CapControlConst.CC_NORMAL_QUAL:
            default:
                break;
        }

        if (getPartialPhaseInfo().compareTo("(none)") != 0) {
            retVal = "-" + getPartialPhaseInfo();
        }

        return retVal;
    }
}
