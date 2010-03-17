#pragma once

#include "types.h"

namespace Cti {
namespace Simulator{

class CommsBehavior
{
public:
    typedef bytes target_type;

    virtual void apply(target_type &message)=0;
    virtual void setChance(double chance)=0;

    enum BehaviorValues
    {
        delayBehavior_enabled = 1
    };
};

}
}
