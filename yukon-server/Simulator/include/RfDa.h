#pragma once

#include <vector>

namespace Cti {

struct RfnIdentifier;
    
namespace Simulator {

struct RfDa
{
    using Bytes = std::vector<unsigned char>;

    static Bytes doHubMeterRequest(const Bytes& request, const RfnIdentifier& rfnId);
    static Bytes buildDnp3Response(const Bytes& request);
};

}
}