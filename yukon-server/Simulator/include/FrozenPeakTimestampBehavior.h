#pragma once

#include "MctBehavior.h"

namespace Cti {
namespace Simulator {

class FrozenPeakTimestampBehavior : public MctBehavior
{
public:

    FrozenPeakTimestampBehavior();
    virtual void apply(target_type &message, Logger &logger);
    void setChance(double chance);

private:

    double _chance;
};

}
}

