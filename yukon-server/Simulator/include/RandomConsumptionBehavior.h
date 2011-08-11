#pragma once

#include "MctBehavior.h"

namespace Cti {
namespace Simulator {

class RandomConsumptionBehavior : public MctBehavior
{
public:

    RandomConsumptionBehavior();
    virtual void apply(target_type &message, Logger &logger);
    void setChance(double chance);

private:

    unsigned makeRandomConsumptionValue();

    double _chance;
};

}
}
