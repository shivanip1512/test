#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"

#include "cmd_rfn_FocusLcdConfiguration.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnFocusLcdConfigurationCommand;

typedef RfnFocusLcdConfigurationCommand::DisplayItem       DisplayItem;
typedef RfnFocusLcdConfigurationCommand::DisplayItemVector DisplayItemVector;

namespace boost {
namespace test_tools {
bool operator!=(const DisplayItem &lhs, const DisplayItem &rhs)
{
    return (lhs.metric != rhs.metric || lhs.alphamericId != rhs.alphamericId);
}
}
}

namespace std {
ostream& operator<<(ostream& out, const DisplayItem &item)
{
    out << item.metric << " " << item.alphamericId;
    return out;
}
}

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_FocusLcdConfiguration )

DisplayItem makeDisplayItem( const std::string &metric, const std::string &alphamericId )
{
    DisplayItem item = { metric, alphamericId };
    return item;
}

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

BOOST_AUTO_TEST_CASE(test_no_items)
{
    const unsigned char display_item_duration = 10;
    const RfnFocusLcdConfigurationCommand::DisplayItemVector display_items;

    RfnFocusLcdConfigurationCommand lcdConfiguration(display_items, display_item_duration);

    // execute
    {
        RfnCommand::RfnRequest rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x00) // command code - operation
                (0x00)       // nbr of items
                (0x0a);      // duration

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)       // command code
                (0x00)       // nbr of items
                (0x0a);      // duration

        RfnCommand::RfnResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Display item duration : 10 seconds\n"
                          "No display metrics";

        BOOST_CHECK_EQUAL(rcv.description, exp);
        BOOST_CHECK_EQUAL(lcdConfiguration.getDisplayItemDurationReceived(), display_item_duration);

        boost::optional<DisplayItemVector> display_items_received = lcdConfiguration.getDisplayItemsReceived();

        BOOST_REQUIRE( display_items_received );

        BOOST_CHECK_EQUAL_COLLECTIONS(
                 display_items_received->begin(), display_items_received->end(),
                 display_items.begin(),           display_items.end());
    }
}

BOOST_AUTO_TEST_CASE(test_3_items)
{
    const unsigned char display_item_duration = 21;
    const RfnFocusLcdConfigurationCommand::DisplayItemVector display_items = boost::assign::list_of
            (makeDisplayItem("deliveredKwh6x1", "AB"))
            (makeDisplayItem("deliveredKwh5x1", "CD"))
            (makeDisplayItem("deliveredKwh4x1", "EF"));

    RfnFocusLcdConfigurationCommand lcdConfiguration(display_items, display_item_duration);

    // execute
    {
        RfnCommand::RfnRequest rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x00)        // command code - operation
                (0x03)              // nbr of items
                (0x15)              // duration
                (0x00)(0x41)(0x42)  // item 1
                (0x01)(0x43)(0x44)  // item 2
                (0x02)(0x45)(0x46); // item 3

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)              // command code
                (0x03)              // nbr of items
                (0x15)              // duration
                (0x00)(0x41)(0x42)  // item 1
                (0x01)(0x43)(0x44)  // item 2
                (0x02)(0x45)(0x46); // item 3

        RfnCommand::RfnResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Display item duration : 21 seconds\n"
                          "Display metric 1 : Delivered +kWh (6 digit)\n"
                          "- ID code : \"AB\"\n"
                          "Display metric 2 : Delivered +kWh (5 digit)\n"
                          "- ID code : \"CD\"\n"
                          "Display metric 3 : Delivered +kWh (4 digit)\n"
                          "- ID code : \"EF\"\n";

        BOOST_CHECK_EQUAL(rcv.description, exp);
        BOOST_CHECK_EQUAL(lcdConfiguration.getDisplayItemDurationReceived(), display_item_duration);

        boost::optional<DisplayItemVector> display_items_received = lcdConfiguration.getDisplayItemsReceived();

        BOOST_REQUIRE( display_items_received );

        BOOST_CHECK_EQUAL_COLLECTIONS(
                 display_items_received->begin(), display_items_received->end(),
                 display_items.begin(),           display_items.end());
    }
}

BOOST_AUTO_TEST_CASE(test_3_items_readonly)
{
    RfnFocusLcdConfigurationCommand lcdConfiguration;

    // execute
    {
        RfnCommand::RfnRequest rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x01);

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        const unsigned char display_item_duration = 21;
        const RfnFocusLcdConfigurationCommand::DisplayItemVector display_items = boost::assign::list_of
                (makeDisplayItem("deliveredKwh6x1", "AB"))
                (makeDisplayItem("deliveredKwh5x1", "CD"))
                (makeDisplayItem("deliveredKwh4x1", "EF"));

        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)              // command code
                (0x03)              // nbr of items
                (0x15)              // duration
                (0x00)(0x41)(0x42)  // item 1
                (0x01)(0x43)(0x44)  // item 2
                (0x02)(0x45)(0x46); // item 3

        RfnCommand::RfnResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Display item duration : 21 seconds\n"
                          "Display metric 1 : Delivered +kWh (6 digit)\n"
                          "- ID code : \"AB\"\n"
                          "Display metric 2 : Delivered +kWh (5 digit)\n"
                          "- ID code : \"CD\"\n"
                          "Display metric 3 : Delivered +kWh (4 digit)\n"
                          "- ID code : \"EF\"\n";

        BOOST_CHECK_EQUAL(rcv.description, exp);
        BOOST_CHECK_EQUAL(lcdConfiguration.getDisplayItemDurationReceived(), display_item_duration);

        boost::optional<DisplayItemVector> display_items_received = lcdConfiguration.getDisplayItemsReceived();

        BOOST_REQUIRE( display_items_received );

        BOOST_CHECK_EQUAL_COLLECTIONS(
                 display_items_received->begin(), display_items_received->end(),
                 display_items.begin(),           display_items.end());
    }
}

BOOST_AUTO_TEST_CASE(test_10_items)
{
    const unsigned char display_item_duration = 7;
    const RfnFocusLcdConfigurationCommand::DisplayItemVector display_items = boost::assign::list_of
            (makeDisplayItem("firmwareVersion", "01"))
            (makeDisplayItem("deliveredKwh5x1", "89"))
            (makeDisplayItem("netKwh4x1",       "$*"))
            (makeDisplayItem("deliveredKwh6x1", " ="))
            (makeDisplayItem("receivedKwh6x1",  ">+"))
            (makeDisplayItem("totalKwh4x1",     "-A"))
            (makeDisplayItem("deliveredKwh6x1", "BC"))
            (makeDisplayItem("totalKwh4x10",    "37"))
            (makeDisplayItem("diagnosticFlags", "T8"))
            (makeDisplayItem("allSegments",     "YZ"));

    RfnFocusLcdConfigurationCommand lcdConfiguration(display_items, display_item_duration);

    // execute
    {
        RfnCommand::RfnRequest rcv = lcdConfiguration.executeCommand(execute_time);

        std::vector<unsigned char> exp = boost::assign::list_of
                (0x60)(0x00)           // command code - operation
                (0x0a)                 // nbr of items
                (0x07)                 // duration
                (0x12)(0x30)(0x31)     // item 1
                (0x01)(0x38)(0x39)     // item 2
                (0x0e)(0x3a)(0x3b)     // item 3
                (0x00)(0x3c)(0x3d)     // item 4
                (0x04)(0x3e)(0x3f)     // item 5
                (0x0a)(0x40)(0x41)     // item 6
                (0x00)(0x42)(0x43)     // item 7
                (0x0b)(0x33)(0x37)     // item 8
                (0x10)(0x54)(0x38)     // item 9
                (0x11)(0x59)(0x5a);    // item 10

        BOOST_CHECK_EQUAL_COLLECTIONS(
                rcv.begin(), rcv.end(),
                exp.begin(), exp.end());
    }

    // decode
    {
        std::vector<unsigned char> response = boost::assign::list_of
                (0x61)                 // command code
                (0x0a)                 // nbr of items
                (0x07)                 // duration
                (0x12)(0x30)(0x31)     // item 1
                (0x01)(0x38)(0x39)     // item 2
                (0x0e)(0x3a)(0x3b)     // item 3
                (0x00)(0x3c)(0x3d)     // item 4
                (0x04)(0x3e)(0x3f)     // item 5
                (0x0a)(0x40)(0x41)     // item 6
                (0x00)(0x42)(0x43)     // item 7
                (0x0b)(0x33)(0x37)     // item 8
                (0x10)(0x54)(0x38)     // item 9
                (0x11)(0x59)(0x5a);    // item 10

        RfnCommand::RfnResult rcv = lcdConfiguration.decodeCommand(execute_time, response);

        std::string exp = "Display item duration : 7 seconds\n"
                          "Display metric 1 : Firmware version\n"
                          "- ID code : \"01\"\n"
                          "Display metric 2 : Delivered +kWh (5 digit)\n"
                          "- ID code : \"89\"\n"
                          "Display metric 3 : Net kWh (4 digit)\n"
                          "- ID code : \"$*\"\n"
                          "Display metric 4 : Delivered +kWh (6 digit)\n"
                          "- ID code : \" =\"\n"
                          "Display metric 5 : Received +kWh (6 digit)\n"
                          "- ID code : \">+\"\n"
                          "Display metric 6 : Total kWh (4 digit)\n"
                          "- ID code : \"-A\"\n"
                          "Display metric 7 : Delivered +kWh (6 digit)\n"
                          "- ID code : \"BC\"\n"
                          "Display metric 8 : Total kWh (4 x 10  digit)\n"
                          "- ID code : \"37\"\n"
                          "Display metric 9 : Diagnostic flags\n"
                          "- ID code : \"T8\"\n"
                          "Display metric 10 : All Segments\n"
                          "- ID code : \"YZ\"\n";

        BOOST_CHECK_EQUAL(rcv.description, exp);
        BOOST_CHECK_EQUAL(lcdConfiguration.getDisplayItemDurationReceived(), display_item_duration);

        boost::optional<DisplayItemVector> display_items_received = lcdConfiguration.getDisplayItemsReceived();

        BOOST_REQUIRE( display_items_received );

        BOOST_CHECK_EQUAL_COLLECTIONS(
                 display_items_received->begin(), display_items_received->end(),
                 display_items.begin(),           display_items.end());
    }
}

BOOST_AUTO_TEST_SUITE_END()
