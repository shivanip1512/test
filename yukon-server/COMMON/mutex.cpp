/*-----------------------------------------------------------------------------
    Filename:  mutex.cpp

    Programmer:  Aaron Lauinger

    Description:    Source file for CtiMutex

    Initial Date:  11/7/00

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "mutex.h"

using namespace std;

CtiMutex::CtiMutex() :
hMutex(INVALID_HANDLE_VALUE)
{
    reset();
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
    for(int i = 2; i > 0; i--)
        _threadID[i] = _threadID[i-1];

    _threadID[0] = GetCurrentThreadId();
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
    //assert(result != WAIT_FAILED);   // Why??? CGP 021502

    if(result == WAIT_OBJECT_0)
    {
        for(int i = 2; i > 0; i--)
            _threadID[i] = _threadID[i-1];

        _threadID[0] = GetCurrentThreadId();
    }
    if( result == WAIT_FAILED )
    {
        std::cerr << " mutex wait failed, last error: " << GetLastError() << std::endl;
    }
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
    BOOL retres = ReleaseMutex( hMutex );

    if(!retres)
        _threadID[0] = 0;
#endif
}

/// #ifdef _DEBUG
DWORD CtiMutex::lastAcquiredByTID() const
{
    return _threadID[0];
}
/// #endif

void CtiMutex::reset()
{
#ifdef _WINDOWS
    if(hMutex != INVALID_HANDLE_VALUE) CloseHandle( hMutex );
    hMutex = INVALID_HANDLE_VALUE;
    hMutex = CreateMutex( NULL, FALSE, NULL );

    for(int i = 0; i < 3; i++) _threadID[i] = 0;
#endif
}

