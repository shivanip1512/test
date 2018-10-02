
#pragma once

#include <boost/shared_ptr.hpp>


namespace Cti            {
namespace LoadManagement {

struct NestControlInterface
{
    virtual bool sendCriticalCycleControl( long controlDurationSeconds ) = 0;

    virtual bool sendStandardCycleControl( long controlDurationSeconds,
                                           int  prepOption,
                                           int  peakOption,
                                           int  postOption ) = 0;
};

typedef boost::shared_ptr<NestControlInterface> NestControlInterfacePtr;

}
}

