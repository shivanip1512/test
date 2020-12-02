
#pragma once

#include <boost/shared_ptr.hpp>


namespace Cti::LoadManagement {

struct EatonCloudControlInterface
{
    struct CycleControlParameters
    {
        long controlDurationSeconds;

        bool rampIn;
        bool rampOut;

        long cyclingOption;
        long dutyCyclePercent;
        long dutyCyclePeriod;
        long criticality;

        // etc...

    };

    virtual bool sendCycleControl( CycleControlParameters parameters ) = 0;

    virtual bool sendNoControl() = 0;
};

using EatonCloudControlInterfacePtr = boost::shared_ptr<EatonCloudControlInterface>;

}

