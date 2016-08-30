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

#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/adaptor/indirected.hpp>

#include <unordered_map>

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

    if( wm == WriteMode_WriteChunkIfOverThreshold && rowsWaiting < MinRowsToWrite )
    {
        return false;
    }

    auto rowsToWrite = getFilteredRows(wm == WriteMode_WriteAll ? rowsWaiting : ChunkSize);

    if( rowsToWrite.empty() )
    {
        return false;
    }

    try
    {
        Cti::Timing::MillisecondTimer timer;

        if( unsigned rowsWritten = writeRawPointHistory(conn, std::move(rowsToWrite)) )
        {
            const unsigned rowsRemaining = archiverQueueSize();

            CTILOG_INFO(dout, "RawPointHistory transaction completed in " << timer.elapsed() << "ms. Inserted "<< rowsWritten <<" rows. remaining: "<< rowsRemaining <<" rows");

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

    const auto now = std::time(nullptr);

    while( maximum-- && itr.base() != _archiverQueue.end() )
    {
        if( ! wasPreviouslyArchived(**itr, now) )
        {
            rows.push_back(*itr);
        }
        else
        {
            duplicates.add(std::to_string((*itr)->pointId)) << (*itr)->time << " - " << (*itr)->value; 
        }
        ++itr;
    }

    _archiverQueue.erase(_archiverQueue.begin(), itr.base());

    if( ! duplicates.empty() )
    {
        CTILOG_ERROR(dout, "Detected duplicates:" << duplicates);
    }

    return rows;
}


bool RawPointHistoryArchiver::wasPreviouslyArchived(const CtiTableRawPointHistory& row, const time_t now)
{
    enum {
        MinutesPerYear = 365 * 24 * 60,
        IntervalBits = 37,
        MaxInterval = 60
    };

    static const auto makeArchiveEpoch = [](const time_t now){ return now / 60 - MinutesPerYear; };  //  1 year before startup

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
        return false;
    }

    // Sanity check on this data - it might be far-flung-future
    if( utcSeconds >= (now + 86400) )
    {
        Cti::FormattedList l;
        l.add("Epoch") << CtiTime(ArchiveEpoch * 60);
        l.add("Incoming timestamp") << row.time;
        l.add("Incoming pointid") << row.pointId;

        CTILOG_WARN(dout, "Recieved pointdata more than 1 day in the future" << l);

        return false;
    }

    const auto utcMinutes = utcSeconds / 60;

    if( utcMinutes < ArchiveEpoch )
    {
        Cti::FormattedList l;
        l.add("Epoch") << CtiTime(ArchiveEpoch * 60);
        l.add("Incoming timestamp") << row.time;
        l.add("Incoming pointid") << row.pointId;

        CTILOG_WARN(dout, "Recieved pointdata older than epoch" << l);

        return false;
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

        CTILOG_WARN(dout, "Epoch exceeded, resetting archive cache" << l);

        ArchiveEpoch = newArchiveEpoch;

        epochMinutes = utcMinutes - ArchiveEpoch;

        value_cache.clear();
    }

    auto record = Cti::mapFindRef(value_cache, row.pointId);

    if( ! record )
    {
        value_cache.emplace(row.pointId, last_seen{ epochMinutes, 0, 1 });  //  just saw it, mark the first occurrence

        return false;
    }

    //  timestamp was equal, we've seen it
    if( epochMinutes == record->latest_timestamp )
    {
        return true;
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

        return false;
    }

    //  If the records aren't a multiple of the interval apart, recalculate the interval
    if( minutesApart % record->interval_minutes )
    {
        //  find the new interval
        const auto new_interval_minutes = Cti::find_gcd(minutesApart % MaxInterval, record->interval_minutes);
        //  adjust the existing intervals to the new interval
        const auto new_intervals = adjust_intervals(record->intervals, record->interval_minutes / new_interval_minutes);

        Cti::FormattedList l;
        l.add("Incoming timestamp") << row.time;
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
            : Cti::Logging::Logger::Info;

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

        return false;
    }

    const auto intervals = (record->latest_timestamp - epochMinutes) / record->interval_minutes;

    //  we only go back 37 intervals
    if( intervals > IntervalBits )
    {
        Cti::FormattedList l;
        l.add("Incoming timestamp") << row.time;
        l.add("Interval") << record->interval_minutes << " minutes";
        l.add("Latest timestamp") << CtiTime((record->latest_timestamp + ArchiveEpoch) * 60);
        l.add("Cache") << std::hex << std::setw((IntervalBits + 3) / 4) << record->intervals;
        l.add("Interval bits") << IntervalBits;

        CTILOG_DEBUG(dout, "Incoming value older than cache depth" << l)

        return false;
    }

    const auto mask = 0x1ull << intervals;  //  declare the 1 as unsigned long long so it can shift more than 32

    const bool seen = record->intervals & mask;

    record->intervals |= mask;

    return seen;
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

    try
    {
        Cti::Database::DatabaseConnection conn;

        const unsigned ThirtySeconds = 30 * 1000;
        unsigned loopTimer = 0;

        Cti::Timing::MillisecondTimer timer;

        try
        {
            while( true )
            {
                WriteMode wm = WriteMode_WriteChunkIfOverThreshold;

                if( loopTimer > ThirtySeconds )
                {
                    loopTimer %= ThirtySeconds;

                    //  guaranteed write once every 30 seconds
                    wm = WriteMode_WriteChunk;

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
        }
        catch( const WorkerThread::Interrupted& ex )
        {
            CTILOG_INFO(dout, "Interrupted, shutting down");

            //  Write anything remaining before we exit.
            writeArchiveDataToDB(conn, WriteMode_WriteAll);
        }
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

    _archiverQueue.emplace_back(
        std::make_unique<CtiTableRawPointHistory>(
            ptData.getId(), 
            ptData.getQuality(), 
            ptData.getValue(), 
            ptData.getTime(), 
            ptData.getMillis()));
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


unsigned RawPointHistoryArchiver::writeRawPointHistory(Cti::Database::DatabaseConnection &conn, std::vector<std::unique_ptr<CtiTableRawPointHistory>>&& rowsToWrite)
{
    using namespace Cti::Database;

    if( ! conn.isValid() )
    {
        CTILOG_ERROR(dout, "Invalid Connection to Database");
        return 0;
    }

    //  Get a RowSources view of the RawPointHistory rows
    const auto asRowSource = [](const std::unique_ptr<CtiTableRawPointHistory>& rph) { return rph.get(); };

    auto rowSources = boost::copy_range<std::vector<const RowSource*>>(rowsToWrite | boost::adaptors::transformed(asRowSource));

    DatabaseBulkInserter<5> rphWriter { CtiTableRawPointHistory::getTempTableSchema(), "RPH", "RawPointHistory", "changeId" };

    unsigned rowsWritten = rowSources.size();

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

        rowsWritten = 0;
    }

    return rowsWritten;
}


}
}