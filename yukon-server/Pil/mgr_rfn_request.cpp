#include "precompiled.h"

#include "mgr_rfn_request.h"

namespace Cti {
namespace Pil {

using Devices::Commands::RfnCommand;
using Devices::Commands::RfnCommandSPtr;

namespace {

RfnRequestManager manager;

}


void RfnRequestManager::enqueueRequestsForDevice(const CtiDeviceBase &dev, const RfnDeviceRequestList &requests)
{
    enqueueRequestsForDevice(dev, requests, CtiTime::now());
}

void RfnRequestManager::enqueueRequestsForDevice(const CtiDeviceBase &dev, const RfnDeviceRequestList &requests, const CtiTime &Now)
{
    for each( const RfnDeviceRequest &request in requests )
    {
        try
        {
            /*RfnCommand::RfnRequest payload = request->executeCommand(Now);

            Messaging::Rfn::E2eDataRequestMsg msg;

            Messaging::ActiveMQConnectionManager::enqueueMessage()

            manager.insertCommand();*/
        }
        catch( Devices::Commands::DeviceCommand::CommandException &ce )
        {
            //  return error to requesting client via internal reporting back to PIL
        }
    }

    //  no-op for now
}


}
}
