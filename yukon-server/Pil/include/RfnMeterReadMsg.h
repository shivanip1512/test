#pragma once

#include "rfn_identifier.h"
#include "ctitime.h"

#include "message_factory.h"
#include "amq_connection.h"

namespace Cti::Messaging::Pil {

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
/*
namespace Cti::Messaging::Serialization {

template<>
boost::optional<Porter::RfnMeterReadRequestMsg> MessageSerializer<Porter::RfnMeterReadRequestMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage& msg);

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<Porter::RfnMeterReadReplyMsg>::serialize(const Porter::RfnMeterReadReplyMsg& msg);

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<Porter::RfnMeterReadDataReplyMsg>::serialize(const Porter::RfnMeterReadDataReplyMsg& msg);

}
*/