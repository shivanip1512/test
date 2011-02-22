
/*---------------------------------------------------------------------------
        SEPCycleGear class. Smart gear that handles the cycling portion
        of Smart Energy Profile.
---------------------------------------------------------------------------*/
#pragma once

#include "smartgearbase.h"

class SEPCycleGear : public SmartGearBase
{
public:

    typedef SmartGearBase Inherited;

    //It is believed all classes that inherit from this base must implement the functions below
    SEPCycleGear(Cti::RowReader &rdr);
    SEPCycleGear(const SEPCycleGear& gear);

    virtual CtiLMProgramDirectGear * replicate() const;

    SEPCycleGear& SEPCycleGear::operator=(const SEPCycleGear& right);
    int SEPCycleGear::operator==(const SEPCycleGear& right) const;
    int SEPCycleGear::operator!=(const SEPCycleGear& right) const;

    void SEPCycleGear::restore(Cti::RowReader &rdr);

    virtual bool attemptControl(CtiLMGroupPtr currentLMGroup, long controlSeconds, DOUBLE &expectedLoadReduced);
    virtual bool stopControl(CtiLMGroupPtr currentLMGroup);
    virtual unsigned long estimateOffTime(long controlSeconds);
    // End must implement functions

    SmartGearBase *getSmartGear() { return this; }

};
