#pragma once

#include "e2e_packet.h"

namespace Cti::Simulator {

struct RfnMeter 
{
    static void processRequest(const E2eRequestSender e2eRequestSender, const E2eReplySender e2eReplySender, const e2edt_request_packet& request_packet, const Messaging::Rfn::E2eDataRequestMsg& requestMsg);
    static void processReply  (const E2eRequestSender e2eRequestSender, const e2edt_reply_packet& reply_packet, const Messaging::Rfn::E2eDataRequestMsg& requestMsg);
};

}