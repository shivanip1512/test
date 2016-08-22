#include "precompiled.h"

#include "rph_archiver.h"

#include "msg_pdata.h"

#include "millisecond_timer.h"
#include "logger.h"
#include "ThreadStatusKeeper.h"

#include "tbl_rawpthistory.h"

#include "database_connection.h"
#include "database_writer.h"
#include "database_util.h"
#include "database_exceptions.h"
#include "database_transaction.h"

#include "coroutine_util.h"

#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/adaptor/indirected.hpp>

namespace Cti {
namespace Dispatch {

RawPointHistoryArchiver::RawPointHistoryArchiver(const bool &shutdownOnThreadTimeout, void (*shutdownFunc)(const std::string& who))
    :   _archiverThread { [this]{ mainThread(); } },
        ShutdownOnThreadTimeout(shutdownOnThreadTimeout),
        ShutdownFunc(shutdownFunc)
{}

RawPointHistoryArchiver::~RawPointHistoryArchiver() = default;

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

    std::vector<std::unique_ptr<CtiTableRawPointHistory>> rowsToWrite;

    {
        std::lock_guard<std::mutex> lock(_archiverLock);

        if( wm != WriteMode_WriteAll && _archiverQueue.size() > ChunkSize )
        {
            rowsToWrite.reserve(ChunkSize);

            const auto begin = _archiverQueue.begin();
            const auto mid = begin + ChunkSize;

            rowsToWrite.assign(
                std::make_move_iterator(begin),
                std::make_move_iterator(mid));

            _archiverQueue.erase(begin, mid);
        }
        else
        {
            rowsToWrite.swap(_archiverQueue);
        }
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

    static const size_t ChunkSize = 180;  //  each row takes 5 placeholders, so this is 900 - which is close to our (current) 999 limit, but still a round number

    unsigned rowsWritten = 0;

    boost::optional<DatabaseTransaction> transaction;

    try
    {
        DatabaseWriter truncator{ conn, CtiTableRawPointHistory::getTempTableTruncationSql(conn.getClientType()) };

        if( ! truncator.execute() )
        {
            CTILOG_INFO(dout, "Temp table not detected, attempting to create");

            DatabaseWriter creator{ conn, CtiTableRawPointHistory::getTempTableCreationSql(conn.getClientType()) };

            executeWriter(creator, __FILE__, __LINE__, Cti::Database::LogDebug::Disable);
        }

        transaction.emplace(conn);

        for( auto chunk : Cti::Coroutines::chunked(rowsToWrite, ChunkSize) )
        {
            DatabaseWriter inserter{ conn, CtiTableRawPointHistory::getInsertSql(conn.getClientType(), chunk.size()) };

            for( auto& record : chunk ) 
            {
                record->fillInserter(inserter);
            }

            executeWriter(inserter, __FILE__, __LINE__, Cti::Database::LogDebug::Disable);

            rowsWritten += chunk.size();
        }

        DatabaseWriter finalizer{ conn, CtiTableRawPointHistory::getFinalizeSql(conn.getClientType()) };

        finalizer.executeWithDatabaseException();
    }
    catch( DatabaseException & ex )
    {
        if( transaction )
        {
            transaction->rollback();
        }

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