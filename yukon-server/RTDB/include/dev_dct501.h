#pragma warning( disable : 4786)

#ifndef __DEV_DCT501_H__
#define __DEV_DCT501_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_dct501
*
* Class:  CtiDeviceDCT501
* Date:   2002-feb-19
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_dct501.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:22 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "dev_mct24x.h"

class IM_EX_DEVDB CtiDeviceDCT501 : public CtiDeviceMCT24X
{
protected:

    enum
    {
        DCT_AnalogsAddr = 0x67,
        DCT_AnalogsLen  =    8
    };

private:

   static CTICMDSET _commandStore;
   RWTime _lastLPRequestAttempt[4], _lastLPRequestBlockStart[4], _lastLPTime[4], _nextLPTime[4];

public:

   typedef CtiDeviceMCT24X Inherited;

   CtiDeviceDCT501( );
   CtiDeviceDCT501( const CtiDeviceDCT501 &aRef );
   virtual ~CtiDeviceDCT501( );

   CtiDeviceDCT501 &operator=( const CtiDeviceDCT501 &aRef );

   static  bool initCommandStore( );
   virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

   ULONG calcNextLPScanTime( void );
   INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );

   virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

   INT decodeGetValueDemand( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigModel( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeScanLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};
#endif // #ifndef __DEV_DCT501_H__
