#pragma once

#include "MctBehavior.h"

namespace Cti {
namespace Simulator {

class FrozenPeakTimestampBehavior : public MctBehavior
{
public:

    FrozenPeakTimestampBehavior();
    void apply(target_type &message, Logger &logger) override;
    void setChance(double chance) override;

private:

    double _chance;
};

}
}

