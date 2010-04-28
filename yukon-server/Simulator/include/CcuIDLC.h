#pragma once

#include "PlcTransmitter.h"

namespace Cti {
namespace Simulator {

class CcuIDLC : public PlcTransmitter
{
public:

    enum 
    {
        Hdlc_FramingFlag = 0x7e
    };

    static bool addressAvailable(Comms &comms);
    static error_t peekAddress(Comms &comms, unsigned &address);
};

}
}
