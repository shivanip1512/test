/*-----------------------------------------------------------------------------*
*
* File:   sema
*
* Date:   5/1/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/02/10 23:23:45 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"
#include "sema.h"


CtiSemaphore::CtiSemaphore(long init, long max)
{
    hSema = CreateSemaphore( NULL, init, max, NULL );
#ifdef DEBUG
    _threadID = 0;
#endif
}

CtiSemaphore::~CtiSemaphore()
{
    CloseHandle( hSema );
}

/*-----------------------------------------------------------------------------
    acquire

    Blocks until the mux is acquired.
-----------------------------------------------------------------------------*/

bool CtiSemaphore::acquire()
{
    DWORD result = WaitForSingleObject( hSema, INFINITE );
#ifdef DEBUG
    _threadID = GetCurrentThreadId();
#endif
    return( result == WAIT_OBJECT_0 );
}

/*-----------------------------------------------------------------------------
    acquire

    Blocks until the mux is acquired or millis milliseconds have elapsed.
    Returns true if the mux is successfully acquired.
    False if there was a timeout.
-----------------------------------------------------------------------------*/
bool CtiSemaphore::acquire(unsigned long millis)
{
    DWORD result = WaitForSingleObject( hSema, millis );

#ifdef DEBUG
    _threadID = GetCurrentThreadId();
#endif
    return( result == WAIT_OBJECT_0 );
}

/*-----------------------------------------------------------------------------
    release

    Releases the mux.
-----------------------------------------------------------------------------*/
void CtiSemaphore::release()
{
    ReleaseSemaphore( hSema, 1, NULL );

#ifdef DEBUG
    _threadID = 0;
#endif
}
