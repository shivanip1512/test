
#pragma once

#include <boost/shared_ptr.hpp>


namespace Cti            {
namespace LoadManagement {

struct ItronControlInterface
{
    virtual bool sendCycleControl( long controlDurationSeconds ) = 0;



};

typedef boost::shared_ptr<ItronControlInterface> ItronControlInterfacePtr;

}
}

