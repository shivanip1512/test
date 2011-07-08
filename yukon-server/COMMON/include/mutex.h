/*-----------------------------------------------------------------------------
    Filename:  mutex.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiMutex

    Initial Date:  11/7/00

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/
#pragma once
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....


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

