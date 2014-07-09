#pragma once

#include "cmd_mct410.h"

namespace Cti {
namespace Devices {
namespace Commands {

class Mct420Command : public Mct410Command
{
protected:

    virtual request_ptr error(const CtiTime now, const YukonError_t error_code, std::string &description);
};

}
}
}

