
#pragma once

#include <boost/shared_ptr.hpp>


namespace Cti::LoadManagement {

struct ItronControlInterface
{
    virtual bool sendCycleControl( long controlDurationSeconds,
                                   bool rampInOption,
                                   bool rampOutOption,
                                   long dutyCyclePercent,
                                   long dutyCyclePeriod,
                                   long criticality ) = 0;

};

using ItronControlInterfacePtr = boost::shared_ptr<ItronControlInterface>;

}

