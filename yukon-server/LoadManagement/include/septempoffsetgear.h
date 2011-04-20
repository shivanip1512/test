
/*---------------------------------------------------------------------------
        Smart gear that handles the temperature offset portion
        of Smart Energy Profile.
---------------------------------------------------------------------------*/
#pragma once

#include "lmprogramdirect.h"
#include "lmprogramthermostatgear.h"
#include "smartgearbase.h"

class SEPTemperatureOffsetGear : public SmartGearBase, public CtiLMProgramThermoStatGear
{
public:

    typedef CtiLMProgramThermoStatGear Inherited;

    //It is believed all classes that inherit from this base must implement the functions below
    SEPTemperatureOffsetGear(Cti::RowReader &rdr);
    SEPTemperatureOffsetGear(const SEPTemperatureOffsetGear& gear);

    virtual CtiLMProgramDirectGear * replicate() const;

    SEPTemperatureOffsetGear& SEPTemperatureOffsetGear::operator=(const SEPTemperatureOffsetGear& right);
    int SEPTemperatureOffsetGear::operator==(const SEPTemperatureOffsetGear& right) const;
    int SEPTemperatureOffsetGear::operator!=(const SEPTemperatureOffsetGear& right) const;

    void SEPTemperatureOffsetGear::restore(Cti::RowReader &rdr);

    virtual bool attemptControl(CtiLMGroupPtr currentLMGroup, long controlSeconds, DOUBLE &expectedLoadReduced);
    virtual bool stopControl(CtiLMGroupPtr currentLMGroup);
    virtual unsigned long estimateOffTime(long controlSeconds);
    // End must implement functions
};
