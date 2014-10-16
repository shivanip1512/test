#pragma once

#include "MctBehavior.h"

namespace Cti {
namespace Simulator {

class RandomConsumptionBehavior : public MctBehavior
{
public:

    RandomConsumptionBehavior();
    void apply(target_type &message, Logger &logger) override;
    void setChance(double chance) override;

private:

    unsigned makeRandomConsumptionValue();

    double _chance;
};

}
}
