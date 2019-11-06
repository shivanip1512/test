#pragma once

#include "rfn_identifier.h"
#include "rfn_asid.h"
#include "RfnE2eMsg.h"

#include <optional>
#include <string>

namespace Cti::Messaging::Rfn {

struct /*IM_EX_MSG*/ E2eDataIndicationMsg : E2eMsg  //  no methods, does not need to be exported
{
    Protocol protocol;
    ApplicationServiceIdentifiers applicationServiceId;
    RfnIdentifier rfnIdentifier;
    bool highPriority;
    std::optional<std::string> security;
    std::vector<unsigned char> payload;
};

}