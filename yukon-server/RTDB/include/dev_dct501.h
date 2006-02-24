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
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2006/02/24 00:19:13 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_DCT501_H__
#define __DEV_DCT501_H__
#pragma warning( disable : 4786)


#include "dev_mct24x.h"

class IM_EX_DEVDB CtiDeviceDCT501 : public CtiDeviceMCT24X
{
protected:

    enum
    {
        DCT_AnalogsPos = 0x67,
        DCT_AnalogsLen =    8,

        DCT_LPChannels =    4
    };

private:

   static DLCCommandSet _commandStore;
   CtiTime   _lastLPRequest[DCT_LPChannels],
            _lastLPTime[DCT_LPChannels],
            _nextLPTime[DCT_LPChannels];

public:

   typedef CtiDeviceMCT24X Inherited;

   CtiDeviceDCT501( );
   CtiDeviceDCT501( const CtiDeviceDCT501 &aRef );
   virtual ~CtiDeviceDCT501( );

   CtiDeviceDCT501 &operator=( const CtiDeviceDCT501 &aRef );

   static  bool initCommandStore( );
   virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

   ULONG calcNextLPScanTime( void );
   INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
   virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

   virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, list< OUTMESS* > &outList );

   INT decodeGetValueDemand( INMESS *InMessage, CtiTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, list< OUTMESS* > &outList );
   INT decodeGetConfigModel( INMESS *InMessage, CtiTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, list< OUTMESS* > &outList );
   INT decodeScanLoadProfile( INMESS *InMessage, CtiTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, list< OUTMESS* > &outList );
};
#endif // #ifndef __DEV_DCT501_H__
