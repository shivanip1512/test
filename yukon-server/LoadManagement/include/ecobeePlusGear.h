#pragma once

#include "lmprogramdirect.h"
#include "lmprogramthermostatgear.h"
#include "smartgearbase.h"

class EcobeePlusGear : public SmartGearBase,
                       public CtiLMProgramThermostatGear
{
public:

    EcobeePlusGear( Cti::RowReader & rdr );

    CtiLMProgramDirectGear * replicate() const override;

    // from SmartGearBase
    bool attemptControl( CtiLMGroupPtr currentLMGroup,
                         long          controlSeconds,
                         DOUBLE      & expectedLoadReduced ) override;

    bool stopControl( CtiLMGroupPtr currentLMGroup ) override;

    unsigned long estimateOffTime( long controlSeconds ) override;

    std::size_t getFixedSize() const override    { return sizeof( *this ); }

    // Custom Gear Settings

    bool isHeatControl() const;
};
