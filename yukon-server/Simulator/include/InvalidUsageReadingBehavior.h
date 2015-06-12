#pragma once

#include "MctBehavior.h"

namespace Cti {
namespace Simulator {

struct InvalidUsageReadingBehavior : MctBehavior
{
    InvalidUsageReadingBehavior(double probability);

    void apply(target_type &message, Logger &logger) override;
};

}
}
