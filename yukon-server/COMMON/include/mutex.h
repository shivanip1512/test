#pragma once

#include "dlldefs.h"

#include <boost/noncopyable.hpp>

#include <windows.h>
#include <assert.h>

class IM_EX_CTIBASE CtiMutex : boost::noncopyable
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

