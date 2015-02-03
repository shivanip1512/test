#include <boost/test/unit_test.hpp>

#include "cmd_mct410_disconnectConfiguration.h"
#include "ctidate.h"
#include "boost_test_helpers.h"

#include <boost/assign/list_of.hpp>

using Cti::Devices::Commands::Mct410DisconnectConfigurationCommand;
using Cti::Devices::Commands::DlcCommand;
using std::string;
using boost::assign::list_of;

BOOST_AUTO_TEST_SUITE( test_cmd_mct410_Disconnect )

BOOST_AUTO_TEST_CASE(test_command_payload)
{
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    {
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::Cycling, 0, 0.0, 5, 5, 5, Mct410DisconnectConfigurationCommand::ButtonRequired, 300);

        {
            DlcCommand::request_ptr r = disconnectCommand.executeCommand(execute_time);

            //  make sure it's not null
            BOOST_REQUIRE(r.get());

            BOOST_CHECK_EQUAL(r->function(), 0xfe);
            BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Write);
            BOOST_CHECK_EQUAL(r->length(),   9);

            const std::vector<unsigned char> expected = boost::assign::list_of
                (0x00)(0x00)(0x00)(0x00)(0x00)(0x05)(0x05)(0x05)(0x40);

            const std::vector<unsigned char> actual = r->payload();

            BOOST_CHECK_EQUAL_RANGES(expected, actual);
        }
        {
            const boost::optional<DlcCommand::Bytes> no_payload;
            std::string description;
            std::vector<DlcCommand::point_data> points;

            DlcCommand::request_ptr r = disconnectCommand.decodeCommand(execute_time + 5, 0x1fe, no_payload, description, points);

            BOOST_CHECK_EQUAL(description, "");

            BOOST_REQUIRE(r.get());
            BOOST_CHECK_EQUAL(r->function(),  0xfe);
            BOOST_CHECK_EQUAL(r->io(),        Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(r->length(),    13);
            BOOST_CHECK(r->payload().empty());
        }
        {
            const DlcCommand::Bytes payload = boost::assign::list_of
                (0x00)  //  disconnect status info
                (0x00)  //  disconnect error flag
                (0x00)(0x00)(0x00)  //  disconnect receiver address
                (0x00)(0x00)  //  disconnect demand threshold
                (0x01)  //  load limit connect delay
                (0x00)  //  disconnect load limit count
                (0x05)  //  cycling mode - disconnect minutes
                (0x05)  //  cycling mode - connect minutes
                (0x00)  //  configuration byte
                (0x00); //  disconnect max usage per minute

            std::string description;
            std::vector<DlcCommand::point_data> points;

            DlcCommand::request_ptr r = disconnectCommand.decodeCommand(execute_time + 10, 0x1fe, payload, description, points);

            BOOST_CHECK_EQUAL(description, "\nConfig data received: 00 00 00 00 00 00 00 01 00 05 05 00 00");

            BOOST_CHECK( ! r.get());

            const boost::optional<float> disconnectDemandThresholdRcv = disconnectCommand.getDisconnectDemandThreshold();

            BOOST_REQUIRE( !! disconnectDemandThresholdRcv );
            BOOST_CHECK_EQUAL( *disconnectDemandThresholdRcv, 0.0f);

            const boost::optional<Mct410DisconnectConfigurationCommand::DisconnectMode> mode = disconnectCommand.getDisconnectMode();

            BOOST_REQUIRE( !! mode );
            BOOST_CHECK_EQUAL(*mode, Mct410DisconnectConfigurationCommand::Cycling);
        }
    }

    {
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::DemandThreshold, 264213, 400.0, 10, 60, 60, Mct410DisconnectConfigurationCommand::ButtonNotRequired, 600);

        {
            DlcCommand::request_ptr r = disconnectCommand.executeCommand(execute_time);

            //  make sure it's not null
            BOOST_REQUIRE(r.get());

            BOOST_CHECK_EQUAL(r->function(), 0xfe);
            BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Write);
            BOOST_CHECK_EQUAL(r->length(),   9);

            const std::vector<unsigned char> expected = boost::assign::list_of
                (0x04)(0x08)(0x15)(0x02)(0x9b)(0x0a)(0x03c)(0x3c)(0x44);

            const std::vector<unsigned char> actual = r->payload();

            BOOST_CHECK_EQUAL(actual.size(), 9);
            BOOST_CHECK_EQUAL_COLLECTIONS(actual.begin(), actual.end(),
                                          expected.begin(), expected.end());
        }
        {
            const boost::optional<DlcCommand::Bytes> no_payload;
            std::string description;
            std::vector<DlcCommand::point_data> points;

            DlcCommand::request_ptr r = disconnectCommand.decodeCommand(execute_time + 5, 0x1fe, no_payload, description, points);

            BOOST_CHECK_EQUAL(description, "");

            BOOST_REQUIRE(r.get());
            BOOST_CHECK_EQUAL(r->function(),  0xfe);
            BOOST_CHECK_EQUAL(r->io(),        Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(r->length(),    13);
            BOOST_CHECK(r->payload().empty());
        }
        {
            const DlcCommand::Bytes payload = boost::assign::list_of
                (0x00)  //  disconnect status info
                (0x00)  //  disconnect error flag
                (0x04)(0x08)(0x15)  //  disconnect receiver address
                (0x02)(0x9b)  //  disconnect demand threshold
                (0x0a)  //  load limit connect delay
                (0x00)  //  disconnect load limit count
                (0x3c)  //  cycling mode - disconnect minutes
                (0x3c)  //  cycling mode - connect minutes
                (0x00)  //  configuration byte
                (0x00); //  disconnect max usage per minute

            std::string description;
            std::vector<DlcCommand::point_data> points;

            DlcCommand::request_ptr r = disconnectCommand.decodeCommand(execute_time + 10, 0x1fe, payload, description, points);

            BOOST_CHECK_EQUAL(description, "\nConfig data received: 00 00 04 08 15 02 9b 0a 00 3c 3c 00 00");

            BOOST_CHECK( ! r.get());

            const boost::optional<float> disconnectDemandThreshold = disconnectCommand.getDisconnectDemandThreshold();

            BOOST_REQUIRE( !! disconnectDemandThreshold);
            BOOST_CHECK_CLOSE(*disconnectDemandThreshold, 400.0f, 0.1);

            const boost::optional<Mct410DisconnectConfigurationCommand::DisconnectMode> mode = disconnectCommand.getDisconnectMode();

            BOOST_REQUIRE( !! mode);
            BOOST_CHECK_EQUAL(*mode, Mct410DisconnectConfigurationCommand::DemandThreshold);
        }
    }
    {
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::OnDemand, 264213, 400.0, 10, 60, 60, Mct410DisconnectConfigurationCommand::ButtonNotRequired, 600);

        {
            DlcCommand::request_ptr r = disconnectCommand.executeCommand(execute_time);

            //  make sure it's not null
            BOOST_REQUIRE(r.get());

            BOOST_CHECK_EQUAL(r->function(), 0xfe);
            BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Write);
            BOOST_CHECK_EQUAL(r->length(),   9);

            const std::vector<unsigned> expected = boost::assign::list_of
                (0x04)(0x08)(0x15)(0x00)(0x00)(0x0a)(0x00)(0x00)(0x44);

            const std::vector<unsigned char> actual = r->payload();

            BOOST_CHECK_EQUAL(actual.size(), 9);
            BOOST_CHECK_EQUAL_COLLECTIONS(actual.begin(), actual.end(),
                                          expected.begin(), expected.end());
        }
        {
            const boost::optional<DlcCommand::Bytes> no_payload;
            std::string description;
            std::vector<DlcCommand::point_data> points;

            DlcCommand::request_ptr r = disconnectCommand.decodeCommand(execute_time + 5, 0x1fe, no_payload, description, points);

            BOOST_CHECK_EQUAL(description, "");

            BOOST_REQUIRE(r.get());
            BOOST_CHECK_EQUAL(r->function(),  0xfe);
            BOOST_CHECK_EQUAL(r->io(),        Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(r->length(),    13);
            BOOST_CHECK(r->payload().empty());
        }
        {
            const DlcCommand::Bytes payload = boost::assign::list_of
                (0x00)  //  disconnect status info
                (0x00)  //  disconnect error flag
                (0x04)(0x08)(0x15)  //  disconnect receiver address
                (0x00)(0x00)  //  disconnect demand threshold
                (0x0a)  //  load limit connect delay
                (0x00)  //  disconnect load limit count
                (0x00)  //  cycling mode - disconnect minutes
                (0x00)  //  cycling mode - connect minutes
                (0x00)  //  configuration byte
                (0x00); //  disconnect max usage per minute

            std::string description;
            std::vector<DlcCommand::point_data> points;

            DlcCommand::request_ptr r = disconnectCommand.decodeCommand(execute_time + 10, 0x1fe, payload, description, points);

            BOOST_CHECK_EQUAL(description, "\nConfig data received: 00 00 04 08 15 00 00 0a 00 00 00 00 00");

            BOOST_CHECK( ! r.get());

            const boost::optional<float> disconnectDemandThreshold = disconnectCommand.getDisconnectDemandThreshold();

            BOOST_REQUIRE( !! disconnectDemandThreshold);
            BOOST_CHECK_EQUAL(*disconnectDemandThreshold, 0.0f);

            const boost::optional<Mct410DisconnectConfigurationCommand::DisconnectMode> mode = disconnectCommand.getDisconnectMode();

            BOOST_REQUIRE( !! mode);
            BOOST_CHECK_EQUAL(*mode, Mct410DisconnectConfigurationCommand::OnDemand);
        }
    }
}

BOOST_AUTO_TEST_CASE(test_command_errors)
{
    try
    {
        // Bad disconnect address
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::Cycling, 1 << 23, 0.0, 5, 5, 5, Mct410DisconnectConfigurationCommand::ButtonRequired, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid disconnect address (8388608), must be 0-4194303");
    }

    try
    {
        // Bad demand threshold
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::DemandThreshold, 0, -1.0, 5, 5, 5, Mct410DisconnectConfigurationCommand::ButtonRequired, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid disconnect demand threshold (-1), must be 0.0-400.0");
    }

    try
    {
        // Bad demand threshold
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::DemandThreshold, 0, 401.0, 5, 5, 5, Mct410DisconnectConfigurationCommand::ButtonRequired, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid disconnect demand threshold (401), must be 0.0-400.0");
    }

    try
    {
        // Bad connect delay
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::Cycling, 0, 0.0, 11, 5, 5, Mct410DisconnectConfigurationCommand::ButtonRequired, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid connect delay (11), must be 0-10");
    }

    try
    {
        // Bad disconnect minutes
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::Cycling, 0, 0.0, 5, 4, 5, Mct410DisconnectConfigurationCommand::ButtonRequired, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid number of disconnect minutes (4), must be 5-60");
    }

    try
    {
        // Bad disconnect minutes
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::Cycling, 0, 0.0, 5, 61, 5, Mct410DisconnectConfigurationCommand::ButtonRequired, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid number of disconnect minutes (61), must be 5-60");
    }

    try
    {
        // Bad connect minutes
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::Cycling, 0, 0.0, 5, 5, 4, Mct410DisconnectConfigurationCommand::ButtonRequired, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid number of connect minutes (4), must be 5-60");
    }

    try
    {
        // Bad connect minutes
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::Cycling, 0, 0.0, 5, 5, 61, Mct410DisconnectConfigurationCommand::ButtonRequired, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid number of connect minutes (61), must be 5-60");
    }

    try
    {
        // Bad demand interval
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::Cycling, 0, 0.0, 5, 5, 5, Mct410DisconnectConfigurationCommand::ButtonRequired, -1);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid demand interval (-1), must be a positive integer");
    }

    try
    {
        // Bad demand interval
        Mct410DisconnectConfigurationCommand disconnectCommand(Mct410DisconnectConfigurationCommand::Cycling, 0, 0.0, 5, 5, 5, Mct410DisconnectConfigurationCommand::ButtonRequired, 0);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid demand interval (0), must be a positive integer");
    }
}

BOOST_AUTO_TEST_SUITE_END()
