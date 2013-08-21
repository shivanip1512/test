#pragma once

#include "dev_rfn.h"

namespace Cti {
namespace Pil {

class IM_EX_CTIPIL RfnRequestManager
{
public:

    static void enqueueRequestsForDevice(const CtiDeviceBase &dev, std::vector<Devices::Commands::RfnCommandSPtr> &requests);

protected:

    static void enqueueRequestsForDevice(const CtiDeviceBase &dev, std::vector<Devices::Commands::RfnCommandSPtr> &requests, const CtiTime &Now);

private:

};


}
}
