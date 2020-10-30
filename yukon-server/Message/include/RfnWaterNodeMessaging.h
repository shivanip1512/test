#pragma once

#include "dlldefs.h"

#include "rfn_identifier.h"
#include "NetworkManagerMessaging.h"
#include "ctitime.h"

#include <boost/cstdint.hpp>
#include <boost/optional.hpp>

namespace Cti       {
namespace Messaging {
namespace Rfn       {

struct IM_EX_MSG RfnSetChannelConfigRequestMessage
{
    RfnIdentifier   rfnIdentifier;
    int             reportingInterval,
                    recordingInterval;

    boost::optional<NetworkManagerRequestHeader> header;


    RfnSetChannelConfigRequestMessage();
};

struct IM_EX_MSG RfnSetChannelConfigReplyMessage
{
    const int replyCode;

    std::string description() const;

    RfnSetChannelConfigReplyMessage( const int thriftCode );
};

struct IM_EX_MSG RfnGetChannelConfigRequestMessage
{
    const RfnIdentifier rfnIdentifier;

    RfnGetChannelConfigRequestMessage( const RfnIdentifier & rfnId );
};

struct IM_EX_MSG RfnGetChannelConfigReplyMessage
{
    enum ReplyCode
    {
        SUCCESS = 0,
        INVALID_DEVICE,    
        NO_NODE,
        FAILURE,
    };

    struct IM_EX_MSG ChannelInfo
    {
        std::string             UOM;
        std::set<std::string>   uomModifier;
        int                     channelNumber;
        bool                    enabled;

        bool operator<( const ChannelInfo & rhs ) const;
    };

    CtiTime                 timestamp;
    std::set<ChannelInfo>   channelInfo;
    RfnIdentifier           rfnIdentifier;
    int                     recordingInterval,
                            reportingInterval;
    int                     replyCode;

    std::string description() const;

    std::string to_string() const;

    RfnGetChannelConfigReplyMessage();
};

}
}
}
