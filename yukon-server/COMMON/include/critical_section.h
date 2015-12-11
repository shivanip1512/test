#pragma once

#include <windows.h>

#include <assert.h>
#include "dlldefs.h"

class IM_EX_CTIBASE CtiCriticalSection final
{
public:
    CtiCriticalSection();
    ~CtiCriticalSection();

    CtiCriticalSection(const CtiCriticalSection &) = delete;
    CtiCriticalSection &operator=(const CtiCriticalSection &) = delete;

    void acquire();
    bool tryAcquire();
    void release();

    unsigned long long lastAcquiredByTID() const;

private:

    CRITICAL_SECTION _critical_section;
    HANDLE _threadID;
};

