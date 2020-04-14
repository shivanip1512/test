package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;

/**
 * Insert the type's description here.
 * Creation date: (4/12/2004 2:39:47 PM)
 * @author: jdayton
 */
public class NoControlGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear 
{

/**
 * NoControlGear constructor comment.
 */
public NoControlGear() 
{
	super();

	setControlMethod( GearControlMethod.NoControl );

}

@Override
public boolean useCustomDbRetrieve() {
    return false;
}
}