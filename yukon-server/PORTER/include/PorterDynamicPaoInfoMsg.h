#pragma once

#include <set>
#include <map>
#include <chrono>

namespace Cti::Messaging::Porter {

enum class DynamicPaoInfoDurationKeys
{
    RfnVoltageProfileInterval,  //  data streaming interval
    MctIedLoadProfileInterval
};

enum class DynamicPaoInfoTimestampKeys
{
    RfnVoltageProfileEnabledUntil
};

enum class DynamicPaoInfoPercentageKeys
{
    MeterProgrammingProgress
};

struct DynamicPaoInfoRequestMsg
{
    long deviceId;

    std::set<DynamicPaoInfoDurationKeys> durationKeys;
    std::set<DynamicPaoInfoTimestampKeys> timestampKeys;
    std::set<DynamicPaoInfoPercentageKeys> percentageKeys;
};

struct DynamicPaoInfoResponseMsg
{
    long deviceId;

    using DurationMap = std::map<DynamicPaoInfoDurationKeys, std::chrono::milliseconds>;
    using TimestampMap = std::map<DynamicPaoInfoTimestampKeys, std::chrono::system_clock::time_point>;
    using PercentageMap = std::map<DynamicPaoInfoPercentageKeys, double>;

    DurationMap durationValues;
    TimestampMap timestampValues;
    PercentageMap percentageValues;
};

}