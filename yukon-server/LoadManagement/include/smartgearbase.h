/*---------------------------------------------------------------------------
        SmartGearBase class. Base class for gears that have logic in them
        instead of relying on the lm program to do all of their work.
---------------------------------------------------------------------------*/
#pragma once

#include "lmprogramdirectgear.h"
#include "lmgroupbase.h"

class SmartGearBase : public CtiLMProgramDirectGear
{
public:
    SmartGearBase() {};
    // The below commented out functions are non virtuals that likely need to be overriden by any class
    // that inherits from this class.

    //SmartGearBase(Cti::RowReader &rdr);
    //SmartGearBase(const SmartGearBase& gear);
    //SmartGearBase& operator=(const SmartGearBase& right);
    //int operator==(const SmartGearBase& right) const;
    //int operator!=(const SmartGearBase& right) const;
    //void restore(Cti::RowReader &rdr);

    virtual CtiLMProgramDirectGear * replicate() const = 0;

    virtual bool attemptControl(CtiLMGroupPtr currentLMGroup, long controlSeconds, DOUBLE &expectedLoadReduced) = 0;
    virtual bool stopControl(CtiLMGroupPtr currentLMGroup) = 0;
    virtual unsigned long estimateOffTime(long controlSeconds) = 0;

    SmartGearBase *getSmartGear() { return this; }

};
