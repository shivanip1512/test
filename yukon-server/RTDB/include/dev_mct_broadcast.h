/*-----------------------------------------------------------------------------*
*
* File:   dev_mct_broadcast
*
* Class:  CtiDeviceMCTBroadcast
* Date:   2/7/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2006/02/27 23:58:32 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT_BROADCAST_H__
#define __DEV_MCT_BROADCAST_H__
#pragma warning( disable : 4786)


#include <set>
#include <utility>
using std::set;

#include <rw\thr\mutex.h>

#include "dev_dlcbase.h"

class IM_EX_DEVDB CtiDeviceMCTBroadcast : public CtiDeviceDLCBase
{
protected:

    enum
    {
        MCTBCAST_ResetPF            =    0x50,
        MCTBCAST_ResetPFLen         =       0,

        MCTBCAST_LeadMeterOffset    = 4186111  //  4186112 - 1
    };

private:

    bool _lastFreeze;                   // if false... last freeze sent was a zero if true, last freeze was a One;

    static DLCCommandSet _commandStore;

public:

    typedef CtiDeviceDLCBase Inherited;

    CtiDeviceMCTBroadcast() {}
    virtual ~CtiDeviceMCTBroadcast() {}

    static bool initCommandStore( );
    bool getOperation( const UINT &cmdType, USHORT &function, USHORT &length, USHORT &io );

    INT executePutStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT executePutValue( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    virtual INT ExecuteRequest ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    // virtual INT ErrorDecode ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT ResultDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual LONG getAddress() const;
};

#endif // #ifndef __DEV_MCT_BROADCAST_H__
