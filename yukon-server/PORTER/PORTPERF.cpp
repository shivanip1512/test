/*-----------------------------------------------------------------------------*
*
* File:   PORTPERF
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTPERF.cpp-arc  $
* REVISION     :  $Revision: 1.47 $
* DATE         :  $Date: 2008/07/17 21:01:10 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


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

#include <deque>
using namespace std;

#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
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
static CtiMutex             gDeviceStatColMux;

static CtiStatisticsIterator_t statisticsPaoFind(const LONG paoId);
bool statisticsDoTargetId(long deviceid, long targetid);

static void statisticsProcessNewRequest(long paoportid, long trxpaoid, long targpaoid);
static void statisticsProcessNewAttempt(long paoportid, long trxpaoid, long targpaoid, int result);
static void statisticsProcessNewCompletion(long paoportid, long trxpaoid, long targpaoid, int result);
static void processCollectedStats(bool force);


typedef struct {
    enum {
        Attempt,
        Completion,
        Request
    };

    int action;
    long paoportid;
    long devicepaoid;
    long targetpaoid;
    int result;

} CtiStatTuple;

typedef deque< CtiStatTuple > statCol_t;

static statCol_t statCol;

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
    CtiTime now, lastTickleTime, lastReportTime;
    LONG delay;

    /* set the priority of this guy high */
    CTISetPriority(PRTYC_TIMECRITICAL, THREAD_PRIORITY_HIGHEST);

    try
    {
        /* do this as long as we are running */
        for(;!PorterQuit;)
        {
            PerfUpdateRate = gConfigParms.getValueAsULong("PORTER_DEVICESTATUPDATERATE", 3600L);

            now = now.now();
            CtiTime nextTime = nextScheduledTimeAlignedOnRate(now, PerfUpdateRate);

            do
            {
                Sleep(1000);
                if(lastTickleTime.seconds() < (now.seconds() - CtiThreadMonitor::StandardTickleTime))
                {
                    if(lastReportTime.seconds() < (now.seconds() - CtiThreadMonitor::StandardMonitorTime))
                    {
                        lastReportTime = lastReportTime.now();
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Perf Update Thread. TID:  " << rwThreadId() << endl;
                    }

                    CtiThreadRegData *data;
                    data = CTIDBG_new CtiThreadRegData( GetCurrentThreadId(), "Perf Update Thread", CtiThreadRegData::None, CtiThreadMonitor::StandardMonitorTime );
                    ThreadMonitor.tickle( data );
                    lastTickleTime = lastTickleTime.now();
                }

                if(statCol.empty())
                {
                    Sleep(1000);
                }
                else   // Process the deque!
                {
                    processCollectedStats(false);
                }
                now = now.now();
            } while( !PorterQuit && now.seconds() < nextTime.seconds()  );

            /* Do the statistics */
            processCollectedStats(true);
            statisticsRecord();

            delay = CtiTime().seconds() - now.seconds();
            if(delay > 5)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << delay << " seconds to update statistics." << endl;
                }
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " PerfUpdateThread: TID " << CurrentTID() << " recieved shutdown request." << endl;
        }


        statisticsRecord();

        {
            CtiLockGuard<CtiMutex> guard(gDeviceStatMapMux);
            gDeviceStatMap.clear();
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PerfUpdateThread: TID " << CurrentTID() << " exiting" << endl;
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
                        dout << CtiTime() << " **** UNX Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }

    return dstatitr;
}

void statisticsNewRequest(long paoportid, long devicepaoid, long targetpaoid, UINT &messageFlags )
{
    messageFlags |= MessageFlag_StatisticsRequested;

    CtiLockGuard<CtiMutex> guard(gDeviceStatColMux);
    CtiStatTuple tup;

    tup.action = CtiStatTuple::Request;
    tup.paoportid = paoportid;
    tup.devicepaoid = devicepaoid;
    tup.targetpaoid = targetpaoid;
    tup.result = 0;
    statCol.push_back(tup);
    return;
}

void statisticsProcessNewRequest(long paoportid, long devicepaoid, long targetpaoid)
{
    if( !findStringIgnoreCase(gConfigParms.getValueAsString("PORTER_DOSTATISTICS"),"false") )
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
                dStats.incrementRequest( CtiTime() );
            }
            dStatItr = statisticsPaoFind( devicepaoid );

            if( dStatItr != gDeviceStatMap.end() )
            {
                CtiStatistics &dStats = (*dStatItr).second;
                dStats.incrementRequest( CtiTime() );
            }

            if(statisticsDoTargetId( devicepaoid, targetpaoid ))
            {
                dStatItr = statisticsPaoFind( targetpaoid );

                if( dStatItr != gDeviceStatMap.end() )
                {
                    CtiStatistics &dStats = (*dStatItr).second;
                    dStats.incrementRequest( CtiTime() );
                }
            }
        }
    #endif
    }
}

void statisticsNewAttempt(long paoportid, long devicepaoid, long targetpaoid, int result,  UINT messageFlags)
{
    if( messageFlags & MessageFlag_StatisticsRequested )
    {
        CtiLockGuard<CtiMutex> guard(gDeviceStatColMux);
        CtiStatTuple tup;

        tup.action = CtiStatTuple::Attempt;
        tup.paoportid = paoportid;
        tup.devicepaoid = devicepaoid;
        tup.targetpaoid = targetpaoid;
        tup.result = result;

        statCol.push_back(tup);
    }
    else if( gConfigParms.getValueAsULong("STATISTICS_DEBUGLEVEL", 0, 16) & STATISTICS_REPORT_ON_MSGFLAGS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Statistics not requested for: Port " << paoportid << " Device " << devicepaoid << " / Target " << targetpaoid << endl;
    }
    return;
}

void statisticsProcessNewAttempt(long paoportid, long devicepaoid, long targetpaoid, int result)
{
    if( !findStringIgnoreCase(gConfigParms.getValueAsString("PORTER_DOSTATISTICS"),"false") )
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
                    dStats.incrementAttempts( CtiTime(), result );
                }
            }

            dStatItr = statisticsPaoFind( devicepaoid );

            if( dStatItr != gDeviceStatMap.end() )
            {
                CtiStatistics &dStats = (*dStatItr).second;
                dStats.incrementAttempts( CtiTime(), result );
            }

            if(statisticsDoTargetId( devicepaoid, targetpaoid ))
            {
                dStatItr = statisticsPaoFind( targetpaoid );

                if( dStatItr != gDeviceStatMap.end() )
                {
                    CtiStatistics &dStats = (*dStatItr).second;
                    dStats.incrementAttempts( CtiTime(), result );
                }
            }
        }
    #endif
    }
}

void statisticsNewCompletion(long paoportid, long devicepaoid, long targetpaoid, int result, UINT &messageFlags)
{
    if( messageFlags & MessageFlag_StatisticsRequested )
    {
        messageFlags &= ~MessageFlag_StatisticsRequested;
        CtiLockGuard<CtiMutex> guard(gDeviceStatColMux);
        CtiStatTuple tup;

        tup.action = CtiStatTuple::Completion;
        tup.paoportid = paoportid;
        tup.devicepaoid = devicepaoid;
        tup.targetpaoid = targetpaoid;
        tup.result = result;
        statCol.push_back(tup);
    }
    else if( gConfigParms.getValueAsULong("STATISTICS_DEBUGLEVEL", 0, 16) & STATISTICS_REPORT_ON_MSGFLAGS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Statistics not requested for: Port " << paoportid << " Device " << devicepaoid << " / Target " << targetpaoid << endl;
    }
    return;
}

void statisticsProcessNewCompletion(long paoportid, long devicepaoid, long targetpaoid, int result)
{
    if( !findStringIgnoreCase(gConfigParms.getValueAsString("PORTER_DOSTATISTICS"),"false") )
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
                dStats.incrementCompletion( CtiTime(), result );
            }

            dStatItr = statisticsPaoFind( devicepaoid );

            if( dStatItr != gDeviceStatMap.end() )
            {
                CtiStatistics &dStats = (*dStatItr).second;
                dStats.incrementCompletion( CtiTime(), result );
            }

            if(statisticsDoTargetId( devicepaoid, targetpaoid ))
            {
                dStatItr = statisticsPaoFind( targetpaoid );

                if( dStatItr != gDeviceStatMap.end() )
                {
                    CtiStatistics &dStats = (*dStatItr).second;
                    dStats.incrementCompletion( CtiTime(), result );
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
            if(getDebugLevel() & DEBUGLEVEL_STATISTICS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Start statisticsRecord() " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            std::vector< CtiStatistics > dirtyStatCol( gDeviceStatMap.size() );
            std::vector< CtiStatistics >::iterator dirtyStatItr;
            CtiStatisticsIterator_t dstatitr;

            int cnt = 0;
            {
                pair< CtiStatisticsMap_t::iterator, bool > resultpair;
                CtiLockGuard<CtiMutex> guard(gDeviceStatMapMux);    // Lock the global list for a minimal amount of time
                if(getDebugLevel() & DEBUGLEVEL_STATISTICS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " statisticsRecord() acquired exclusion object " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

            if(getDebugLevel() & DEBUGLEVEL_STATISTICS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " statisticsRecord() generated table candidates. " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

                    if( gConfigParms.getValueAsULong("STATISTICS_NUM_DAYS", 120, 10) > 0 )
                    {
                        conn.beginTransaction();
                        for(dirtyStatItr = dirtyStatCol.begin(); dirtyStatItr != dirtyStatCol.end(); dirtyStatItr++)
                        {
                            CtiStatistics &dStats = *dirtyStatItr;
                            dbstat = dStats.InsertDaily(conn);
                        }
                        conn.commitTransaction();

                        conn.beginTransaction();
                        {
                            CtiStatistics &dStats = *(dirtyStatCol.begin());
                            dStats.PruneDaily(conn);
                        }
                        conn.commitTransaction();
                    }
                }
            }

            if(cnt && getDebugLevel() & DEBUGLEVEL_STATISTICS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Recorded " << cnt << " device's performance statistics." << endl;
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    CtiPointSPtr pPoint;

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

void processCollectedStats(bool force)
{
    for(size_t qcnt = statCol.size(); qcnt > 0; qcnt-- )
    {
        CtiStatTuple tup;
        {
            CtiLockGuard<CtiMutex> guard(gDeviceStatColMux, 2500);

            while(force && !guard.isAcquired())  guard.tryAcquire(2500);

            if(guard.isAcquired())
            {
                tup = statCol.front();
                statCol.pop_front();
            }
            else
            {
                break;
            }
        }

        switch(tup.action)
        {
        case (CtiStatTuple::Request):
            {
                statisticsProcessNewRequest(tup.paoportid, tup.devicepaoid, tup.targetpaoid);
                break;
            }
        case (CtiStatTuple::Attempt):
            {
                statisticsProcessNewAttempt(tup.paoportid, tup.devicepaoid, tup.targetpaoid, tup.result);
                break;
            }
        case (CtiStatTuple::Completion):
            {
                statisticsProcessNewCompletion(tup.paoportid, tup.devicepaoid, tup.targetpaoid, tup.result);
                break;
            }
        }

        if(!force && PorterQuit) break; // If force is set, WE WILL DO THEM ALL.
    }
}
