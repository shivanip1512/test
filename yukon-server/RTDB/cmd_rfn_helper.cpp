#include "precompiled.h"

#include "cmd_rfn_helper.h"
#include "std_helper.h"

#include <boost/assign/list_of.hpp>

namespace Cti {
namespace Devices {
namespace Commands {


namespace   {

const std::map< std::pair<unsigned char, unsigned char>, std::string>  ascAscqResolver = boost::assign::map_list_of
    ( std::make_pair( 0x00, 0x00 ), "NO ADDITIONAL STATUS" )
    ( std::make_pair( 0x00, 0x01 ), "REJECTED, SERVICE NOT SUPPORTED" )
    ( std::make_pair( 0x00, 0x02 ), "REJECTED, INVALID FIELD IN COMMAND" )
    ( std::make_pair( 0x00, 0x03 ), "REJECTED, INAPPROPRIATE ACTION REQUESTED" )
    ( std::make_pair( 0x00, 0x04 ), "REJECTED, LOAD VOLTAGE HIGHER THAN THRESHOLD" )
    ( std::make_pair( 0x00, 0x05 ), "REJECTED, SWITCH IS OPEN" )
    ( std::make_pair( 0x00, 0x06 ), "REJECTED, TEST MODE ENABLED" )
    ( std::make_pair( 0x00, 0x07 ), "REJECTED, SERVICE DISCONNECT BUTTON PRESSED BUT METER NOT ARMED" )
    ( std::make_pair( 0x00, 0x08 ), "REJECTED, SERVICE DISCONNECT NOT ENABLED" )
    ( std::make_pair( 0x00, 0x09 ), "REJECTED, SERVICE DISCONNECT IS CURRENTLY CHARGING" )
    ( std::make_pair( 0x00, 0x0a ), "REJECTED, SERVICE DISCONNECT IN OPERATION" )
    ( std::make_pair( 0x01, 0x00 ), "ACCESS DENIED, INSUFFICIENT SECURITY CLEARANCE" )
    ( std::make_pair( 0x01, 0x01 ), "ACCESS DENIED, DATA LOCKED" )
    ( std::make_pair( 0x01, 0x02 ), "ACCESS DENIED, INVALID SERVICE SEQUENCE STATE" )
    ( std::make_pair( 0x01, 0x03 ), "ACCESS DENIED, RENEGOTIATE REQUEST" )
    ( std::make_pair( 0x02, 0x00 ), "DATA NOT READY" )
    ( std::make_pair( 0x03, 0x00 ), "DEVICE BUSY" )
    ( std::make_pair( 0x04, 0x00 ), "SCHEDULED FOR NEXT RECORD INTERVAL" );

}


boost::optional<std::string> findDescriptionForAscAsq( const unsigned char asc, const unsigned char asq )
{
    return mapFind(ascAscqResolver, std::make_pair(asc, asq));
}


}
}
}
