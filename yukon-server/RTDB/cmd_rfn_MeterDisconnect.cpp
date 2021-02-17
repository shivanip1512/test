#include "precompiled.h"

#include "cmd_rfn_MeterDisconnect.h"
#include "cmd_rfn_helper.h"

#include "RfnMeterDisconnectMsg.h"

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
    return { static_cast<uint8_t>(_action) };
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

[[nodiscard]] auto makeFailureResponse(const RfnMeterDisconnectConfirmationReplyType replyType)
{
    RfnMeterDisconnectConfirmationReplyMsg response;

    response.replyType = replyType;
    response.state = RfnMeterDisconnectState::UNKNOWN;

    return response;
}


RfnCommandResult RfnMeterDisconnectCommand::error(const CtiTime now, const YukonError_t errorCode)
{
    _response = makeFailureResponse(RfnMeterDisconnectConfirmationReplyType::FAILURE);

    return RfnIndividualCommand::error(now, errorCode);
}


[[nodiscard]] auto makeSuccessResponse(const std::uint8_t status)
{
    static const std::map<std::uint8_t, RfnMeterDisconnectState> states {
        { 0x01, RfnMeterDisconnectState::DISCONNECTED },
        { 0x02, RfnMeterDisconnectState::ARMED },
        { 0x03, RfnMeterDisconnectState::CONNECTED },
        { 0x04, RfnMeterDisconnectState::DISCONNECTED_DEMAND_THRESHOLD_ACTIVE },
        { 0x05, RfnMeterDisconnectState::CONNECTED_DEMAND_THRESHOLD_ACTIVE },
        { 0x06, RfnMeterDisconnectState::DISCONNECTED_CYCLING_ACTIVE },
        { 0x07, RfnMeterDisconnectState::CONNECTED_CYCLING_ACTIVE }};

    if( const auto state = mapFind(states, status) )
    {
        RfnMeterDisconnectConfirmationReplyMsg response;

        response.replyType = RfnMeterDisconnectConfirmationReplyType::SUCCESS;
        response.state = *state;

        return response;
    }

    return makeFailureResponse(RfnMeterDisconnectConfirmationReplyType::FAILED_UNEXPECTED_STATUS);
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

    validate(Condition(commandType == static_cast<std::uint8_t>(_action), ClientErrors::InvalidData)
        << "RFN meter disconnect command type does not match request: " << FormattedList::of(
            "Request type", static_cast<int>(_action),
            "Response type", commandType));

    const auto commandStatus = response[2];

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
                    _response = makeFailureResponse(RfnMeterDisconnectConfirmationReplyType::FAILURE);

                    break;
                }
                case 0x02:
                {
                    validate(Condition(length == 1, ClientErrors::InvalidData)
                        << "RFN meter disconnect response does not include failure TLV");

                    validate(Condition(failure_payload.size() >= 3, ClientErrors::DataMissing)
                        << "RFN meter disconnect response does not include failure TLV value byte");

                    static std::map<std::uint8_t, RfnMeterDisconnectConfirmationReplyType> meterFailures{
                        { 0x01, RfnMeterDisconnectConfirmationReplyType::FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD },
                        { 0x0a, RfnMeterDisconnectConfirmationReplyType::FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT },
                        { 0x0a, RfnMeterDisconnectConfirmationReplyType::FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT },
                        { 0x0b, RfnMeterDisconnectConfirmationReplyType::FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT },
                    };

                    const auto failureType = mapFindOrDefault(meterFailures, failure_payload[2], RfnMeterDisconnectConfirmationReplyType::FAILURE);

                    _response = makeFailureResponse(failureType);

                    break;
                }
                default:
                {
                    CTILOG_WARN(dout, "Unknown failure TLV " << static_cast<int>(type));

                    _response = makeFailureResponse(RfnMeterDisconnectConfirmationReplyType::FAILURE);

                    break;
                }
            }

            break;
        }
        case 0x02:
        {
            _response = makeFailureResponse(RfnMeterDisconnectConfirmationReplyType::NOT_SUPPORTED);

            break;
        }
        default:
        {
            CTILOG_WARN(dout, "Unknown command status " << static_cast<int>(commandStatus));

            _response = makeFailureResponse(RfnMeterDisconnectConfirmationReplyType::FAILURE);

            break;
        }
    }

    return "Results:" + FormattedList::of(
        "User message ID", _userMessageId,
        "Reply type", static_cast<int>(_response->replyType),
        "Status", static_cast<int>(_response->state));
}
catch( const YukonErrorException& ex )
{
    _response = makeFailureResponse(RfnMeterDisconnectConfirmationReplyType::FAILURE);
    
    throw;
}

}
