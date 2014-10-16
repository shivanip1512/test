#include "precompiled.h"

#include "InvalidUsageReadingBehavior.h"

namespace Cti {
namespace Simulator {

InvalidUsageReadingBehavior::InvalidUsageReadingBehavior() :
    _chance(0)
{
}

void InvalidUsageReadingBehavior::setChance(double chance)
{
    _chance = chance;
}

void InvalidUsageReadingBehavior::apply(target_type &message, Logger &logger)
{
    if( message.function_read && message.function == 0x90 )
    {
        double dist = rand() / double(RAND_MAX+1);
        double chance = dist * 100.0;
        if( chance < _chance )
        {
            logger.breadcrumbLog("***** INVALID USAGE READING GENERATED *****");

            if( message.data.size() >= 3 )
            {
                message.data[0] = 0xff;
                message.data[1] = 0xff;
                message.data[2] = 0xfc;
            }
        }
    }
}

}
}
