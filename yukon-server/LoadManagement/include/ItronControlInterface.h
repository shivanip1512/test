
#pragma once

#include <boost/shared_ptr.hpp>


namespace Cti::LoadManagement {

struct ItronControlInterface
{
    virtual bool sendCycleControl( long controlDurationSeconds ) = 0;

};

using ItronControlInterfacePtr = boost::shared_ptr<ItronControlInterface>;

}

