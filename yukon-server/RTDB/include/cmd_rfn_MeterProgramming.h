#pragma once

#include "cmd_rfn_Individual.h"
#include <map>

namespace Cti::Devices::Commands {

    class IM_EX_DEVDB RfnMeterProgrammingConfigurationCommand
    {
    public:
        enum Command
        {
            Response = 0x92
        };

        std::string     getMeterConfigurationID() const;
        YukonError_t    getStatusCode() const;

    protected:

        RfnCommandResult processResponse(const RfnCommand::RfnResponsePayload & response);

        std::string     _meterConfigurationID;
        YukonError_t    _returnCode;
    };

    class IM_EX_DEVDB RfnMeterProgrammingSetConfigurationCommand : public RfnMeterProgrammingConfigurationCommand, public RfnOneWayCommand
    {
    public:

        RfnMeterProgrammingSetConfigurationCommand(std::string guid, std::size_t length);

        std::string getCommandName() override;

        bool isPost() const override;

        static std::unique_ptr<RfnMeterProgrammingSetConfigurationCommand> handleUnsolicitedReply(const CtiTime now, const RfnResponsePayload & response);

    private:

        enum Command
        {
            Request = 0x90
        };

        Bytes getCommandHeader() override;
        Bytes getCommandData()   override;

        //  unused
        unsigned char getOperation()   const override;
        unsigned char getCommandCode() const override;

        const std::string _guid;
        const std::size_t _length;

    protected: 

        enum TlvType
        {
            TlvType_ConfigurationSize = 0x01,
            TlvType_ConfigurationURI = 0x02
        };
    };

    class IM_EX_DEVDB RfnMeterProgrammingGetConfigurationCommand : public RfnMeterProgrammingConfigurationCommand, public RfnTwoWayCommand,
        InvokerFor<RfnMeterProgrammingGetConfigurationCommand>
    {
    public:

        std::string getCommandName() override;

        RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;

    private:

        enum Command
        {
            Request = 0x91
        };

        Bytes getCommandHeader() override;
        Bytes getCommandData()   override;

        //  unused
        unsigned char getOperation()   const override;
        unsigned char getCommandCode() const override;
    };
}
