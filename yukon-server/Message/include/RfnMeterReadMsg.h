#pragma once

#include "rfn_identifier.h"
#include "ctitime.h"

namespace Cti::Messaging::Rfn {

struct RfnMeterReadRequestMsg 
{
    RfnIdentifier rfnIdentifier;
};
// Looks like I need to updaate this
enum class RfnMeterReadingReplyType 
{
    OK,
    NO_NODE,
    NO_GATEWAY,
    FAILURE,
    TIMEOUT, // Yukon specific timeout
};

struct RfnMeterReadReplyMsg
{
    RfnMeterReadingReplyType replyType;
};
// And this
enum class RfnMeterReadingDataReplyType
{
    OK,
    FAILURE,
    NETWORK_TIMEOUT,
    TIMEOUT, // Yukon specific timeout
};

enum class ChannelDataStatus
{
    OK,
    TIMEOUT,
    FAILURE,
    LONG,
};

struct ChannelData
{
    int channelNumber;
    ChannelDataStatus status;
    std::string unitOfMeasure;
    std::set<std::string> unitOfMeasureModifiers;
    double value;
};

struct DatedChannelData
{
    ChannelData channelData;
    CtiTime timeStamp;
    std::optional<ChannelData> baseChannelData;
};
    
struct RfnMeterReadingData
{
    std::vector<ChannelData> channelDataList;
    std::vector<DatedChannelData> datedChannelDataList;
    RfnIdentifier rfnIdentifier;
    CtiTime timeStamp;
    int recordInterval;
};

struct RfnMeterReadDataReplyMsg
{
    RfnMeterReadingDataReplyType replyType;
    RfnMeterReadingData data;
};

}
