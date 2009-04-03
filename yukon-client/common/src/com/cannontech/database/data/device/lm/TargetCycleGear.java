package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;

/**
 * @author: jdayton
 */
public class TargetCycleGear extends TrueCycleGear
{
    public TargetCycleGear() 
    {
        super();
        setControlMethod( GearControlMethod.TargetCycle );
    }

}
