#include "yukon.h"
#include "DelayBehavior.h"

#include <ctime>
#include <random>

namespace Cti {
namespace Simulator{

DelayBehavior::DelayBehavior()
{
}

void DelayBehavior::applyBehavior(bytes &message)
{
    if (_delayed.empty())
    {
        double dist = rand() / double(RAND_MAX+1);
        int chance = int(dist * 100);
        {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << "\n\nRandom number = " << chance << endl << endl;
        }
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
