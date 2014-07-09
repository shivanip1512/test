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
    public boost::noncopyable,
    public Commands::RfnCommand::ResultHandler  //  default implementation, to be overridden by child classes
{
public:

    typedef std::vector<Commands::RfnCommandSPtr> RfnCommandList;
    typedef boost::ptr_deque<CtiReturnMsg> ReturnMsgList;

    virtual int ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    virtual std::string getSQLCoreStatement() const;
    virtual void DecodeDatabaseReader(RowReader &rdr);

    RfnIdentifier getRfnIdentifier() const;

    virtual void extractCommandResult(const Commands::RfnCommand &command);
    virtual int  invokeDeviceHandler(DeviceHandler &handler);

protected:

    virtual YukonError_t executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual YukonError_t executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual YukonError_t executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual YukonError_t executePutValue (CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual YukonError_t executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);
    virtual YukonError_t executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, ReturnMsgList &returnMsgs, RfnCommandList &rfnRequests);

    RfnIdentifier _rfnId;

    void logInfo( const std::string &note,
                  const char* function,
                  const char* file,
                  int line ) const;

    void logInfo( int debugLevel,
                  const std::string &note,
                  const char* function,
                  const char* file,
                  int line ) const;
};

}
}
