#include "precompiled.h"
#include "DelayBehavior.h"
#include "logger.h"

#include <stdlib.h>

using std::endl;

namespace Cti {
namespace Simulator{

DelayBehavior::DelayBehavior()
{
}

void DelayBehavior::apply(bytes &message, Logger &logger)
{
    if (_delayed.empty())
    {
        double dist = rand() / double(RAND_MAX+1);
        double chance = dist * 100;
        if (chance < _chance)
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

void DelayBehavior::setChance(double chance)
{
    _chance = chance;
}

}
}
