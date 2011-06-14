
#pragma once
#include <boost/shared_ptr.hpp>

namespace Cti {
namespace LoadManagement {

// Interface class to define a basic shed and restore command
class SEPControlInterface
{
public:
    // Sends out a SEP Cycling command, both with or without advanced cycling
    virtual bool sendSEPCycleControl(long controlMinutes, long cyclePercent, long criticality, bool isTrueCycle, bool randomizeStart, bool randomizeStop) = 0;

    // Sends out a thermostat temperature offset command. Only one offset should be nonzero.
    virtual bool sendSEPTempOffsetControl(long controlMinutes, long heatOffset, long coolOffset, bool isCelsius, long criticality, bool randomizeStart, bool randomizeStop) = 0;
};

typedef boost::shared_ptr<SEPControlInterface> SEPControlInterfacePtr;

}//namespaces
}
