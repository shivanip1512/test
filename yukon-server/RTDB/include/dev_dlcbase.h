/*-----------------------------------------------------------------------------*
*
* File:   dev_dlcbase
*
* Class:  CtiDeviceDLCBase
* Date:   8/19/1999
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_dlcbase.h-arc  $
* REVISION     :  $Revision: 1.26 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_DLCBASE_H__
#define __DEV_DLCBASE_H__


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


class IM_EX_DEVDB CtiDeviceDLCBase : public CtiDeviceSingle
{
private:

    typedef CtiDeviceSingle Inherited;

    CtiTableDeviceRoute  getDeviceRoute() const;
    CtiTableDeviceRoute &getDeviceRoute();
    CtiDeviceDLCBase    &setDeviceRoute(const CtiTableDeviceRoute& aRoute);

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
                           OUTMESS                   *&OutMessage,
                           list< OUTMESS* >     &tmpOutList,
                           list< CtiMessage* >  &vgList,
                           list< CtiMessage* >  &retList,
                           list< OUTMESS* >     &outList,
                           bool                        wait );

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

    CtiDeviceDLCBase();
    CtiDeviceDLCBase(const CtiDeviceDLCBase& aRef);
    virtual ~CtiDeviceDLCBase();

    CtiDeviceDLCBase& operator=(const CtiDeviceDLCBase& aRef);

    CtiTableDeviceCarrier  getCarrierSettings() const;
    CtiTableDeviceCarrier &getCarrierSettings();
    CtiDeviceDLCBase      &setCarrierSettings( const CtiTableDeviceCarrier & aCarrierSettings );

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;

    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    // virtual void DecodeRoutesDatabaseReader(RWDBReader &rdr);

    virtual LONG getAddress() const;
    virtual LONG getRouteID() const;

    INT retMsgHandler( string commandStr, int status, CtiReturnMsg *retMsg, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, bool expectMore = false );
    INT decodeCheckErrorReturn(INMESS *InMessage, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    virtual bool processAdditionalRoutes( INMESS *InMessage ) const;
    virtual ULONG selectInitialMacroRouteOffset(LONG routeid = 0) const;
};

#endif // #ifndef __DEV_DLCBASE_H__
