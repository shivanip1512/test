#pragma once

#ifdef _WINDOWS
    
    #if !defined (NOMINMAX)
    #define NOMINMAX
    #endif

    #include <windows.h>
#endif

#include <assert.h>
#include "dlldefs.h"

class IM_EX_CTIBASE CtiMutex
{
public:
    CtiMutex();
    virtual ~CtiMutex();

    bool acquire();
    bool acquire(unsigned long millis);
    void release();
    void reset();


/// #ifdef _DEBUG
    DWORD lastAcquiredByTID() const;
/// #endif

private:

#ifdef _WINDOWS
    HANDLE hMutex;
/// #ifdef _DEBUG
    DWORD  _threadID[3];
/// #endif
#endif
};

