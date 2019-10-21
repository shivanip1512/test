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

    static std::vector<unsigned char> DataStreamingConfig(const std::vector<unsigned char> &request, const RfnIdentifier &rfnId);

    static path_size RequestMeterProgram(const std::vector<unsigned char> &request, const RfnIdentifier &rfnId);
    static path_size RequestMeterProgramContinuation(const std::vector<unsigned char> &request, const RfnIdentifier &rfnId);
};

}
}