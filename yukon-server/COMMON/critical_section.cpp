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
void CtiCriticalSection::acquire()
{
    EnterCriticalSection(&_critical_section);
    _threadID = _critical_section.OwningThread;
}


/*-----------------------------------------------------------------------------
    tryAcquire

    Attempts to acquire the mux.
-----------------------------------------------------------------------------*/
bool CtiCriticalSection::tryAcquire()
{
    if( TryEnterCriticalSection(&_critical_section) )
    {
        _threadID = _critical_section.OwningThread;
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

unsigned long long CtiCriticalSection::lastAcquiredByTID() const
{
    return reinterpret_cast<unsigned long long>(_threadID);
}
