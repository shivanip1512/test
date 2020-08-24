#pragma once

#include "cmd_rfn_Individual.h"

namespace Cti::Devices::Commands
{

class IM_EX_DEVDB RfnMetrologyCommand
    :   public RfnTwoWayCommand
{
public:

    static bool isSupportedByDeviceType( DeviceTypes type ); 

    enum State
    {
        Enable,
        Disable
    };

protected:

    enum Command
    {
        Request  = 0x57,
        Response = 0x58
    };

    enum Operation
    {
        Operation_SetConfiguration = 0x00,
        Operation_GetConfiguration = 0x01
    };

    RfnMetrologyCommand( Operation op );

    unsigned char getOperation()   const override;

    Bytes getCommandHeader() override;

    RfnCommandResult predecodeCommand(const CtiTime now, const RfnResponsePayload & response);

private:

    unsigned char getCommandCode() const override;

    const Operation _operation;
};

/////

class IM_EX_DEVDB RfnMetrologySetConfigurationCommand
    :   public RfnMetrologyCommand,
        InvokerFor<RfnMetrologySetConfigurationCommand>
{
public:

    RfnMetrologySetConfigurationCommand( State state );

    std::string getCommandName() const override;

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;

private:

    Bytes getCommandData() override;

    bool _disable;
};

/////

class IM_EX_DEVDB RfnMetrologyGetConfigurationCommand
    :   public RfnMetrologyCommand,
        InvokerFor<RfnMetrologyGetConfigurationCommand>

{
public:

    RfnMetrologyGetConfigurationCommand();

    std::string getCommandName() const override;

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;

    std::optional<State> getMetrologyState() const;

private:

    Bytes getCommandData() override;

    std::optional<State> _metrologyState;
};

}   // -- Cti::Devices::Commands

