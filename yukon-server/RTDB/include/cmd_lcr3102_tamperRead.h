#pragma once

#include "cmd_lcr3102.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Lcr3102TamperReadCommand : public Lcr3102Command
{
private:

    unsigned _retries;

    bool _firstRead;
    bool _dummyRead;

public:
    
    Lcr3102TamperReadCommand();

    virtual request_ptr execute(const CtiTime now);
    virtual request_ptr decode (const CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data> &points);
    virtual request_ptr error  (const CtiTime now, const int error_code, std::string &description);
};

}
}
}
