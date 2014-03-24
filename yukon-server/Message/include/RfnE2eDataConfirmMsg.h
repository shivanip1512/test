#pragma once

#include "rfn_identifier.h"
#include "RfnE2eMsg.h"

namespace Cti {
namespace Messaging {
namespace Rfn {

struct /*IM_EX_MSG*/ E2eDataConfirmMsg : E2eMsg  //  no methods, does not need to be exported
{
    //  TODO - convert to a strongly-typed enum when we convert to VS2012
    struct ReplyType
    {
        enum type
        {
            OK = 0,
            DESTINATION_DEVICE_ADDRESS_UNKNOWN = 1,
            DESTINATION_NETWORK_UNAVAILABLE = 2,
            PMTU_LENGTH_EXCEEDED = 3,
            E2E_PROTOCOL_TYPE_NOT_SUPPORTED = 4,
            NETWORK_SERVER_IDENTIFIER_INVALID = 5,
            APPLICATION_SERVICE_IDENTIFIER_INVALID = 6,
            NETWORK_LOAD_CONTROL = 7
        };
    };

    Protocol protocol;
    unsigned char applicationServiceId;
    RfnIdentifier rfnIdentifier;
    ReplyType::type replyType;
};

}
}
}
