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
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2004/12/07 17:56:01 $
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
#include "mgr_route.h"
#include "logger.h"
#include "prot_emetcon.h"

#include <set>
#include <utility>
using namespace std;

class CtiDLCCommandStore
{
public:

    UINT _cmd;  // Command
    UINT _io;   // Function read/write ?

    pair< UINT, UINT > _funcLen;  // Function and Length.


    CtiDLCCommandStore() :
    _cmd(0),    // == DLCCmd_Invalid
    _io(0)
    {
    }

    explicit CtiDLCCommandStore( const UINT &cmd ) :
    _cmd(cmd),
    _io(0)
    {
    }

    bool operator<( const CtiDLCCommandStore &rhs ) const
    {
        return( _cmd < rhs._cmd );
    }
};

class IM_EX_DEVDB CtiDeviceDLCBase : public CtiDeviceSingle
{
private:

    CtiTableDeviceRoute  getDeviceRoute() const;
    CtiTableDeviceRoute& getDeviceRoute();
    CtiDeviceDLCBase& setDeviceRoute(const CtiTableDeviceRoute& aRoute);

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

    unsigned int getLPRetryRate( unsigned int interval );

    int executeOnDLCRoute( CtiRequestMsg              *pReq,
                           CtiCommandParser           &parse,
                           OUTMESS                   *&OutMessage,
                           RWTPtrSlist< OUTMESS >     &tmpOutList,
                           RWTPtrSlist< CtiMessage >  &vgList,
                           RWTPtrSlist< CtiMessage >  &retList,
                           RWTPtrSlist< OUTMESS >     &outList,
                           bool                        wait );

public:

    typedef set< CtiDLCCommandStore > DLCCommandSet;

    typedef CtiDeviceSingle Inherited;

    CtiDeviceDLCBase();
    CtiDeviceDLCBase(const CtiDeviceDLCBase& aRef);
    virtual ~CtiDeviceDLCBase();

    CtiDeviceDLCBase& operator=(const CtiDeviceDLCBase& aRef);

    CtiTableDeviceCarrier  getCarrierSettings() const;
    CtiTableDeviceCarrier& getCarrierSettings();
    CtiDeviceDLCBase& setCarrierSettings( const CtiTableDeviceCarrier & aCarrierSettings );

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    // virtual void DecodeRoutesDatabaseReader(RWDBReader &rdr);

    virtual LONG getAddress() const;
    virtual LONG getRouteID() const;

    INT retMsgHandler( RWCString commandStr, int status, CtiReturnMsg *retMsg, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, bool expectMore = false );
    INT decodeCheckErrorReturn(INMESS *InMessage, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

    virtual bool processAdditionalRoutes( INMESS *InMessage ) const;
    virtual ULONG selectInitialMacroRouteOffset(LONG routeid = 0) const;
};

#endif // #ifndef __DEV_DLCBASE_H__
