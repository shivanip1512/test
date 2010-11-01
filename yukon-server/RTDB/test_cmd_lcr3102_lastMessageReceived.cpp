#include "cmd_lcr3102_lastMessageReceived.h"

#include "ctidate.h"
#include "ctitime.h"

#define BOOST_TEST_MAIN "Testing Devices::Commands::Lcr3102LastMessageReceivedCommand"

#include <boost/test/unit_test.hpp>

using Cti::Devices::Commands::Lcr3102LastMessageReceivedCommand;
using Cti::Devices::Commands::DlcCommand;

BOOST_AUTO_TEST_CASE( test_execute_decode_normal )
{
    CtiDate today(1, 11, 2010);
    CtiTime executeTime(today, 17, 31, 30);

    Lcr3102LastMessageReceivedCommand lastMessageRead = Lcr3102LastMessageReceivedCommand();

    {
        DlcCommand::request_ptr ptr = lastMessageRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x180);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  13);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x06);
        payload.push_back(0x04);
        payload.push_back(0x08);
        payload.push_back(0x0f);
        payload.push_back(0x10);
        payload.push_back(0x17);
        payload.push_back(0x2a);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = lastMessageRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description, "Last message received: 04 08 0f 10 17 2a\n");
    }
}

BOOST_AUTO_TEST_CASE( test_execute_decode_incorrect_payload_amount )
{
    CtiDate today(1, 11, 2010);
    CtiTime executeTime(today, 17, 31, 30);

    Lcr3102LastMessageReceivedCommand lastMessageRead = Lcr3102LastMessageReceivedCommand();

    {
        DlcCommand::request_ptr ptr = lastMessageRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x180);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  13);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x07); // Size reported as 1 larger than actually present.
        payload.push_back(0x04);
        payload.push_back(0x08);
        payload.push_back(0x0f);
        payload.push_back(0x10);
        payload.push_back(0x17);
        payload.push_back(0x2a);

        string description;
        std::vector<DlcCommand::point_data> points;

        try
        {
            DlcCommand::request_ptr ptr = lastMessageRead.decode(executeTime, 0x0, payload, description, points);

            BOOST_FAIL("Lcr3102LastMessageReceivedCommand::decode() did not throw!");
        }
        catch(DlcCommand::CommandException &ex)
        {
            BOOST_CHECK_EQUAL(ex.error_code,        NOTNORMAL);
            BOOST_CHECK_EQUAL(ex.error_description, "Payload too small");
        }

    }
}

BOOST_AUTO_TEST_CASE( test_execute_decode_no_payload )
{
    CtiDate today(1, 11, 2010);
    CtiTime executeTime(today, 17, 31, 30);

    Lcr3102LastMessageReceivedCommand lastMessageRead = Lcr3102LastMessageReceivedCommand();

    {
        DlcCommand::request_ptr ptr = lastMessageRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x180);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  13);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload; // Empty

        string description;
        std::vector<DlcCommand::point_data> points;

        try
        {
            DlcCommand::request_ptr ptr = lastMessageRead.decode(executeTime, 0x0, payload, description, points);

            BOOST_FAIL("Lcr3102LastMessageReceivedCommand::decode() did not throw!");
        }
        catch(DlcCommand::CommandException &ex)
        {
            BOOST_CHECK_EQUAL(ex.error_code,        NOTNORMAL);
            BOOST_CHECK_EQUAL(ex.error_description, "Payload too small");
        }

    }
}
