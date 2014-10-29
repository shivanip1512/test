#pragma once

#include "dlldefs.h"
#include <string>
#include <winbase.h>  //  for SYSTEMTIME

namespace Cti {
namespace Timing {

class IM_EX_CTIBASE DebugTimer
{
private:

    SYSTEMTIME _start;

    const std::string _action;

    bool  _print;
    float _timeout;

    static double calculateDuration(const SYSTEMTIME &begin, const SYSTEMTIME &end);

public:

    DebugTimer(const  std::string &action,
               bool   print   = false,  //  always print start/finished lines
               double timeout = 5.0);   //  print if the total duration exceeds this time, in seconds

    ~DebugTimer();
};

}
}

