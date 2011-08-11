#pragma once

#include "MctBehavior.h"

namespace Cti {
namespace Simulator{

class FrozenReadParityBehavior : public MctBehavior
{
public:

    FrozenReadParityBehavior();
    virtual void apply(target_type &message, Logger &logger);
    void setChance(double chance);

private:

    void invertFrozenParityBit(unsigned char &byte, Logger &logger);

    double _chance;
};

}
}

