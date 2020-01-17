package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;

/**
 * Insert the type's description here.
 * Creation date: (2/11/2002 10:36:09 AM)
 * @author: 
 */
public class RotationGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear 
{

/**
 * RotationGear constructor comment.
 */
public RotationGear() 
{
	super();

	setControlMethod( GearControlMethod.Rotation );
	setCycleRefreshRate( new Integer(0) );
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 */
@Override
public String getGroupSelectionMethod()
{
	return super.getGroupSelectionMethod();
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 */
public Integer getNumberOfGroups()
{
	return getMethodRateCount();
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public Integer getSendRate()
{
	return getMethodRate();
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public Integer getShedTime()
{
	return getMethodPeriod();
}

/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
@Override
public void setGroupSelectionMethod(String method) 
{
	super.setGroupSelectionMethod( method );
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public void setNumberOfGroups(Object value) 
{
	//value should either be String or Integer
	if( value instanceof Integer ) {
        setMethodRateCount( (Integer)value );
    } else {
        setMethodRateCount( new Integer(0) );
    }
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public void setSendRate(Integer seconds) 
{
	setMethodRate( seconds );
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public void setShedTime(Integer seconds) 
{
	setMethodPeriod( seconds );
}

@Override
public boolean useCustomDbRetrieve() {
    return false;
}
}
