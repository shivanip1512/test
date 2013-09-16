#pragma once

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnFocusLcdConfigurationCommand : public RfnCommand
{
public:

    struct DisplayMetric
    {
        unsigned char indexCode;
        unsigned char idCode[2];
    };

    typedef std::vector<DisplayMetric> DisplayMetrics_t;

    struct ResultHandler
    {
        virtual void handleResult( const RfnFocusLcdConfigurationCommand &cmd ) = 0;
    };

    RfnFocusLcdConfigurationCommand( const DisplayMetrics_t & display_metrics );  //  write

    virtual RfnResult decodeCommand(const CtiTime now, const RfnResponse &response);
    virtual RfnResult error  (const CtiTime now, const YukonError_t error_code);

protected:

    virtual unsigned char getCommandCode() const;
    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

private:

    const boost::optional<DisplayMetrics_t> _display_metrics_to_send;
};

}
}
}
