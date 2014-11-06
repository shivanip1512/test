#include "precompiled.h"

#include "truncatingConsoleAppender.h"

#include "streamBuffer.h"

#include <log4cxx/helpers/systemoutwriter.h>
#include <log4cxx/helpers/loglog.h>
#include <log4cxx/helpers/transcoder.h>

#include <boost/range/algorithm/count.hpp>

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


void TruncatingConsoleAppender::subAppend(const log4cxx::spi::LoggingEventPtr& event, log4cxx::helpers::Pool& p)
{
    if( event->getTimeStamp() > _intervalEnd )
    {
        _intervalEnd = std::max(apr_time_now(),  event->getTimeStamp() + _interval);
        _currentBurst = 0;

        for each(const log4cxx::spi::LoggingEventPtr &event in _burstBuffer)
        {
            WriterAppender::subAppend(event, p);
        }

        _burstBuffer.clear();
    }

    if( _currentBurst < _maxBurstSize )
    {
        WriterAppender::subAppend(event, p);

        _currentBurst += 1 + boost::range::count(event->getMessage(), '\n');

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
        _burstBuffer.push_back(event);
    }
}

}  //  namespace Cti::Logging
}
