#pragma once

#include "e2e_packet.h"

#include <vector>

namespace Cti {

struct RfnIdentifier;
    
namespace Simulator {

class RfnMeter 
{
public:
    using Bytes = std::vector<unsigned char>;

    static Bytes doChannelManagerRequest(const std::vector<unsigned char> &request, const RfnIdentifier& rfnId);

    static std::unique_ptr<e2edt_request_packet> processReply(const e2edt_reply_packet& reply_packet, const RfnIdentifier& rfnId);

    static std::unique_ptr<e2edt_request_packet> processChannelManagerPost(const e2edt_request_packet& post_request, const RfnIdentifier& rfnId);

private:
    struct path_size {
        std::string path;
        long size;
    };

    static std::optional<path_size> ParseSetMeterProgram(const Bytes& request, const RfnIdentifier& rfnId);
};

}
}