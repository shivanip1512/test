package com.cannontech.database.db.capcontrol;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlUtils;

@SuppressWarnings("serial")
public class CapBank extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private String operationalState = com.cannontech.database.data.capcontrol.CapBank.SWITCHED_OPSTATE;
	private String controllerType = StringUtils.EMPTY;
	private Integer controlDeviceID = new Integer(com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID);
	private Integer controlPointID = new Integer(com.cannontech.database.data.point.PointTypes.SYS_PID_SYSTEM);
	private Integer bankSize = new Integer(600);
	private String typeOfSwitch = StringUtils.EMPTY;
	private String switchManufacture = StringUtils.EMPTY;
	private String mapLocationID = "0";  //old integer default
	private Integer recloseDelay = new Integer(0);
	private Integer maxDailyOps = new Integer(0);
	private Character maxOpDisable = new Character('N');
	
    private Boolean customBankSize = Boolean.TRUE;
    private Integer bankSizeCustom = new Integer(0);
    
	public static final String SETTER_COLUMNS[] = 
	{ 
		"OperationalState", "ControllerType", "ControlDeviceID",
		"ControlPointID", "BankSize", "TypeOfSwitch",
		"SwitchManufacture", "MapLocationID", "RecloseDelay",
		"MaxDailyOps", "MaxOpDisable"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "CapBank";
public CapBank() {
	super();
}
public CapBank(Integer capBankID) {
	super();
	setDeviceID( capBankID );
}
public void add() throws java.sql.SQLException
{
	Object[] addValues = 
	{  
		getDeviceID(),
		getOperationalState(),
		SqlUtils.convertStringToDbValue(getControllerType()),
		getControlDeviceID(), 
		getControlPointID(),
		getBankSize(), 
		SqlUtils.convertStringToDbValue(getTypeOfSwitch()), 
		SqlUtils.convertStringToDbValue(getSwitchManufacture()),
		getMapLocationID(),
		getRecloseDelay(),
		getMaxDailyOps(),
		getMaxOpDisable()
	};

	add( TABLE_NAME, addValues );
}
public void delete() throws java.sql.SQLException {

	delete( TABLE_NAME, "DeviceID", getDeviceID() );
	
}
public java.lang.Integer getBankSize() {
    if(bankSizeCustom != null && bankSizeCustom != 0){
        bankSize = bankSizeCustom;
    }
	return bankSize;
}
public Integer getControlDeviceID() {
	return controlDeviceID;
}
public java.lang.String getControllerType() {
	return controllerType;
}
public Integer getControlPointID() {
	return controlPointID;
}
public Integer getDeviceID() {
	return deviceID;
}
public String getMapLocationID() {
	return mapLocationID;
}
public java.lang.String getOperationalState() {
	return operationalState;
}
public java.lang.String getSwitchManufacture() {
	return switchManufacture;
}
public java.lang.String getTypeOfSwitch() {
	return typeOfSwitch;
}

public Integer getRecloseDelay() {
	return recloseDelay;
}

public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setOperationalState( (String) results[0] );
		setControllerType( SqlUtils.convertDbValueToString((String) results[1]) );
		setControlDeviceID( (Integer) results[2] );
		setControlPointID( (Integer) results[3] );
		setBankSize( (Integer) results[4] );
		setTypeOfSwitch( SqlUtils.convertDbValueToString((String) results[5]) );
		setSwitchManufacture( SqlUtils.convertDbValueToString((String) results[6]) );
		setMapLocationID( (String) results[7] );
		setRecloseDelay( (Integer) results[8] );		
		setMaxDailyOps( (Integer) results[9] );
		setMaxOpDisable( new Character(results[10].toString().charAt(0)) );		
	}

}
public void setBankSize(java.lang.Integer newBankSize) {
	bankSize = newBankSize;
}
public void setControlDeviceID(Integer newValue) {
	this.controlDeviceID = newValue;
}
/**
 * If input is the string "(none)"
 * the controller type will be "" (empty string)
 */
public void setControllerType(java.lang.String newControllerType) {
    controllerType = com.cannontech.common.util.StringUtils.stripNone(newControllerType);
}
public void setControlPointID(Integer newValue) {
	this.controlPointID = newValue;
}
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
public void setMapLocationID(String newMapLocationID) {
	mapLocationID = newMapLocationID;
}
public void setOperationalState(java.lang.String newOperationalState) {
    if(!(newOperationalState.equalsIgnoreCase(com.cannontech.database.data.capcontrol.CapBank.SWITCHED_OPSTATE) ||
       newOperationalState.equalsIgnoreCase(com.cannontech.database.data.capcontrol.CapBank.STANDALONE_OPSTATE) ||
       newOperationalState.equalsIgnoreCase(com.cannontech.database.data.capcontrol.CapBank.UNINSTALLED_OPSTATE))) {
        operationalState = com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE;
    }else {
        operationalState = newOperationalState;
    }
}
/**
 * If input is the string "(none)"
 * the manufacture will be "" (empty string)
 */
public void setSwitchManufacture(java.lang.String newSwitchManufacture) {
	switchManufacture = com.cannontech.common.util.StringUtils.stripNone(newSwitchManufacture);
}
/**
 * If input is the string "(none)"
 * the type of switch will be "" (empty string)
 */
public void setTypeOfSwitch(java.lang.String newTypeOfSwitch) {
	typeOfSwitch = com.cannontech.common.util.StringUtils.stripNone(newTypeOfSwitch);
}

public void setRecloseDelay(Integer newValue) {
	recloseDelay = newValue;
}
public void update() throws java.sql.SQLException 
{

	Object setValues[]= 
	{  
		getOperationalState(),
		SqlUtils.convertStringToDbValue(getControllerType()),
		getControlDeviceID(), 
		getControlPointID(),
		getBankSize(), 
		SqlUtils.convertStringToDbValue(getTypeOfSwitch()), 
		SqlUtils.convertStringToDbValue(getSwitchManufacture()),
		getMapLocationID(),
		getRecloseDelay(),
		getMaxDailyOps(),
		getMaxOpDisable()
	};

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

	public Integer getMaxDailyOps() {
		return maxDailyOps;
	}

	public Character getMaxOpDisable() {
		return maxOpDisable;
	}

	public void setMaxDailyOps(Integer integer) {
		maxDailyOps = integer;
	}

	public void setMaxOpDisable(Character character) {
		maxOpDisable = character;
	}

	/**
	 * Boolean method for MaxOperations diable flag
	 */
	public boolean isMaxOperationDisabled() {
		return CtiUtilities.isTrue(getMaxOpDisable());
	}
	
	/**
	 * Boolean method for MaxOperations diable flag
	 */
	public void setMaxOperationDisabled( boolean val ) {
		setMaxOpDisable(
			(val ? CtiUtilities.trueChar : CtiUtilities.falseChar) );
	}
    public Boolean getCustomBankSize() {
        return customBankSize;
    }
    public void setCustomBankSize(Boolean customBankSize) {
        this.customBankSize = customBankSize;
    }
    public Integer getBankSizeCustom() {
        return bankSizeCustom;
    }
    public void setBankSizeCustom(Integer bankSizeCustom) {
        this.bankSizeCustom = bankSizeCustom;
    }

}
