#include "precompiled.h"

#include "truncatingConsoleAppender.h"

#include "streamBuffer.h"

#include <log4cxx/helpers/systemoutwriter.h>
#include <log4cxx/helpers/loglog.h>
#include <log4cxx/helpers/transcoder.h>

#include <boost/range/algorithm/find_if.hpp>

#include <apr_time.h>

namespace Cti {
namespace Logging {

namespace {

log4cxx::LogString toLogStr(const std::string &str)
{
    log4cxx::LogString logStr;
    log4cxx::helpers::Transcoder::decode(str, logStr);
    return logStr;
}

}

TruncatingConsoleAppender::TruncatingConsoleAppender(const log4cxx::LayoutPtr& layout) :
    _currentBurst(0),
    _maxBurstSize(250),
    _interval(1000 * 1000),
    _intervalEnd(0),
    _burstBuffer(BurstBufferLength)
{
    setLayout(layout);
    log4cxx::helpers::WriterPtr wr(new log4cxx::helpers::SystemOutWriter());
    setWriter(wr);
    log4cxx::helpers::Pool p;
    WriterAppender::activateOptions(p);
}

TruncatingConsoleAppender::~TruncatingConsoleAppender()
{
    finalize();
}

void TruncatingConsoleAppender::setMaxBurstSize(const unsigned burstSize)
{
    _maxBurstSize = burstSize;
}

void TruncatingConsoleAppender::setInterval(const Cti::Timing::Chrono interval)
{
    //  interval is in microseconds
    _interval = interval.milliseconds() * 1000;
}

extern const log4cxx::spi::LoggingEventPtr PokeEvent;

void TruncatingConsoleAppender::subAppend(const log4cxx::spi::LoggingEventPtr& event, log4cxx::helpers::Pool& p)
{
    const auto Now = apr_time_now();
    const auto eventTimestamp = (event != PokeEvent) ? event->getTimeStamp() : Now;

    if( eventTimestamp > _intervalEnd )
    {
        _intervalEnd = Now + _interval;

        _currentBurst = 0;

        for(const auto& bufferedEvent : _burstBuffer)
        {
            WriterAppender::subAppend(bufferedEvent, p);
        }

        _burstBuffer.clear();
    }

    if( event != PokeEvent )
    {
        log4cxx::spi::LoggingEventPtr consoleEvent = event;

        size_t lines = 0;
        const size_t maxNewlines = 500;

        static const auto nth_newline = 
                [&](const log4cxx::LogString::value_type c) 
                {
                    return c == '\n' && ++lines > maxNewlines;
                };

        const auto splitPoint = boost::range::find_if(event->getMessage(), nth_newline);

        if( splitPoint != event->getMessage().end() )
        {
            consoleEvent = log4cxx::spi::LoggingEventPtr{
                new log4cxx::spi::LoggingEvent(
                    event->getLoggerName(),
                    event->getLevel(),
                    log4cxx::LogString{event->getMessage().cbegin(), splitPoint} + L"\nLog entry truncated after 500 lines.",
                    event->getLocationInformation())};
        }

        if( _currentBurst < _maxBurstSize )
        {
            _currentBurst += 1 + lines;
            
            WriterAppender::subAppend(consoleEvent, p);

            if( _currentBurst >= _maxBurstSize )
            {
                StreamBuffer sb;

                sb << "Console output truncated, max burst size of " << _maxBurstSize << " reached.";

                log4cxx::helpers::LogLog::warn(toLogStr(sb.extractToString()));
            }
        }
        else
        {
            //  circular buffer, only retains BurstBufferLength elements
            _burstBuffer.push_back(consoleEvent);
        }
    }
}

}  //  namespace Cti::Logging
}
