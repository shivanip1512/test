#pragma once

#include "PlcBehavior.h"
#include "logger.h"

namespace Cti {
namespace Simulator{

class BchBehavior : public PlcBehavior
{
public:
    BchBehavior();
    virtual void apply(bytes &message, Logger &logger);
    void setChance(double chance);

private:
    double _chance;
};

}
}

