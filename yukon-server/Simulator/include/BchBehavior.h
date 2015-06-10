#pragma once

#include "PlcBehavior.h"
#include "logger.h"

namespace Cti {
namespace Simulator{

class BchBehavior : public PlcBehavior
{
public:
    BchBehavior(double chance);
    void apply(target_type &message, Logger &logger) override;

private:
    double _chance;
};

}
}

