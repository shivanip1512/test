#pragma once

#include "PlcBehavior.h"
#include "logger.h"

namespace Cti {
namespace Simulator{

class NackBehavior : public PlcBehavior
{
public:
    NackBehavior(double chance);
    void apply(target_type &message, Logger &logger) override;

private:
    double _chance;
};

}
}

