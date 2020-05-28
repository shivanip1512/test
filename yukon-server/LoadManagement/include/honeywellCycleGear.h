#pragma once

#include "lmprogramdirectgear.h"
#include "smartgearbase.h"


struct HoneywellCycleGear : SmartGearBase,
                            CtiLMProgramDirectGear
{
    HoneywellCycleGear(Cti::RowReader & rdr);

    CtiLMProgramDirectGear * replicate() const override;

    // from SmartGearBase
    bool attemptControl(CtiLMGroupPtr  currentLMGroup,
                        long           controlSeconds,
                        DOUBLE       & expectedLoadReduced) override;

    bool stopControl(CtiLMGroupPtr currentLMGroup) override;

    unsigned long estimateOffTime(long controlSeconds) override;

    double calculateLoadReduction( double groupCapacity ) const;

    std::size_t getFixedSize() const override    { return sizeof( *this ); }

    // Custom Gear settings

    bool isRampInOut() const;
    bool isMandatory() const;
    int  getDutyCycle() const;
};