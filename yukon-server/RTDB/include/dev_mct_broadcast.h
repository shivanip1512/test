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
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
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
private:

    typedef CtiDeviceDLCBase Inherited;

    int _last_freeze;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

protected:

    bool getOperation( const UINT &cmdType, USHORT &function, USHORT &length, USHORT &io );

    enum
    {
        MCTBCAST_ResetPF            =    0x50,
        MCTBCAST_ResetPFLen         =       0,

        MCTBCAST_LeadMeterOffset    = 4186111  //  4186112 - 1
    };

    INT executePutStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT executePutValue( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

public:

    CtiDeviceMCTBroadcast();
    virtual ~CtiDeviceMCTBroadcast();

    virtual INT ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT ResultDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    // virtual INT ErrorDecode ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool &overrideExpectMore );

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual LONG getAddress() const;
};

#endif // #ifndef __DEV_MCT_BROADCAST_H__
