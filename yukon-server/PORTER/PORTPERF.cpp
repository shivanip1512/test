/*-----------------------------------------------------------------------------*
*
* File:   PORTPERF
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTPERF.cpp-arc  $
* REVISION     :  $Revision: 1.27 $
* DATE         :  $Date: 2005/08/23 20:10:27 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#pragma title ( "Periodic Statistic Update Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PORTPERF.C

    Purpose:
        Perform midnight and roll statistic updates

    The following procedures are contained in this module:
        PerfThread                      PerfUpdateThread

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-7-93   Converted to 32 bit                                WRO
        11-1-93  Modified to keep statistics temporarily in memory  TRH
        2-28-94  Added enviroment variables for update frequency    WRO
        6-11-96  Fixed Remote previous day statistics               WRO

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
//// #include "btrieve.h"
#include <stdio.h>
#include <string.h>
#include <time.h>

#include "cparms.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "elogger.h"

#include "portglob.h"
#include "portdecl.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "port_base.h"
#include "thread_monitor.h"

#include "logger.h"
#include "guard.h"
#include "utility.h"

static CtiStatisticsMap_t   gDeviceStatMap;
static bool                 gDeviceStatDirty = false;
static CtiMutex             gDeviceStatMapMux;

static CtiStatisticsIterator_t statisticsPaoFind(const LONG paoId);
bool statisticsDoTargetId(long deviceid, long targetid);


/* Routine to Update statistics for Ports and Remotes every 5 minutes */
VOID PerfUpdateThread (PVOID Arg)
{
    ULONG PerfUpdateRate = 3600L;
    UINT sanity;

    ULONG PostCount;
    USHORT i;
    CtiPortSPtr PortRecord;
    HEV PerfUpdateSem;
    PSZ Environment;
    ULONG Rate;
    RWTime now;
    LONG delay;

    /* set the priority of this guy high */
    CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, 31, 0);

    try
    {
        /* do this as long as we are running */
        for(;!PorterQuit;)
        {
            if(gConfigParms.isOpt("PORTER_DEVICESTATUPDATERATE"))
            {
                if(!(PerfUpdateRate = atol(gConfigParms.getValueAsString("PORTER_DEVICESTATUPDATERATE").data())))
                {
                    PerfUpdateRate = 3600L;
                }
            }

            //Thread Monitor Begins here**************************************************
            if(!(++sanity % SANITY_RATE))
            {
                {//This is not necessary and can be annoying, but if you want it (which you might) here it is.
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Perf Update Thread. TID:  " << rwThreadId() << endl;
                }
          
                CtiThreadRegData *data = new CtiThreadRegData( GetCurrentThreadId(), "Perf Update Thread", CtiThreadRegData::None, 400 );
                ThreadMonitor.tickle( data );
            }
            //End Thread Monitor Section

            now = now.now();

            RWTime nextTime = nextScheduledTimeAlignedOnRate(now, PerfUpdateRate);

            if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 1000L * (nextTime.seconds() - now.seconds())) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " PerfUpdateThread: TID " << CurrentTID() << " recieved quit event." << endl;
                }
                PorterQuit = TRUE;
                break;
            }

            now = now.now();

            /* Do the statistics */
            statisticsRecord();

            delay = RWTime().seconds() - now.seconds();
            if(delay > 5)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << delay << " seconds to update statistics." << endl;
                }
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " PerfUpdateThread: TID " << CurrentTID() << " recieved shutdown request." << endl;
        }


#if 1
        statisticsRecord();

        {
            CtiLockGuard<CtiMutex> guard(gDeviceStatMapMux);
            gDeviceStatMap.clear();
        }
#else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        {
            CtiLockGuard<CtiMutex> guard(gDeviceStatMapMux);
            gDeviceStatMap.clear();
        }

#endif
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PerfUpdateThread: TID " << CurrentTID() << " exiting" << endl;
    }
}

bool statisticsDoTargetId(long deviceid, long targetid)
{
    long doit = false;

    if(targetid != 0 && targetid != deviceid)
    {
        doit = true;
    }

    return doit;
}

CtiStatisticsIterator_t statisticsPaoFind(const LONG paoId)
{
    CtiStatisticsIterator_t dstatitr = gDeviceStatMap.end();

    if(paoId > 0)
    {

        dstatitr = gDeviceStatMap.find( paoId );

        if( dstatitr == gDeviceStatMap.end() )       // It is not in there!  Make an entry!
        {
            try
            {
                CtiStatistics dStats(paoId);
                // We need to load it up, and/or then insert it!
                if(dStats.Restore() == RWDBStatus::ok)
                {
                    pair< CtiStatisticsMap_t::iterator, bool > resultpair;

                    // Try to insert. Return indicates success.
                    resultpair = gDeviceStatMap.insert( make_pair(paoId, dStats) );

                    if(resultpair.second == true)           // Insert was successful.
                    {
                        dstatitr = resultpair.first;        // Iterator which points to the set entry.
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** UNX Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }

    return dstatitr;
}

void statisticsNewRequest(long paoportid, long devicepaoid, long targetpaoid)
{
    if( gConfigParms.getValueAsString("PORTER_DOSTATISTICS").contains("true", RWCString::ignoreCase) )
    {
    #ifndef DONOSTATS
        CtiLockGuard<CtiMutex> guard(gDeviceStatMapMux);

        if(!PorterQuit)
        {
            CtiStatisticsIterator_t dStatItr = statisticsPaoFind( paoportid );

            gDeviceStatDirty = true;

            if( dStatItr != gDeviceStatMap.end() )
            {
                CtiStatistics &dStats = (*dStatItr).second;
                dStats.incrementRequest( RWTime() );
            }
            dStatItr = statisticsPaoFind( devicepaoid );

            if( dStatItr != gDeviceStatMap.end() )
            {
                CtiStatistics &dStats = (*dStatItr).second;
                dStats.incrementRequest( RWTime() );
            }

            if(statisticsDoTargetId( devicepaoid, targetpaoid ))
            {
                dStatItr = statisticsPaoFind( targetpaoid );

                if( dStatItr != gDeviceStatMap.end() )
                {
                    CtiStatistics &dStats = (*dStatItr).second;
                    dStats.incrementRequest( RWTime() );
                }
            }
        }
    #endif
    }
}

void statisticsNewAttempt(long paoportid, long devicepaoid, long targetpaoid, int result)
{
    if( gConfigParms.getValueAsString("PORTER_DOSTATISTICS").contains("true", RWCString::ignoreCase) )
    {
    #ifndef DONOSTATS
        CtiLockGuard<CtiMutex> guard(gDeviceStatMapMux);

        if(!PorterQuit)
        {
            CtiStatisticsIterator_t dStatItr = statisticsPaoFind( paoportid );
            gDeviceStatDirty = true;

            if( dStatItr != gDeviceStatMap.end() )
            {
                CtiStatistics &dStats = (*dStatItr).second;

                if(dStats.resolveFailType(result) == CtiStatistics::CommErrors)
                {
                    dStats.incrementAttempts( RWTime(), result );
                    dStats.decrementRequest( RWTime() );       // Because I don't want it counted to try again.
                }
            }

            dStatItr = statisticsPaoFind( devicepaoid );

            if( dStatItr != gDeviceStatMap.end() )
            {
                CtiStatistics &dStats = (*dStatItr).second;
                dStats.incrementAttempts( RWTime(), result );
                dStats.decrementRequest( RWTime() );       // Because I don't want it counted to try again.
            }

            if(statisticsDoTargetId( devicepaoid, targetpaoid ))
            {
                dStatItr = statisticsPaoFind( targetpaoid );

                if( dStatItr != gDeviceStatMap.end() )
                {
                    CtiStatistics &dStats = (*dStatItr).second;
                    dStats.incrementAttempts( RWTime(), result );
                    dStats.decrementRequest( RWTime() );       // Because I don't want it counted to try again.
                }
            }
        }
    #endif
    }
}

void statisticsNewCompletion(long paoportid, long devicepaoid, long targetpaoid, int result)
{
    if( gConfigParms.getValueAsString("PORTER_DOSTATISTICS").contains("true", RWCString::ignoreCase) )
    {
    #ifndef DONOSTATS
        CtiLockGuard<CtiMutex> guard(gDeviceStatMapMux);

        if(!PorterQuit)
        {
            CtiStatisticsIterator_t dStatItr = statisticsPaoFind( paoportid );

            gDeviceStatDirty = true;

            if( dStatItr != gDeviceStatMap.end() )
            {
                CtiStatistics &dStats = (*dStatItr).second;
                dStats.incrementCompletion( RWTime(), result );
            }

            dStatItr = statisticsPaoFind( devicepaoid );

            if( dStatItr != gDeviceStatMap.end() )
            {
                CtiStatistics &dStats = (*dStatItr).second;
                dStats.incrementCompletion( RWTime(), result );
            }

            if(statisticsDoTargetId( devicepaoid, targetpaoid ))
            {
                dStatItr = statisticsPaoFind( targetpaoid );

                if( dStatItr != gDeviceStatMap.end() )
                {
                    CtiStatistics &dStats = (*dStatItr).second;
                    dStats.incrementCompletion( RWTime(), result );
                }
            }
        }
    #endif
    }
}

void statisticsRecord()
{
    if(gDeviceStatDirty)
    {
        try
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Start statisticsRecord() " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            vector< CtiStatistics > dirtyStatCol( gDeviceStatMap.size() );
            vector< CtiStatistics >::iterator dirtyStatItr;
            CtiStatisticsIterator_t dstatitr;

            int cnt = 0;
            {
                pair< CtiStatisticsMap_t::iterator, bool > resultpair;
                CtiLockGuard<CtiMutex> guard(gDeviceStatMapMux);    // Lock the global list for a minimal amount of time
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " statisticsRecord() acquired exclusion object " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                for(dstatitr = gDeviceStatMap.begin(); dstatitr != gDeviceStatMap.end(); dstatitr++)
                {
                    CtiStatistics &dStats = (*dstatitr).second;
                    if(dStats.isDirty())
                    {
                        cnt++;
                        dirtyStatCol.push_back(dStats);     // Copy and insert!
                        dStats.resetDirty();                // It has been cleaned up now...
                    }
                }

                gDeviceStatDirty = false;
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " statisticsRecord() generated table candidates. " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            // Ok, now we stuff the dirtyStatCol out on the DB.  WITHOUT BLOCKING OPERATIONS!
            {
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();

                if(conn.isValid())
                {
                    RWDBStatus dbstat;

                    conn.beginTransaction();
                    for(dirtyStatItr = dirtyStatCol.begin(); dirtyStatItr != dirtyStatCol.end(); dirtyStatItr++)
                    {
                        CtiStatistics &dStats = *dirtyStatItr;
                        dbstat = dStats.Update(conn);
                    }
                    conn.commitTransaction();
                }
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(slog);
                slog << endl;
            }

            if(cnt)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Recorded " << cnt << " device's performance statistics." << endl;
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    return;
}


#define STATISTICS_OFFSET_START                     2600

#define STATISTICS_OFFSET_COMPLETIONRATIO           0
#define STATISTICS_OFFSET_ATTEMPTPERCOMPLETION      1
#define STATISTICS_OFFSET_REQUESTS                  2
#define STATISTICS_OFFSET_COMPLETIONS               3
#define STATISTICS_OFFSET_ATTEMPTS                  4
#define STATISTICS_OFFSET_COMMERRORS                5
#define STATISTICS_OFFSET_PROTOCOLERRORS            6
#define STATISTICS_OFFSET_SYSTEMERRORS              7
#define STATISTICS_OFFSET_NUMBER_08                 8
#define STATISTICS_OFFSET_NUMBER_09                 9

/*
Analog Point offsets:
Current Hour:
2510 - Completion Ratio = Completions / Requests.
2511 - Attempts per Completion = Attempts / Completions.
2512 - Requests
2513 - Completions
2514 - Attempts
2515 - CommErrors
2516 - ProtocolErrors
2517 - SystemErrors

Current Day:
2520 - Completion Ratio = Completions / Requests.
2521 - Attempts per Completion = Attempts / Completions.
2522 - Requests
2523 - Completions
2524 - Attempts
2525 - CommErrors
2526 - ProtocolErrors
2527 - SystemErrors

Current Month:
2530 - Completion Ratio = Completions / Requests.
2531 - Attempts per Completion = Attempts / Completions.
2532 - Requests
2533 - Completions
2534 - Attempts
2535 - CommErrors
2536 - ProtocolErrors
2537 - SystemErrors
*/

void statisticsReport( CtiDeviceSPtr pDevice )
{
    CtiPoint *pPoint;

    CtiLockGuard<CtiMutex> guard(gDeviceStatMapMux);
    CtiStatisticsIterator_t dStatItr = statisticsPaoFind( pDevice->getID() );

    if( dStatItr != gDeviceStatMap.end() )
    {
        int req;
        int cmp;
        int att;
        int ecomm;
        int eprot;
        int esystem;
        double compratio;

        CtiStatistics &dStats = (*dStatItr).second;

        for(int i = 0; i < CtiStatistics::FinalCounterBin; i++)
        {
            if(pDevice->getDevicePointOffsetTypeEqual(STATISTICS_OFFSET_COMPLETIONRATIO, AnalogPointType))
            {
                req     = dStats.get( i, CtiStatistics::Requests);
            }

            cmp     = dStats.get( i, CtiStatistics::Completions);
            att     = dStats.get( i, CtiStatistics::Attempts);
            ecomm   = dStats.get( i, CtiStatistics::CommErrors);
            eprot   = dStats.get( i, CtiStatistics::ProtocolErrors);
            esystem = dStats.get( i, CtiStatistics::SystemErrors);
            compratio = dStats.getCompletionRatio( i );
        }
    }
}

