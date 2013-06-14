#pragma once

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnCentronLcdConfigurationCommand : public RfnCommand
{
    typedef std::vector<unsigned char> metric_vector_t;

    //  const parameters that define this request
    metric_vector_t _display_metrics;

    bool _read_only;

protected:

    virtual unsigned char getCommandCode() const;
    virtual unsigned char getOperation() const;
    virtual Bytes         getData();

public:

    RfnCentronLcdConfigurationCommand(const metric_vector_t &display_metrics, bool reads_only);

    enum
    {
        LcdConfiguration_CommandCode_Request  = 0x69,
        LcdConfiguration_CommandCode_Response = 0x71,

        LcdConfiguration_Operation_Write      = 0x00,
        LcdConfiguration_Operation_Read       = 0x01,
    };

    virtual RfnResult decode (const CtiTime now, const RfnResponse &response);
    virtual RfnResult error  (const CtiTime now, const YukonError_t error_code);

};

}
}
}
