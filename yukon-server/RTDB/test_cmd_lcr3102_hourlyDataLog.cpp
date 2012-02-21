#include <boost/test/unit_test.hpp>

#include "cmd_lcr3102_hourlyDataLog.h"
#include "ctidate.h"

using Cti::Devices::Commands::DlcCommand;
using std::string;

BOOST_AUTO_TEST_SUITE( test_cmd_lcr3102_hourlyDataLog )

struct test_Lcr3102HourlyDataLogCommand : Cti::Devices::Commands::Lcr3102HourlyDataLogCommand
{
    test_Lcr3102HourlyDataLogCommand(int seconds) :
        Lcr3102HourlyDataLogCommand(seconds)
    {
    }

    using Lcr3102HourlyDataLogCommand::validateFlags;
};

BOOST_AUTO_TEST_CASE( test_validate_flags_error_active )
{
    CtiDate today(13, 2, 2009);
    CtiTime executeTime(today, 17, 31, 30);

    test_Lcr3102HourlyDataLogCommand hourlyRead = test_Lcr3102HourlyDataLogCommand(executeTime.seconds());

    try
    {
        const unsigned char flags = 0x1c;
        hourlyRead.validateFlags(flags);

        BOOST_FAIL("Lcr3102HourlyDataLogCommand::validateFlags() did not throw!");
    }
    catch(DlcCommand::CommandException &ex)
    {
        BOOST_CHECK_EQUAL(ex.error_code,        NOTNORMAL);
        BOOST_CHECK_EQUAL(ex.error_description, "Device responded with a data log dispute. (0x1c)");
    }
}

BOOST_AUTO_TEST_CASE( test_validate_flags_temperature_active )
{
    CtiDate today(13, 2, 2009);
    CtiTime executeTime(today, 17, 31, 30);

    test_Lcr3102HourlyDataLogCommand hourlyRead = test_Lcr3102HourlyDataLogCommand(executeTime.seconds());

    try
    {
        const unsigned char flags = 0x0e;
        hourlyRead.validateFlags(flags);

        BOOST_FAIL("Lcr3102HourlyDataLogCommand::validateFlags() did not throw!");
    }
    catch(DlcCommand::CommandException &ex)
    {
        BOOST_CHECK_EQUAL(ex.error_code,        NOTNORMAL);
        BOOST_CHECK_EQUAL(ex.error_description, "Device responded with invalid data types (0x0e)");
    }
}

BOOST_AUTO_TEST_CASE( test_validate_flags_setpoint_active )
{
    CtiDate today(13, 2, 2009);
    CtiTime executeTime(today, 17, 31, 30);

    test_Lcr3102HourlyDataLogCommand hourlyRead = test_Lcr3102HourlyDataLogCommand(executeTime.seconds());

    try
    {
        const unsigned char flags = 0x0d;
        hourlyRead.validateFlags(flags);

        BOOST_FAIL("Lcr3102HourlyDataLogCommand::validateFlags() did not throw!");
    }
    catch(DlcCommand::CommandException &ex)
    {
        BOOST_CHECK_EQUAL(ex.error_code,        NOTNORMAL);
        BOOST_CHECK_EQUAL(ex.error_description, "Device responded with invalid data types (0x0d)");
    }
}

BOOST_AUTO_TEST_CASE( test_validate_flags_temperature_and_setpoint_active )
{
    CtiDate today(13, 2, 2009);
    CtiTime executeTime(today, 17, 31, 30);

    test_Lcr3102HourlyDataLogCommand hourlyRead = test_Lcr3102HourlyDataLogCommand(executeTime.seconds());

    try
    {
        const unsigned char flags = 0x0f;
        hourlyRead.validateFlags(flags);

        BOOST_FAIL("Lcr3102HourlyDataLogCommand::validateFlags() did not throw!");
    }
    catch(DlcCommand::CommandException &ex)
    {
        BOOST_CHECK_EQUAL(ex.error_code,        NOTNORMAL);
        BOOST_CHECK_EQUAL(ex.error_description, "Device responded with invalid data types (0x0f)");
    }
}

BOOST_AUTO_TEST_CASE( test_validate_flags_runtime_inactive )
{
    CtiDate today(13, 2, 2009);
    CtiTime executeTime(today, 17, 31, 30);

    test_Lcr3102HourlyDataLogCommand hourlyRead = test_Lcr3102HourlyDataLogCommand(executeTime.seconds());

    try
    {
        const unsigned char flags = 0x04;
        hourlyRead.validateFlags(flags);

        BOOST_FAIL("Lcr3102HourlyDataLogCommand::validateFlags() did not throw!");
    }
    catch(DlcCommand::CommandException &ex)
    {
        BOOST_CHECK_EQUAL(ex.error_code,        NOTNORMAL);
        BOOST_CHECK_EQUAL(ex.error_description, "Device did not respond with expected shedtime and runtime data (0x04)");
    }
}

BOOST_AUTO_TEST_CASE( test_validate_flags_shedtime_inactive )
{
    CtiDate today(13, 2, 2009);
    CtiTime executeTime(today, 17, 31, 30);

    test_Lcr3102HourlyDataLogCommand hourlyRead = test_Lcr3102HourlyDataLogCommand(executeTime.seconds());

    try
    {
        const unsigned char flags = 0x08;
        hourlyRead.validateFlags(flags);

        BOOST_FAIL("Lcr3102HourlyDataLogCommand::validateFlags() did not throw!");
    }
    catch(DlcCommand::CommandException &ex)
    {
        BOOST_CHECK_EQUAL(ex.error_code,        NOTNORMAL);
        BOOST_CHECK_EQUAL(ex.error_description, "Device did not respond with expected shedtime and runtime data (0x08)");
    }
}

BOOST_AUTO_TEST_CASE( test_validate_flags_runtime_and_shedtime_inactive )
{
    CtiDate today(13, 2, 2009);
    CtiTime executeTime(today, 17, 31, 30);

    test_Lcr3102HourlyDataLogCommand hourlyRead = test_Lcr3102HourlyDataLogCommand(executeTime.seconds());

    try
    {
        const unsigned char flags = 0x00;
        hourlyRead.validateFlags(flags);

        BOOST_FAIL("Lcr3102HourlyDataLogCommand::validateFlags() did not throw!");
    }
    catch(DlcCommand::CommandException &ex)
    {
        BOOST_CHECK_EQUAL(ex.error_code,        NOTNORMAL);
        BOOST_CHECK_EQUAL(ex.error_description, "Device did not respond with expected shedtime and runtime data (0x00)");
    }
}

BOOST_AUTO_TEST_CASE( test_decode_execute_incorrect_start_time )
{
    CtiDate today(13, 2, 2009);
    CtiTime executeTime(today, 17, 31, 30);

    test_Lcr3102HourlyDataLogCommand hourlyRead = test_Lcr3102HourlyDataLogCommand(executeTime.seconds());

    {
        DlcCommand::request_ptr ptr = hourlyRead.execute(executeTime.seconds());

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x186);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(ptr->length(),  4);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 4);

        DlcCommand::payload_t payload = ptr->payload();

        BOOST_CHECK_EQUAL(payload[0], 0x49);
        BOOST_CHECK_EQUAL(payload[1], 0x96);
        BOOST_CHECK_EQUAL(payload[2], 0x02);
        BOOST_CHECK_EQUAL(payload[3], 0xd2);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x04);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  4);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x50);
        payload.push_back(0x97);
        payload.push_back(0x03);
        payload.push_back(0xd2);

        string description;
        std::vector<DlcCommand::point_data> points;

        try
        {
            DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

            BOOST_FAIL("Lcr3102HourlyDataLogCommand::decode() did not throw!");
        }
        catch(DlcCommand::CommandException &ex)
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ErrorInvalidTimestamp);
            BOOST_CHECK_EQUAL(ex.error_description, "Device did not respond with the correct hourly log start time (0x509703d2)");
        }
    }
}

BOOST_AUTO_TEST_CASE( test_decode_execute_insufficient_data )
{
    CtiDate today(13, 2, 2009);
    CtiTime executeTime(today, 17, 31, 30);

    test_Lcr3102HourlyDataLogCommand hourlyRead = test_Lcr3102HourlyDataLogCommand(executeTime.seconds());

    {
        DlcCommand::request_ptr ptr = hourlyRead.execute(executeTime.seconds());

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x186);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(ptr->length(),  4);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 4);

        DlcCommand::payload_t payload = ptr->payload();

        BOOST_CHECK_EQUAL(payload[0], 0x49);
        BOOST_CHECK_EQUAL(payload[1], 0x96);
        BOOST_CHECK_EQUAL(payload[2], 0x02);
        BOOST_CHECK_EQUAL(payload[3], 0xd2);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x04);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  4);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x50);
        payload.push_back(0x97);
        payload.push_back(0x03);

        string description;
        std::vector<DlcCommand::point_data> points;

        try
        {
            DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

            BOOST_FAIL("Lcr3102HourlyDataLogCommand::decode() did not throw!");
        }
        catch(DlcCommand::CommandException &ex)
        {
            BOOST_CHECK_EQUAL(ex.error_code,        NOTNORMAL);
            BOOST_CHECK_EQUAL(ex.error_description, "Payload too small");
        }
    }
}

BOOST_AUTO_TEST_CASE( test_decode_execute_normal_execution )
{
    CtiDate today(13, 2, 2009);
    CtiTime executeTime(today, 17, 31, 30);

    test_Lcr3102HourlyDataLogCommand hourlyRead = test_Lcr3102HourlyDataLogCommand(executeTime.seconds());

    {
        DlcCommand::request_ptr ptr = hourlyRead.execute(executeTime.seconds());

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x186);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(ptr->length(),  4);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 4);

        DlcCommand::payload_t payload = ptr->payload();

        BOOST_CHECK_EQUAL(payload[0], 0x49);
        BOOST_CHECK_EQUAL(payload[1], 0x96);
        BOOST_CHECK_EQUAL(payload[2], 0x02);
        BOOST_CHECK_EQUAL(payload[3], 0xd2);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x04);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  4);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x49);
        payload.push_back(0x96);
        payload.push_back(0x02);
        payload.push_back(0xd2);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x183);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  10);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x0c); // Flags
        payload.push_back(0xb4); // Data...
        payload.push_back(0xf7); //     hour 0: 45 run 15 shed 
        payload.push_back(0x9e); //     hour 1: 30 run 30 shed 
        payload.push_back(0xa1); //     hour 2: 40 run 20 shed 
        payload.push_back(0x4f); //     hour 3: 60 run 0  shed 
        payload.push_back(0x00); //     hour 4: 0  run 60 shed 
        payload.push_back(0x03); //     hour 5: 20 run 40 shed
        payload.push_back(0xc5);    
        payload.push_back(0x28);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x184);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  10);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(description,  "Hour 0 - Runtime: 45 minutes, Shedtime: 15 minutes\n"
                                        "Hour 1 - Runtime: 30 minutes, Shedtime: 30 minutes\n"
                                        "Hour 2 - Runtime: 40 minutes, Shedtime: 20 minutes\n"
                                        "Hour 3 - Runtime: 60 minutes, Shedtime: 0 minutes\n"
                                        "Hour 4 - Runtime: 0 minutes, Shedtime: 60 minutes\n"
                                        "Hour 5 - Runtime: 20 minutes, Shedtime: 40 minutes\n");

    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x0c); // Flags
        payload.push_back(0x2b); // Data...
        payload.push_back(0x25); //     hour 6:  10 run 50 shed
        payload.push_back(0x28); //     hour 7:  20 run 40 shed
        payload.push_back(0x79); //     hour 8:  30 run 30 shed
        payload.push_back(0xea); //     hour 9:  40 run 20 shed
        payload.push_back(0x14); //     hour 10: 50 run 10 shed
        payload.push_back(0xc8); //     hour 11: 60 run  0 shed
        payload.push_back(0xaf);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x185);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  10);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(description,  "Hour 6 - Runtime: 10 minutes, Shedtime: 50 minutes\n"
                                        "Hour 7 - Runtime: 20 minutes, Shedtime: 40 minutes\n"
                                        "Hour 8 - Runtime: 30 minutes, Shedtime: 30 minutes\n"
                                        "Hour 9 - Runtime: 40 minutes, Shedtime: 20 minutes\n"
                                        "Hour 10 - Runtime: 50 minutes, Shedtime: 10 minutes\n"
                                        "Hour 11 - Runtime: 60 minutes, Shedtime: 0 minutes\n");

    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x0c); // Flags
        payload.push_back(0x2b); // Data...
        payload.push_back(0x25); //     hour 12: 10 run 50 shed
        payload.push_back(0x28); //     hour 13: 20 run 40 shed
        payload.push_back(0x79); //     hour 14: 30 run 30 shed
        payload.push_back(0xea); //     hour 15: 40 run 20 shed
        payload.push_back(0x14); //     hour 16: 50 run 10 shed
        payload.push_back(0xc8); //     hour 17: 60 run  0 shed
        payload.push_back(0xaf);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x186);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  10);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(description,  "Hour 12 - Runtime: 10 minutes, Shedtime: 50 minutes\n"
                                        "Hour 13 - Runtime: 20 minutes, Shedtime: 40 minutes\n"
                                        "Hour 14 - Runtime: 30 minutes, Shedtime: 30 minutes\n"
                                        "Hour 15 - Runtime: 40 minutes, Shedtime: 20 minutes\n"
                                        "Hour 16 - Runtime: 50 minutes, Shedtime: 10 minutes\n"
                                        "Hour 17 - Runtime: 60 minutes, Shedtime: 0 minutes\n");

    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x0c); // Flags
        payload.push_back(0xb4); // Data...
        payload.push_back(0xf7); //     hour 18: 45 run 15 shed
        payload.push_back(0x9e); //     hour 19: 30 run 30 shed
        payload.push_back(0xa1); //     hour 20: 40 run 20 shed
        payload.push_back(0x4f); //     hour 21: 60 run 0  shed
        payload.push_back(0x00); //     hour 22: 0  run 60 shed
        payload.push_back(0x03); //     hour 23: 20 run 40 shed
        payload.push_back(0xc5);
        payload.push_back(0x28);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer should be null here!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description,  "Hour 18 - Runtime: 45 minutes, Shedtime: 15 minutes\n"
                                        "Hour 19 - Runtime: 30 minutes, Shedtime: 30 minutes\n"
                                        "Hour 20 - Runtime: 40 minutes, Shedtime: 20 minutes\n"
                                        "Hour 21 - Runtime: 60 minutes, Shedtime: 0 minutes\n"
                                        "Hour 22 - Runtime: 0 minutes, Shedtime: 60 minutes\n"
                                        "Hour 23 - Runtime: 20 minutes, Shedtime: 40 minutes\n"
                                        "\n"
                                        "Hourly data log read complete.");

    }
}

BOOST_AUTO_TEST_CASE( test_execute_decode_more_than_sixty_minutes_error )
{
    CtiDate today(13, 2, 2009);
    CtiTime executeTime(today, 17, 31, 30);

    test_Lcr3102HourlyDataLogCommand hourlyRead = test_Lcr3102HourlyDataLogCommand(executeTime.seconds());

    {
        DlcCommand::request_ptr ptr = hourlyRead.execute(executeTime.seconds());

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x186);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(ptr->length(),  4);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 4);

        DlcCommand::payload_t payload = ptr->payload();

        BOOST_CHECK_EQUAL(payload[0], 0x49);
        BOOST_CHECK_EQUAL(payload[1], 0x96);
        BOOST_CHECK_EQUAL(payload[2], 0x02);
        BOOST_CHECK_EQUAL(payload[3], 0xd2);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x04);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  4);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x49);
        payload.push_back(0x96);
        payload.push_back(0x02);
        payload.push_back(0xd2);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x183);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  10);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x0c); // Flags
        payload.push_back(0xb4); // Data...
        payload.push_back(0xf7); //     hour 0: 45 run 15 shed
        payload.push_back(0x9e); //     hour 1: 30 run 30 shed
        payload.push_back(0xa1); //     hour 2: 40 run 20 shed
        payload.push_back(0x4f); //     hour 3: 60 run 1  shed
        payload.push_back(0x01); //     hour 4: 0  run 60 shed
        payload.push_back(0x03); //     hour 5: 20 run 40 shed
        payload.push_back(0xc5);
        payload.push_back(0x28);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = hourlyRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x184);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  10);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(description,  "Hour 0 - Runtime: 45 minutes, Shedtime: 15 minutes\n"
                                        "Hour 1 - Runtime: 30 minutes, Shedtime: 30 minutes\n"
                                        "Hour 2 - Runtime: 40 minutes, Shedtime: 20 minutes\n"
                                        "Hour 3 - ERROR: Runtime(0x3c), Shedtime(0x01)\n"
                                        "Hour 4 - Runtime: 0 minutes, Shedtime: 60 minutes\n"
                                        "Hour 5 - Runtime: 20 minutes, Shedtime: 40 minutes\n");

    }
}

BOOST_AUTO_TEST_SUITE_END()
