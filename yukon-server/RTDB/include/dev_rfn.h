#pragma once

#include <dev_single.h>

#include "rfn_identifier.h"
#include "cmd_rfn.h"

#include <boost/ptr_container/ptr_deque.hpp>
#include <boost/container/flat_map.hpp>

namespace Cti {
namespace Devices {

class IM_EX_DEVDB RfnDevice :
    public CtiDeviceSingle,
    public Commands::RfnCommand::ResultHandler  //  default implementation, to be overridden by child classes
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    RfnDevice(const RfnDevice&);
    RfnDevice& operator=(const RfnDevice&);

public:
    RfnDevice() {};

    typedef std::vector<Commands::RfnCommandSPtr> RfnCommandList;
    typedef boost::ptr_deque<CtiReturnMsg> ReturnMsgList;

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    virtual std::string getSQLCoreStatement() const;
    void DecodeDatabaseReader(RowReader &rdr) override;

    RfnIdentifier getRfnIdentifier() const;

    virtual void extractCommandResult(const Commands::RfnCommand &command);
    virtual YukonError_t invokeDeviceHandler(DeviceHandler &handler);

protected:

    virtual YukonError_t executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual YukonError_t executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual YukonError_t executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual YukonError_t executePutValue (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual YukonError_t executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual YukonError_t executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    RfnIdentifier _rfnId;
};

}
}
