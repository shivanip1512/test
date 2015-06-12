#pragma once

#include "PlcBehavior.h"
#include "logger.h"

namespace Cti {
namespace Simulator{

struct NackBehavior : PlcBehavior
{
    NackBehavior(double probability);

    void apply(target_type &message, Logger &logger) override;
};

}
}

