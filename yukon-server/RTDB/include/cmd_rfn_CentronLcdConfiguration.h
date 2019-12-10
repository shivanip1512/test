#pragma once

#include "cmd_rfn_Individual.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnCentronLcdConfigurationCommand : public RfnTwoWayCommand
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

    struct Read
    {
        virtual metric_map_t getDisplayMetrics() const = 0;
        virtual boost::optional<bool> getDisconnectDisplayDisabled()   const = 0;
        virtual boost::optional<unsigned char> getDigitConfiguration() const = 0;
        virtual boost::optional<unsigned char> getLcdCycleTime()       const = 0;
    };

    virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response) = 0;

    enum
    {
        Slot_DisconnectDisplay  = 0xff,
        Slot_CycleDelay         = 0xfe,
        Slot_DigitConfiguration = 0xfd,
    };

protected:

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

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response) override;

    std::string getCommandName() override;

    const metric_vector_t display_metrics;
    const boost::optional<DisconnectDisplayState> disconnect_display;
    const DisplayDigits display_digits;
    const unsigned char cycle_time;

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();
};


class IM_EX_DEVDB RfnCentronGetLcdConfigurationCommand : public RfnCentronLcdConfigurationCommand, public RfnCentronLcdConfigurationCommand::Read,
       InvokerFor<RfnCentronGetLcdConfigurationCommand>
{
public:

    RfnCentronGetLcdConfigurationCommand();

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response) override;

    std::string getCommandName() override;

    metric_map_t getDisplayMetrics() const override;
    boost::optional<bool> getDisconnectDisplayDisabled()   const override;
    boost::optional<unsigned char> getDigitConfiguration() const override;
    boost::optional<unsigned char> getLcdCycleTime()       const override;

protected:

    unsigned char getOperation() const override;
    Bytes         getCommandData()     override;

    metric_map_t _displayMetrics;
    boost::optional<unsigned char> _disconnectDisplay;
    boost::optional<unsigned char> _cycleTime;
    boost::optional<unsigned char> _digitConfiguration;
};

}
}
}
