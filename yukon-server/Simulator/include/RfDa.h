#pragma once

#include "e2e_packet.h"

namespace Cti::Simulator {

struct RfDa
{
    using Bytes = std::vector<unsigned char>;

    static void processRequest(
        const E2eReplySender e2eReplySender, 
        const e2edt_request_packet& request, 
        const RfnIdentifier rfnIdentifier, 
        const Messaging::Rfn::ApplicationServiceIdentifiers applicationServiceId);
        
    static Bytes buildDnp3Response(const Bytes& request);
};

}