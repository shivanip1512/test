#pragma once

#include <boost/shared_ptr.hpp>


namespace Cti::LoadManagement {

struct HoneywellControlInterface
{
    virtual bool sendCycleControl( const long programID,
                                   const long dutyCycle,
                                   const long controlDurationSeconds,
                                   const bool rampInOutOption ) = 0;

    virtual bool sendSetpointControl( const long programID,
                                      const bool temperatureOption,
                                      const bool mandatory,
                                      const int  temperatureOffset,
                                      const int  controlDurationSeconds ) = 0;
};

typedef boost::shared_ptr<HoneywellControlInterface> HoneywellControlInterfacePtr;

}
