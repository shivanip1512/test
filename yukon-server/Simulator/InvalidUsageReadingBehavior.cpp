#include "precompiled.h"

#include "InvalidUsageReadingBehavior.h"

#include "SimulatorLogger.h"

namespace Cti {
namespace Simulator {

InvalidUsageReadingBehavior::InvalidUsageReadingBehavior(double probability)
    :   MctBehavior(probability)
{
}

void InvalidUsageReadingBehavior::apply(target_type &message, Logger &logger)
{
    if( message.function_read && message.function == 0x90 )
    {
        if( behaviorOccurs() )
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
