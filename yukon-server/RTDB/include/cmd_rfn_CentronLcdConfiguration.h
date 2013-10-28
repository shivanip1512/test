#pragma once

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnCentronLcdConfigurationCommand : public RfnCommand
{
public:

    typedef std::vector<unsigned char> metric_vector_t;
    typedef std::map<unsigned char, unsigned char> metric_map_t;

    virtual void invokeResultHandler(RfnCommand::ResultHandler &rh) const = 0;

    virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response) = 0;

protected:

    virtual unsigned char getCommandCode() const;
    virtual unsigned char getOperation() const = 0;
};

class IM_EX_DEVDB RfnCentronSetLcdConfigurationCommand : public RfnCentronLcdConfigurationCommand
{
public:

    virtual void invokeResultHandler(RfnCommand::ResultHandler &rh) const;

    RfnCentronSetLcdConfigurationCommand(const metric_vector_t &display_metrics);

    virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response);

    const metric_vector_t display_metrics_to_send;

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();
};


class IM_EX_DEVDB RfnCentronGetLcdConfigurationCommand : public RfnCentronLcdConfigurationCommand
{
public:

    virtual void invokeResultHandler(RfnCommand::ResultHandler &rh) const;

    RfnCentronGetLcdConfigurationCommand();

    virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response);

    metric_map_t getReceivedMetrics() const;

protected:

    enum
    {
        Slot_DisconnectDisplay  = 0xff,
        Slot_CycleDelay         = 0xfe,
        Slot_DigitConfiguration = 0xfd,
    };

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    metric_map_t _display_metrics_received;
    boost::optional<unsigned char> _disconnectDisplay;
    boost::optional<unsigned char> _cycleDelay;
    boost::optional<unsigned char> _digitConfiguration;
};

}
}
}
