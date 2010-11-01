#pragma once

#include "cmd_lcr3102.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Lcr3102LastMessageReceivedCommand : public Lcr3102Command
{

    enum FunctionReads
    {
        Read_LastMessageReceived = 0x180
    };

    enum ReadLengths
    {
        ReadLength_LastMessageReceived = 13
    };

public:

    virtual request_ptr execute(const CtiTime now);
    virtual request_ptr decode (const CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data> &points);
    virtual request_ptr error  (const CtiTime now, const int error_code, std::string &description) { return request_ptr(); }

};

}
}
}
