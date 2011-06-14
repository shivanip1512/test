#pragma once
#include <boost/shared_ptr.hpp>

namespace Cti {
namespace LoadManagement {

// Interface class to define a basic shed and restore command
class GroupControlInterface
{
public:
    // Sends a stop message, either allowing randomization or cycles to stop
    // or stopping immediately if asked for.
    virtual bool sendStopControl(bool stopImmediately) = 0;

    //Sends a basic shed command for the control minutes given
    virtual bool sendShedControl(long controlMinutes) = 0;
};

typedef boost::shared_ptr<GroupControlInterface> GroupControlInterfacePtr;

}//namespaces
}
