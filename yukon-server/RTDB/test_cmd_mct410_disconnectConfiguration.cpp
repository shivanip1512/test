#include <boost/test/unit_test.hpp>

#include "cmd_mct410_disconnectConfiguration.h"
#include "ctidate.h"

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
        Mct410DisconnectConfigurationCommand disconnectCommand(0, 0.0, 5, 5, 5, Mct410DisconnectConfigurationCommand::Enabled, 300);

        const std::vector<unsigned char> expected = boost::assign::list_of
            (0x00)(0x00)(0x00)(0x00)(0x00)(0x05)(0x05)(0x05)(0x40);

        DlcCommand::request_ptr r = disconnectCommand.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1fe);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(r->length(),  9);

        const std::vector<unsigned char> actual = r->payload();

        BOOST_CHECK_EQUAL(actual.size(), 9);
        BOOST_CHECK_EQUAL_COLLECTIONS(actual.begin(), actual.end(),
                                      expected.begin(), expected.end());
    }

    {
        Mct410DisconnectConfigurationCommand disconnectCommand(264213, 400.0, 10, 60, 60, Mct410DisconnectConfigurationCommand::Disabled, 600);

        const std::vector<unsigned char> expected = boost::assign::list_of
            (0x04)(0x08)(0x15)(0x02)(0x9b)(0x0a)(0x03c)(0x3c)(0x44);

        DlcCommand::request_ptr r = disconnectCommand.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1fe);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(r->length(),  9);

        const std::vector<unsigned char> actual = r->payload();

        BOOST_CHECK_EQUAL(actual.size(), 9);
        BOOST_CHECK_EQUAL_COLLECTIONS(actual.begin(), actual.end(),
                                      expected.begin(), expected.end());
    }
}

BOOST_AUTO_TEST_CASE(test_command_errors)
{
    try
    {
        // Bad disconnect address
        Mct410DisconnectConfigurationCommand disconnectCommand(1 << 23, 0.0, 5, 5, 5, Mct410DisconnectConfigurationCommand::Enabled, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        BADPARAM);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid disconnect address (8388608), must be 0-4194303");
    }

    try
    {
        // Bad demand threshold
        Mct410DisconnectConfigurationCommand disconnectCommand(0, -1.0, 5, 5, 5, Mct410DisconnectConfigurationCommand::Enabled, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        BADPARAM);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid disconnect demand threshold (-1.0), must be 0.0-400.0");
    }

    try
    {
        // Bad demand threshold
        Mct410DisconnectConfigurationCommand disconnectCommand(0, 401.0, 5, 5, 5, Mct410DisconnectConfigurationCommand::Enabled, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        BADPARAM);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid disconnect demand threshold (401.0), must be 0.0-400.0");
    }

    try
    {
        // Bad connect delay
        Mct410DisconnectConfigurationCommand disconnectCommand(0, 0.0, 11, 5, 5, Mct410DisconnectConfigurationCommand::Enabled, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        BADPARAM);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid connect delay (11), must be 0-10");
    }

    try
    {
        // Bad disconnect minutes
        Mct410DisconnectConfigurationCommand disconnectCommand(0, 0.0, 5, 4, 5, Mct410DisconnectConfigurationCommand::Enabled, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        BADPARAM);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid number of disconnect minutes (4), must be 5-60");
    }

    try
    {
        // Bad disconnect minutes
        Mct410DisconnectConfigurationCommand disconnectCommand(0, 0.0, 5, 61, 5, Mct410DisconnectConfigurationCommand::Enabled, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        BADPARAM);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid number of disconnect minutes (61), must be 5-60");
    }

    try
    {
        // Bad connect minutes
        Mct410DisconnectConfigurationCommand disconnectCommand(0, 0.0, 5, 5, 4, Mct410DisconnectConfigurationCommand::Enabled, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        BADPARAM);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid number of connect minutes (4), must be 5-60");
    }

    try
    {
        // Bad connect minutes
        Mct410DisconnectConfigurationCommand disconnectCommand(0, 0.0, 5, 5, 61, Mct410DisconnectConfigurationCommand::Enabled, 300);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        BADPARAM);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid number of connect minutes (61), must be 5-60");
    }

    try
    {
        // Bad demand interval
        Mct410DisconnectConfigurationCommand disconnectCommand(0, 0.0, 5, 5, 5, Mct410DisconnectConfigurationCommand::Enabled, -1);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        BADPARAM);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid demand interval (-1), must be a positive integer");
    }

    try
    {
        // Bad demand interval
        Mct410DisconnectConfigurationCommand disconnectCommand(0, 0.0, 5, 5, 5, Mct410DisconnectConfigurationCommand::Enabled, 0);
        BOOST_FAIL("Mct410DisconnectCommand constructor did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        BADPARAM);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid demand interval (0), must be a positive integer");
    }
}

BOOST_AUTO_TEST_SUITE_END()
