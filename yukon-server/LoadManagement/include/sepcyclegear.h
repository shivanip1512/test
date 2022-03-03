
/*---------------------------------------------------------------------------
        SEPCycleGear class. Smart gear that handles the cycling portion
        of Smart Energy Profile.
---------------------------------------------------------------------------*/
#pragma once

#include "lmprogramdirect.h"
#include "smartgearbase.h"

class SEPCycleGear : public SmartGearBase, public CtiLMProgramDirectGear
{
public:

    //It is believed all classes that inherit from this base must implement the functions below
    SEPCycleGear(Cti::RowReader &rdr);

    virtual CtiLMProgramDirectGear * replicate() const;

    virtual bool attemptControl(CtiLMGroupPtr currentLMGroup, long controlSeconds, DOUBLE &expectedLoadReduced);
    virtual bool stopControl(CtiLMGroupPtr currentLMGroup);
    virtual unsigned long estimateOffTime(long controlSeconds);
    // End must implement functions

    std::size_t getFixedSize() const override    { return sizeof( *this ); }
};
