#pragma once

#include "dev_rfn.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB RfDaDevice : public RfnDevice
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    RfDaDevice(const RfDaDevice&);
    RfDaDevice& operator=(const RfDaDevice&);

    typedef RfnDevice Inherited;

    virtual YukonError_t executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    virtual void handleCommandResult(const Commands::RfDaReadDnpSlaveAddressCommand &);

public:
    RfDaDevice() {};
    virtual ~RfDaDevice() {};
};

}
}


