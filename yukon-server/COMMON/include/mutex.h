#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include "dlldefs.h"

#include <windows.h>
#include <assert.h>

class IM_EX_CTIBASE CtiMutex
{
public:
    CtiMutex();
    virtual ~CtiMutex();

    bool acquire();
    bool acquire(unsigned long millis);
    void release();
    void reset();

    // For debug:
    // returns the last TID or the current TID
    DWORD lastAcquiredByTID() const;

private:
    HANDLE hMutex;

    // For debug:
    // contains the 3 last TID
    DWORD _threadIDs[3];
};

