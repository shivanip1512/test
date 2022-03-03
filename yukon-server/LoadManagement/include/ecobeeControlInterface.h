#pragma once

#include <boost/shared_ptr.hpp>


namespace Cti            {
namespace LoadManagement {

struct EcobeeControlInterface
{
    virtual bool sendCycleControl( long programId,
                                   long dutyCycle,
                                   long controlDurationSeconds,
                                   bool mandatory,
                                   bool rampInOutOption ) = 0;

    virtual bool sendSetpointControl( long programId,
                                      long controlDurationSeconds,
                                      bool temperatureOption,
                                      bool mandatory,
                                      long temperatureOffset ) = 0;

    virtual bool sendEcobeePlusControl( long programId,
                                        long controlDurationSeconds,
                                        bool temperatureOption,
                                        long randomTimeSeconds ) = 0;
};

typedef boost::shared_ptr<EcobeeControlInterface> EcobeeControlInterfacePtr;

}
}

