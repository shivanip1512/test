#pragma once

#include "rfn_identifier.h"
#include "rfn_asid.h"
#include "RfnE2eMsg.h"
#include "NetworkManagerMessaging.h"


namespace Cti::Messaging::Rfn {

struct /*IM_EX_MSG*/ E2eDataRequestMsg : E2eMsg  //  no methods, does not need to be exported
{
    Protocol protocol;
    ApplicationServiceIdentifiers applicationServiceId;
    RfnIdentifier rfnIdentifier;
    bool highPriority;
    std::optional<std::string> security;
    std::vector<unsigned char> payload;

    NetworkManagerRequestHeader header;
};

}