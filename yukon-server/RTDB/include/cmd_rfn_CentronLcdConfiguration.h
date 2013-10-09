#pragma once

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnCentronLcdConfigurationCommand : public RfnCommand
{
public:

    typedef std::vector<unsigned char> metric_vector_t;

    virtual void invokeResultHandler(RfnCommand::ResultHandler &rh) const;

    RfnCentronLcdConfigurationCommand();  //  read
    RfnCentronLcdConfigurationCommand(const metric_vector_t &display_metrics);  //  write

    virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response);

    metric_vector_t getReceivedMetrics() const;

protected:

    virtual unsigned char getCommandCode() const;
    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

private:

    const boost::optional<metric_vector_t> _display_metrics_to_send;

    metric_vector_t _display_metrics_received;
};

}
}
}
