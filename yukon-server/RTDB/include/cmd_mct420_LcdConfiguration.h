#pragma once

#include "cmd_mct420.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Mct420LcdConfigurationCommand : public Mct420Command
{
    typedef std::vector<unsigned char> metric_vector_t;

    using state_t = emetcon_request_ptr (Mct420LcdConfigurationCommand::*)(void);

    enum
    {
        //  1 byte per metric
        DisplayMetricSlotsPerRead  = 13,
        DisplayMetricSlotsPerWrite = 13,
        //  2 writes of 13 bytes/metrics each
        TotalDisplayMetricSlots = DisplayMetricSlotsPerWrite * 2
    };

    state_t _executionState;

    //  const parameters that define this request
    metric_vector_t _display_metrics;

    emetcon_request_ptr doCommand();

    emetcon_request_ptr read1();
    emetcon_request_ptr read2();
    emetcon_request_ptr write1();
    emetcon_request_ptr write2();
    emetcon_request_ptr done();

public:

    Mct420LcdConfigurationCommand(const metric_vector_t &display_metrics, bool reads_only);

    enum
    {
        //  public for Mct420Device::initReadKeyStore()
        Read_LcdConfiguration1  = 0x1f6,
        Read_LcdConfiguration2  = 0x1f7,

        Write_LcdConfiguration1 = 0x1f6,
        Write_LcdConfiguration2 = 0x1f7,
    };

    emetcon_request_ptr executeCommand(const CtiTime now) override;
    request_ptr decodeCommand (const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points) override;
    request_ptr error         (const CtiTime now, const YukonError_t error_code, std::string &description) override;
};

}
}
}

