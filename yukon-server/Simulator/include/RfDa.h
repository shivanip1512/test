#pragma once

#include <vector>

namespace Cti {

struct RfnIdentifier;
    
namespace Simulator {

class RfDa
{
public:
    static std::vector<unsigned char> Dnp3Address(const std::vector<unsigned char> &request, const RfnIdentifier &rfnId);
};

}
}