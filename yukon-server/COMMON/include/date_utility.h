#pragma once

#include "dlldefs.h"

#include "ctidate.h"

#include <boost/optional.hpp>

#include <string>
#include <chrono>

namespace Cti {

IM_EX_CTIBASE CtiDate parseDateString(std::string date_str);

struct TimeParts
{
    unsigned hour, minute, second;
};

IM_EX_CTIBASE boost::optional<TimeParts> parseTimeString(std::string time_str);

IM_EX_CTIBASE boost::optional<std::chrono::duration<double>> parseDurationString(const std::string duration);

}
