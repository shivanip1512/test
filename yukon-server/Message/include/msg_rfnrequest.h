#pragma once

#include <vector>

namespace Cti {
namespace Messaging {

struct RfnRequestMsg
{
    long deviceId;
    std::vector<unsigned char> payload;
};


}
}

