
/*---------------------------------------------------------------------------
        Smart gear that handles the temperature offset portion
        of Smart Energy Profile.
---------------------------------------------------------------------------*/
#pragma once

#include "lmprogramdirect.h"
#include "lmprogramthermostatgear.h"
#include "smartgearbase.h"

class SEPTemperatureOffsetGear : public SmartGearBase, public CtiLMProgramThermostatGear
{
public:

    //It is believed all classes that inherit from this base must implement the functions below
    SEPTemperatureOffsetGear(Cti::RowReader &rdr);

    virtual CtiLMProgramDirectGear * replicate() const;

    virtual bool attemptControl(CtiLMGroupPtr currentLMGroup, long controlSeconds, DOUBLE &expectedLoadReduced);
    virtual bool stopControl(CtiLMGroupPtr currentLMGroup);
    virtual unsigned long estimateOffTime(long controlSeconds);
    // End must implement functions

    std::size_t getFixedSize() const override    { return sizeof( *this ); }
};
