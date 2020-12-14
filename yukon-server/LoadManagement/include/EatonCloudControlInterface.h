
#pragma once

#include <boost/shared_ptr.hpp>

#include "SmartGearCyclingOption.h"


namespace Cti::LoadManagement {

struct EatonCloudControlInterface
{
    struct CycleControlParameters
    {
        long controlDurationSeconds;
        bool rampIn;
        bool rampOut;
        SmartGearCyclingOption cyclingOption;
        long dutyCyclePercent;
        long dutyCyclePeriod;
        long criticality;
    };

    virtual bool sendCycleControl( CycleControlParameters parameters ) = 0;

    virtual bool sendNoControl( bool doRestore ) = 0;
};

using EatonCloudControlInterfacePtr = boost::shared_ptr<EatonCloudControlInterface>;

}

