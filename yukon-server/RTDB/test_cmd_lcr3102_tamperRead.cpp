#include <boost/test/unit_test.hpp>

#include "cmd_lcr3102_tamperRead.h"

#include "ctidate.h"

using Cti::Devices::Commands::Lcr3102TamperReadCommand;
using Cti::Devices::Commands::DlcCommand;
using std::string;

BOOST_AUTO_TEST_SUITE( test_cmd_lcr3102_tamperRead )

struct expected_pointdata
{
    std::string name;
    CtiTime time;
    double value;
    PointQuality_t quality;
    CtiPointType_t type;
    unsigned offset;
};

BOOST_AUTO_TEST_CASE( test_execute_decode_rcircuit_tamper )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102TamperReadCommand tamperRead;

    {
        auto ptr = tamperRead.executeCommand(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function(), 0x00);
        BOOST_CHECK_EQUAL(ptr->io(),       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),   0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto ptr = tamperRead.decodeCommand(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        auto req = dynamic_cast<DlcCommand::emetcon_request_t *>(ptr.get());

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->function(), 0x00);
        BOOST_CHECK_EQUAL(req->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(req->length(),   7);
        BOOST_CHECK_EQUAL(req->payload().size(), 0);

        BOOST_CHECK_EQUAL(points.size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x01);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto ptr = tamperRead.decodeCommand(executeTime, 0x0, payload, description, points);

        //  This is the last step in the command process. We should have a null pointer!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description, "Relay Circuit Fault detected. ");

        expected_pointdata expected[] =
        {
            { "Relay Circuit Fault", CtiTime(CtiDate(13, 10, 2010), 10, 6), 0x01, NormalQuality, StatusPointType, 30 },
            { "Runtime Tamper",      CtiTime(CtiDate(13, 10, 2010), 10, 6), 0x00, NormalQuality, StatusPointType, 31 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL(points.size(), expected_size);

        for( int i = 0; i < expected_size; i++ )
        {
            BOOST_CHECK_EQUAL(points[i].name,    expected[i].name);
            BOOST_CHECK_EQUAL(points[i].time,    expected[i].time);
            BOOST_CHECK_EQUAL(points[i].value,   expected[i].value);
            BOOST_CHECK_EQUAL(points[i].quality, expected[i].quality);
            BOOST_CHECK_EQUAL(points[i].type,    expected[i].type);
            BOOST_CHECK_EQUAL(points[i].offset,  expected[i].offset);
        }
    }
}

BOOST_AUTO_TEST_CASE( test_execute_decode_runtime_tamper )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102TamperReadCommand tamperRead;

    {
        auto ptr = tamperRead.executeCommand(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function(), 0x00);
        BOOST_CHECK_EQUAL(ptr->io(),       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),   0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto ptr = tamperRead.decodeCommand(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        auto req = dynamic_cast<DlcCommand::emetcon_request_t *>(ptr.get());

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->function(), 0x00);
        BOOST_CHECK_EQUAL(req->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(req->length(),   7);
        BOOST_CHECK_EQUAL(req->payload().size(), 0);

        BOOST_CHECK_EQUAL(points.size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x02);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto ptr = tamperRead.decodeCommand(executeTime, 0x0, payload, description, points);

        //  This is the last step in the command process. We should have a null pointer!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description, "Runtime Tamper detected. ");

        expected_pointdata expected[] =
        {
            { "Relay Circuit Fault", CtiTime(CtiDate(13, 10, 2010), 10, 6), 0x00, NormalQuality, StatusPointType, 30 },
            { "Runtime Tamper",      CtiTime(CtiDate(13, 10, 2010), 10, 6), 0x01, NormalQuality, StatusPointType, 31 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL(points.size(), expected_size);

        for( int i = 0; i < expected_size; i++ )
        {
            BOOST_CHECK_EQUAL(points[i].name,    expected[i].name);
            BOOST_CHECK_EQUAL(points[i].time,    expected[i].time);
            BOOST_CHECK_EQUAL(points[i].value,   expected[i].value);
            BOOST_CHECK_EQUAL(points[i].quality, expected[i].quality);
            BOOST_CHECK_EQUAL(points[i].type,    expected[i].type);
            BOOST_CHECK_EQUAL(points[i].offset,  expected[i].offset);
        }
    }
}

BOOST_AUTO_TEST_CASE( test_execute_decode_both_tamper )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102TamperReadCommand tamperRead;

    {
        auto ptr = tamperRead.executeCommand(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function(), 0x00);
        BOOST_CHECK_EQUAL(ptr->io(),       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),   0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto ptr = tamperRead.decodeCommand(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        auto req = dynamic_cast<DlcCommand::emetcon_request_t *>(ptr.get());

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->function(), 0x00);
        BOOST_CHECK_EQUAL(req->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(req->length(),   7);
        BOOST_CHECK_EQUAL(req->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x03);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto ptr = tamperRead.decodeCommand(executeTime, 0x0, payload, description, points);

        //  This is the last step in the command process. We should have a null pointer!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description, "Relay Circuit Fault detected. Runtime Tamper detected. ");

        expected_pointdata expected[] =
        {
            { "Relay Circuit Fault", CtiTime(CtiDate(13, 10, 2010), 10, 6), 0x01, NormalQuality, StatusPointType, 30 },
            { "Runtime Tamper",      CtiTime(CtiDate(13, 10, 2010), 10, 6), 0x01, NormalQuality, StatusPointType, 31 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL(points.size(), expected_size);

        for( int i = 0; i < expected_size; i++ )
        {
            BOOST_CHECK_EQUAL(points[i].name,    expected[i].name);
            BOOST_CHECK_EQUAL(points[i].time,    expected[i].time);
            BOOST_CHECK_EQUAL(points[i].value,   expected[i].value);
            BOOST_CHECK_EQUAL(points[i].quality, expected[i].quality);
            BOOST_CHECK_EQUAL(points[i].type,    expected[i].type);
            BOOST_CHECK_EQUAL(points[i].offset,  expected[i].offset);
        }
    }
}

BOOST_AUTO_TEST_CASE( test_execute_decode_neither_tamper )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102TamperReadCommand tamperRead;

    {
        auto ptr = tamperRead.executeCommand(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function(), 0x00);
        BOOST_CHECK_EQUAL(ptr->io(),       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),   0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto ptr = tamperRead.decodeCommand(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        auto req = dynamic_cast<DlcCommand::emetcon_request_t *>(ptr.get());

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->function(), 0x00);
        BOOST_CHECK_EQUAL(req->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(req->length(),   7);
        BOOST_CHECK_EQUAL(req->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto ptr = tamperRead.decodeCommand(executeTime, 0x0, payload, description, points);

        //  This is the last step in the command process. We should have a null pointer!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description, "No tamper detected.");

        expected_pointdata expected[] =
        {
            { "Relay Circuit Fault", CtiTime(CtiDate(13, 10, 2010), 10, 6), 0x00, NormalQuality, StatusPointType, 30 },
            { "Runtime Tamper",      CtiTime(CtiDate(13, 10, 2010), 10, 6), 0x00, NormalQuality, StatusPointType, 31 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL(points.size(), expected_size);

        for( int i = 0; i < expected_size; i++ )
        {
            BOOST_CHECK_EQUAL(points[i].name,    expected[i].name);
            BOOST_CHECK_EQUAL(points[i].time,    expected[i].time);
            BOOST_CHECK_EQUAL(points[i].value,   expected[i].value);
            BOOST_CHECK_EQUAL(points[i].quality, expected[i].quality);
            BOOST_CHECK_EQUAL(points[i].type,    expected[i].type);
            BOOST_CHECK_EQUAL(points[i].offset,  expected[i].offset);
        }
    }
}

BOOST_AUTO_TEST_CASE( test_execute_decode_tamper_value_error )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102TamperReadCommand tamperRead;

    {
        auto ptr = tamperRead.executeCommand(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function(), 0x00);
        BOOST_CHECK_EQUAL(ptr->io(),       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),   0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto ptr = tamperRead.decodeCommand(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        auto req = dynamic_cast<DlcCommand::emetcon_request_t *>(ptr.get());

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->function(), 0x00);
        BOOST_CHECK_EQUAL(req->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(req->length(),   7);
        BOOST_CHECK_EQUAL(req->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x04);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        // Does not get retries! Will throw immediately upon bad payload!
        try
        {
            tamperRead.decodeCommand(executeTime, 0x0, payload, description, points);

            BOOST_FAIL("Lcr3102TamperReadCommand::decodeCommand() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::InvalidData);
            BOOST_CHECK_EQUAL(ex.error_description, "Returned value is outside the range of acceptable values (4)");
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()
