#include "precompiled.h"

#include "utility.h"
#include "logFileAppender.h"

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

namespace Cti {
namespace Logging {

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
        log4cxx::helpers::LogLog::error(toLogStr(error));
    }
}

} // namespace anonymous

/**
 * Wrapper for OutputStream that will report all write
 * operations back to the LogFileAppender class for file length calculations.
 */
class LogFileAppender::CountingOutputStream : public log4cxx::helpers::OutputStream
{
    log4cxx::helpers::OutputStreamPtr os;
    LogFileAppender* appender;

public:
    CountingOutputStream(log4cxx::helpers::OutputStreamPtr& os_, LogFileAppender* appender_) :
        os(os_), appender(appender_)
    {}

    void close(log4cxx::helpers::Pool& p) override
    {
        os->close(p);
        appender = NULL;
    }

    void flush(log4cxx::helpers::Pool& p) override
    {
        os->flush(p);
    }

    void write(log4cxx::helpers::ByteBuffer& buf, log4cxx::helpers::Pool& p) override
    {
        os->write(buf, p);
        if( appender )
        {
            appender->_fileSize += buf.limit();
        }
    }
};

/// class LogFileAppender ///

LogFileAppender::LogFileAppender(const log4cxx::LayoutPtr& layout, const FileInfo& fileInfo)
    :   log4cxx::WriterAppender(layout),
        _fileInfo(fileInfo),
        _maxFileSizeReached(false),
        _lastRolloverFailed(false)
{
    log4cxx::helpers::Pool p;
    activateOptions(p);
    cleanupOldFiles();
}

void LogFileAppender::activateOptions(log4cxx::helpers::Pool& p)
{
    log4cxx::helpers::synchronized sync(mutex);

    const std::string fileName = _fileInfo.logFileName(_logDate);

    try
    {
        setFile(toLogStr(fileName), _fileInfo._fileAppend, _fileInfo._bufferedIO, _fileInfo._bufferSize, p);

        WriterAppender::activateOptions(p);
    }
    catch( const log4cxx::helpers::IOException& e )
    {
        std::ostringstream oss;
        oss <<"setFile("<< fileName <<","<< (_fileInfo._fileAppend ? "true":"false") << ") call failed.";
        log4cxx::helpers::LogLog::error(toLogStr(oss.str()), e);
    }
}

void LogFileAppender::setFile(
        const log4cxx::LogString& filename,
        bool append,
        bool bufferedIO,
        size_t bufferSize,
        log4cxx::helpers::Pool& p)
{
    log4cxx::helpers::synchronized sync(mutex);

    // It does not make sense to have immediate flush and bufferedIO.
    if( bufferedIO )
    {
        setImmediateFlush(false);
    }

    closeWriter();

    bool writeBOM = false;
    if( log4cxx::helpers::StringHelper::equalsIgnoreCase(getEncoding(), LOG4CXX_STR("utf-16"), LOG4CXX_STR("UTF-16")) )
    {
        // don't want to write a byte order mark if the file exists
        if( append )
        {
            log4cxx::File outFile;
            outFile.setPath(filename);
            writeBOM = ! outFile.exists(p);
        }
        else
        {
            writeBOM = true;
        }
    }

    log4cxx::helpers::OutputStreamPtr outStream;

    try
    {
        outStream = new log4cxx::helpers::FileOutputStream(filename, append);
    }
    catch( const log4cxx::helpers::IOException& e )
    {
        log4cxx::LogString parentName = log4cxx::File().setPath(filename).getParent(p);
        if( ! parentName.empty() )
        {
            log4cxx::File parentDir;
            parentDir.setPath(parentName);
            if( ! parentDir.exists(p) && parentDir.mkdirs(p) )
            {
                outStream = new log4cxx::helpers::FileOutputStream(filename, append);
            }
            else
            {
                throw e;
            }
        }
        else
        {
            throw e;
        }
    }

    // if a new file and UTF-16, then write a BOM
    if( writeBOM )
    {
        char bom[] = { (char) 0xFE, (char) 0xFF };
        log4cxx::helpers::ByteBuffer buf(bom, 2);
        outStream->write(buf, p);
    }

    outStream = new CountingOutputStream(outStream, this);
    log4cxx::helpers::WriterPtr newWriter = createWriter(outStream);

    if( bufferedIO )
    {
        newWriter = new log4cxx::helpers::BufferedWriter(newWriter, bufferSize);
    }

    setWriter(_writer = newWriter);

    _fileSize = log4cxx::File().setPath(filename).length(p);

    writeHeader(p);
}


void LogFileAppender::subAppend(
        const log4cxx::spi::LoggingEventPtr& event,
        log4cxx::helpers::Pool& p)
{
    log4cxx::LogString msg;
    layout->format(msg, event, p);

    const unsigned long seconds = event->getTimeStamp() / (1000 * 1000);
    const CtiDate today = CtiTime(seconds).date();

    {
        log4cxx::helpers::synchronized sync(mutex);

        if( ! rollover(today, p) )
        {
            return; // rollover was attempted and failed
        }

        if( _fileInfo._maxFileSize <= _fileSize )
        {
            if( !_maxFileSizeReached )
            {
                std::ostringstream oss;
                oss << "Maximum file size reached: "<< _fileInfo._maxFileSize <<" bytes";
                log4cxx::helpers::LogLog::error(toLogStr(oss.str()));
                _maxFileSizeReached = true;
            }
            return;
        }

        if( _writer != NULL )
        {
            _writer->write(msg, p);
            if( getImmediateFlush() )
            {
                _writer->flush(p);
            }
        }
    }
}

bool LogFileAppender::rollover(const CtiDate& today, log4cxx::helpers::Pool &p)
{
    log4cxx::helpers::synchronized sync(mutex);

    if( today <= _logDate )
    {
        return true;
    }

    const std::string fileName = _fileInfo.logFileName(today);

    for(int openRetry=0 ; ; )
    {
        try
        {
            setFile(toLogStr(fileName), _fileInfo._fileAppend, _fileInfo._bufferedIO, _fileInfo._bufferSize, p);
            break;
        }
        catch( const log4cxx::helpers::IOException& e )
        {
            if( _lastRolloverFailed )
            {
                return false;
            }

            std::ostringstream oss;
            oss <<"Error opening log file "<< fileName;

            if( ++openRetry > _fileInfo._maxOpenRetries )
            {
                log4cxx::helpers::LogLog::error(toLogStr(oss.str()), e);
                _lastRolloverFailed = true;
                return false;
            }

            oss <<", will retry..";
            log4cxx::helpers::LogLog::error(toLogStr(oss.str()), e);
        }

        Sleep(_fileInfo._openRetryMillis);
    }

    _logDate            = today;
    _lastRolloverFailed = false;
    _maxFileSizeReached = false;

    cleanupOldFiles();

    return true;
}

void LogFileAppender::cleanupOldFiles() const
{
    log4cxx::helpers::synchronized sync(mutex);

    if( !_fileInfo._logRetentionDays )
    {
        return;
    }

    // We expect:
    // '\filenameYYYYMMDD.log' or
    // '\filename_YYYYMMDD.log'
    // Files found are fully verified inside shouldDeleteFile()

    const std::string fileName = _fileInfo._path + "\\" + _fileInfo._baseFileName + "*.log";

    WIN32_FIND_DATA fileInfoFound;
    const HANDLE finderHandle = FindFirstFile(fileName.c_str(), &fileInfoFound);

    if( finderHandle != INVALID_HANDLE_VALUE )
    {
        const CtiDate cutoffDate  = _logDate - _fileInfo._logRetentionDays;

        do
        {
            const std::string fileNameFound = _fileInfo._path + "\\" + fileInfoFound.cFileName;
            if( _fileInfo.shouldDeleteFile(fileNameFound, cutoffDate) )
            {
                deleteOldFile(fileNameFound);
            }

        } while( FindNextFile(finderHandle, &fileInfoFound) );

        FindClose(finderHandle);
    }
}

}
} // namespace Cti::Logging
