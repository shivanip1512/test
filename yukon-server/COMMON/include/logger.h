#pragma once

#include "dlldefs.h"
#include "streamBuffer.h"
#include "string_util.h"
#include "exception_helper.h"
#include "boostutil.h"
#include "critical_section.h"
#include "streamBuffer.h"
#include "CallSite.h"

#include <boost/scoped_ptr.hpp>
#include <boost/shared_ptr.hpp>
#include <boost/preprocessor/variadic/size.hpp>
#include <boost/ptr_container/ptr_vector.hpp>

#include <atomic>
#include <string>

namespace Cti {
namespace Logging {

enum Indents
{
    Indent_None,
    Indent_SingleTab
};

class IM_EX_CTIBASE Logger : private boost::noncopyable
{
    friend class LogManager;

    struct LoggerObj; // forward declaration
    std::unique_ptr<LoggerObj> _logger;

    struct LogEvent;  // forward declaration
    std::vector<std::unique_ptr<LogEvent>> _preBufferedLogEvents;
    CtiCriticalSection          _preBufferMux;

    const char * const _newline;

    std::atomic<bool> _ready = false;

    Logger (const std::string &loggerName, const Indents indentStyle);

public:

    ~Logger ();  //  explicit destructor delays deletion of forward declared objects until defined in CPP (e.g. LoggerObj, LogEvent)

    enum Level
    {
        Fatal,
        Error,
        Warn,
        Info,
        Debug,
        Trace,
    };

    void formatAndForceLog (Level level, StreamBufferSink& logStream, const Cti::CallSite callSite);
    bool isLevelEnable     (Level level) const;
    void poke();
};

typedef boost::shared_ptr<Logger> LoggerPtr;

}
} // namespace Cti::Logging

IM_EX_CTIBASE extern Cti::Logging::LoggerPtr dout; // Global instance
IM_EX_CTIBASE extern Cti::Logging::LoggerPtr slog; // Global instance. Simulator log

/*
 * Usage:
 *
 * CTILOG_INFO(dout, "hello world")
 * CTILOG_INFO(dout, "hello world" << int_value)
 *
 * ---------------------
 * Using a predefined log stream:
 *
 * Cti::StreamBuffer outLog;
 * outLog << "test" << hex
 * for(int i = 0; 1< 10 i++) {
 *     outLog << " " << i;
 * }
 *
 * CTILOG_INFO(dout, outLog)
 *
 * ---------------------
 * Exception:
 *
 * catch( const Exception &ex )
 * {
 *     // Exception that inherit from std::exception OR that has no cause
 *     CTILOG_EXCEPTION_ERROR(dout, ex);
 *     CTILOG_EXCEPTION_ERROR(dout, ex, "optional message")
 * }
 * catch(...)
 * {
 *     // Unknown exception (will log the cause if the exception is known)
 *     CTILOG_UNKNOWN_EXCEPTION_ERROR(dout)
 *     CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "optional message")
 * }
 *
 */

// Logging Definitions

#define CTILOG_LOG(level, logger, message) { \
        if( logger->isLevelEnable(level) ) { \
            Cti::StreamBufferSink logStream_; \
            logStream_ << message; \
            logger->formatAndForceLog(level, logStream_, CALLSITE); \
        }}

#define CTILOG_FATAL(logger, message) CTILOG_LOG(Cti::Logging::Logger::Fatal, logger, message)
#define CTILOG_ERROR(logger, message) CTILOG_LOG(Cti::Logging::Logger::Error, logger, message)
#define CTILOG_WARN(logger, message)  CTILOG_LOG(Cti::Logging::Logger::Warn,  logger, message)
#define CTILOG_INFO(logger, message)  CTILOG_LOG(Cti::Logging::Logger::Info,  logger, message)
#define CTILOG_DEBUG(logger, message) CTILOG_LOG(Cti::Logging::Logger::Debug, logger, message)
#define CTILOG_TRACE(logger, message) CTILOG_LOG(Cti::Logging::Logger::Trace, logger, message)

// Exception Logging Definitions

#define CTILOG_EXCEPTION_LOG_3(level, logger, ex) { \
        if( logger->isLevelEnable(level) ) { \
            Cti::StreamBufferSink logStream_; \
            logStream_ <<"caused by exception "<< typeid(ex).name(); \
            const std::string cause_ = Cti::Logging::getExceptionCause(ex); \
            if( ! cause_.empty() ) { \
                logStream_ <<" - "<< cause_; \
            } \
            logger->formatAndForceLog(level, logStream_, CALLSITE); \
        }}

#define CTILOG_EXCEPTION_LOG_4(level, logger, ex, message) { \
        if( logger->isLevelEnable(level) ) { \
            Cti::StreamBufferSink logStream_; \
            logStream_ << message <<"\ncaused by exception "<< typeid(ex).name(); \
            const std::string cause_ = Cti::Logging::getExceptionCause(ex); \
            if( ! cause_.empty() ) { \
                logStream_ <<" - "<< cause_; \
            } \
            logger->formatAndForceLog(level, logStream_, CALLSITE); \
        }}

#define CTILOG_EXCEPTION_LOG(...) \
        BOOST_PP_CAT(BOOST_PP_OVERLOAD(CTILOG_EXCEPTION_LOG_, __VA_ARGS__)(__VA_ARGS__), BOOST_PP_EMPTY())

#define CTILOG_EXCEPTION_FATAL(...) CTILOG_EXCEPTION_LOG(Cti::Logging::Logger::Fatal, __VA_ARGS__)
#define CTILOG_EXCEPTION_ERROR(...) CTILOG_EXCEPTION_LOG(Cti::Logging::Logger::Error, __VA_ARGS__)
#define CTILOG_EXCEPTION_WARN(...)  CTILOG_EXCEPTION_LOG(Cti::Logging::Logger::Warn,  __VA_ARGS__)

// Unknown Exception Logging Definitions

#define CTILOG_UNKNOWN_EXCEPTION_LOG_2(level, logger) { \
        if( logger->isLevelEnable(level) ) { \
            Cti::StreamBufferSink logStream_; \
            logStream_ <<"caused by "<< Cti::Logging::getUnknownExceptionCause(); \
            logger->formatAndForceLog(level, logStream_, CALLSITE); \
        }}

#define CTILOG_UNKNOWN_EXCEPTION_LOG_3(level, logger, message) { \
        if( logger->isLevelEnable(level) ) { \
            Cti::StreamBufferSink logStream_; \
            logStream_ << message <<"\ncaused by "<< Cti::Logging::getUnknownExceptionCause(); \
            logger->formatAndForceLog(level, logStream_, CALLSITE); \
        }}

#define CTILOG_UNKNOWN_EXCEPTION_LOG(...) \
        BOOST_PP_CAT(BOOST_PP_OVERLOAD(CTILOG_UNKNOWN_EXCEPTION_LOG_, __VA_ARGS__)(__VA_ARGS__), BOOST_PP_EMPTY())

#define CTILOG_UNKNOWN_EXCEPTION_FATAL(...) CTILOG_UNKNOWN_EXCEPTION_LOG(Cti::Logging::Logger::Fatal, __VA_ARGS__)
#define CTILOG_UNKNOWN_EXCEPTION_ERROR(...) CTILOG_UNKNOWN_EXCEPTION_LOG(Cti::Logging::Logger::Error, __VA_ARGS__)
#define CTILOG_UNKNOWN_EXCEPTION_WARN(...)  CTILOG_UNKNOWN_EXCEPTION_LOG(Cti::Logging::Logger::Warn,  __VA_ARGS__)

#define CTILOG_ENTRY(logger, message) \
    Cti::StreamBufferSink logStream_; \
    logStream_ << message; \
    Cti::Logging::LogMethodEntry<int> log_entry(logger, logStream_.extractToString(), CALLSITE);

#define CTILOG_ENTRY_RC(logger, message, rc) \
    Cti::StreamBufferSink logStream_; \
    logStream_ << message; \
    Cti::Logging::LogMethodEntry<decltype(rc)> log_entry(logger, logStream_.extractToString(), CALLSITE, rc);

namespace Cti {
    namespace Logging {
        template<class RC>
        class LogMethodEntry
        {
            Cti::Logging::LoggerPtr logger;
            const CallSite callSite;
            bool rcDefined = false;
            RC rc;

        public:
            LogMethodEntry(Cti::Logging::LoggerPtr logger, std::string message, CallSite callSite_) : callSite { callSite_ }
            {
                if(logger == nullptr || !logger->isLevelEnable(Cti::Logging::Logger::Trace)) return;

                this->logger = logger;

                Cti::StreamBufferSink logStream;
                logStream << "Entry " << callSite.getFunction() << "(" << message << ")";

                logger->formatAndForceLog(Cti::Logging::Logger::Trace, logStream, callSite);
            }

            LogMethodEntry(Cti::Logging::LoggerPtr logger, std::string message, CallSite callSite_, RC &rc) : callSite{ callSite_ }
            {
                if(logger == nullptr || !logger->isLevelEnable(Cti::Logging::Logger::Trace)) return;

                this->logger = logger;
                this->rc = rc;
                rcDefined = true;

                Cti::StreamBufferSink logStream;
                logStream << "Entry " << callSite.getFunction() << "(" << message << ")";

                logger->formatAndForceLog(Cti::Logging::Logger::Trace, logStream, callSite);
            }

            ~LogMethodEntry()
            {
                if(logger == nullptr || !logger->isLevelEnable(Cti::Logging::Logger::Trace)) return;

                Cti::StreamBufferSink logStream;

                if(rcDefined)
                {
                    logStream << "Exit " << callSite.getFunction() << "() rc=" << rc;
                }
                else
                {
                    logStream << "Exit " << callSite.getFunction() << "()";
                }
                logger->formatAndForceLog(Cti::Logging::Logger::Trace, logStream, callSite);
            }
        };
    }
} // namespace Cti::Logging
