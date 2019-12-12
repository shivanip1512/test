#pragma once

#include "cmd_rfn_Individual.h"
#include <map>

namespace Cti::Devices::Commands {

class IM_EX_DEVDB RfnDemandIntervalCommand : public RfnTwoWayCommand
{
protected:

    RfnDemandIntervalCommand() = default;

    virtual Bytes getCommandHeader() override = 0;
    virtual Bytes getCommandData()   override = 0;

    //  unused
    unsigned char getOperation()   const override;
    unsigned char getCommandCode() const override;

public:

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response ) = 0;
};

class IM_EX_DEVDB RfnDemandIntervalSetConfigurationCommand : public RfnDemandIntervalCommand,
       InvokerFor<RfnDemandIntervalSetConfigurationCommand>
{
public:

    RfnDemandIntervalSetConfigurationCommand( const std::chrono::minutes interval );

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response ) override;

    std::string getCommandName() override;

    const std::chrono::minutes demandInterval;

private:

    enum Command
    {
        Request = 0x62,
        Response = 0x63
    };

    Bytes getCommandHeader() override;
    Bytes getCommandData()   override;
};

class IM_EX_DEVDB RfnDemandIntervalGetConfigurationCommand : public RfnDemandIntervalCommand,
    InvokerFor<RfnDemandIntervalGetConfigurationCommand>
{
public:

    RfnCommandResult decodeCommand(const CtiTime now,
        const RfnResponsePayload & response) override;

    std::string getCommandName() override;

    std::chrono::minutes getDemandInterval() const;

private:

    enum Command
    {
        Request = 0x64,
        Response = 0x65
    };

    Bytes getCommandHeader() override;
    Bytes getCommandData()   override;

    std::chrono::minutes _interval;
};

}