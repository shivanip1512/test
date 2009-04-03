package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;

/**
 * @author: jdayton
 */
public class MagnitudeCycleGear extends TrueCycleGear
{
    public MagnitudeCycleGear() 
    {
        super();
        setControlMethod( GearControlMethod.MagnitudeCycle );
    }

}