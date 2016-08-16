#pragma once

#include <vector>

namespace Cti {

struct RfnIdentifier;
    
namespace Simulator {

class RfnMeter 
{
public:
    static std::vector<unsigned char> DataStreamingConfig(const std::vector<unsigned char> &request, const RfnIdentifier &rfnId);
};

}
}