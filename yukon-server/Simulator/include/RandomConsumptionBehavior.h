#pragma once

#include "MctBehavior.h"

namespace Cti {
namespace Simulator {

class RandomConsumptionBehavior : public MctBehavior
{
public:

    RandomConsumptionBehavior(double probability);
    void apply(target_type &message, Logger &logger) override;

private:

    RandomGenerator<unsigned> _consumptionValueGenerator { 10000000 };
};

}
}
