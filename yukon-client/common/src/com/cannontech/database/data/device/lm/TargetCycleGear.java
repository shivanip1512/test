package com.cannontech.database.data.device.lm;

/**
 * @author: jdayton
 */
public class TargetCycleGear extends TrueCycleGear
{
    public TargetCycleGear() 
    {
        super();
        setControlMethod( CONTROL_TARGET_CYCLE );
    }

}
