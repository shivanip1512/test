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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2004/02/10 22:53:18 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT_BROADCAST_H__
#define __DEV_MCT_BROADCAST_H__
#pragma warning( disable : 4786)


#include <set>
#include <utility>
using namespace std;

#include <rw\cstring.h>
#include <rw\thr\mutex.h>

#include "dev_dlcbase.h"

class IM_EX_DEVDB CtiDeviceMCTBroadcast : public CtiDeviceDLCBase
{
protected:

    enum
    {
        MCTBCAST_ResetPF            = 0x50,
        MCTBCAST_ResetPFLen         =    0,

        MCTBCAST_FreezeOne          = 0x51,
        MCTBCAST_FreezeTwo          = 0x52,
        MCTBCAST_FreezeLen          =    0,

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

    INT executePutStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT executePutValue( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );

    virtual INT ExecuteRequest ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    // virtual INT ErrorDecode ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );
    virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual LONG getAddress() const;
};

#endif // #ifndef __DEV_MCT_BROADCAST_H__
