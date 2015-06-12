#pragma once

#include "CommsBehavior.h"
#include "logger.h"

namespace Cti {
namespace Simulator{

class DelayBehavior : public CommsBehavior
{
public:

    DelayBehavior(double probability);
    void apply(bytes &message, Logger &logger) override;

private:

    bytes _delayed;
};

}
}
