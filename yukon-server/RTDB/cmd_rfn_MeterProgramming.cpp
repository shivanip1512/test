#include "precompiled.h"

#include "cmd_rfn_MeterProgramming.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"
#include "cmd_rfn_Individual.h"

namespace Cti::Devices::Commands {

    namespace
    {
        struct MeterStatuses
        {
            YukonError_t    returnCode;
            std::string     text;
        };

        // the meter status code

        static const std::map<unsigned char, MeterStatuses> meterStatusCodes
        {
            {   0,  { ClientErrors::None,           "Configured"    } },
            {   1,  { ClientErrors::None,           "Unchanged"     } },
            {   2,  { ClientErrors::None,           "Changed"       } },
            {   3,  { ClientErrors::MeterBricked,   "Bricked"       } }
        };

        // the detailed configuration status code

        static const std::map<unsigned char, MeterStatuses> detailedConfigurationStatusCodes
        {
            {   0,  { ClientErrors::None,                           "Success"                                       } },
            {   1,  { ClientErrors::ReasonUnknown,                  "Reason Unknown"                                } },
            {   2,  { ClientErrors::ServiceUnsupported,             "Service Not Supported"                         } },
            {   3,  { ClientErrors::InsufficientSecurityClearance,  "Insufficient Security Clearance"               } },
            {   4,  { ClientErrors::OperationNotPossible,           "Operation Not Possible"                        } },
            {   5,  { ClientErrors::InappropriateActionRequested,   "Inappropriate Action Requested"                } },
            {   6,  { ClientErrors::DeviceBusy,                     "Device Busy"                                   } },
            {   7,  { ClientErrors::DataNotReady,                   "Data Not Ready"                                } },
            {   8,  { ClientErrors::DataLocked,                     "Data Locked"                                   } },
            {   9,  { ClientErrors::RenegotiateRequest,             "Renegotiate Request"                           } },
            {  10,  { ClientErrors::InvalidServiceSequence,         "Invalid Service Sequence State"                } },
            {  32,  { ClientErrors::DownloadAborted,                "Download Aborted"                              } },
            {  33,  { ClientErrors::FileTooLarge,                   "File Too Large"                                } },
            {  34,  { ClientErrors::ConfigurationInProgress,        "Configuration In Progress"                     } },
            {  35,  { ClientErrors::UnableToGETFile,                "Unable To GET File"                            } },
            {  36,  { ClientErrors::InsufficientMeterVersion,       "Insufficient Meter Version"                    } },
            {  37,  { ClientErrors::FileExpired,                    "File Expired"                                  } },
            {  38,  { ClientErrors::FailedRequirements,             "Failed Requirements"                           } },
            {  39,  { ClientErrors::MalformedConfigFileRecord,      "Malformed Record In Configuration File"        } },
            {  40,  { ClientErrors::VerificationFailed,             "Verification Failed"                           } },
            {  41,  { ClientErrors::WriteKeyFailed,                 "Write Key Failed"                              } },
            {  42,  { ClientErrors::CatastrophicFailure,            "Catastrophic Failure, Full Reprogram Required" } }
        };

        static const std::map<unsigned char, std::string> validGuidPrefixes
        {
            {   'R',    "Yukon programmed"                      },
            {   'P',    "Optically programmed"                  },
            {   'N',    "Unknown program"                       },
            {   'U',    "Unprogrammed"                          },
            {   'X',    "Insufficient meter hardware/firmware"  }
        };
    }

    //  unused
    unsigned char RfnMeterProgrammingCommand::getOperation()   const { return {}; }
    unsigned char RfnMeterProgrammingCommand::getCommandCode() const { return {}; }

    RfnMeterProgrammingSetConfigurationCommand::RfnMeterProgrammingSetConfigurationCommand(std::string guid, std::size_t length)
        :   _guid(guid),
            _length(length)
    {
    }

    auto RfnMeterProgrammingSetConfigurationCommand::getCommandHeader() -> Bytes
    {
        return { static_cast<uint8_t>(Request) };
    }

    auto RfnMeterProgrammingSetConfigurationCommand::getCommandData() -> Bytes
    {
        std::string uri = "/meterPrograms/" + _guid;
        std::vector<TypeLengthValue> tlvs;

        auto tlv_size = TypeLengthValue::makeLongTlv(TlvType_ConfigurationSize);
        auto tlv_uri  = TypeLengthValue::makeLongTlv(TlvType_ConfigurationURI);

        setBits_bEndian(tlv_size.value, 0, 32, _length);
        tlv_uri.value.assign(uri.begin(), uri.end());

        tlvs.push_back(tlv_size);
        tlvs.push_back(tlv_uri);

        return getBytesFromTlvs(tlvs);
    }


    RfnCommandResult RfnMeterProgrammingSetConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload & response)
    {
        return { "No response", ClientErrors::E2eBadRequest };
    }


    std::string RfnMeterProgrammingSetConfigurationCommand::getCommandName()
    {
        return "Set Meter Programming Request";
    }

    bool RfnMeterProgrammingSetConfigurationCommand::isPost() const
    {
        return true;
    }

    bool RfnMeterProgrammingSetConfigurationCommand::isOneWay() const
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


    std::string RfnMeterProgrammingGetConfigurationCommand::getMeterConfigurationID() const
    {
        return _meterConfigurationID;
    }


    RfnCommandResult RfnMeterProgrammingGetConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload & response)
    {
        std::string description;

        // We need at least 6 bytes - the length of the payload is in the 6th byte

        validate( Condition( response.size() >= 6, ClientErrors::InvalidData )
                << "Invalid Response Length (" << response.size() << ") - minimum (6)" );

        // now we can validate our total length

        validate( Condition( response.size() == 6 + response[5], ClientErrors::InvalidData )
                << "Invalid Response Length (" << response.size() << ") - expected (" << ( 6 + response[5] ) << ")" );

        // the response code

        validate( Condition( response[0] == Command::Response, ClientErrors::InvalidData )
                << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ") - expected (" << CtiNumStr(Command::Response).xhex(2) << ")" );

        // the number of TLVs -- is always 1

        validate( Condition( response[3] == 1, ClientErrors::InvalidData )
                << "Invalid TLV Count (" << std::to_string(response[3]) << ") - expected (1)" );

        // the TLV type -- is always 3

        validate( Condition( response[4] == 3, ClientErrors::InvalidData )
                << "Invalid TLV Type (" << std::to_string(response[4]) << ") - expected (3)" );

        // the meter status code

        auto meterStatus = Cti::mapFind( meterStatusCodes, response[1] );

        // meter status code not found in map

        validate( Condition( !! meterStatus, ClientErrors::InvalidData )
                  << "Invalid Meter Status (" << response[1] << ")" );

        description += "Meter Status: " + meterStatus->text + " (" + std::to_string(response[1]) + ")";

        // the detailed configuration status code

        auto detailedStatus = Cti::mapFind( detailedConfigurationStatusCodes, response[2] );

        // detailed configuration status code not found in map

        validate( Condition( !! detailedStatus, ClientErrors::InvalidData )
                  << "Invalid Detailed Configuration Status (" << response[2] << ")" );

        description += "\nDetailed Configuration Status: " + detailedStatus->text + " (" + std::to_string(response[2]) + ")";

        YukonError_t returnCode =
            meterStatus->returnCode != ClientErrors::None 
                ? meterStatus->returnCode
                : detailedStatus->returnCode;

        // convert the rest of the data (if any) into the meter configuration ID

        if ( response.size() > 6 )
        {
            _meterConfigurationID = { response.begin() + 6, response.end() };

            using namespace std::string_literals;

            std::string guidPrefix =
                Cti::mapFindOrDefault( validGuidPrefixes, _meterConfigurationID[0],
                                       "Unmapped Prefix '"s + _meterConfigurationID[0] + "'"s );

            description += "\nSource: " + guidPrefix 
                        +  "\nMeter Configuration ID: " + _meterConfigurationID;
        }

        return { description, returnCode };
    }


    std::string RfnMeterProgrammingGetConfigurationCommand::getCommandName()
    {
        return "Get Meter Programming Request";
    }
}
