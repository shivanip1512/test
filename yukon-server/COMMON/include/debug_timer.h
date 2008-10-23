#ifndef __DEBUG_TIMER_H__
#define __DEBUG_TIMER_H__

#include "logger.h"

namespace Cti {

class IM_EX_CTIBASE DebugTimer
{
private:

    SYSTEMTIME _start;

    const string _action;

    bool  _print_bounds;
    float _alert_timeout;

    static string formatSystemTime (const SYSTEMTIME &time);
    static double calculateDuration(const SYSTEMTIME &begin, const SYSTEMTIME &end);

public:

    DebugTimer(const  string &action,
               bool   print_bounds  = false,  //  always print start/finished lines
               double alert_timeout = 5.0);   //  print if the total duration exceeds this time

    ~DebugTimer();
};

}

#endif
