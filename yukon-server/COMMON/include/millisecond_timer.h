#pragma once

#include "dlldefs.h"

namespace Cti {
namespace Timing {


class IM_EX_CTIBASE MillisecondTimer
{
    DWORD _mark;

public:
    MillisecondTimer();
    ~MillisecondTimer();
    void reset();
    DWORD elapsed() const;
};


}
}

