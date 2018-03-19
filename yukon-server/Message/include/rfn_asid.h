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
    BulkMessageHandler = 0xFE,
};

namespace {

    using ASIDs = ApplicationServiceIdentifiers;

    bool isAsid_E2eDt(const unsigned char &asid)
    {
        static const std::set<unsigned char> e2eDtAsids{
            static_cast<unsigned char>(ASIDs::ChannelManager),
            static_cast<unsigned char>(ASIDs::E2EDT),
            static_cast<unsigned char>(ASIDs::EventManager),
            static_cast<unsigned char>(ASIDs::HubMeterCommandSet),
            static_cast<unsigned char>(ASIDs::BulkMessageHandler),
        };

        return e2eDtAsids.count(asid);
    }

    bool isAsid_Dnp3(const unsigned char &asid)
    {
        return asid == static_cast<unsigned char>(ASIDs::E2EAP_DNP3);
    }

    bool isAsid_DataStreaming(const unsigned char &asid)
    {
        return asid == static_cast<unsigned char>(ASIDs::E2EAP_DataStreaming);
    }

}

}
}
}
