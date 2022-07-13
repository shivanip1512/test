#pragma once

#include <optional>

#include "rfn_identifier.h"
#include "NetworkManagerMessaging.h"



namespace Cti::Messaging::Rfn
{

enum class RfnBroadcastDeliveryType
{
    IMMEDIATE,
    NON_REAL_TIME
};

inline auto to_string( RfnBroadcastDeliveryType e )
{
    return
        e == RfnBroadcastDeliveryType::IMMEDIATE 
            ? "IMMEDIATE" 
            : "NON_REAL_TIME";
}

struct RfnBroadcastRequest
{
    char    sourceId;
    short   messageId;
    char    broadcastApplicationId;

    RfnBroadcastDeliveryType    deliveryType;

    std::vector<unsigned char>  payload;

    std::optional<NetworkManagerRequestHeader>  header;
};

struct RfnBroadcastReply
{
    long replyType;

    std::optional<std::string> failureReason;

    std::map<RfnIdentifier, std::string> gatewayErrors;

    std::optional<NetworkManagerRequestHeader> header;
};

}

