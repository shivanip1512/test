#include "precompiled.h"

#include "cmd_rfn_Metrology.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"
#include "cmd_rfn_Individual.h"

namespace Cti::Devices::Commands
{

namespace
{

struct Statuses
{
    YukonError_t    errorCode;
    std::string     description;
};

static const std::map<unsigned char, Statuses>  statusCodesDecoder
{
    { 0x00, { ClientErrors::None,       "Successful"                    } },
    { 0x01, { ClientErrors::Abnormal,   "No Change in Key Value"        } },
    { 0x05, { ClientErrors::Abnormal,   "Illegal Request"               } },
    { 0x06, { ClientErrors::Abnormal,   "Aborted"                       } },
    { 0x08, { ClientErrors::Abnormal,   "Configuration not present"     } }
};

struct States
{
    RfnMetrologyCommand::State state;
    std::string description;
};

static const std::map<unsigned char, States>  stateDecoder
{
    { 0x00, { RfnMetrologyCommand::Enable,  "Enable"    } },
    { 0x01, { RfnMetrologyCommand::Disable, "Disable"   } }
};

static const std::set<DeviceTypes>  supportedDeviceTypes
{
    // Supported by the L+G Focus 520AX/530AX
    TYPE_RFN520FAX,
    TYPE_RFN520FAXD,
    TYPE_RFN530FAX,
    TYPE_RFN530S4EAX,
    TYPE_RFN530S4EAXR,
};

}   // -- anon

/////

bool RfnMetrologyCommand::isSupportedByDeviceType( DeviceTypes type )
{
    return supportedDeviceTypes.count( type );
}

RfnMetrologyCommand::RfnMetrologyCommand( Operation op )
    :   _operation( op )
{
    // empty
}

unsigned char RfnMetrologyCommand::getCommandCode() const
{
    return Request;
}

unsigned char RfnMetrologyCommand::getOperation() const
{
    return _operation;
}

DeviceCommand::Bytes RfnMetrologyCommand::getCommandHeader()
{
    return { getCommandCode() };
}

RfnCommandResult RfnMetrologyCommand::predecodeCommand(const CtiTime now, const RfnResponsePayload & response)
{
    // We should have a 4 byte response

    validate( Condition( response.size() == 4, ClientErrors::InvalidData )
            << "Invalid Response Length (" << response.size() << ") - expecting 4 bytes" );

    // the response code

    validate( Condition( response[0] == Response, ClientErrors::InvalidData )
            << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ") - expected (" << CtiNumStr(Response).xhex(2) << ")" );

    // the operation code

    validate( Condition( response[1] == getOperation(), ClientErrors::InvalidData )
            << "Invalid Response Operation Code (" << CtiNumStr(response[1]).xhex(2) << ") - expected (" << CtiNumStr(getOperation()).xhex(2) << ")" );

    // the status code

    auto status = Cti::mapFind( statusCodesDecoder, response[2] );

    // status code not found in map

    validate( Condition( !! status, ClientErrors::InvalidData )
              << "Invalid Response Status Code (" << response[2] << ")" );

    return { "Status: " + status->description + " (" + std::to_string(response[2]) + ")", status->errorCode };
}

/////

RfnMetrologySetConfigurationCommand::RfnMetrologySetConfigurationCommand( State state )
    :   RfnMetrologyCommand( Operation_SetConfiguration ),
        _disable( state == Disable )
{
    // empty
}

std::string RfnMetrologySetConfigurationCommand::getCommandName() const
{
    return _disable 
            ? "METLIB Disable Request"
            : "METLIB Enable Request";
}

DeviceCommand::Bytes RfnMetrologySetConfigurationCommand::getCommandData()
{
    return { getOperation(), _disable };
}

RfnCommandResult RfnMetrologySetConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload & response)
{
    RfnCommandResult result = predecodeCommand( now, response );

    auto value = Cti::mapFind( stateDecoder, response[3] );

    validate( Condition( !! value, ClientErrors::InvalidData )
              << "Invalid Response Value (" << response[3] << ")" );

    result.description += "\nValue: " + value->description + " (" + std::to_string(response[3]) + ")";

    // On a successful result - if the command request type doesn't match the responses value byte,
    //  then there is some sort of error.

    validate( Condition( _disable == response[3], ClientErrors::InvalidData )
              << "Invalid Response Value (" << response[3] << ") - request type mismatch" );

    return result;
}

/////

RfnMetrologyGetConfigurationCommand::RfnMetrologyGetConfigurationCommand()
    :   RfnMetrologyCommand( Operation_GetConfiguration )
{
    // empty
}

std::string RfnMetrologyGetConfigurationCommand::getCommandName() const
{
    return "METLIB Get Enable/Disable State Request";
}

DeviceCommand::Bytes RfnMetrologyGetConfigurationCommand::getCommandData()
{
    return { getOperation() };
}

RfnCommandResult RfnMetrologyGetConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload & response)
{
    RfnCommandResult result = predecodeCommand( now, response );

    auto value = Cti::mapFind( stateDecoder, response[3] );

    validate( Condition( !! value, ClientErrors::InvalidData )
              << "Invalid Response Value (" << response[3] << ")" );

    result.description += "\nValue: " + value->description + " (" + std::to_string(response[3]) + ")";

    _metrologyState = value->state;

    return result;
}

std::optional<RfnMetrologyCommand::State> RfnMetrologyGetConfigurationCommand::getMetrologyState() const
{
    return _metrologyState;
}

}   // -- Cti::Devices::Commands

