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
    };

    class IM_EX_DEVDB RfnMeterProgrammingSetConfigurationCommand : public RfnMeterProgrammingCommand,
        InvokerFor<RfnMeterProgrammingSetConfigurationCommand>
    {
    public:

        RfnMeterProgrammingSetConfigurationCommand(std::string guid, std::size_t size);

        RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;

        std::string getCommandName() override;

    private:

        enum Command
        {
            Request = 0x90
        };

        Bytes getCommandHeader() override;
        Bytes getCommandData()   override;
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

        //RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload & response) override;

        std::string getCommandName() override;

        std::string getStatus();

        std::string getFile();

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

    private:

        Bytes getCommandHeader() override;
        Bytes getCommandData()   override;
    protected:

        enum TlvType
        {
            TlvType_ConfigurationSize = 0x01,
            TlvType_ConfigurationURI = 0x02
        };
    };
}