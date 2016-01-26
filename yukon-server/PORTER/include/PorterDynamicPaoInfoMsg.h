#pragma once

#include <set>
#include <map>
#include <chrono>

namespace Cti {
namespace Messaging {
namespace Porter {

enum class DynamicPaoInfoDurationKeys
{
    RfnVoltageProfileInterval,  //  data streaming interval
    MctIedLoadProfileInterval
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
