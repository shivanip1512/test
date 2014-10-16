#pragma once

#include "MctBehavior.h"

namespace Cti {
namespace Simulator {

class InvalidUsageReadingBehavior : public MctBehavior
{
    double _chance;

public:

    InvalidUsageReadingBehavior();

    void apply(target_type &message, Logger &logger) override;
    void setChance(double chance) override;
};

}
}
