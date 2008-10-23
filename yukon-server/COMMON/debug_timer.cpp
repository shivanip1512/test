#include "yukon.h"

#include <strstream>

#include "debug_timer.h"

using namespace std;

namespace Cti {

string DebugTimer::formatSystemTime(const SYSTEMTIME &time)
{
    ostringstream formatted;

    formatted.fill('0');

    formatted << setw(2) << time.wMonth << "/";
    formatted << setw(2) << time.wDay   << "/";
    formatted << setw(2) << time.wYear  << " ";

    formatted << setw(2) << time.wHour   << ":";
    formatted << setw(2) << time.wMinute << ":";
    formatted << setw(2) << time.wSecond << ".";
    formatted << setw(3) << time.wMilliseconds;

    return formatted.str();
}


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

    //  should never happen, but here for completeness
    if( fc_begin.raw > fc_end.raw )
    {
        swap(fc_begin, fc_end);
    }

    double duration = fc_begin.raw - fc_end.raw;

    //  convert from 100 ns units to seconds
    duration *= 100.0 / (1000.0 * 1000.0 * 1000.0);

    return duration;
}


DebugTimer::DebugTimer(const string &action, bool print_bounds, double alert_timeout) :
    _action(action),
    _print_bounds (print_bounds),
    _alert_timeout(alert_timeout)
{
    GetLocalTime(&_start);

    if( _print_bounds )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << formatSystemTime(_start) << " " << _action << " / start" << endl;
    }
}


DebugTimer::~DebugTimer()
{
    SYSTEMTIME end;

    GetLocalTime(&end);

    double duration = calculateDuration(_start, end);

    if( _print_bounds || duration >= _alert_timeout )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << formatSystemTime(_start) << " " << _action << " / complete, took " << setprecision(3) << duration << " seconds" << endl;
    }
}

}

