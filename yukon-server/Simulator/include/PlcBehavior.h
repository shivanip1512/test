#pragma once

#include "types.h"
#include "SimulatorLogger.h"

namespace Cti {
namespace Simulator {

//! The parent class for behaviors that affect PLC messages. 
/*! 
 * PlcBehavior is a virtual class which will be the parent
 * for objects that will affect PLC messages in the CCU 
 * Simulator. They will be applied probabilistically to 
 * each message based on CPARM values retrieved from the 
 * master.cfg file. 
 */
class PlcBehavior
{
public:
    typedef bytes target_type;

    virtual void apply(target_type &message, Logger &logger) = 0;
};

}
}
