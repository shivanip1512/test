#include "precompiled.h"
#include "critical_section.h"

CtiCriticalSection::CtiCriticalSection()
{
    InitializeCriticalSection(&_critical_section);
    _threadID = 0;
}

CtiCriticalSection::~CtiCriticalSection()
{
    DeleteCriticalSection(&_critical_section);
}

/*-----------------------------------------------------------------------------
    acquire

    Blocks until the mux is acquired.
-----------------------------------------------------------------------------*/
bool CtiCriticalSection::acquire()
{
    EnterCriticalSection(&_critical_section);
    _threadID = (int) _critical_section.OwningThread;
    return true;
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
    if( TryEnterCriticalSection(&_critical_section) )
    {
        _threadID = (int) _critical_section.OwningThread;
        return true;
    }
    return false;
}


/*-----------------------------------------------------------------------------
    release

    Releases the mux.
-----------------------------------------------------------------------------*/
void CtiCriticalSection::release()
{
    LeaveCriticalSection(&_critical_section);

    _threadID = 0;
}

DWORD CtiCriticalSection::lastAcquiredByTID() const
{
    return _threadID;
}

