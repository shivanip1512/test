#pragma once

#include "dlldefs.h"
#include "streamBuffer.h"

namespace Cti::Messaging::Rfn {

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

    bool isAsid_E2eDt(const ASIDs asid)
    {
        switch( asid )
        {
            case ASIDs::ChannelManager:
            case ASIDs::E2EDT:
            case ASIDs::EventManager:
            case ASIDs::HubMeterCommandSet:
            case ASIDs::BulkMessageHandler:
                return true;
            default:
                return false;
        }
    }

    bool isAsid_Dnp3(const ASIDs asid)
    {
        return asid == ASIDs::E2EAP_DNP3;
    }

    bool isAsid_DataStreaming(const ASIDs asid)
    {
        return asid == ASIDs::E2EAP_DataStreaming;
    }

    StreamBufferSink& operator<<(StreamBufferSink& sb, ASIDs asid)
    {
        return sb << static_cast<unsigned char>(asid);
    }
}

}