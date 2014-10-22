#pragma once

#include "dlldefs.h"
#include "streamBuffer.h"
#include "string_util.h"
#include "exception_helper.h"
#include "boostutil.h"
#include "critical_section.h"
#include "atomic.h"

#include <string>

#include "boost/scoped_ptr.hpp"
#include "boost/shared_ptr.hpp"
#include "boost/preprocessor/variadic/size.hpp"
#include "boost/ptr_container/ptr_vector.hpp"

namespace Cti {
namespace Logging {

enum Indents
{
    Indent_None,
    Indent_SingleTab
};

class IM_EX_CTIBASE Logger : private boost::noncopyable
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Logger(const Logger&);
    Logger& operator=(const Logger&);

    friend class LogManager;

    struct LoggerObj; // forward declaration
    boost::scoped_ptr<LoggerObj> _logger;

    struct LogEvent;  // forward declaration
    boost::ptr_vector<LogEvent> _preBufferLogEvents;
    CtiCriticalSection          _preBufferMux;

    const char * const _newline;

    Atomic<bool> _ready;

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

    void formatAndForceLog (Level level, StreamBufferSink& logStream, const char* file, const char* func, int line);
    bool isLevelEnable     (Level level) const;
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
            logger->formatAndForceLog(level, logStream_, __FILE__, __FUNCSIG__, __LINE__); \
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
            logStream_ <<"\ncaused by exception "<< typeid(ex).name(); \
            const std::string cause_ = Cti::Logging::getExceptionCause(ex); \
            if( ! cause_.empty() ) { \
                logStream_ <<" - "<< cause_; \
            } \
            logger->formatAndForceLog(level, logStream_, __FILE__, __FUNCSIG__, __LINE__); \
        }}

#define CTILOG_EXCEPTION_LOG_4(level, logger, ex, message) { \
        if( logger->isLevelEnable(level) ) { \
            Cti::StreamBufferSink logStream_; \
            logStream_ << message <<"\ncaused by exception "<< typeid(ex).name(); \
            const std::string cause_ = Cti::Logging::getExceptionCause(ex); \
            if( ! cause_.empty() ) { \
                logStream_ <<" - "<< cause_; \
            } \
            logger->formatAndForceLog(level, logStream_, __FILE__, __FUNCSIG__, __LINE__); \
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
            logStream_ <<"\ncaused by "<< Cti::Logging::getUnknownExceptionCause(); \
            logger->formatAndForceLog(level, logStream_, __FILE__, __FUNCSIG__, __LINE__); \
        }}

#define CTILOG_UNKNOWN_EXCEPTION_LOG_3(level, logger, message) { \
        if( logger->isLevelEnable(level) ) { \
            Cti::StreamBufferSink logStream_; \
            logStream_ << message <<"\ncaused by "<< Cti::Logging::getUnknownExceptionCause(); \
            logger->formatAndForceLog(level, logStream_, __FILE__, __FUNCSIG__, __LINE__); \
        }}

#define CTILOG_UNKNOWN_EXCEPTION_LOG(...) \
        BOOST_PP_CAT(BOOST_PP_OVERLOAD(CTILOG_UNKNOWN_EXCEPTION_LOG_, __VA_ARGS__)(__VA_ARGS__), BOOST_PP_EMPTY())

#define CTILOG_UNKNOWN_EXCEPTION_FATAL(...) CTILOG_UNKNOWN_EXCEPTION_LOG(Cti::Logging::Logger::Fatal, __VA_ARGS__)
#define CTILOG_UNKNOWN_EXCEPTION_ERROR(...) CTILOG_UNKNOWN_EXCEPTION_LOG(Cti::Logging::Logger::Error, __VA_ARGS__)
#define CTILOG_UNKNOWN_EXCEPTION_WARN(...)  CTILOG_UNKNOWN_EXCEPTION_LOG(Cti::Logging::Logger::Warn,  __VA_ARGS__)
