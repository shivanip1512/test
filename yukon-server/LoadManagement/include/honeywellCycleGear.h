#pragma once

#include "lmprogramdirect.h"
#include "smartgearbase.h"


class HoneywellCycleGear : public SmartGearBase,
                           public CtiLMProgramDirectGear
{
public:

    HoneywellCycleGear(Cti::RowReader & rdr);

    CtiLMProgramDirectGear * replicate() const override;

    // from SmartGearBase
    bool attemptControl(CtiLMGroupPtr  currentLMGroup,
                        long           controlSeconds,
                        DOUBLE       & expectedLoadReduced) override;

    bool stopControl(CtiLMGroupPtr currentLMGroup) override;

    unsigned long estimateOffTime(long controlSeconds) override;
};