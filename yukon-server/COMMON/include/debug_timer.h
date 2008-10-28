#ifndef __DEBUG_TIMER_H__
#define __DEBUG_TIMER_H__

#include "logger.h"

namespace Cti {

class IM_EX_CTIBASE DebugTimer
{
private:

    SYSTEMTIME _start;

    const string _action;

    bool  _print;
    float _timeout;

    static string formatSystemTime (const SYSTEMTIME &time);
    static double calculateDuration(const SYSTEMTIME &begin, const SYSTEMTIME &end);

public:

    DebugTimer(const  string &action,
               bool   print   = false,  //  always print start/finished lines
               double timeout = 5.0);   //  print if the total duration exceeds this time, in seconds

    ~DebugTimer();
};

}

#endif
