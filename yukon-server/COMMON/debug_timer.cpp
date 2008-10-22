#include "yukon.h"

#include "debug_timer.h"

namespace Cti {

DebugTimer::DebugTimer(const string &action, ctitime_t notice_duration, bool print_start_stop, bool print_duration) :
    _print_start_stop(print_start_stop),
    _print_duration  (print_duration),
    _notice_duration (notice_duration),
    _action(action)
{
    if( _print_start_stop )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << _action << " / start" << endl;
    }
}


DebugTimer::~DebugTimer()
{
    ctitime_t duration = CtiTime::now().seconds() - _start.seconds();

    _print_duration |= duration >= _notice_duration;

    if( _print_start_stop || _print_duration )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << _action << " / complete";

        if( _print_duration )  dout << ", took " << duration << " seconds";

        dout << endl;
    }
}

}

