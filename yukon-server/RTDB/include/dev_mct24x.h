#pragma warning( disable : 4786)

#ifndef __DEV_MCT24X_H__
#define __DEV_MCT24X_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_mct2XX
*
* Class:  CtiDeviceMCT2XX
* Date:   5/3/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct2xx.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:52 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "dev_mct2xx.h"

class IM_EX_DEVDB CtiDeviceMCT24X : public CtiDeviceMCT2XX
{
protected:

    enum
    {
        MCT24X_MReadAddr    = 0x89,
        MCT24X_MReadLen     =    3,  //  24 bit most recent (MREAD) copied from CUREAD value on interval boundary
        MCT24X_PutMReadAddr = 0x86,
        MCT24X_PutMReadLen  =    9,
        MCT24X_DiscAddr     = 0x37,
        MCT24X_DiscLen      =    3,  //  Gets last latch cmnd recv, reserved byte, LCIMAG
        MCT24X_DemandAddr   = 0x56,
        MCT24X_DemandLen    =    2,
        MCT24X_LPStatusAddr = 0x95,
        MCT24X_LPStatusLen  =    5,

        MCT24X_DemandIntervalAddr = 0x58,
        MCT24X_DemandIntervalLen  =    1,
        MCT24X_LPIntervalAddr     = 0x97,
        MCT24X_LPIntervalLen      =    1,

        MCT24X_StatusAddr = 0x37,  //  just get disconnect status
        MCT24X_StatusLen  =    1,

        MCT250_StatusAddr = 0x43,  //  get the 4 status inputs
        MCT250_StatusLen  =    3
    };

private:

   static CTICMDSET _commandStore;
   RWTime _lastLPRequestAttempt, _lastLPRequestBlockStart;

public:

   typedef CtiDeviceMCT2XX Inherited;

   CtiDeviceMCT24X( );
   CtiDeviceMCT24X( const CtiDeviceMCT24X &aRef );
   virtual ~CtiDeviceMCT24X( );

   CtiDeviceMCT24X &operator=( const CtiDeviceMCT24X &aRef );

   static  bool initCommandStore( );
   virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

   ULONG calcNextLPScanTime( void );
   INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );

   virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

   INT decodeScanLoadProfile     ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeScanStatus          ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigModel      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusDisconnect ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};
#endif // #ifndef __DEV_MCT24X_H__
