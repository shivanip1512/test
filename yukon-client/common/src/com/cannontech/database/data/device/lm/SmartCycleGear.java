package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;

/**
 * Insert the type's description here.
 * Creation date: (2/11/2002 10:36:09 AM)
 * @author: 
 */
public class SmartCycleGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear {
/**
 * TimerRefreshGear constructor comment.
 */
public SmartCycleGear() 
{
	super();
	setControlMethod( GearControlMethod.SmartCycle );
}

/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public Integer getControlPercent()
{
	return getMethodRate();
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public Integer getCyclePeriodLength()
{
	return getMethodPeriod();
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public Integer getResendRate()
{
	return getCycleRefreshRate();
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public Integer getStartingPeriodCnt()
{
	return getMethodRateCount();
}

/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public void setControlPercent(Integer seconds) 
{
	setMethodRate( seconds );
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public void setCyclePeriodLength(Integer seconds) 
{
	setMethodPeriod( seconds );
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public void setResendRate(Integer seconds) 
{
	setCycleRefreshRate( seconds );
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:40:08 AM)
 * @param seconds java.lang.Integer
 */
public void setStartingPeriodCnt(Integer periodCnt ) 
{
	setMethodRateCount( periodCnt );
}

@Override
public String getFrontRampOption()
{
    return super.getFrontRampOption();
}

@Override
public void setFrontRampOption(String newOption)
{
    super.setFrontRampOption(newOption);
}

@Override
public boolean useCustomDbRetrieve() {
    return false;
}
}
