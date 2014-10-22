#pragma once

#include "dlldefs.h"
#include "logManager.h"

#include "log4cxx/log4cxx.h"
#include "log4cxx/helpers/objectptr.h"
#include "log4cxx/patternlayout.h"

namespace Cti {
namespace Logging {

class LogLayout : public log4cxx::PatternLayout
{
    const OwnerInfo _ownerInfo;
    bool            _bFirstHeader;

    void appendHeader(log4cxx::LogString& output, log4cxx::helpers::Pool& p) override;
    void appendFooter(log4cxx::LogString& output, log4cxx::helpers::Pool& p) override;

protected:
    LogLayout(const OwnerInfo& ownerInfo, const log4cxx::logchar *patternFormat);
};

struct IM_EX_CTIBASE GeneralLogLayout : LogLayout
{
    GeneralLogLayout(const OwnerInfo& ownerInfo);
};

struct IM_EX_CTIBASE CommsLogLayout : LogLayout
{
    CommsLogLayout(const OwnerInfo& ownerInfo);
};

}
} // namespace Cti::Logging
