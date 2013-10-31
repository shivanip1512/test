#include "precompiled.h"

#include "std_helper.h"
#include "config_data_rfn.h"
#include "config_device.h"
#include "MissingConfigDataException.h"
#include "dev_rfnCommercial.h"
#include "devicetypes.h"

#include <boost/optional.hpp>
#include <boost/make_shared.hpp>
#include <boost/assign/list_of.hpp>
#include <boost/type_traits.hpp>

#include <limits>
#include <string>


namespace Cti {
namespace Devices {

int RfnCommercialDevice::executeImmediateDemandFreeze( CtiRequestMsg     * pReq,
                                                       CtiCommandParser  & parse,
                                                       ReturnMsgList     & returnMsgs,
                                                       RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnImmediateDemandFreezeCommand>() );

    return NoError;
}


int RfnCommercialDevice::executeReadDemandFreezeInfo( CtiRequestMsg     * pReq,
                                                      CtiCommandParser  & parse,
                                                      ReturnMsgList     & returnMsgs,
                                                      RfnCommandList    & rfnRequests )
{
    rfnRequests.push_back( boost::make_shared<Commands::RfnGetDemandFreezeInfoCommand>() );

    return NoError;
}


}
}

