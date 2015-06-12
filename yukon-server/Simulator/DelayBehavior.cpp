#include "precompiled.h"

#include "DelayBehavior.h"

#include "SimulatorLogger.h"

namespace Cti {
namespace Simulator{

DelayBehavior::DelayBehavior(double probability)
    :   CommsBehavior(probability)
{
}

void DelayBehavior::apply(bytes &message, Logger &logger)
{
    if (_delayed.empty())
    {
        if (behaviorOccurs())
        {
            logger.breadcrumbLog("***** MESSAGE STALLED *****");
            _delayed = message;
            message.clear();
        }
        else
        {
            return;
        }
    }

    else
    {
        bytes init_message = message;
        message.clear();
        message = _delayed;
        message.insert(message.end(), init_message.begin(), init_message.end());
        _delayed.clear();
        return;
    }
}

}
}
