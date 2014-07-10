#pragma once

#include "ctidate.h"
#include "dlldefs.h"

#include <boost/optional.hpp>

#include <string>

namespace Cti {

IM_EX_CTIBASE CtiDate parseDateString(std::string date_str);

struct TimeParts
{
    unsigned hour, minute, second;
};

IM_EX_CTIBASE boost::optional<TimeParts> parseTimeString(std::string time_str);

}
