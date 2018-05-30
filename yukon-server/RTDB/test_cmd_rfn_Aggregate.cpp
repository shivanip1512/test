#include <boost/test/unit_test.hpp>

#include "cmd_rfn_Aggregate.h"
#include "cmd_rfn_CentronLcdConfiguration.h"

#include "boost_test_helpers.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandPtr;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnAggregateCommand;
using Cti::Devices::Commands::RfnCentronSetLcdConfigurationCommand;
using Cti::Devices::Commands::RfnCentronGetLcdConfigurationCommand;
using Cti::Devices::Commands::RfnIndividualCommandList;

struct resetContextId
{
    resetContextId()
    {
        RfnAggregateCommand::setGlobalContextId(0x4444, test_tag);
    }
};

BOOST_FIXTURE_TEST_SUITE( test_cmd_rfn_Aggregate, resetContextId )

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

BOOST_AUTO_TEST_CASE(test_no_items)
{
    try 
    {
        RfnAggregateCommand({});
        BOOST_FAIL("Empty command list did not throw");
    } 
    catch ( const Cti::YukonErrorException & ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        10);
        BOOST_CHECK_EQUAL(ex.error_description, "No commands passed to RfnAggregateCommand");
    }
}

BOOST_AUTO_TEST_CASE(test_send_one_command)
{
    RfnIndividualCommandList l;
    
    l.emplace_back( std::make_unique<RfnCentronGetLcdConfigurationCommand>() );

    RfnAggregateCommand aggregate( std::move( l ) );

    // execute
    {
        RfnCommand::RfnRequestPayload req = aggregate.executeCommand(execute_time);

        const std::vector<unsigned> exp {
            0x01, //  Aggregate message
            0x01, //  1 message
            0x00, 0x08, //  Payload length 8
            //  message 1
            0x44, 0x44, //  context ID 0x4444
            0x02,       //  ASID 2
            0x00, 0x03, //  message length 3
            0x70, 0x01, 0x00 };

        BOOST_CHECK_EQUAL_RANGES(req, exp);
    }

    // decode
    {
        std::vector<unsigned char> response {
            0x01, //  Aggregate message
            0x01, //  1 message
            0x00, 0x0e, //  Payload length 14
            //  message 1
            0x44, 0x44, //  context ID 0x4444
            0x02,       //  ASID 2
            0x00, 0x09, //  message length 9
            0x71, 0x00, 0x03, 0x00, 0x00, 0x01, 0x01, 0x02, 0x02 };

        const auto & results = aggregate.handleResponse(execute_time, response);
        BOOST_REQUIRE_EQUAL(results.size(), 1);
        const auto & result = results.front();

        BOOST_CHECK(result.points.empty());
        BOOST_CHECK_EQUAL(result.status, 0);
        BOOST_CHECK_EQUAL(result.description, 
                          "Get LCD Configuration Request:"
                          "\nDisplay metrics:"
                          "\nDisplay metric 1: Metric Slot Disabled"
                          "\nDisplay metric 2: No Segments"
                          "\nDisplay metric 3: All Segments");
    }
}

BOOST_AUTO_TEST_CASE(test_short_header)
{
    RfnIndividualCommandList l;

    l.emplace_back(std::make_unique<RfnCentronGetLcdConfigurationCommand>());

    RfnAggregateCommand aggregate(std::move(l));

    aggregate.executeCommand(execute_time);

    std::vector<unsigned char> response{
        0x01, //  Aggregate message
        0x01, //  1 message
        0x01 };  //  truncated header

    const auto & results = aggregate.handleResponse(execute_time, response);
    BOOST_REQUIRE_EQUAL(results.size(), 1);
    const auto & result = results.front();

    BOOST_CHECK(result.points.empty());
    BOOST_CHECK_EQUAL(result.status, 283);
    BOOST_CHECK_EQUAL(result.description, "Get LCD Configuration Request:"
                                          "\nNot enough data received from the device.");
}

BOOST_AUTO_TEST_CASE(test_invalid_command)
{
    RfnIndividualCommandList l;

    l.emplace_back(std::make_unique<RfnCentronGetLcdConfigurationCommand>());

    RfnAggregateCommand aggregate(std::move(l));

    aggregate.executeCommand(execute_time);

    std::vector<unsigned char> response{
        0xcd, //  invalid command type
        0x01, //  1 message
        0x00, 0x08, //  Payload length 8
        //  message 1
        0x44, 0x44, //  context ID 0x4444
        0x44,       //  ASID xx
        0x00, 0x03, //  message length 3
        0x71, 0x00, 0x03 };

    const auto & results = aggregate.handleResponse(execute_time, response);
    BOOST_REQUIRE_EQUAL(results.size(), 1);
    const auto & result = results.front();

    BOOST_CHECK(result.points.empty());
    BOOST_CHECK_EQUAL(result.status, 38);
    BOOST_CHECK_EQUAL(result.description, "Get LCD Configuration Request:"
                                          "\nUnknown Command Received");
}

BOOST_AUTO_TEST_CASE(test_short_payload)
{
    RfnIndividualCommandList l;

    l.emplace_back(std::make_unique<RfnCentronGetLcdConfigurationCommand>());

    RfnAggregateCommand aggregate(std::move(l));

    aggregate.executeCommand(execute_time);

    std::vector<unsigned char> response{
        0x01, //  Aggregate message
        0x01, //  1 message
        0x00, 0x14, //  Payload length 20, actual payload length 8
        //  message 1
        0x44, 0x44, //  context ID 0x4444
        0x02,       //  ASID 2
        0x00, 0x03, //  message length 3
        0x71, 0x00, 0x03 };

    const auto & results = aggregate.handleResponse(execute_time, response);
    BOOST_REQUIRE_EQUAL(results.size(), 1);
    const auto & result = results.front();

    BOOST_CHECK(result.points.empty());
    BOOST_CHECK_EQUAL(result.status, 283);
    BOOST_CHECK_EQUAL(result.description, "Get LCD Configuration Request:"
                                          "\nNot enough data received from the device.");
}

BOOST_AUTO_TEST_CASE(test_short_message)
{
    RfnIndividualCommandList l;

    l.emplace_back(std::make_unique<RfnCentronGetLcdConfigurationCommand>());

    RfnAggregateCommand aggregate(std::move(l));

    aggregate.executeCommand(execute_time);

    std::vector<unsigned char> response{
        0x01, //  Aggregate message
        0x01, //  1 message
        0x00, 0x08, //  Payload length 8
        //  message 1
        0x44, 0x44, //  context ID 0x4444
        0x02,       //  ASID 2
        0x00, 0x09, //  message length 9, actual message length only 3
        0x71, 0x00, 0x03 };

    const auto & results = aggregate.handleResponse(execute_time, response);
    BOOST_REQUIRE_EQUAL(results.size(), 1);
    const auto & result = results.front();

    BOOST_CHECK(result.points.empty());
    BOOST_CHECK_EQUAL(result.status, 283);
    BOOST_CHECK_EQUAL(result.description, "Get LCD Configuration Request:"
                                          "\nNot enough data received from the device.");
}

BOOST_AUTO_TEST_CASE(test_error_response)
{
    RfnIndividualCommandList l;

    l.emplace_back(std::make_unique<RfnCentronGetLcdConfigurationCommand>());

    RfnAggregateCommand aggregate(std::move(l));

    aggregate.executeCommand(execute_time);

    std::vector<unsigned char> response{
        0x01, //  Aggregate message
        0x01, //  1 message
        0x00, 0x08, //  Payload length 8
        //  message 1
        0x44, 0x44, //  context ID 0x4444
        0x02,       //  ASID 2
        0x00, 0x03, //  message length 3
        0x71, 0x00, 0x03 };

    const auto results = aggregate.handleResponse(execute_time, response);
    BOOST_REQUIRE_EQUAL(results.size(), 1);
    const auto & result = results.front();
    BOOST_CHECK(result.points.empty());
    BOOST_CHECK_EQUAL(result.status, 264);
    BOOST_CHECK_EQUAL(result.description, "Get LCD Configuration Request:"
                                          "\nInvalid display metric length - (0, expecting 6)");
}

BOOST_AUTO_TEST_CASE(test_error_response_two_commands)
{
    RfnIndividualCommandList l;

    l.emplace_back(std::make_unique<RfnCentronGetLcdConfigurationCommand>());
    l.emplace_back(std::make_unique<RfnCentronSetLcdConfigurationCommand>(
            RfnCentronSetLcdConfigurationCommand::metric_vector_t{ 0x00, 0x01, 0x02 },
            RfnCentronSetLcdConfigurationCommand::DisconnectDisplayEnabled,
            RfnCentronSetLcdConfigurationCommand::DisplayDigits6x1,
            1));

    RfnAggregateCommand aggregate(std::move(l));

    aggregate.executeCommand(execute_time);

    std::vector<unsigned char> response{
        0x01, //  Aggregate message
        0x01, //  1 message
        0x00, 0x08, //  Payload length 8
        //  message 1
        0x44, 0x44, //  context ID 0x4444
        0x02,       //  ASID 2
        0x00, 0x03, //  message length 3
        0x71, 0x00, 0x03 };

    const auto results = aggregate.handleResponse(execute_time, response);
    BOOST_REQUIRE_EQUAL(results.size(), 2);
    {
        const auto & result = results.front();
        BOOST_CHECK(result.points.empty());
        BOOST_CHECK_EQUAL(result.status, 264);
        BOOST_CHECK_EQUAL(result.description, "Get LCD Configuration Request:\n"
                                              "Invalid display metric length - (0, expecting 6)");
    }
    {
        const auto & result = results.back();
        BOOST_CHECK(result.points.empty());
        BOOST_CHECK_EQUAL(result.status, 308);
        BOOST_CHECK_EQUAL(result.description, "Set LCD Configuration Request:"
                                              "\nAggregate response did not include an entry for the command.");
    }
}

BOOST_AUTO_TEST_CASE(test_error_response_bad_second_payload_length)
{
    RfnIndividualCommandList l;

    l.emplace_back(std::make_unique<RfnCentronGetLcdConfigurationCommand>());
    l.emplace_back(std::make_unique<RfnCentronSetLcdConfigurationCommand>(
        RfnCentronSetLcdConfigurationCommand::metric_vector_t{ 0x00, 0x01, 0x02 },
        RfnCentronSetLcdConfigurationCommand::DisconnectDisplayEnabled,
        RfnCentronSetLcdConfigurationCommand::DisplayDigits6x1,
        1));

    RfnAggregateCommand aggregate(std::move(l));

    aggregate.executeCommand(execute_time);

    std::vector<unsigned char> response{
        0x01, //  Aggregate message
        0x02, //  1 message
        0x00, 0x0d, //  Payload length 13
        //  message 1
        0x44, 0x44, //  context ID 0x4444
        0x02,       //  ASID 2
        0x00, 0x03, //  message length 3
        0x71, 0x00, 0x03,
        //  message 2
        0x44, 0x45, //  context ID 0x4445
        0x02,       //  ASID 2
        0x00, 0x03 };  //  message length 3, but missing

    const auto results = aggregate.handleResponse(execute_time, response);
    BOOST_REQUIRE_EQUAL(results.size(), 2);
    {
        const auto & result = results.front();
        BOOST_CHECK(result.points.empty());
        BOOST_CHECK_EQUAL(result.status, 264);
        BOOST_CHECK_EQUAL(result.description, "Get LCD Configuration Request:"
                                              "\nInvalid display metric length - (0, expecting 6)");
    }
    {
        const auto & result = results.back();
        BOOST_CHECK(result.points.empty());
        BOOST_CHECK_EQUAL(result.status, 283);
        BOOST_CHECK_EQUAL(result.description, "Set LCD Configuration Request:"
                                              "\nNot enough data received from the device.");
    }
}

BOOST_AUTO_TEST_CASE(test_two_commands_handleError)
{
    RfnIndividualCommandList l;

    l.emplace_back(std::make_unique<RfnCentronGetLcdConfigurationCommand>());
    l.emplace_back(std::make_unique<RfnCentronSetLcdConfigurationCommand>(
        RfnCentronSetLcdConfigurationCommand::metric_vector_t{ 0x00, 0x01, 0x02 },
        RfnCentronSetLcdConfigurationCommand::DisconnectDisplayEnabled,
        RfnCentronSetLcdConfigurationCommand::DisplayDigits6x1,
        1));

    RfnAggregateCommand aggregate(std::move(l));

    aggregate.executeCommand(execute_time);

    const auto results = aggregate.handleError(execute_time, ClientErrors::E2eNetworkUnavailable);
    BOOST_REQUIRE_EQUAL(results.size(), 2);
    {
        const auto & result = results.front();
        BOOST_CHECK(result.points.empty());
        BOOST_CHECK_EQUAL(result.status, 286);
        BOOST_CHECK_EQUAL(result.description, "Get LCD Configuration Request:"
                                              "\nNetwork is unavailable.");
    }
    {
        const auto & result = results.back();
        BOOST_CHECK(result.points.empty());
        BOOST_CHECK_EQUAL(result.status, 286);
        BOOST_CHECK_EQUAL(result.description, "Set LCD Configuration Request:"
                                              "\nNetwork is unavailable.");
    }
}

BOOST_AUTO_TEST_CASE(test_missing_response)
{
    RfnIndividualCommandList l;

    l.emplace_back(std::make_unique<RfnCentronGetLcdConfigurationCommand>());

    RfnAggregateCommand aggregate(std::move(l));

    aggregate.executeCommand(execute_time);

    std::vector<unsigned char> response {
        0x01, //  Aggregate message
        0x00, //  1 message
        0x00, 0x00 }; //  Payload length 0

    const auto results = aggregate.handleResponse(execute_time, response);
    BOOST_REQUIRE_EQUAL(results.size(), 1);
    const auto & result = results.front();
    BOOST_CHECK(result.points.empty());
    BOOST_CHECK_EQUAL(result.status, 308);
    BOOST_CHECK_EQUAL(result.description, "Get LCD Configuration Request:"
                                          "\nAggregate response did not include an entry for the command.");
}

BOOST_AUTO_TEST_CASE(test_send_two_commands)
{
    RfnIndividualCommandList l;

    l.emplace_back(std::make_unique<RfnCentronGetLcdConfigurationCommand>());
    l.emplace_back(std::make_unique<RfnCentronSetLcdConfigurationCommand>(
            RfnCentronSetLcdConfigurationCommand::metric_vector_t { 0x00, 0x01, 0x02 },
            RfnCentronSetLcdConfigurationCommand::DisconnectDisplayEnabled,
            RfnCentronSetLcdConfigurationCommand::DisplayDigits6x1,
            1));

    RfnAggregateCommand aggregate(std::move(l));

    // execute
    {
        RfnCommand::RfnRequestPayload req = aggregate.executeCommand(execute_time);

        const std::vector<unsigned> exp{
            0x01, //  Aggregate message
            0x02, //  2 messages
            0x00, 0x1c, //  Payload length 28
            //  message 1
            0x44, 0x44, //  context ID 0x4444
            0x02,       //  ASID 2
            0x00, 0x03, //  message length 3
                0x70, 
                0x01, 
                0x00,
            //  message 2
            0x44, 0x45, //  context ID 0x4445
            0x02,       //  ASID 2
            0x00, 0x0f, //  message length 15
                0x70, 
                0x00, 
                0x06, 
                0x00, 0x00, 
                0x01, 0x01, 
                0x02, 0x02, 
                0xfd, 0x06, 
                0xfe, 0x01, 
                0xff, 0x01 };

        BOOST_CHECK_EQUAL_RANGES(req, exp);
    }

    // decode
    {
        std::vector<unsigned char> response{
            0x01, //  Aggregate message
            0x02, //  2 messages
            0x00, 0x16, //  Payload length 22
            //  message 1
            0x44, 0x44, //  context ID 0x4444
            0x02,       //  ASID 2
            0x00, 0x09, //  message length 9
                0x71, 
                0x00, 
                0x03, 
                0x00, 0x00, 
                0x01, 0x01, 
                0x02, 0x02,
            //  message 2
            0x44, 0x45, //  context ID 0x4445
            0x02,       //  ASID 2
            0x00, 0x03,
                0x71, 
                0x00, 
                0x00 };

        const auto results = aggregate.handleResponse(execute_time, response);
        BOOST_REQUIRE_EQUAL(results.size(), 2);
        {
            const auto & result = results.front();
            BOOST_CHECK(result.points.empty());
            BOOST_CHECK_EQUAL(result.status, 0);
            BOOST_CHECK_EQUAL(result.description,
                "Get LCD Configuration Request:"
                "\nDisplay metrics:"
                "\nDisplay metric 1: Metric Slot Disabled"
                "\nDisplay metric 2: No Segments"
                "\nDisplay metric 3: All Segments");
        }
        {
            const auto & result = results.back();
            BOOST_CHECK(result.points.empty());
            BOOST_CHECK_EQUAL(result.status, 0);
            BOOST_CHECK_EQUAL(result.description,
                "Set LCD Configuration Request:"
                "\nDisplay metrics successfully set:"
                "\nDisplay metric 1: Metric Slot Disabled"
                "\nDisplay metric 2: No Segments"
                "\nDisplay metric 3: All Segments"
                "\nDisconnect display: enabled"
                "\nLCD cycle time: 1 second"
                "\nDisplay digits: 6x1");

        }
    }
}

BOOST_AUTO_TEST_SUITE_END()
