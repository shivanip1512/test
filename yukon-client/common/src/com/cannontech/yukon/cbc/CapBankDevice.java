package com.cannontech.yukon.cbc;

/**
 * Insert the type's description here.
 * Creation date: (8/18/00 4:23:32 PM)
 * @author: 
 */
public class CapBankDevice extends StreamableCapObject
{
	private Integer alarmInhibit = null;
	private Integer controlInhibit = null;
	private String operationalState = null;
	private String controllerType = null;
	private Integer controlDeviceID = null;
	private Integer controlPointID = null;
	private Integer bankSize = null;
	private String typeOfSwitch = null;
	private String switchManufacture = null;
	private Integer mapLocationID = null;
	private Integer controlOrder = null;

	private Integer statusPointID = null;
	private Integer controlStatus = null;
	private Integer operationAnalogPointID = null;
	private Integer currentDailyOperations = null;	
	private java.util.Date lastStatusChangeTime = null;
	private Integer tagControlStatus = null;
	
	private Integer recloseDelay = null;
	
	private int origFeederID = 0;

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
	public java.lang.Integer getControlOrder() {
		return controlOrder;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getControlPointID() {
		return controlPointID;
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
	public java.lang.Integer getCurrentDailyOperations() {
		return currentDailyOperations;
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
	 * Creation date: (12/5/2001 2:21:14 PM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getMapLocationID() {
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
	public void setControlOrder(java.lang.Integer newControlOrder) {
		controlOrder = newControlOrder;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/19/2001 11:45:37 AM)
	 * @param newControlPointID java.lang.Integer
	 */
	public void setControlPointID(java.lang.Integer newControlPointID) {
		controlPointID = newControlPointID;
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
	public void setCurrentDailyOperations(java.lang.Integer newCurrentDailyOperations) {
		currentDailyOperations = newCurrentDailyOperations;
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
	 * Creation date: (12/5/2001 2:21:14 PM)
	 * @param newMapLocationID java.lang.Integer
	 */
	public void setMapLocationID(java.lang.Integer newMapLocationID) {
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

}
