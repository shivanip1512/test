package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;

/**
 * Insert the type's description here.
 * Creation date: (2/11/2002 10:36:09 AM)
 * @author: 
 */
public class LatchingGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear {
/**
 * TimerRefreshGear constructor comment.
 */
public LatchingGear() 
{
	super();

	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param l String
 */
public Integer getStartControlState()
{
	return getMethodRateCount();
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:37:20 AM)
 */
private void initialize() 
{
	setControlMethod( GearControlMethod.Latching );
	setCycleRefreshRate( new Integer(0) );
	setMethodRateCount( new Integer(0) );
	setMethodPeriod( new Integer(0) );
	setMethodRate( new Integer(0) );
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param l String
 */
public void setStartControlState(Integer state) 
{
	setMethodRateCount( state );
}

@Override
public boolean useCustomDbRetrieve() {
    return false;
}
}
