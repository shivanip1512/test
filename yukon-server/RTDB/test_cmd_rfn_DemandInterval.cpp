#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "cmd_rfn_DemandInterval.h"
#include "boost_test_helpers.h"

using namespace std::chrono_literals;

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_DemandInterval )


const CtiTime execute_time( CtiDate( 29, 7, 2013 ) , 11 );


BOOST_AUTO_TEST_CASE( test_setConfigurationCommand )
{
    Cti::Devices::Commands::RfnDemandIntervalSetConfigurationCommand command( std::chrono::minutes { 10 } );

    // execute
    {
        const std::vector< unsigned char > exp = { 0x62, 0x0a };

        auto rcv = command.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_RANGES( rcv, exp );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response { 0x63, 0x00 };

        auto rcv = command.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Successfully set demand interval to 10 minutes" );
    }
}


BOOST_AUTO_TEST_CASE( test_setConfigurationCommand_failure )
{
    Cti::Devices::Commands::RfnDemandIntervalSetConfigurationCommand command( std::chrono::minutes { 10 } );

    command.executeCommand( execute_time );

    // decode -- failure response
    const std::vector< unsigned char > response { 0x63, 0x01 };

    try
    {
        command.decodeCommand(execute_time, response);

        BOOST_FAIL("Did not throw");
    }
    catch( const Cti::YukonErrorException & ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code, 1);
        BOOST_CHECK_EQUAL(ex.error_description, "Set command failed with error code 1");
    }
}


BOOST_AUTO_TEST_CASE( test_setConfigurationCommand_exceptions )
{
    try
    {
        Cti::Devices::Commands::RfnDemandIntervalSetConfigurationCommand command(std::chrono::minutes { 0 });

        BOOST_FAIL("Did not throw");
    }
    catch( const Cti::YukonErrorException & ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code, 26);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid interval, must be greater than 0 (0 minutes)");
    }

    try
    {
        Cti::Devices::Commands::RfnDemandIntervalSetConfigurationCommand command(std::chrono::minutes { 99 });

        BOOST_FAIL("Did not throw");
    }
    catch( const Cti::YukonErrorException & ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code, 26);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid interval, must be less than 61 (99 minutes)");
    }
}


BOOST_AUTO_TEST_CASE(test_getConfigurationCommand)
{
    Cti::Devices::Commands::RfnDemandIntervalGetConfigurationCommand command;

    // execute
    {
        const std::vector< unsigned char > exp = { 0x64 };

        auto rcv = command.executeCommand(execute_time);

        BOOST_CHECK_EQUAL_RANGES(rcv, exp);
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response{ 0x65, 0x0d };

        auto rcv = command.decodeCommand(execute_time, response);

        BOOST_CHECK_EQUAL( command.getDemandInterval().count(), 13 );

        BOOST_CHECK_EQUAL(rcv.description, "Demand interval: 13 minutes");
    }
}

BOOST_AUTO_TEST_SUITE_END()