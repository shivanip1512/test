#pragma once

#include "e2e_packet.h"

namespace Cti::Simulator {

struct RfDa
{
    using Bytes = std::vector<unsigned char>;

    using E2eReplySender = std::function<void(const Messaging::Rfn::E2eDataRequestMsg &, const e2edt_reply_packet &)>;

    static void processRequest(const E2eReplySender e2eReplySender, const e2edt_request_packet& request, const Messaging::Rfn::E2eDataRequestMsg& requestMsg);
        
    static Bytes buildDnp3Response(const Bytes& request);
};

}