#include "precompiled.h"

#include "debug_timer.h"

#include "logger.h"

#include <sstream>

using namespace std;

namespace Cti {
namespace Timing {

//  forced into a union to allow memory mapping without 64-bit pointer misalignment
union filetime_converter
{
    FILETIME filetime;
    __int64  raw;

};

//  returns the difference in seconds between two SYSTEMTIME structs
double DebugTimer::calculateDuration(const SYSTEMTIME &begin, const SYSTEMTIME &end)
{
    filetime_converter fc_begin, fc_end;

    SystemTimeToFileTime(&begin, &fc_begin.filetime);
    SystemTimeToFileTime(&end,   &fc_end.filetime);

    double duration = fc_end.raw - fc_begin.raw;

    //  convert from 100 ns units to seconds
    duration *= 100.0 / (1000.0 * 1000.0 * 1000.0);

    return duration;
}


DebugTimer::DebugTimer(const string &action, bool print, double timeout) :
    _action(action),
    _print (print),
    _timeout(timeout)
{
    GetLocalTime(&_start);

    if( _print )
    {
        CTILOG_DEBUG(dout, _action << " / start");
    }
}


DebugTimer::~DebugTimer()
{
    SYSTEMTIME end;

    GetLocalTime(&end);

    double duration = calculateDuration(_start, end);

    if( _print || duration >= _timeout )
    {
        CTILOG_DEBUG(dout, _action << " / complete, took " << setprecision(3) << duration << " seconds");
    }
}

}
}

