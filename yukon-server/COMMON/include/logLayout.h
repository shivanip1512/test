#pragma once

#include "dlldefs.h"
#include "logManager.h"

#include "log4cxx/log4cxx.h"
#include "log4cxx/helpers/objectptr.h"
#include "log4cxx/patternlayout.h"

namespace Cti {
namespace Logging {

class IM_EX_CTIBASE LogLayout : public log4cxx::PatternLayout
{
    const OwnerInfo _ownerInfo;
    bool            _bFirstHeader;

public:
    LogLayout(const OwnerInfo& ownerInfo);

    void appendHeader(log4cxx::LogString& output, log4cxx::helpers::Pool& p) override;
    void appendFooter(log4cxx::LogString& output, log4cxx::helpers::Pool& p) override;
};

}
} // namespace Cti::Logging
