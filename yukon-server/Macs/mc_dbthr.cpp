#include "yukon.h"
#include "mc_dbthr.h"

/*-----------------------------------------------------------------------------*
*
* File:   mc_dbthr
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_dbthr.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:52 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


CtiMCDBThread::CtiMCDBThread(CtiMCScheduleManager& mgr,
                             unsigned long interval )
: _schedule_manager(mgr), _interval(interval)
{
}

CtiMCDBThread::~CtiMCDBThread()
{
}

void CtiMCDBThread::run()
{
    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << RWTime() << " Database update thread starting up" << endl;
    }

    try
    {
        while( !isSet( CtiThread::SHUTDOWN ) )
        {
            if( sleep(_interval) )
                continue;

            if( gMacsDebugLevel & MC_DEBUG_DB )
            {
                CtiLockGuard< CtiLogger > g(dout);
                dout << RWTime() << " Updating database..." << endl;
            }

            _schedule_manager.updateAllSchedules();
        }

        {
            CtiLockGuard< CtiLogger > g(dout);
            dout << RWTime() << " Saving schedules to the database and exiting update thread." << endl;
            _schedule_manager.updateAllSchedules();
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > g(dout);
        dout << RWTime() << " Unknown exception occurred in CtiMCDBThread::run()" << endl;
    }
}
