#pragma once

#include "rfn_identifier.h"
#include "ctitime.h"

namespace Cti::Messaging::Rfn {

struct RfnMeterReadRequestMsg 
{
    RfnIdentifier rfnIdentifier;
};

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

enum class RfnMeterReadingDataReplyType
{
    OK,
    FAILURE,
    NETWORK_TIMEOUT,
    TIMEOUT, // Yukon specific timeout
};

enum class ChannelDataStatus
{
    OK, //Used by partial and full reads
    PARTIAL_READ_TIMEOUT,
    PARTIAL_READ_FAILURE,
    PARTIAL_READ_LONG,
    FULL_READ_PASSWORD_ERROR,
    FULL_READ_BUSY_ERROR,
    FULL_READ_TIMEOUT_ERROR,
    FULL_READ_PROTOCOL_ERROR,
    FULL_READ_NO_SUCH_CHANNEL_ERROR,
    FULL_READ_READ_RESPONSE_ERROR_UNKNOWN,
    FULL_READ_UNKNOWN,
    FAILURE
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
