#pragma once

#include "CommsBehaviorInterface.h"
#include "logger.h"

namespace Cti {
namespace Simulator{

class DelayBehavior : public CommsBehavior
{
public:
    DelayBehavior();
    virtual void applyBehavior(bytes &message);
    void setChance(int chance);

private:
    bytes _delayed;
    int _chance;
};

}
}
