#pragma once

#include "dev_rfn.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB RfDaDevice : public RfnDevice
{
    typedef RfnDevice Inherited;

    virtual YukonError_t executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    virtual void handleCommandResult(const Commands::RfDaReadDnpSlaveAddressCommand &);

public:
    RfDaDevice() {};
};

}
}


