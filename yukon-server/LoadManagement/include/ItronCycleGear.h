#pragma once

#include "lmprogramdirect.h"
#include "smartgearbase.h"



namespace Cti::LoadManagement {

class ItronCycleGear : public SmartGearBase,
                       public CtiLMProgramDirectGear
{
public:

    ItronCycleGear( Cti::RowReader & rdr );

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
};

}

