#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "cmd_rfn_DemandFreeze.h"


using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnDemandFreezeConfigurationCommand;
using Cti::Devices::Commands::RfnImmediateDemandFreezeCommand;
using Cti::Devices::Commands::RfnGetDemandFreezeInfoCommand;

using boost::assign::list_of;



BOOST_AUTO_TEST_SUITE( test_cmd_rfn_DemandFreeze )


const CtiTime execute_time( CtiDate( 29, 7, 2013 ) , 11 );


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

    // decode -- TBD


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

    // decode -- TBD


}


BOOST_AUTO_TEST_CASE( test_cmd_rfn_DemandFreeze_GetFreezeInfo )
{
    RfnGetDemandFreezeInfoCommand   command;

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

