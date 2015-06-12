#pragma once

#include "Behavior.h"

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
struct PlcBehavior : Behavior
{
    PlcBehavior(double probability)
        :   Behavior(probability)
    {
    }

    typedef bytes target_type;

    virtual void apply(target_type &message, Logger &logger) = 0;
};

}
}
