#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "ctidate.h"
#include "cmd_rf_da_dnpAddress.h"

using Cti::Devices::Commands::RfnCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfDaReadDnpSlaveAddressCommand;

using boost::assign::list_of;
using boost::assign::pair_list_of;

// --- defined in RTDB\test_main.cpp -- so BOOST_CHECK_EQUAL_COLLECTIONS() works for RfnCommand::CommandException
namespace boost         {
namespace test_tools    {
    bool operator!=( const RfnCommand::CommandException & lhs, const RfnCommand::CommandException & rhs );
}
}

namespace std   {
    ostream & operator<<( ostream & os, const RfnCommand::CommandException & ex );
}
// ---

BOOST_AUTO_TEST_SUITE( test_cmd_rfn_LoadProfile )

const CtiTime execute_time( CtiDate( 6, 3, 2014 ) , 12 );

BOOST_AUTO_TEST_CASE( test_cmd_rf_da_dnpAddress )
{
    RfDaReadDnpSlaveAddressCommand cmd;

    BOOST_CHECK_EQUAL( static_cast<unsigned>(cmd.getApplicationServiceId()), 0x81 );

    // execute
    {
        const std::vector< unsigned char > exp = boost::assign::list_of
            (0x35);

        RfnCommand::RfnRequestPayload rcv = cmd.executeCommand( execute_time );

        BOOST_CHECK_EQUAL_COLLECTIONS( rcv.begin() , rcv.end() ,
                                       exp.begin() , exp.end() );
    }

    // decode
    {
        const std::vector< unsigned char > response = boost::assign::list_of
            (0x36)(0x12)(0x34);

        RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );

        BOOST_CHECK_EQUAL( rcv.description, "Outstation DNP3 address: 4660" );
        BOOST_CHECK_EQUAL( rcv.points.size(), 0 );
    }
}

BOOST_AUTO_TEST_CASE( test_cmd_rf_da_dnpAddress_decoding_exceptions )
{
	const std::vector< RfnCommand::RfnResponsePayload > responses = {
			{ 0xcc, 0xbb},
			{ 0xcc, 0xbb, 0xaa, 0x99},
			{ 0xcc, 0xbb, 0xaa}
	};

    const std::vector< RfnCommand::CommandException >   expected = list_of
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response length (2)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid Response length (4)" ) )
        ( RfnCommand::CommandException( ClientErrors::InvalidData, "Invalid response code (204)" ) );

    std::vector< RfnCommand::CommandException > actual;

    RfDaReadDnpSlaveAddressCommand cmd;

    for each ( const RfnCommand::RfnResponsePayload & response in responses )
    {
        BOOST_CHECK_THROW( cmd.decodeCommand( execute_time, response ), RfnCommand::CommandException );

        try
        {
            RfnCommandResult rcv = cmd.decodeCommand( execute_time, response );
        }
        catch ( const RfnCommand::CommandException & ex )
        {
            actual.push_back( ex );
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS( actual.begin(),   actual.end(),
                                   expected.begin(), expected.end() );
}

BOOST_AUTO_TEST_SUITE_END()
