#pragma once

#include "MctBehavior.h"

namespace Cti {
namespace Simulator {

struct FrozenPeakTimestampBehavior : MctBehavior
{
    FrozenPeakTimestampBehavior(double probability);
    void apply(target_type &message, Logger &logger) override;
};

}
}

