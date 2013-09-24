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

    typedef int (RfnDevice::*InstallMethod)(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    typedef std::map<std::string, InstallMethod> InstallMap;

    virtual boost::optional<const InstallMap &> getGetConfigInstallMap() const;
    virtual boost::optional<const InstallMap &> getPutConfigInstallMap() const;

    virtual int executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executePutStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    virtual int executeImmediateDemandFreeze(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executeTouCriticalPeak(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    virtual int executePutConfigTou    (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executeGetConfigTou    (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executePutConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executeGetConfigHoliday(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

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

private:

    int  executeConfigInstall       (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests, const boost::optional<const InstallMap &> &installMap );
    void executeConfigInstallSingle (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests, const std::string &installValue, InstallMethod installMethod );
};

typedef RfnDevice Rfn410Device;
//typedef RfnDevice Rfn420CentronDevice;
//typedef RfnDevice Rfn420FocusDevice;
typedef RfnDevice Rfn430Device;

}
}


