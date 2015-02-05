#pragma once

#include <windows.h>
#include <assert.h>
#include "dlldefs.h"

class IM_EX_CTIBASE CtiSemaphore
{
    HANDLE hSema;
#ifdef DEBUG
    DWORD  _threadID;
#endif

public:

    CtiSemaphore(long init = 1, long max = 1);
    virtual ~CtiSemaphore();

    bool acquire();
    bool acquire(unsigned long millis);
    bool release();
    DWORD lastAcquiredByTID() const { return 0; }
};
