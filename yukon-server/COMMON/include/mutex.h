/*-----------------------------------------------------------------------------
    Filename:  mutex.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiMutex

    Initial Date:  11/7/00

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __CTIMUTEX_H__
#define __CTIMUTEX_H__

#ifdef _WINDOWS
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

private:

#ifdef _WINDOWS
    HANDLE hMutex;
#ifdef DEBUG
    DWORD  _threadID;
#endif
#endif
};
#endif
