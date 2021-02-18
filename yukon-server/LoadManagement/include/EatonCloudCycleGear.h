#pragma once

#include "lmprogramdirect.h"
#include "smartgearbase.h"
#include "SmartGearCyclingOption.h"


namespace Cti::LoadManagement {

class EatonCloudCycleGear : public SmartGearBase,
                            public CtiLMProgramDirectGear
{
public:

    EatonCloudCycleGear( Cti::RowReader & rdr );

    CtiLMProgramDirectGear * replicate() const override;

    bool attemptControl( CtiLMGroupPtr  currentLMGroup,
                         long           controlSeconds,
                         double       & expectedLoadReduced ) override;

    bool stopControl( CtiLMGroupPtr currentLMGroup ) override;

    unsigned long estimateOffTime( long controlSeconds ) override;

    // Custom Gear settings

    bool isRampIn() const;
    bool isRampOut() const;
    long getDutyCyclePercentage() const;
    long getDutyCyclePeriod() const;
    long getCriticality() const;
    long getGroupCapacityReduction() const;

    double calculateLoadReduction( double groupCapacity ) const;

    std::size_t getFixedSize() const override    { return sizeof( *this ); }

    SmartGearCyclingOption getCyclingOption() const;

private:

    SmartGearCyclingOption _cyclingOption;
};

}

