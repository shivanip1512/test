#pragma once

#include "Behavior.h"

namespace Cti {
namespace Simulator{

struct CommsBehavior : Behavior
{
    CommsBehavior(double probability)
        :   Behavior(probability)
    {
    }

    typedef bytes target_type;

    virtual void apply(target_type &message, Logger &logger) = 0;
};

}
}
