#pragma warning( disable : 4786)

#ifndef __DEV_MCT2XX_H__
#define __DEV_MCT2XX_H__

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
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct2XX.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:52 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "dev_mct.h"

class IM_EX_DEVDB CtiDeviceMCT2XX : public CtiDeviceMCT
{
protected:

    enum
    {
        MCT2XX_PFCountAddr = 0x20,
        MCT2XX_PFCountLen  =    2,
        MCT2XX_TimeAddr    = 0x46,
        MCT2XX_TimeLen     =    3,

        MCT2XX_MultAddr    = 0x90,
        MCT2XX_MultLen     =    2,

        MCT2XX_GenStatAddr = 0x3D,
        MCT2XX_GenStatLen  =    9,
        MCT2XX_OptionAddr  = 0x2F,
        MCT2XX_OptionLen   =    1,

        MCT2XX_ResetAddr   = 0x43,
        MCT2XX_ResetLen    =    3
    };


private:

   static CTICMDSET _commandStore;
   RWTime _lastLPRequestAttempt, _lastLPRequestBlockStart;

public:

   typedef CtiDeviceMCT Inherited;

   CtiDeviceMCT2XX( );
   CtiDeviceMCT2XX( const CtiDeviceMCT2XX &aRef );
   virtual ~CtiDeviceMCT2XX( );

   CtiDeviceMCT2XX &operator=( const CtiDeviceMCT2XX &aRef );

   static  bool initCommandStore( );
   virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

   virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

   INT decodeGetValueKWH      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValueDemand   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusInternal( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigModel   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigOptions ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};
#endif // #ifndef __DEV_MCT2XX_H__
