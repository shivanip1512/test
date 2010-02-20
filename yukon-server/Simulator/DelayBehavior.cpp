#include "yukon.h"
#include "DelayBehavior.h"
#include "cparms.h"

#include <ctime>
#include <boost/random/uniform_int.hpp>
#include <random>

namespace Cti {
namespace Simulator{

DelayBehavior::DelayBehavior()
{
    srand(time(NULL));
}

void DelayBehavior::applyBehavior(bytes &message)
{
    if (_delayed.empty())
    {
        double dist = rand() / double(RAND_MAX);
        int chance = int(dist * 100);
        if (chance < _chance)
        {
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << "*******   Stalled Message   ********" << endl;
            }
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
        {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << "*******   Stalled Message Being Sent Through  ********" << endl;
        }
        bytes init_message = message;
        message.clear();
        message = _delayed;
        message.insert(message.end(), init_message.begin(), init_message.end());
        _delayed.clear();
        return;
    }
}

void DelayBehavior::setChance(int chance)
{
    _chance = chance;
}

}
}
