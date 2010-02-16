#include "yukon.h"

#include "FilterImpl.h"

#include <ctime>

namespace Cti {
namespace Simulator{

DelayFilter::DelayFilter(int chance)
{
    _chance = chance;
}

void DelayFilter::filter(bytes &message)
{
    if (_delayed.empty())
    {
        srand(time(NULL));
    
        if ((rand() % 100) < _chance)
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

}
}
