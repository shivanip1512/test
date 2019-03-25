#include "precompiled.h"

#include "logFileAppender.h"
#include "CParms.h"

#include "log4cxx/file.h"
#include "log4cxx/helpers/synchronized.h"
#include "log4cxx/helpers/loglog.h"
#include "log4cxx/helpers/transcoder.h"
#include "log4cxx/helpers/outputstream.h"
#include "log4cxx/helpers/bytebuffer.h"
#include "log4cxx/helpers/pool.h"
#include "log4cxx/helpers/stringhelper.h"
#include "log4cxx/helpers/fileoutputstream.h"
#include "log4cxx/helpers/bufferedwriter.h"
#include "log4cxx/rolling/timebasedrollingpolicy.h"

#include <apr_time.h>

#include <filesystem>

namespace fs = std::filesystem;

namespace Cti::Logging {

using log4cxx::helpers::LogLog;
using namespace std::string_literals;

namespace {

log4cxx::LogString toLogStr(const std::string &str)
{
    log4cxx::LogString logStr;
    log4cxx::helpers::Transcoder::decode(str, logStr);
    return logStr;
}

void deleteOldFile(const std::string &fileToDelete)
{
    if( ! ::DeleteFile(fileToDelete.c_str()) )
    {
        const std::string error = "Error deleting old log file: "+ fileToDelete;
        LogLog::error(toLogStr(error));
    }
}

} // namespace anonymous

LogFileAppender::LogFileAppender(const log4cxx::LayoutPtr& layout, const FileInfo& fileInfo)
    :   _fileInfo(fileInfo),
        _maxFileSizeLogged(false),
        _nextResumeAttempt(0),
        _nextCleanupAttempt(0),
        _nextFlush(0),
        _flushInterval(5 * 1000 * 1000)  //  force a flush every 5 seconds
{
    setLayout(layout);

    log4cxx::rolling::TimeBasedRollingPolicyPtr policy { new log4cxx::rolling::TimeBasedRollingPolicy() };

    const auto fileNamePattern = fileInfo.path + "\\" + fileInfo.baseFileName + "_%d{yyyyMMdd}.log.zip";

    policy->setFileNamePattern(toLogStr(fileNamePattern));
    policy->setZipPath(toLogStr(gConfigParms.getYukonBase() + "\\server\\bin\\zip.cmd"));

    setRollingPolicy(policy);

    log4cxx::helpers::Pool p;
    activateOptions(p);
}

std::atomic<uintmax_t> LogFileAppender::_maxFileSize = 1024 * 1024 * 1024;  //  1 GB default
std::atomic<uint16_t>  LogFileAppender::_logRetentionDays = 90;  //  90 days default

void LogFileAppender::setMaxFileSize(uintmax_t maxFileSize)
{
    _maxFileSize = maxFileSize;
}

void LogFileAppender::setLogRetentionDays(uint16_t days)
{
    _logRetentionDays = days;
}

void LogFileAppender::tryCleanupOldFiles(const long long timestamp)
{
    if( timestamp < _nextCleanupAttempt )
    {
        return;
    }

    //  If the setting changed, run it again
    if( _retentionDays != _logRetentionDays )
    {
        cleanupOldFiles();
    }

    _nextCleanupAttempt = apr_time_now() + 5 * 1000 * 1000;  //  only check every 5 seconds
}

bool LogFileAppender::tryResumeWriting(const long long timestamp, log4cxx::helpers::Pool &p)
{
    if( timestamp < _nextResumeAttempt )
    {
        return false;
    }

    _nextResumeAttempt = apr_time_now() + 5 * 1000 * 1000;  //  try every 5 seconds

    fs::path filepath { getFile() };
    const auto fileSize = fs::file_size(filepath);
    const auto maxSize = _maxFileSize.load();

    if( ! fs::exists(filepath) || fileSize < maxSize )
    {
        try
        {
            activateOptions(p);

            const log4cxx::spi::LoggingEventPtr maxSizeEvent =
                new log4cxx::spi::LoggingEvent(
                    getName(),
                    log4cxx::Level::getError(),
                    toLogStr("Logging resuming - current file size " + std::to_string(fileSize) + ", max file size " + std::to_string(maxSize)),
                    log4cxx::spi::LocationInfo(__FILE__, __FUNCTION__, __LINE__));

            FileAppender::subAppend(maxSizeEvent, p);

            return true;
        }
        catch( const log4cxx::helpers::IOException& e )
        {
            LogLog::error(LOG4CXX_STR("Could not resume writing"), e);
        }
    }

    return false;
}

extern const log4cxx::spi::LoggingEventPtr PokeEvent;

log4cxx::helpers::WriterPtr LogFileAppender::createWriter(log4cxx::helpers::OutputStreamPtr& os)
{
    cleanupOldFiles();

    //  Grab a copy of the writer for us to flush on demand
    return _writer = RollingFileAppender::createWriter(os);
}

void LogFileAppender::subAppend(
        const log4cxx::spi::LoggingEventPtr& event,
        log4cxx::helpers::Pool& p)
{
// ---- copied from RollingFileAppender ----
    // The rollover check must precede actual writing. This is the
    // only correct behavior for time driven triggers.
    if( triggeringPolicy->isTriggeringEvent(this, event, getFile(), getFileLength()) ) 
    {
        //
        //   wrap rollover request in try block since
        //    rollover may fail in case read access to directory
        //    is not provided.  However appender should still be in good
        //     condition and the append should still happen.
        try {
            rollover(p);
        }
        catch( std::exception& ex ) {
            LogLog::warn(toLogStr("Exception during rollover attempt: "s + ex.what()));
        }
    }
// ---- copied from RollingFileAppender ----

    const long long timestamp = event != PokeEvent ? event->getTimeStamp() : apr_time_now();

    {
        log4cxx::helpers::synchronized sync(mutex);

        tryCleanupOldFiles(timestamp);

        if( _maxFileSizeLogged )
        {
            if( ! tryResumeWriting(timestamp, p) )
            {
                return;
            }

            _maxFileSizeLogged = false;
        }

        if( const auto maxFileSize = _maxFileSize.load(); 
            fs::file_size(getFile()) >= maxFileSize )
        {
            auto maxSizeReached = "Maximum file size reached: " + std::to_string(maxFileSize) + " bytes";

            LogLog::error(toLogStr(maxSizeReached));

            if( _writer != NULL )
            {
                const log4cxx::spi::LoggingEventPtr maxSizeEvent =
                    new log4cxx::spi::LoggingEvent(
                        getName(),
                        log4cxx::Level::getError(),
                        toLogStr(maxSizeReached),
                        log4cxx::spi::LocationInfo(__FILE__, __FUNCTION__, __LINE__));

                FileAppender::subAppend(maxSizeEvent, p);
            }

            _maxFileSizeLogged = true;

            return;
        }
        
        if( _writer != NULL )
        {
            if( event != PokeEvent )
            {
                FileAppender::subAppend(event, p);
            }
            if( getImmediateFlush() || timestamp > _nextFlush )
            {
                _writer->flush(p);
                _nextFlush = timestamp + _flushInterval;
            }
        }
    }
}

void LogFileAppender::cleanupOldFiles()
{
    log4cxx::helpers::synchronized sync(mutex);

    _retentionDays = _logRetentionDays;

    if( ! _retentionDays )
    {
        return;
    }

    // We expect:
    // '\filenameYYYYMMDD.log' or
    // '\filename_YYYYMMDD.log' or
    // '\filename_YYYYMMDD.zip'
    // Files found are fully verified inside shouldDeleteFile()

    for ( auto extension : { "*.log", "*.zip" } )
    {
        const std::string fileName = _fileInfo.path + "\\" + _fileInfo.baseFileName + extension;

        WIN32_FIND_DATA fileInfoFound;
        const HANDLE finderHandle = FindFirstFile(fileName.c_str(), &fileInfoFound);

        if (finderHandle != INVALID_HANDLE_VALUE)
        {
            const CtiDate cutoffDate = CtiDate::now() - _retentionDays;

            do
            {
                const std::string fileNameFound = _fileInfo.path + "\\" + fileInfoFound.cFileName;
                if (_fileInfo.shouldDeleteFile(fileNameFound, cutoffDate))
                {
                    deleteOldFile(fileNameFound);
                }

            } while (FindNextFile(finderHandle, &fileInfoFound));

            FindClose(finderHandle);
        }
    }
}

}
