#include "cmd_lcr3102_DemandResponseSummary.h"

#include "ctidate.h"
#include "ctitime.h"

#define BOOST_TEST_MAIN "Testing Devices::Commands::Lcr3102DemandResponseSummaryCommand"

#include <boost/test/unit_test.hpp>

using Cti::Devices::Commands::Lcr3102DemandResponseSummaryCommand;
using Cti::Devices::Commands::DlcCommand;
using std::string;

BOOST_AUTO_TEST_CASE( test_execute_summary_controlled_only )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102DemandResponseSummaryCommand drSummaryRead = Lcr3102DemandResponseSummaryCommand();

    {
        DlcCommand::request_ptr ptr = drSummaryRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x00);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x100);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  7);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
    
    {
        DlcCommand::payload_t payload;

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

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  This is the last step in the command process. We should have a null pointer!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description, "Not in cold load pickup\n"
                                       "Device controlling\n"
                                       "No under voltage event\n"
                                       "No under frequency event\n"
                                       "Device not out of service\n");

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
}

BOOST_AUTO_TEST_CASE( test_execute_summary_cold_load_pickup )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102DemandResponseSummaryCommand drSummaryRead = Lcr3102DemandResponseSummaryCommand();

    {
        DlcCommand::request_ptr ptr = drSummaryRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x00);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x100);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  7);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
    
    {
        DlcCommand::payload_t payload;

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

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  This is the last step in the command process. We should have a null pointer!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description, "In cold load pickup\n"
                                       "No under voltage event\n"
                                       "No under frequency event\n"
                                       "Device not out of service\n");

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
}

BOOST_AUTO_TEST_CASE( test_execute_summary_activated_not_controlling )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102DemandResponseSummaryCommand drSummaryRead = Lcr3102DemandResponseSummaryCommand();

    {
        DlcCommand::request_ptr ptr = drSummaryRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x00);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x100);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  7);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
    
    {
        DlcCommand::payload_t payload;

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

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  This is the last step in the command process. We should have a null pointer!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description, "Not in cold load pickup\n"
                                       "Activated, not controlling\n"
                                       "No under voltage event\n"
                                       "No under frequency event\n"
                                       "Device not out of service\n");

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
}

BOOST_AUTO_TEST_CASE( test_execute_summary_activated_controlling )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102DemandResponseSummaryCommand drSummaryRead = Lcr3102DemandResponseSummaryCommand();

    {
        DlcCommand::request_ptr ptr = drSummaryRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x00);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x100);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  7);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
    
    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x09);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  This is the last step in the command process. We should have a null pointer!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description, "Not in cold load pickup\n"
                                       "Activated and controlling\n"
                                       "No under voltage event\n"
                                       "No under frequency event\n"
                                       "Device not out of service\n");

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
}

BOOST_AUTO_TEST_CASE( test_execute_summary_under_voltage_event )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102DemandResponseSummaryCommand drSummaryRead = Lcr3102DemandResponseSummaryCommand();

    {
        DlcCommand::request_ptr ptr = drSummaryRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x00);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x100);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  7);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
    
    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x10);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  This is the last step in the command process. We should have a null pointer!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description, "Not in cold load pickup\n"
                                       "Under voltage event\n"
                                       "No under frequency event\n"
                                       "Device not out of service\n");

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
}

BOOST_AUTO_TEST_CASE( test_execute_summary_under_frequency_event )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102DemandResponseSummaryCommand drSummaryRead = Lcr3102DemandResponseSummaryCommand();

    {
        DlcCommand::request_ptr ptr = drSummaryRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x00);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x100);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  7);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
    
    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x20);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  This is the last step in the command process. We should have a null pointer!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description, "Not in cold load pickup\n"
                                       "No under voltage event\n"
                                       "Under frequency event\n"
                                       "Device not out of service\n");

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
}

BOOST_AUTO_TEST_CASE( test_execute_summary_out_of_service )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102DemandResponseSummaryCommand drSummaryRead = Lcr3102DemandResponseSummaryCommand();

    {
        DlcCommand::request_ptr ptr = drSummaryRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x00);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x100);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  7);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
    
    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x40);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  This is the last step in the command process. We should have a null pointer!
        BOOST_CHECK(!ptr.get());

        BOOST_CHECK_EQUAL(description, "Not in cold load pickup\n"
                                       "No under voltage event\n"
                                       "No under frequency event\n"
                                       "Device out of service\n");

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
}

BOOST_AUTO_TEST_CASE( test_execute_summary_activated_not_controlling_conflict )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102DemandResponseSummaryCommand drSummaryRead = Lcr3102DemandResponseSummaryCommand();

    {
        DlcCommand::request_ptr ptr = drSummaryRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x00);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x100);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  7);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
    
    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x05);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        try
        {
            DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

            BOOST_FAIL("Lcr3102DemandResponseSummaryCommand::decode() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ErrorInvalidData);
            BOOST_CHECK_EQUAL(ex.error_description, "LCR returned a conflicted account of its currently controlled state (5)");
        }
    }
}

BOOST_AUTO_TEST_CASE( test_execute_summary_activated_controlling_conflict )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102DemandResponseSummaryCommand drSummaryRead = Lcr3102DemandResponseSummaryCommand();

    {
        DlcCommand::request_ptr ptr = drSummaryRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x00);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x100);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  7);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
    
    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x08);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        try
        {
            DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

            BOOST_FAIL("Lcr3102DemandResponseSummaryCommand::decode() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ErrorInvalidData);
            BOOST_CHECK_EQUAL(ex.error_description, "LCR returned a conflicted account of its currently controlled state (8)");
        }
    }
}

BOOST_AUTO_TEST_CASE( test_execute_summary_both_activated_enabled_conflict )
{
    CtiDate today(13, 10, 2010);
    CtiTime executeTime(today, 10, 6);

    Lcr3102DemandResponseSummaryCommand drSummaryRead = Lcr3102DemandResponseSummaryCommand();

    {
        DlcCommand::request_ptr ptr = drSummaryRead.execute(executeTime);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());
    
        BOOST_CHECK_EQUAL(ptr->function,  0x00);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  0);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

        //  Request pointer shouldn't be null here!
        BOOST_CHECK(ptr.get());

        BOOST_CHECK_EQUAL(ptr->function,  0x100);
        BOOST_CHECK_EQUAL(ptr->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(ptr->length(),  7);
        BOOST_CHECK_EQUAL(ptr->payload().size(), 0);

        BOOST_CHECK_EQUAL(points.size(), 0);
    }
    
    {
        DlcCommand::payload_t payload;

        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x0c);
        payload.push_back(0x00);
        payload.push_back(0x00);

        string description;
        std::vector<DlcCommand::point_data> points;

        try
        {
            DlcCommand::request_ptr ptr = drSummaryRead.decode(executeTime, 0x0, payload, description, points);

            BOOST_FAIL("Lcr3102DemandResponseSummaryCommand::decode() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ErrorInvalidData);
            BOOST_CHECK_EQUAL(ex.error_description, "LCR returned a conflicted account of its currently controlled state (12)");
        }
    }
}
