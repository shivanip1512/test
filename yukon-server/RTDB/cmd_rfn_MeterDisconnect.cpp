#include "precompiled.h"

#include "cmd_rfn_MeterDisconnect.h"
#include "cmd_rfn_helper.h"

#include "RfnMeterDisconnectMsg.h"

#include "std_helper.h"

#include <gsl/span>

namespace Cti::Devices::Commands {

RfnMeterDisconnectCommand::RfnMeterDisconnectCommand(CommandType action, long userMessageId)
    :   _action(action)
    ,   _userMessageId(userMessageId)
{
}

auto RfnMeterDisconnectCommand::getApplicationServiceId() const -> ASID
{
    return ASID::ChannelManager;
}

auto RfnMeterDisconnectCommand::getPriorityClass() const -> Messaging::Rfn::PriorityClass
{
    return Messaging::Rfn::PriorityClass::MeterDisconnect;
}

std::string RfnMeterDisconnectCommand::getCommandName() const
{
    static const std::string commandName = "RFN Meter Disconnect - ";

    static const std::map<CommandType, std::string> actionNames {
        { CommandType::ArmForResume,      "Arm for resume"     },
        { CommandType::Query,             "Query"              },
        { CommandType::ResumeImmediately, "Resume immediately" },
        { CommandType::TerminateService,  "Terminate service"  }};

    if( const auto actionName = mapFind(actionNames, _action) )
    {
        return commandName + *actionName;
    }

    return commandName + "Unknown action " + std::to_string(static_cast<int>(_action));
}

unsigned char RfnMeterDisconnectCommand::getCommandCode() const
{
    return Command::Request;
}

auto RfnMeterDisconnectCommand::getCommandHeader() -> Bytes
{
    return { getCommandCode() };
}

auto RfnMeterDisconnectCommand::getCommandData() -> Bytes
{
    return { as_underlying(_action) };
}

unsigned char RfnMeterDisconnectCommand::getOperation() const
{
    return {};  //  unused
}

long RfnMeterDisconnectCommand::getUserMessageId() const
{
    return _userMessageId;
}

auto RfnMeterDisconnectCommand::getResponseMessage() const -> std::optional<ReplyMsg>
{
    if( _response )
    {
        return *_response;
    }
    return std::nullopt;
}

using namespace Messaging::Rfn;
using ReplyType = RfnMeterDisconnectConfirmationReplyType;
using State = RfnMeterDisconnectState;

[[nodiscard]] auto makeFailureResponse(const ReplyType replyType)
{
    RfnMeterDisconnectConfirmationReplyMsg response;

    response.replyType = replyType;
    response.state = State::UNKNOWN;

    return response;
}


RfnCommandResult RfnMeterDisconnectCommand::error(const CtiTime now, const YukonError_t errorCode)
{
    _response = makeFailureResponse(ReplyType::FAILURE);

    return RfnIndividualCommand::error(now, errorCode);
}


[[nodiscard]] auto makeSuccessResponse(const std::uint8_t status)
{
    static const std::map<std::uint8_t, State> states {
        { 0x01, State::DISCONNECTED },
        { 0x02, State::ARMED },
        { 0x03, State::CONNECTED },
        { 0x04, State::DISCONNECTED_DEMAND_THRESHOLD_ACTIVE },
        { 0x05, State::CONNECTED_DEMAND_THRESHOLD_ACTIVE },
        { 0x06, State::DISCONNECTED_CYCLING_ACTIVE },
        { 0x07, State::CONNECTED_CYCLING_ACTIVE }};

    if( const auto state = mapFind(states, status) )
    {
        RfnMeterDisconnectConfirmationReplyMsg response;

        response.replyType = ReplyType::SUCCESS;
        response.state = *state;

        return response;
    }

    return makeFailureResponse(ReplyType::FAILED_UNEXPECTED_STATUS);
}


RfnCommandResult RfnMeterDisconnectCommand::decodeCommand(const CtiTime now, const RfnResponsePayload& response)
try
{
    validate(Condition(response.size() >= 3, ClientErrors::DataMissing)
        << "RFN meter disconnect response does not include response command, command type and status");

    const auto responseType = response[0];
    
    validate(Condition(responseType == static_cast<std::uint8_t>(Response), ClientErrors::InvalidData)
        << "RFN meter disconnect response type does not match command: " << FormattedList::of(
            "Response type", static_cast<int>(responseType),
            "Expected", Response));

    const auto commandType = response[1];

    validate(Condition(commandType == as_underlying(_action), ClientErrors::InvalidData)
        << "RFN meter disconnect command type does not match request: " << FormattedList::of(
            "Request type", static_cast<int>(_action),
            "Response type", commandType));

    const auto commandStatus = response[2];

    std::string details;

    switch( commandStatus )
    {
        case 0x00:
        {
            gsl::span success_payload { response.data() + 3, response.size() - 3 };
                
            validate(Condition(success_payload.size() >= 1, ClientErrors::DataMissing)
                << "RFN meter disconnect response does not include status");

            _response = makeSuccessResponse(success_payload[0]);

            break;
        }
        case 0x01:
        {
            gsl::span failure_payload { response.data() + 3, response.size() - 3 };

            validate(Condition(failure_payload.size() >= 2, ClientErrors::DataMissing)
                << "RFN meter disconnect response does not include failure TLV");
            
            const auto type   = failure_payload[0];
            const auto length = failure_payload[1];

            switch( type )
            {
                case 0x01:
                {
                    _response = makeFailureResponse(ReplyType::FAILURE);

                    static std::map<std::uint8_t, std::string> protocolFailureDescriptions {
                        { 0x00, "Request Accepted" },
                        { 0x01, "Request Rejected (reason unknown)" },
                        { 0x02, "Service Not Supported" },
                        { 0x03, "Insufficient Security Clearance" },
                        { 0x04, "Operation Not Possible (covers errors such as Invalid Length and Invalid Offset)" },
                        { 0x05, "Inappropriate Action Requested (covers errors such as Writing to a Read-only table and Invalid Table ID)" },
                        { 0x06, "Device Busy (request not acted upon because meter was busy)" },
                        { 0x07, "Data Not Ready" },
                        { 0x08, "Data Locked (requested data cannot be accessed)" },
                        { 0x09, "Renegotiate Request (indicates that the meter has to return to the ID or base state and re-negotiate communication parameters)" },
                        { 0x0A, "Invalid Service Sequence State indicates that node has not logged into the meter properly before making a request."
                                "  This can happen when power has been lost briefly and the meter’s processor has reset but the node has not reset." }};

                    if( length == 1 )
                    {
                        details = "Protocol failure " + std::to_string(failure_payload[2]) + " - " + mapFindOrDefault(protocolFailureDescriptions, failure_payload[2], "<no description>");
                    }
                    else
                    {
                        details = "Unsupported Protocol Failure TLV length " + std::to_string(length);
                    }

                    break;
                }
                case 0x02:
                {
                    validate(Condition(length == 1, ClientErrors::InvalidData)
                        << "RFN meter disconnect response does not include failure TLV");

                    validate(Condition(failure_payload.size() >= 3, ClientErrors::DataMissing)
                        << "RFN meter disconnect response does not include failure TLV value byte");

                    static std::map<std::uint8_t, ReplyType> meterFailures{
                        { 0x01, ReplyType::FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD },
                        { 0x0a, ReplyType::FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT },
                        { 0x0b, ReplyType::FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT },
                    };

                    const auto failureType = mapFindOrDefault(meterFailures, failure_payload[2], ReplyType::FAILURE);

                    _response = makeFailureResponse(failureType);

                    static std::map<std::uint8_t, std::string> meterFailureDescriptions {
                        { 0x00, "Command accepted." },
                        { 0x01, "Command rejected because a load side voltage higher than the configurable threshold prevented the switch from closing." },
                        { 0x02, "Arm command rejected because the switch can only be armed when the switch is open." },
                        { 0x03, "Command rejected because the meter is in test mode." },
                        { 0x04, "Command rejected because the service disconnect close button was pressed but the meter was not armed." },
                        { 0x05, "Command rejected because meter is not capable of service disconnect." },
                        { 0x06, "Command rejected because service disconnect is not enabled." },
                        { 0x07, "Command rejected because service disconnect is currently charging." },
                        { 0x08, "Command rejected because service disconnect was already in operation." },
                        { 0x09, "Command failed because discharge of capacitor was not detected." },
                        { 0x0A, "Error, load side voltage detected after completion of disconnect." },
                        { 0x0B, "Error, no load side voltage detected after completion of a connect." }};

                    if( length == 1 )
                    {
                        details += "Meter failure " + std::to_string(failure_payload[2]) + " - " + mapFindOrDefault(meterFailureDescriptions, failure_payload[2], "<no description>");
                    }
                    else
                    {
                        details += "Unsupported Meter Failure TLV length " + std::to_string(length);
                    }

                    break;
                }
                default:
                {
                    details = "Unknown failure TLV " + std::to_string(type);

                    CTILOG_WARN(dout, details);

                    _response = makeFailureResponse(ReplyType::FAILURE);

                    break;
                }
            }

            break;
        }
        case 0x02:
        {
            _response = makeFailureResponse(ReplyType::NOT_SUPPORTED);

            break;
        }
        default:
        {
            details = "Unknown command status " + std::to_string(commandStatus);

            CTILOG_WARN(dout, details);

            _response = makeFailureResponse(ReplyType::FAILURE);

            break;
        }
    }

    FormattedList results;
    
    static const std::map<ReplyType, std::string> replyTypeDescriptions {
        { ReplyType::SUCCESS,
                    "SUCCESS" },
        { ReplyType::FAILURE,
                    "FAILURE" },
        { ReplyType::FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD,
                    "FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD" },
        { ReplyType::FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT,
                    "FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT" },
        { ReplyType::FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT,
                    "FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT" },
        { ReplyType::FAILED_UNEXPECTED_STATUS,
                    "FAILED_UNEXPECTED_STATUS" },
        { ReplyType::NOT_SUPPORTED,
                    "NOT_SUPPORTED" },
        { ReplyType::NETWORK_TIMEOUT,
                    "NETWORK_TIMEOUT" },
        { ReplyType::TIMEOUT,
                    "TIMEOUT" } };

    static const std::map<State, std::string> stateDescriptions {
        { State::UNKNOWN,
                "UNKNOWN" },
        { State::CONNECTED,
                "CONNECTED" },
        { State::DISCONNECTED,
                "DISCONNECTED" },
        { State::ARMED,
                "ARMED" },
        { State::DISCONNECTED_DEMAND_THRESHOLD_ACTIVE,
                "DISCONNECTED_DEMAND_THRESHOLD_ACTIVE" },
        { State::CONNECTED_DEMAND_THRESHOLD_ACTIVE,
                "CONNECTED_DEMAND_THRESHOLD_ACTIVE" },
        { State::DISCONNECTED_CYCLING_ACTIVE,
                "DISCONNECTED_CYCLING_ACTIVE" },
        { State::CONNECTED_CYCLING_ACTIVE,
                "CONNECTED_CYCLING_ACTIVE" } };

    results.add("User message ID") << _userMessageId;
    results.add("Reply type") << std::to_string(static_cast<int>(_response->replyType)) + " - " + mapFindOrDefault(replyTypeDescriptions, _response->replyType, "<no description>");
    results.add("Status") << std::to_string(static_cast<int>(_response->state)) + " - "  + mapFindOrDefault(stateDescriptions, _response->state, "<no description>");

    if( ! details.empty() )
    {
        results.add("Details") << details;
    }

    return results.toString().substr(1);  //  Lop off the leading newline
}
catch( const YukonErrorException& ex )
{
    _response = makeFailureResponse(ReplyType::FAILURE);
    
    throw;
}

}
