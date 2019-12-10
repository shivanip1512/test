#pragma once

#include "cmd_rfn_Individual.h"
#include <map>

namespace Cti::Devices::Commands {

    class IM_EX_DEVDB RfnMeterProgrammingCommand : public RfnIndividualCommand
    {
    protected:

        RfnMeterProgrammingCommand() = default;

        virtual Bytes getCommandHeader() override = 0;
        virtual Bytes getCommandData()   override = 0;

        //  unused
        unsigned char getOperation()   const override;
        unsigned char getCommandCode() const override;

    public:

        virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) = 0;
    };

    class IM_EX_DEVDB RfnMeterProgrammingConfigurationCommand : public RfnMeterProgrammingCommand
    {
    public:
        enum Command
        {
            Response = 0x92
        };

        RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;

        std::string     getMeterConfigurationID() const;
        YukonError_t    getStatusCode() const;

    protected:

        std::string     _meterConfigurationID;
        YukonError_t    _returnCode;
    };

    class IM_EX_DEVDB RfnMeterProgrammingSetConfigurationCommand : public RfnMeterProgrammingConfigurationCommand,
        InvokerFor<RfnMeterProgrammingSetConfigurationCommand>
    {
    public:

        RfnMeterProgrammingSetConfigurationCommand(std::string guid, std::size_t length);

        std::string getCommandName() override;

        bool isPost() const override;

        bool isOneWay() const override;

        static std::unique_ptr<RfnMeterProgrammingSetConfigurationCommand> handleUnsolicitedReply(const CtiTime now, const RfnResponsePayload & response);

    private:

        enum Command
        {
            Request = 0x90
        };

        Bytes getCommandHeader() override;
        Bytes getCommandData()   override;

        const std::string _guid;
        const std::size_t _length;

    protected: 

        enum TlvType
        {
            TlvType_ConfigurationSize = 0x01,
            TlvType_ConfigurationURI = 0x02
        };
    };

    class IM_EX_DEVDB RfnMeterProgrammingGetConfigurationCommand : public RfnMeterProgrammingConfigurationCommand,
        InvokerFor<RfnMeterProgrammingGetConfigurationCommand>
    {
    public:

        std::string getCommandName() override;

    private:

        enum Command
        {
            Request = 0x91
        };

        Bytes getCommandHeader() override;
        Bytes getCommandData()   override;
    };

    class IM_EX_DEVDB RfnMeterProgrammingGetFileCommand : public RfnMeterProgrammingCommand,
        InvokerFor<RfnMeterProgrammingGetFileCommand>
    {
    public:

        RfnMeterProgrammingGetFileCommand(std::string guid, std::size_t blockSize);

        RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;

        std::string getCommandName() override;
    };
}
