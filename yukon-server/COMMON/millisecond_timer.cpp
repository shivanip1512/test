#include "precompiled.h"

#include "millisecond_timer.h"

#include <mmsystem.h>

namespace Cti {
namespace Timing {


MillisecondTimer::MillisecondTimer()
{
    timeBeginPeriod(1U);

    reset();
}

MillisecondTimer::~MillisecondTimer()
{
    timeEndPeriod(1U);
}

void MillisecondTimer::reset()
{
    _mark = timeGetTime();
}

DWORD MillisecondTimer::elapsed() const
{
    return timeGetTime() - _mark;
}


}
}

