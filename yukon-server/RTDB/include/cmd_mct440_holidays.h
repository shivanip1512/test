#pragma once

#include "cmd_dlc.h"

#include <boost/optional.hpp>

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Mct440HolidaysCommand : public DlcCommand
{
    std::set<CtiDate> _holidays;
    const CtiTime _now;

protected:

    typedef boost::function<request_ptr (Mct440HolidaysCommand *)> state_t;

    state_t _executionState;

    request_ptr read1();
    request_ptr read2();
    request_ptr read3();
    request_ptr write();
    request_ptr done();

    request_ptr doCommand();

public:

    // Write constructor
    Mct440HolidaysCommand(const CtiTime Now, const std::set<CtiDate> &holidays);

    // Read constructor
    Mct440HolidaysCommand();

    virtual request_ptr execute(const CtiTime now);
    virtual request_ptr decode (const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points);
    virtual request_ptr error  (const CtiTime now, const int error_code, std::string &description);
};

}
}
}

