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
    void setChance(int chance);

private:
    bytes _delayed;
    int _chance;
};

}
}
