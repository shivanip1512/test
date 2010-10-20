#pragma once

#include "cmd_lcr3102.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Lcr3102ThreePartCommand : public Lcr3102Command
{
protected:

    enum State
    {
        State_ExpresscomWrite,
        State_ActOnMessageRead
    };

    State    _state;

    // We want the children (and children ONLY) to be able to call this constructor.
    Lcr3102ThreePartCommand(unsigned length, unsigned retries = 2);

private:

    unsigned _retries;
    unsigned _length;

public:

    virtual request_ptr execute(const CtiTime now);
    virtual request_ptr error  (const CtiTime now, const int error_code, std::string &description);
};

}   // Commands
}   // Devices
}   // Cti
