#include "precompiled.h"

#include "mutex.h"
#include "logger.h"
#include "std_helper.h"
#include "win_helper.h"

using std::begin;
using std::end;

CtiMutex::CtiMutex() :
hMutex(INVALID_HANDLE_VALUE)
{
    reset();
}

CtiMutex::~CtiMutex()
{
    CloseHandle( hMutex );
}

/*-----------------------------------------------------------------------------
    acquire

    Blocks until the mux is acquired or millis milliseconds have elapsed.
    Returns true if the mux is successfully acquired.
    False if there was a timeout.
-----------------------------------------------------------------------------*/
bool CtiMutex::acquire(unsigned long millis)
{
    const DWORD result = WaitForSingleObject( hMutex, millis );

    if( result == WAIT_OBJECT_0 )
    {
        _threadIDs[2] = _threadIDs[1];
        _threadIDs[1] = _threadIDs[0];
        _threadIDs[0] = GetCurrentThreadId();

        return true;
    }

    //if( result == WAIT_TIMEOUT )  //  just return false, no error handling necessary for a timeout

    if( result == WAIT_FAILED )
    {
        const DWORD error = GetLastError();
        CTILOG_ERROR(dout, "Wait to acquire mutex failed, last error: "<< error <<" / "<< Cti::getSystemErrorMessage(error));

        //  Prevent a tight loop by sleeping for a bit.
        //    WaitForSingleObject returns immediately if the underlying failure is ERR_INVALID_HANDLE (6).
        Sleep(500);
    }

    return false;
}

/*-----------------------------------------------------------------------------
    release

    Releases the mux.
-----------------------------------------------------------------------------*/
void CtiMutex::release()
{
    const BOOL retres = ReleaseMutex( hMutex );

    if( ! retres )
    {
        const DWORD error = GetLastError();
        CTILOG_ERROR(dout, "Release mutex failed, last error: "<< error <<" / "<< Cti::getSystemErrorMessage(error));
    }
    _threadIDs[0] = 0;
}


DWORD CtiMutex::lastAcquiredByTID() const
{
    return _threadIDs[0];
}


void CtiMutex::reset()
{
    if(hMutex != INVALID_HANDLE_VALUE)
        CloseHandle( hMutex );

    hMutex = INVALID_HANDLE_VALUE;
    hMutex = CreateMutex( NULL, FALSE, NULL );

    std::fill( begin(_threadIDs), end(_threadIDs), 0);
}

