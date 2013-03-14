#pragma once

#include "cmd_mct420.h"

#include <boost/optional.hpp>

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Mct420MeterParametersCommand : public Mct420Command
{
    unsigned _cycleTime;
    boost::optional<unsigned> _transformerRatio;
    bool _disconnectDisplayDisabled;

protected:

    typedef boost::function<request_ptr (Mct420MeterParametersCommand *)> state_t;

    state_t _executionState;

    request_ptr read();
    request_ptr write();
    request_ptr done();

    request_ptr doCommand();

    virtual unsigned char getDisplayParametersByte();

public:

    // Write constructor
    Mct420MeterParametersCommand(const unsigned cycleTime, bool disconnectDisplayDisabled, boost::optional<unsigned> transformerRatio);

    // Read constructor
    Mct420MeterParametersCommand();

    enum
    {
        //  public for Mct420Device::initReadKeyStore()
        Read_MeterParameters  = 0x1f3,

        Write_MeterParameters = 0x1f3,
    };

    virtual request_ptr execute(const CtiTime now);
    virtual request_ptr decode (const CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data> &points);
};

}
}
}

