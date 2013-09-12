#pragma once

#include <dev_single.h>

#include "rfn_identifier.h"
#include "cmd_rfn.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB RfnDevice : public CtiDeviceSingle
{
public:

    typedef std::vector<Commands::RfnCommandSPtr> RfnCommandList;

    virtual int ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    struct RfnRequestExecuter : virtual RequestExecuter
    {
        RfnCommandList rfnRequests;

        RfnRequestExecuter(CtiRequestMsg *pReq_, CtiCommandParser &parse_) :
            RequestExecuter(pReq_, parse_)
        {}

        virtual int execute(RfnDevice &dev)
        {
            return dev.ExecuteRequest(pReq, parse, retList, rfnRequests);
        }
    };

    virtual std::string getSQLCoreStatement() const;
    virtual void DecodeDatabaseReader(RowReader &rdr);

    virtual int invokeRequestExecuter(RequestExecuter &executer)
    {
        return executer.execute(*this);
    }

protected:

    virtual int executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    int executePutConfigInstall (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    Commands::RfnCommandSPtr executePutConfigInstallFreezeDay               (CtiRequestMsg *pReq, CtiCommandParser &parse);
    Commands::RfnCommandSPtr executePutConfigInstallVoltageAveragingInterval(CtiRequestMsg *pReq, CtiCommandParser &parse);
    Commands::RfnCommandSPtr executePutConfigInstallTou                     (CtiRequestMsg *pReq, CtiCommandParser &parse);
    virtual Commands::RfnCommandSPtr executePutConfigInstallDisplay         (CtiRequestMsg *pReq, CtiCommandParser &parse);

    virtual int executeImmediateDemandFreeze(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executeTouCriticalPeak(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    virtual int executePutConfigTou    (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executeGetConfigTou    (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executePutConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executeGetConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    RfnIdentifier _rfnId;
};

typedef RfnDevice Rfn410Device;
//typedef RfnDevice Rfn420CentronDevice;
typedef RfnDevice Rfn420FocusDevice;
typedef RfnDevice Rfn430Device;

}
}


