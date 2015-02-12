#pragma once

#include "dlldefs.h"
#include "timing_util.h"

#include <boost/circular_buffer.hpp>

#include <log4cxx/writerappender.h>

namespace Cti {
namespace Logging {

class IM_EX_CTIBASE TruncatingConsoleAppender : public log4cxx::WriterAppender
{
    long long _interval;
    long long _intervalEnd;
    unsigned _maxBurstSize, _currentBurst;
    static const size_t BurstBufferLength = 10;
    boost::circular_buffer<log4cxx::spi::LoggingEventPtr> _burstBuffer;

    void subAppend(const log4cxx::spi::LoggingEventPtr& event, log4cxx::helpers::Pool& p) override;

public:
    TruncatingConsoleAppender(const log4cxx::LayoutPtr& layout);
    ~TruncatingConsoleAppender();

    void setMaxBurstSize(const unsigned burstSize);
    void setInterval(const Cti::Timing::Chrono interval);
};

}  //  namespace Cti::Logging
}


