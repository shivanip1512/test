#pragma once

#include <dev_single.h>

#include "rfn_identifier.h"
#include "cmd_rfn.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB RfnDevice :
    public CtiDeviceSingle,
    public boost::noncopyable,
    public Commands::RfnCommand::ResultHandler  //  default implementation, to be overridden by child classes
{
public:

    typedef std::vector<Commands::RfnCommandSPtr> RfnCommandList;

    virtual int ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

    virtual std::string getSQLCoreStatement() const;
    virtual void DecodeDatabaseReader(RowReader &rdr);

    RfnIdentifier getRfnIdentifier() const;

    virtual void extractCommandResult(const Commands::RfnCommand &command);
    virtual int  invokeDeviceHandler(DeviceHandler &handler);

protected:

    typedef int (RfnDevice::*InstallMethod)(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    typedef std::map<std::string, InstallMethod> InstallMap;

    const static InstallMap emptyMap;

    virtual const InstallMap & getGetConfigInstallMap() const;
    virtual const InstallMap & getPutConfigInstallMap() const;

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

    virtual int executeGetConfigVoltageProfile (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executePutConfigVoltageProfile (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);
    virtual int executeGetValueVoltageProfile  (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests);

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

    int  executeConfigInstall       (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests, const InstallMap &installMap );
    void executeConfigInstallSingle (CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &retList, RfnCommandList &rfnRequests, const std::string &installValue, InstallMethod installMethod );
};

typedef RfnDevice Rfn410flDevice;  //  kWh only

typedef RfnDevice Rfn430a3dDevice;
typedef RfnDevice Rfn430a3tDevice;
typedef RfnDevice Rfn430a3kDevice;
typedef RfnDevice Rfn430a3rDevice;
typedef RfnDevice Rfn430kvDevice;

}
}
