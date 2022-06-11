#pragma once

#include <optional>


namespace Cti::Messaging::Rfn
{

struct EdgeDrError
{
    int errorType;
    std::string errorMessage;
};

enum class EdgeUnicastPriority
{
    HIGH,
    LOW
};

inline auto to_string(EdgeUnicastPriority eup)
{
    return eup == EdgeUnicastPriority::HIGH 
        ? "HIGH" 
        : "LOW";
}

struct EdgeDrUnicastRequest
{
    std::string messageGuid;
    std::set<int> paoIds;

    std::vector<unsigned char> payload;

    EdgeUnicastPriority queuePriority;      // = HIGH;
    EdgeUnicastPriority networkPriority;    // = HIGH;
};

struct EdgeDrUnicastResponse
{
    std::string messageGuid;

    std::map<int,short> paoToE2eId;       // map<i32,i16>

    std::optional<EdgeDrError> error;
};

enum class EdgeBroadcastMessagePriority
{
    IMMEDIATE,
    NON_REAL_TIME
};

inline auto to_string(EdgeBroadcastMessagePriority ebmp)
{
    return ebmp == EdgeBroadcastMessagePriority::IMMEDIATE
        ? "IMMEDIATE"
        : "NON_REAL_TIME";
}

struct EdgeDrBroadcastRequest
{
    std::string messageGuid;
    std::vector<unsigned char> payload;
    std::optional<EdgeBroadcastMessagePriority> priority;   // = NON_REAL_TIME;
};

struct EdgeDrBroadcastResponse
{
    std::string messageGuid;
    std::optional<EdgeDrError> error;
};

struct EdgeDrDataNotification
{
    int paoId;
    std::optional<std::vector<unsigned char>> payload;
    std::optional<short> e2eId;   // i16
    std::optional<EdgeDrError> error;
}; 

}

