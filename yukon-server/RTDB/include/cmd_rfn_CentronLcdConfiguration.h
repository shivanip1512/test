#pragma once

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnCentronLcdConfigurationCommand : public RfnCommand
{
public:

    typedef std::vector<unsigned char> metric_vector_t;

    struct ResultHandler
    {
        virtual void handleResult(const RfnCentronLcdConfigurationCommand &cmd) = 0;
    };

    RfnCentronLcdConfigurationCommand(ResultHandler &rh);  //  read
    RfnCentronLcdConfigurationCommand(ResultHandler &rh, const metric_vector_t &display_metrics);  //  write

    virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response);
    virtual RfnCommandResult error  (const CtiTime now, const YukonError_t error_code);

    metric_vector_t getReceivedMetrics() const;

protected:

    virtual unsigned char getCommandCode() const;
    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

private:

    ResultHandler &_rh;

    const boost::optional<metric_vector_t> _display_metrics_to_send;

    metric_vector_t _display_metrics_received;
};

}
}
}
