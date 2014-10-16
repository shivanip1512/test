#pragma once

#include "MctBehavior.h"

namespace Cti {
namespace Simulator{

class FrozenReadParityBehavior : public MctBehavior
{
public:

    FrozenReadParityBehavior();
    void apply(target_type &message, Logger &logger) override;
    void setChance(double chance) override;

private:

    void invertFrozenParityBit(unsigned char &byte, Logger &logger);

    double _chance;
};

}
}

