#include "precompiled.h"

#include "logLayout.h"
#include "logFileAppender.h"
#include "truncatingConsoleAppender.h"
#include "logManager.h"
#include "cparms.h"

#include "log4cxx/logmanager.h"
#include "log4cxx/asyncappender.h"
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
    LogManager::inShutdown = true;

    try
    {
        log4cxx::LogManager::shutdown();
    }
    catch( ... )
    {
        // This page intentionally left blank.
    }
}

///  class FileInfo ///

FileInfo::FileInfo() :
    path             ("..\\log"),
    maxFileSize      (1024 * 1024 * 1024), // 1 GB
    maxOpenRetries   (0),
    openRetryMillis  (1000),
    logRetentionDays (0),
    fileAppend       (true),
    bufferedIO       (true),
    bufferSize       (1024 * 128)
{}

std::string FileInfo::logFileName(const CtiDate &date) const
{
    std::ostringstream oss;

    oss << path <<"\\"<< baseFileName<<"_";

    oss << date.year() << std::setfill('0')
        << std::setw(2) << date.month()
        << std::setw(2) << date.dayOfMonth();

    oss <<".log";

    return oss.str();
}

bool FileInfo::shouldDeleteFile(const std::string& fileToDelete, const CtiDate& cutOffDate) const
{
    const std::string lowercaseFileName = boost::algorithm::to_lower_copy(baseFileName);
    const std::string date_regex_spec = "2\\d{7}";
    const std::string file_regex_spec = "\\\\" + lowercaseFileName + "_?" + date_regex_spec + "\\.log$";

    std::string input(fileToDelete);
    CtiToLower(input);

    // Check if the filename portion is in the proper format
    // '\filenameYYYYMMDD.log' or
    // '\filename_YYYYMMDD.log'
    std::string output = matchRegex(input, file_regex_spec );

    if( output.empty() )
    {
        return false;
    }

    // Check the date format
    // Previous 'file_regex_spec' match ensures a match here too.
    std::string date = matchRegex(output, date_regex_spec);

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

bool LogManager::inShutdown = false;

LogManager::LogManager(const std::string &baseLoggerName)
    :   _baseLoggerName(baseLoggerName),
        _format(LogFormat_General),
        _started(false)
{
}

void LogManager::setOutputPath(const std::string& path)
{
    _fileInfo.path = path;
}

void LogManager::setOutputFile(const std::string& baseFileName)
{
    _fileInfo.baseFileName.swap(scrub(baseFileName));
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
    _fileInfo.logRetentionDays = days;
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

    log4cxx::helpers::ObjectPtrT<log4cxx::AsyncAppender> asyncAppender(new log4cxx::AsyncAppender);
    asyncAppender->setBufferSize(gConfigParms.getValueAsULong("LOG_BUFFER_SIZE", 1024 * 1024));  //  default is 128, which was not sufficient for heavy logging

    // This provides an overall logging level.  
    LogManager::refresh();

    if(_toStdout)
    {
        std::auto_ptr<TruncatingConsoleAppender> consoleAppender(new TruncatingConsoleAppender(logLayout));
        consoleAppender->setInterval(Timing::Chrono::seconds(gConfigParms.getValueAsULong("LOG_CONSOLE_BURST_INTERVAL_SECONDS", 1)));
        consoleAppender->setMaxBurstSize(gConfigParms.getValueAsULong("LOG_CONSOLE_BURST_SIZE", 120));

        asyncAppender->addAppender(log4cxx::AppenderPtr(consoleAppender.release()));
    }

    const log4cxx::AppenderPtr logFileAppender(new LogFileAppender(logLayout, _fileInfo));
    asyncAppender->addAppender(logFileAppender);

    baseLogger->addAppender(asyncAppender);

    _started = true;
}

void LogManager::refresh()
{
    // This provides an overall logging level.  
    std::string logLevelString = gConfigParms.getValueAsString("LOG_LEVEL", "DEBUG");
    log4cxx::LevelPtr level = log4cxx::Level::toLevel(logLevelString);

    // Set overall logging level in root logger, which propigates to all children 
    // unless overriden in the child.
    log4cxx::LoggerPtr rootLogger = log4cxx::LogManager::getRootLogger();
    rootLogger->setLevel(level);
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
        case LogFormat_General:  return Indent_SingleTab;
        case LogFormat_CommLog:  return Indent_None;
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

Cti::Logging::LoggerPtr dout = doutManager.getLogger(); // Global log
Cti::Logging::LoggerPtr slog = slogManager.getLogger(); // Global instance. Simulator log
