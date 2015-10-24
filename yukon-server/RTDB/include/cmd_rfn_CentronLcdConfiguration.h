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

    enum DisconnectDisplayState
    {
        DisconnectDisplayDisabled,
        DisconnectDisplayEnabled
    };

    enum DisplayDigits
    {
        DisplayDigits4x1 = 4,
        DisplayDigits5x1 = 5,
        DisplayDigits6x1 = 6,
    };

    virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response) = 0;

protected:

    enum
    {
        Slot_DisconnectDisplay  = 0xff,
        Slot_CycleDelay         = 0xfe,
        Slot_DigitConfiguration = 0xfd,
    };

    virtual unsigned char getCommandCode() const;
    virtual unsigned char getOperation() const = 0;
};

class IM_EX_DEVDB RfnCentronSetLcdConfigurationCommand : public RfnCentronLcdConfigurationCommand,
       InvokerFor<RfnCentronSetLcdConfigurationCommand>
{
public:

    RfnCentronSetLcdConfigurationCommand(
            const metric_vector_t &display_metrics,
            const DisconnectDisplayState disconnect_display,
            const DisplayDigits display_digits,
            const unsigned char cycle_time);

    RfnCentronSetLcdConfigurationCommand(
            const metric_vector_t &display_metrics,
            const DisplayDigits display_digits,
            const unsigned char cycle_time);

    virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response);

    const metric_vector_t display_metrics;
    const boost::optional<DisconnectDisplayState> disconnect_display;
    const DisplayDigits display_digits;
    const unsigned char cycle_time;

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();
};


class IM_EX_DEVDB RfnCentronGetLcdConfigurationCommand : public RfnCentronLcdConfigurationCommand,
       InvokerFor<RfnCentronGetLcdConfigurationCommand>
{
public:

    RfnCentronGetLcdConfigurationCommand();

    virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response);

    metric_map_t getDisplayMetrics() const;
    boost::optional<bool> getDisconnectDisplayDisabled() const;
    boost::optional<unsigned char> getDigitConfiguration() const;
    boost::optional<unsigned char> getLcdCycleTime() const;

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    metric_map_t _displayMetrics;
    boost::optional<unsigned char> _disconnectDisplay;
    boost::optional<unsigned char> _cycleTime;
    boost::optional<unsigned char> _digitConfiguration;
};

}
}
}
