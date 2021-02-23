#include <boost/test/unit_test.hpp>

#include "cmd_rfn_MeterDisconnect.h"

#include "ctidate.h"

#include "std_helper.h"
#include "boost_test_helpers.h"

using Cti::Devices::Commands::RfnMeterDisconnectCommand;
using CommandType = RfnMeterDisconnectCommand::CommandType;
using rt = Cti::Messaging::Rfn::RfnMeterDisconnectConfirmationReplyType;
using s = Cti::Messaging::Rfn::RfnMeterDisconnectState;

namespace std {
    ostream& operator<<(ostream& o, const vector<unsigned char>& bytes);
}
namespace Cti {
    std::ostream& operator<<(std::ostream& os, const RfnIdentifier rfnId);
}
namespace Cti::Messaging::Rfn {
    std::ostream& operator<<(std::ostream& os, const RfnMeterDisconnectConfirmationReplyType type) {
        return os << "[RfnMeterDisconnectConfirmationReplyType " << static_cast<int>(type) << "]";
    }
    std::ostream& operator<<(std::ostream& os, const RfnMeterDisconnectState state) {
        return os << "[RfnMeterDisconnectState " << static_cast<int>(state) << "]";
    }
}

BOOST_AUTO_TEST_SUITE(test_cmd_rfn_MeterDisconnect)

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

struct Exchange
{
    const std::vector<unsigned char> outbound;
    const std::vector<unsigned char> response;
};

BOOST_AUTO_TEST_CASE(test_mismatched_response_command)
{
    RfnMeterDisconnectCommand command(CommandType::Query, 11235);

    Exchange x {
        { 0x80, 0x04 },
        {
            0x99, //  Invalid response command
            0x04, //  Echoed action (Query)
            0x00, //  Command status (OK)
        } };

    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    try 
    {
        const auto result = command.decodeCommand(execute_time, x.response);
        BOOST_FAIL("Did not throw");
    }
    catch( const Cti::YukonErrorException& ex )
    {
        BOOST_CHECK_EQUAL(ex.error_description,
            "RFN meter disconnect response type does not match command: "
            "\nResponse type : 153"
            "\nExpected      : 129");
        BOOST_CHECK_EQUAL(ex.error_code, 264);
    }

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::FAILURE);
    BOOST_CHECK_EQUAL(responseMsg->state, s::UNKNOWN);
}

BOOST_AUTO_TEST_CASE(test_mismatched_echoed_action)
{
    RfnMeterDisconnectCommand command(CommandType::Query, 11235);

    Exchange x {
        { 0x80, 0x04 },
        {
            0x81, //  Response command
            0x01, //  Mismatched action
            0x00, //  Command status (OK)
        } };

    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    try
    {
        const auto result = command.decodeCommand(execute_time, x.response);
        BOOST_FAIL("Did not throw");
    }
    catch( const Cti::YukonErrorException& ex )
    {
        BOOST_CHECK_EQUAL(ex.error_description,
            "RFN meter disconnect command type does not match request: "
            "\nRequest type  : 4"
            "\nResponse type : 1");
        BOOST_CHECK_EQUAL(ex.error_code, 264);
    }

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::FAILURE);
    BOOST_CHECK_EQUAL(responseMsg->state, s::UNKNOWN);
}

BOOST_AUTO_TEST_CASE(test_unknown_command_status)
{
    RfnMeterDisconnectCommand command(CommandType::Query, 11235);

    Exchange x {
        { 0x80, 0x04 },
        {
            0x81, //  Response command
            0x04, //  Echoed action (Query)
            0x99, //  Invalid command status
        } };

    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    const auto result = command.decodeCommand(execute_time, x.response);
    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 1 - FAILURE"
        "\nStatus          : 0 - UNKNOWN"
        "\nDetails         : Unknown command status 153");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::FAILURE);
    BOOST_CHECK_EQUAL(responseMsg->state, s::UNKNOWN);
}

BOOST_AUTO_TEST_CASE(test_unsupported)
{
    RfnMeterDisconnectCommand command(CommandType::Query, 11235);

    Exchange x {
        { 0x80, 0x04 },
        {
            0x81, //  Response command
            0x04, //  Echoed action (Query)
            0x02, //  Unsupported
        } };

    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    const auto result = command.decodeCommand(execute_time, x.response);
    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 6 - NOT_SUPPORTED"
        "\nStatus          : 0 - UNKNOWN");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::NOT_SUPPORTED);
    BOOST_CHECK_EQUAL(responseMsg->state, s::UNKNOWN);
}

BOOST_AUTO_TEST_CASE(test_protocol_fail)
{
    RfnMeterDisconnectCommand command(CommandType::Query, 11235);

    Exchange x {
        { 0x80, 0x04 },
        {
            0x81, //  Response command
            0x04, //  Echoed action (Query)
            0x01, //  Failure
            0x01, //  Type - Protocol failure
            0x01, //  Length - 1
            0x06, //  Value - Device busy
        } };

    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    const auto result = command.decodeCommand(execute_time, x.response);
    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 1 - FAILURE"
        "\nStatus          : 0 - UNKNOWN"
        "\nDetails         : Protocol failure 6 - Device Busy (request not acted upon because meter was busy)");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::FAILURE);
    BOOST_CHECK_EQUAL(responseMsg->state, s::UNKNOWN);
}

BOOST_AUTO_TEST_CASE(test_meter_fail)
{
    RfnMeterDisconnectCommand command(CommandType::Query, 11235);

    Exchange x {
        { 0x80, 0x04 },
        {
            0x81, //  Response command
            0x04, //  Echoed action (Query)
            0x01, //  Failure
            0x02, //  Type - Meter failure
            0x01, //  Length - 1
            0x0a, //  Value - Load side voltage after a disconnect
        } };

    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    const auto result = command.decodeCommand(execute_time, x.response);
    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 3 - FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT"
        "\nStatus          : 0 - UNKNOWN"
        "\nDetails         : Meter failure 10 - Error, load side voltage detected after completion of disconnect.");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT);
    BOOST_CHECK_EQUAL(responseMsg->state, s::UNKNOWN);
}

BOOST_AUTO_TEST_CASE(test_tlv_unsupported_length)
{
    RfnMeterDisconnectCommand command(CommandType::Query, 11235);

    Exchange x{
        { 0x80, 0x04 },
        {
            0x81, //  Response command
            0x04, //  Echoed action (Query)
            0x01, //  Failure
            0x02, //  Type - Meter failure
            0x04, //  Length - 4
            0x0a, //  Value - don't care
            0x0a, //  Value - 
            0x0a, //  Value - 
            0x0a, //  Value - 
        } };

    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    try
    {
        const auto result = command.decodeCommand(execute_time, x.response);
        BOOST_FAIL("Did not throw");
    }
    catch( const Cti::YukonErrorException& ex )
    {
        BOOST_CHECK_EQUAL(ex.error_description,
            "RFN meter disconnect response does not include failure TLV");
        BOOST_CHECK_EQUAL(ex.error_code, 264);
    }

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::FAILURE);
    BOOST_CHECK_EQUAL(responseMsg->state, s::UNKNOWN);
}

BOOST_AUTO_TEST_CASE(test_missing_tlv)
{
    RfnMeterDisconnectCommand command(CommandType::Query, 11235);

    Exchange x {
        { 0x80, 0x04 },
        {
            0x81, //  Response command
            0x04, //  Echoed action (Query)
            0x01, //  Failure
        } };

    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    try
    {
        const auto result = command.decodeCommand(execute_time, x.response);
        BOOST_FAIL("Did not throw");
    }
    catch( const Cti::YukonErrorException& ex )
    {
        BOOST_CHECK_EQUAL(ex.error_description,
            "RFN meter disconnect response does not include failure TLV");
        BOOST_CHECK_EQUAL(ex.error_code, 283);
    }

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::FAILURE);
    BOOST_CHECK_EQUAL(responseMsg->state, s::UNKNOWN);
}

BOOST_AUTO_TEST_CASE(test_unknown_tlv)
{
    RfnMeterDisconnectCommand command(CommandType::Query, 11235);

    Exchange x {
        { 0x80, 0x04 },
        {
            0x81, //  Response command
            0x04, //  Echoed action (Query)
            0x01, //  Failure
            0x03, //  Type - invalid
            0x01, //  Length - 1
            0xff, //  Value - don't care
        } };
    
    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    const auto result = command.decodeCommand(execute_time, x.response);
    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 1 - FAILURE"
        "\nStatus          : 0 - UNKNOWN"
        "\nDetails         : Unknown failure TLV 3");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::FAILURE);
    BOOST_CHECK_EQUAL(responseMsg->state, s::UNKNOWN);
}

BOOST_AUTO_TEST_CASE(test_arm)
{
    RfnMeterDisconnectCommand command(CommandType::ArmForResume, 11235);

    Exchange x {
        { 0x80, 0x02 },
        {
            0x81, //  Response command
            0x02, //  Echoed action (Arm)
            0x00, //  Command status (Success)
            0x02, //  Disconnect state (Arm)
        } };
    
    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    const auto result = command.decodeCommand(execute_time, x.response);
    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0 - SUCCESS"
        "\nStatus          : 3 - ARMED");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::SUCCESS);
    BOOST_CHECK_EQUAL(responseMsg->state, s::ARMED);
}

BOOST_AUTO_TEST_CASE(test_query)
{
    RfnMeterDisconnectCommand command(CommandType::Query, 11235);

    Exchange x {
        { 0x80, 0x04 },
        {
            0x81, //  Response command
            0x04, //  Echoed action (Query)
            0x00, //  Command status (Success)
            0x01, //  Disconnect state (Terminate)
        } };

    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    const auto result = command.decodeCommand(execute_time, x.response);
    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0 - SUCCESS"
        "\nStatus          : 2 - DISCONNECTED");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::SUCCESS);
    BOOST_CHECK_EQUAL(responseMsg->state, s::DISCONNECTED);
}

BOOST_AUTO_TEST_CASE(test_resume)
{
    RfnMeterDisconnectCommand command(CommandType::ResumeImmediately, 11235);

    Exchange x {
        { 0x80, 0x03 },
        {
            0x81, //  Response command
            0x03, //  Echoed action (Resume)
            0x00, //  Command status (Success)
            0x03, //  Disconnect state (Resume)
        } };

    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    const auto result = command.decodeCommand(execute_time, x.response);
    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0 - SUCCESS"
        "\nStatus          : 1 - CONNECTED");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::SUCCESS);
    BOOST_CHECK_EQUAL(responseMsg->state, s::CONNECTED);
}

BOOST_AUTO_TEST_CASE(test_terminate)
{
    RfnMeterDisconnectCommand command(CommandType::TerminateService, 11235);

    Exchange x {
        { 0x80, 0x01 },
        {
            0x81, //  Response command
            0x01, //  Echoed action (Terminate)
            0x00, //  Command status (Success)
            0x01, //  Disconnect state (Terminate)
        } };

    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    const auto result = command.decodeCommand(execute_time, x.response);
    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0 - SUCCESS"
        "\nStatus          : 2 - DISCONNECTED");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::SUCCESS);
    BOOST_CHECK_EQUAL(responseMsg->state, s::DISCONNECTED);
}

BOOST_AUTO_TEST_CASE(test_terminate_failure)
{
    RfnMeterDisconnectCommand command(CommandType::TerminateService, 11235);

    Exchange x {
        { 0x80, 0x01 },
        {
            0x81, //  Response command
            0x01, //  Echoed action (Terminate)
            0x01, //  Command status (Failure)
            0x02, //  TLV type 2 (Meter failure)
            0x01, //  TLV length 1
            0x06  //  Reason - 6
        } };

    BOOST_CHECK_EQUAL(x.outbound, command.executeCommand(execute_time));

    const auto result = command.decodeCommand(execute_time, x.response);
    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 1 - FAILURE"
        "\nStatus          : 0 - UNKNOWN"
        "\nDetails         : Meter failure 6 - Command rejected because service disconnect is not enabled.");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();
    BOOST_REQUIRE(responseMsg);
    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::FAILURE);
    BOOST_CHECK_EQUAL(responseMsg->state, s::UNKNOWN);
}

BOOST_AUTO_TEST_SUITE_END()