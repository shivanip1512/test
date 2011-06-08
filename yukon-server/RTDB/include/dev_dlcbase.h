#pragma once

#include "dev_single.h"
#include "tbl_route.h"
#include "tbl_carrier.h"
#include "rte_base.h"
#include "prot_emetcon.h"

#include "cmd_dlc.h"

#include <set>
#include <map>

namespace Cti {
namespace Devices {

class IM_EX_DEVDB DlcBaseDevice : public CtiDeviceSingle
{
public:

    typedef boost::shared_ptr<Devices::Commands::DlcCommand> DlcCommandSPtr;
    typedef boost::weak_ptr  <Devices::Commands::DlcCommand> DlcCommandWPtr;

private:

    typedef CtiDeviceSingle Inherited;

    static unsigned int _lpRetryMultiplier;
    static unsigned int _lpRetryMinimum;
    static unsigned int _lpRetryMaximum;

    enum
    {
        DefaultLPRetryMultiplier = 3,       //  retry every 3 intervals
        DefaultLPRetryMinimum    = 900,     //  minimum retry rate is 15 minutes
        DefaultLPRetryMaximum    = 10800    //  maximum is 3 hours
    };

    typedef std::map<long, DlcCommandSPtr> active_command_map;

    active_command_map _activeCommands;

    long _activeIndex;

    long trackCommand(const DlcCommandSPtr &command);

protected:

    CtiTableDeviceCarrier      CarrierSettings;
    CtiTableDeviceRoute        DeviceRoutes;

    enum
    {
        LPBlockEvacuationTime    = 300,

        // Return value to allow non error returns
        // where error was handled in code.
        ExecutionComplete = NoError
    };

    enum ArmFlags
    {
        Q_ARML = 0x0020,
        Q_ARMS = 0x0040,
        Q_ARMC = 0x0080,
    };

    unsigned int getLPRetryRate( unsigned int interval );

    virtual INT executeLoopback ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList ) {  return NoMethod;  };
    virtual INT executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList ) {  return NoMethod;  };
    virtual INT executeGetConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList ) {  return NoMethod;  };
    virtual INT executeGetStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList ) {  return NoMethod;  };
    virtual INT executeControl  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList ) {  return NoMethod;  };
    virtual INT executePutValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList ) {  return NoMethod;  };
    virtual INT executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList ) {  return NoMethod;  };
    virtual INT executePutStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList ) {  return NoMethod;  };

    // Places error onto the retlist, DELETES OUT MESSAGE
    void returnErrorMessage( int retval, OUTMESS *&om, std::list< CtiMessage* > &retList, const std::string &error ) const;

    bool tryExecuteCommand(OUTMESS &OutMessage, DlcCommandSPtr command);

    int decodeCommand(const INMESS &InMessage, CtiTime TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    void fillOutMessage(OUTMESS &OutMessage, Devices::Commands::DlcCommand::request_t &request);

    int executeOnDLCRoute( CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           std::list< OUTMESS* >     &tmpOutList,
                           std::list< CtiMessage* >  &vgList,
                           std::list< CtiMessage* >  &retList,
                           std::list< OUTMESS* >     &outList,
                           bool                  broadcastWritesOnMacroSubroutes );

    virtual INT SubmitRetry(const INMESS &InMessage, const CtiTime TimeNow, std::list<CtiMessage *> &vgList, std::list<CtiMessage *> &retList, std::list<OUTMESS *> &outList);

    virtual INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, std::list<CtiMessage *> &vgList, std::list<CtiMessage *> &retList, std::list<OUTMESS *> &outList);

    INT retMsgHandler( std::string commandStr, int status, CtiReturnMsg *retMsg, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, bool expectMore = false ) const;
    INT decodeCheckErrorReturn(INMESS *InMessage, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

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
    DlcBaseDevice(const DlcBaseDevice& aRef);
    virtual ~DlcBaseDevice();

    DlcBaseDevice& operator=(const DlcBaseDevice& aRef);

    virtual std::string getSQLCoreStatement() const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual LONG getAddress() const;
    virtual LONG getRouteID() const;

    virtual INT ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    virtual bool processAdditionalRoutes( INMESS *InMessage ) const;
    virtual ULONG selectInitialMacroRouteOffset(LONG routeid = 0) const;
};

}
}

