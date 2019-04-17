#include "precompiled.h"

#include "rph_archiver.h"

#include "msg_pdata.h"

#include "millisecond_timer.h"
#include "logger.h"
#include "ThreadStatusKeeper.h"

#include "tbl_rawpthistory.h"

#include "database_connection.h"
#include "database_bulk_writer.h"
#include "database_exceptions.h"

#include "coroutine_util.h"
#include "std_helper.h"

#include <boost/range/adaptor/indirected.hpp>
#include <boost/range/adaptor/filtered.hpp>
#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/numeric.hpp>

#include <unordered_map>

using Cti::Logging::Vector::operator<<;
using namespace std::string_literals;

namespace Cti {
namespace Dispatch {

RawPointHistoryArchiver::RawPointHistoryArchiver(const bool &shutdownOnThreadTimeout, void (*shutdownFunc)(const std::string& who))
    :   _archiverThread { [this]{ mainThread(); } },
        ShutdownOnThreadTimeout(shutdownOnThreadTimeout),
        ShutdownFunc(shutdownFunc)
{}

RawPointHistoryArchiver::~RawPointHistoryArchiver() = default;

namespace {

//  Spreads out intervals by a given factor.  
//    For example, takes a 1 hour bitmask of 
//      01011011
//    and given a factor of 4, turns it to a 15 minute bitmask of
//      00000001000000010001000000010001
//  Only adjusts as many bits as will fit into a 64-bit ULL.
//    Will miss a couple bits on the top end of the 64 bits for uneven factors (like 3, which only converts 21 bits into 63).
//    Since this method is currently feeding a 37-bit field, that's not a big deal yet.
unsigned long long adjust_intervals(unsigned long long intervals, unsigned factor)
{
    auto mask = 0x1ull << (64 / factor);

    unsigned long long output = 0;

    while( mask )
    {
        output <<= factor;
        output |= !!(intervals & mask);
        mask >>= 1;
    }

    return output;
}

}

bool RawPointHistoryArchiver::writeArchiveDataToDB(Cti::Database::DatabaseConnection& conn, const WriteMode wm)
{
    const unsigned MinRowsToWrite = 10;
    const unsigned ChunkSize = 10000;

    const unsigned rowsWaiting = archiverQueueSize();

    if( ! rowsWaiting )
    {
        return false;
    }

    if( wm == WriteMode::Threshold && rowsWaiting < MinRowsToWrite )
    {
        return false;
    }

    auto rowsToWrite = getFilteredRows(wm == WriteMode::All ? rowsWaiting : ChunkSize);

    if( rowsToWrite.empty() )
    {
        return false;
    }

    try
    {
        Cti::Timing::MillisecondTimer timer;

        auto trackingIds = writeRawPointHistory(conn, std::move(rowsToWrite));

        if( ! trackingIds.empty() )
        {
            const unsigned rowsWritten = trackingIds.size();
            const unsigned rowsRemaining = archiverQueueSize();

            std::string trackingInfo = 
                boost::accumulate(trackingIds, ""s, [](std::string s1, std::string s2) {
                return
                    s1.empty() ? s2 : 
                    s2.empty() ? s1 : 
                    s1 + " " + s2;
                });

            if( ! trackingInfo.empty() )
            {
                trackingInfo = " Tracking: " + trackingInfo;
            }

            CTILOG_INFO(dout, "RawPointHistory transaction completed in " << timer.elapsed() << "ms. Inserted " << rowsWritten << " rows. remaining: " << rowsRemaining << " rows." << trackingInfo);

            return rowsRemaining > MinRowsToWrite;
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return false;
}


auto RawPointHistoryArchiver::getFilteredRows(size_t maximum) -> std::vector<std::unique_ptr<CtiTableRawPointHistory>>
{
    std::lock_guard<std::mutex> guard(_archiverLock);

    std::vector<std::unique_ptr<CtiTableRawPointHistory>> rows;

    auto itr = std::make_move_iterator(_archiverQueue.begin());

    FormattedList duplicates;

    std::vector<std::string> ancient, future, beyondCache;

    for( const auto now = std::time(nullptr); maximum-- && itr.base() != _archiverQueue.end(); ++itr )
    {
        const auto status = getArchiveStatus(**itr, now);

        if( status == ArchiveStatus::Duplicate )
        {
            duplicates.add(std::to_string((*itr)->pointId)) << (*itr)->time << " - " << (*itr)->value << " " << (*itr)->trackingId;
            continue;
        }

        std::vector<std::string>* classification =
            status == ArchiveStatus::Ancient     ? &ancient :
            status == ArchiveStatus::BeyondCache ? &beyondCache :
            status == ArchiveStatus::Future      ? &future :
            nullptr;

        if( classification )
        {
            auto info = std::to_string((*itr)->pointId);

            if( const auto& trackingId = (*itr)->trackingId; ! trackingId.empty() )
            {
                info += " " + trackingId;
            }

            classification->emplace_back(info);
        }

        rows.push_back(*itr);
    }

    _archiverQueue.erase(_archiverQueue.begin(), itr.base());

    if( ! duplicates.empty() )
    {
        CTILOG_INFO(dout, "Detected duplicates:" << duplicates);
    }
    if( ! future.empty() )
    {
        CTILOG_INFO(dout, "Received pointids more than 1 day in the future " << future);
    }
    if( ! ancient.empty() )
    {
        CTILOG_INFO(dout, "Received pointids more than 1 year old " << ancient);
    }
    if( ! beyondCache.empty() )
    {
        CTILOG_DEBUG(dout, "Received pointids beyond cache depth " << beyondCache);
    }

    return rows;
}


auto RawPointHistoryArchiver::getArchiveStatus(const CtiTableRawPointHistory& row, const time_t now) -> ArchiveStatus
{
    constexpr int MinutesPerYear = 365 * 24 * 60;
    constexpr int IntervalBits = 37;
    constexpr int MaxInterval = 60;

    static const auto makeArchiveEpoch = [MinutesPerYear](const time_t now){ return now / 60 - MinutesPerYear; };  //  1 year before startup

    static auto ArchiveEpoch = makeArchiveEpoch(now);

    struct last_seen
    {
        //  28 bits for timestamp info:
        //    21 bits to encode minute-resolution timestamps of about 1 year prior/3 years after startup:
        //      365 * 24 * 60 = 525,600; 2^21=2,097,152, or ~4 years of minutes
        //    6 bits to encode interval
        //      2^6=64
        //  Remaining 37 bits for last-seen cache

        unsigned long long latest_timestamp : 21;
        unsigned long long interval_minutes :  6;
        unsigned long long intervals        : 37;
    };

    static std::unordered_map<long, last_seen> value_cache;

    const auto utcSeconds = row.time.seconds();

    //  don't cache rows with millis or non-integral minutes
    if( row.millis || (utcSeconds % 60) )
    {
        return ArchiveStatus::NotInCache;
    }

    // Sanity check on this data - it might be far-flung-future
    if( utcSeconds >= (now + 86400) )
    {
        Cti::FormattedList l;
        l.add("Epoch") << CtiTime(ArchiveEpoch * 60);
        l.add("Incoming timestamp") << row.time;
        l.add("Incoming pointid") << row.pointId;

        CTILOG_TRACE(dout, "Received pointdata more than 1 day in the future" << l);

        return ArchiveStatus::Future;
    }

    const auto utcMinutes = utcSeconds / 60;

    if( utcMinutes < ArchiveEpoch )
    {
        Cti::FormattedList l;
        l.add("Epoch") << CtiTime(ArchiveEpoch * 60);
        l.add("Incoming timestamp") << row.time;
        l.add("Incoming pointid") << row.pointId;

        CTILOG_TRACE(dout, "Received pointdata older than epoch" << l);

        return ArchiveStatus::Ancient;
    }

    unsigned long long epochMinutes = utcMinutes - ArchiveEpoch;

    //  if the incoming value won't fit in the 21 bit timestamp, reset the epoch and clear the cache
    if( epochMinutes >= (1 << 21) )
    {
        auto newArchiveEpoch = makeArchiveEpoch(now);

        Cti::FormattedList l;
        l.add("Previous epoch")     << CtiTime(ArchiveEpoch * 60);
        l.add("New epoch")          << CtiTime(newArchiveEpoch * 60);
        l.add("Incoming timestamp") << row.time;
        l.add("Incoming pointid") << row.pointId;

        CTILOG_WARN(dout, "Epoch exceeded, resetting archive cache" << l);

        ArchiveEpoch = newArchiveEpoch;

        epochMinutes = utcMinutes - ArchiveEpoch;

        value_cache.clear();
    }

    auto record = Cti::mapFindRef(value_cache, row.pointId);

    if( ! record )
    {
        value_cache.emplace(row.pointId, last_seen{ epochMinutes, 0, 1 });  //  just saw it, mark the first occurrence

        return ArchiveStatus::NotInCache;
    }

    //  timestamp was equal, we've seen it
    if( epochMinutes == record->latest_timestamp )
    {
        return ArchiveStatus::Duplicate;
    }

    unsigned long long minutesApart = 
        epochMinutes > record->latest_timestamp 
            ? epochMinutes - record->latest_timestamp
            : record->latest_timestamp - epochMinutes;

    //  No interval yet - try to determine the interval based on how far apart these records are
    if( ! record->interval_minutes )
    {
        record->interval_minutes = ((minutesApart - 1) % MaxInterval) + 1;
        record->latest_timestamp = std::max<long long>(epochMinutes, record->latest_timestamp);
        record->intervals <<= minutesApart / record->interval_minutes;

        return ArchiveStatus::NotInCache;
    }

    //  If the records aren't a multiple of the interval apart, recalculate the interval
    if( minutesApart % record->interval_minutes )
    {
        //  find the new interval - note that interval_minutes is less than MaxInterval at this point, so the GCD will be, too
        const auto new_interval_minutes = Cti::find_gcd(minutesApart, record->interval_minutes);
        //  adjust the existing intervals to the new interval
        const auto new_intervals = adjust_intervals(record->intervals, record->interval_minutes / new_interval_minutes);

        Cti::FormattedList l;
        l.add("Incoming timestamp") << row.time;
        l.add("Incoming pointid") << row.pointId;
        l.add("Old interval") << record->interval_minutes << " minutes";
        l.add("New interval") << new_interval_minutes << " minutes";
        l.add("Latest timestamp") << CtiTime((record->latest_timestamp + ArchiveEpoch) * 60);
        l.add("Old cache") << std::hex << std::setw((IntervalBits + 3) / 4) << record->intervals;
        l.add("New cache") << std::hex << std::setw((IntervalBits + 3) / 4) << new_intervals;
        l.add("Interval bits") << IntervalBits;

        //  If we're going below 5 minutes, issue a warning
        const auto log_level =
            (new_intervals < 5)
            ? Cti::Logging::Logger::Warn
            : Cti::Logging::Logger::Debug;

        CTILOG_LOG(log_level, dout, "New interval detected" << l);

        record->interval_minutes = new_interval_minutes;
        record->intervals        = new_intervals;
    }

    //  If the record is after our latest record, we obviously haven't seen it yet
    if( epochMinutes > record->latest_timestamp )
    {
        const auto intervals = (epochMinutes - record->latest_timestamp) / record->interval_minutes;

        record->latest_timestamp = epochMinutes;
        record->intervals <<= intervals;

        return ArchiveStatus::NotInCache;
    }

    const auto intervals = (record->latest_timestamp - epochMinutes) / record->interval_minutes;

    //  we only go back 37 intervals
    if( intervals >= IntervalBits )
    {
        Cti::FormattedList l;
        l.add("Incoming timestamp") << row.time;
        l.add("Incoming pointid") << row.pointId;
        l.add("Interval") << record->interval_minutes << " minutes";
        l.add("Latest timestamp") << CtiTime((record->latest_timestamp + ArchiveEpoch) * 60);
        l.add("Cache") << std::hex << std::setw((IntervalBits + 3) / 4) << record->intervals;
        l.add("Interval bits") << IntervalBits;

        CTILOG_TRACE(dout, "Incoming value older than cache depth" << l)

        return ArchiveStatus::BeyondCache;
    }

    const auto mask = 0x1ull << intervals;  //  declare the 1 as unsigned long long so it can shift more than 32

    const bool seen = record->intervals & mask;

    record->intervals |= mask;

    return seen 
        ? ArchiveStatus::Duplicate
        : ArchiveStatus::NotInCache;
}


void RawPointHistoryArchiver::start()
{
    _archiverThread.start();
}

bool RawPointHistoryArchiver::isRunning()
{
    return _archiverThread.isRunning();
}

void RawPointHistoryArchiver::interrupt()
{
    _archiverThread.interrupt();
}

bool RawPointHistoryArchiver::tryJoinFor(const std::chrono::seconds duration)
{
    return _archiverThread.tryJoinFor(Cti::Timing::Chrono::seconds(duration.count()));
}

void RawPointHistoryArchiver::terminate()
{
    _archiverThread.terminateThread();
}


void RawPointHistoryArchiver::mainThread()
{
    ThreadStatusKeeper threadStatus("RawPointHistory Writer Thread");

    CTILOG_INFO(dout, "Dispatch RawPointHistory Writer Thread starting");

    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);

    const unsigned ThirtySeconds = 30 * 1000;
    unsigned loopTimer = 0;

    Cti::Timing::MillisecondTimer timer;

    try
    {
        unsigned resetDelay = 1;

        while( true )
        {
            Database::DatabaseConnection conn;

            if( ! conn.isValid() )
            {
                CTILOG_WARN(dout, "Database connection invalid, attempting reset in " << resetDelay << " seconds");

                WorkerThread::sleepFor(Timing::Chrono::seconds(resetDelay));

                resetDelay = std::min(resetDelay * 2, 30U);  //  1, 2, 4, 8, 16, 30

                continue;
            }

            resetDelay = 1;

            while( conn.isValid() )
            {
                try
                {
                    WriteMode wm = WriteMode::Threshold;

                    if( loopTimer > ThirtySeconds )
                    {
                        loopTimer %= ThirtySeconds;

                        //  guaranteed write once every 30 seconds
                        wm = WriteMode::Chunk;

                        if( ShutdownOnThreadTimeout )
                        {
                            threadStatus.monitorCheck(ShutdownFunc);
                        }
                        else
                        {
                            threadStatus.monitorCheck(CtiThreadRegData::None);
                        }
                    }

                    const bool MoreWaiting = writeArchiveDataToDB(conn, wm);

                    const unsigned MinimumLoopTime = MoreWaiting ? 50 : 1000;  //  wait 50 ms if more work waiting, 1 second otherwise

                    const unsigned elapsed = timer.elapsed();

                    if( elapsed < MinimumLoopTime )
                    {
                        WorkerThread::sleepFor(Timing::Chrono::milliseconds(MinimumLoopTime - elapsed));
                    }

                    loopTimer += timer.elapsed();
                    timer.reset();
                }
                catch( const WorkerThread::Interrupted& ex )
                {
                    CTILOG_INFO(dout, "Interrupted, shutting down");

                    //  Write anything remaining before we exit.
                    writeArchiveDataToDB(conn, WriteMode::All);

                    // get out of here...
                    CTILOG_INFO(dout, "Dispatch RawPointHistory Writer Thread shutting down");

                    return;
                }
            }
        }
    }
    catch( const WorkerThread::Interrupted& ex )
    {
        CTILOG_ERROR(dout, "Interrupted, no DB connection - shutting down without writing");
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_INFO(dout, "Dispatch RawPointHistory Writer Thread shutting down");
}


void RawPointHistoryArchiver::submitPointData(const CtiPointDataMsg& ptData)
{
    std::lock_guard<std::mutex> lock(_archiverLock);

    auto rphRow =
        std::make_unique<CtiTableRawPointHistory>(
            ptData.getId(), 
            ptData.getQuality(), 
            ptData.getValue(), 
            ptData.getTime(), 
            ptData.getMillis(),
            ptData.getTrackingId());

    _archiverQueue.emplace_back(std::move(rphRow));
}

void RawPointHistoryArchiver::submitRows(std::vector<std::unique_ptr<CtiTableRawPointHistory>>&& rows)
{
    std::lock_guard<std::mutex> lock(_archiverLock);

    _archiverQueue.reserve(_archiverQueue.size() + rows.size());

    _archiverQueue.insert(
        _archiverQueue.end(), 
        std::make_move_iterator(rows.begin()), 
        std::make_move_iterator(rows.end()));
}

unsigned RawPointHistoryArchiver::archiverQueueSize()
{
    std::lock_guard<std::mutex> lock(_archiverLock);

    return _archiverQueue.size();
}


std::vector<std::string> RawPointHistoryArchiver::writeRawPointHistory(Cti::Database::DatabaseConnection &conn, std::vector<std::unique_ptr<CtiTableRawPointHistory>>&& rowsToWrite)
{
    using namespace Cti::Database;
    using boost::adaptors::transformed;

    if( ! conn.isValid() )
    {
        CTILOG_ERROR(dout, "Invalid Connection to Database");
        return {};
    }

    //  Get a RowSources view of the RawPointHistory rows
    const auto asRowSource = [](const std::unique_ptr<CtiTableRawPointHistory>& rph) { return rph.get(); };
    const auto getTrackingId = [](const std::unique_ptr<CtiTableRawPointHistory>& rph) { return rph->trackingId; };

    auto rowSources = boost::copy_range<std::vector<const RowSource*>>(rowsToWrite | transformed(asRowSource));

    DatabaseBulkInserter<5> rphWriter { conn.getClientType(), CtiTableRawPointHistory::getTempTableSchema(), "RPH", "RawPointHistory", "changeId" };

    try
    {
        rphWriter.writeRows(conn, std::move(rowSources));
    }
    catch( DatabaseException & ex )
    {
        CTILOG_EXCEPTION_ERROR(dout, ex, "Unable to insert rows into RawPointHistory:\n" <<
            boost::join(rowsToWrite |
                boost::adaptors::indirected |
                boost::adaptors::transformed(
                    [](const Cti::Loggable &obj) {
                        return obj.toString(); }), "\n"));

        return {};
    }

    return boost::copy_range<std::vector<std::string>>(rowsToWrite | transformed(getTrackingId));
}


}
}