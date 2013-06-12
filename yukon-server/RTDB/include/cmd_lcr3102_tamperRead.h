#pragma once

#include "cmd_lcr3102_ThreePart.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Lcr3102TamperReadCommand : public Lcr3102ThreePartCommand
{
private:

    enum ReadLengths
    {
        ReadLength_Tamper = 7
    };

public:

    Lcr3102TamperReadCommand();

    virtual request_ptr decodeReading(const CtiTime now, const unsigned function, const Bytes &payload, std::string &description, std::vector<point_data> &points);
};

}
}
}
