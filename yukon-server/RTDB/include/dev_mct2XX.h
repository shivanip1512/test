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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2003/06/27 21:05:37 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT2XX_H__
#define __DEV_MCT2XX_H__
#pragma warning( disable : 4786)


#include "dev_mct.h"

class IM_EX_DEVDB CtiDeviceMCT2XX : public CtiDeviceMCT
{
protected:

    enum
    {
        MCT2XX_PFCountPos = 0x20,
        MCT2XX_PFCountLen =    2,
        MCT2XX_TimePos    = 0x46,
        MCT2XX_TimeLen    =    3,

        MCT2XX_MultPos    = 0x90,
        MCT2XX_MultLen    =    2,

        MCT2XX_GenStatPos = 0x3D,
        MCT2XX_GenStatLen =    9,
        MCT2XX_OptionPos  = 0x2F,
        MCT2XX_OptionLen  =    1,

        MCT2XX_ResetPos   = 0x43,
        MCT2XX_ResetLen   =    3
    };

    enum
    {
        //  these addresses are not valid for the 210 series meters, but that
        //    doesn't matter because these are never added in the 210 initCommandStore

        MCT2XX_GroupAddrPos     = 0x28,
        MCT2XX_GroupAddrLen     =    5,

        MCT2XX_GroupAddrBronzePos      = 0x28,
        MCT2XX_GroupAddrBronzeLen      =    1,
        MCT2XX_GroupAddrLeadPos        = 0x29,
        MCT2XX_GroupAddrLeadLen        =    3,
        MCT2XX_GroupAddrGoldSilverPos  = 0x2c,
        MCT2XX_GroupAddrGoldSilverLen  =    1,
        MCT2XX_UniqAddrPos             = 0x22,
        MCT2XX_UniqAddrLen             =    6
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
