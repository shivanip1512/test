#include "precompiled.h"

#include "logLayout.h"
#include "logFileAppender.h"
#include "logManager.h"
#include "ctistring.h"

#include "log4cxx/logmanager.h"
#include "log4cxx/consoleappender.h"
#include "log4cxx/helpers/loglog.h"
#include "log4cxx/helpers/transcoder.h"

namespace Cti {
namespace Logging {

namespace {

log4cxx::LogString toLogStr(const std::string &str)
{
    log4cxx::LogString logStr;
    log4cxx::helpers::Transcoder::decode(str, logStr);
    return logStr;
}

} // namespace anonymous

/// Auto shutdown of log4cxx ///

AutoShutdownLoggers::~AutoShutdownLoggers()
{
    log4cxx::LogManager::shutdown();
}

///  class FileInfo ///

FileInfo::FileInfo() :
    _path             ("..\\log"),
    _maxFileSize      (1024 * 1024 * 1024), // 1-Go
    _maxOpenRetries   (0),
    _openRetryMillis  (1000),
    _logRetentionDays (0),
    _fileAppend       (true),
    _bufferedIO       (false),
    _bufferSize       (1024 * 8)
{}

std::string FileInfo::logFileName(const CtiDate &date) const
{
    std::ostringstream oss;

    oss << _path <<"\\"<< _baseFileName<<"_";

    oss << date.year() << std::setfill('0')
        << std::setw(2) << date.month()
        << std::setw(2) << date.dayOfMonth();

    oss <<".log";

    return oss.str();
}

bool FileInfo::shouldDeleteFile(const std::string& fileToDelete, const CtiDate& cutOffDate) const
{
    const std::string baseFileName    = boost::algorithm::to_lower_copy(_baseFileName);
    const std::string date_regex_spec = "2\\d{7}";
    const std::string file_regex_spec = "\\\\" + baseFileName + "_?" + date_regex_spec + "\\.log$";

    CtiString input(fileToDelete);
    input.toLower();

    // Check if the filename portion is in the proper format
    // '\filenameYYYYMMDD.log' or
    // '\filename_YYYYMMDD.log'
    CtiString output = input.match( file_regex_spec );

    if( output.empty() )
    {
        return false;
    }

    // Check the date format
    // Previous 'file_regex_spec' match ensures a match here too.
    CtiString date = output.match(date_regex_spec);

    unsigned long date_num = std::strtoul(date.c_str(), NULL, 10);

    unsigned day = date_num % 100;
    date_num /= 100;
    unsigned month = date_num % 100;
    unsigned year = date_num / 100;

    CtiDate fileDate(day, month, year); // Any invalid params set us to Jan 01, 1970

    if( fileDate < CtiDate(1, 1, 2000) || fileDate > CtiDate(31, 12, 2035) )
    {
        return false;
    }

    return fileDate < cutOffDate;
}

/// class LogManager ///

LogManager::LogManager(const std::string &baseLoggerName)
    :   _baseLoggerName(baseLoggerName),
        _format(LogFormat_General),
        _started(false)
{
}

void LogManager::setOutputPath(const std::string& path)
{
    _fileInfo._path = path;
}

void LogManager::setOutputFile(const std::string& baseFileName)
{
    _fileInfo._baseFileName.swap(scrub(baseFileName));
}

std::string LogManager::scrub(std::string fileName)
{
    //  this only gets called once per file per day, so it's not too expensive
    for(int i = 0; i < fileName.length(); i++)
    {
        //  if the character is not a-z, A-Z, 0-9, '-', or '_', scrub it to an underscore
        if( !(isalnum(fileName[i]) || fileName[i] == '-' || fileName[i] == '_') )
        {
            fileName[i] = '_';
        }
    }

    return fileName;
}

void LogManager::setOwnerInfo(const compileinfo_t &ownerinfo)
{
    _ownerInfo._project = ownerinfo.project;
    _ownerInfo._version = ownerinfo.version;
    _ownerInfo._details = ownerinfo.details;
    _ownerInfo._date    = ownerinfo.date;
}

void LogManager::setToStdOut(const bool toStdout)
{
    _toStdout = toStdout;
}

void LogManager::setRetentionDays(const unsigned long days)
{
    _fileInfo._logRetentionDays = days;
}

void LogManager::setOutputFormat(const LogFormats format)
{
    _format = format;
}

const FileInfo& LogManager::getFileInfo() const
{
    return _fileInfo;
}

void LogManager::start()
{
    const log4cxx::LoggerPtr baseLogger = log4cxx::Logger::getLogger(_baseLoggerName);

    log4cxx::LayoutPtr logLayout;
    switch( _format )
    {
        case LogFormat_CommLog:
            logLayout = new CommsLogLayout(_ownerInfo);
            break;
        default:
        case LogFormat_General:
            logLayout = new GeneralLogLayout(_ownerInfo);
            break;
    }

    const log4cxx::AppenderPtr logFileAppender(new LogFileAppender(logLayout, _fileInfo));
    baseLogger->addAppender(logFileAppender);

    if( _toStdout )
    {
        const log4cxx::AppenderPtr consoleAppender(new log4cxx::ConsoleAppender(logLayout));
        baseLogger->addAppender(consoleAppender);
    }

    _started = true;
}

bool LogManager::isStarted() const
{
    return _started;
}

Indents getIndentFor(const LogFormats format)
{
    switch( format )
    {
        default:
        LogFormat_General:  return Indent_SingleTab;
        LogFormat_CommLog:  return Indent_None;
    }
}

LoggerPtr LogManager::getLogger() const
{
    return LoggerPtr(new Logger(_baseLoggerName, getIndentFor(_format)));
}

LoggerPtr LogManager::getLogger(const std::string &loggerName) const
{
    return LoggerPtr(new Logger(_baseLoggerName + "." + loggerName, getIndentFor(_format)));
}

}
} // namespace Cti::Logging

IM_EX_CTIBASE Cti::Logging::LogManager doutManager("dout");
IM_EX_CTIBASE Cti::Logging::LogManager slogManager("slog");
