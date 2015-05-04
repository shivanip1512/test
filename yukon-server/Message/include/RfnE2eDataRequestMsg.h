#pragma once

#include "rfn_identifier.h"
#include "RfnE2eMsg.h"
#include "NetworkManagerMessaging.h"

#include <boost/optional.hpp>

#include <string>

namespace Cti {
namespace Messaging {
namespace Rfn {

struct /*IM_EX_MSG*/ E2eDataRequestMsg : E2eMsg  //  no methods, does not need to be exported
{
    Protocol protocol;
    unsigned char applicationServiceId;
    RfnIdentifier rfnIdentifier;
    bool highPriority;
    boost::optional<std::string> security;
    std::vector<unsigned char> payload;

    NetworkManagerRequestHeader header;
};

}
}
}
