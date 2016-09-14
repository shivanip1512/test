#include <boost/test/unit_test.hpp>

#include "rf_data_streaming_processor.h"

#include "mgr_device.h"
#include "mgr_point.h"
#include "pt_analog.h"
#include "dev_rfnResidential.h"
#include "dev_rfnCommercial.h"

#include "rtdb_test_helpers.h"

#include "boost_test_helpers.h"

using namespace std::literals::string_literals;

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
    test_RfDataStreamingProcessor(CtiDeviceManager *DM, CtiPointManager *PM)
        :   RfDataStreamingProcessor(DM, PM)
    {}

    using Packet = RfDataStreamingProcessor::Packet;
    using DeviceReport = RfDataStreamingProcessor::DeviceReport;
    using Value = RfDataStreamingProcessor::Value;

    using RfDataStreamingProcessor::processPacket;
    using RfDataStreamingProcessor::processDeviceReport;
};

struct test_Rfn410flDevice : Cti::Devices::Rfn410flDevice
{
    test_Rfn410flDevice(std::string& name)
    {
        _name = name;
        setDeviceType(TYPE_RFN410FL);
    }
};

struct test_Rfn430sl1Device : Cti::Devices::Rfn430sl1Device
{
    test_Rfn430sl1Device(std::string& name)
    {
        _name = name;
        setDeviceType(TYPE_RFN430SL1);
    }
};

struct test_DeviceManager : CtiDeviceManager
{
    std::map<int, CtiDeviceSPtr> devices {
        { 123, boost::make_shared<test_Rfn410flDevice>("JIMMY JOHNS GARGANTUAN (123)"s) },
        {  49, boost::make_shared<test_Rfn410flDevice>("JIMMY JOHNS VITO (49)"s) },
        { 499, boost::make_shared<test_Rfn430sl1Device>("JIMMY JOHNS TURKEY TOM (499)"s) }};

    test_DeviceManager()
    {
        for( auto& device : devices )
        {
            device.second->setID(device.first, test_tag);
        }
    }

    ptr_type getDeviceByRfnIdentifier(const Cti::RfnIdentifier& rfnId) override
    {
        if( rfnId == Cti::RfnIdentifier{ "JIMMY", "JOHNS", "GARGANTUAN" } )
        {
            return devices[123];
        }
        if( rfnId == Cti::RfnIdentifier{ "JIMMY", "JOHNS", "VITO" } )
        {
            return devices[49];
        }
        if( rfnId == Cti::RfnIdentifier{ "JIMMY", "JOHNS", "TURKEY TOM" } )
        {
            return devices[499];
        }

        return nullptr;
    }
};

struct test_PointManager : CtiPointManager
{
    ptr_type getOffsetTypeEqual(long pao, int offset, CtiPointType_t type) override
    {
        //  We only expect analog points to come out of RFN Data Streaming
        BOOST_REQUIRE_EQUAL( type, AnalogPointType );

        if( pao > 100 )
        {
            auto pt = Cti::Test::makeAnalogPoint(pao, pao * 1000 + offset, offset);

            //  Pao 499 gets non-default multipliers
            if( pao == 499 )
            {
                pt->multiplier = 3;
                pt->offset = 100;
            }

            return ptr_type{pt};
        }

        return nullptr;
    }
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

BOOST_AUTO_TEST_CASE( test_processPacket_bad_attribute )
{
    test_RfDataStreamingProcessor::Packet p;

    p.rfnIdentifier = Cti::RfnIdentifier { "JIMMY", "JOHNS", "GARGANTUAN" };

    p.payload = { 
        0x01, 
        0x01, 
        0xf0, 0x0f,  //  invalid metric ID
        0x00, 0x00, 0x00, 0x00, 
        0x00, 
        0x00, 0x00, 0x01, 0x01 };

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

    BOOST_CHECK_EQUAL( reportValue.attribute, Attribute::Demand );
    BOOST_CHECK_EQUAL( reportValue.quality, NormalQuality );
    BOOST_CHECK_EQUAL( reportValue.timestamp, make_time_point(2016, 1, 1, 0, 0, 0) );
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
        0x51,                    //    scaling 5, status 1 (MeterAccessError, maps to AbnormalQuality)
        0xde, 0xad, 0xbe, 0xef, 
        0x00, 0x73, 
        0x00, 0xff, 0xa9, 0xfd,  //    12 Jul 2016 22:13:17
        0x75,                    //    scaling 7, status 5 (ChannelNotSupported, maps to Invalid)
        0x00, 0x03, 0xbf, 0xae,
        0x00, 0x53, 
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

        BOOST_CHECK_EQUAL( reportValue.attribute, Attribute::Demand );
        BOOST_CHECK_EQUAL( reportValue.quality, AbnormalQuality );
        BOOST_CHECK_EQUAL( reportValue.timestamp, make_time_point(2016, 7, 12, 22, 13, 18) );
        BOOST_CHECK_CLOSE( reportValue.value, 3.735928559, 0.000'000'01 );
    }

    {
        const auto& reportValue = *report_itr++;

        BOOST_CHECK_EQUAL( reportValue.attribute, Attribute::Voltage );
        BOOST_CHECK_EQUAL( reportValue.quality, InvalidQuality );
        BOOST_CHECK_EQUAL( reportValue.timestamp, make_time_point(2016, 7, 12, 22, 13, 17) );
        BOOST_CHECK_CLOSE( reportValue.value, 245.678, 0.000'000'01 );
    }

    {
        const auto& reportValue = *report_itr++;

        BOOST_CHECK_EQUAL( reportValue.attribute, Attribute::PowerFactor );
        BOOST_CHECK_EQUAL( reportValue.quality, UnknownQuality );
        BOOST_CHECK_EQUAL( reportValue.timestamp, make_time_point(2152, 2, 7, 6, 28, 15) );
        BOOST_CHECK_CLOSE( reportValue.value, 4.294967295e+18, 0.000'000'01 );
    }
}

BOOST_AUTO_TEST_CASE(test_processDeviceReport)
{
    test_DeviceManager dm;
    test_PointManager pm;

    test_RfDataStreamingProcessor p{ &dm, &pm };

    Cti::Test::set_to_eastern_timezone();

    {
        test_RfDataStreamingProcessor::DeviceReport r {
            { "JIMMY", "JOHNS", "GARGANTUAN" },
            {   
                {   Attribute::Demand,
                    make_time_point(2016, 7, 12, 22, 13, 18),  //  GMT
                    3.735928559,
                    AbnormalQuality },
                {   Attribute::Voltage,
                    make_time_point(2016, 7, 12, 22, 13, 17),  //  GMT
                    245.678,
                    InvalidQuality },
                {   Attribute::PowerFactor,
                    make_time_point(2152, 2, 7, 6, 28, 15),    //  GMT
                    4.294967295e+18,
                    UnknownQuality } } };

        auto pdata = p.processDeviceReport(r);

        BOOST_REQUIRE_EQUAL(pdata.size(), 2);  //  RFN-410 does not support PowerFactor
        
        auto pdata_itr = pdata.begin();

        {
            auto& pdatum = *pdata_itr++;

            BOOST_CHECK_CLOSE(pdatum->getValue(), 3.735928559, 0.000'000'01);
            BOOST_CHECK_EQUAL(pdatum->getTime(), CtiTime( CtiDate( 12, 7, 2016 ), 18, 13, 18 ));
            BOOST_CHECK_EQUAL(pdatum->getId(), 123101);  //  device ID 123, point offset 101
            BOOST_CHECK_EQUAL(pdatum->getType(), 1);  //  Analog
            BOOST_CHECK_EQUAL(pdatum->getString(), "JIMMY JOHNS GARGANTUAN (123) / Analog101 = 3.735929 @ 07/12/2016 18:13:18");
        }
        {
            auto& pdatum = *pdata_itr++;

            BOOST_CHECK_CLOSE(pdatum->getValue(), 245.678, 0.000'000'01);
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
                {   Attribute::Demand,
                    make_time_point(2016, 7, 12, 22, 13, 18),
                    3.735928559,
                    AbnormalQuality } } };

        auto pdata = p.processDeviceReport(r);

        BOOST_REQUIRE_EQUAL(pdata.size(), 0);  //  VITO has no points
    }
    {
        test_RfDataStreamingProcessor::DeviceReport r {
            { "JIMMY", "JOHNS", "TURKEY TOM" },
            {   
                {   Attribute::PowerFactor,
                    make_time_point(2016, 7, 12, 22, 13, 18),
                    3.735928559,
                    AbnormalQuality },
                {   Attribute::VoltagePhaseA,
                    make_time_point(2016, 7, 12, 22, 13, 17),
                    245.678,
                    InvalidQuality },
                {   Attribute::CurrentPhaseA,
                    make_time_point(2152, 2, 7, 6, 28, 15),
                    4.294967295e+18,
                    UnknownQuality } } };

        auto pdata = p.processDeviceReport(r);

        BOOST_REQUIRE_EQUAL(pdata.size(), 3);

        auto pdata_itr = pdata.begin();

        {
            auto& pdatum = *pdata_itr++;

            BOOST_CHECK_CLOSE(pdatum->getValue(), 111.207785677, 0.000'000'01);
            BOOST_CHECK_EQUAL(pdatum->getTime(), CtiTime( CtiDate( 12, 7, 2016 ), 18, 13, 18 ));
            BOOST_CHECK_EQUAL(pdatum->getId(), 499133);  //  device ID 123, point offset 10
            BOOST_CHECK_EQUAL(pdatum->getType(), 1);  //  Analog
            BOOST_CHECK_EQUAL(pdatum->getString(), "JIMMY JOHNS TURKEY TOM (499) / Analog133 = 111.207786 @ 07/12/2016 18:13:18");
        }
        {
            auto& pdatum = *pdata_itr++;

            BOOST_CHECK_CLOSE(pdatum->getValue(), 837.034, 0.000'000'01);
            BOOST_CHECK_EQUAL(pdatum->getTime(), CtiTime( CtiDate( 12, 7, 2016 ), 18, 13, 17 ));
            BOOST_CHECK_EQUAL(pdatum->getId(), 499070);
            BOOST_CHECK_EQUAL(pdatum->getType(), 1);
            BOOST_CHECK_EQUAL(pdatum->getString(), "JIMMY JOHNS TURKEY TOM (499) / Analog70 = 837.034000 @ 07/12/2016 18:13:17");
        }
        {
            auto& pdatum = *pdata_itr++;

            BOOST_CHECK_CLOSE(pdatum->getValue(), 1.2884901885e+19, 0.000'000'01);
            BOOST_CHECK_EQUAL(pdatum->getTime(), CtiTime( CtiDate( 7, 2, 2152 ), 1, 28, 15 ));
            BOOST_CHECK_EQUAL(pdatum->getId(), 499073);  
            BOOST_CHECK_EQUAL(pdatum->getType(), 1);  //  Analog
            BOOST_CHECK_EQUAL(pdatum->getString(), "JIMMY JOHNS TURKEY TOM (499) / Analog73 = 12884901884999999488.000000 @ 02/07/2152 01:28:15");
        }
    }
    {
        test_RfDataStreamingProcessor::DeviceReport r {
            { "BANANA", "BOAT", "DAY-O" },
            {   
                {   Attribute::Demand,
                    make_time_point(2016, 7, 12, 22, 13, 18),
                    3.735928559,
                    AbnormalQuality },
                {   Attribute::Voltage,
                    make_time_point(2016, 7, 12, 22, 13, 17),
                    245.678,
                    InvalidQuality },
                {   Attribute::PowerFactor,
                    make_time_point(2152, 2, 7, 6, 28, 15),
                    4.294967295e+18,
                    UnknownQuality } } };

        auto pdata = p.processDeviceReport(r);

        BOOST_REQUIRE_EQUAL(pdata.size(), 0);  //  Invalid device
    }
}

BOOST_AUTO_TEST_SUITE_END()

