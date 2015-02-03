#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"

#include "cmd_rfn_FocusAlLcdConfiguration.h"

#include "boost_test_helpers.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnFocusAlLcdConfigurationCommand;
using Cti::Devices::Commands::RfnFocusAlLcdConfigurationReadCommand;
using Cti::Devices::Commands::RfnFocusAlLcdConfigurationWriteCommand;

typedef RfnFocusAlLcdConfigurationCommand::Metrics      Metrics;
typedef RfnFocusAlLcdConfigurationCommand::MetricVector MetricVector;

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_FocusAlLcdConfiguration )

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

BOOST_AUTO_TEST_CASE(test_write_no_items)
{
    const unsigned char display_item_duration = 10;
    const RfnFocusAlLcdConfigurationCommand::MetricVector display_items;

    RfnFocusAlLcdConfigurationWriteCommand lcdConfiguration(display_items, display_item_duration);

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x72)(0x00) // command code - operation
                (0x00)       // nbr of items
                (0x0a);      // duration

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        std::vector<unsigned char> response = boost::assign::list_of
                (0x73)       // command code
                (0x00);      // success

        RfnCommandResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Status: Success (0)"
                          "\nDisplay items sent:"
                          "\nLCD cycle time : 10 seconds"
                          "\nNo display metrics";

        BOOST_CHECK_EQUAL(rcv.description, exp);
        BOOST_CHECK_EQUAL(lcdConfiguration.displayItemDuration, display_item_duration);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                lcdConfiguration.metrics.begin(), lcdConfiguration.metrics.end(),
                 display_items.begin(),           display_items.end());
    }
}

BOOST_AUTO_TEST_CASE(test_write_fail)
{
    const unsigned char display_item_duration = 10;
    const RfnFocusAlLcdConfigurationCommand::MetricVector display_items;

    RfnFocusAlLcdConfigurationWriteCommand lcdConfiguration(display_items, display_item_duration);

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x72)(0x00) // command code - operation
                (0x00)       // nbr of items
                (0x0a);      // duration

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        std::vector<unsigned char> response = boost::assign::list_of
                (0x73)       // command code
                (0x01);      // failure

        try
        {
            RfnCommandResult rcv = lcdConfiguration.decodeCommand( execute_time, response );

            BOOST_ERROR("Should have thrown");
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_CHECK_EQUAL( ex.error_code, ClientErrors::Unknown );
            BOOST_CHECK_EQUAL( ex.error_description, "Failure Status (1)" );
        }
    }
}

BOOST_AUTO_TEST_CASE(test_write_3_items)
{
    const unsigned char display_item_duration = 21;
    const RfnFocusAlLcdConfigurationCommand::MetricVector display_items = boost::assign::list_of
            (RfnFocusAlLcdConfigurationCommand::deliveredKwh6x1)
            (RfnFocusAlLcdConfigurationCommand::deliveredKwh5x1)
            (RfnFocusAlLcdConfigurationCommand::deliveredKwh4x1);

    RfnFocusAlLcdConfigurationWriteCommand lcdConfiguration(display_items, display_item_duration);

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x72)(0x00)        // command code - operation
                (0x03)              // nbr of items
                (0x15)              // duration
                (0x00)(0x30)(0x31)  // item 1
                (0x01)(0x30)(0x32)  // item 2
                (0x02)(0x30)(0x33); // item 3

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        std::vector<unsigned char> response = boost::assign::list_of
                (0x73)              // command code
                (0x00);             // success

        RfnCommandResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Status: Success (0)"
                          "\nDisplay items sent:"
                          "\nLCD cycle time : 21 seconds"
                          "\nDisplay metric 1 : Delivered kWh (6 digit)"
                          "\nDisplay metric 2 : Delivered kWh (5 digit)"
                          "\nDisplay metric 3 : Delivered kWh (4 digit)";

        BOOST_CHECK_EQUAL(rcv.description, exp);
        BOOST_CHECK_EQUAL(lcdConfiguration.displayItemDuration, display_item_duration);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                lcdConfiguration.metrics.begin(), lcdConfiguration.metrics.end(),
                 display_items.begin(),           display_items.end());
    }
}

BOOST_AUTO_TEST_CASE(test_Write_10_items)
{
    const unsigned char display_item_duration = 7;
    const RfnFocusAlLcdConfigurationCommand::MetricVector display_items = boost::assign::list_of
            (RfnFocusAlLcdConfigurationCommand::firmwareVersion)
            (RfnFocusAlLcdConfigurationCommand::deliveredKwh5x1)
            (RfnFocusAlLcdConfigurationCommand::netKwh4x1      )
            (RfnFocusAlLcdConfigurationCommand::deliveredKwh6x1)
            (RfnFocusAlLcdConfigurationCommand::reverseKwh6x1 )
            (RfnFocusAlLcdConfigurationCommand::totalKwh4x1    )
            (RfnFocusAlLcdConfigurationCommand::deliveredKwh6x1)
            (RfnFocusAlLcdConfigurationCommand::totalKwh4x10   )
            (RfnFocusAlLcdConfigurationCommand::diagnosticFlags)
            (RfnFocusAlLcdConfigurationCommand::allSegments    );

    RfnFocusAlLcdConfigurationWriteCommand lcdConfiguration(display_items, display_item_duration);

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x72)(0x00)           // command code - operation
                (0x0a)                 // nbr of items
                (0x07)                 // duration
                (0x12)(0x30)(0x31)     // item 1
                (0x01)(0x30)(0x32)     // item 2
                (0x0e)(0x30)(0x33)     // item 3
                (0x00)(0x30)(0x34)     // item 4
                (0x04)(0x30)(0x35)     // item 5
                (0x0a)(0x30)(0x36)     // item 6
                (0x00)(0x30)(0x37)     // item 7
                (0x0b)(0x30)(0x38)     // item 8
                (0x10)(0x30)(0x39)     // item 9
                (0x11)(0x31)(0x30);    // item 10

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        std::vector<unsigned char> response = boost::assign::list_of
                (0x73)                 // command code
                (0x00);                // success

        RfnCommandResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Status: Success (0)"
                          "\nDisplay items sent:"
                          "\nLCD cycle time : 7 seconds"
                          "\nDisplay metric 1 : Firmware version"
                          "\nDisplay metric 2 : Delivered kWh (5 digit)"
                          "\nDisplay metric 3 : Net kWh (4 digit)"
                          "\nDisplay metric 4 : Delivered kWh (6 digit)"
                          "\nDisplay metric 5 : Reverse kWh (6 digit)"
                          "\nDisplay metric 6 : Total kWh (4 digit)"
                          "\nDisplay metric 7 : Delivered kWh (6 digit)"
                          "\nDisplay metric 8 : Total kWh (4 x 10  digit)"
                          "\nDisplay metric 9 : Diagnostic flags"
                          "\nDisplay metric 10 : All Segments";

        BOOST_CHECK_EQUAL(rcv.description, exp);
        BOOST_CHECK_EQUAL(lcdConfiguration.displayItemDuration, display_item_duration);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                 lcdConfiguration.metrics.begin(), lcdConfiguration.metrics.end(),
                 display_items.begin(),             display_items.end());
    }
}

BOOST_AUTO_TEST_CASE(test_read_3_items)
{
    RfnFocusAlLcdConfigurationReadCommand lcdConfiguration;

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x72)(0x01);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        const unsigned char display_item_duration = 21;
        const RfnFocusAlLcdConfigurationCommand::MetricVector display_items = boost::assign::list_of
                (RfnFocusAlLcdConfigurationCommand::deliveredKwh6x1)
                (RfnFocusAlLcdConfigurationCommand::deliveredKwh5x1)
                (RfnFocusAlLcdConfigurationCommand::deliveredKwh4x1);

        std::vector<unsigned char> response = boost::assign::list_of
                (0x73)              // command code
                (0x03)              // nbr of items
                (0x15)              // duration
                (0x00)(0x41)(0x42)  // item 1
                (0x01)(0x43)(0x44)  // item 2
                (0x02)(0x45)(0x46); // item 3

        RfnCommandResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Display items received:"
                          "\nLCD cycle time : 21 seconds"
                          "\nDisplay metric 1 [AB] : Delivered kWh (6 digit)"
                          "\nDisplay metric 2 [CD] : Delivered kWh (5 digit)"
                          "\nDisplay metric 3 [EF] : Delivered kWh (4 digit)";

        BOOST_CHECK_EQUAL(rcv.description, exp);

        const boost::optional<unsigned char> displayItemDurationRcv = lcdConfiguration.getDisplayItemDurationReceived();

        BOOST_REQUIRE( !! displayItemDurationRcv );

        BOOST_CHECK_EQUAL( *displayItemDurationRcv, display_item_duration);

        boost::optional<RfnFocusAlLcdConfigurationCommand::MetricVector> display_items_received = lcdConfiguration.getDisplayItemsReceived();

        BOOST_REQUIRE( !! display_items_received );

        BOOST_CHECK_EQUAL_RANGES(
                 *display_items_received,
                 display_items);
    }
}

BOOST_AUTO_TEST_SUITE_END()
