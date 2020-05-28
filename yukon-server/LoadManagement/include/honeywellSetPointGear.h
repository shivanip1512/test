#pragma once

#include "lmprogramthermostatgear.h"
#include "smartgearbase.h"


struct HoneywellSetpointGear : SmartGearBase,
                               CtiLMProgramThermostatGear
{
    HoneywellSetpointGear( Cti::RowReader & rdr );

    CtiLMProgramDirectGear * replicate() const override;

    // from SmartGearBase
    bool attemptControl( CtiLMGroupPtr  currentLMGroup,
                         long           controlSeconds,
                         DOUBLE       & expectedLoadReduced ) override;

    bool stopControl( CtiLMGroupPtr currentLMGroup ) override;

    unsigned long estimateOffTime( long controlSeconds ) override;

    double calculateLoadReduction( double groupCapacity ) const;

    std::size_t getFixedSize() const override    { return sizeof( *this ); }

    // Custom Gear settings

    bool isHeatControl() const;
    bool isMandatory() const;
    int  getSetpointOffset() const;
};

