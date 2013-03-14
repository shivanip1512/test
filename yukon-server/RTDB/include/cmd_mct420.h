#pragma once

#include "cmd_mct410.h"

namespace Cti {
namespace Devices {
namespace Commands {

class Mct420Command : public Mct410Command
{
protected:

    virtual request_ptr error(const CtiTime now, const int error_code, std::string &description);
};

}
}
}

