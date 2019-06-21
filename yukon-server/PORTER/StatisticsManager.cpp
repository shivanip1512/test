#include "precompiled.h"

#include "StatisticsManager.h"

#include "PaoStatistics.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_transaction.h"
#include "InvalidReaderException.h"
#include "ctidate.h"
#include "debug_timer.h"
#include "c_port_interface.h"  //  for the Porter DeviceManager and PortManager instances
#include "mgr_device.h"
#include "mgr_port.h"

#include "database_exceptions.h"
#include "database_bulk_writer.h"
#include "tbl_dyn_paostatistics.h"

#include <boost/range/adaptor/transformed.hpp>

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

void StatisticsManager::enqueueEvent(statistics_event_t::EventType action, YukonError_t result, long port_id, long device_id, long target_id)
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

    enqueueEvent(statistics_event_t::Request, ClientErrors::None, port_id, device_id, target_id);
}

void StatisticsManager::newAttempt(long port_id, long device_id, long target_id, YukonError_t result, unsigned messageFlags)
{
    if( messageFlags & MessageFlag_StatisticsRequested )
    {
        enqueueEvent(statistics_event_t::Attempt, result, port_id, device_id, target_id);
    }
    else if( gConfigParms.getValueAsULong("STATISTICS_DEBUGLEVEL", 0, 16) & STATISTICS_REPORT_ON_MSGFLAGS )
    {
        CTILOG_DEBUG(dout, "Statistics not requested for: Port "<< port_id <<" Device "<< device_id <<" / Target "<< target_id);
    }
}

void StatisticsManager::newCompletion(long port_id, long device_id, long target_id, YukonError_t result, unsigned &messageFlags)
{
    if( messageFlags & MessageFlag_StatisticsRequested )
    {
        messageFlags &= ~MessageFlag_StatisticsRequested;

        enqueueEvent(statistics_event_t::Completion, result, port_id, device_id, target_id);
    }
    else if( gConfigParms.getValueAsULong("STATISTICS_DEBUGLEVEL", 0, 16) & STATISTICS_REPORT_ON_MSGFLAGS )
    {
        CTILOG_DEBUG(dout, "Statistics not requested for: Port "<< port_id <<" Device "<< device_id <<" / Target "<< target_id);
    }
}

void StatisticsManager::deleteRecord(const long pao_id)
{
    enqueueEvent(statistics_event_t::Deletion, ClientErrors::None, 0, 0, pao_id);
}

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
        int processed = 0, total = _inactive_event_queue->size();

        for each( const statistics_event_t &evt in *_inactive_event_queue )
        {
            processEvent(evt);

            if( !(++processed % 1000) && (getDebugLevel() & DEBUGLEVEL_STATISTICS) )
            {
                CTILOG_DEBUG(dout, "processed "<< processed <<" / "<< total <<" statistics events");
            }
        }

        _inactive_event_queue->clear();

        if( processed > 1000 && (getDebugLevel() & DEBUGLEVEL_STATISTICS) )
        {
            CTILOG_DEBUG(dout, "complete - processed "<< processed <<" / "<< total <<" statistics events");
        }
    }
}

PaoStatistics *StatisticsManager::getPaoStatistics(const long pao_id)
{
    id_statistics_map::iterator itr = _pao_statistics.find(pao_id);

    if( itr != _pao_statistics.end() )
    {
        return itr->second;
    }

    //  Only make a new record if the ID exists in the DeviceManager or PortManager
    if( DeviceManager.getDeviceByID(pao_id) || PortManager.getPortById(pao_id) )
    {
        PaoStatistics *p = new PaoStatistics(pao_id);

        _pao_statistics.insert(std::make_pair(p->getPaoId(), p));

        return p;
    }

    return 0;
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
            if( PaoStatistics *p = getPaoStatistics(evt.pao_id) )
            {
                p->incrementRequests(evt.time);
            }

            break;
        }
        case statistics_event_t::Attempt:
        {
            if( PaoStatistics *p = getPaoStatistics(evt.pao_id) )
            {
                p->incrementAttempts(evt.time, evt.result);
            }

            break;
        }
        case statistics_event_t::Completion:
        {
            if( PaoStatistics *p = getPaoStatistics(evt.pao_id) )
            {
                p->incrementCompletion(evt.time, evt.result);
            }

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
    // Check in with the monitor first

    threadKeeper.monitorCheck(CtiThreadMonitor::StandardMonitorTime);

    // Connect to the database

    Database::DatabaseConnection conn;

    if ( ! conn.isValid() )
    {
        CTILOG_ERROR(dout, "Invalid Connection to Database");
        return;
    }

    // Write out the statistics

    writeAllRecords(conn);

    // Delete the expired records

    pruneDaily(conn);
}

bool StatisticsManager::writeAllRecords( Database::DatabaseConnection & conn )
{
    id_statistics_map   writeableStatistics;

    writeableStatistics.swap( _pao_statistics );

    // convert the PaoStatistics blobs into a collection of CtiTableDynamicPaoStatistics records

    std::vector<std::unique_ptr<CtiTableDynamicPaoStatistics>>  tableRecords;

    for ( auto & [ID, paoStatistic] : writeableStatistics )
    {
        paoStatistic->collectRecords( tableRecords );
    }

    Cti::Database::DatabaseBulkAccumulator<9>
        ba(
            conn.getClientType(),
            CtiTableDynamicPaoStatistics::getTempTableSchema(),
            3,
            "DynamicPAOStatistics",
            "DynamicPaoStatistics",
            "DynamicPAOStatisticsId",
            "YukonPAObject" );

    const auto asRowSource =
        boost::adaptors::transformed(
            []( const std::unique_ptr<CtiTableDynamicPaoStatistics> & p )
            {
                return p.get();
            } );

    auto rowSources = boost::copy_range<std::vector<const Cti::RowSource*>>( tableRecords | asRowSource );

    CTILOG_INFO( dout, "Inspecting " << tableRecords.size() << " statistics records for "
                        << writeableStatistics.size() << " devices.");

    try
    {
        auto rejectedRows = ba.writeRows( conn, std::move( rowSources ) );

        if ( ! rejectedRows.empty() )
        {
            CTILOG_WARN( dout, "Failed to MERGE statistics records for " << rejectedRows.size() << " devices.");

            // dump the rejected tableRecords to the porter log

            for ( const auto & record : tableRecords )
            {
                if ( rejectedRows.count( record->_pAObjectId ) )
                {
                    CTILOG_WARN( dout, "Rejected record: " << *record );
                }
            }
        }
    }
    catch ( const Cti::Database::DatabaseException & ex )
    {
        CTILOG_EXCEPTION_ERROR(dout, ex, "Unable to MERGE rows in DynamicPaoStatistics");
    }

    return true;
}

unsigned StatisticsManager::daysFromHours (const unsigned hours)
{
    return (hours + 23) / 24;
}

unsigned StatisticsManager::daysFromMonths(const unsigned months)
{
    //  This is an approximate, round-up version.
    return (months % 12 * 31) + (months / 12 * 366);
}

bool StatisticsManager::pruneDaily(Database::DatabaseConnection &conn)
{
    static const unsigned defaultHours  =  24;
    static const unsigned defaultDays   = 123;
    static const unsigned defaultMonths =   2;

    const unsigned retainedHours  = gConfigParms.getValueAsULong("STATISTICS_NUM_HOURS");
    const unsigned retainedDays   = gConfigParms.getValueAsULong("STATISTICS_NUM_DAYS");
    const unsigned retainedMonths = gConfigParms.getValueAsULong("STATISTICS_NUM_MONTHS");

    const CtiDate today;

    static const std::string sql =
        "DELETE FROM "
            "DynamicPaoStatistics "
        "WHERE "
            "(StartDateTime < ? AND StatisticType = 'Hourly') OR "
            "(StartDateTime < ? AND StatisticType = 'Daily') OR "
            "(StartDateTime < ? AND StatisticType = 'Monthly')";

    Database::DatabaseWriter deleter(conn, sql);

    deleter << today - daysFromHours (retainedHours  ? retainedHours  : defaultHours);
    deleter << today -               (retainedDays   ? retainedDays   : defaultDays);
    deleter << today - daysFromMonths(retainedMonths ? retainedMonths : defaultMonths);

    if( getDebugLevel() & DEBUGLEVEL_STATISTICS )
    {
        CTILOG_DEBUG(dout, "Executing deletion SQL query: "<< deleter.asString());
    }

    return deleter.execute();
}

}
}

