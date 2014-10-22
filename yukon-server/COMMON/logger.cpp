#include "precompiled.h"

#include "guard.h"
#include "logManager.h"
#include "logger.h"

#include "log4cxx/logger.h"
#include "log4cxx/helpers/transcoder.h"
#include "log4cxx/helpers/pool.h"
#include "log4cxx/spi/loggingevent.h"

#include "boost/algorithm/string/replace.hpp"

namespace Cti {
namespace Logging {

namespace {

log4cxx::LevelPtr getLogLevel(Logger::Level levelIn)
{
    log4cxx::LevelPtr levelOut;

    switch(levelIn)
    {
    case Logger::Fatal : levelOut = log4cxx::Level::getFatal(); break;
    case Logger::Error : levelOut = log4cxx::Level::getError(); break;
    case Logger::Warn  : levelOut = log4cxx::Level::getWarn();  break;
    case Logger::Info  : levelOut = log4cxx::Level::getInfo();  break;
    case Logger::Debug : levelOut = log4cxx::Level::getDebug(); break;
    case Logger::Trace : levelOut = log4cxx::Level::getTrace(); break;
    }

    return levelOut;
}

/**
 *  Preformat the method name to avoid bad logging.
 *  The formatting will be completed by log4cxx::LocationInfo::getMethodName()
 *
 *  Example:
 *  "void __cdecl Cti::identifyProject(const struct Cti::compileinfo_t &)"
 *  will be formatted to:
 *  "::Cti::identifyProject(const struct Cti::compileinfo_t &)"
 *
 *  formatting done by log4cxx::LocationInfo::getMethodName() removes the initial double colons "::"
 *  and arguments:
 *  "Cti::identifyProject"
 */
std::string preformatMethodName(const char * func)
{
    std::string methodName(func);
    const size_t parenPos = methodName.find('(');
    const size_t spacePos = methodName.rfind(' ', parenPos);
    if( spacePos != std::string::npos )
    {
        if( methodName.compare(spacePos+1, 2, "::") == 0)
        {
            methodName.erase(0, spacePos+1);
        }
        else
        {
            methodName.replace(0, spacePos+1, "::");
        }
    }

    return methodName;
}

} // namespace anonymous

struct Logger::LoggerObj
{
    const log4cxx::LoggerPtr _logger;
    LoggerObj(log4cxx::LoggerPtr logger) : _logger(logger)
    {}
};

struct Logger::LogEvent
{
    std::string _methodName;
    log4cxx::spi::LoggingEventPtr _event;
};


const char * const getNewlineForIndent(const Indents indent)
{
    switch( indent )
    {
        case Indent_SingleTab:  return "\x0d\x0a\t"; // preformat the message: convert newlines to CRLF and add a tab at the start of the new line
        default:
        case Indent_None:       return "\x0d\x0a";   // otherwise just convert newlines to CRLF
    }
}

Logger::Logger(const std::string &loggerName, const Indents indentStyle)
    :   _logger(new LoggerObj(log4cxx::Logger::getLogger(loggerName))),
        _newline(getNewlineForIndent(indentStyle))
{}

Logger::~Logger()
{}

void Logger::formatAndForceLog(Level level, StreamBufferSink& logStream, const char* file, const char* func, int line)
{
    std::string message = logStream.extractToString();
    boost::replace_all(message, "\n", _newline);

    LOG4CXX_DECODE_CHAR(msg, message);

    std::string methodName = preformatMethodName(func);

    if( !_ready )
    {
        CtiLockGuard<CtiCriticalSection> guard(_preBufferMux);

        if( !_ready )
        {
            if( _logger->_logger->getAllAppenders().empty() )
            {
                // no appenders are available, the logManager as not been started yet.
                // save the logging event for later!
                std::auto_ptr<LogEvent> logEvent(new LogEvent());

                // move the contain of the method name
                logEvent->_methodName.swap(methodName);

                // create the logging event.
                // contains raw all info (timestamp, location, TID, level, message, etc..)
                //
                // IMPORTANT:
                // we use a temporary c_str, the logging event will be unusable if logEvent->_methodName is modified
                logEvent->_event = new log4cxx::spi::LoggingEvent(
                        _logger->_logger->getName(),
                        getLogLevel(level),
                        msg,
                        log4cxx::spi::LocationInfo(file, logEvent->_methodName.c_str(), line));

                // no appenders are available, the logManager as not been started yet.
                // save the logging event for later!
                _preBufferLogEvents.push_back(logEvent);

                return;
            }

            // the logManager as just been started!
            // log all saved event before logging our own event
            log4cxx::helpers::Pool pool;
            for each(const LogEvent& event in _preBufferLogEvents)
            {
                _logger->_logger->callAppenders(event._event, pool);
            }

            _preBufferLogEvents.clear();
            _ready = true;
        }
    }

    // create the logging event.
    // contains raw all info (timestamp, location, TID, level, message, etc..)
    //
    // IMPORTANT:
    // we use a temporary c_str, the event will be unusable once methodName is out-of-scope
    const log4cxx::spi::LoggingEventPtr event =
            new log4cxx::spi::LoggingEvent(
                _logger->_logger->getName(),
                getLogLevel(level),
                msg,
                log4cxx::spi::LocationInfo(file, methodName.c_str(), line));

    log4cxx::helpers::Pool pool;
    _logger->_logger->callAppenders(event, pool);
}

bool Logger::isLevelEnable(Level level) const
{
    return _logger->_logger->isEnabledFor(getLogLevel(level));
}

}
} // namespace Cti::Logging

Cti::Logging::LoggerPtr dout = doutManager.getLogger(); // Global log
Cti::Logging::LoggerPtr slog = slogManager.getLogger(); // Global instance. Simulator log
