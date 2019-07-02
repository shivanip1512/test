package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.database.db.device.lm.LMProgramDirectGear;

public interface ProgramGearFields {
    
    /**
     * Build model object from the passed db persistent Gear.
     * @return 
     */
      void buildModel(LMProgramDirectGear lmProgramDirectGear);
    
    /**
     * Builds db persistent object for Gear.
     */
      void  buildDBPersistent(LMProgramDirectGear lmProgramDirectGear);

}
