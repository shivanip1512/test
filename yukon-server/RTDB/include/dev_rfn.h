#pragma once

#include <dev_single.h>

#include "rfn_identifier.h"
#include "cmd_rfn_Individual.h"

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

protected:

    using RfnIndividualCommandList = Commands::RfnIndividualCommandList;

    using ConfigMethod = std::function<YukonError_t (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests)>;

    /// Helper function to bind configuration method.
    template<typename Function, typename Caller>
    inline static ConfigMethod bindConfigMethod(Function f, Caller c)
    {
        return [f, c](CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests) {
            return (c->*f)(pReq, parse, returnMsgs, rfnRequests);
        };
    }

    virtual bool areAggregateCommandsSupported() const;

    std::unique_ptr<CtiReturnMsg> makeReturnMsg(const CtiRequestMsg& req, const std::string result, YukonError_t nRet);

    YukonError_t executeConfigInstallSingle(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests, const std::string &configPart, const ConfigMethod &configMethod );

    virtual YukonError_t executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executePutValue (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);
    virtual YukonError_t executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnIndividualCommandList &rfnRequests);

    RfnIdentifier _rfnId;
};

using RfnDeviceSPtr = boost::shared_ptr<RfnDevice>;

}
}
