#include <boost/test/unit_test.hpp>

#include "rf_data_streaming_processor.h"

#include "mgr_device.h"
#include "mgr_point.h"
#include "pt_analog.h"
#include "dev_rfnResidential.h"
#include "dev_rfnCommercial.h"
#include "dev_rfn_LgyrFocus_al.h"

#include "rtdb_test_helpers.h"

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

std::chrono::system_clock::time_point make_time_point(int year, int month, int day, int hour, int minute, int second)
{
    tm t {
        second,
        minute,
        hour,
        day,
        month - 1,
        year - 1900
    };

    return std::chrono::system_clock::from_time_t(_mkgmtime(&t));
}


BOOST_AUTO_TEST_SUITE( test_mgr_rfn_request )

using Cti::Messaging::Rfn::E2eDataRequestMsg;

struct test_RfDataStreamingProcessor : Cti::Pil::RfDataStreamingProcessor
{
    test_RfDataStreamingProcessor(CtiDeviceManager &DM, CtiPointManager &PM)
        :   RfDataStreamingProcessor(DM, PM)
    {}

    using Packet = RfDataStreamingProcessor::Packet;
    using DeviceReport = RfDataStreamingProcessor::DeviceReport;
    using Value = RfDataStreamingProcessor::Value;

    using RfDataStreamingProcessor::processPacket;
    using RfDataStreamingProcessor::processDeviceReport;
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

BOOST_AUTO_TEST_CASE(test_processPacket_invalid_format_id)
{
    test_RfDataStreamingProcessor::Packet p;

    p.rfnIdentifier = Cti::RfnIdentifier{ "JIMMY", "JOHNS", "GARGANTUAN" };

    p.payload = { 0x71, 0x00 };

    auto report = test_RfDataStreamingProcessor::processPacket(p);

    BOOST_CHECK_EQUAL(report.rfnId.manufacturer, "JIMMY");
    BOOST_CHECK_EQUAL(report.rfnId.model, "JOHNS");
    BOOST_CHECK_EQUAL(report.rfnId.serialNumber, "GARGANTUAN");

    BOOST_CHECK(report.values.empty());
}

BOOST_AUTO_TEST_CASE(test_processPacket_empty_payload)
{
    test_RfDataStreamingProcessor::Packet p;

    p.rfnIdentifier = Cti::RfnIdentifier{ "JIMMY", "JOHNS", "GARGANTUAN" };

    p.payload.clear();  //  just for readability, it was already empty

    auto report = test_RfDataStreamingProcessor::processPacket(p);

    BOOST_CHECK_EQUAL(report.rfnId.manufacturer, "JIMMY");
    BOOST_CHECK_EQUAL(report.rfnId.model, "JOHNS");
    BOOST_CHECK_EQUAL(report.rfnId.serialNumber, "GARGANTUAN");

    BOOST_CHECK(report.values.empty());
}

BOOST_AUTO_TEST_CASE(test_processPacket_incomplete_header)
{
    test_RfDataStreamingProcessor::Packet p;

    p.rfnIdentifier = Cti::RfnIdentifier{ "JIMMY", "JOHNS", "GARGANTUAN" };

    p.payload = { 0x01 };

    auto report = test_RfDataStreamingProcessor::processPacket(p);

    BOOST_CHECK_EQUAL(report.rfnId.manufacturer, "JIMMY");
    BOOST_CHECK_EQUAL(report.rfnId.model, "JOHNS");
    BOOST_CHECK_EQUAL(report.rfnId.serialNumber, "GARGANTUAN");

    BOOST_CHECK(report.values.empty());
}

BOOST_AUTO_TEST_CASE(test_processPacket_incomplete_payload)
{
    test_RfDataStreamingProcessor::Packet p;

    p.rfnIdentifier = Cti::RfnIdentifier{ "JIMMY", "JOHNS", "GARGANTUAN" };

    p.payload = { 
        0x01, 
        0x01,
        0xf0, 0x0f,
        0x00, 0x00, 0x00, 0x00,
        0x00,
        0x00, 0x00, 0x01 /*, 0x01*/ };  //  missing the last byte

    auto report = test_RfDataStreamingProcessor::processPacket(p);

    BOOST_CHECK_EQUAL(report.rfnId.manufacturer, "JIMMY");
    BOOST_CHECK_EQUAL(report.rfnId.model, "JOHNS");
    BOOST_CHECK_EQUAL(report.rfnId.serialNumber, "GARGANTUAN");

    BOOST_CHECK(report.values.empty());
}

BOOST_AUTO_TEST_CASE( test_processPacket_large_metricid )
{
    test_RfDataStreamingProcessor::Packet p;

    p.rfnIdentifier = Cti::RfnIdentifier { "JIMMY", "JOHNS", "GARGANTUAN" };

    p.payload = { 
        0x01, 
        0x01, 
        0xf0, 0x0f,
        0x00, 0x00, 0x00, 0x00, 
        0x00, 
        0x00, 0x00, 0x01, 0x01 };

    auto report = test_RfDataStreamingProcessor::processPacket(p);

    BOOST_CHECK_EQUAL( report.rfnId.manufacturer, "JIMMY" );
    BOOST_CHECK_EQUAL( report.rfnId.model,        "JOHNS" );
    BOOST_CHECK_EQUAL( report.rfnId.serialNumber, "GARGANTUAN" );

    BOOST_REQUIRE_EQUAL( report.values.size(), 1 );

    const auto& reportValue = report.values[0];

    BOOST_CHECK_EQUAL( reportValue.metricId, 61455 );
    BOOST_CHECK_EQUAL( reportValue.quality, NormalQuality );
    BOOST_CHECK_EQUAL( reportValue.timestamp, make_time_point(2016, 1, 1, 0, 0, 0) );
    BOOST_CHECK_CLOSE( reportValue.value, 257, 1e-8 );
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

    BOOST_CHECK_EQUAL( reportValue.metricId, 5 );
    BOOST_CHECK_EQUAL( reportValue.quality, NormalQuality );
    BOOST_CHECK_EQUAL( reportValue.timestamp, make_time_point(2016, 1, 1, 0, 0, 0) );
    BOOST_CHECK_CLOSE( reportValue.value, 257, 1e-8 );
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
        0x51,                    //    scaling 5, status 1 (MeterAccessError, maps to AbnormalQuality)
        0xde, 0xad, 0xbe, 0xef, 
        0x00, 0x73, 
        0x00, 0xff, 0xa9, 0xfd,  //    12 Jul 2016 22:13:17
        0x75,                    //    scaling 7, status 5 (ChannelNotSupported, maps to Invalid)
        0x00, 0x03, 0xbf, 0xae,
        0x00, 0x50, 
        0xff, 0xff, 0xff, 0xff,  //    07 Feb 2152 06:28:15 GMT
        0x3f,                    //    scaling 3, status 15 (Unknown, maps to UnknownQuality)
        0xff, 0xff, 0xff, 0xff };

    auto report = test_RfDataStreamingProcessor::processPacket(p);

    BOOST_CHECK_EQUAL( report.rfnId.manufacturer, "JIMMY" );
    BOOST_CHECK_EQUAL( report.rfnId.model,        "JOHNS" );
    BOOST_CHECK_EQUAL( report.rfnId.serialNumber, "GARGANTUAN" );

    BOOST_REQUIRE_EQUAL( report.values.size(), 3 );

    auto report_itr = report.values.begin();

    {
        const auto& reportValue = *report_itr++;

        BOOST_CHECK_EQUAL( reportValue.metricId, 5 );
        BOOST_CHECK_EQUAL( reportValue.quality, AbnormalQuality );
        BOOST_CHECK_EQUAL( reportValue.timestamp, make_time_point(2016, 7, 12, 22, 13, 18) );
        BOOST_CHECK_CLOSE( reportValue.value, -0.559038737, 1e-8 );
    }

    {
        const auto& reportValue = *report_itr++;

        BOOST_CHECK_EQUAL( reportValue.metricId, 115 );
        BOOST_CHECK_EQUAL( reportValue.quality, InvalidQuality );
        BOOST_CHECK_EQUAL( reportValue.timestamp, make_time_point(2016, 7, 12, 22, 13, 17) );
        BOOST_CHECK_CLOSE( reportValue.value, 245.678, 1e-8 );
    }

    {
        const auto& reportValue = *report_itr++;

        BOOST_CHECK_EQUAL( reportValue.metricId, 80 );
        BOOST_CHECK_EQUAL( reportValue.quality, UnknownQuality );
        BOOST_CHECK_EQUAL( reportValue.timestamp, make_time_point(2152, 2, 7, 6, 28, 15) );
        BOOST_CHECK_CLOSE( reportValue.value, -1'000'000'000, 1e-8 );
    }
}

BOOST_AUTO_TEST_CASE(test_processDeviceReport)
{
    Cti::Test::test_DeviceManager dm;
    Cti::Test::test_PointManager pm;

    test_RfDataStreamingProcessor p { dm, pm };

    Cti::Test::set_to_eastern_timezone();

    {
        test_RfDataStreamingProcessor::DeviceReport r {
            { "JIMMY", "JOHNS", "GARGANTUAN" },
            {   
                {   5,
                    make_time_point(2016, 7, 12, 22, 13, 18),  //  GMT
                    -0.559038737,
                    AbnormalQuality },
                {   115,
                    make_time_point(2016, 7, 12, 22, 13, 17),  //  GMT
                    245.678,
                    InvalidQuality },
                {   80,
                    make_time_point(2152, 2, 7, 6, 28, 15),    //  GMT
                    -1'000'000'000,
                    UnknownQuality } } };

        auto pdata = p.processDeviceReport(r);

        BOOST_REQUIRE_EQUAL(pdata.size(), 2);  //  RFN-410 does not support PowerFactor
        
        auto pdata_itr = pdata.begin();

        {
            auto& pdatum = *pdata_itr++;

            BOOST_CHECK_CLOSE(pdatum->getValue(), -0.000559038737, 1e-8);
            BOOST_CHECK_EQUAL(pdatum->getTime(), CtiTime( CtiDate( 12, 7, 2016 ), 18, 13, 18 ));
            BOOST_CHECK_EQUAL(pdatum->getId(), 123101);  //  device ID 123, point offset 101
            BOOST_CHECK_EQUAL(pdatum->getType(), 1);  //  Analog
            BOOST_CHECK_EQUAL(pdatum->getString(), "JIMMY JOHNS GARGANTUAN (123) / Analog101 = -0.000559 @ 07/12/2016 18:13:18");
        }
        {
            auto& pdatum = *pdata_itr++;

            BOOST_CHECK_CLOSE(pdatum->getValue(), 245.678, 1e-8);
            BOOST_CHECK_EQUAL(pdatum->getTime(), CtiTime( CtiDate( 12, 7, 2016 ), 18, 13, 17 ));
            BOOST_CHECK_EQUAL(pdatum->getId(), 123005);
            BOOST_CHECK_EQUAL(pdatum->getType(), 1);
            BOOST_CHECK_EQUAL(pdatum->getString(), "JIMMY JOHNS GARGANTUAN (123) / Analog5 = 245.678000 @ 07/12/2016 18:13:17");
        }
    }
    {
        test_RfDataStreamingProcessor::DeviceReport r {
            { "JIMMY", "JOHNS", "VITO" },
            {   
                {   5,
                    make_time_point(2016, 7, 12, 22, 13, 18),
                    -0.559038737,
                    AbnormalQuality } } };

        auto pdata = p.processDeviceReport(r);

        BOOST_REQUIRE_EQUAL(pdata.size(), 0);  //  VITO has no points
    }
    {
        test_RfDataStreamingProcessor::DeviceReport r {
            { "JIMMY", "JOHNS", "TURKEY TOM" },
            {   
                {   80,
                    make_time_point(2016, 7, 12, 22, 13, 18),
                    -0.559038737,
                    AbnormalQuality },
                {   100,
                    make_time_point(2016, 7, 12, 22, 13, 17),
                    245.678,
                    InvalidQuality },
                {   103,
                    make_time_point(2152, 2, 7, 6, 28, 15),
                    -1'000'000'000,
                    UnknownQuality } } };

        auto pdata = p.processDeviceReport(r);

        BOOST_REQUIRE_EQUAL(pdata.size(), 3);

        auto pdata_itr = pdata.begin();

        {
            auto& pdatum = *pdata_itr++;

            BOOST_CHECK_CLOSE(pdatum->getValue(), 99.998322884, 1e-8);
            BOOST_CHECK_EQUAL(pdatum->getTime(), CtiTime( CtiDate( 12, 7, 2016 ), 18, 13, 18 ));
            BOOST_CHECK_EQUAL(pdatum->getId(), 499133);  //  device ID 123, point offset 10
            BOOST_CHECK_EQUAL(pdatum->getType(), 1);  //  Analog
            BOOST_CHECK_EQUAL(pdatum->getString(), "JIMMY JOHNS TURKEY TOM (499) / Analog133 = 99.998323 @ 07/12/2016 18:13:18");
        }
        {
            auto& pdatum = *pdata_itr++;

            BOOST_CHECK_CLOSE(pdatum->getValue(), 837.034, 1e-8);
            BOOST_CHECK_EQUAL(pdatum->getTime(), CtiTime( CtiDate( 12, 7, 2016 ), 18, 13, 17 ));
            BOOST_CHECK_EQUAL(pdatum->getId(), 499070);
            BOOST_CHECK_EQUAL(pdatum->getType(), 1);
            BOOST_CHECK_EQUAL(pdatum->getString(), "JIMMY JOHNS TURKEY TOM (499) / Analog70 = 837.034000 @ 07/12/2016 18:13:17");
        }
        {
            auto& pdatum = *pdata_itr++;

            BOOST_CHECK_CLOSE(pdatum->getValue(), -2'999'999'900.0, 1e-8);
            BOOST_CHECK_EQUAL(pdatum->getTime(), CtiTime( CtiDate( 7, 2, 2152 ), 1, 28, 15 ));
            BOOST_CHECK_EQUAL(pdatum->getId(), 499073);  
            BOOST_CHECK_EQUAL(pdatum->getType(), 1);  //  Analog
            BOOST_CHECK_EQUAL(pdatum->getString(), "JIMMY JOHNS TURKEY TOM (499) / Analog73 = -2999999900.000000 @ 02/07/2152 01:28:15");
        }
    }
    {
        test_RfDataStreamingProcessor::DeviceReport r {
            { "JIMMY", "JOHNS", "ITALIAN NIGHT CLUB" },
            {   
                {   115,
                    make_time_point(2016, 7, 14, 5, 15, 27),
                    120.55,
                    NormalQuality } } };

        auto pdata = p.processDeviceReport(r);

        BOOST_REQUIRE_EQUAL(pdata.size(), 1);

        auto pdata_itr = pdata.begin();

        {
            auto& pdatum = *pdata_itr++;

            BOOST_CHECK_CLOSE(pdatum->getValue(), 120.55, 1e-8);
            BOOST_CHECK_EQUAL(pdatum->getTime(), CtiTime( CtiDate( 14, 7, 2016 ), 1, 15, 27 ));
            BOOST_CHECK_EQUAL(pdatum->getId(), 500214);  //  device ID 500, point offset 214
            BOOST_CHECK_EQUAL(pdatum->getType(), 1);  //  Analog
            BOOST_CHECK_EQUAL(pdatum->getString(), "JIMMY JOHNS ITALIAN NIGHT CLUB (500) / Analog214 = 120.550000 @ 07/14/2016 01:15:27");
        }
    }
    {
        test_RfDataStreamingProcessor::DeviceReport r {
            { "BANANA", "BOAT", "DAY-O" },
            {   
                {   5,
                    make_time_point(2016, 7, 12, 22, 13, 18),
                    -0.559038737,
                    AbnormalQuality },
                {   115,
                    make_time_point(2016, 7, 12, 22, 13, 17),
                    245.678,
                    InvalidQuality },
                {   80,
                    make_time_point(2152, 2, 7, 6, 28, 15),
                    -1'000'000'000,
                    UnknownQuality } } };

        auto pdata = p.processDeviceReport(r);

        BOOST_REQUIRE_EQUAL(pdata.size(), 0);  //  Invalid device
    }
}

BOOST_AUTO_TEST_SUITE_END()

