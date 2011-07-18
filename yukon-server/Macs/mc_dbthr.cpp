#include "precompiled.h"
#include "mc_dbthr.h"

using std::endl;

/*-----------------------------------------------------------------------------*
*
* File:   mc_dbthr
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_dbthr.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2007/03/05 19:15:34 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


CtiMCDBThread::CtiMCDBThread(CtiMCScheduleManager& mgr,
                             unsigned long interval )
: _schedule_manager(mgr), _interval(interval)
{
    _startUpdateNow = CreateEvent(NULL, false, false, NULL);
}

CtiMCDBThread::~CtiMCDBThread()
{
    if( _startUpdateNow )
    {
        CloseHandle( _startUpdateNow );
    }
}

void CtiMCDBThread::run()
{
    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << CtiTime() << " Database update thread starting up" << endl;
    }

    try
    {
        while( !isSet( CtiThread::SHUTDOWN ) )
        {
            if( gMacsDebugLevel & MC_DEBUG_DB )
            {
                CtiLockGuard< CtiLogger > g(dout);
                dout << CtiTime() << " Updating database..." << endl;
            }

            _schedule_manager.updateAllSchedules();

            WaitForSingleObject( _startUpdateNow, _interval );
        }

        {
            CtiLockGuard< CtiLogger > g(dout);
            dout << CtiTime() << " Saving schedules to the database and exiting update thread." << endl;
        }
        _schedule_manager.updateAllSchedules();
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << CtiTime() << " Unknown exception occurred in CtiMCDBThread::run()" << endl;
    }
}

void CtiMCDBThread::forceImmediateUpdate()
{
    SetEvent(_startUpdateNow);
}
