#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"

#include "cmd_rfn_CentronLcdConfiguration.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCentronLcdConfigurationCommand;

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_CentronLcdConfiguration )

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);


BOOST_AUTO_TEST_CASE(test_no_metrics)
{
    const std::vector<unsigned char> metrics;

    RfnCentronLcdConfigurationCommand lcdConfiguration(metrics, false);

    // execute
    {
        RfnCommand::RfnRequest rcv = lcdConfiguration.execute(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x69)(0x00)(0x00);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        std::vector<unsigned char> response = boost::assign::list_of
                (0x71)(0x00);

        RfnCommand::RfnResult rcv = lcdConfiguration.decode(execute_time, response);

        std::string exp = "No display metric found";

        BOOST_REQUIRE_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_3_items)
{
    const std::vector<unsigned char> metrics = boost::assign::list_of
            (0x00)(0x01)(0x02);

    RfnCentronLcdConfigurationCommand lcdConfiguration(metrics, false);

    // execute
    {
        RfnCommand::RfnRequest rcv = lcdConfiguration.execute(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x69)(0x00)(0x03)(0x00)(0x01)(0x02);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        std::vector<unsigned char> response = boost::assign::list_of
                (0x71)(0x03)(0x00)(0x01)(0x02);

        RfnCommand::RfnResult rcv = lcdConfiguration.decode(execute_time, response);

        std::string exp = "Display metric : Metric Slot Disabled\n"
                          "Display metric : No Segments\n"
                          "Display metric : All Segments\n";

        BOOST_REQUIRE_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_3_items_readonly)
{
    const std::vector<unsigned char> metrics = boost::assign::list_of
            (0x00)(0x01)(0x13);

    RfnCentronLcdConfigurationCommand lcdConfiguration(metrics, true);

    // execute
    {
        RfnCommand::RfnRequest rcv = lcdConfiguration.execute(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x69)(0x01)(0x00); // read_only -> operation code = 0x01, zero metric items

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        std::vector<unsigned char> response = boost::assign::list_of
                (0x71)(0x03)(0x00)(0x01)(0x02);

        RfnCommand::RfnResult rcv = lcdConfiguration.decode(execute_time, response);

        std::string exp = "Display metric : Metric Slot Disabled\n"
                          "Display metric : No Segments\n"
                          "Display metric : All Segments\n";

        BOOST_REQUIRE_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_all_items)
{
    const std::vector<unsigned char> metrics = boost::assign::list_of
            (0x00)(0x01)(0x02)(0x03)(0x04)(0x05)(0x06)(0x07)(0x08)
            (0x09)(0x0A)(0x0B)(0x0C)(0x0D)(0x0E)(0x0F)(0x10)(0x11)(0x12)(0x13)
            (0x14)(0x15)(0x16)(0x17)(0x18)(0x19)(0x1A)(0x1B)(0x1C)(0x1D)(0x1E)
            (0x1F)(0x20)(0x21)(0x22)(0x23)(0x24);

    RfnCentronLcdConfigurationCommand lcdConfiguration(metrics, false);

    // execute
    {
        RfnCommand::RfnRequest rcv = lcdConfiguration.execute(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x69)(0x00)(0x25)(0x00)(0x01)(0x02)(0x03)(0x04)(0x05)(0x06)(0x07)(0x08)
                (0x09)(0x0A)(0x0B)(0x0C)(0x0D)(0x0E)(0x0F)(0x10)(0x11)(0x12)(0x13)
                (0x14)(0x15)(0x16)(0x17)(0x18)(0x19)(0x1A)(0x1B)(0x1C)(0x1D)(0x1E)
                (0x1F)(0x20)(0x21)(0x22)(0x23)(0x24);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        std::vector<unsigned char> response = boost::assign::list_of
                (0x71)(0x25)(0x00)(0x01)(0x02)(0x03)(0x04)(0x05)(0x06)(0x07)(0x08)
                (0x09)(0x0A)(0x0B)(0x0C)(0x0D)(0x0E)(0x0F)(0x10)(0x11)(0x12)(0x13)
                (0x14)(0x15)(0x16)(0x17)(0x18)(0x19)(0x1A)(0x1B)(0x1C)(0x1D)(0x1E)
                (0x1F)(0x20)(0x21)(0x22)(0x23)(0x24);

        RfnCommand::RfnResult rcv = lcdConfiguration.decode(execute_time, response);

        std::string exp = "Display metric : Metric Slot Disabled\n"
                          "Display metric : No Segments\n"
                          "Display metric : All Segments\n"
                          "Display metric : Tamper\n"
                          "Display metric : Current Local Time\n"
                          "Display metric : Current Local Date\n"
                          "Display metric : Unidir kWh (Sum of Forward and Reverse)\n"
                          "Display metric : Net kWh (Difference of Forward and Reverse)\n"
                          "Display metric : Delivered kWh\n"
                          "Display metric : Received kWh\n"
                          "Display metric : Last Interval Demand (W)\n"
                          "Display metric : Peak Demand (W)\n"
                          "Display metric : Date of Peak Demand (W)\n"
                          "Display metric : Time of Peak Demand (W)\n"
                          "Display metric : Last Interval Voltage (V)\n"
                          "Display metric : Peak Voltage (V)\n"
                          "Display metric : Date of Peak Voltage\n"
                          "Display metric : Time of Peak Voltage\n"
                          "Display metric : Min Voltage (V)\n"
                          "Display metric : Date of Min Voltage\n"
                          "Display metric : Time of Min Voltage\n"
                          "Display metric : TOU Rate A kWh\n"
                          "Display metric : TOU Rate A Peak Demand (W)\n"
                          "Display metric : TOU Rate A Date of Peak Demand\n"
                          "Display metric : TOU Rate A Time of Peak Demand\n"
                          "Display metric : TOU Rate B kWh\n"
                          "Display metric : TOU Rate B Peak Demand (W)\n"
                          "Display metric : TOU Rate B Date of Peak Demand\n"
                          "Display metric : TOU Rate B Time of Peak Demand\n"
                          "Display metric : TOU Rate C kWh\n"
                          "Display metric : TOU Rate C Peak Demand (W)\n"
                          "Display metric : TOU Rate C Date of Peak Demand\n"
                          "Display metric : TOU Rate C Time of Peak Demand\n"
                          "Display metric : TOU Rate D kWh\n"
                          "Display metric : TOU Rate D Peak Demand (W)\n"
                          "Display metric : TOU Rate D Date of Peak Demand\n"
                          "Display metric : TOU Rate D Time of Peak Demand\n";

        BOOST_REQUIRE_EQUAL(rcv.description, exp);
    }
}

BOOST_AUTO_TEST_SUITE_END()
