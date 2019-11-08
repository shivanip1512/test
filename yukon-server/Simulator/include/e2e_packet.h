#pragma once

#include "e2e_messaging.h"
#include "coap_helper.h"

#include <optional>

namespace Cti::Simulator {

struct e2edt_packet
{
    virtual ~e2edt_packet() = default;  //  to provide a vtable and RTTI

    std::vector<unsigned char> payload;
    unsigned token;
    unsigned id;
    std::optional<Protocols::E2e::Block> block;
    std::string path;
};

struct e2edt_request_packet : e2edt_packet
{
    bool confirmable;
    Protocols::Coap::RequestMethod method;
};

struct e2edt_reply_packet : e2edt_packet
{
    Protocols::Coap::ResponseCode status;
};

}