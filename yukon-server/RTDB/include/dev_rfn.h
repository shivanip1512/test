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
public:
    RfnDevice() {};

    using RfnCommandList = Commands::RfnCommandList;

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    virtual std::string getSQLCoreStatement() const;
    void DecodeDatabaseReader(RowReader &rdr) override;

    RfnIdentifier getRfnIdentifier() const;

    virtual void extractCommandResult(const Commands::RfnCommand &command);
    virtual YukonError_t invokeDeviceHandler(DeviceHandler &handler);

    static Commands::RfnCommandPtr combineRfnCommands(RfnCommandList commands);

protected:

    typedef std::function<YukonError_t (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests)> ConfigMethod;

    /// Helper function to bind configuration method.
    template<typename Function, typename Caller>
    inline static ConfigMethod bindConfigMethod(Function f, Caller c)
    {
        return boost::bind( f, c, _1, _2, _3, _4 );
    }

    YukonError_t executeConfigInstallSingle(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests, const std::string &configPart, const ConfigMethod &configMethod );

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
