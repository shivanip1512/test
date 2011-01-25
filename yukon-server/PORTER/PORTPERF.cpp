#include "yukon.h"

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <process.h>

#include <deque>

#include "portglob.h"
#include "cparms.h"
#include "thread_monitor.h"
#include "ThreadStatusKeeper.h"

#include "dsm2.h"
#include "dbaccess.h"
#include "statistics.h"
#include "utility.h"
#include "debug_timer.h"
#include "database_connection.h"
#include "database_reader.h"

using namespace std;
using namespace Cti;

typedef map<long, CtiStatistics * > id_statistics_map;

static id_statistics_map  statistics;
static bool               new_events = false;
static CtiCriticalSection event_mux;

static void processCollectedStats(bool force);
static void statisticsRecord(ThreadStatusKeeper threadStatus);

struct statistics_event_t
{
    enum EventType
    {
        Attempt,
        Completion,
        Request
    };

    CtiTime time;
    long paoportid;
    long devicepaoid;
    long targetpaoid;
    int result;
    EventType action;
};

typedef deque< statistics_event_t > event_queue_t;

static event_queue_t  statistics_event_queues[2];
static unsigned       active_index = 0;
static event_queue_t *active_event_queue = &statistics_event_queues[active_index];
static CtiTime        last_statistics_midnight_operation;

// Function used for for_each processing on statistics
struct process_statistics_midnight
{
    CtiTime &operation_time;

    process_statistics_midnight(CtiTime &time_to_set) : operation_time(time_to_set)  { };

    void operator()(std::pair<long const ,class CtiStatistics *> &stat_pair)  {  if( stat_pair.second ) stat_pair.second->updateTime(operation_time);  };
};

VOID PerfUpdateThread (PVOID Arg)
{
    ULONG PerfUpdateRate = 3600L;

    CtiTime now;
    LONG delay;

    /* set the priority of this guy high */
    CTISetPriority(PRTYC_TIMECRITICAL, THREAD_PRIORITY_HIGHEST);

    try
    {
        ThreadStatusKeeper threadStatus("Perf Update Thread");

        for(;!PorterQuit;)
        {
            PerfUpdateRate = gConfigParms.getValueAsULong("PORTER_DEVICESTATUPDATERATE", 3600L);

            now = now.now();
            CtiTime nextTime = nextScheduledTimeAlignedOnRate(now, PerfUpdateRate);

            do
            {
                Sleep(1000);

                threadStatus.monitorCheck(CtiThreadRegData::None);

                if(active_event_queue->empty())
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
            statisticsRecord(threadStatus);

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


        processCollectedStats(true);
        statisticsRecord(threadStatus);

        delete_assoc_container(statistics);
        statistics.clear();
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

bool targetIDValid(long deviceid, long targetid)
{
    return targetid && targetid != deviceid;
}

void statisticsNewRequest(long paoportid, long devicepaoid, long targetpaoid, UINT &messageFlags )
{
    messageFlags |= MessageFlag_StatisticsRequested;

    //  constructed with current time
    statistics_event_t tup;

    tup.action = statistics_event_t::Request;
    tup.paoportid = paoportid;
    tup.devicepaoid = devicepaoid;
    tup.targetpaoid = targetpaoid;
    tup.result = 0;

    {
        CtiLockGuard<CtiCriticalSection> guard(event_mux);
        active_event_queue->push_back(tup);
    }
}

void statisticsNewAttempt(long paoportid, long devicepaoid, long targetpaoid, int result,  UINT messageFlags)
{
    if( messageFlags & MessageFlag_StatisticsRequested )
    {
        //  constructed with current time
        statistics_event_t tup;

        tup.action = statistics_event_t::Attempt;
        tup.paoportid = paoportid;
        tup.devicepaoid = devicepaoid;
        tup.targetpaoid = targetpaoid;
        tup.result = result;

        {
            CtiLockGuard<CtiCriticalSection> guard(event_mux);
            active_event_queue->push_back(tup);
        }
    }
    else if( gConfigParms.getValueAsULong("STATISTICS_DEBUGLEVEL", 0, 16) & STATISTICS_REPORT_ON_MSGFLAGS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Statistics not requested for: Port " << paoportid << " Device " << devicepaoid << " / Target " << targetpaoid << endl;
    }
}

void statisticsNewCompletion(long paoportid, long devicepaoid, long targetpaoid, int result, UINT &messageFlags)
{
    if( messageFlags & MessageFlag_StatisticsRequested )
    {
        messageFlags &= ~MessageFlag_StatisticsRequested;

        //  constructed with current time
        statistics_event_t tup;

        tup.action = statistics_event_t::Completion;
        tup.paoportid = paoportid;
        tup.devicepaoid = devicepaoid;
        tup.targetpaoid = targetpaoid;
        tup.result = result;

        {
            CtiLockGuard<CtiCriticalSection> guard(event_mux);
            active_event_queue->push_back(tup);
        }
    }
    else if( gConfigParms.getValueAsULong("STATISTICS_DEBUGLEVEL", 0, 16) & STATISTICS_REPORT_ON_MSGFLAGS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Statistics not requested for: Port " << paoportid << " Device " << devicepaoid << " / Target " << targetpaoid << endl;
    }
}

CtiStatistics *getStatisticsRecord(long paoid)
{
    id_statistics_map::const_iterator itr = statistics.find(paoid);

    if( itr != statistics.end() )
    {
        return itr->second;
    }

    return 0;
}

void processEvent(statistics_event_t &tup)
{
    CtiStatistics *eStat;

    switch( tup.action )
    {
        case statistics_event_t::Request:
        {
            if( eStat = getStatisticsRecord(tup.paoportid) )
            {
                eStat->incrementRequest(tup.time);
            }

            if( eStat = getStatisticsRecord(tup.devicepaoid) )
            {
                eStat->incrementRequest(tup.time);
            }

            if( targetIDValid(tup.devicepaoid, tup.targetpaoid) &&
                (eStat = getStatisticsRecord(tup.targetpaoid)) )
            {
                eStat->incrementRequest(tup.time);
            }

            break;
        }
        case statistics_event_t::Attempt:
        {
            if( eStat = getStatisticsRecord(tup.paoportid) )
            {
                eStat->incrementAttempts(tup.time, tup.result);
            }

            if( eStat = getStatisticsRecord(tup.devicepaoid) )
            {
                eStat->incrementAttempts(tup.time, tup.result);
            }

            if( targetIDValid(tup.devicepaoid, tup.targetpaoid) &&
                (eStat = getStatisticsRecord(tup.targetpaoid)) )
            {
                eStat->incrementAttempts(tup.time, tup.result);
            }

            break;
        }
        case statistics_event_t::Completion:
        {
            if( eStat = getStatisticsRecord(tup.paoportid) )
            {
                eStat->incrementCompletion(tup.time, tup.result);
            }

            if( eStat = getStatisticsRecord(tup.devicepaoid) )
            {
                eStat->incrementCompletion(tup.time, tup.result);
            }

            if( targetIDValid(tup.devicepaoid, tup.targetpaoid) &&
                (eStat = getStatisticsRecord(tup.targetpaoid)) )
            {
                eStat->incrementCompletion(tup.time, tup.result);
            }

            break;
        }
    }
}

void statisticsRecord(ThreadStatusKeeper threadStatus)
{
    if(!new_events)
    {
        return;
    }

    new_events = false;

    try
    {
        vector< CtiStatistics * > dirty_stats;

        id_statistics_map::iterator stat_itr;

        for(stat_itr = statistics.begin(); stat_itr != statistics.end(); stat_itr++)
        {
            if(stat_itr->second && stat_itr->second->isDirty())
            {
                dirty_stats.push_back(CTIDBG_new CtiStatistics(*(stat_itr->second)));
                stat_itr->second->resetDirty();                // It has been cleaned up now...
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " statisticsRecord() : found " << dirty_stats.size() << " dirty statistics records." << endl;
        }

        // Ok, now we stuff the dirtyStatCol out on the DB.  WITHOUT BLOCKING OPERATIONS!
        {
            Cti::Database::DatabaseConnection conn;

            int sCount = 0, total = dirty_stats.size();

            bool dbstat = true;

            vector< CtiStatistics * >::iterator dirty_itr;

            conn.beginTransaction();
            for(dirty_itr = dirty_stats.begin(); dirty_itr != dirty_stats.end(); dirty_itr++)
            {
                dbstat = (*dirty_itr)->Record(conn);

                if( !(++sCount % 1000) )
                {
                    /* The tickle call that needs to happen here follows a different alert mechanism
                       than occurs in the majority of other cases using the ThreadStatusKeeper class.
                       As such, we need to use a deprecated function to bypass the normal functionality
                       of the monitorCheck() function, and use this deprecated forceTickle() function
                       instead.
                     
                       Ideally we would like to be able to send a tickle without forcing us to use this
                       type of support so that future uses of the class aren't able to navigate around
                       the intended uses of this class. This may require adding another private variable
                       to the ThreadStatusKeeper class similar to the sCount parameter and determining
                       whether this tickle occurs from within the class itself. Food for thought.        */
                    threadStatus.forceTickle(CtiThreadRegData::None, CtiThreadMonitor::StandardMonitorTime);

                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " statisticsRecord() : committed " << sCount << " / " << total << " statistics records." << endl;
                }
            }
            conn.commitTransaction();

            if( sCount % 1000 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " statisticsRecord() : committed " << sCount << " / " << total << " statistics records." << endl;
            }

            if( gConfigParms.getValueAsULong("STATISTICS_NUM_DAYS", 120, 10) > 0 )
            {
                sCount = 0;

                conn.beginTransaction();
                for(dirty_itr = dirty_stats.begin(); dirty_itr != dirty_stats.end(); dirty_itr++)
                {
                    dbstat = (*dirty_itr)->InsertDaily(conn);

                    if( !(++sCount % 1000) )
                    {
                        /* The tickle call that needs to happen here follows a different alert mechanism
                           than occurs in the majority of other cases using the ThreadStatusKeeper class.
                           As such, we need to use a deprecated function to bypass the normal functionality
                           of the monitorCheck() function, and use this deprecated forceTickle() function
                           instead.
                         
                           Ideally we would like to be able to send a tickle without forcing us to use this
                           type of support so that future uses of the class aren't able to navigate around
                           the intended uses of this class. This may require adding another private variable
                           to the ThreadStatusKeeper class similar to the sCount parameter and determining
                           whether this tickle occurs from within the class itself. Food for thought.        */
                        threadStatus.forceTickle(CtiThreadRegData::None, CtiThreadMonitor::StandardMonitorTime);

                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " statisticsRecord() : InsertDaily : committed " << sCount << " / " << total << " statistics records." << endl;
                    }
                }
                conn.commitTransaction();

                if( sCount % 1000 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " statisticsRecord() : InsertDaily : committed " << sCount << " / " << total << " statistics records." << endl;
                }

                conn.beginTransaction();
                {
                    CtiStatistics::PruneDaily(conn);
                }
                conn.commitTransaction();

                if( sCount % 1000 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " statisticsRecord() : PruneDaily : complete." << endl;
                }
            }

            delete_container(dirty_stats);
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


/*
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

void initStatisticsRecords(const set<long> &ids)
{
    if( ids.empty() )
    {
        return;
    }

    vector<long> ids_to_load;

    ids_to_load.reserve(ids.size());

    //  make a temporary copy so we can use random-access iterators and distance()
    copy(ids.begin(), ids.end(), back_inserter(ids_to_load));

    vector<long>::iterator id_itr = ids_to_load.begin(),
                           id_end = ids_to_load.end();

    const int max_ids_per_select = gConfigParms.getValueAsInt("MAX_IDS_PER_STATISTICS_SELECT", 1000);

    Timing::DebugTimer timer("initStatisticsRecords()");

    int loaded = 0, created = 0, total = ids_to_load.size();

    while( id_itr != id_end )
    {
        vector<long>::iterator chunk_end = id_itr + min(distance(id_itr, id_end), max_ids_per_select);

        const string sql = CtiStatistics::getSQLCoreStatement(id_itr, chunk_end);

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);
        rdr.execute();

        if( DebugLevel & 0x00020000 || !rdr.isValid() )
        {
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << loggedSQLstring << endl;
            }
        }

        CtiStatistics *statRec;

        set<long> loaded_ids, new_ids;

        vector<CtiStatistics *> restored;

        CtiStatistics::Factory(rdr, restored);

        vector<CtiStatistics *>::iterator restored_itr = restored.begin(),
                                          restored_end = restored.end();

        for( ; restored_itr != restored_end; ++restored_itr )
        {
            statistics.insert(make_pair((*restored_itr)->getID(), *restored_itr));

            loaded_ids.insert((*restored_itr)->getID());
        }

        //  see if any weren't loaded
        set_difference(id_itr, chunk_end, loaded_ids.begin(), loaded_ids.end(), inserter(new_ids, new_ids.begin()));

        if( !new_ids.empty() )
        {
            set<long>::iterator new_id_itr = new_ids.begin(),
                                new_id_end = new_ids.end();

            for( ; new_id_itr != new_id_end; ++new_id_itr )
            {
                statRec = new CtiStatistics(*new_id_itr);

                statistics.insert(make_pair(*new_id_itr, statRec));
            }
        }

        id_itr = chunk_end;

        loaded  += loaded_ids.size();
        created += new_ids.size();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " initStatisticsRecords() : initialized " << loaded + created << " / " << total << " statistics records (" << loaded << " loaded, " << created << " created)." << endl;
        }
    }
}


template <class InputIterator, class OutputIterator>
void collect_ids(InputIterator id_itr, InputIterator id_end, OutputIterator out)
{
    for( ; id_itr != id_end; ++id_itr )
    {
        *out++ = id_itr->paoportid;
        *out++ = id_itr->devicepaoid;

        if( targetIDValid(id_itr->devicepaoid, id_itr->targetpaoid) )
        {
            *out++ = id_itr->targetpaoid;
        }
    }
}

template<class Key, class Value>
struct map_compare
{
    typedef pair<Key, Value> Pair;

    bool operator()(const Key  &lhs, const Key  &rhs) const  {  return lhs < rhs;  };
    bool operator()(const Key  &lhs, const Pair &rhs) const  {  return lhs < rhs.first;  };
    bool operator()(const Pair &lhs, const Key  &rhs) const  {  return lhs.first < rhs;  };
    bool operator()(const Pair &lhs, const Pair &rhs) const  {  return lhs.first < rhs.first;  };
};

void processCollectedStats(bool force)
{
    int inactive_index;
    CtiTime current_stat_time;

    {
        CtiLockGuard<CtiCriticalSection> guard(event_mux);

        inactive_index = active_index;
        //  only place this changes
        active_index ^= 0x01;
        active_event_queue = &statistics_event_queues[active_index];
        current_stat_time.resetToNow();  // This time will be equal to or after all current stats and equal to or before all following stats
    }

    event_queue_t &inactive_event_queue = statistics_event_queues[inactive_index];

    if( gConfigParms.isOpt("PORTER_DOSTATISTICS", "false") )
    {
        inactive_event_queue.clear();

        return;
    }

    set<long> all_ids, ids_to_load;

    collect_ids(inactive_event_queue.begin(),
                inactive_event_queue.end(),
                inserter(all_ids, all_ids.begin()));

    //  find all of the paoIDs that aren't yet loaded
    set_difference(all_ids.begin(), all_ids.end(),
                   statistics.begin(), statistics.end(),
                   inserter(ids_to_load, ids_to_load.begin()),
                   map_compare<long, CtiStatistics *>());

    initStatisticsRecords(ids_to_load);

    int sCount = 0, total = inactive_event_queue.size();

    new_events |= !inactive_event_queue.empty();

    // If force is set, WE WILL DO THEM ALL.
    while( !inactive_event_queue.empty() && (!PorterQuit || force) )
    {
        processEvent(inactive_event_queue.front());

        inactive_event_queue.pop_front();

        if( !(++sCount % 1000) && (getDebugLevel() & DEBUGLEVEL_STATISTICS) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " processCollectedStats() : processed " << sCount << " / " << total << " statistics events." << endl;
        }
    }

    //Everything has been processed. Now, if necessary, we tell all stats to pass midnight.
    if( current_stat_time.day() != last_statistics_midnight_operation.day() )
    {
        last_statistics_midnight_operation.resetToNow();
        for_each(statistics.begin(), statistics.end(), process_statistics_midnight(current_stat_time));
        new_events = true;

    }

    if( sCount > 1000 && (getDebugLevel() & DEBUGLEVEL_STATISTICS) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " processCollectedStats() : complete, processed " << sCount << " / " << total << " statistics events." << endl;
    }
}
