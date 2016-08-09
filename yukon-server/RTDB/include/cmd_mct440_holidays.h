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

    using state_t = emetcon_request_ptr (Mct440HolidaysCommand::*)(void);

    state_t _executionState;

    emetcon_request_ptr read1();
    emetcon_request_ptr read2();
    emetcon_request_ptr read3();
    emetcon_request_ptr write();
    emetcon_request_ptr done();

    emetcon_request_ptr doCommand();

public:

    // Write constructor
    Mct440HolidaysCommand(const CtiTime Now, const std::set<CtiDate> &holidays);

    // Read constructor
    Mct440HolidaysCommand();

    emetcon_request_ptr executeCommand(const CtiTime now) override;
    request_ptr decodeCommand (const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points) override;
    request_ptr error         (const CtiTime now, const YukonError_t error_code, std::string &description) override;
};

}
}
}

