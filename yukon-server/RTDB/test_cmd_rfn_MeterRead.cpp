#include <boost/test/unit_test.hpp>

#include "cmd_rfn_MeterRead.h"

#include "ctidate.h"

#include "std_helper.h"
#include "boost_test_helpers.h"

using namespace Cti::Devices::Commands;

namespace Cti::Messaging::Rfn {
    std::ostream& operator<<(std::ostream& os, const RfnMeterReadingDataReplyType type) {
        return os << "[RfnMeterReadingDataReplyType " << static_cast<int>(type) << "]";
    }
}

namespace Cti::Messaging::Rfn {
    std::ostream& operator<<(std::ostream& os, const ChannelDataStatus cds) {
        return os << "[ChannelDataStatus " << static_cast<int>(cds) << "]";
    }
}

namespace Cti {
    std::ostream& operator<<(std::ostream& os, const RfnIdentifier rfnId) {
        return os << rfnId.toString();
    }
}

BOOST_AUTO_TEST_SUITE(test_cmd_rfn_MeterRead)

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

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

        RfnCommandResult result = command.decodeCommand(execute_time, response);

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

    RfnCommandResult result = command.decodeCommand(execute_time, response);

    BOOST_CHECK_EQUAL(result.description, 
        "Results:"
        "\nUser message ID : 11235"
        "\nReply type      : 0"
        "\n# Channel data  : 0"
        "\n# Dated data    : 0"
        "\nTimestamp       : 02/17/2010 10:00:00");
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

    RfnCommandResult result = command.decodeCommand(execute_time, response);

    BOOST_CHECK_EQUAL(result.description,
        "Results:"
        "\nUser message ID : 11235"
        "\nReply type      : 0"
        "\n# Channel data  : 0"
        "\n# Dated data    : 0"
        "\nTimestamp       : 02/17/2010 10:00:00");
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

    RfnCommandResult result = command.decodeCommand(execute_time, response);

    BOOST_CHECK_EQUAL(result.description,
        "Results:"
        "\nUser message ID : 11235"
        "\nReply type      : 0"
        "\n# Channel data  : 1"
        "\n# Dated data    : 0"
        "\nTimestamp       : 02/17/2010 10:00:00");
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

        RfnCommandResult result = command.decodeCommand(execute_time, response);

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

    RfnCommandResult result = command.decodeCommand(execute_time, response);

    BOOST_CHECK_EQUAL(result.description,
        "Results:"
        "\nUser message ID : 11235"
        "\nReply type      : 0"
        "\n# Channel data  : 1"
        "\n# Dated data    : 0"
        "\nTimestamp       : 02/17/2010 10:00:00");
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

    RfnCommandResult result = command.decodeCommand(execute_time, response);

    BOOST_CHECK_EQUAL(result.description,
        "Results:"
        "\nUser message ID : 11235"
        "\nReply type      : 0"
        "\n# Channel data  : 2"
        "\n# Dated data    : 0"
        "\nTimestamp       : 02/17/2010 10:00:00");
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
        0x05, //  Number of channels in response
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
    };

    RfnCommandResult result = command.decodeCommand(execute_time, response);

    BOOST_CHECK_EQUAL(result.description,
        "Results:"
        "\nUser message ID : 11235"
        "\nReply type      : 0"
        "\n# Channel data  : 3"
        "\n# Dated data    : 1"
        "\nTimestamp       : 02/17/2010 10:00:00");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();

    BOOST_REQUIRE(responseMsg);

    using rt = Cti::Messaging::Rfn::RfnMeterReadingDataReplyType;

    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::OK);

    const auto& data = responseMsg->data;

    BOOST_CHECK_EQUAL(data.timeStamp, execute_time);
    BOOST_CHECK_EQUAL(data.rfnIdentifier, Cti::RfnIdentifier());
    BOOST_CHECK_EQUAL(data.recordInterval, 0);

    const auto& channelDataList = data.channelDataList;

    BOOST_REQUIRE_EQUAL(channelDataList.size(), 3);

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
        const auto expectedModifiers = { "Max" };
        BOOST_CHECK_EQUAL_RANGES(channelData.unitOfMeasureModifiers, expectedModifiers);
        BOOST_CHECK_EQUAL(channelData.value, 42);
    }

    const auto& datedChannelDataList = data.datedChannelDataList;

    BOOST_REQUIRE_EQUAL(datedChannelDataList.size(), 1);

    {
        const auto& datedChannelData = datedChannelDataList[0];

        const auto& channelData = datedChannelData.channelData;
        BOOST_CHECK_EQUAL(channelData.channelNumber, 25);
        BOOST_CHECK_EQUAL(channelData.status, cds::OK);
        BOOST_CHECK_EQUAL(channelData.unitOfMeasure, "W");
        const auto expectedModifiers = { "Max" };
        BOOST_CHECK_EQUAL_RANGES(channelData.unitOfMeasureModifiers, expectedModifiers);
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

    RfnCommandResult result = command.decodeCommand(execute_time, response);
    
    BOOST_CHECK_EQUAL(result.description,
        "Results:"
        "\nUser message ID : 11235"
        "\nReply type      : 0"
        "\n# Channel data  : 0"
        "\n# Dated data    : 2"
        "\nTimestamp       : 02/17/2010 10:00:00");
    BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    BOOST_CHECK(result.points.empty());

    const auto responseMsg = command.getResponseMessage();

    BOOST_REQUIRE(responseMsg);

    using rt = Cti::Messaging::Rfn::RfnMeterReadingDataReplyType;

    BOOST_CHECK_EQUAL(responseMsg->replyType, rt::OK);
    
    const auto& data = responseMsg->data;

    BOOST_CHECK_EQUAL(data.timeStamp, execute_time);
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
        const auto expectedModifiers = { "Max" };
        BOOST_CHECK_EQUAL_RANGES(channelData.unitOfMeasureModifiers, expectedModifiers);
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
        const auto expectedModifiers = { "Quadrant 1", "Quadrant 4" };
        BOOST_CHECK_EQUAL_RANGES(channelData.unitOfMeasureModifiers, expectedModifiers);
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

BOOST_AUTO_TEST_SUITE_END()