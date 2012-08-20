package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;

public class BeatThePeakGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear 
{

public BeatThePeakGear() 
{
    super();

    setControlMethod( GearControlMethod.BeatThePeak );
}

}