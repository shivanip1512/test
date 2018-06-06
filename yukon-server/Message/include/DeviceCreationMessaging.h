#pragma once

#include "dlldefs.h"
#include "ctitime.h"
#include "rfn_identifier.h"

namespace Cti {
namespace Messaging {

struct IM_EX_MSG RfnDeviceCreationRequestMessage
{
    RfnIdentifier   rfnIdentifier;

    RfnDeviceCreationRequestMessage(const RfnIdentifier & rfnId);
};

struct IM_EX_MSG RfnDeviceCreationReplyMessage
{
    int             paoId;
    std::string     category,
                    deviceType;

    RfnDeviceCreationReplyMessage();
};

}
}
