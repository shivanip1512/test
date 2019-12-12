#pragma once

#include "lmprogramdirect.h"
#include "lmprogramthermostatgear.h"
#include "smartgearbase.h"


class EcobeeSetpointGear : public SmartGearBase,
                           public CtiLMProgramThermostatGear
{
public:

    EcobeeSetpointGear( Cti::RowReader & rdr );

    CtiLMProgramDirectGear * replicate() const override;

    // from SmartGearBase
    bool attemptControl( CtiLMGroupPtr  currentLMGroup,
                         long           controlSeconds,
                         DOUBLE       & expectedLoadReduced ) override;

    bool stopControl( CtiLMGroupPtr currentLMGroup ) override;

    unsigned long estimateOffTime( long controlSeconds ) override;

    // Custom Gear settings

    bool isHeatControl() const;
    bool isMandatory() const;
    int  getSetpointOffset() const;
};

