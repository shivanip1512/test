#pragma once

#include "utility.h"

namespace Cti {
namespace Timing {

/**
 * Implements a timer with a countdown.
 */
class CountdownTimer
{
    unsigned long _timeNow;
    unsigned long _timeEnd;

public:
    /// construct, set the time to wait in millisec
    CountdownTimer( unsigned long milliSec )
    {
        MilliTime( &_timeNow );
        _timeEnd = _timeNow + milliSec;
    }

    /// function to call to update internal time
    void update()
    {
        MilliTime( &_timeNow );
    }

    /// returns true if the delay as expired
    bool isExpired() const
    {
        return ( _timeNow >= _timeEnd );
    }

    /// returns the remaining time;
    unsigned long getRemaining() const
    {
        return ( isExpired() ? 0 : ( _timeEnd - _timeNow ));
    }
};

}
}
