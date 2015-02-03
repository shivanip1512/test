#pragma once

#include <windows.h>

#include <assert.h>
#include "dlldefs.h"

class IM_EX_CTIBASE CtiCriticalSection
{
public:
    CtiCriticalSection();
    virtual ~CtiCriticalSection();

    bool acquire();
    bool acquire(unsigned long ignored_millis);     // 20050426 CGP.  I need this for debug.  It is not timed
    bool tryAcquire();
    void release();

    DWORD lastAcquiredByTID() const;

private:

    CRITICAL_SECTION _critical_section;
    DWORD  _threadID;
};

