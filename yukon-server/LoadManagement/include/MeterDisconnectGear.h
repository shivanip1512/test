#pragma once

#include "lmprogramdirect.h"
#include "smartgearbase.h"



namespace Cti::LoadManagement {

class MeterDisconnectGear : public SmartGearBase,
                            public CtiLMProgramDirectGear
{
public:

    MeterDisconnectGear( Cti::RowReader & rdr );

    CtiLMProgramDirectGear * replicate() const override;

    bool attemptControl( CtiLMGroupPtr  currentLMGroup,
                         long           controlSeconds,
                         double       & expectedLoadReduced ) override;

    bool stopControl( CtiLMGroupPtr currentLMGroup ) override;

    unsigned long estimateOffTime( long controlSeconds ) override;
};

}

