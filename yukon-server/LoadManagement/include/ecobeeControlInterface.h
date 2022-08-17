#pragma once

#include <boost/shared_ptr.hpp>


namespace Cti            {
namespace LoadManagement {

struct EcobeeControlInterface
{
    virtual bool sendCycleControl( long dutyCycle,
                                   long controlDurationSeconds,
                                   bool rampInOption,
                                   bool rampOutOption,
                                   bool mandatory ) = 0;

    virtual bool sendSetpointControl( long controlDurationSeconds,
                                      bool temperatureOption,
                                      bool mandatory,
                                      long temperatureOffset ) = 0;
};

typedef boost::shared_ptr<EcobeeControlInterface> EcobeeControlInterfacePtr;

}
}

