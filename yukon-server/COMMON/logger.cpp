#include "precompiled.h"

#include "guard.h"
#include "logManager.h"
#include "logger.h"
#include "ctitime.h"

#include "log4cxx/logger.h"
#include "log4cxx/helpers/transcoder.h"
#include "log4cxx/helpers/pool.h"
#include "log4cxx/spi/loggingevent.h"
#include <log4cxx/mdc.h>

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
    log4cxx::spi::LoggingEventPtr event;
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

static const size_t MaxMessageSize = 1024 * 1024;  //  1 MB, which should be enough for all sane log entries

void Logger::formatAndForceLog(Level level, StreamBufferSink& logStream, const char* file, const char* func, int line)
{
    std::string message = logStream.extractToString();
    const size_t messageSize = message.size();
    if( messageSize > MaxMessageSize )
    {
        message.resize(MaxMessageSize);

        message += StreamBuffer() << "\n[Message truncated to " << MaxMessageSize << ", originally " << messageSize << "]";
    }
    boost::replace_all(message, "\n", _newline);

    LOG4CXX_DECODE_CHAR(msg, message);

    const char *methodName = 0;

    {
        CtiLockGuard<CtiCriticalSection> guard(_formattedMethodNameMux);

        MethodLookup::iterator itr = _formattedMethodNames.find(func);

        if( itr == _formattedMethodNames.end() )
        {
            itr = _formattedMethodNames.insert(make_pair(func, preformatMethodName(func))).first;
        }

        methodName = itr->second.c_str();
    }

    log4cxx::MDC::put("tz", CtiTime::now().getTZ());

    const log4cxx::spi::LoggingEventPtr event =
            new log4cxx::spi::LoggingEvent(
                    _logger->_logger->getName(),
                    getLogLevel(level),
                    msg,
                    log4cxx::spi::LocationInfo(file, methodName, line));
    
    event->getMDCCopy();

    if( ! _ready )
    {
        CtiLockGuard<CtiCriticalSection> guard(_preBufferMux);

        if( ! _ready )
        {
            if( _logger->_logger->getAllAppenders().empty() )
            {
                // no appenders are available, the logManager has not been started yet.
                // save the logging event for later!
                std::auto_ptr<LogEvent> logEvent(new LogEvent());

                logEvent->event = event;

                _preBufferedLogEvents.push_back(logEvent);

                return;
            }

            // the logManager has just been started!
            // log all saved events before logging the new event
            log4cxx::helpers::Pool pool;
            for each(const LogEvent& event in _preBufferedLogEvents)
            {
                _logger->_logger->callAppenders(event.event, pool);
            }

            _preBufferedLogEvents.clear();
            _ready = true;
        }
    }

    _logger->_logger->callAppenders(event, log4cxx::helpers::Pool{});
}

bool Logger::isLevelEnable(Level level) const
{
    return _logger->_logger->isEnabledFor(getLogLevel(level));
}

const log4cxx::LogString NoLogger, PokeEventName = LOG4CXX_STR("Poke Event");

extern const log4cxx::spi::LoggingEventPtr PokeEvent =
        new log4cxx::spi::LoggingEvent(
                NoLogger,
                log4cxx::Level::getFatal(),
                PokeEventName,
                log4cxx::spi::LocationInfo{});

void Logger::poke()
{
    if( _ready )
    {
        _logger->_logger->callAppenders(
                PokeEvent,  //  poke event is handled as a refresh by our custom appenders (LogFileAppender, TruncatingConsoleAppender)
                log4cxx::helpers::Pool{});
    }
}

}
} // namespace Cti::Logging

