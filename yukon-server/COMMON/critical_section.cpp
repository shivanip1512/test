/*-----------------------------------------------------------------------------
    Filename:  critical_section.cpp

    Programmer:  Aaron Lauinger

    Description:    Source file for CtiCriticalSection

    Initial Date:  11/7/04

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/
#include "critical_section.h"

CtiCriticalSection::CtiCriticalSection()
{
#ifdef _WINDOWS
    InitializeCriticalSection(&_critical_section);
#ifdef _DEBUG
    _threadID = 0;
#endif
#endif
}

CtiCriticalSection::~CtiCriticalSection()
{
#ifdef _WINDOWS
    DeleteCriticalSection(&_critical_section);
#endif
}

/*-----------------------------------------------------------------------------
    acquire

    Blocks until the mux is acquired.
-----------------------------------------------------------------------------*/
bool CtiCriticalSection::acquire()
{
#ifdef _WINDOWS
    EnterCriticalSection(&_critical_section);
#ifdef _DEBUG
    _threadID = (int) _critical_section.OwningThread;
#endif
    return true;
#endif
}

/*-----------------------------------------------------------------------------
    release

    Releases the mux.
-----------------------------------------------------------------------------*/
void CtiCriticalSection::release()
{
#ifdef _WINDOWS
    LeaveCriticalSection(&_critical_section);

#ifdef _DEBUG
    _threadID = 0;
#endif
#endif
}

#ifdef _DEBUG
DWORD CtiCriticalSection::lastAcquiredByTID() const
{
    return _threadID;
}
#endif

