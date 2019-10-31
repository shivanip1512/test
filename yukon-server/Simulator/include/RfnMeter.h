#pragma once

#include <vector>

namespace Cti {

struct RfnIdentifier;
    
namespace Simulator {

class RfnMeter 
{
public:
    struct path_size {
        std::string path;
        long size;
    };

    using Bytes = std::vector<unsigned char>;

    static Bytes doChannelManagerRequest(const std::vector<unsigned char> &request, const RfnIdentifier& rfnId);

    static std::unique_ptr<e2edt_reply_packet> processChannelManagerPost()

    static path_size ParseSetMeterProgram(const Bytes& request, const RfnIdentifier& rfnId);
    static path_size RequestMeterProgramContinuation(const Bytes& request, const RfnIdentifier& rfnId);
};

}
}