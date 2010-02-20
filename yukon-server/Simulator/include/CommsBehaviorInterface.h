#pragma once

#include "types.h"

namespace Cti {
namespace Simulator{

class CommsBehavior
{
public:
    virtual void applyBehavior(bytes &message)=0;

    enum BehaviorValues
    {
        delayBehavior_enabled = 1
    };
};

}
}
