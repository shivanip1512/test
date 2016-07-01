#pragma once

#include "dlldefs.h"

namespace Cti {
namespace Messaging {
namespace Rfn {

enum class IM_EX_MSG ApplicationServiceIdentifiers : unsigned char
{
    //Reserved = 0,
    //C1218PassThrough = 1,
    ChannelManager     = 2,
    //DlmsPassThrough  = 3,
    //MeterTimeSynchronization = 4,
    //HanManager       = 5,
    EventManager       = 6,
    //ConfigurationManagement = 8,
    HubMeterCommandSet = 0x81,
    E2EDT              = 9,
    //E2EAP_SNMP       = 10,
    E2EAP_DNP3         = 11,
    E2EAP_DataStreaming = 12,
};

}
}
}
