#include "precompiled.h"

#include "NackBehavior.h"
#include "logger.h"

namespace Cti {
namespace Simulator{

NackBehavior::NackBehavior(double chance) :
    _chance(chance)
{
}

void NackBehavior::apply(target_type &message, Logger &logger)
{
    double dist = rand() / double(RAND_MAX+1);
    double chance = dist * 100;
    if(chance < _chance)
    {
        logger.breadcrumbLog("***** NACK behavior clearing all response words *****");
        message.clear();
    }
}

}
}
