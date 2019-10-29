#include "precompiled.h"

#include "cmd_rfn_MeterProgramming.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"
#include "cmd_rfn_Individual.h"

namespace Cti::Devices::Commands {

    //  unused
    unsigned char RfnMeterProgrammingCommand::getOperation()   const { return {}; }
    unsigned char RfnMeterProgrammingCommand::getCommandCode() const { return {}; }

    RfnMeterProgrammingSetConfigurationCommand::RfnMeterProgrammingSetConfigurationCommand(std::string guid, std::size_t length)
    {
        //  
    }

    auto RfnMeterProgrammingSetConfigurationCommand::getCommandHeader() -> Bytes
    {
        return { static_cast<uint8_t>(Request) };
    }

    auto RfnMeterProgrammingSetConfigurationCommand::getCommandData() -> Bytes
    {
        long size = 11235;
        std::string uri = "/meterPrograms/7d444840-9dc0-11d1-b245-5ffdce74fad2";
        std::vector<TypeLengthValue> tlvs;

        TypeLengthValue tlv_size = TypeLengthValue(TlvType_ConfigurationSize);
        TypeLengthValue tlv_uri = TypeLengthValue(TlvType_ConfigurationURI);

        setBits_bEndian(tlv_size.value, 0, 32, size);
        tlv_uri.value.assign(uri.begin(), uri.end());

        tlvs.push_back(tlv_size);
        tlvs.push_back(tlv_uri);

        return getBytesFromTlvs(tlvs);
    }


    RfnCommandResult RfnMeterProgrammingSetConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload & response)
    {
        return { "No respondo", ClientErrors::E2eBadRequest };
    }


    std::string RfnMeterProgrammingSetConfigurationCommand::getCommandName()
    {
        return "Set Meter Programming Request";
    }

    bool RfnMeterProgrammingSetConfigurationCommand::isPost() const
    {
        return true;
    }


    auto RfnMeterProgrammingGetConfigurationCommand::getCommandHeader() -> Bytes
    {
        return { static_cast<uint8_t>(Command::Request) };
    }


    auto RfnMeterProgrammingGetConfigurationCommand::getCommandData() -> Bytes
    {
        return {};
    }


    //RfnCommandResult RfnMeterProgrammingGetConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload & response)
    //{
    //    // We need at least 2 bytes

    //    validate(Condition(response.size() >= 2, ClientErrors::DataMissing)
    //        << "Invalid Response length (" << response.size() << ")");

    //    validate(Condition(response[0] == Command::Response, ClientErrors::InvalidData)
    //        << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ")");

    //    _interval = std::chrono::minutes(response[1]);

    //    validate(Condition(_interval > 0min, ClientErrors::BadParameter)
    //        << "Invalid demand interval " << _interval);

    //    validate(Condition(_interval < 61min, ClientErrors::BadParameter)
    //        << "Invalid demand interval " << _interval);

    //    return StreamBuffer() << "Demand interval: " << _interval;
    //}


    std::string RfnMeterProgrammingGetConfigurationCommand::getCommandName()
    {
        return "Get Meter Programming Request";
    }
}