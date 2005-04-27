/*-----------------------------------------------------------------------------
    Filename:  critical_section.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiCriticalSection

    Initial Date:  10/1/04

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2004
-----------------------------------------------------------------------------*/
#ifndef __CTICRITICALSECTION_H__
#define __CTICRITICALSECTION_H__
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifdef _WINDOWS
    #include <windows.h>
#endif

#include <assert.h>
#include "dlldefs.h"

class IM_EX_CTIBASE CtiCriticalSection
{
public:
    CtiCriticalSection();
    virtual ~CtiCriticalSection();

    bool acquire();
    bool acquire(unsigned long ignored_millis);     // 20050426 CGP.  I need this for debug.  It is not timed
    void release();

#ifdef _DEBUG
    DWORD lastAcquiredByTID() const;
#endif

private:

#ifdef _WINDOWS
    CRITICAL_SECTION _critical_section;
#ifdef _DEBUG
    DWORD  _threadID;
#endif
#endif
};
#endif
