#pragma once

#include "dev_single.h"
#include "tbl_route.h"
#include "tbl_carrier.h"
#include "rte_base.h"
#include "prot_emetcon.h"

#include "cmd_dlc.h"

#include <boost/ptr_container/ptr_map.hpp>

#include <set>

namespace Cti {
namespace Devices {

class IM_EX_DEVDB DlcBaseDevice :
    public CtiDeviceSingle,
    protected Commands::DlcCommand::ResultHandler
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    DlcBaseDevice(const DlcBaseDevice&);
    DlcBaseDevice& operator=(const DlcBaseDevice&);

public:

    typedef std::auto_ptr<Commands::DlcCommand> DlcCommandAutoPtr;
    static bool dlcAddressMismatch(const DSTRUCT Dst, const CtiDeviceBase & temDevice);

private:

    typedef DlcBaseDevice Self;
    typedef CtiDeviceSingle Parent;

    typedef int (Self::*ExecuteMethod)(CtiRequestMsg *, CtiCommandParser &, OUTMESS *&, CtiMessageList &, CtiMessageList &, OutMessageList &);

    static const std::map<CtiClientRequest_t, ExecuteMethod> _executeMethods;
    static const std::map<CtiClientRequest_t, ExecuteMethod> buildExecuteMethodMap();

    static unsigned int _lpRetryMultiplier;
    static unsigned int _lpRetryMinimum;
    static unsigned int _lpRetryMaximum;

    enum
    {
        DefaultLPRetryMultiplier = 3,       //  retry every 3 intervals
        DefaultLPRetryMinimum    = 900,     //  minimum retry rate is 15 minutes
        DefaultLPRetryMaximum    = 10800    //  maximum is 3 hours
    };

    typedef boost::ptr_map<long, Commands::DlcCommand> active_command_map;

    active_command_map _activeCommands;

    long _activeIndex;

    long trackCommand(DlcCommandAutoPtr command);

protected:

    CtiTableDeviceCarrier      CarrierSettings;
    CtiTableDeviceRoute        DeviceRoutes;

    enum
    {
        LPBlockEvacuationTime    = 300,
    };

    enum ArmFlags
    {
        Q_ARML = 0x0020,
        Q_ARMS = 0x0040,
        Q_ARMC = 0x0080,
    };

    unsigned int getLPRetryRate( unsigned int interval );

    virtual INT executeLoopback ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) {  return ClientErrors::NoMethod;  };
    virtual INT executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) {  return ClientErrors::NoMethod;  };
    virtual INT executeGetConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) {  return ClientErrors::NoMethod;  };
    virtual INT executeGetStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) {  return ClientErrors::NoMethod;  };
    virtual INT executeControl  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) {  return ClientErrors::NoMethod;  };
    virtual INT executePutValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) {  return ClientErrors::NoMethod;  };
    virtual INT executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) {  return ClientErrors::NoMethod;  };
    virtual INT executePutStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) {  return ClientErrors::NoMethod;  };

    bool tryExecuteCommand(OUTMESS &OutMessage, DlcCommandAutoPtr command);

    virtual void handleCommandResult(const Commands::DlcCommand &command);

    void findAndDecodeCommand(const INMESS &InMessage, CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    void fillOutMessage(OUTMESS &OutMessage, Devices::Commands::DlcCommand::request_t &request);

    void populateDlcOutMessage(OUTMESS &OutMessage);

    int executeOnDLCRoute( CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OutMessageList     &tmpOutList,
                           CtiMessageList  &vgList,
                           CtiMessageList  &retList,
                           OutMessageList     &outList,
                           bool                  broadcastWritesOnMacroSubroutes );

    INT SubmitRetry (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    INT ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    INT retMsgHandler( std::string commandStr, int status, CtiReturnMsg *retMsg, CtiMessageList &vgList, CtiMessageList &retList, bool expectMore = false ) const;

    class CommandStore
    {
    public:

        UINT cmd, io, function, length;

        CommandStore() :
            cmd(0),    // == DLCCmd_Invalid
            io(0),
            function(0),
            length(0)
        {
        }

        explicit CommandStore( UINT command ) :
            cmd(command),
            io(0),
            function(0),
            length(0)
        {
        }

        CommandStore( UINT command, UINT io, UINT function, UINT length ) :
            cmd(command),
            io(io),
            function(function),
            length(length)
        {
        }

        bool operator<( const CommandStore &rhs ) const
        {
            return( cmd < rhs.cmd );
        }
    };

    typedef std::set< CommandStore > CommandSet;

public:

    enum
    {
        BroadcastAddress = 0x3fffff
    };

    DlcBaseDevice();

    virtual std::string getSQLCoreStatement() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr) override;

    virtual LONG getAddress() const;
    virtual LONG getRouteID() const;

    INT ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    virtual bool processAdditionalRoutes( const INMESS &InMessage, int nRet ) const;
    virtual MacroOffset selectInitialMacroRouteOffset(LONG routeid = 0) const;
};

}
}

