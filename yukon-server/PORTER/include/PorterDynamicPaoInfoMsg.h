#pragma once

#include "ctitime.h"
#include "tbl_dyn_paoinfo.h"

#include <boost/optional.hpp>
#include <boost/variant/variant.hpp>

#include <string>
#include <set>
#include <map>

namespace Cti {
namespace Messaging {
namespace Porter {

enum class DynamicPaoInfoDurationKeys
{
    RfnVoltageProfileInterval  //  data streaming interval
};

enum class DynamicPaoInfoTimestampKeys
{
    RfnVoltageProfileEnabledUntil
};

struct DynamicPaoInfoRequestMsg
{
    long deviceId;

    std::set<DynamicPaoInfoDurationKeys> durationKeys;
    std::set<DynamicPaoInfoTimestampKeys> timestampKeys;
};

struct DynamicPaoInfoResponseMsg
{
    long deviceId;

    using DurationMap = std::map<DynamicPaoInfoDurationKeys, std::chrono::milliseconds>;
    using TimestampMap = std::map<DynamicPaoInfoTimestampKeys, std::chrono::system_clock::time_point>;

    DurationMap durationValues;
    TimestampMap timestampValues;
};

}
}
}
