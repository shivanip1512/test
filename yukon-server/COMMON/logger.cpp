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

#include <boost/algorithm/string/replace.hpp>

#include <atomic>

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
{
    log4cxx::MDC::clear();
}

static const size_t MaxMessageSize = 1024 * 1024;  //  1 MB, which should be enough for all sane log entries

struct minute_tz
{
    unsigned quarterHours;
    char tzName[10];  //  Longest name in CtiTime::getTZ() is 'BRST-BRA', or 9 characters including null.
};

std::atomic<minute_tz> currentTz { minute_tz{ 1'000'000, { 0 } } };

void Logger::formatAndForceLog(Level level, StreamBufferSink& logStream, const CallSite callSite)
{
    if( LogManager::inShutdown )
    {
        return;
    }

    std::string message = logStream.extractToString();
    const size_t messageSize = message.size();
    if( messageSize > MaxMessageSize )
    {
        message.resize(MaxMessageSize);

        message += StreamBuffer() << "\n[Message truncated to " << MaxMessageSize << ", originally " << messageSize << "]";
    }
    boost::replace_all(message, "\n", _newline);

    LOG4CXX_DECODE_CHAR(msg, message);

    auto tz = currentTz.load();
    const unsigned quarterHours = std::time(nullptr) / 900 % 1'000'000;  //  around 3 years of quarter-hours before wraparound

    //  Is this the same quarter of the hour as the last TZ check?
    if( tz.quarterHours != quarterHours )
    {
        minute_tz newTz { quarterHours, {0} };

        auto tzName = CtiTime::now().getTZ();

        strcpy_s(newTz.tzName, tzName.c_str());

        currentTz.compare_exchange_strong(tz, newTz);

        tz = newTz;
    }

    log4cxx::MDC::put("tz", std::string(tz.tzName));

    const log4cxx::spi::LoggingEventPtr event =
            new log4cxx::spi::LoggingEvent(
                    _logger->_logger->getName(),
                    getLogLevel(level),
                    msg,
                    log4cxx::spi::LocationInfo(callSite.getFilename(), callSite.getFunction(), callSite.getLine()));
    
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
                auto logEvent = std::make_unique<LogEvent>();

                logEvent->event = event;

                _preBufferedLogEvents.push_back(std::move(logEvent));

                return;
            }

            // the logManager has just been started!
            // log all saved events before logging the new event
            log4cxx::helpers::Pool pool;
            for( const auto & event : _preBufferedLogEvents )
            {
                _logger->_logger->callAppenders(event->event, pool);
            }

            _preBufferedLogEvents.clear();
            _ready = true;
        }
    }

    log4cxx::helpers::Pool pool;
    _logger->_logger->callAppenders(event, pool);
}

bool Logger::isLevelEnable(Level level) const
{
    log4cxx::LevelPtr ptr = getLogLevel(level);
    // Make sure level is initialized first
    if(ptr)
    {
        return _logger->_logger->isEnabledFor(ptr);
    }
    else
    {
        return true;
    }
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

