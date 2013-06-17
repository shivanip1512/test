package com.cannontech.message.capcontrol.streamable;

import com.cannontech.util.CapControlConst;


/**
 * Insert the type's description here.
 * Creation date: (8/18/00 4:23:32 PM)
 * @author: 
 */
public class CapBankDevice extends StreamableCapObject
{
	
    private final static String[] IGNORE_REASON = {

    "LOCAL_CONTROL", "FAULT_CURRENT_CONTROL", "EMERGENCY_VOLTAGE",
            "TIME_SCHEDULE", "VOLTAGE", "DIGITAL1", "ANALOG1", "DIGITAL2",
            "ANALOG2", "DIGITAL3", "ANALOG3", "DIGITAL4", "TEMPERATURE",
            "REMOTE", "NEUTRAL_LOCKOUT", "BROWN_OUT", "BAD_ACTIVE_RELAY" };
    

	private Integer maxDailyOperation = null;
	private Boolean maxOperationDisableFlag = null;
	private Boolean maxDailyOperationHitFlag = null;
	private Boolean ovuvSituationFlag = null;
	private Integer controlStatusQuality = null;

	private Integer alarmInhibit = null;
	private Integer controlInhibit = null;
	private String operationalState = null;
	private String controllerType = null;
	private Integer controlDeviceID = null;
	private Integer bankSize = null;
	private String typeOfSwitch = null;
	private String switchManufacture = null;
	private String mapLocationID = null;
	private Float controlOrder = null;
	private Float tripOrder = null;
    private Float closeOrder = null;
    
	private Integer statusPointID = null;
	private Integer controlStatus = null;
	private Integer operationAnalogPointID = null;
	private Integer totalOperations = null;	
	private java.util.Date lastStatusChangeTime = null;
	private Integer tagControlStatus = null;
	
	private Integer recloseDelay = null;	
	private int origFeederID = 0;
	private Integer currentDailyOperations = null;	
	private Boolean ignoreFlag = null;
	private Integer ignoreReason = null;
    
    private Boolean ovUVDisabled = Boolean.TRUE;
    private String controlDeviceType = null;
    private String beforeVars = new String();
    private String afterVars = new String();
    private String percentChange = new String();
    private Boolean localControlFlag = Boolean.FALSE;
    private String partialPhaseInfo = new String();


	public String getPartialPhaseInfo() {
		return partialPhaseInfo;
	}
	public void setPartialPhaseInfo(String partialPhaseInfo) {
		this.partialPhaseInfo = partialPhaseInfo;
	}
	public Boolean getMaxDailyOperationHitFlag() {
        return maxDailyOperationHitFlag;
    }
    public void setMaxDailyOperationHitFlag(Boolean maxDailyOperationHitFlag) {
        this.maxDailyOperationHitFlag = maxDailyOperationHitFlag;
    }
    public Boolean getOvuvSituationFlag() {
        return ovuvSituationFlag;
    }
    public void setOvuvSituationFlag(Boolean ovuvSituationFlag) {
        this.ovuvSituationFlag = ovuvSituationFlag;
    }
    
    public Boolean getLocalControlFlag() {
        return localControlFlag;
    }
    public void setLocalControlFlag(Boolean localControlFlag) {
        this.localControlFlag = localControlFlag;
    }
    
    public Boolean getOvUVDisabled() {
        //here to fake the return
        //boolean retVal = ((Math.random() * 100) > 50) ?  true : false; 
        return ovUVDisabled;
    }
    public void setOvUVDisabled(Boolean ovUVDisabled) {
        this.ovUVDisabled = ovUVDisabled;
    }
    /**
	 * CapBankDevice constructor comment.
	 */
	public CapBankDevice() {
		super();
	}
	/**
	 * StreamableCapObject constructor comment.
	 */
	public CapBankDevice( Integer paoId_, String paoCategory_, String paoClass_,
					String paoName_, String paoType_, String paoDescription_, 
					Boolean paoDisableFlag_ )
	{
		super( paoId_, paoCategory_, paoClass_, paoName_, 
					paoType_, paoDescription_, paoDisableFlag_ );
	
	}
	
	public Boolean isIgnoreFlag() {
		return ignoreFlag;
	}
	public Integer getIgnoreReason() {
		return ignoreReason;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/10/2001 8:53:46 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getAlarmInhibit() {
		return alarmInhibit;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getBankSize() {
		return bankSize;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getControlDeviceID() {
		return controlDeviceID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (1/10/2001 8:53:46 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getControlInhibit() {
		return controlInhibit;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getControllerType() {
		return controllerType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Float getControlOrder() {
		return controlOrder;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/23/00 10:08:35 AM)
	 */
	public Integer getControlStatus() {
		return controlStatus;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getTotalOperations() {
		return totalOperations;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @return java.util.Date
	 */
	public java.util.Date getLastStatusChangeTime() {
		return lastStatusChangeTime;
	}
	/**
	 * Insert the method's description here.
	 *
	 */
	public String getMapLocationID() {
		return mapLocationID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getOperationalState() {
		return operationalState;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/23/00 10:08:35 AM)
	 */
	public Integer getOperationAnalogPointID() {
		return operationAnalogPointID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/23/00 10:08:35 AM)
	 */
	public Integer getStatusPointID() {
		return statusPointID;
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getSwitchManufacture() {
		return switchManufacture;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getTagControlStatus() {
		return tagControlStatus;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getTypeOfSwitch() {
		return typeOfSwitch;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (1/2/2001 1:57:19 PM)
	 */
	public static boolean isInAnyCloseState( CapBankDevice capBank)
	{
		if( capBank != null )
			return isStatusClosed( capBank.getControlStatus().intValue() );
	
		return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (1/2/2001 1:57:19 PM)
	 */
	public static boolean isInAnyOpenState( CapBankDevice capBank)
	{
		if( capBank != null )
		{
			if( capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN ||
				 capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN_PENDING ||
				 capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN_FAIL ||
				 capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN_QUESTIONABLE ) 
			{
				return true;
			}
		}
	
		return false;
	}
	public void setIgnoreFlag(Boolean ignoreFlag) {
		this.ignoreFlag = ignoreFlag;
	}
	public void setIgnoreReason(Integer ignoreReason) {
		this.ignoreReason = ignoreReason;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/4/2001 12:21:38 PM)
	 * @return boolean
	 * @param status int
	 */
	public static boolean isStatusClosed(int status) 
	{
		if( status == CapControlConst.BANK_CLOSE || status == CapControlConst.BANK_CLOSE_FAIL
			 || status == CapControlConst.BANK_CLOSE_PENDING || status == CapControlConst.BANK_CLOSE_QUESTIONABLE ) 
		{
			return true;
		}
		else
			return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (1/10/2001 8:53:46 AM)
	 * @param newAlarmInhibit java.lang.Integer
	 */
	public void setAlarmInhibit(java.lang.Integer newAlarmInhibit) {
		alarmInhibit = newAlarmInhibit;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @param newBankSize java.lang.Integer
	 */
	public void setBankSize(java.lang.Integer newBankSize) {
		bankSize = newBankSize;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @param newControlDeviceID java.lang.Integer
	 */
	public void setControlDeviceID(java.lang.Integer newControlDeviceID) {
		controlDeviceID = newControlDeviceID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (1/10/2001 8:53:46 AM)
	 * @param newControlInhibit java.lang.Integer
	 */
	public void setControlInhibit(java.lang.Integer newControlInhibit) {
		controlInhibit = newControlInhibit;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @param newControllerType java.lang.String
	 */
	public void setControllerType(java.lang.String newControllerType) {
		controllerType = newControllerType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @param newControlOrder java.lang.Integer
	 */
	public void setControlOrder(java.lang.Float newControlOrder) {
		controlOrder = newControlOrder;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/23/00 10:08:35 AM)
	 */
	public void setControlStatus(Integer newValue) {
		controlStatus = newValue;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @param newCurrentDailyOperations java.lang.Integer
	 */
	public void setTotalOperations(java.lang.Integer newCurrentDailyOperations) {
		totalOperations = newCurrentDailyOperations;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @param newLastStatusChangeTime java.util.Date
	 */
	public void setLastStatusChangeTime(java.util.Date newLastStatusChangeTime) {
		lastStatusChangeTime = newLastStatusChangeTime;
	}
	/**
	 * Insert the method's description here.
	 *
	 */
	public void setMapLocationID(String newMapLocationID) {
		mapLocationID = newMapLocationID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @param newOperationalState java.lang.String
	 */
	public void setOperationalState(java.lang.String newOperationalState) {
		operationalState = newOperationalState;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/23/00 10:08:35 AM)
	 */
	public void setOperationAnalogPointID(Integer newValue) {
		operationAnalogPointID = newValue;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/23/00 10:08:35 AM)
	 */
	public void setStatusPointID(Integer newValue) {
		statusPointID = newValue;
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @param newSwitchManufacture java.lang.String
	 */
	public void setSwitchManufacture(java.lang.String newSwitchManufacture) {
		switchManufacture = newSwitchManufacture;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @param newTagControlStatus java.lang.Integer
	 */
	public void setTagControlStatus(java.lang.Integer newTagControlStatus) {
		tagControlStatus = newTagControlStatus;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @param newTypeOfSwitch java.lang.String
	 */
	public void setTypeOfSwitch(java.lang.String newTypeOfSwitch) {
		typeOfSwitch = newTypeOfSwitch;
	}

	/**
	 * Returns the origFeederID.
	 * @return int
	 */
	public int getOrigFeederID() {
		return origFeederID;
	}


	/**
	 * Sets the origFeederID.
	 * @param origFeederID The origFeederID to set
	 */
	public void setOrigFeederID(int origFeederID) {
		this.origFeederID = origFeederID;
	}


	public boolean isBankMoved()
	{
		return getOrigFeederID() != 0;
	}
	
	/**
	 * @return
	 */
	public Integer getRecloseDelay()
	{
		return recloseDelay;
	}

	/**
	 * @param integer
	 */
	public void setRecloseDelay(Integer integer)
	{
		recloseDelay = integer;
	}

	/**
	 * @return
	 */
	public Integer getMaxDailyOperation() {
		return maxDailyOperation;
	}

	/**
	 * @return
	 */
	public Boolean getMaxOperationDisableFlag() {
		return maxOperationDisableFlag;
	}

	/**
	 * @param integer
	 */
	public void setMaxDailyOperation(Integer integer) {
		maxDailyOperation = integer;
	}

	/**
	 * @param boolean1
	 */
	public void setMaxOperationDisableFlag(Boolean boolean1) {
		maxOperationDisableFlag = boolean1;
	}

	/**
	 * @return
	 */
	public Integer getCurrentDailyOperations() {
		return currentDailyOperations;
	}

	/**
	 * @param integer
	 */
	public void setCurrentDailyOperations(Integer integer) {
		currentDailyOperations = integer;
	}
    
    public static String getIgnoreReason (int idx) {
        return IGNORE_REASON[idx];
    }
    public Float getTripOrder() {
        return tripOrder;
    }
    public void setTripOrder(Float tripOrder) {
        this.tripOrder = tripOrder;
    }
    public Float getCloseOrder() {
        return closeOrder;
    }
    public void setCloseOrder(Float closeOrder) {
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
	public Integer getControlStatusQuality() {
		return controlStatusQuality;
	}
	public void setControlStatusQuality(Integer controlStatusQuality) {
		this.controlStatusQuality = controlStatusQuality;
	}
	
	public String getControlStatusQualityString(){
		String retVal = "";
		switch (getControlStatusQuality())
		{
			case CapControlConst.CC_PARTIAL_QUAL:{
				retVal = "-P";
				break;
			}
			case CapControlConst.CC_SIGNIFICANT_QUAL:{
				retVal = "-S";
				break;
			}
			case CapControlConst.CC_ABNORMAL_QUAL:{
				retVal = "-Q";
			    break;
			}
			case CapControlConst.CC_UNSOLICITED_QUAL:{ 
			    retVal = "-U";
			    break;
			}
			case CapControlConst.CC_COMMFAIL_QUAL:{
                retVal = "-CF";
                break;
            }
			case CapControlConst.CC_FAIL_QUAL:				
			case CapControlConst.CC_NO_CONTROL_QUAL:
		    case CapControlConst.CC_NORMAL_QUAL:
			default:
				break;
		}
		if( getPartialPhaseInfo().compareTo("(none)") != 0) {
			retVal = "-"+getPartialPhaseInfo();
		}
		
		return retVal;
		
	}
}