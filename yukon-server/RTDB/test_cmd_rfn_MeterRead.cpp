#include <boost/test/unit_test.hpp>

#include "cmd_rfn_MeterRead.h"

#include "ctidate.h"

#include "std_helper.h"
#include "boost_test_helpers.h"

using namespace Cti::Devices::Commands;
using Cti::Test::parseIso8601String;

using ModifierSet = std::set<std::string>;

namespace Cti {
    std::ostream& operator<<(std::ostream& os, const RfnIdentifier rfnId);
}
namespace Cti::Messaging::Rfn {
    std::ostream& operator<<(std::ostream& os, const RfnMeterReadingDataReplyType type) {
        return os << "[RfnMeterReadingDataReplyType " << static_cast<int>(type) << "]";
    }
    std::ostream& operator<<(std::ostream& os, const ChannelDataStatus cds) {
        return os << "[ChannelDataStatus " << static_cast<int>(cds) << "]";
    }
}
namespace std {
    ostream& operator<<(ostream& os, const ModifierSet& modifiers) {
        return Cti::Logging::Set::operator<<(os, modifiers);
    }
}

namespace {
    struct test_state {
        const decltype(Cti::Test::set_to_central_timezone()) overrideTimezone = Cti::Test::set_to_central_timezone();

        const CtiTime execute_time = parseIso8601String("2010-02-17 15:00:00");
        const CtiTime decode_time  = parseIso8601String("2010-02-17 16:00:00");
    };
}

BOOST_FIXTURE_TEST_SUITE(test_cmd_rfn_MeterRead, test_state)

/**
*   Format 1 (response type 0x02) test cases
*/

BOOST_AUTO_TEST_CASE(test_read_fmt1_fail)
{
    RfnMeterReadCommand command(11235);

    try
    {
        const std::vector< unsigned char > expected_out{
            0x01 };

        BOOST_CHECK_EQUAL_RANGES(expected_out, command.executeCommand(execute_time));

        const std::vector< unsigned char > response{
            0x02, //  Response type 2, no modifiers
            0xff  //  Response status (nonzero is unknown/fail)
        };

        RfnCommandResult result = command.decodeCommand(decode_time, response);

        BOOST_FAIL("Did not throw");
    }
    catch( const Cti::YukonErrorException& ex )
    {
        BOOST_CHECK_EQUAL(ex.error_description, "RFN meter read status is not OK: 255");
        BOOST_CHECK_EQUAL(ex.error_code, ClientErrors::InvalidData);
    }
}

BOOST_AUTO_TEST_CASE(test_read_fmt1_channel_error)
{
    RfnMeterReadCommand command(11235);

    const std::vector<unsigned char> expected_out {
        0x01 };

    BOOST_CHECK_EQUAL_RANGES(expected_out, command.executeCommand(execute_time));

    const std::vector< unsigned char > response{
        0x02, //  Response type 2, no modifiers
        0x00, //  Response status (OK)
        0x01, //  Number of channels in response
            0x17, //  Channel number
            0x05, //  Unit of measure
            0x00, 0x00, 0x00, 0x01, //  Data
            0x02  //  Status (Busy)
    };

    RfnCommandResult result = command.decodeCommand(decode_time, response);

    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0"
        "\nTimestamp       : 02/17/2010 16:00:00"
        "\n# Channel data  : 0"
        "\n# Dated data    : 0");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());
}

BOOST_AUTO_TEST_CASE(test_read_fmt1_ok)
{
    RfnMeterReadCommand command(11235);

    const std::vector<unsigned char> expected_out {
        0x01 };

    BOOST_CHECK_EQUAL_RANGES(expected_out, command.executeCommand(execute_time));

    const std::vector< unsigned char > response{
        0x02, //  Response type 2, no modifiers
        0x00, //  Response status (OK)
        0x01, //  Number of channels in response
            0x17, //  Channel number
            0x05, //  Unit of measure
            0x00, 0x00, 0x00, 0x01, //  Data
            0x00  //  Status (OK)
    };

    RfnCommandResult result = command.decodeCommand(decode_time, response);

    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0"
        "\nTimestamp       : 02/17/2010 16:00:00"
        "\n# Channel data  : 0"
        "\n# Dated data    : 0");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());
}

/**
*   Format 2/3 (response type 0x03) test cases
*/

BOOST_AUTO_TEST_CASE(test_read_fmt23_channel_error)
{
    RfnMeterReadCommand command(11235);

    const std::vector<unsigned char> expected_out {
        0x01 };

    BOOST_CHECK_EQUAL_RANGES(expected_out, command.executeCommand(execute_time));

    const std::vector< unsigned char > response{
        0x03, //  Response type 3, contains one or more modifiers
        0x00, //  Response status (OK)
        0x01, //  Number of channels in response
            0x17, //  Channel number
            0x01, //  Unit of measure (Watts)
            0x80, 0x00, //  Modifier 1, has extension bit set
            0x00, 0x00, //  Modifier 2, no extension bit
            0x00, 0x00, 0x00, 0x2a, //  Data
            0x02  //  Status (Busy)
    };

    RfnCommandResult result = command.decodeCommand(decode_time, response);

    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0"
        "\nTimestamp       : 02/17/2010 16:00:00"
        "\n# Channel data  : 1"
        "\nChannel data 0  : Channel number : 23"
        "\n                  Status         : 2"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {<empty>}"
        "\n                  Value          : 42"
        "\n# Dated data    : 0");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());
}

BOOST_AUTO_TEST_CASE(test_read_fmt23_excess_modifier)
{
    RfnMeterReadCommand command(11235);

    try
    {
        const std::vector< unsigned char > expected_out{
            0x01 };

        BOOST_CHECK_EQUAL_RANGES(expected_out, command.executeCommand(execute_time));

        const std::vector< unsigned char > response{
            0x03, //  Response type 3, contains one or more modifiers
            0x00, //  Response status (OK)
            0x01, //  Number of channels in response
                0x17, //  Channel number
                0x01, //  Unit of measure (Watts)
                0x80, 0x00, //  Modifier 1, has extension bit set
                0x80, 0x00, //  Modifier 2, has extension bit set
                0x00, 0x00, //  Modifier 3, no extension bit
                0x00, 0x00, 0x00, 0x2a, //  Data
                0x00  //  Status (OK)
        };

        RfnCommandResult result = command.decodeCommand(decode_time, response);

        BOOST_FAIL("Did not throw");
    }
    catch( const Cti::YukonErrorException& ex )
    {
        BOOST_CHECK_EQUAL(ex.error_description, "RFN meter read response channel includes a third modifier, cannot decode");
        BOOST_CHECK_EQUAL(ex.error_code, ClientErrors::InvalidData);
    }
}

BOOST_AUTO_TEST_CASE(test_read_fmt23_single_channel)
{
    RfnMeterReadCommand command(11235);

    const std::vector< unsigned char > expected_out{
        0x01 };

    BOOST_CHECK_EQUAL_RANGES(expected_out, command.executeCommand(execute_time));

    const std::vector< unsigned char > response{
        0x03, //  Response type 3, contains one or more modifiers
        0x00, //  Response status (OK)
        0x01, //  Number of channels in response
            0x17, //  Channel number
            0x01, //  Unit of measure (Watts)
            0x80, 0x00, //  Modifier 1, has extension bit set
            0x00, 0x00, //  Modifier 2, no extension bit
            0x00, 0x00, 0x00, 0x2a, //  Data
            0x00  //  Status (OK)
    };

    RfnCommandResult result = command.decodeCommand(decode_time, response);

    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0"
        "\nTimestamp       : 02/17/2010 16:00:00"
        "\n# Channel data  : 1"
        "\nChannel data 0  : Channel number : 23"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {<empty>}"
        "\n                  Value          : 42"
        "\n# Dated data    : 0");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());
}

BOOST_AUTO_TEST_CASE(test_read_fmt23_multiple_channels)
{
    RfnMeterReadCommand command(11235);

    const std::vector< unsigned char > expected_out{
        0x01 };

    BOOST_CHECK_EQUAL_RANGES(expected_out, command.executeCommand(execute_time));

    const std::vector< unsigned char > response{
        0x03, //  Response type 3, contains one or more modifiers
        0x00, //  Response status (OK)
        0x02, //  Number of channels in response
            0x17, //  Channel number
            0x01, //  Unit of measure (Watth)
            0x80, 0x00, //  Modifier 1, has extension bit set
            0x00, 0x00, //  Modifier 2, no extension bit
            0x00, 0x00, 0x00, 0x2a, //  Data
            0x00, //  Status (OK)

            0x18, //  Channel number
            0x02, //  Unit of measure (Varh)
            0x80, 0x00, //  Modifier 1, has extension bit set
            0x00, 0x00, //  Modifier 2, no extension bit
            0x00, 0x00, 0x00, 0x15, //  Data
            0x00  //  Status (OK)
    };

    RfnCommandResult result = command.decodeCommand(decode_time, response);

    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0"
        "\nTimestamp       : 02/17/2010 16:00:00"
        "\n# Channel data  : 2"
        "\nChannel data 0  : Channel number : 23"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {<empty>}"
        "\n                  Value          : 42"
        "\nChannel data 1  : Channel number : 24"
        "\n                  Status         : 0"
        "\n                  UOM            : Varh"
        "\n                  Modifiers      : {<empty>}"
        "\n                  Value          : 21"
        "\n# Dated data    : 0");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());
}

BOOST_AUTO_TEST_CASE(test_read_fmt23_multiple_channels_with_time)
{
    RfnMeterReadCommand command(11235);

    const std::vector<unsigned char> expected_out{
        0x01 };

    BOOST_CHECK_EQUAL_RANGES(expected_out, command.executeCommand(execute_time));

    const std::vector< unsigned char > response{
        0x03, //  Response type 3, contains one or more modifiers
        0x00, //  Response status (OK)
        0x06, //  Number of channels in response
            0x17, //  Channel number
            0x01, //  Unit of measure (Wh)
            0x80, 0x00, //  Modifier 1, has extension bit set
            0x00, 0x00, //  Modifier 2, no extension bit
            0x00, 0x00, 0x00, 0x2a, //  Data
            0x00, //  Status (OK)

            0x18, //  Channel number
            0x02, //  Unit of measure (Varh)
            0x80, 0x00, //  Modifier 1, has extension bit set
            0x00, 0x00, //  Modifier 2, no extension bit
            0x00, 0x00, 0x00, 0x15, //  Data
            0x00, //  Status (OK)

            0x19, //  Channel number
            0x41, //  Unit of measure (W)
            0xc0, 0x00, //  Modifier 1, has extension bit set, Max
            0x00, 0x00, //  Modifier 2, no extension bit
            0x00, 0x00, 0x00, 0x2a, //  Data
            0x00, //  Status (OK)

            0x1a, //  Channel number
            0x05, //  Unit of measure (s)
            0x80, 0x00, //  Modifier 1, has extension bit set
            0x00, 0x00, //  Modifier 2, no extension bit
            0x60, 0x14, 0x6c, 0xfc, //  Data  0x60146cfc - Fri, 29 Jan 2021 20:15:56 GMT
            0x00, //  Status (OK)

            0x1a, //  Channel number
            0x18, //  Unit of measure (Power Factor)
            0xc0, 0x00, //  Modifier 1, has extension bit set, Max
            0x00, 0x00, //  Modifier 2, no extension bit
            0x00, 0x00, 0x00, 0x2a, //  Data
            0x00, //  Status (OK)

            0x1b, //  Channel number
            0x44, //  Unit of measure (VA)
            0xc0, 0x00, //  Modifier 1, has extension bit set, Max
            0x00, 0x00, //  Modifier 2, no extension bit
            0x00, 0x00, 0x01, 0x54, //  Data
            0x02, //  Status (FAILURE)
    };

    RfnCommandResult result = command.decodeCommand(decode_time, response);

    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0"
        "\nTimestamp       : 02/17/2010 16:00:00"
        "\n# Channel data  : 4"
        "\nChannel data 0  : Channel number : 23"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {<empty>}"
        "\n                  Value          : 42"
        "\nChannel data 1  : Channel number : 24"
        "\n                  Status         : 0"
        "\n                  UOM            : Varh"
        "\n                  Modifiers      : {<empty>}"
        "\n                  Value          : 21"
        "\nChannel data 2  : Channel number : 26"
        "\n                  Status         : 0"
        "\n                  UOM            : PF"
        "\n                  Modifiers      : {Max}"
        "\n                  Value          : 42"
        "\nChannel data 3  : Channel number : 27"
        "\n                  Status         : 2"
        "\n                  UOM            : VA"
        "\n                  Modifiers      : {Max}"
        "\n                  Value          : 340"
        "\n# Dated data    : 1"
        "\nDated data 0    : Channel number : 25"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 01/29/2021 14:15:56"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max}"
        "\n                  Value          : 42"
        "\n                  Base channel   : (none)");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();

    BOOST_REQUIRE(responseMsg);

    using rt = Cti::Messaging::Rfn::RfnMeterReadingDataReplyType;

    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::OK);

    const auto& data = responseMsg->data;

    BOOST_CHECK_EQUAL(data.timeStamp, decode_time);
    BOOST_CHECK_EQUAL(data.rfnIdentifier, Cti::RfnIdentifier());
    BOOST_CHECK_EQUAL(data.recordInterval, 0);

    const auto& channelDataList = data.channelDataList;

    BOOST_REQUIRE_EQUAL(channelDataList.size(), 4);

    using cds = Cti::Messaging::Rfn::ChannelDataStatus;
    {
        const auto& channelData = channelDataList[0];
        BOOST_CHECK_EQUAL(channelData.channelNumber, 23);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers.empty(), true);
        BOOST_CHECK_EQUAL(channelData.value, 42);
    }
    {
        const auto& channelData = channelDataList[1];
        BOOST_CHECK_EQUAL(channelData.channelNumber, 24);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Varh");
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers.empty(), true);
        BOOST_CHECK_EQUAL(channelData.value, 21);
    }
    {
        const auto& channelData = channelDataList[2];
        BOOST_CHECK_EQUAL(channelData.channelNumber, 26);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "PF");
        const ModifierSet expectedModifiers { "Max" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 42);
    }
    {
        const auto& channelData = channelDataList[3];
        BOOST_CHECK_EQUAL(channelData.channelNumber, 27);
        BOOST_CHECK_EQUAL(channelData.status, cds::PARTIAL_READ_FAILURE);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "VA");
        const ModifierSet expectedModifiers{ "Max" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 340);
    }

    const auto& datedChannelDataList = data.datedChannelDataList;

    BOOST_REQUIRE_EQUAL(datedChannelDataList.size(), 1);

    {
        const auto& datedChannelData = datedChannelDataList[0];

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 25);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 42);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, CtiTime(0x60146cfc));

        BOOST_CHECK_EQUAL(datedChannelData.baseChannelData.has_value(), false);
    }
}

BOOST_AUTO_TEST_CASE(test_read_fmt23_multiple_channels_with_coincident)
{
    RfnMeterReadCommand command(11235);

    const std::vector<unsigned char> expected_out {
        0x01 };

    BOOST_CHECK_EQUAL_RANGES(expected_out, command.executeCommand(execute_time));

    const std::vector< unsigned char > response{
        0x03, //  Response type 3, contains one or more modifiers
        0x00, //  Response status (OK)
        0x03, //  Number of channels in response
            0x19, //  Channel number
            0x41, //  Unit of measure (W)
            0xc0, 0x00, //  Modifier 1, has extension bit set, Max
            0x00, 0x00, //  Modifier 2, no extension bit
            0x00, 0x00, 0x00, 0x2a, //  Data
            0x00, //  Status (OK)

            0x1a, //  Channel number
            0x05, //  Unit of measure (s)
            0x00, 0x00, //  Modifier 1, no extension bit
            0x60, 0x14, 0x6c, 0xfc, //  Data  0x60146cfc - Fri, 29 Jan 2021 20:15:56 GMT
            0x00, //  Status (OK)

            0x1a, //  Channel number
            0x18, //  Unit of measure (Power Factor)
            0x80, 0x90, //  Modifier 1, Quad 1, Quad 4, no extension bit
            0x02, 0x00, //  Modifier 2, Coincident 1, no extension bit
            0x00, 0x00, 0x00, 0x2a, //  Data
            0x00, //  Status (OK)
    };

    RfnCommandResult result = command.decodeCommand(decode_time, response);

    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0"
        "\nTimestamp       : 02/17/2010 16:00:00"
        "\n# Channel data  : 0"
        "\n# Dated data    : 2"
        "\nDated data 0    : Channel number : 25"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 01/29/2021 14:15:56"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max}"
        "\n                  Value          : 42"
        "\n                  Base channel   : (none)"
        "\nDated data 1    : Channel number : 26"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 01/29/2021 14:15:56"
        "\n                  UOM            : PF"
        "\n                  Modifiers      : {Quadrant 1,Quadrant 4}"
        "\n                  Value          : 42"
        "\n                  Base channel   : Channel number : 25"
        "\n                                   Status         : 0"
        "\n                                   UOM            : W"
        "\n                                   Modifiers      : {Max}"
        "\n                                   Value          : 42");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();

    BOOST_REQUIRE(responseMsg);

    using rt = Cti::Messaging::Rfn::RfnMeterReadingDataReplyType;

    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::OK);

    const auto& data = responseMsg->data;

    BOOST_CHECK_EQUAL(data.timeStamp, decode_time);
    BOOST_CHECK_EQUAL(data.rfnIdentifier, Cti::RfnIdentifier());
    BOOST_CHECK_EQUAL(data.recordInterval, 0);

    const auto& channelDataList = data.channelDataList;

    BOOST_CHECK_EQUAL(channelDataList.size(), 0);

    using cds = Cti::Messaging::Rfn::ChannelDataStatus;

    const auto& datedChannelDataList = data.datedChannelDataList;

    BOOST_REQUIRE_EQUAL(datedChannelDataList.size(), 2);

    {
        const auto& datedChannelData = datedChannelDataList[0];

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 25);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 42);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, CtiTime(0x60146cfc));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = datedChannelDataList[1];

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 26);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "PF");
        const ModifierSet expectedModifiers { "Quadrant 1", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 42);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, CtiTime(0x60146cfc));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_REQUIRE(baseChannelData);
        BOOST_CHECK_EQUAL(baseChannelData->channelNumber, 25);
        BOOST_CHECK_EQUAL(baseChannelData->status, cds::OK);
        BOOST_CHECK_EQUAL(baseChannelData->unitOfMeasure, "W");
        const auto expectedBaseModifiers = { "Max" };
        BOOST_CHECK_EQUAL_RANGES(baseChannelData->unitOfMeasureModifiers, expectedBaseModifiers);
        BOOST_CHECK_EQUAL(baseChannelData->value, 42);
    }
}

BOOST_AUTO_TEST_CASE(test_read_itrn_c2sx)
{
    RfnMeterReadCommand command(11235);

    const std::vector<unsigned char> expected_out{
        0x01 };

    BOOST_CHECK_EQUAL_RANGES(expected_out, command.executeCommand(execute_time));

    Cti::Test::byte_str response =
        "03"
        " 00"
        " 08"
            " 01"  " 81"  " 00 90"            " 00 00 2c 14"  " 00"
            " 02"  " 41"  " 00 90"            " 00 00 00 00"  " 00"
            " 03"  " 41"  " 40 90"            " 00 00 00 00"  " 00"
            " 04"  " 05"  " 80 00"  " 02 00"  " 60 2d 82 a4"  " 00"
            " 05"  " 41"  " c0 90"  " 40 00"  " 00 00 00 00"  " 00"
            " 06"  " 05"  " 80 00"  " 42 00"  " 60 17 8a d0"  " 00"
            " 07"  " 41"  " 60 90"            " 00 00 00 00"  " 00"
            " 08"  " 05"  " 80 00"  " 02 00"  " 60 2d 83 c3"  " 00";

    RfnCommandResult result = command.decodeCommand(decode_time, response.bytes);

    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0"
        "\nTimestamp       : 02/17/2010 16:00:00"
        "\n# Channel data  : 2"
        "\nChannel data 0  : Channel number : 1"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Quadrant 1,Quadrant 4}"
        "\n                  Value          : 11284"
        "\nChannel data 1  : Channel number : 2"
        "\n                  Status         : 0"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Quadrant 1,Quadrant 4}"
        "\n                  Value          : 0"
        "\n# Dated data    : 3"
        "\nDated data 0    : Channel number : 3"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 02/17/2021 14:55:00"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max,Quadrant 1,Quadrant 4}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)"
        "\nDated data 1    : Channel number : 5"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 01/31/2021 23:00:00"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max,Previous,Quadrant 1,Quadrant 4}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)"
        "\nDated data 2    : Channel number : 7"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 02/17/2021 14:59:47"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Daily Max,Quadrant 1,Quadrant 4}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();

    BOOST_REQUIRE(responseMsg);

    using rt = Cti::Messaging::Rfn::RfnMeterReadingDataReplyType;

    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::OK);

    const auto& data = responseMsg->data;

    BOOST_CHECK_EQUAL(data.timeStamp, decode_time);
    BOOST_CHECK_EQUAL(data.rfnIdentifier, Cti::RfnIdentifier());
    BOOST_CHECK_EQUAL(data.recordInterval, 0);

    using cds = Cti::Messaging::Rfn::ChannelDataStatus;

    const auto& channelDataList = data.channelDataList;

    BOOST_REQUIRE_EQUAL(channelDataList.size(), 2);
    {
        const auto& channelData = channelDataList[0];
        BOOST_CHECK_EQUAL(channelData.channelNumber, 1);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Quadrant 1", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 11284);
    }
    {
        const auto& channelData = channelDataList[1];
        BOOST_CHECK_EQUAL(channelData.channelNumber, 2);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Quadrant 1", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);
    }

    const auto& datedChannelDataList = data.datedChannelDataList;

    BOOST_REQUIRE_EQUAL(datedChannelDataList.size(), 3);
    {
        const auto& datedChannelData = datedChannelDataList[0];

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 3);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max", "Quadrant 1", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-02-17 14:55:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = datedChannelDataList[1];

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 5);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max", "Previous", "Quadrant 1", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-01-31 23:00:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = datedChannelDataList[2];

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 7);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Daily Max", "Quadrant 1", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-02-17 14:59:47"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
}

BOOST_AUTO_TEST_CASE(test_read_lgyr_focus_axd_sd_500)
{
    RfnMeterReadCommand command(11235);

    const std::vector<unsigned char> expected_out{
        0x01 };

    BOOST_CHECK_EQUAL_RANGES(expected_out, command.executeCommand(execute_time));

    Cti::Test::byte_str response =
        "03"
        " 00"
        " 28"
            " 01"  " 81"  " 00 90"            " 00 00 0b b9"  " 00"
            " 02"  " 81"  " 00 60"            " 00 00 00 00"  " 00"
            " 03"  " 81"  " 00 f0"            " 00 00 0b b9"  " 00"
            " 04"  " 81"  " 10 f0"            " 00 00 0b b9"  " 00"
            " 05"  " c1"  " 00 90"            " 00 00 00 00"  " 00"
            " 06"  " c1"  " 40 90"            " 00 00 00 00"  " 00"
            " 07"  " 85"  " 80 00"  " 02 00"  " 60 2d 9c 6c"  " 00"
            " 08"  " c1"  " c0 90"  " 40 00"  " 00 00 00 00"  " 00"
            " 09"  " 85"  " 80 00"  " 42 00"  " 60 17 8a d0"  " 00"
            " 0a"  " c1"  " 60 90"            " 00 00 00 00"  " 00"
            " 0b"  " 85"  " 80 00"  " 02 00"  " 60 2d 9e e5"  " 00"
            " 0c"  " 90"  " 80 00"  " 01 c0"  " 00 03 9a c7"  " 00"
            " 0d"  " 90"  " e0 00"  " 01 c0"  " 00 03 b7 5c"  " 00"
            " 0e"  " 85"  " 80 00"  " 02 00"  " 60 2d 5f 04"  " 00"
            " 0f"  " 90"  " f0 00"  " 01 c0"  " 00 03 94 13"  " 00"
            " 10"  " 85"  " 80 00"  " 02 00"  " 60 2c fa 00"  " 00"
            " 11"  " 81"  " 80 90"  " 00 08"  " 00 00 00 b1"  " 00"
            " 12"  " 81"  " 90 f0"  " 00 08"  " 00 00 00 b1"  " 00"
            " 13"  " c1"  " c0 90"  " 00 08"  " 00 00 00 00"  " 00"
            " 14"  " 85"  " 80 00"  " 02 08"  " 60 2d 9c 6c"  " 00"
            " 15"  " c1"  " c0 90"  " 40 08"  " 00 00 00 00"  " 00"
            " 16"  " 85"  " 80 00"  " 42 08"  " 60 17 8a d0"  " 00"
            " 17"  " 81"  " 80 90"  " 00 10"  " 00 00 00 00"  " 00"
            " 18"  " 81"  " 90 f0"  " 00 10"  " 00 00 00 00"  " 00"
            " 19"  " c1"  " c0 90"  " 00 10"  " 00 00 00 00"  " 00"
            " 1a"  " 85"  " 80 00"  " 02 10"  " 60 2d 1d dc"  " 00"
            " 1b"  " c1"  " c0 90"  " 40 10"  " 00 00 00 00"  " 00"
            " 1c"  " 85"  " 80 00"  " 42 10"  " 60 16 b4 5c"  " 00"
            " 1d"  " 81"  " 80 90"  " 00 18"  " 00 00 00 00"  " 00"
            " 1e"  " 81"  " 90 f0"  " 00 18"  " 00 00 00 00"  " 00"
            " 1f"  " c1"  " c0 90"  " 00 18"  " 00 00 00 00"  " 00"
            " 20"  " 85"  " 80 00"  " 02 18"  " 60 2d 2b ec"  " 00"
            " 21"  " c1"  " c0 90"  " 40 18"  " 00 00 00 00"  " 00"
            " 22"  " 85"  " 80 00"  " 42 18"  " 60 16 c2 6c"  " 00"
            " 23"  " 81"  " 80 90"  " 00 20"  " 00 00 00 00"  " 00"
            " 24"  " 81"  " 90 f0"  " 00 20"  " 00 00 00 00"  " 00"
            " 25"  " c1"  " c0 90"  " 00 20"  " 00 00 00 00"  " 00"
            " 26"  " 85"  " 80 00"  " 02 20"  " 60 2d 39 fc"  " 00"
            " 27"  " c1"  " c0 90"  " 40 20"  " 00 00 00 00"  " 00"
            " 28"  " 85"  " 80 00"  " 42 20"  " 60 16 d0 7c"  " 00";

    RfnCommandResult result = command.decodeCommand(decode_time, response.bytes);

    BOOST_CHECK_EQUAL(result.description,
        "User message ID : 11235"
        "\nReply type      : 0"
        "\nTimestamp       : 02/17/2010 16:00:00"
        "\n# Channel data  : 14"
        "\nChannel data 0  : Channel number : 1"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Quadrant 1,Quadrant 4}"
        "\n                  Value          : 3001"
        "\nChannel data 1  : Channel number : 2"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Quadrant 2,Quadrant 3}"
        "\n                  Value          : 0"
        "\nChannel data 2  : Channel number : 3"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Quadrant 1,Quadrant 2,Quadrant 3,Quadrant 4}"
        "\n                  Value          : 3001"
        "\nChannel data 3  : Channel number : 4"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Net Flow,Quadrant 1,Quadrant 2,Quadrant 3,Quadrant 4}"
        "\n                  Value          : 3001"
        "\nChannel data 4  : Channel number : 5"
        "\n                  Status         : 0"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Quadrant 1,Quadrant 4}"
        "\n                  Value          : 0"
        "\nChannel data 5  : Channel number : 12"
        "\n                  Status         : 0"
        "\n                  UOM            : V"
        "\n                  Modifiers      : {milli}"
        "\n                  Value          : 236231"
        "\nChannel data 6  : Channel number : 17"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Quadrant 1,Quadrant 4,TOU Rate A}"
        "\n                  Value          : 177"
        "\nChannel data 7  : Channel number : 18"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Net Flow,Quadrant 1,Quadrant 2,Quadrant 3,Quadrant 4,TOU Rate A}"
        "\n                  Value          : 177"
        "\nChannel data 8  : Channel number : 23"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Quadrant 1,Quadrant 4,TOU Rate B}"
        "\n                  Value          : 0"
        "\nChannel data 9  : Channel number : 24"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Net Flow,Quadrant 1,Quadrant 2,Quadrant 3,Quadrant 4,TOU Rate B}"
        "\n                  Value          : 0"
        "\nChannel data 10 : Channel number : 29"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Quadrant 1,Quadrant 4,TOU Rate C}"
        "\n                  Value          : 0"
        "\nChannel data 11 : Channel number : 30"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Net Flow,Quadrant 1,Quadrant 2,Quadrant 3,Quadrant 4,TOU Rate C}"
        "\n                  Value          : 0"
        "\nChannel data 12 : Channel number : 35"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Quadrant 1,Quadrant 4,TOU Rate D}"
        "\n                  Value          : 0"
        "\nChannel data 13 : Channel number : 36"
        "\n                  Status         : 0"
        "\n                  UOM            : Wh"
        "\n                  Modifiers      : {Net Flow,Quadrant 1,Quadrant 2,Quadrant 3,Quadrant 4,TOU Rate D}"
        "\n                  Value          : 0"
        "\n# Dated data    : 13"
        "\nDated data 0    : Channel number : 6"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 02/17/2021 16:45:00"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max,Quadrant 1,Quadrant 4}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)"
        "\nDated data 1    : Channel number : 8"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 01/31/2021 23:00:00"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max,Previous,Quadrant 1,Quadrant 4}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)"
        "\nDated data 2    : Channel number : 10"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 02/17/2021 16:55:33"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Daily Max,Quadrant 1,Quadrant 4}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)"
        "\nDated data 3    : Channel number : 13"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 02/17/2021 12:23:00"
        "\n                  UOM            : V"
        "\n                  Modifiers      : {Daily Max,milli}"
        "\n                  Value          : 243548"
        "\n                  Base channel   : (none)"
        "\nDated data 4    : Channel number : 15"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 02/17/2021 05:12:00"
        "\n                  UOM            : V"
        "\n                  Modifiers      : {Daily Min,milli}"
        "\n                  Value          : 234515"
        "\n                  Base channel   : (none)"
        "\nDated data 5    : Channel number : 19"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 02/17/2021 16:45:00"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max,Quadrant 1,Quadrant 4,TOU Rate A}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)"
        "\nDated data 6    : Channel number : 21"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 01/31/2021 23:00:00"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max,Previous,Quadrant 1,Quadrant 4,TOU Rate A}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)"
        "\nDated data 7    : Channel number : 25"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 02/17/2021 07:45:00"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max,Quadrant 1,Quadrant 4,TOU Rate B}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)"
        "\nDated data 8    : Channel number : 27"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 01/31/2021 07:45:00"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max,Previous,Quadrant 1,Quadrant 4,TOU Rate B}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)"
        "\nDated data 9    : Channel number : 31"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 02/17/2021 08:45:00"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max,Quadrant 1,Quadrant 4,TOU Rate C}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)"
        "\nDated data 10   : Channel number : 33"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 01/31/2021 08:45:00"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max,Previous,Quadrant 1,Quadrant 4,TOU Rate C}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)"
        "\nDated data 11   : Channel number : 37"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 02/17/2021 09:45:00"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max,Quadrant 1,Quadrant 4,TOU Rate D}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)"
        "\nDated data 12   : Channel number : 39"
        "\n                  Status         : 0"
        "\n                  Timestamp      : 01/31/2021 09:45:00"
        "\n                  UOM            : W"
        "\n                  Modifiers      : {Max,Previous,Quadrant 1,Quadrant 4,TOU Rate D}"
        "\n                  Value          : 0"
        "\n                  Base channel   : (none)");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();

    BOOST_REQUIRE(responseMsg);

    using rt = Cti::Messaging::Rfn::RfnMeterReadingDataReplyType;

    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::OK);

    const auto& data = responseMsg->data;

    BOOST_CHECK_EQUAL(data.timeStamp, decode_time);
    BOOST_CHECK_EQUAL(data.rfnIdentifier, Cti::RfnIdentifier());
    BOOST_CHECK_EQUAL(data.recordInterval, 0);

    using cds = Cti::Messaging::Rfn::ChannelDataStatus;

    const auto& channelDataList = data.channelDataList;

    BOOST_REQUIRE_EQUAL(channelDataList.size(), 14);
    auto channelData_itr = channelDataList.cbegin();
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 1);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Quadrant 1", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 3001);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 2);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Quadrant 2", "Quadrant 3" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 3);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Quadrant 1", "Quadrant 2", "Quadrant 3", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 3001);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 4);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Net Flow", "Quadrant 1", "Quadrant 2", "Quadrant 3", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 3001);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 5);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Quadrant 1", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 12);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "V");
        const ModifierSet expectedModifiers { "milli" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 236231);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 17);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Quadrant 1", "Quadrant 4", "TOU Rate A" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 177);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 18);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Net Flow", "Quadrant 1", "Quadrant 2", "Quadrant 3", "Quadrant 4", "TOU Rate A" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 177);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 23);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Quadrant 1", "Quadrant 4", "TOU Rate B" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 24);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Net Flow", "Quadrant 1", "Quadrant 2", "Quadrant 3", "Quadrant 4", "TOU Rate B" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 29);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Quadrant 1", "Quadrant 4", "TOU Rate C" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 30);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Net Flow", "Quadrant 1", "Quadrant 2", "Quadrant 3", "Quadrant 4", "TOU Rate C" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 35);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Quadrant 1", "Quadrant 4", "TOU Rate D" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);
    }
    {
        const auto& channelData = *channelData_itr++;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 36);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "Wh");
        const ModifierSet expectedModifiers { "Net Flow", "Quadrant 1", "Quadrant 2", "Quadrant 3", "Quadrant 4", "TOU Rate D" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);
    }

    const auto& datedChannelDataList = data.datedChannelDataList;

    BOOST_REQUIRE_EQUAL(datedChannelDataList.size(), 13);
    auto datedChannelData_itr = datedChannelDataList.cbegin();
    {
        const auto& datedChannelData = *datedChannelData_itr++;

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 6);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max", "Quadrant 1", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-02-17 16:45:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = *datedChannelData_itr++;

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 8);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max", "Previous", "Quadrant 1", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-01-31 23:00:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = *datedChannelData_itr++;

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 10);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Daily Max", "Quadrant 1", "Quadrant 4" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-02-17 16:55:33"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = *datedChannelData_itr++;

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 13);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "V");
        const ModifierSet expectedModifiers { "Daily Max", "milli" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 243548);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-02-17 12:23:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = *datedChannelData_itr++;

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 15);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "V");
        const ModifierSet expectedModifiers { "Daily Min", "milli" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 234515);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-02-17 05:12:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = *datedChannelData_itr++;

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 19);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max", "Quadrant 1", "Quadrant 4", "TOU Rate A" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-02-17 16:45:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = *datedChannelData_itr++;

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 21);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max", "Previous", "Quadrant 1", "Quadrant 4", "TOU Rate A" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-01-31 23:00:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = *datedChannelData_itr++;

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 25);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max", "Quadrant 1", "Quadrant 4", "TOU Rate B" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-02-17 07:45:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = *datedChannelData_itr++;

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 27);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max", "Previous", "Quadrant 1", "Quadrant 4", "TOU Rate B" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-01-31 07:45:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = *datedChannelData_itr++;

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 31);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max", "Quadrant 1", "Quadrant 4", "TOU Rate C" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-02-17 08:45:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = *datedChannelData_itr++;

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 33);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max", "Previous", "Quadrant 1", "Quadrant 4", "TOU Rate C" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-01-31 08:45:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
    {
        const auto& datedChannelData = *datedChannelData_itr++;

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 37);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const ModifierSet expectedModifiers { "Max", "Quadrant 1", "Quadrant 4", "TOU Rate D" };
        BOOST_CHECK_EQUAL(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 0);

        BOOST_CHECK_EQUAL(datedChannelData.timeStamp, parseIso8601String("2021-02-17 09:45:00"));

        const auto& baseChannelData = datedChannelData.baseChannelData;
        BOOST_CHECK_EQUAL(baseChannelData.has_value(), false);
    }
}

BOOST_AUTO_TEST_SUITE_END()
