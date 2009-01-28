/*-----------------------------------------------------------------------------
    Filename:  critical_section.cpp

    Programmer:  Aaron Lauinger

    Description:    Source file for CtiCriticalSection

    Initial Date:  11/7/04

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/
#include "yukon.h"
#include "critical_section.h"

CtiCriticalSection::CtiCriticalSection()
{
#ifdef _WINDOWS
    InitializeCriticalSection(&_critical_section);
/// #ifdef _DEBUG
    _threadID = 0;
/// #endif
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
/// #ifdef _DEBUG
    _threadID = (int) _critical_section.OwningThread;
/// #endif
    return true;
#endif
}

/*-----------------------------------------------------------------------------
    acquire

    Blocks until the mux is acquired.
    20050426 CGP.  I need this for debug.  It is not timed.
-----------------------------------------------------------------------------*/
bool CtiCriticalSection::acquire(unsigned long ignored_arg_millis)
{
    return acquire();
}


/*-----------------------------------------------------------------------------
    tryAcquire

    Attempts to acquire the mux.
-----------------------------------------------------------------------------*/
bool CtiCriticalSection::tryAcquire()
{
#ifdef _WINDOWS
/// #ifdef _DEBUG
    if( TryEnterCriticalSection(&_critical_section) )
    {
        _threadID = (int) _critical_section.OwningThread;
        return true;
    }
    return false;
/// #else
///     return TryEnterCriticalSection(&_critical_section);
/// #endif
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

/// #ifdef _DEBUG
    _threadID = 0;
/// #endif
#endif
}

/// #ifdef _DEBUG
DWORD CtiCriticalSection::lastAcquiredByTID() const
{
    return _threadID;
}
/// #endif

