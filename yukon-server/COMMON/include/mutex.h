#pragma once

#include "dlldefs.h"

#include <windows.h>


class IM_EX_CTIBASE CtiMutex final
{
public:
    CtiMutex();
    ~CtiMutex();

    CtiMutex(const CtiMutex &) = delete;
    CtiMutex &operator=(const CtiMutex &) = delete;

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

