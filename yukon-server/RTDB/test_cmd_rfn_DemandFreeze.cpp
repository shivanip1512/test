#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "cmd_rfn_DemandFreeze.h"


using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnDemandFreezeConfigurationCommand;
using Cti::Devices::Commands::RfnImmediateDemandFreezeCommand;
using Cti::Devices::Commands::RfnGetDemandFreezeInfoCommand;

using boost::assign::list_of;
using boost::assign::pair_list_of;



BOOST_AUTO_TEST_SUITE( test_cmd_rfn_DemandFreeze )


const CtiTime execute_time( CtiDate( 29, 7, 2013 ) , 11 );


struct test_ResultHandler : RfnGetDemandFreezeInfoCommand::ResultHandler
{
    void handleResult(const RfnGetDemandFreezeInfoCommand &cmd)
    {
        //  This is temporarily marked BOOST_FAIL - the unit test should eventually exercise this.
        BOOST_FAIL("Should not reach this code!");
    }
};


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_SetFreezeDay )
{
    RfnDemandFreezeConfigurationCommand command( 10 );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x02 )( 0x0a );

        RfnCommand::RfnRequest rcv = command.execute( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x56 )( 0x00 )( 0x00 )( 0x00 )( 0x00 );

        RfnCommand::RfnResult rcv = command.decode( execute_time, response );

        BOOST_REQUIRE_EQUAL( rcv.description, "Status: Success (0x00)"
                                              "\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_SetFreezeDay_decoding_exceptions )
{
    const std::vector< RfnCommand::RfnResponse >   responses = list_of
        ( list_of( 0x57 )( 0x00 )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x00 )( 0x00 )( 0x00 )( 0x01 ) )
        ( list_of( 0x56 )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x00 )( 0x00 )( 0x00 )( 0x00 )( 0x00) );

    const std::vector< std::pair< int, std::string > >  exceptions = pair_list_of
        ( ErrorInvalidData, "Invalid Response Command Code (0x57)" )
        ( ErrorInvalidData, "Invalid TLV count (1)" )
        ( ErrorInvalidData, "Invalid Response length (4)" )
        ( ErrorInvalidData, "Invalid Response length (6)" );

    BOOST_REQUIRE( responses.size() == exceptions.size() );

    RfnDemandFreezeConfigurationCommand command( 10 );

    for ( int i = 0; i < responses.size(); i++ )
    {
        BOOST_CHECK_THROW( command.decode( execute_time, responses[i] ), RfnCommand::CommandException );

        try
        {
            RfnCommand::RfnResult rcv = command.decode( execute_time, responses[i] );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_REQUIRE_EQUAL( ex.error_code, exceptions[i].first );
            BOOST_REQUIRE_EQUAL( ex.what(),     exceptions[i].second );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_SetFreezeDay_status_exceptions )
{
    const std::vector< RfnCommand::RfnResponse >   responses = list_of
        ( list_of( 0x56 )( 0x01 )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x02 )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x03 )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x04 )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x05 )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x06 )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x07 )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x08 )( 0x00 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x00 )( 0x01 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x00 )( 0x02 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x00 )( 0x03 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x00 )( 0x04 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x00 )( 0x05 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x00 )( 0x06 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x00 )( 0x07 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x00 )( 0x08 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x00 )( 0x09 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x00 )( 0x0a )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x01 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x01 )( 0x01 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x01 )( 0x02 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x01 )( 0x03 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x02 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x0f )( 0x03 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x00 )( 0x04 )( 0x00 )( 0x00 ) )
        ( list_of( 0x56 )( 0x00 )( 0x00 )( 0x0f )( 0x00 ) );

    const std::vector< std::pair< int, std::string > >  exceptions = pair_list_of
        ( ErrorInvalidData, "Status: Not Ready (0x01)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" )
        ( ErrorInvalidData, "Status: Busy (0x02)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" )
        ( ErrorInvalidData, "Status: Protocol Error (0x03)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" )
        ( ErrorInvalidData, "Status: Meter Error (0x04)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" )
        ( ErrorInvalidData, "Status: Illegal Request (0x05)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" )
        ( ErrorInvalidData, "Status: Aborted Command (0x06)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" )
        ( ErrorInvalidData, "Status: Timeout (0x07)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" )
        ( ErrorInvalidData, "Status: Reserved (0x08)\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, SERVICE NOT SUPPORTED (ASC: 0x00, ASCQ: 0x01)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, INVALID FIELD IN COMMAND (ASC: 0x00, ASCQ: 0x02)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, INAPPROPRIATE ACTION REQUESTED (ASC: 0x00, ASCQ: 0x03)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, LOAD VOLTAGE HIGHER THAN THRESHOLD (ASC: 0x00, ASCQ: 0x04)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, SWITCH IS OPEN (ASC: 0x00, ASCQ: 0x05)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, TEST MODE ENABLED (ASC: 0x00, ASCQ: 0x06)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, SERVICE DISCONNECT BUTTON PRESSED BUT METER NOT ARMED (ASC: 0x00, ASCQ: 0x07)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, SERVICE DISCONNECT NOT ENABLED (ASC: 0x00, ASCQ: 0x08)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, SERVICE DISCONNECT IS CURRENTLY CHARGING (ASC: 0x00, ASCQ: 0x09)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: REJECTED, SERVICE DISCONNECT IN OPERATION (ASC: 0x00, ASCQ: 0x0a)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: ACCESS DENIED, INSUFFICIENT SECURITY CLEARANCE (ASC: 0x01, ASCQ: 0x00)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: ACCESS DENIED, DATA LOCKED (ASC: 0x01, ASCQ: 0x01)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: ACCESS DENIED, INVALID SERVICE SEQUENCE STATE (ASC: 0x01, ASCQ: 0x02)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: ACCESS DENIED, RENEGOTIATE REQUEST (ASC: 0x01, ASCQ: 0x03)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: DATA NOT READY (ASC: 0x02, ASCQ: 0x00)" )
        ( ErrorInvalidData, "Status: Reserved (0x0f)\nAdditional Status: DEVICE BUSY (ASC: 0x03, ASCQ: 0x00)" )
        ( ErrorInvalidData, "Invalid Additional Status (ASC: 0x04, ASCQ: 0x00)" )
        ( ErrorInvalidData, "Invalid Additional Status (ASC: 0x00, ASCQ: 0x0f)" );

    BOOST_REQUIRE( responses.size() == exceptions.size() );

    RfnDemandFreezeConfigurationCommand command( 10 );

    for ( int i = 0; i < responses.size(); i++ )
    {
        BOOST_CHECK_THROW( command.decode( execute_time, responses[i] ), RfnCommand::CommandException );

        try
        {
            RfnCommand::RfnResult rcv = command.decode( execute_time, responses[i] );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            BOOST_REQUIRE_EQUAL( ex.error_code, exceptions[i].first );
            BOOST_REQUIRE_EQUAL( ex.what(),     exceptions[i].second );
        }
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_ImmediateFreeze )
{
    RfnImmediateDemandFreezeCommand command;

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x01 );

        RfnCommand::RfnRequest rcv = command.execute( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x56 )( 0x00 )( 0x00 )( 0x00 )( 0x00 );

        RfnCommand::RfnResult rcv = command.decode( execute_time, response );

        BOOST_REQUIRE_EQUAL( rcv.description, "Status: Success (0x00)"
                                              "\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" );
    }
}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_GetFreezeInfo )
{
    test_ResultHandler  rh;

    RfnGetDemandFreezeInfoCommand   command( rh );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            ( 0x55 )( 0x03 );

        RfnCommand::RfnRequest rcv = command.execute( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode -- success response
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            ( 0x56 )
            ( 0x00 )( 0x00 )( 0x00 )
            // plus a bunch of freeze day info...
                ;

        RfnCommand::RfnResult rcv = command.decode( execute_time, response );

        BOOST_REQUIRE_EQUAL( rcv.description, "Status: Success (0x00)"
                                              "\nAdditional Status: NO ADDITIONAL STATUS (ASC: 0x00, ASCQ: 0x00)" );
    }


}


BOOST_AUTO_TEST_SUITE_END()

