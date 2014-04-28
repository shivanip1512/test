#pragma once

#include "lmprogramdirect.h"
#include "smartgearbase.h"


class EcobeeCycleGear : public SmartGearBase,
                        public CtiLMProgramDirectGear
{
public:

    EcobeeCycleGear( Cti::RowReader & rdr );

    virtual CtiLMProgramDirectGear * replicate() const;

    // from SmartGearBase
    virtual bool attemptControl( CtiLMGroupPtr  currentLMGroup,
                                 long           controlSeconds,
                                 DOUBLE       & expectedLoadReduced );

    virtual bool stopControl( CtiLMGroupPtr currentLMGroup );

    virtual unsigned long estimateOffTime( long controlSeconds );
};

