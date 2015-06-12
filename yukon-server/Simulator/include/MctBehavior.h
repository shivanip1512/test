#pragma once

#include "Behavior.h"

namespace Cti {
namespace Simulator {

/**
 * An object to pass into the MctBehaviors that gives them the
 * required context they need to be able to perform the various
 * operations on the read bytes.
 */
struct MctMessageContext
{
    bytes data;
    unsigned function;
    bool function_read;
};

//! The parent class for behaviors that affect MCT reads.
/*!
 * MctBehavior is a virtual class which will be the parent
 * for objects that will affect MCT reads and responses in the CCU
 * Simulator. They will be applied probabilistically to
 * each message based on CPARM values retrieved from the
 * master.cfg file.
 */
struct MctBehavior : Behavior
{
    MctBehavior(double probability)
        :   Behavior(probability)
    {
    }

    typedef MctMessageContext target_type;

    virtual void apply(target_type &message, Logger &logger) = 0;
};

}
}
