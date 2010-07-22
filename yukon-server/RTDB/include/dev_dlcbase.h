#pragma once

#include "dev_single.h"
#include "tbl_route.h"
#include "tbl_carrier.h"
#include "dlldefs.h"
#include "rte_base.h"
#include "logger.h"
#include "prot_emetcon.h"

#include <set>
#include <utility>
using std::set;


namespace Cti {
namespace Devices {

class IM_EX_DEVDB DlcBaseDevice : public CtiDeviceSingle
{
private:

    typedef CtiDeviceSingle Inherited;

    CtiTableDeviceRoute  getDeviceRoute() const;
    CtiTableDeviceRoute &getDeviceRoute();
    DlcBaseDevice           &setDeviceRoute(const CtiTableDeviceRoute& aRoute);

    static unsigned int _lpRetryMultiplier;
    static unsigned int _lpRetryMinimum;
    static unsigned int _lpRetryMaximum;

    enum
    {
        DefaultLPRetryMultiplier = 3,       //  retry every 3 intervals
        DefaultLPRetryMinimum    = 900,     //  minimum retry rate is 15 minutes
        DefaultLPRetryMaximum    = 10800    //  maximum is 3 hours
    };

protected:

    CtiTableDeviceCarrier      CarrierSettings;
    CtiTableDeviceRoute        DeviceRoutes;

    enum
    {
        LPBlockEvacuationTime    = 300
    };

    enum ArmFlags
    {
        Q_ARML = 0x0020,
        Q_ARMS = 0x0040,
        Q_ARMC = 0x0080,
    };

    unsigned int getLPRetryRate( unsigned int interval );

    int executeOnDLCRoute( CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           list< OUTMESS* >     &tmpOutList,
                           list< CtiMessage* >  &vgList,
                           list< CtiMessage* >  &retList,
                           list< OUTMESS* >     &outList,
                           bool                  broadcastWritesOnMacroSubroutes );

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

    typedef set< CommandStore > CommandSet;

public:

    enum
    {
        BroadcastAddress = 0x3fffff
    };

    DlcBaseDevice();
    DlcBaseDevice(const DlcBaseDevice& aRef);
    virtual ~DlcBaseDevice();

    DlcBaseDevice& operator=(const DlcBaseDevice& aRef);

    CtiTableDeviceCarrier  getCarrierSettings() const;
    CtiTableDeviceCarrier &getCarrierSettings();
    DlcBaseDevice      &setCarrierSettings( const CtiTableDeviceCarrier & aCarrierSettings );

    virtual string getSQLCoreStatement() const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual LONG getAddress() const;
    virtual LONG getRouteID() const;

    INT retMsgHandler( string commandStr, int status, CtiReturnMsg *retMsg, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, bool expectMore = false ) const;
    INT decodeCheckErrorReturn(INMESS *InMessage, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    virtual bool processAdditionalRoutes( INMESS *InMessage ) const;
    virtual ULONG selectInitialMacroRouteOffset(LONG routeid = 0) const;
};

}
}

