#pragma once

#include "MctBehavior.h"

namespace Cti {
namespace Simulator{

class FrozenReadParityBehavior : public MctBehavior
{
public:

    FrozenReadParityBehavior(double probability);

    void apply(target_type &message, Logger &logger) override;

private:

    void invertFrozenParityBit(unsigned char &byte, Logger &logger);
};

}
}

