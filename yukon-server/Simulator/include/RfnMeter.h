#pragma once

#include "e2e_packet.h"
#include "rfn_identifier.h"
#include "rfn_asid.h"

namespace Cti::Simulator {

struct RfnMeter 
{
    static void processRequest(
        const E2eRequestSender e2eRequestSender, 
        const E2eReplySender e2eReplySender, 
        const e2edt_request_packet& request_packet, 
        const RfnIdentifier rfnIdentifier, 
        const Messaging::Rfn::ApplicationServiceIdentifiers applicationServiceId);
    
    static void processReply(
        const E2eRequestSender e2eRequestSender, 
        const e2edt_reply_packet& reply_packet, 
        const RfnIdentifier rfnIdentifier, 
        const Messaging::Rfn::ApplicationServiceIdentifiers applicationServiceId);
};

}