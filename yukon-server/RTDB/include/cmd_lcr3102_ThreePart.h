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
        State_Reading
    };

    State    _state;

    // We want the children (and children ONLY) to be able to call this constructor.
    Lcr3102ThreePartCommand(unsigned length, unsigned retries = 2);

    virtual request_ptr decodeReading(const CtiTime now, const unsigned function, const Bytes &payload, std::string &description, std::vector<point_data> &points) = 0;

    virtual request_ptr makeRequest(const CtiTime now);

private:

    unsigned _retries;
    unsigned _length;

public:

    virtual request_ptr executeCommand(const CtiTime now);
    virtual request_ptr decodeCommand (const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points);
    virtual request_ptr error         (const CtiTime now, const YukonError_t error_code, std::string &description);

};

}
}
}
