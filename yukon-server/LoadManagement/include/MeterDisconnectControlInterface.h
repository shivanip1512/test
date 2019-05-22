
#pragma once

#include <boost/shared_ptr.hpp>


namespace Cti::LoadManagement {

struct MeterDisconnectControlInterface
{
    virtual bool sendControl( long controlSeconds ) = 0;
};

using MeterDisconnectControlInterfacePtr = boost::shared_ptr<MeterDisconnectControlInterface>;

}

