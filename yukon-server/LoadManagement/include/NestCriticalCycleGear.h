#pragma once

#include "lmprogramdirect.h"
#include "smartgearbase.h"



class NestCriticalCycleGear : public SmartGearBase,
                              public CtiLMProgramDirectGear
{
public:

    NestCriticalCycleGear( Cti::RowReader & rdr );

    CtiLMProgramDirectGear * replicate() const override;

    bool attemptControl( CtiLMGroupPtr  currentLMGroup,
                         long           controlSeconds,
                         DOUBLE       & expectedLoadReduced ) override;

    bool stopControl( CtiLMGroupPtr currentLMGroup ) override;

    unsigned long estimateOffTime( long controlSeconds ) override;
};

