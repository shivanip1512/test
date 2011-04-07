#include "yukon.h"

#include "StatisticsManager.h"

#include "PaoStatistics.h"
#include "database_writer.h"
#include "InvalidReaderException.h"
#include "ctidate.h"
#include "debug_timer.h"

#define STATISTICS_REPORT_ON_MSGFLAGS  0x00000001
#define STATISTICS_COMPENSATED_RESULTS 0x00000010
#define STATISTICS_REPORT_ON_RESULTS   0x00000020

using Cti::Porter::Statistics::PaoStatistics;

namespace Cti {
namespace Porter {

StatisticsManager::StatisticsManager()
{
    _active_event_queue   = &_event_queues[0];
    _inactive_event_queue = &_event_queues[1];
}


StatisticsManager::~StatisticsManager()
{
    delete_assoc_container(_pao_statistics);
}


void StatisticsManager::enqueueEvent(statistics_event_t::EventType action, int result, long port_id, long device_id, long target_id)
{
    CtiLockGuard<CtiCriticalSection> guard(_event_queue_lock);

    statistics_event_t evt;

    evt.action = action;
    evt.result = result;

    //  The event for the port
    evt.pao_id = port_id;
    _active_event_queue->push_back(evt);

    //  The event for the device
    evt.pao_id = device_id;
    _active_event_queue->push_back(evt);

    //  The event for the target device
    if( target_id && target_id != device_id )
    {
        evt.pao_id = target_id;
        _active_event_queue->push_back(evt);
    }
}

void StatisticsManager::newRequest(long port_id, long device_id, long target_id, unsigned &messageFlags)
{
    messageFlags |= MessageFlag_StatisticsRequested;

    enqueueEvent(statistics_event_t::Request, 0, port_id, device_id, target_id);
}

void StatisticsManager::newAttempt(long port_id, long device_id, long target_id, int result, unsigned messageFlags)
{
    if( messageFlags & MessageFlag_StatisticsRequested )
    {
        enqueueEvent(statistics_event_t::Attempt, result, port_id, device_id, target_id);
    }
    else if( gConfigParms.getValueAsULong("STATISTICS_DEBUGLEVEL", 0, 16) & STATISTICS_REPORT_ON_MSGFLAGS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Statistics not requested for: Port " << port_id << " Device " << device_id << " / Target " << target_id << endl;
    }
}

void StatisticsManager::newCompletion(long port_id, long device_id, long target_id, int result, unsigned &messageFlags)
{
    if( messageFlags & MessageFlag_StatisticsRequested )
    {
        messageFlags &= ~MessageFlag_StatisticsRequested;

        enqueueEvent(statistics_event_t::Completion, result, port_id, device_id, target_id);
    }
    else if( gConfigParms.getValueAsULong("STATISTICS_DEBUGLEVEL", 0, 16) & STATISTICS_REPORT_ON_MSGFLAGS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Statistics not requested for: Port " << port_id << " Device " << device_id << " / Target " << target_id << endl;
    }
}


void StatisticsManager::deleteRecord(const long pao_id)
{
    enqueueEvent(statistics_event_t::Deletion, 0, 0, 0, pao_id);
}


template<class Key, class Value>
struct map_compare
{
    typedef std::pair<Key, Value> Pair;

    bool operator()(const Key  &lhs, const Key  &rhs) const  {  return lhs < rhs;  };
    bool operator()(const Key  &lhs, const Pair &rhs) const  {  return lhs < rhs.first;  };
    bool operator()(const Pair &lhs, const Key  &rhs) const  {  return lhs.first < rhs;  };
    bool operator()(const Pair &lhs, const Pair &rhs) const  {  return lhs.first < rhs.first;  };
};


void StatisticsManager::processEvents(ThreadStatusKeeper &threadKeeper)
{
    {
        CtiLockGuard<CtiCriticalSection> guard(_event_queue_lock);

        std::swap(_active_event_queue, _inactive_event_queue);
    }

    if( gConfigParms.isOpt("PORTER_DOSTATISTICS", "false") )
    {
        _inactive_event_queue->clear();

        return;
    }

    if( ! _inactive_event_queue->empty() )
    {
        std::set<long> event_ids, ids_to_load;

        // Grab all of the pao IDs from the events in the queue
        std::transform(
            _inactive_event_queue->begin(),
            _inactive_event_queue->end(),
            inserter(event_ids, event_ids.begin()),
            bind(&statistics_event_t::pao_id, _1));

        //  find all of the paoIDs that aren't yet loaded
        std::set_difference(
            event_ids.begin(),
            event_ids.end(),
            _pao_statistics.begin(),
            _pao_statistics.end(),
            inserter(ids_to_load, ids_to_load.begin()),
            map_compare<long, PaoStatistics *>());

        loadPaoStatistics(ids_to_load);

        int processed = 0, total = _inactive_event_queue->size();

        for each( const statistics_event_t &evt in *_inactive_event_queue )
        {
            processEvent(evt);

            if( !(++processed % 1000) && (getDebugLevel() & DEBUGLEVEL_STATISTICS) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " processCollectedStats() : processed " << processed << " / " << total << " statistics events." << endl;
            }
        }

        _inactive_event_queue->clear();

        if( processed > 1000 && (getDebugLevel() & DEBUGLEVEL_STATISTICS) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " processCollectedStats() : complete, processed " << processed << " / " << total << " statistics events." << endl;
        }
    }
}


PaoStatistics &StatisticsManager::getPaoStatistics(long pao_id)
{
    id_statistics_map::iterator itr = _pao_statistics.find(pao_id);

    if( itr != _pao_statistics.end() )
    {
        return *itr->second;
    }

    PaoStatistics *p = new PaoStatistics(pao_id);

    _pao_statistics.insert(std::make_pair(p->getPaoId(), p));

    return *p;
}


void StatisticsManager::deletePaoStatistics(const long pao_id)
{
    id_statistics_map::iterator itr = _pao_statistics.find(pao_id);

    if( itr != _pao_statistics.end() )
    {
        delete itr->second;

        _pao_statistics.erase(itr);
    }
}


void StatisticsManager::processEvent(const statistics_event_t &evt)
{
    switch( evt.action )
    {
        case statistics_event_t::Request:
        {
            getPaoStatistics(evt.pao_id).incrementRequests(evt.time);

            break;
        }
        case statistics_event_t::Attempt:
        {
            getPaoStatistics(evt.pao_id).incrementAttempts(evt.time, evt.result);

            break;
        }
        case statistics_event_t::Completion:
        {
            getPaoStatistics(evt.pao_id).incrementCompletion(evt.time, evt.result);

            break;
        }
        case statistics_event_t::Deletion:
        {
            deletePaoStatistics(evt.pao_id);

            break;
        }
    }
}


void StatisticsManager::writeRecords(ThreadStatusKeeper &threadKeeper)
{
    runWriterThreads(gConfigParms.getValueAsULong("PORTER_STATISTICS_WRITER_THREADS", 4), threadKeeper);

    Database::DatabaseConnection conn;

    pruneDaily(conn);
}


void StatisticsManager::runWriterThreads(unsigned num_threads, ThreadStatusKeeper &threadKeeper)
{
    boost::thread_group writers;

    id_statistics_map::const_iterator pos = _pao_statistics.begin();

    const unsigned stat_size = _pao_statistics.size();  //  say, 570
    const unsigned chunk_size = (stat_size / num_threads) + 1;  //  say, (570 / 5) + 1, or 115

    //  We may eventually need to randomize the statistics records that each thread receives,
    //    since system-wide reads can (acidentally?) match the map ordering.
    //  That would unfairly load one or more of the writer threads.
    for( unsigned thread_num = 2; thread_num <= num_threads; ++thread_num )
    {
        id_statistics_map::const_iterator start = pos;

        std::advance(pos, chunk_size);

        writers.create_thread(boost::bind(&StatisticsManager::writeRecordRange, thread_num, chunk_size, start, pos, (ThreadStatusKeeper *)0));
    }

    const unsigned last_chunk_size = stat_size - chunk_size * (num_threads - 1);

    //  The math is easier this way.
    writeRecordRange(1, last_chunk_size, pos, _pao_statistics.end(), &threadKeeper);

    if( num_threads > 1 )
    {
        writers.join_all();
    }
}

void StatisticsManager::writeRecordRange(unsigned thread_num, unsigned chunk_size, const id_statistics_map::const_iterator begin, const id_statistics_map::const_iterator end, ThreadStatusKeeper *threadKeeper)
{
    if( begin != end )
    {
        Database::DatabaseConnection conn;

        conn.beginTransaction();

        unsigned records_inspected = 0, dirty_records = 0, rows_written = 0;

        id_statistics_map::const_iterator itr = begin;

        while( itr != end )
        {
            PaoStatistics &p = *(itr++->second);

            ++records_inspected;

            if( p.isDirty() )
            {
                Database::DatabaseWriter writer(conn);

                rows_written += p.writeRecords(writer);

                if( !(++dirty_records % 1000) )
                {
                    threadKeeper && threadKeeper->monitorCheck(CtiThreadMonitor::StandardMonitorTime);

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " StatisticsManager::writeRecordRange() : thread " << thread_num << " ";
                        dout << "inspected " << records_inspected << " / " << chunk_size << " statistics records; ";
                        dout << dirty_records << " records have written a total of " << rows_written << " rows." << endl;
                    }
                }
            }
        }

        conn.commitTransaction();

        if( dirty_records )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " StatisticsManager::writeRecordRange() : thread " << thread_num << " finished, ";
            dout << "inspected " << chunk_size << " statistics records; ";
            dout << dirty_records << " records wrote a total of " << rows_written << " rows." << endl;
        }
    }
}


bool StatisticsManager::pruneDaily(Database::DatabaseConnection &conn)
{
    unsigned numDays = gConfigParms.getValueAsULong("STATISTICS_NUM_DAYS", 120, 10);

    if( numDays == 0 )
    {
        numDays = 120;
    }

    numDays++;

    static const std::string sql =
        "DELETE FROM "
            "DynamicPaoStatistics "
        "WHERE "
            "(StartDateTime < ? AND StatisticType = 'Hourly') OR "
            "(StartDateTime < ? AND StatisticType = 'Daily') OR "
            "(StartDateTime < ? AND StatisticType = 'Monthly')";

    Database::DatabaseWriter deleter(conn, sql);
    CtiDate today;

    //  Hourly
    deleter << today - 2;

    //  Daily
    deleter << today - numDays;

    //  Monthly
    deleter << today - 62;

    return deleter.execute();
}


void StatisticsManager::loadPaoStatistics(const std::set<long> &pao_ids)
{
    //  Grab at least ten at once, even if they specify a negative number
    const int max_ids_per_select = std::max(10, gConfigParms.getValueAsInt("MAX_IDS_PER_STATISTICS_SELECT", 1000));

    Timing::DebugTimer timer("StatisticsManager::loadPaoStatistics()");

    unsigned pos = 0;
    const unsigned total = pao_ids.size();

    std::set<long>::const_iterator pao_id_itr = pao_ids.begin();

    Cti::Database::DatabaseConnection connection;

    const CtiTime reader_time;

    while( pao_id_itr != pao_ids.end() )
    {
        const unsigned chunk_size = std::min<unsigned>(total - pos, max_ids_per_select);

        std::set<long>::const_iterator chunk_begin = pao_id_itr;
        std::set<long>::const_iterator chunk_end   = pao_id_itr;

        std::advance(chunk_end, chunk_size);

        Cti::Database::DatabaseReader rdr(connection);

        PaoStatistics::buildDatabaseReader(rdr, reader_time, chunk_begin, chunk_end);

        pao_id_itr = chunk_end;
        pos       += chunk_size;

        rdr.execute();

        if( DebugLevel & 0x00020000 || !rdr.isValid() )
        {
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " StatisticsManager::loadPaoStatistics() : error executing query \"" << loggedSQLstring << "\"\n";
            }
        }

        try
        {
            while( rdr() )
            {
                PaoStatistics *p = new PaoStatistics(reader_time, rdr);

                _pao_statistics.insert(std::make_pair(p->getPaoId(), p));
            }
        }
        catch(Database::InvalidReaderException &e)
        {
            //  assume the reader is toast - we have to bail
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " StatisticsManager::loadPaoStatistics() : bad reader when restoring PaoStatistics object from query \"" << loggedSQLstring << "\"\n";
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " StatisticsManager::loadPaoStatistics() : processed " << pos << " / " << total << " statistics records" << endl;
        }
    }
}

}
}

