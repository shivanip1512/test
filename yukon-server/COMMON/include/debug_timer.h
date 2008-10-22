#include "logger.h"

namespace Cti {

class IM_EX_CTIBASE DebugTimer
{
private:

    CtiTime   _start;
    ctitime_t _notice_duration;

    const string _action;

    bool _print_start_stop,
         _print_duration;

public:

    DebugTimer(const string &action,
               ctitime_t print_force_duration,
               bool print_start_stop_debuglevel,
               bool print_duration_debuglevel);

    ~DebugTimer();
};

}

