#include <boost/test/unit_test.hpp>

#include "cmd_rfn_Aggregate.h"
#include "cmd_rfn_CentronLcdConfiguration.h"

#include "boost_test_helpers.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandPtr;
using Cti::Devices::Commands::RfnCommandList;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnAggregateCommand;
using Cti::Devices::Commands::RfnCentronSetLcdConfigurationCommand;
using Cti::Devices::Commands::RfnCentronGetLcdConfigurationCommand;

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_Aggregate )

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
    RfnCommandList l;
    
    l.emplace_back( std::make_unique<RfnCentronGetLcdConfigurationCommand>() );

    RfnAggregateCommand aggregate( std::move( l ) );

    // execute
    {
        RfnCommand::RfnRequestPayload req = aggregate.executeCommand(execute_time);

        const std::vector<unsigned> exp {
            0x01, //  Aggregate message
            0x01, //  1 message
            0x07, 0x00, //  Payload length 7
            //  message 1
            0x44, 0x44, //  context ID 4444
            0x03, 0x00, //  message length 3
            0x70, 0x01, 0x00 };

        BOOST_CHECK_EQUAL_RANGES(req, exp);
    }

    // decode
    {
        std::vector<unsigned char> response {
            0x01, //  Aggregate message
            0x01, //  1 message
            0x07, 0x00, //  Payload length 7
            //  message 1
            0x44, 0x44, //  context ID 4444
            0x09, 0x00, //  message length 9
            0x71, 0x00, 0x03, 0x00, 0x00, 0x01, 0x01, 0x02, 0x02 };

        RfnCommandResult rcv = aggregate.decodeCommand(execute_time, response);

        BOOST_CHECK(rcv.points.empty());
        BOOST_CHECK_EQUAL(rcv.description, 
                          "Aggregate message 1, context ID 17476"
                          "\nDisplay metrics:"
                          "\nDisplay metric 1: Metric Slot Disabled"
                          "\nDisplay metric 2: No Segments"
                          "\nDisplay metric 3: All Segments");
    }
}

BOOST_AUTO_TEST_CASE(test_send_two_commands)
{
    RfnCommandList l;

    l.emplace_back(std::make_unique<RfnCentronGetLcdConfigurationCommand>());
    l.emplace_back(std::make_unique<RfnCentronSetLcdConfigurationCommand>(
            RfnCentronSetLcdConfigurationCommand::metric_vector_t{ 0x00, 0x01, 0x02 },
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
            0x1a, 0x00, //  Payload length 26
            //  message 1
            0x45, 0x44, //  context ID 4445
            0x03, 0x00, //  message length 3
                0x70, 
                0x01, 
                0x00,
            //  message 2
            0x46, 0x44, //  context ID 4446
            0x0f, 0x00, //  message length 15
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
            0x14, 0x00, //  Payload length 20
            //  message 1
            0x45, 0x44, //  context ID 4445
            0x09, 0x00, //  message length 9
                0x71, 
                0x00, 
                0x03, 
                0x00, 0x00, 
                0x01, 0x01, 
                0x02, 0x02,
            //  message 2
            0x46, 0x44, //  context ID 4446
            0x03, 0x00,
                0x71, 
                0x00, 
                0x00 };

        RfnCommandResult rcv = aggregate.decodeCommand(execute_time, response);

        BOOST_CHECK(rcv.points.empty());
        BOOST_CHECK_EQUAL(rcv.description,
            "Aggregate message 1, context ID 17477"
            "\nDisplay metrics:"
            "\nDisplay metric 1: Metric Slot Disabled"
            "\nDisplay metric 2: No Segments"
            "\nDisplay metric 3: All Segments"
            "\nAggregate message 2, context ID 17478"
            "\nDisplay metrics successfully set"
            "\nDisplay metric 1: Metric Slot Disabled"
            "\nDisplay metric 2: No Segments"
            "\nDisplay metric 3: All Segments"
            "\nDisconnect display: enabled"
            "\nLCD cycle time: 1 second"
            "\nDisplay digits: 6x1");
    }
}

BOOST_AUTO_TEST_SUITE_END()
