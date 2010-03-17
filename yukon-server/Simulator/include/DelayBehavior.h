#pragma once

#include "CommsBehavior.h"
#include "logger.h"

namespace Cti {
namespace Simulator{

class DelayBehavior : public CommsBehavior
{
public:
    DelayBehavior();
    virtual void apply(bytes &message);
    void setChance(double chance);

private:
    bytes _delayed;
    double _chance;
};

}
}
