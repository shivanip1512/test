#pragma once

#include "dev_rfn.h"

namespace Cti {
namespace Pil {

struct RfnDeviceRequest
{
    Devices::Commands::RfnCommandSPtr command;
    Devices::RfnIdentifier rfnIdentifier;
    long deviceId;
    std::string commandString;
    unsigned priority;
    long userMessageId;
    long groupMessageId;
    void *connectionHandle;

    bool operator<(const RfnDeviceRequest &rhs) const
    {
        return priority < rhs.priority;
    }
};

struct RfnDeviceResult
{
    RfnDeviceRequest request;
    Devices::Commands::RfnCommandResult commandResult;
    int status;
};

class IM_EX_CTIPIL RfnRequestManager
{
public:

    typedef std::vector<RfnDeviceRequest> RfnDeviceRequestList;

    static void enqueueRequestsForDevice(const CtiDeviceBase &dev, const RfnDeviceRequestList &requests);

protected:

    static void enqueueRequestsForDevice(const CtiDeviceBase &dev, const RfnDeviceRequestList &requests, const CtiTime &Now);

private:

};


}
}
