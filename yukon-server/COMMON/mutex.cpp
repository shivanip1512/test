/*-----------------------------------------------------------------------------
    Filename:  mutex.cpp

    Programmer:  Aaron Lauinger

    Description:    Source file for CtiMutex

    Initial Date:  11/7/00

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "mutex.h"

CtiMutex::CtiMutex()
{
#ifdef _WINDOWS
    hMutex = CreateMutex( NULL, FALSE, NULL );
#ifdef _DEBUG
    _threadID = 0;
#endif
#endif
}

CtiMutex::~CtiMutex()
{
#ifdef _WINDOWS
    CloseHandle( hMutex );
#endif
}

/*-----------------------------------------------------------------------------
    acquire

    Blocks until the mux is acquired.
-----------------------------------------------------------------------------*/
bool CtiMutex::acquire()
{
#ifdef _WINDOWS
    DWORD result = WaitForSingleObject( hMutex, INFINITE );
#ifdef _DEBUG
    _threadID = GetCurrentThreadId();
#endif
    return( result == WAIT_OBJECT_0 );
#endif
}

/*-----------------------------------------------------------------------------
    acquire

    Blocks until the mux is acquired or millis milliseconds have elapsed.
    Returns true if the mux is successfully acquired.
    False if there was a timeout.
-----------------------------------------------------------------------------*/
bool CtiMutex::acquire(unsigned long millis)
{
#ifdef _WINDOWS
    DWORD result = WaitForSingleObject( hMutex, millis );
    // assert(result != WAIT_FAILED);   // Why??? CGP 021502
#ifdef _DEBUG
    if(result == WAIT_OBJECT_0)
        _threadID = GetCurrentThreadId();
#endif
    return( result == WAIT_OBJECT_0 );
#endif
}

/*-----------------------------------------------------------------------------
    release

    Releases the mux.
-----------------------------------------------------------------------------*/
void CtiMutex::release()
{
#ifdef _WINDOWS
    ReleaseMutex( hMutex );

#ifdef _DEBUG
    _threadID = 0;
#endif
#endif
}

#ifdef _DEBUG
DWORD CtiMutex::lastAcquiredByTID() const
{
    return _threadID;
}
#endif

