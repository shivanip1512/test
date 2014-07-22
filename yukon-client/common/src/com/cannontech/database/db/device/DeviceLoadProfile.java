package com.cannontech.database.db.device;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created by Cannon Technologies Inc.
 */
public class DeviceLoadProfile extends com.cannontech.database.db.DBPersistent
{
	private Integer deviceID = null;
	private Integer lastIntervalDemandRate = new Integer(300);	// demand averaged over an hour 
	private Integer loadProfileDemandRate = new Integer(3600);	//rate at which load profile is stored.
	private String loadProfileCollection = "NNNN";
	private Integer voltageDmdInterval = new Integer(60);	// voltage averaged over an hour
	private Integer voltageDmdRate = new Integer(300);	// voltage "profile". Rate at which profile is stored.


	public static final String SETTER_COLUMNS[] = 
	{ 
		"LastIntervalDemandRate", "LoadProfileDemandRate",
		"LoadProfileCollection", "VoltageDmdInterval", "VoltageDmdRate"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
   
	public static final String TABLE_NAME = "DeviceLoadProfile";
         
/**
 * DeviceLoadProfile constructor comment.
 */
public DeviceLoadProfile() {
	super();
}

/**
 * add method comment.
 */
@Override
public void add() throws java.sql.SQLException {

	Object addValues[] =
	{
		getDeviceID(), getLastIntervalDemandRate(), getLoadProfileDemandRate(),
		getLoadProfileCollection(), getVoltageDmdInterval(), getVoltageDmdRate()
	};

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
@Override
public void delete() throws java.sql.SQLException {

	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getLastIntervalDemandRate() {
	return lastIntervalDemandRate;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public String getLoadProfileCollection() {
	return loadProfileCollection;
}

public boolean loadProfileIsOnForChannel(int channel){
    
    boolean isOn = false;
    
    if(channel >=1 && channel <=4){
        
        String lpValue = String.valueOf(loadProfileCollection.charAt(channel - 1));
        if(lpValue.equalsIgnoreCase("Y")){
            isOn = true;
        }
    }

    return isOn;
}

public void setLoadProfileIsOnForChannel(int channel, boolean isOn){
    
    char[] loadProfileCollectionArray = loadProfileCollection.toCharArray();
    
    loadProfileCollectionArray[channel - 1] = isOn ? 'Y':'N';
    
    setLoadProfileCollection(new String(loadProfileCollectionArray));
    
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getLoadProfileDemandRate() {
	return loadProfileDemandRate;
}

/**
 * retrieve method comment.
 */
@Override
public void retrieve() throws java.sql.SQLException {
    Object constraintValues[] = { getDeviceID() };

    Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

    // load up data stored in standard database table
    if (results.length == SETTER_COLUMNS.length) {
        setLastIntervalDemandRate((Integer) results[0]);
        setLoadProfileDemandRate((Integer) results[1]);
        setLoadProfileCollection((String) results[2]);
        setVoltageDmdInterval((Integer) results[3]);
        setVoltageDmdRate((Integer) results[4]);
    }
    
    PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
    YukonPao yukonPao = paoDao.getYukonPao(getDeviceID());

    // If MCT430, demand rate data is actually stored in DyanmicPaoInfo instead. So attempt to load that and override previous entries
    if (DeviceTypesFuncs.isMCT430(yukonPao.getPaoIdentifier().getPaoType())) {
        Object demandRateResult[] = retrieve(new String[] {"Value"},
                                             "DynamicPaoInfo",
                                             new String[] {"PAObjectID", "InfoKey", "Owner"},
                                             new Object[] {getDeviceID(), "mct ied load profile rate", "scanner"});
        
        if (demandRateResult.length == 1) {
            setLoadProfileDemandRate(Integer.parseInt(demandRateResult[0].toString()));
            setVoltageDmdRate(Integer.parseInt(demandRateResult[0].toString()));
        } else {
            setLoadProfileDemandRate(0);
            setVoltageDmdRate(0);
        }
    }
}

/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setLastIntervalDemandRate(Integer newValue) {
	this.lastIntervalDemandRate = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setLoadProfileCollection(String newValue) {
	this.loadProfileCollection = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setLoadProfileDemandRate(Integer newValue) {
	this.loadProfileDemandRate = newValue;
}
/**
 * update method comment.
 */
@Override
public void update() throws java.sql.SQLException
{
	Object setValues[] =
	{ 
		getLastIntervalDemandRate(), getLoadProfileDemandRate(),
		getLoadProfileCollection(), getVoltageDmdInterval(), getVoltageDmdRate()
	};

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	/**
	 * @return
	 */
	public Integer getVoltageDmdInterval()
	{
		return voltageDmdInterval;
	}

	/**
	 * @return
	 */
	public Integer getVoltageDmdRate()
	{
		return voltageDmdRate;
	}

	/**
	 * @param integer
	 */
	public void setVoltageDmdInterval(Integer integer)
	{
		voltageDmdInterval = integer;
	}

	/**
	 * @param integer
	 */
	public void setVoltageDmdRate(Integer integer)
	{
		voltageDmdRate = integer;
	}


}
