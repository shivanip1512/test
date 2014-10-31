#pragma once

#include "dlldefs.h"
#include "timing_util.h"

#include <log4cxx/writerappender.h>

namespace Cti {
namespace Logging {

class IM_EX_CTIBASE TruncatingConsoleAppender : public log4cxx::WriterAppender
{
    __int64 _interval;
    __int64 _intervalEnd;
    unsigned _maxBurstSize, _currentBurst;

    void subAppend(const log4cxx::spi::LoggingEventPtr& event, log4cxx::helpers::Pool& p) override;

public:
    TruncatingConsoleAppender(const log4cxx::LayoutPtr& layout);
    ~TruncatingConsoleAppender();

    void setMaxBurstSize(const unsigned burstSize);
    void setInterval(const Cti::Timing::Chrono interval);
};

}  //  namespace Cti::Logging
}


