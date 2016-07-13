#include <boost/test/unit_test.hpp>

#include "rf_data_streaming_processor.h"

#include "boost_test_helpers.h"

namespace std {
    ostream& operator<<(ostream& os, const std::chrono::system_clock::time_point &t)
    {
        const auto as_time_t = std::chrono::system_clock::to_time_t(t);
        const auto as_gmt = gmtime(&as_time_t);

        return os << std::put_time(as_gmt, "%c");
    }
}

std::ostream& operator<<(std::ostream& os, const Attribute &attrib)
{
    return os << attrib.getName();
}

BOOST_AUTO_TEST_SUITE( test_mgr_rfn_request )

using Cti::Messaging::Rfn::E2eDataRequestMsg;

struct test_RfDataStreamingProcessor : Cti::Pil::RfDataStreamingProcessor
{
    using Packet = RfDataStreamingProcessor::Packet;

    using RfDataStreamingProcessor::processPacket;
};

BOOST_AUTO_TEST_CASE( test_processPacket_no_points )
{
    test_RfDataStreamingProcessor::Packet p;

    p.rfnIdentifier = Cti::RfnIdentifier { "JIMMY", "JOHNS", "GARGANTUAN" };

    p.payload = { 0x01, 0x00 };

    auto report = test_RfDataStreamingProcessor::processPacket(p);

    BOOST_CHECK_EQUAL( report.rfnId.manufacturer, "JIMMY" );
    BOOST_CHECK_EQUAL( report.rfnId.model,        "JOHNS" );
    BOOST_CHECK_EQUAL( report.rfnId.serialNumber, "GARGANTUAN" );

    BOOST_CHECK( report.values.empty() );
}

BOOST_AUTO_TEST_CASE( test_processPacket_one_point_start_of_epoch )
{
    test_RfDataStreamingProcessor::Packet p;

    p.rfnIdentifier = Cti::RfnIdentifier { "JIMMY", "JOHNS", "GARGANTUAN" };

    p.payload = { 
        0x01, 
        0x01, 
        0x00, 0x05, 
        0x00, 0x00, 0x00, 0x00, 
        0x00, 
        0x00, 0x00, 0x01, 0x01 };

    auto report = test_RfDataStreamingProcessor::processPacket(p);

    BOOST_CHECK_EQUAL( report.rfnId.manufacturer, "JIMMY" );
    BOOST_CHECK_EQUAL( report.rfnId.model,        "JOHNS" );
    BOOST_CHECK_EQUAL( report.rfnId.serialNumber, "GARGANTUAN" );

    BOOST_REQUIRE_EQUAL( report.values.size(), 1 );

    const auto& reportValue = report.values[0];

    tm t {};

    //  Jan 1, 2016, 0:00 GMT
    t.tm_year = 116;
    t.tm_mday = 1;

    const auto jan_1_2016 = std::chrono::system_clock::from_time_t(_mkgmtime(&t));

    BOOST_CHECK_EQUAL( reportValue.attribute, Attribute::Demand );
    BOOST_CHECK_EQUAL( reportValue.quality, NormalQuality );
    BOOST_CHECK_EQUAL( reportValue.timestamp, jan_1_2016 );
    BOOST_CHECK_EQUAL( reportValue.value, 257 );
}

BOOST_AUTO_TEST_CASE( test_processPacket_three_points )
{
    test_RfDataStreamingProcessor::Packet p;

    p.rfnIdentifier = Cti::RfnIdentifier { "JIMMY", "JOHNS", "GARGANTUAN" };

    p.payload = { 
        0x01, 
        0x03, 
        0x00, 0x05, 
        0x00, 0xff, 0xa9, 0xfe,  //    12 Jul 2016 22:13:18
        0x51, 
        0xde, 0xad, 0xbe, 0xef, 
        0x00, 0x73, 
        0x00, 0xff, 0xa9, 0xfd,  //    12 Jul 2016 22:13:17
        0x72, 
        0x00, 0x03, 0xbf, 0xae,
        0x00, 0x53, 
        0xff, 0xff, 0xff, 0xff,  //    07 Feb 2152 06:28:15 GMT
        0x3f, 
        0xff, 0xff, 0xff, 0xff };

    auto report = test_RfDataStreamingProcessor::processPacket(p);

    BOOST_CHECK_EQUAL( report.rfnId.manufacturer, "JIMMY" );
    BOOST_CHECK_EQUAL( report.rfnId.model,        "JOHNS" );
    BOOST_CHECK_EQUAL( report.rfnId.serialNumber, "GARGANTUAN" );

    BOOST_REQUIRE_EQUAL( report.values.size(), 3 );

    auto report_itr = report.values.begin();

    {
        const auto& reportValue = *report_itr++;

        tm t {};

        //  Jan 1, 2016, 0:00 GMT
        t.tm_year = 116;
        t.tm_mon  = 6;
        t.tm_mday = 12;
        t.tm_hour = 22;
        t.tm_min  = 13;
        t.tm_sec  = 18;

        const auto jul_12_2016_22_13_18 = std::chrono::system_clock::from_time_t(_mkgmtime(&t));

        BOOST_CHECK_EQUAL( reportValue.attribute, Attribute::Demand );
        BOOST_CHECK_EQUAL( reportValue.quality, AbnormalQuality );
        BOOST_CHECK_EQUAL( reportValue.timestamp, jul_12_2016_22_13_18 );
        BOOST_CHECK_CLOSE( reportValue.value, 3.735928559, 0.000'000'01 );
    }

    {
        const auto& reportValue = *report_itr++;

        tm t {};

        //  Jan 1, 2016, 0:00 GMT
        t.tm_year = 116;
        t.tm_mon  = 6;
        t.tm_mday = 12;
        t.tm_hour = 22;
        t.tm_min  = 13;
        t.tm_sec  = 17;

        const auto jul_12_2016_22_13_17 = std::chrono::system_clock::from_time_t(_mkgmtime(&t));

        BOOST_CHECK_EQUAL( reportValue.attribute, Attribute::Voltage );
        BOOST_CHECK_EQUAL( reportValue.quality, InvalidQuality );
        BOOST_CHECK_EQUAL( reportValue.timestamp, jul_12_2016_22_13_17 );
        BOOST_CHECK_CLOSE( reportValue.value, 245.678, 0.000'000'01 );
    }

    {
        const auto& reportValue = *report_itr++;

        tm t {};

        //  Feb 7, 2152, 6:28:15 GMT
        t.tm_year = 252;
        t.tm_mon  = 1;
        t.tm_mday = 7;
        t.tm_hour = 6;
        t.tm_min  = 28;
        t.tm_sec  = 15;

        const auto feb_07_2152_06_28_15 = std::chrono::system_clock::from_time_t(_mkgmtime(&t));

        BOOST_CHECK_EQUAL( reportValue.attribute, Attribute::PowerFactor );
        BOOST_CHECK_EQUAL( reportValue.quality, UnknownQuality );
        BOOST_CHECK_EQUAL( reportValue.timestamp, feb_07_2152_06_28_15 );
        BOOST_CHECK_CLOSE( reportValue.value, 4.294967295e+18, 0.000'000'01 );
    }
}
BOOST_AUTO_TEST_SUITE_END()

