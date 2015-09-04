#include <boost/test/unit_test.hpp>

#include "cmd_rfn_CentronLcdConfiguration.h"

#include "boost_test_helpers.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnCentronSetLcdConfigurationCommand;
using Cti::Devices::Commands::RfnCentronGetLcdConfigurationCommand;

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_CentronLcdConfiguration )

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);


BOOST_AUTO_TEST_CASE(test_send_no_items)
{
    const std::vector<unsigned char> metrics;

    RfnCentronSetLcdConfigurationCommand lcdConfiguration(
        metrics,
        RfnCentronSetLcdConfigurationCommand::DisconnectDisplayDisabled,
        RfnCentronSetLcdConfigurationCommand::DisplayDigits5x1,
        0);

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = lcdConfiguration.executeCommand(execute_time);

        const std::vector<unsigned> exp{
            0x70, 0x00, 0x03, 0xfd, 0x05, 0xfe, 0x00, 0xff, 0x00 };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        std::vector<unsigned char> response{
            0x71, 0x00, 0x00 };

        RfnCommandResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Display metrics successfully set"
            "\nNo display metrics"
            "\nDisconnect display: disabled"
            "\nLCD cycle time: (default)"
            "\nDisplay digits: 5x1";

        BOOST_CHECK_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_send_no_items_disconnect_omitted)
{
    const std::vector<unsigned char> metrics;

    RfnCentronSetLcdConfigurationCommand lcdConfiguration(
        metrics,
        RfnCentronSetLcdConfigurationCommand::DisplayDigits5x1,
        0);

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = lcdConfiguration.executeCommand(execute_time);

        const std::vector<unsigned> exp{
            0x70, 0x00, 0x03, 0xfd, 0x05, 0xfe, 0x00 };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        std::vector<unsigned char> response{
            0x71, 0x00, 0x00 };

        RfnCommandResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Display metrics successfully set"
            "\nNo display metrics"
            "\nLCD cycle time: (default)"
            "\nDisplay digits: 5x1";

        BOOST_CHECK_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_read_no_items)
{
    RfnCentronGetLcdConfigurationCommand lcdConfiguration;

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp{
                0x70, 0x01, 0x00 };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        std::vector<unsigned char> response {
                0x71, 0x00, 0x00 };

        RfnCommandResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Display metrics:"
                          "\nNo display metrics";

        BOOST_CHECK_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_send_3_items)
{
    const std::vector<unsigned char> metrics {
            0x00, 0x01, 0x02 };

    RfnCentronSetLcdConfigurationCommand lcdConfiguration(
            metrics,
            RfnCentronSetLcdConfigurationCommand::DisconnectDisplayEnabled,
            RfnCentronSetLcdConfigurationCommand::DisplayDigits6x1,
            1 );

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned> exp {
                0x70, 0x00, 0x06, 0x00, 0x00, 0x01, 0x01, 0x02, 0x02, 0xfd, 0x06, 0xfe, 0x01, 0xff, 0x01 };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        std::vector<unsigned char> response {
                0x71, 0x00, 0x00 };

        RfnCommandResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Display metrics successfully set"
                          "\nDisplay metric 1: Metric Slot Disabled"
                          "\nDisplay metric 2: No Segments"
                          "\nDisplay metric 3: All Segments"
                          "\nDisconnect display: enabled"
                          "\nLCD cycle time: 1 second"
                          "\nDisplay digits: 6x1";

        BOOST_CHECK_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_read_3_items)
{
    RfnCentronGetLcdConfigurationCommand lcdConfiguration;

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp {
                0x70, 0x01, 0x00 }; // read_only -> operation code = 0x01, zero metric items

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        std::vector<unsigned char> response {
                0x71, 0x00, 0x03, 0x00, 0x00, 0x01, 0x01, 0x02, 0x02 };

        RfnCommandResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Display metrics:"
                          "\nDisplay metric 1: Metric Slot Disabled"
                          "\nDisplay metric 2: No Segments"
                          "\nDisplay metric 3: All Segments";

        BOOST_CHECK_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_send_all_items)
{
    const std::vector<unsigned char> metrics {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
            0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13,
            0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D,
            0x1E, 0x1F, 0x20, 0x21, 0x22, 0x23, 0x24 };

    RfnCentronSetLcdConfigurationCommand lcdConfiguration(
            metrics,
            RfnCentronSetLcdConfigurationCommand::DisconnectDisplayEnabled,
            RfnCentronSetLcdConfigurationCommand::DisplayDigits4x1,
            2 );

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp {
                0x70, 0x00, 0x28, 0x00, 0x00, 0x01, 0x01, 0x02, 0x02, 0x03,
                0x03, 0x04, 0x04, 0x05, 0x05, 0x06, 0x06, 0x07, 0x07, 0x08,
                0x08, 0x09, 0x09, 0x0A, 0x0A, 0x0B, 0x0B, 0x0C, 0x0C, 0x0D,
                0x0D, 0x0E, 0x0E, 0x0F, 0x0F, 0x10, 0x10, 0x11, 0x11, 0x12,
                0x12, 0x13, 0x13, 0x14, 0x14, 0x15, 0x15, 0x16, 0x16, 0x17,
                0x17, 0x18, 0x18, 0x19, 0x19, 0x1A, 0x1A, 0x1B, 0x1B, 0x1C,
                0x1C, 0x1D, 0x1D, 0x1E, 0x1E, 0x1F, 0x1F, 0x20, 0x20, 0x21,
                0x21, 0x22, 0x22, 0x23, 0x23, 0x24, 0x24, 0xfd, 0x04, 0xfe,
                0x02, 0xff, 0x01 };

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        std::vector<unsigned char> response {
                0x71, 0x00, 0x00 };

        RfnCommandResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Display metrics successfully set"
                          "\nDisplay metric 1: Metric Slot Disabled"
                          "\nDisplay metric 2: No Segments"
                          "\nDisplay metric 3: All Segments"
                          "\nDisplay metric 4: Tamper"
                          "\nDisplay metric 5: Current Local Time"
                          "\nDisplay metric 6: Current Local Date"
                          "\nDisplay metric 7: Unidir kWh (Sum of Forward and Reverse)"
                          "\nDisplay metric 8: Net kWh (Difference of Forward and Reverse)"
                          "\nDisplay metric 9: Delivered kWh"
                          "\nDisplay metric 10: Received kWh"
                          "\nDisplay metric 11: Last Interval Demand (W)"
                          "\nDisplay metric 12: Peak Demand (W)"
                          "\nDisplay metric 13: Date of Peak Demand (W)"
                          "\nDisplay metric 14: Time of Peak Demand (W)"
                          "\nDisplay metric 15: Last Interval Voltage (V)"
                          "\nDisplay metric 16: Peak Voltage (V)"
                          "\nDisplay metric 17: Date of Peak Voltage"
                          "\nDisplay metric 18: Time of Peak Voltage"
                          "\nDisplay metric 19: Min Voltage (V)"
                          "\nDisplay metric 20: Date of Min Voltage"
                          "\nDisplay metric 21: Time of Min Voltage"
                          "\nDisplay metric 22: TOU Rate A kWh"
                          "\nDisplay metric 23: TOU Rate A Peak Demand (W)"
                          "\nDisplay metric 24: TOU Rate A Date of Peak Demand"
                          "\nDisplay metric 25: TOU Rate A Time of Peak Demand"
                          "\nDisplay metric 26: TOU Rate B kWh"
                          "\nDisplay metric 27: TOU Rate B Peak Demand (W)"
                          "\nDisplay metric 28: TOU Rate B Date of Peak Demand"
                          "\nDisplay metric 29: TOU Rate B Time of Peak Demand"
                          "\nDisplay metric 30: TOU Rate C kWh"
                          "\nDisplay metric 31: TOU Rate C Peak Demand (W)"
                          "\nDisplay metric 32: TOU Rate C Date of Peak Demand"
                          "\nDisplay metric 33: TOU Rate C Time of Peak Demand"
                          "\nDisplay metric 34: TOU Rate D kWh"
                          "\nDisplay metric 35: TOU Rate D Peak Demand (W)"
                          "\nDisplay metric 36: TOU Rate D Date of Peak Demand"
                          "\nDisplay metric 37: TOU Rate D Time of Peak Demand"
                          "\nDisconnect display: enabled"
                          "\nLCD cycle time: 2 seconds"
                          "\nDisplay digits: 4x1";

        BOOST_CHECK_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_read_all_items)
{
    RfnCentronGetLcdConfigurationCommand lcdConfiguration;

    // execute
    {
        RfnCommand::RfnRequestPayload rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp {
                0x70, 0x01, 0x00 }; // read_only -> operation code = 0x01, zero metric items

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode
    {
        std::vector<unsigned char> response {
                0x71, 0x00, 0x28, 0x00, 0x00, 0x01, 0x01, 0x02, 0x02, 0x03,
                0x03, 0x04, 0x04, 0x05, 0x05, 0x06, 0x06, 0x07, 0x07, 0x08,
                0x08, 0x09, 0x09, 0x0A, 0x0A, 0x0B, 0x0B, 0x0C, 0x0C, 0x0D,
                0x0D, 0x0E, 0x0E, 0x0F, 0x0F, 0x10, 0x10, 0x11, 0x11, 0x12,
                0x12, 0x13, 0x13, 0x14, 0x14, 0x15, 0x15, 0x16, 0x16, 0x17,
                0x17, 0x18, 0x18, 0x19, 0x19, 0x1A, 0x1A, 0x1B, 0x1B, 0x1C,
                0x1C, 0x1D, 0x1D, 0x1E, 0x1E, 0x1F, 0x1F, 0x20, 0x20, 0x21,
                0x21, 0x22, 0x22, 0x23, 0x23, 0x24, 0x24, 0xff, 0x01, 0xfe,
                0x0f, 0xfd, 0x05 };

        RfnCommandResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Display metrics:"
                          "\nDisplay metric 1: Metric Slot Disabled"
                          "\nDisplay metric 2: No Segments"
                          "\nDisplay metric 3: All Segments"
                          "\nDisplay metric 4: Tamper"
                          "\nDisplay metric 5: Current Local Time"
                          "\nDisplay metric 6: Current Local Date"
                          "\nDisplay metric 7: Unidir kWh (Sum of Forward and Reverse)"
                          "\nDisplay metric 8: Net kWh (Difference of Forward and Reverse)"
                          "\nDisplay metric 9: Delivered kWh"
                          "\nDisplay metric 10: Received kWh"
                          "\nDisplay metric 11: Last Interval Demand (W)"
                          "\nDisplay metric 12: Peak Demand (W)"
                          "\nDisplay metric 13: Date of Peak Demand (W)"
                          "\nDisplay metric 14: Time of Peak Demand (W)"
                          "\nDisplay metric 15: Last Interval Voltage (V)"
                          "\nDisplay metric 16: Peak Voltage (V)"
                          "\nDisplay metric 17: Date of Peak Voltage"
                          "\nDisplay metric 18: Time of Peak Voltage"
                          "\nDisplay metric 19: Min Voltage (V)"
                          "\nDisplay metric 20: Date of Min Voltage"
                          "\nDisplay metric 21: Time of Min Voltage"
                          "\nDisplay metric 22: TOU Rate A kWh"
                          "\nDisplay metric 23: TOU Rate A Peak Demand (W)"
                          "\nDisplay metric 24: TOU Rate A Date of Peak Demand"
                          "\nDisplay metric 25: TOU Rate A Time of Peak Demand"
                          "\nDisplay metric 26: TOU Rate B kWh"
                          "\nDisplay metric 27: TOU Rate B Peak Demand (W)"
                          "\nDisplay metric 28: TOU Rate B Date of Peak Demand"
                          "\nDisplay metric 29: TOU Rate B Time of Peak Demand"
                          "\nDisplay metric 30: TOU Rate C kWh"
                          "\nDisplay metric 31: TOU Rate C Peak Demand (W)"
                          "\nDisplay metric 32: TOU Rate C Date of Peak Demand"
                          "\nDisplay metric 33: TOU Rate C Time of Peak Demand"
                          "\nDisplay metric 34: TOU Rate D kWh"
                          "\nDisplay metric 35: TOU Rate D Peak Demand (W)"
                          "\nDisplay metric 36: TOU Rate D Date of Peak Demand"
                          "\nDisplay metric 37: TOU Rate D Time of Peak Demand"
                          "\nDisconnect display: Enabled"
                          "\nLCD cycle time: 15 seconds"
                          "\nDisplay digits: 5x1";

        BOOST_CHECK_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_SUITE_END()
