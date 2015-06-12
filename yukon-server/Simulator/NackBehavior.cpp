#include "precompiled.h"

#include "NackBehavior.h"

#include "SimulatorLogger.h"

namespace Cti {
namespace Simulator{

NackBehavior::NackBehavior(double probability)
    :   PlcBehavior(probability)
{
}

void NackBehavior::apply(target_type &message, Logger &logger)
{
    if( behaviorOccurs() )
    {
        logger.breadcrumbLog("***** NACK behavior clearing all response words *****");
        message.clear();
    }
}

}
}
